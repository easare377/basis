package org.odk.cersgis.basis.listeners;

public interface CsvDownloadListener {
    void fileDownloadComplete(int totalFilesDownloaded, int totalFiles);
    void batchDownloadComplete(boolean isSuccess);
}
