package com.orca.week2.mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

data class GameState(val newSecret: String, val newGuess: String, val rightPositions: Int)

fun evaluateGuess(secret: String, guess: String): Evaluation {

    val currState = findRightPositions(secret, guess)

    val wrongPositions = findWrongPositions(currState.newSecret, currState.newGuess)

    return Evaluation(currState.rightPositions, wrongPositions)

}

fun findRightPositions(secret: String, guess: String): GameState {


    val differentChars = secret.zip(guess).filterNot { it.second == it.first }

    val newGuess = differentChars.map { it.second }.joinToString("")
    val newSecret = differentChars.map { it.first }.joinToString("")

    return GameState(newSecret, newGuess, 4 - differentChars.size)


}

fun findWrongPositions(secret: String, guess: String): Int {

    return guess.fold(0 to secret) { wrongsAndSecret: Pair<Int, String>, currentGuessChar ->
        val foundIndex = wrongsAndSecret.second.indexOf(currentGuessChar)
        if (currentGuessChar != 0.toChar() && foundIndex >= 0) {
            val newSecret = wrongsAndSecret.second.removeCharAtIndex(foundIndex)
            println(newSecret)
            (wrongsAndSecret.first + 1) to newSecret
        } else {
            wrongsAndSecret
        }
    }.first

}

fun String.removeCharAtIndex(index: Int): String {

    val strAsCharArr = toCharArray()

    for (i in index until (strAsCharArr.size - 1)) {
        strAsCharArr[i] = strAsCharArr[i + 1]
    }

    strAsCharArr[strAsCharArr.size - 1] = 0.toChar()

    return String(strAsCharArr)

}