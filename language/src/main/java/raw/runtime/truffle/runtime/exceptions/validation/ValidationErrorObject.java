package raw.runtime.truffle.runtime.exceptions.validation;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.ErrorMessage;

import java.util.List;

@ExportLibrary(InteropLibrary.class)
public class ValidationErrorObject implements TruffleObject {

    public final ValidationErrorMessage[] errors;

    public ValidationErrorObject(List<ErrorMessage> errors) {
        this.errors = new ValidationErrorMessage[errors.size()];
        for (int i = 0; i < errors.size(); i++) {
            ErrorMessage error = errors.get(i);
            ValidationErrorRange[] positions = new ValidationErrorRange[error.positions().length()];
            for (int j = 0; j < error.positions().length(); j++) {
                positions[j] = new ValidationErrorRange(
                        new ValidationErrorPosition(
                                error.positions().apply(j).begin().line(),
                                error.positions().apply(j).begin().column()
                        ),
                        new ValidationErrorPosition(
                                error.positions().apply(j).end().line(),
                                error.positions().apply(j).end().column()
                        )
                );
            }
            this.errors[i] = new ValidationErrorMessage(error.message(), positions);
        }
    }

    @ExportMessage
    public final boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    public final  Object readArrayElement(long index) {
        return errors[(int) index];
    }

    @ExportMessage
    public final long getArraySize() {
        return errors.length;
    }

    @ExportMessage
    public final boolean isArrayElementReadable(long index) {
        return index >= 0 && index < errors.length;
    }

}
