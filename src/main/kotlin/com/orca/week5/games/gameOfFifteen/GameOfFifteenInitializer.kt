package com.orca.week5.games.gameOfFifteen

interface GameOfFifteenInitializer {
    /*
     * Even permutation of numbers 1..15
     * used to initialized the first 15 cells on a board.
     * The last cell is empty.
     */
    val initialPermutation: List<Int>
}

class RandomGameInitializer : GameOfFifteenInitializer {
    /*
     * Generate a random permutation from 1 to 15.
     * `shuffled()` function might be helpful.
     * If the permutation is not even, make it even (for instance,
     * by swapping two numbers).
     */
    override val initialPermutation by lazy {
        val trialPermutation = (1..15).shuffled()
        val mutableTrialPermutation = trialPermutation.toMutableList()
        if (!isEven(mutableTrialPermutation)) {
            val indexedList = mutableTrialPermutation.withIndex()
             for ((indx, value) in indexedList) {
                val valuesRightOfIndex = indexedList.partition { it.index > indx }.first
                 val foundVal = valuesRightOfIndex.find { it.value <= value }
                 if (foundVal != null) {
                     mutableTrialPermutation[foundVal.index] = value
                     mutableTrialPermutation[indx] = foundVal.value
                     break
                 }
            }
        }
        println("initial permutation is $trialPermutation \neventual permutation is $mutableTrialPermutation")
        mutableTrialPermutation
    }
}

