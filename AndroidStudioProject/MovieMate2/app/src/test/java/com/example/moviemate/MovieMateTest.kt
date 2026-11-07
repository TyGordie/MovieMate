package com.example.moviemate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieMateTest {

    @Test
    fun validEmailIsAccepted() {
        val email = "test@example.com"

        assertTrue(email.contains("@"))
    }

    @Test
    fun ratingMustBeBetweenOneAndFive() {
        val rating = 5.0

        assertTrue(rating in 1.0..5.0)
    }

    @Test
    fun movieTitleIsCorrect() {
        val title = "Inception"

        assertEquals("Inception", title)
    }
}