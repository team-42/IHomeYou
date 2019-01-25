package de.weareprophet.ihomeyou

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class IHomeYouTest {

    @Test
    fun answer() {
        val subject = IHomeYou()
        assertThat(subject.answer()).isEqualTo(42)
    }

}