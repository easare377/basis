package org.odk.cersgis.basis.formentry.questions;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;

import java.util.HashMap;

public interface AnswersProvider {
    HashMap<FormIndex, IAnswerData> getAnswers();
}
