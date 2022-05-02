package org.odk.cersgis.basis.formmanagement;

import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.core.reference.RootTranslator;
import org.odk.cersgis.basis.logic.FileReferenceFactory;
import org.odk.cersgis.basis.utilities.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.odk.cersgis.basis.utilities.FileUtils.LAST_SAVED_FILENAME;
import static org.odk.cersgis.basis.utilities.FileUtils.STUB_XML;
import static org.odk.cersgis.basis.utilities.FileUtils.write;

public class FormMetadataParser {

    private final ReferenceManager referenceManager;

    public FormMetadataParser(ReferenceManager referenceManager) {
        this.referenceManager = referenceManager;
    }

    public Map<String, String> parse(File file, File mediaDir) {
        // Add a stub last-saved instance to the tmp media directory so it will be resolved
        // when parsing a form definition with last-saved reference
        File tmpLastSaved = new File(mediaDir, LAST_SAVED_FILENAME);
        write(tmpLastSaved, STUB_XML.getBytes(StandardCharsets.UTF_8));
        referenceManager.reset();
        referenceManager.addReferenceFactory(new FileReferenceFactory(mediaDir.getAbsolutePath()));
        referenceManager.addSessionRootTranslator(new RootTranslator("jr://file-csv/", "jr://file/"));

        HashMap<String, String> metadata = FileUtils.getMetadataFromFormDefinition(file);
        referenceManager.reset();
        tmpLastSaved.delete();

        return metadata;
    }
}
