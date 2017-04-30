package com.a461group5.utbuysell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.a461group5.utbuysell.adapters.MessageAdapter;
import com.a461group5.utbuysell.models.Chat;
import com.a461group5.utbuysell.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageActivity extends Activity {
    private String chatId;
    private EditText messageBodyField;
    private String messageBody;
    private String path;
    private String receiverId;
    private MessageAdapter messageAdapter;
    private ListView messagesList;
    private boolean firstTime = true;
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
        Intent intent = getIntent();
        chatId = intent.getStringExtra("CHAT_ID");
        receiverId = intent.getStringExtra("sellerId");
        //Need to distinguish if this is the first time starting this chat
        if (chatId != null) {
            initDatabaseRef(chatId);
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference("chats");
        }

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
                if (chatId == null) {
                    chatId = mDatabase.push().getKey(); //create a new chat in DB
                    chat = new Chat(receiverId, user.getUid());
                    mDatabase.child(chatId).setValue(chat);
                    initDatabaseRef(chatId);

                    //add this chat to both people's inboxes
                    FirebaseDatabase.getInstance().getReference("users").child(receiverId)
                            .child("chats").child(chatId).setValue(user.getDisplayName()); //adding to other person's inbox
                    FirebaseDatabase.getInstance().getReference("users/" + receiverId). //adding to this user's inbox
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.getValue(User.class);
                                    FirebaseDatabase.getInstance().getReference("users").
                                            child(user.getUid()).child("chats").child(chatId).
                                            setValue(u.getName());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                sendMessage(messageBody);
                messageBodyField.getText().clear();

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

    //checks if message was a sent or received message (from the viewpoint of this user)
    //and displays correct type of message bubble
    void displayMessage(Chat.Message msg) {
        if (msg.getSenderId().equals(user.getUid())) {
            messageAdapter.addMessage(msg, MessageAdapter.DIRECTION_OUTGOING);
        } else {
            messageAdapter.addMessage(msg, MessageAdapter.DIRECTION_INCOMING);
        }
    }

    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Sets FirebaseDB reference to correct path (ie make it point to where current chat messages are stored)
     * @param chatId the id that points to the chat that the  current instance of this activity represents
     */
    private void initDatabaseRef(@NonNull String chatId) {
        path = "chats/" + chatId;
        mDatabase = FirebaseDatabase.getInstance().getReference(path);
        ValueEventListener chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Chat object and use the values to update the UI
                chat = dataSnapshot.getValue(Chat.class);


                //Need to check if this first time this chat has been added to database
                //If so then there are no messages and we shouldn't try to access the messages list
                if (chat.getMessages() != null) {
                    //also this listener triggers once as soon as it is registered
                    //so we will load past messages at this time
                    if (firstTime) {
                        for (Chat.Message m : chat.getMessages()) {
                            displayMessage(m);
                        }
                        firstTime = false;
                    } else {
                        displayMessage(chat.getLastMessage());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(chatListener);
    }
}
