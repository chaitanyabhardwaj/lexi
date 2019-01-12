package com.github.chaitanyabhardwaj.lexi;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        String quote = "Breathing dreams like air.";
        String author = "Scott Fitzgerald";
        //TODO: FETCH QUOTE AND AUTHOR NAME FROM FIRESTORE
        author = "- " + author;
        TextView textViewQuote = findViewById(R.id.text_quote);
        TextView textViewAuthor = findViewById(R.id.text_author);
        textViewQuote.setText(quote);
        textViewAuthor.setText(author);

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(r, 3000);
    }

    public Context getActivity() {
        return this;
    }

}
