package org.odk.cersgis.basis.instancemanagement;

import org.odk.cersgis.basis.forms.Form;
import org.odk.cersgis.basis.forms.FormsRepository;
import org.odk.cersgis.basis.instances.Instance;
import org.odk.cersgis.basis.instances.InstancesRepository;

import java.util.List;

public class InstanceDeleter {

    private final InstancesRepository instancesRepository;
    private final FormsRepository formsRepository;

    public InstanceDeleter(InstancesRepository instancesRepository, FormsRepository formsRepository) {
        this.instancesRepository = instancesRepository;
        this.formsRepository = formsRepository;
    }

    public void delete(Long id) {
        Instance instance = instancesRepository.get(id);
        instancesRepository.delete(id);

        Form form = formsRepository.getOneByFormIdAndVersion(instance.getJrFormId(), instance.getJrVersion());
        if (form != null && form.isDeleted()) {
            List<Instance> otherInstances = instancesRepository.getAllNotDeletedByFormIdAndVersion(form.getJrFormId(), form.getJrVersion());
            if (otherInstances.isEmpty()) {
                formsRepository.delete(form.getId());
            }
        }
    }
}
