package com.orca.week5.games.game2048

import com.orca.week4.board.Cell
import com.orca.week4.board.Direction
import com.orca.week4.board.GameBoard
import com.orca.week4.board.createGameBoard
import com.orca.week5.games.game.Game

/*
 * Your task is to implement the game 2048 https://en.wikipedia.org/wiki/2048_(video_game).
 * Implement the utility methods below.
 *
 * After implementing it you can try to play the game running 'PlayGame2048'.
 */
fun newGame2048(initializer: Game2048Initializer<Int> = RandomGame2048Initializer): Game =
        Game2048(initializer)

class Game2048(private val initializer: Game2048Initializer<Int>) : Game {
    private val board = createGameBoard<Int?>(4)

    override fun initialize() {
        repeat(2) {
            board.addNewValue(initializer)
        }
    }

    override fun canMove() = board.any { it == null }

    override fun hasWon() = board.any { it == 2048 }

    override fun processMove(direction: Direction) {
        if (board.moveValues(direction)) {
            board.addNewValue(initializer)
        }
    }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }
}

/*
 * Add a new value produced by 'initializer' to a specified cell in a board.
 */
fun GameBoard<Int?>.addNewValue(initializer: Game2048Initializer<Int>) {

    initializer.nextValue(this)?.let { (cell, value) ->
        set(cell, value)
    }
}

/*
 * Update the values stored in a board,
 * so that the values were "moved" in a specified rowOrColumn only.
 * Use the helper function 'moveAndMergeEqual' (in Game2048Helper.kt).
 * The values should be moved to the beginning of the row (or column),
 * in the same manner as in the function 'moveAndMergeEqual'.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValuesInRowOrColumn(rowOrColumn: List<Cell>): Boolean {
    val usedValues = rowOrColumn
            .map { cell -> get(cell) }

    val mergedValues = usedValues.moveAndMergeEqual { it * 2 }

    return if (mergedValues.isEmpty() || mergedValues == usedValues) {
        false
    } else {
        mergedValues.zip(rowOrColumn).map{ (newValue, cell2Update) -> set(cell2Update, newValue)}
        rowOrColumn.drop(mergedValues.size).map { cell2Upadate -> set(cell2Upadate, null) }
        true
    }



}

/*
 * Update the values stored in a board,
 * so that the values were "moved" to the specified direction
 * following the rules of the 2048 game .
 * Use the 'moveValuesInRowOrColumn' function above.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValues(direction: Direction): Boolean {
    return when (direction){
        Direction.UP ->
            (1..width).map {col ->getColumn(1..width, col) }
                    .map { moveValuesInRowOrColumn(it) }
                    .any { it }
        Direction.DOWN ->
            (1..width).map {col ->getColumn(1..width, col) }
                    .map { moveValuesInRowOrColumn(it.asReversed()) }
                    .any { it }
        Direction.RIGHT ->
            (1..width).map{ row -> getRow(row, 1 .. width)}
                    .map{moveValuesInRowOrColumn(it.asReversed())}
                    .any{it}
        Direction.LEFT ->
            (1..width).map{ row -> getRow(row, 1 .. width)}
                    .map{moveValuesInRowOrColumn(it)}
                    .any{it}
    }
}