package org.odk.cersgis.basis.widgets.support;

import androidx.lifecycle.LiveData;

import com.google.common.io.Files;

import org.odk.cersgis.basis.utilities.QuestionMediaManager;
import org.odk.cersgis.utilities.Result;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FakeQuestionMediaManager implements QuestionMediaManager {

    public final Map<String, String> originalFiles = new HashMap<>();
    public final Map<String, String> recentFiles = new HashMap<>();
    private final File tempDir = Files.createTempDir();

    @Override
    public LiveData<Result<String>> createAnswerFile(File file) {
        return null;
    }

    @Override
    public File getAnswerFile(String fileName) {
        return new File(tempDir, fileName);
    }

    @Override
    public void deleteAnswerFile(String questionIndex, String fileName) {
        originalFiles.put(questionIndex, fileName);
    }

    @Override
    public void replaceAnswerFile(String questionIndex, String fileName) {
        recentFiles.put(questionIndex, fileName);
    }

    public File getDir() {
        return tempDir;
    }
}
