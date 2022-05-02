package org.odk.cersgis.basis.database;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.forms.FormsRepository;
import org.odk.cersgis.basis.forms.FormsRepositoryTest;
import org.odk.cersgis.basis.storage.StorageInitializer;
import org.odk.cersgis.basis.storage.StoragePathProvider;
import org.odk.cersgis.basis.storage.StorageSubdirectory;
import org.odk.cersgis.basis.support.RobolectricHelpers;

@RunWith(AndroidJUnit4.class)
public class DatabaseFormsRepositoryTest extends FormsRepositoryTest {

    private StoragePathProvider storagePathProvider;

    @Before
    public void setup() {
        RobolectricHelpers.mountExternalStorage();
        storagePathProvider = new StoragePathProvider();
        new StorageInitializer().createOdkDirsOnStorage();
    }

    @Override
    public FormsRepository buildSubject() {
        return new DatabaseFormsRepository();
    }

    @Override
    public String getFormFilesPath() {
        return storagePathProvider.getDirPath(StorageSubdirectory.FORMS);
    }
}
