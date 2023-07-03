package raw.runtime.truffle.runtime.exceptions;

import com.oracle.truffle.api.nodes.Node;


// This exception is thrown when an internal error occurs in the Raw Truffle runtime.
public class RawTruffleInternalErrorException extends RawTruffleRuntimeException {

    public static final String message = "Internal error";

    public RawTruffleInternalErrorException() {
        super(message);
    }

    public RawTruffleInternalErrorException(Throwable cause) {
        super(message, cause, null);
    }

    public RawTruffleInternalErrorException(Throwable cause, Node location) {
        super(message, cause, location);
    }


}
