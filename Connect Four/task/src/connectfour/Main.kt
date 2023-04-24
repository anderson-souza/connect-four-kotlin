package connectfour

val numberRegex = "\\d*".toRegex()
var column = 6
var row = 5

val player1HorizontalWinRegex = "o{4,}".toRegex()
val player2HorizontalWinRegex = "\\*{4,}".toRegex()

var player1 = ""
var player2 = ""

fun main() {
    println("Connect Four")
    player1 = getPlayerName("First")
    player2 = getPlayerName("Second")

    val columnAndRow = askBoardDimensionsAndReturnColAndRow()
    column = columnAndRow.first
    row = columnAndRow.second

    val gameManager = askForHowManyGamesAndGetGameManager()

    println("$player1 VS $player2")
    println("$row X $column board")
    if (!gameManager.isSingleMode) {
        println("Total ${gameManager.numberOfGames} games")
    }

    var currentPlayerMove = 1
    games@ for (i in gameManager.currentGame until gameManager.numberOfGames) {
        gameManager.startNewGame()
        println(if (gameManager.isSingleMode) "Single game" else "Game #${gameManager.currentGame}")

        val boardMoves: Array<Array<String>> = Array(row) { Array(column) { " " } }

        printBoard(row, column, boardMoves)
        loop@ while (true) {
            do {
                var invalidInput = true
                println(if (currentPlayerMove == 1) "$player1's turn:" else "$player2's turn:")
                val playerMove: String = readln()

                if (playerMove == "end") {
                    break@games
                }

                if (!isValidMove(playerMove, boardMoves)) {
                    continue
                }

                addPlayerMoveToBoardMoves(playerMove.toInt(), currentPlayerMove, boardMoves)
                invalidInput = false

            } while (invalidInput)

            printBoard(row, column, boardMoves)

            if (isBoardFull(boardMoves)) {
                gameManager.drawGame()
                break@loop
            }

            if (checkWinningCondition(boardMoves, currentPlayerMove)) {
                println(if (currentPlayerMove == 1) "Player $player1 won" else "Player $player2 won")
                gameManager.increasePlayerScore(currentPlayerMove)
                currentPlayerMove = if (currentPlayerMove == 1) 2 else 1
                break@loop
            }

            currentPlayerMove = if (currentPlayerMove == 1) 2 else 1
        }
        printScoreForMultipleGames(gameManager)
    }
    println("Game Over!")
}

private fun printScoreForMultipleGames(gameManager: GameManager) {
    if (!gameManager.isSingleMode) {
        println(
            "Score\n" +
                    "$player1: ${gameManager.getPlayer1Score()} $player2: ${gameManager.getPlayer2Score()}"
        )
    }
}

private fun askForHowManyGamesAndGetGameManager(): GameManager {
    var isInvalidInput = true
    var numberOfGames: String
    do {
        println(
            "Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter\n" +
                    "Input a number of games:"
        )
        numberOfGames = readln()

        if (numberOfGames.isEmpty()) {
            numberOfGames = "1"
        } else if (!numberOfGames.matches(numberRegex) || numberOfGames == "0") {
            println("Invalid Input")
            continue
        }
        isInvalidInput = false
    } while (isInvalidInput)

    return GameManager(numberOfGames.toInt())
}

private fun getPlayerName(playerPosition: String): String {
    println("$playerPosition player's name:")
    return readln()
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

    else -> boardMoves.reversed().any {
        player2HorizontalWinRegex.matches(it.joinToString("").trim())
    }
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
    isValidIntegerMove(move) && isMoveValidColumnNumber(move.toInt()) && isColumnAvailableToMove(
        move.toInt(),
        boardMoves
    )

private fun isValidIntegerMove(move: String): Boolean =
    numberRegex.matches(move).also { if (!it) println("Incorrect column number") }

private fun isMoveValidColumnNumber(move: Int): Boolean =
    (move in 1..column).also { if (!it) println("The column number is out of range (1 - $column)") }

private fun isColumnAvailableToMove(move: Int, boardMoves: Array<Array<String>>): Boolean =
    boardMoves.any { it[move - 1] == " " }.also {
        if (!it) {
            println("Column $move is full")
        }
    }

private fun askBoardDimensionsAndReturnColAndRow(): Pair<Int, Int> {
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

            val splitInput = boardDimensionsInput.split("X", ignoreCase = true)

            row = splitInput[0].toInt()
            column = splitInput[1].toInt()

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
