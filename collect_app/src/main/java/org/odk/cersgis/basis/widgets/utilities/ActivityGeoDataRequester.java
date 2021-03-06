package org.odk.cersgis.basis.widgets.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.javarosa.form.api.FormEntryPrompt;
import org.odk.cersgis.basis.activities.GeoPointActivity;
import org.odk.cersgis.basis.activities.GeoPointMapActivity;
import org.odk.cersgis.basis.activities.GeoPolyActivity;
import org.odk.cersgis.basis.listeners.PermissionListener;
import org.odk.cersgis.basis.utilities.ApplicationConstants;
import org.odk.cersgis.basis.utilities.PermissionUtils;
import org.odk.cersgis.basis.utilities.WidgetAppearanceUtils;
import org.odk.cersgis.basis.widgets.interfaces.GeoDataRequester;

public class ActivityGeoDataRequester implements GeoDataRequester {
    public static final String LOCATION = "gp";
    public static final String ACCURACY_THRESHOLD = "accuracyThreshold";
    public static final String READ_ONLY = "readOnly";
    public static final String DRAGGABLE_ONLY = "draggable";

    private final PermissionUtils permissionUtils;

    public ActivityGeoDataRequester(PermissionUtils permissionUtils) {
        this.permissionUtils = permissionUtils;
    }

    @Override
    public void requestGeoPoint(Context context, FormEntryPrompt prompt, String answerText, WaitingForDataRegistry waitingForDataRegistry) {

        permissionUtils.requestLocationPermissions((Activity) context, new PermissionListener() {
            @Override
            public void granted() {
                waitingForDataRegistry.waitForData(prompt.getIndex());

                Bundle bundle = new Bundle();

                if (answerText != null && !answerText.isEmpty()) {
                    bundle.putDoubleArray(LOCATION, GeoWidgetUtils.getLocationParamsFromStringAnswer(answerText));
                }

                bundle.putDouble(ACCURACY_THRESHOLD, GeoWidgetUtils.getAccuracyThreshold(prompt.getQuestion()));
                bundle.putBoolean(READ_ONLY, prompt.isReadOnly());
                bundle.putBoolean(DRAGGABLE_ONLY, hasPlacementMapAppearance(prompt));

                Intent intent = new Intent(context, isMapsAppearance(prompt) ? GeoPointMapActivity.class : GeoPointActivity.class);
                intent.putExtras(bundle);
                ((Activity) context).startActivityForResult(intent, ApplicationConstants.RequestCodes.LOCATION_CAPTURE);
            }

            @Override
            public void denied() {
            }
        });
    }

    @Override
    public void requestGeoShape(Context context, FormEntryPrompt prompt, String answerText, WaitingForDataRegistry waitingForDataRegistry) {
        permissionUtils.requestLocationPermissions((Activity) context, new PermissionListener() {
            @Override
            public void granted() {
                waitingForDataRegistry.waitForData(prompt.getIndex());

                Intent intent = new Intent(context, GeoPolyActivity.class);
                intent.putExtra(GeoPolyActivity.ANSWER_KEY, answerText);
                intent.putExtra(GeoPolyActivity.OUTPUT_MODE_KEY, GeoPolyActivity.OutputMode.GEOSHAPE);
                intent.putExtra(READ_ONLY, prompt.isReadOnly());

                ((Activity) context).startActivityForResult(intent, ApplicationConstants.RequestCodes.GEOSHAPE_CAPTURE);
            }

            @Override
            public void denied() {
            }
        });
    }

    @Override
    public void requestGeoTrace(Context context, FormEntryPrompt prompt, String answerText, WaitingForDataRegistry waitingForDataRegistry) {
        permissionUtils.requestLocationPermissions((Activity) context, new PermissionListener() {
            @Override
            public void granted() {
                waitingForDataRegistry.waitForData(prompt.getIndex());

                Intent intent = new Intent(context, GeoPolyActivity.class);
                intent.putExtra(GeoPolyActivity.ANSWER_KEY, answerText);
                intent.putExtra(GeoPolyActivity.OUTPUT_MODE_KEY, GeoPolyActivity.OutputMode.GEOTRACE);
                intent.putExtra(READ_ONLY, prompt.isReadOnly());

                ((Activity) context).startActivityForResult(intent, ApplicationConstants.RequestCodes.GEOTRACE_CAPTURE);
            }

            @Override
            public void denied() {
            }
        });
    }

    private boolean isMapsAppearance(FormEntryPrompt prompt) {
        return hasMapsAppearance(prompt) || hasPlacementMapAppearance(prompt);
    }

    private boolean hasMapsAppearance(FormEntryPrompt prompt) {
        return WidgetAppearanceUtils.hasAppearance(prompt, WidgetAppearanceUtils.MAPS);
    }

    private boolean hasPlacementMapAppearance(FormEntryPrompt prompt) {
        return WidgetAppearanceUtils.hasAppearance(prompt, WidgetAppearanceUtils.PLACEMENT_MAP);
    }
}