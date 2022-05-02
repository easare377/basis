package org.odk.cersgis.basis;

import android.os.AsyncTask;

import com.emma.general_backend_library.Functions;

import org.odk.cersgis.basis.listeners.CsvDownloadListener;
import org.odk.cersgis.basis.models.CsvData2;
import org.odk.cersgis.basis.models.Generic;
import org.odk.cersgis.basis.models.Paths;
import org.odk.cersgis.basis.models.Uris;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Emmanuel on 9/7/2019.
 */

public class RequestCsvTask extends AsyncTask<String, Integer, Boolean> {
    private final CsvDownloadListener listener;
    private final CsvData2 csvData;
    private final String phone_no;

    public RequestCsvTask(CsvDownloadListener listener, String formName, CsvData2 csvData, String phone_no) {
        this.listener = listener;
        this.csvData = csvData;
        this.phone_no = phone_no;
        try {
            String path = Paths.getBasisFormsDir() + "/" + formName + "-media";
            Functions.createDir(path);
        } catch (Exception e) {
//
        }
    }

    @Override
    protected Boolean doInBackground(String... uri) {
        int progress = 0;
        for (Generic genericCsv : csvData.getGenericCsvs()) {
            if(isCancelled()){
                return false;
            }
            if (!downloadCSV(genericCsv)) {
                return false;
            }
            progress++;
            publishProgress(progress);
        }
        if (csvData.getDynamicData().getTriggerUrl() == null || csvData.getDynamicData().getTriggerUrl().equals("")){
            return true;
        }
        String response = Functions.DownloadString(Uris.getBaseUrl()+ csvData.getDynamicData().getTriggerUrl() + "?contact=" + phone_no);
        if (response != null && response.equalsIgnoreCase("\"done\"")) {
            for (Generic genericCsv : csvData.getDynamicData().getDynamicCsvs()) {
                if(isCancelled()){
                    return false;
                }
                if (!downloadCSV(genericCsv)) {
                    return false;
                }
                progress++;
                publishProgress(progress);
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean downloadCSV(Generic cd) {
        try {
            URL url = new URL("http://" + cd.getUrl().replace(" ", "%20"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                FileOutputStream fileOutputStream;
                String path = Paths.getBasisFormsDir() + "/"
                        + cd.getForm_name() + "-media/" + cd.getCsvName() + ".csv";
                File fPath = new File(path);
                fileOutputStream = new FileOutputStream(fPath);
                // Do normal input or output stream reading
                byte[] buffer = new byte[1024];
                int byteCount;
                InputStream inputStream = conn.getInputStream();
                try {
                    while ((byteCount = inputStream.read(buffer)) != -1) {
                        if(isCancelled()){
                            return false;
                        }
                        fileOutputStream.write(buffer, 0, byteCount);
                    }
                    fileOutputStream.flush();
                } finally {
                    if(isCancelled()){
                        fPath.delete();
                    }
                    fileOutputStream.close();
                    inputStream.close();
                }
            } else if (responseCode == HttpsURLConnection.HTTP_NOT_FOUND) {
                return false;
            } else {
                return false;
            }

        } catch (Exception e) {
            //TODO Handle problems..\
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        listener.batchDownloadComplete(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.fileDownloadComplete(values[0],
                csvData.getGenericCsvs().length + csvData.getDynamicData().getDynamicCsvs().length);
        //formNameView.updateProgress (values[0]);
    }
}




