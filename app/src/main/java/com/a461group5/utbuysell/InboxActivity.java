package com.a461group5.utbuysell;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class InboxActivity extends AppCompatActivity {
    //private ListView allMessages;
    private String chatId;
    FirebaseUser user;
    DatabaseReference mDatabase;
    private String userID;
    private String path;
    private ListView userMessage;
    private ArrayList<String> names;
    ArrayAdapter<String> namesArrayAdapter;
    private HashMap<String, Boolean> chat_history;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        userMessage = (ListView)findViewById(R.id.usersListView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        Intent intent = getIntent();
        path = "/users/" + userID + "/chats";

        mDatabase = FirebaseDatabase.getInstance().getReference(path);
        //chat_history = mDatabase.getReference(path);
        // returns the hashmap of chats that have started??
        chat_history = new HashMap<String, Boolean>();
        chat_history.put("Shamma", true);
    }

    private void createInboxList() {
        for (String u : chat_history.keySet()) {
            if (chat_history.get(u)) {
                names.add(u);
                System.out.print(u);
            }
        }
        namesArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.user_list, names);


        userMessage.setAdapter(namesArrayAdapter);


        userMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                //open messaging activity
                Intent intent = new Intent(InboxActivity.this, MessageActivity.class);
                //message activity parameters?? (user in, user out?)
                InboxActivity.this.startActivity(intent);
            }
        });


        //ArrayList<Inbox> all_users = new ArrayList<Inbox>();



       //allMessages = ListView (findViewById(R.id.listMessages));
        //mDatabase = FirebaseDatabase.getInstance().getReference(path);

    }
    /*
    Implementation:
    1. have a data base with chat history of every user, based on user-to-user

    users
    2. everytime a user starts a chat with someone new, that's added to the data base
    3. on create page should have a for-loop that displays all those messages
    4. confused about UI, not really sure how to stack the messages
    5. on-click activity should open page of messages
     */
}
