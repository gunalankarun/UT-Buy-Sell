package com.a461group5.utbuysell;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class InboxActivity extends AppCompatActivity {
    private ListView allMessages;
    private String chatId;
    FirebaseUser user;
    private String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        allMessages = ListView (findViewById(R.id.listMessages));
        mDatabase = FirebaseDatabase.getInstance().getReference(path);
    }
}
