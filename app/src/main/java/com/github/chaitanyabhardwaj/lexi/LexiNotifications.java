package com.github.chaitanyabhardwaj.lexi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LexiNotifications {

    final public Context context;
    final private static String MAIN_CHANNEL_ID = "channel_01";
    private NotificationCompat.Builder builder;

    public LexiNotifications(Context context) {
        this.context = context;

        //Firestore update settings
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
    }

    public void buildNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_01_name);
            String description = context.getString(R.string.channel_01_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MAIN_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void buildNotification() {
        Log.d("TAGGED","STARTED");
        final int requestID = (int) System.currentTimeMillis();
        final Intent intent = new Intent(context, NotificationTapReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("app_info").document("word_pointer");
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d("TAGGED","STARTED1");
                String word;
                if(task.isSuccessful()) {
                    Log.d("TAGGED","STARTED2");
                    word = task.getResult().getData().get("word").toString();
                    intent.putExtra(NotificationTapReceiver.EXTRA_STRING_KEY, word);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder = new NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
                            .setSmallIcon(R.mipmap.favicon)
                            .setContentTitle(word)
                            .setContentText("Word of the day")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent);
                    showNotification();
                }
                else {
                    Log.d("Firestore", task.getException().toString());
                }
            }
        });
    }

    private void showNotification() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
    }

}
