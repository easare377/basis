package org.odk.cersgis.basis.configure.qr;

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.odk.cersgis.basis.preferences.JsonPreferencesGenerator;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;


public interface QRCodeGenerator {

    String generateQRCode(Collection<String> selectedPasswordKeys, JsonPreferencesGenerator jsonPreferencesGenerator) throws JSONException, NoSuchAlgorithmException, IOException, WriterException;
}
