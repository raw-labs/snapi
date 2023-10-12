package raw.runtime.truffle.runtime.exceptions.validation;

import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(InteropLibrary.class)
public class ValidationErrorMessage implements TruffleObject {

    private final String message;
    private final ValidationErrorRange[] positions;

    public ValidationErrorMessage(String message, ValidationErrorRange[] positions) {
        this.message = message;
        this.positions = positions;
    }

    @ExportMessage
    public final boolean isString() {
        return true;
    }

    @ExportMessage
    public final String asString() {
        return message;
    }

    @ExportMessage
    public  final boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    public final Object readArrayElement(long index) {
        return positions[(int) index];
    }

    @ExportMessage
    public final long getArraySize() {
        return positions.length;
    }

    @ExportMessage
    public final boolean isArrayElementReadable(long index) {
        return index >= 0 && index < positions.length;
    }

}
