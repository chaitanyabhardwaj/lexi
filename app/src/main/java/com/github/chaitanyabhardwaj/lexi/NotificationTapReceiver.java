package com.github.chaitanyabhardwaj.lexi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

public class NotificationTapReceiver extends BroadcastReceiver {

    final public static String EXTRA_STRING_KEY = "<extra-word-key>";

    @Override
    public void onReceive(Context context, Intent intent) {
        String word = intent.getStringExtra(EXTRA_STRING_KEY);
        Log.d("tagged", word);
        try {
            String query = URLEncoder.encode(word, "UTF-8");
            Uri uri = Uri.parse("http://www.google.com/#q=" + query);
            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent1);
        }
        catch(IOException ex) {
            Log.e("ERROR", ex.toString());
        }
    }

}
