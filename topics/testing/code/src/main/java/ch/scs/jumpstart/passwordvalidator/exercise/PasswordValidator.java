package ch.scs.jumpstart.passwordvalidator.exercise;

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PasswordValidator {

  private final List<Validator> validators;

  public PasswordValidator(List<Validator> validators) {
    this.validators = validators;
  }

  public List<String> validate(String password) {
    return validators.stream()
        .map(val -> val.validate(password))
        .flatMap(Collection::stream)
        .toList();
  }
}

interface Validator {
  public List<String> validate(String password);
}

class Lenght implements Validator {

  @Override
  public List<String> validate(String password) {
    if (password.length() < 12) {
      return List.of("must have at least 12 characters.");
    }
    return List.of();
  }
}

class MixedCase implements Validator {

  @Override
  public List<String> validate(String password) {
    if (!StringUtils.isMixedCase(password)) {
      return List.of("password must be mixed case");
    }
    return List.of();
  }
}
