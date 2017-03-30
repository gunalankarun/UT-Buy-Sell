package com.a461group5.utbuysell;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by irfanhasan on 3/29/17.
 */

public class MessageActivity extends Activity {
    private String chatId;
    private EditText messageBodyField;
    private String messageBody;
    private String path;
    private MessageAdapter messageAdapter;
    private ListView messagesList;

    DatabaseReference mDatabase;
    FirebaseUser user;
    Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);

        //get chatId from the intent
//        Intent intent = getIntent();
//        chatId = intent.getStringExtra("CHAT_ID");
        chatId = "chatTestKey";

        path = "chats/" + chatId;


        mDatabase = FirebaseDatabase.getInstance().getReference(path);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                chat = snap.getValue(Chat.class);
            }

            @Override
            public void onCancelled(DatabaseError e) { }
        });




        ValueEventListener chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Chat object and use the values to update the UI
                 chat = dataSnapshot.getValue(Chat.class);
                messageAdapter.addMessage(chat.getLastMessage(), MessageAdapter.DIRECTION_OUTGOING);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(chatListener);

        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        //listen for a click on the send button
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send the message!
                messageBody = messageBodyField.getText().toString();
                if (messageBody.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
                    return;
                }
                sendMessage(messageBody);

            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    void sendMessage(String msg) {
        if (user != null && chat != null)
            chat.sendMessage(msg, user.getUid());
        try {
            mDatabase.setValue(chat);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
