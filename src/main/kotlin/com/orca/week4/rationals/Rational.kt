package com.orca.week4.rationals

import java.math.BigInteger

class Rational private constructor(private val numerator: BigInteger, private val denominator: BigInteger) : Comparable<Rational> {

    override fun toString(): String {
        return if (denominator == oneBigInteger) {
            numerator.toString()
        } else {
            "$numerator/$denominator"
        }
    }

    operator fun rangeTo(rational: Rational): ClosedRange<Rational> =
        RationalRange(this, rational)

    override fun hashCode(): Int {
        var result = numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Rational -> compare(this, other) == 0
            else -> false
        }
    }

    override operator fun compareTo(other: Rational): Int = compare(this, other)

    fun compare(o1: Rational, o2: Rational): Int {

        return (o1.numerator * o2.denominator - o2.numerator * o1.denominator).signum()
    }

    operator fun component1() = numerator
    operator fun component2() = denominator

    companion object {

        val oneBigInteger = 1.toBigInteger()

        class RationalRange(override val start: Rational, override val endInclusive: Rational) : ClosedRange<Rational>

        fun createRational(numerator: BigInteger, denominator: BigInteger): Rational {

            fun normalizeSign(rational: Rational): Rational {
                val (numer, denom) = rational
                return Rational(
                    numer * denom.signum().toBigInteger(),
                    if (denom.signum() == -1) denom.negate() else denom
                )
            }

            fun normalizeByGcd(rational: Rational): Rational {
                val (numer, denom) = rational
                val gcd = numer.gcd(denom)
                return Rational(numer / gcd, denom / gcd)
            }

            require(denominator != 0.toBigInteger()) { "denominator can't be 0" }

            return Rational(numerator, denominator).let(::normalizeSign).let(::normalizeByGcd)

        }

        fun createRational(numerator: Int, denominator: Int) =
            createRational(
                numerator.toBigInteger(),
                denominator.toBigInteger()
            )
        fun createRational(numerator: Long, denominator: Long) =
            createRational(
                numerator.toBigInteger(),
                denominator.toBigInteger()
            )
    }


    operator fun plus(rational: Rational): Rational =
        createRational(
            this.numerator * rational.denominator + rational.numerator * this.denominator,
            this.denominator * rational.denominator
        )

    operator fun minus(rational: Rational): Rational =
            this + (-rational)

    operator fun times(rational: Rational): Rational =
        createRational(
            this.numerator * rational.numerator,
            this.denominator * rational.denominator
        )

    operator fun div(rational: Rational): Rational =
        createRational(
            this.numerator * rational.denominator,
            this.denominator * rational.numerator
        )

    operator fun unaryMinus(): Rational =
        createRational(numerator.negate(), denominator)

}

private val rationalRegex = "-?\\d+(/-?\\d+)?".toRegex()

fun String.toRational(): Rational {
    return if (rationalRegex.matches(this)) {
        if (this.contains("/")) {
            val splitStr = split("""/""")
            Rational.createRational(
                splitStr.first().toBigInteger(),
                splitStr.last().toBigInteger()
            )
        } else {
            Rational.createRational(
                this.toBigInteger(),
                Rational.oneBigInteger
            )
        }

    } else {
        throw NumberFormatException("string number format incorrect $this")
    }
}


infix fun Int.divBy(denominator: Int): Rational =
    Rational.createRational(this, denominator)
infix fun Long.divBy(denominator: Long): Rational =
    Rational.createRational(this, denominator)
infix fun BigInteger.divBy(denominator: BigInteger): Rational =
    Rational.createRational(this, denominator)

fun main() {

    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third

    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}