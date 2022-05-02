package org.odk.cersgis.basis.configure.qr;

import android.content.Context;

import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeResult;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.MainMenuActivity;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.analytics.AnalyticsEvents;
import org.odk.cersgis.basis.configure.SettingsImporter;
import org.odk.cersgis.basis.fragments.BarCodeScannerFragment;
import org.odk.cersgis.basis.injection.DaggerUtils;
import org.odk.cersgis.basis.utilities.FileUtils;
import org.odk.cersgis.basis.utilities.ToastUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.DataFormatException;

import javax.inject.Inject;

import static org.odk.cersgis.basis.activities.ActivityUtils.startActivityAndCloseAllOthers;
import static org.odk.cersgis.basis.utilities.CompressionUtils.decompress;

public class QRCodeScannerFragment extends BarCodeScannerFragment {

    @Inject
    SettingsImporter settingsImporter;

    @Inject
    Analytics analytics;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DaggerUtils.getComponent(context).inject(this);
    }

    @Override
    protected void handleScanningResult(BarcodeResult result) throws IOException, DataFormatException {
        boolean importSuccess = settingsImporter.fromJSON(decompress(result.getText()));
        String settingsHash = FileUtils.getMd5Hash(new ByteArrayInputStream(result.getText().getBytes()));

        if (importSuccess) {
            ToastUtils.showLongToast(getString(R.string.successfully_imported_settings));
            analytics.logEvent(AnalyticsEvents.SETTINGS_IMPORT_QR, "Success", settingsHash);
            startActivityAndCloseAllOthers(requireActivity(), MainMenuActivity.class);
        } else {
            ToastUtils.showLongToast(getString(R.string.invalid_qrcode));
            analytics.logEvent(AnalyticsEvents.SETTINGS_IMPORT_QR, "No valid settings", settingsHash);
        }
    }

    @Override
    protected Collection<String> getSupportedCodeFormats() {
        return Collections.singletonList(IntentIntegrator.QR_CODE);
    }
}
