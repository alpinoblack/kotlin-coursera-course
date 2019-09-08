package com.orca.week4.board

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl.createSquareBoard(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl.createGameBoard(width)


class SquareBoardImpl private constructor(private val cellList: List<Cell>, override val width: Int) : SquareBoard {

    private val rangeForBoard = 1..width

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return if (i in rangeForBoard && j in rangeForBoard) {
            cellList[(i - 1) * width + (j - 1)]
        } else {
            null
        }
    }

    override fun getCell(i: Int, j: Int): Cell {
        return getCellOrNull(i, j) ?: throw IllegalArgumentException("illegal cell coordinates i=$i, j=$j")
    }

    override fun getAllCells(): Collection<Cell> {
        return cellList
    }

    private fun createEffRange(intRange: IntProgression): IntProgression {
        return when {
            intRange.step <= 0 -> {
                val effStart = if (intRange.first >= width) width else intRange.first
                val effEnd = if (intRange.last in 1..width) intRange.last else 1
                effStart downTo effEnd
            }
            else -> {
                val effEnd = if (intRange.last >= width) width else intRange.last
                val effStart = if (intRange.first in 1..width) intRange.first else 1
                effStart..effEnd
            }
        }
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        require(i in rangeForBoard) { "invalid requested row number" }
        val aggCellList = mutableListOf<Cell>()
        val effectiveJRange = createEffRange(jRange)
        for (j in effectiveJRange) {
            aggCellList += getCell(i, j)
        }
        return aggCellList.toList()
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        require(j in rangeForBoard) { "invalid requested column number" }
        val aggCellList = mutableListOf<Cell>()
        val effectiveIRange = createEffRange(iRange)
        for (i in effectiveIRange) {
            aggCellList += getCell(i, j)
        }
        return aggCellList.toList()
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            Direction.UP -> getCellOrNull(i - 1, j)
            Direction.DOWN -> getCellOrNull(i + 1, j)
            Direction.LEFT -> getCellOrNull(i, j - 1)
            Direction.RIGHT -> getCellOrNull(i, j + 1)
        }
    }

    companion object {
        fun createSquareBoard(width: Int): SquareBoard {
            require(width >= 0)
            val listOfCells = mutableListOf<Cell>()
            for (i in 1..width) {
                for (j in 1..width) {
                    listOfCells += Cell(i, j)
                }
            }
            return SquareBoardImpl(listOfCells.toList(), width)
        }
    }

}

class GameBoardImpl<T> private constructor(override val width: Int, private val b: SquareBoard) : GameBoard<T> {

    private var cell2ValueMapping: MutableMap<Cell, T?> = b.getAllCells().associateTo(mutableMapOf()) { cell -> cell to null }

    override fun getCellOrNull(i: Int, j: Int): Cell? = b.getCellOrNull(i, j)

    override fun getCell(i: Int, j: Int): Cell = b.getCell(i, j)

    override fun getAllCells(): Collection<Cell> = b.getAllCells()

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = b.getRow(i, jRange)

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = b.getColumn(iRange, j)

    override fun Cell.getNeighbour(direction: Direction): Cell? = this.getNeighbour(direction)

    override fun get(cell: Cell): T? = cell2ValueMapping[cell]

    override fun set(cell: Cell, value: T?) {
        cell2ValueMapping[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return cell2ValueMapping.toList().filter { (_, value) -> predicate(value) }.map { it.first }
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return cell2ValueMapping.toList().find { (_, value) -> predicate(value) }?.first
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return find(predicate) != null
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        val negatedPredicate = andThen(predicate, Companion::negatePredicate)
        return !any(negatedPredicate)
    }

    companion object {
        fun <T> createGameBoard(width: Int): GameBoard<T> {
            val squareBoard = SquareBoardImpl.createSquareBoard(width)
            return GameBoardImpl(width, squareBoard)
        }

        fun negatePredicate(predicateResult: Boolean): Boolean = !predicateResult

        private fun <A, B, C> andThen(f: (A) -> B, g: (B) -> C): (A) -> C {
            return { x -> g(f(x)) }
        }

    }

}

