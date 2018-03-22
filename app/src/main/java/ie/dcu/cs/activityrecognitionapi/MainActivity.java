package ie.dcu.cs.activityrecognitionapi;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import ie.dcu.cs.activityrecognitionapi.activity.DetectedActivitiesIntentService;
import ie.dcu.cs.activityrecognitionapi.activity.DetectedTransitionIntentService;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private ActivityRecognitionClient activityRecognitionClient;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        Constants.setCurrentActivityState(this, Constants.IDLE);
        activityRecognitionClient = ActivityRecognition.getClient(context);
    }

    public void requestActivityUpdatesButtonHandler(View view) {
        Task<Void> task = activityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent());

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(context,
                        getString(R.string.activity_updates_enabled),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, getString(R.string.activity_updates_not_enabled));
                Toast.makeText(context,
                        getString(R.string.activity_updates_not_enabled),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = activityRecognitionClient.removeActivityUpdates(
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(context,
                        getString(R.string.activity_updates_removed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Failed to enable activity recognition.");
                Toast.makeText(context, getString(R.string.activity_updates_not_removed),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);

        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void requestTransitionUpdatesButtonHandler(View view) {
        Log.i(TAG, "Requesting Transitions");
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());


        Intent intent = new Intent(this, DetectedTransitionIntentService.class);
        PendingIntent transitionPendingIntent = PendingIntent.getService(this, 10,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);
        Task<Void> task = activityRecognitionClient.requestActivityTransitionUpdates(request,
                transitionPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(context,
                                "Transition updates set up",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(context,
                                "Transition updates FAILED to be set up",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        );
    }

    @Override
    protected void onStop() {
        removeActivityUpdatesButtonHandler();
        super.onStop();
    }
}
