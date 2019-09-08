package com.orca.week3.taxipark

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> =
        allDrivers.filterNot { testedDriver ->
            trips.map { it.driver }.contains(testedDriver)
        }.toSet()

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
        allPassengers.filter { passenger ->
            trips.count { trip -> trip.passengers.contains(passenger) } >= minTrips
        }.toSet()

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> =
        allPassengers.filter { passenger ->
            trips.count { trip -> trip.passengers.contains(passenger) && trip.driver == driver } > 1
        }.toSet()

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> =
        allPassengers.filter { passenger ->
            val tripsForPassenger = trips.filter { it.passengers.contains(passenger) }
            tripsForPassenger.sumBy { if (it.discount == null) 0 else 1 } > tripsForPassenger.size / 2.0
        }.toSet()

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    return trips.map { it.duration }.groupBy { duration ->
        val closestDecimal = duration - (duration % 10)
        closestDecimal until closestDecimal + 10
    }.maxBy { it.value.size }?.key
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    return if (trips.isEmpty()) {
        false
    } else {
        val driversEarningsRanking = trips.map { it.driver to it.cost }
                .groupBy { it.first }
                .mapValues { (_, value) -> value.sumByDouble { it.second } }
                .toList().sortedByDescending { it.second }

        val twentyPercentOfDriversFloored = Math.floor(allDrivers.size * 0.2).toInt()

        val totalIncome = trips.sumByDouble { it.cost }

        val earningsOfTopTwentyPercent = driversEarningsRanking.take(twentyPercentOfDriversFloored).sumByDouble { it.second }

        earningsOfTopTwentyPercent >= totalIncome * 0.8
    }

}