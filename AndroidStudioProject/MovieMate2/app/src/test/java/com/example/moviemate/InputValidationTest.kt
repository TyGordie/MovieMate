package com.example.moviemate

import com.example.moviemate.util.isValidEmail
import com.example.moviemate.util.isValidRating
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidationTest {
    @Test fun acceptsValidEmail() = assertTrue(isValidEmail("student@example.com"))
    @Test fun rejectsInvalidEmail() = assertFalse(isValidEmail("not-an-email"))
    @Test fun acceptsRatingWithinRange() = assertTrue(isValidRating(4.5))
    @Test fun rejectsRatingOutsideRange() = assertFalse(isValidRating(6.0))
    @Test fun rejectsNanRating() = assertFalse(isValidRating(Double.NaN))
}
