package org.odk.cersgis.basis.utilities;

import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;

import static org.odk.cersgis.basis.preferences.GeneralKeys.KEY_FONT_SIZE;

public class QuestionFontSizeUtils {
    public static final int DEFAULT_FONT_SIZE = 21;

    private QuestionFontSizeUtils() {

    }

    public static int getQuestionFontSize() {
        try {
            return Integer.parseInt(String.valueOf(GeneralSharedPreferences.getInstance().get(KEY_FONT_SIZE)));
        } catch (Exception | Error e) {
            return DEFAULT_FONT_SIZE;
        }
    }
}
