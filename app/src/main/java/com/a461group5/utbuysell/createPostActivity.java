package com.a461group5.utbuysell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class createPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initUI();
    }

    private void initUI() {

        Button logOutButton = (Button) findViewById(R.id.create_post_cancel);
        TextView ProfileHeader = (TextView) findViewById(R.id.create_post_header);
        ProfileHeader.setText("Title of Item");

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(createPostActivity.this, MainActivity.class);
                createPostActivity.this.startActivity(myIntent);
            }
        });
    }
}
