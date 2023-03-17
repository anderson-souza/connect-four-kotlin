package connectfour


val numberRegex = "\\d*".toRegex()
var column = 6
var row = 5

val player1HorizontalWinRegex = "o{4,}".toRegex()
val player2HorizontalWinRegex = "\\*{4,}".toRegex()

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

    val boardMoves: Array<Array<String>> = Array(row) { Array(column) { " " } }

    printBoard(row, column, boardMoves)

    val askForPlayerInput = true
    loop@ while (askForPlayerInput) {
        do {
            var invalidInput = true
            println("$player1's turn:")
            val player1Move: String = readln()

            if (player1Move == "end") {
                break@loop
            }

            if (!isValidMove(player1Move, boardMoves)) {
                continue
            }

            addPlayerMoveToBoardMoves(player1Move.toInt(), 1, boardMoves)
            invalidInput = false

        } while (invalidInput)

        printBoard(row, column, boardMoves)

        if (isBoardFull(boardMoves)) {
            break@loop
        }

        if (checkWinningCondition(boardMoves, 1)) {
            println("Player $player1 won")
            break@loop
        }

        do {
            var invalidInput = true
            println("$player2's turn:")
            val player2Move: String = readln()

            if (player2Move == "end") {
                break@loop
            }

            if (!isValidMove(player2Move, boardMoves)) {
                continue
            }

            addPlayerMoveToBoardMoves(player2Move.toInt(), 2, boardMoves)
            invalidInput = false

        } while (invalidInput)

        printBoard(row, column, boardMoves)

        if (isBoardFull(boardMoves)) {
            break@loop
        }

        if (checkWinningCondition(boardMoves, 2)) {
            println("Player $player2 won")
            break@loop
        }
    }
    println("Game Over!")
}

private fun isBoardFull(boardMoves: Array<Array<String>>) =
    (boardMoves.flatten().all { it != " " }).also { if (it) println("It is a draw") }

private fun checkWinningCondition(boardMoves: Array<Array<String>>, player: Int): Boolean {

    val horizontalWin = checkSequence(player, boardMoves)

    if (horizontalWin) {
        return true
    }

    val verticalWin = checkSequence(player, transpose(boardMoves))

    if (verticalWin) {
        return true
    }

    val diagonals = getDiagonals(boardMoves)

    val diagonalWin = when (player) {
        1 -> diagonals.any {
            player1HorizontalWinRegex.matches(it.joinToString("").trim())
        }

        2 -> diagonals.any {
            player2HorizontalWinRegex.matches(it.joinToString("").trim())
        }

        else -> false
    }

    return diagonalWin
}

private fun checkSequence(player: Int, boardMoves: Array<Array<String>>) = when (player) {
    1 -> boardMoves.reversed().any {
        player1HorizontalWinRegex.matches(it.joinToString("").trim())
    }

    2 -> boardMoves.reversed().any {
        player2HorizontalWinRegex.matches(it.joinToString("").trim())
    }

    else -> false
}

inline fun <reified T> transpose(xs: Array<Array<T>>): Array<Array<T>> {
    val cols = xs[0].size
    val rows = xs.size
    return Array(cols) { j ->
        Array(rows) { i ->
            xs[i][j]
        }
    }
}

fun getDiagonals(matrix: Array<Array<String>>): List<List<String>> {
    val diagonals: MutableList<List<String>> = ArrayList()
    val numRows = matrix.size
    val numCols = matrix[0].size

    // Loop through all rows and columns to get diagonals starting from left to right
    for (i in 0 until numRows + numCols - 1) {
        val diagonal: MutableList<String> = ArrayList()
        var row = if (i < numCols) 0 else i - numCols + 1
        var col = if (i < numCols) i else numCols - 1
        while (row < numRows && col >= 0) {
            diagonal.add(matrix[row][col])
            row++
            col--
        }
        diagonals.add(diagonal)
    }

    // Loop through all rows and columns to get diagonals starting from right to left
    for (i in 1 - numCols until numRows) {
        val diagonal: MutableList<String> = ArrayList()
        var row = if (i < 0) -i else 0
        var col = if (i < 0) 0 else i
        while (row < numRows && col < numCols) {
            diagonal.add(matrix[row][col])
            row++
            col++
        }
        diagonals.add(diagonal)
    }
    return diagonals
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
            if (row in 0 until rowNumber && column < columnNumber) {
                print("║${boardMoves[row][column]}")
            } else if (row == rowNumber) {
                printFooter(column, columnNumber)
            } else {
                print("║")
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
