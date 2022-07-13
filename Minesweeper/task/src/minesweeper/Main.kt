package minesweeper
import kotlin.random.Random
val mineField = MutableList(9) { MutableList(9) {'/'} }
val visibleMineField = MutableList(9) { MutableList(9) {'.'} }
fun main() {
    makeField()
    showField(visibleMineField)
    var done = false
    while (!done) {
        print("Set/unset mines marks or claim a cell as free: ")
        val (xString, yString, toDo) = readln().split(' ')
        val y = xString.toInt() - 1
        val x = yString.toInt() - 1
        if (toDo == "mine") placeMark(x, y)
        else if (toDo == "free" && mineField[x][y] == 'X') {
            showField(mineField)
            println("You stepped on a mine and failed!")
            return
        } else if (toDo == "free") {
            visibleMineField[x][y] = mineField[x][y]
            floodFillEnter(x, y)
        }
        showField(visibleMineField)
        done = gameOverAllMarked() || gameOverAllShown()
    }
    println("Congratulations! You found all the mines!")

}
fun placeMark(x: Int, y: Int){
    if (visibleMineField[x][y].isDigit()) println("There is a number here!")
    else if (visibleMineField[x][y] == '.') visibleMineField[x][y] = '*'
    else if (visibleMineField[x][y] == '*') visibleMineField[x][y] = '.'
}
fun floodFillEnter(x: Int, y: Int) {
    val location = findLocation(x, y)
    val north = x - 1
    val south = x + 1
    val east = y + 1
    val west = y - 1
    when(location) {
        "topLeft" -> {
            floodFillAction(x, east)
            floodFillAction(south, east)
            floodFillAction(south, y)
        }
        "topRight" -> {
            floodFillAction(x, west)
            floodFillAction(south, west)
            floodFillAction(south, y)
        }
        "bottomLeft" -> {
            floodFillAction(x, east)
            floodFillAction(north, east)
            floodFillAction(north, y)
        }
        "bottomRight" -> {
            floodFillAction(x, west)
            floodFillAction(north, west)
            floodFillAction(north, y)
        }
        "top" -> {
            floodFillAction(x, east)
            floodFillAction(south, east)
            floodFillAction(south, y)
            floodFillAction(x, west)
            floodFillAction(south, west)
        }
        "bottom" -> {
            floodFillAction(x, east)
            floodFillAction(north, east)
            floodFillAction(north, y)
            floodFillAction(x, west)
            floodFillAction(north, west)
        }
        "left"-> {
            floodFillAction(x, east)
            floodFillAction(north, east)
            floodFillAction(north, y)
            floodFillAction(south, y)
            floodFillAction(south, east)
        }
        "right" -> {
            floodFillAction(x, west)
            floodFillAction(north, west)
            floodFillAction(north, y)
            floodFillAction(south, y)
            floodFillAction(south, west)
        }
        "middle" -> {
            floodFillAction(x, east)
            floodFillAction(north, east)
            floodFillAction(north, y)
            floodFillAction(south, y)
            floodFillAction(south, east)
            floodFillAction(x, west)
            floodFillAction(north, west)
            floodFillAction(south, west)
        }
    }
}
fun floodFillAction(x: Int, y: Int) {
    if (mineField[x][y] == 'X' || visibleMineField[x][y] == '/' || visibleMineField[x][y].isDigit()) return
    visibleMineField[x][y] = mineField[x][y]
    if (mineField[x][y] == '/') floodFillEnter(x,y)
}
fun makeField() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    for (i in 1..numberOfMines) {
        do{
            val randomRow = Random.nextInt(0, 9)
            val randomColumn = Random.nextInt(0, 9)
            if (mineField[randomRow][randomColumn] != '/') continue
            else {
                mineField[randomRow][randomColumn] = 'X'
                break
            }
        } while(true)
    }
    for (i in 0..8) {
        for (j in 0..8) {
            if(mineField[i][j] == 'X') continue
            val location = findLocation(i, j)
            val minesInArea = Character.forDigit(counter(i, j, location), 10)
            if (minesInArea == '0') mineField[i][j] = '/'
            else mineField[i][j] = minesInArea
        }
    }
}
fun showField(field: MutableList<MutableList<Char>>) {
    println(" │123456789│")
    println("—│—————————│")
    for (i in 0..8) {
        print("${i + 1}│")
        for (j in 0..8) {
            print(field[i][j])
        }
        println("│")
    }
    println("—│—————————│")
}
fun gameOverAllMarked(): Boolean {
    for (i in 0..8) {
        for (j in 0..8) {
            val conditions = (visibleMineField[i][j] == '*' && mineField[i][j] != 'X') || (visibleMineField[i][j] != '*' && mineField[i][j] == 'X')
            if (conditions) return false
        }
    }
    return true
}
fun gameOverAllShown(): Boolean {
    for (i in 0..8){
        for (j in 0..8) {
            if (mineField[i][j] == 'X') continue
            if (mineField[i][j] != visibleMineField[i][j]) return false
        }
    }
    return true
}
fun findLocation(row: Int, col: Int): String  = when {
        row == 0 && col == 0 -> "topLeft"
        row == 0 && col == 8 -> "topRight"
        row == 8 && col == 0 -> "bottomLeft"
        row == 8 && col == 8 -> "bottomRight"
        row == 0 -> "top"
        row == 8 -> "bottom"
        col == 0 -> "left"
        col == 8 -> "right"
        else -> "middle"
}
fun counter(row: Int, col: Int, location: String): Int {
    val u = row - 1
    val d = row + 1
    val l = col - 1
    val r = col + 1
    return when (location) {
        "topLeft" -> checkMine(mineField[d][col]) + checkMine(mineField[row][r]) + checkMine(mineField[d][r])
        "topRight" -> checkMine(mineField[row][l]) + checkMine(mineField[d][col]) + checkMine(mineField[d][l])
        "bottomLeft" -> checkMine(mineField[u][col]) + checkMine(mineField[row][r]) + checkMine(mineField[u][r])
        "bottomRight" -> checkMine(mineField[row][l]) + checkMine(mineField[u][col]) + checkMine(mineField[u][l])
        "top" -> checkMine(mineField[row][l]) + checkMine(mineField[row][r]) + checkMine(mineField[d][r]) +
                checkMine(mineField[d][col]) + checkMine(mineField[d][l])
        "bottom" -> checkMine(mineField[row][l]) + checkMine(mineField[row][r]) + checkMine(mineField[u][r]) +
                checkMine(mineField[u][col]) + checkMine(mineField[u][l])
        "left" -> checkMine(mineField[u][col]) + checkMine(mineField[d][col]) + checkMine(mineField[u][r]) +
                checkMine(mineField[row][r]) + checkMine(mineField[d][r])
        "right" -> checkMine(mineField[u][col]) + checkMine(mineField[d][col]) + checkMine(mineField[u][l]) +
                checkMine(mineField[row][l]) + checkMine(mineField[d][l])
        "middle" -> checkMine(mineField[u][col]) + checkMine(mineField[d][col]) + checkMine(mineField[row][r]) +
                checkMine(mineField[row][l]) + checkMine(mineField[u][l]) + checkMine(mineField[u][r]) +
                checkMine(mineField[d][l]) + checkMine(mineField[d][r])
        else -> 0
    }
}
fun checkMine(spot: Char): Int= if(spot == 'X') 1 else 0