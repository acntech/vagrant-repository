package no.acntech.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectVersionValidatorTest {

    private final CorrectVersionValidator validator = new CorrectVersionValidator();

    @Test
    void isValid() {
        assertThat(validator.isValid("0", null)).isTrue();
        assertThat(validator.isValid("1", null)).isTrue();
        assertThat(validator.isValid("00", null)).isFalse();
        assertThat(validator.isValid("01", null)).isFalse();
        assertThat(validator.isValid("0.1", null)).isTrue();
        assertThat(validator.isValid("0.0.1", null)).isTrue();
        assertThat(validator.isValid("0.0.0", null)).isTrue();
        assertThat(validator.isValid("1.0", null)).isTrue();
        assertThat(validator.isValid("1.0.0", null)).isTrue();
        assertThat(validator.isValid("10000.0.0", null)).isTrue();
    }
}