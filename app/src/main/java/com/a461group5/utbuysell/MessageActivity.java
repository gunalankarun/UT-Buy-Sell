package com.a461group5.utbuysell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by irfanhasan on 3/29/17.
 */

public class MessageActivity extends Activity {
    private String recipientId;
    private EditText messageBodyField;
    private String messageBody;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);



        //get recipientId from the intent
        Intent intent = getIntent();
        recipientId = intent.getStringExtra("CHAT_ID");


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
            }
        });
    }

    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
