package org.odk.cersgis.basis.configure.qr;

import java.io.InputStream;

public interface QRCodeDecoder {

    String decode(InputStream inputStream) throws InvalidException, NotFoundException;

    class InvalidException extends Exception {
    }

    class NotFoundException extends Exception {
    }
}
