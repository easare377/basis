/*
 * Copyright (C) 2013 Nafundi LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.cersgis.basis.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;

import org.odk.cersgis.basis.databinding.UrlWidgetAnswerBinding;
import org.odk.cersgis.basis.formentry.questions.QuestionDetails;
import org.odk.cersgis.basis.utilities.CustomTabHelper;
import org.odk.cersgis.basis.utilities.ToastUtils;

@SuppressLint("ViewConstructor")
public class UrlWidget extends QuestionWidget {
    UrlWidgetAnswerBinding binding;

    private final CustomTabHelper customTabHelper;

    public UrlWidget(Context context, QuestionDetails questionDetails, CustomTabHelper customTabHelper) {
        super(context, questionDetails);
        this.customTabHelper = customTabHelper;
    }

    @Override
    protected View onCreateAnswerView(Context context, FormEntryPrompt prompt, int answerFontSize) {
        binding = UrlWidgetAnswerBinding.inflate(((Activity) context).getLayoutInflater());
        binding.urlButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, answerFontSize);
        binding.urlButton.setOnClickListener(v -> onButtonClick());

        return binding.getRoot();
    }

    @Override
    public void clearAnswer() {
        ToastUtils.showShortToast("URL is readonly");
    }

    @Override
    public IAnswerData getAnswer() {
        return getFormEntryPrompt().getAnswerValue() == null
                ? null
                : new StringData(getFormEntryPrompt().getAnswerText());
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        binding.urlButton.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        binding.urlButton.cancelLongPress();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (customTabHelper.getServiceConnection() != null) {
            getContext().unbindService(customTabHelper.getServiceConnection());
        }
    }

    public void onButtonClick() {
        if (getFormEntryPrompt().getAnswerValue() != null) {
            customTabHelper.bindCustomTabsService(getContext(), null);
            customTabHelper.openUri(getContext(), Uri.parse(getFormEntryPrompt().getAnswerText()));
        } else {
            ToastUtils.showShortToast("No URL set");
        }
    }
}