package raw.runtime.truffle.runtime.exceptions;

import raw.compiler.ErrorMessage;

import java.util.List;

public class ValidationErrorObject {

    public final List<ErrorMessage> errors;

    public ValidationErrorObject(List<ErrorMessage> errors) {
        this.errors = errors;
    }

}
