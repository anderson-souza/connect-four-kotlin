package connectfour

val numberRegex = "\\d*".toRegex()
var column = 6
var row = 5

fun main() {
    println("Connect Four")
    println("First player's name:")
    val player1 = readln()

    println("Second player's name:")
    val player2 = readln()

    val columnAndRow = generateBoardAndReturnColumnAndRow()
    column = columnAndRow.first
    row = columnAndRow.second

    println("$player1 VS $player2")
    println("$row X $column board")

    val boardMoves: Array<Array<String>> = Array(row) { Array(column + 1) { " " } }

    printBoard(row, column, boardMoves)

    val askForPlayerInput = true
    loop@ while (askForPlayerInput) {
        do {
            var invalidInput = true
            println("$player1's turn:")
            val player1Move: String = readln()

            if (player1Move == "end") {
                println("Game Over!")
                break@loop
            }

            if (!isValidMove(player1Move, boardMoves)) {
                continue
            }

            addPlayerMoveToBoardMoves(player1Move.toInt(), 1, boardMoves)
            invalidInput = false

        } while (invalidInput)

        printBoard(row, column, boardMoves)

        do {
            var invalidInput = true
            println("$player2's turn:")
            val player2Move: String = readln()

            if (player2Move == "end") {
                println("Game Over!")
                break@loop
            }

            if (!isValidMove(player2Move, boardMoves)) {
                continue
            }

            addPlayerMoveToBoardMoves(player2Move.toInt(), 2, boardMoves)
            invalidInput = false

        } while (invalidInput)

        printBoard(row, column, boardMoves)
    }
}

private fun addPlayerMoveToBoardMoves(move: Int, player: Int, boardMoves: Array<Array<String>>) {

    val moveValue = if (player == 1) "o" else "*"

    boardMoves.lastOrNull { it[move - 1] == " " }?.apply { set(move - 1, moveValue) }

}

private fun isValidMove(move: String, boardMoves: Array<Array<String>>): Boolean =
    isValidIntegerMove(move) && isMoveValidColumnNumber(move.toInt()) && isColumnAvaliableToMove(
        move.toInt(),
        boardMoves
    )

private fun isValidIntegerMove(move: String): Boolean =
    numberRegex.matches(move).also { if (!it) println("Incorrect column number") }

private fun isMoveValidColumnNumber(move: Int): Boolean =
    (move in 1..column).also { if (!it) println("The column number is out of range (1 - $column)") }

private fun isColumnAvaliableToMove(move: Int, boardMoves: Array<Array<String>>): Boolean =
    boardMoves.any { it[move - 1] == " " }.also {
        if (!it) {
            println("Column $move is full")
        }
    }

private fun generateBoardAndReturnColumnAndRow(): Pair<Int, Int> {
    var row = 6
    var column = 7
    var validInput = false

    while (!validInput) {
        val boardDimensionsInput = getBoardDimension()
        if (boardDimensionsInput.isNotEmpty()) {

            val regex = """^\s*\d+\s*[xX]\s*\d+\s*${'$'}""".toRegex()
            if (!regex.matches(boardDimensionsInput)) {
                println("Invalid input")
                continue
            }

            val splitedInput = boardDimensionsInput.split("X", ignoreCase = true)

            row = splitedInput[0].toInt()
            column = splitedInput[1].toInt()

            if (row !in 5..9) {
                println("Board rows should be from 5 to 9")
                continue
            }

            if (column !in 5..9) {
                println("Board columns should be from 5 to 9")
                continue
            }
        }
        validInput = true
    }
    return Pair(column, row)
}

private fun getBoardDimension(): String {
    println(
        """Set the board dimensions (Rows x Columns)
        |Press Enter for default (6 x 7)
        """.trimMargin("|")
    )
    return readln().replace(" ", "").replace("\t", "")
}

private fun printBoard(rowNumber: Int, columnNumber: Int, boardMoves: Array<Array<String>>) {
    (0..rowNumber).forEach { row ->
        printHeader(row, columnNumber)
        println()
        (0..columnNumber).forEach { column ->
            if (row in 0 until rowNumber) {
                print("║${boardMoves[row][column]}")
            }

            if (row == rowNumber) {
                printFooter(column, columnNumber)
            }
        }
    }
    println()
}

private fun printFooter(column: Int, columnNumber: Int) = when (column) {
    0 -> {
        print("╚═")
    }

    columnNumber -> {
        print("╝")
    }

    else -> {
        print("╩═")
    }
}

private fun printHeader(row: Int, columnNumber: Int) {
    if (row == 0) {
        (1..columnNumber).forEach { print(" $it") }
    }
}
