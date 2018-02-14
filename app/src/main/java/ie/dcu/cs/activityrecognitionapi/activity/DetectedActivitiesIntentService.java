package ie.dcu.cs.activityrecognitionapi.activity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = DetectedActivitiesIntentService.class.getSimpleName();

    public DetectedActivitiesIntentService() {
        super("DetectedActivitiesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            ArrayList<DetectedActivity> detectedActivities = (ArrayList<DetectedActivity>) result.getProbableActivities();

            if (new TransitionDetector(this).triggerActiveTransition(detectedActivities)) {
                Log.i(TAG, "We are ACTIVE NOW!!!");
                //TODO (jos) Create an EMA notification now.
            }
        }
        else {
            Log.i(TAG, "In the " + TAG + " but intent is null for some reason!!!");
        }
    }
}
