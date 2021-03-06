package org.odk.cersgis.basis.formmanagement.matchexactly;

import org.odk.cersgis.basis.formmanagement.FormDeleter;
import org.odk.cersgis.basis.formmanagement.FormDownloadException;
import org.odk.cersgis.basis.formmanagement.FormDownloader;
import org.odk.cersgis.basis.formmanagement.ServerFormDetails;
import org.odk.cersgis.basis.formmanagement.ServerFormsDetailsFetcher;
import org.odk.cersgis.basis.forms.Form;
import org.odk.cersgis.basis.forms.FormSourceException;
import org.odk.cersgis.basis.forms.FormsRepository;
import org.odk.cersgis.basis.instances.InstancesRepository;

import java.util.List;

public class ServerFormsSynchronizer {

    private final FormsRepository formsRepository;
    private final InstancesRepository instancesRepository;
    private final FormDownloader formDownloader;
    private final ServerFormsDetailsFetcher serverFormsDetailsFetcher;

    public ServerFormsSynchronizer(ServerFormsDetailsFetcher serverFormsDetailsFetcher, FormsRepository formsRepository, InstancesRepository instancesRepository, FormDownloader formDownloader) {
        this.serverFormsDetailsFetcher = serverFormsDetailsFetcher;
        this.formsRepository = formsRepository;
        this.instancesRepository = instancesRepository;
        this.formDownloader = formDownloader;
    }

    public void synchronize() throws FormSourceException {
        List<ServerFormDetails> formList = serverFormsDetailsFetcher.fetchFormDetails();
        List<Form> formsOnDevice = formsRepository.getAll();
        FormDeleter formDeleter = new FormDeleter(formsRepository, instancesRepository);

        formsOnDevice.stream().forEach(form -> {
            if (formList.stream().noneMatch(f -> form.getJrFormId().equals(f.getFormId()))) {
                formDeleter.delete(form.getId());
            }
        });

        boolean downloadException = false;

        for (ServerFormDetails form : formList) {
            if (form.isNotOnDevice() || form.isUpdated()) {
                try {
                    formDownloader.downloadForm(form, null, null);
                } catch (FormDownloadException e) {
                    downloadException = true;
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        if (downloadException) {
            throw new FormSourceException(FormSourceException.Type.FETCH_ERROR);
        }
    }
}
