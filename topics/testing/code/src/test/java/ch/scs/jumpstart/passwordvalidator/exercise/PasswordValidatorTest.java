package ch.scs.jumpstart.passwordvalidator.exercise;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PasswordValidatorTest {

  @Nested
  class JavaStringTest {
    @Test
    void string_length() {
      assertThat("password".length()).isEqualTo(8);
    }

    @Test
    void mixed_case() {
      assertThat(StringUtils.isMixedCase("aA")).isTrue();
    }

    @Test
    void is_numeric() {
      assertThat(StringUtils.isNumeric("1")).isTrue();
      assertThat(StringUtils.isNumeric("1A")).isFalse();
    }

    @Test
    void contains_special_char() {
      Function<String, Boolean> containsSpecialChar =
          (input) -> {
            for (String s : input.split("")) {
              if (!Character.isLetterOrDigit(s.charAt(0))) {
                return true;
              }
            }
            return false;
          };
      assertThat(containsSpecialChar.apply("+")).isTrue();
    }

    interface Validator {
      List<String> validate(String password);
    }

    class LengthValidator implements Validator {

      @Override
      public List<String> validate(String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
      }
    }
    ;

    class NumberValidator implements Validator {

      @Override
      public List<String> validate(String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
      }
    }
    ;

    List<Validator> validators = List.of(new LengthValidator(), new NumberValidator());
  }

  private final PasswordValidator passwordValidator =
      new PasswordValidator(List.of(new Lenght(), new MixedCase()));

  @ParameterizedTest
  @CsvSource(
      value = {
        "'';2",
        "'passwordpassword';1",
        "'Passwordpassword';0",
      },
      delimiter = ';')
  void validates_password(String password, int numOfViolations) {
    assertThat(passwordValidator.validate(password)).hasSize(numOfViolations);
  }

  @Nested
  class LengthTest {
    @Test
    void empty_when_valid() {
      assertThat(new Lenght().validate("passwordpassword")).isEqualTo(List.of());
    }

    @Test
    void error_msg_when_violated() {
      assertThat(new Lenght().validate("")).isEqualTo(List.of("password must be mixed case"));
    }
  }
}
