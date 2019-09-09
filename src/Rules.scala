class Rules() { //TODO: 1. Check if there are empty possValues to stop recursion
                //TODO: 2. Let BruteForce skip square if it is solved (the first TODO is a prerequisite for this)

  //1. Remove number from possible value in row and column if its already set as solution
  //2. Remove all values from neighbors squares which are not possible
  //3. Remove all values from squares which are not possible

  def applyRules(matrix: SquareMatrix): SquareMatrix = {

    val matrixRow = removeRedundantValuesInRows(matrix)
    val matrixColumn = removeRedundantValuesInColumns(matrixRow)
    return matrixColumn
  }

  def applyRules(matrix: SquareMatrix,
                 square: Square): SquareMatrix = {
    val matrixRow = removeRedundantValuesInRow(matrix, square)
    val matrixColumn = removeRedundantValuesInColumn(matrixRow, square)
    val matrixNeighbour = updateNeighbours(matrixColumn, square, 0)

    return matrixNeighbour
  }

  def updateNeighbours(matrix: SquareMatrix, square: Square, idx:Int):SquareMatrix = {
    if (idx > square.neighbours.length - 1){
      return matrix;
    }
    val neighbour = matrix.getSquare(square.neighbours(idx)(0),square.neighbours(idx)(1))
    if (neighbour.isSolved){
      return matrix;
    }
    if (square.isSolved) {
      val neighbourValues = List[Int](square.getCorrectValue()+1,square.getCorrectValue()-1)
      val newPossValues = neighbour.possibleValues.intersect(neighbourValues)
      if (newPossValues.length == 1) {
        return updateNeighbours(applyRules(matrix.setSquare(neighbour.setValue(newPossValues(0))),neighbour),
          square,
          idx+1)
      } else {
        return updateNeighbours(matrix.setSquare(neighbour.setValues(newPossValues)), square, idx + 1)
      }
    }

    return updateNeighbours(matrix, square, idx+1);
  }

  def removeRedundantValuesInColumn(matrix: SquareMatrix,
                                    square: Square): SquareMatrix = {
    val allSquaresWithoutX = matrix.allSquares.filter(_.x != square.x)
    val xSquares = matrix.getAllFromX(square.x)
    val solvedX = xSquares.filter(_.isSolved)
    val updatedSquares = getUpdatedSquares(xSquares)
    val newSquareList = updatedSquares ::: allSquaresWithoutX ::: solvedX
    val newMatrix = new SquareMatrix(matrix.size, newSquareList)
    return reApplyRules(newMatrix,updatedSquares)
  }

  def removeRedundantValuesInRow(matrix: SquareMatrix,
                                 square: Square): SquareMatrix = {
    val allSquaresWithoutY = matrix.allSquares.filter(_.y != square.y)
    val ySquares = matrix.getAllFromY(square.y)
    val solvedY = ySquares.filter(_.isSolved)
    val updatedSquares = getUpdatedSquares(ySquares)
    val newSquareList = updatedSquares ::: allSquaresWithoutY ::: solvedY
    val newMatrix = new SquareMatrix(matrix.size, newSquareList)
    return reApplyRules(newMatrix,updatedSquares)
  }


  def removeRedundantValuesInRows(matrix: SquareMatrix): SquareMatrix = {
    val sizeP = matrix.allSquares.size
    var newSquareList = List[Square]()

    for (x <- 1 to sizeP) {
      val xSquares = matrix.getAllFromX(x)
      val solvedSquares = xSquares.filter(_.isSolved)
      newSquareList = newSquareList ::: getUpdatedSquares(xSquares) ::: solvedSquares
    }
    return new SquareMatrix(matrix.size, newSquareList)
  }

  def removeRedundantValuesInColumns(matrix: SquareMatrix): SquareMatrix = {
    val sizeP = matrix.allSquares.size
    var newSquareList = List[Square]()

    for (y <- 1 to sizeP) {
      val ySquares = matrix.getAllFromY(y)
      val solvedSquares = ySquares.filter(_.isSolved)
      newSquareList = newSquareList ::: getUpdatedSquares(ySquares) ::: solvedSquares
    }
    return new SquareMatrix(matrix.size, newSquareList)
  }

  private def getUpdatedSquares(squares: List[Square]): List[Square] = {
    val solvedSquares = squares.filter(_.isSolved)
    var updatedSquareList = List[Square]()

    var solutions = List[Int]()
    for (s <- solvedSquares) {
      solutions = solutions :+ s.possibleValues.head
    }

    val notSolved = squares.filter(!_.isSolved)
    for (s <- notSolved) {
      updatedSquareList = updatedSquareList :+ s.removeValues(solutions)
    }
    return updatedSquareList
  }

  private def reApplyRules(matrix:SquareMatrix, updateSquares:List[Square]):SquareMatrix = {
    val solvedSquares = updateSquares.filter(_.isSolved)
    if (solvedSquares.isEmpty) {
      return matrix;
    }
    return reApplyRules(applyRules(matrix,solvedSquares(0)), solvedSquares.splitAt(1)._2)
  }


}
