package com.orca.week5.games.gameOfFifteen

import com.orca.week4.board.Cell
import com.orca.week4.board.Direction
import com.orca.week4.board.GameBoard
import com.orca.week4.board.createGameBoard
import com.orca.week5.games.game.Game

/*
 * Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
 * When you finish, you can play the game by executing 'PlayGameOfFifteen'.
 */
fun newGameOfFifteen(initializer: GameOfFifteenInitializer = RandomGameInitializer()): Game = GameOfFifteen(initializer)

class GameOfFifteen(private val initializer: GameOfFifteenInitializer) : Game {

    private val victorySequence = (1..15).toList()

    private val boardWidth = 4

    private val board = createGameBoard<Int?>(boardWidth)

    override fun initialize() {

        var i = 1
        var j = 1

        for (permVal in initializer.initialPermutation) {

            board[Cell(i, j)] = permVal
            if (j == 4) {
                j = 1
                i += 1
            } else {
                j += 1
            }

        }
    }

    override fun canMove(): Boolean = true

    override fun hasWon(): Boolean {
        val currentBoardStatus = board.getAllCells().map { cell -> board[cell] }
        return currentBoardStatus.last() == null && currentBoardStatus.dropLast(1) == victorySequence
    }

    override fun processMove(direction: Direction) {
        val vacantCell = board.find { it == null } ?: throw IllegalStateException("board doesn't have a vacant cell," +
                " this shouldn't happen")
        when (direction) {
            Direction.UP -> board.getColumn(1..boardWidth, vacantCell.j)
                    .apply { board.moveValuesInRowOrColumnWOMerge(this.asReversed(), (boardWidth- 1) - (vacantCell.i - 1), true) }
            Direction.DOWN -> board.getColumn(1..boardWidth, vacantCell.j)
                    .apply { board.moveValuesInRowOrColumnWOMerge(this, vacantCell.i - 1) }
            Direction.LEFT -> board.getRow(vacantCell.i, 1..boardWidth)
                    .apply { board.moveValuesInRowOrColumnWOMerge(this.asReversed(), (boardWidth - 1) - (vacantCell.j - 1), true) }
            Direction.RIGHT -> board.getRow(vacantCell.i, 1..boardWidth)
                    .apply { board.moveValuesInRowOrColumnWOMerge(this, vacantCell.j - 1) }
        }

    }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }

    private fun GameBoard<Int?>.moveValuesInRowOrColumnWOMerge(rowOrColumn: List<Cell>, vacantIndex: Int, reverse: Boolean = false)/*: Boolean*/ {

        val usedValues = rowOrColumn
                .map { cell -> get(cell) }.toMutableList()

        usedValues.getOrNull(vacantIndex - 1)?.let {
            usedValues[vacantIndex] = it
            usedValues[vacantIndex - 1] = null
        }

        usedValues.apply { if (reverse) asReversed() }.zip(rowOrColumn).map { (newValue, cell2Update) -> set(cell2Update, newValue)  }


    }
}