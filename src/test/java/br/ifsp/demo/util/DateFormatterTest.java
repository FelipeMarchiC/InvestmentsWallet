package br.ifsp.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DateFormatterTest {
    DateFormatter dateFormatter = new DateFormatter();

    @Nested
    class MutationTests {
        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Should correctly return formatted date")
        void shouldCorrectlyReturnFormattedDate(){
            LocalDate date = LocalDate.of(2024, 12, 25);
            String result = dateFormatter.formatDateToSlash(date);
            assertThat(result).isEqualTo("25/12/2024");
        }
    }

}