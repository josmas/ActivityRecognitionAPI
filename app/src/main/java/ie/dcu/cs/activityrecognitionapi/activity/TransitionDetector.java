package ie.dcu.cs.activityrecognitionapi.activity;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import ie.dcu.cs.activityrecognitionapi.Constants;

/**
 * At the moment we are only interested in transitions from Idle to Active. But that Idle
 * transition is the only starting point allowed, so it has to be stored (in sharedprefs?).
 *
 */

public class TransitionDetector {

    private static final String TAG = TransitionDetector.class.getSimpleName();
    private static final int MIN_CONFIDENCE = 50; // 50 is totally random
    private Context context;

    public TransitionDetector(Context context) {
        this.context = context;
    }

    public boolean triggerActiveTransition(ArrayList<DetectedActivity> detectedActivities) {

        boolean anyActivity = false;
        for (DetectedActivity currentActivity: detectedActivities) {
            Log.i(TAG, Constants.getActivityString(context, currentActivity.getType()) +
                    " :: " + currentActivity.getConfidence());
            if (isActive(currentActivity)) {
                anyActivity = true;
                break;
            }
        }

        if (anyActivity) {
            if (isCurrentActive()) {
                return false;
            }
            else {
                Constants.setCurrentActivityState(context, Constants.ACTIVE);
                Log.i(TAG, " A transition to ACTIVE has occurred ================>>>>");
                return true;
            }
        }
        else {
            if (isCurrentActive()) {
                Constants.setCurrentActivityState(context, Constants.IDLE);
                Log.i(TAG, " A transition to IDLE has occurred ================>>>>");
            }
        }

        return false;
    }

    private boolean isCurrentActive() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.KEY_CURRENT_ACTIVITY_STATE, Constants.IDLE)
                .equals(Constants.ACTIVE);
    }


    private boolean isActive(DetectedActivity activity) {
        int activityType = activity.getType();
        boolean isActive = (activityType == DetectedActivity.IN_VEHICLE
                || activityType == DetectedActivity.ON_BICYCLE
                || activityType == DetectedActivity.ON_FOOT
                || activityType == DetectedActivity.RUNNING
                || activityType == DetectedActivity.WALKING);

        isActive = isActive && (activity.getConfidence() >= MIN_CONFIDENCE);

        return isActive;
    }
}
