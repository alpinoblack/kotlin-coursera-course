package com.orca.week3.nicestring

fun String.isNice(): Boolean {
    return listOf(::notContainsSubstrings,
            ::containsVowels,
            ::containsDoubleLetters
    ).map { it.invoke(this) }.count { it } >= 2
}

fun notContainsSubstrings(str: String): Boolean {
    return !str.contains("b[uae]".toRegex())
}

fun containsVowels(str: String): Boolean {
    val vowels = listOf('a', 'e', 'i', 'o', 'u')
    return str.filter { vowels.contains(it) }.toList().size >= 3
}

fun containsDoubleLetters(str: String): Boolean {
    return str.zipWithNext().any { (prev, next) -> prev == next }
}