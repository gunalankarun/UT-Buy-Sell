package com.a461group5.utbuysell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPostActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private EditText meeting_location;
    private EditText meeting_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        String messageId = bundle.getString("messageId");

        initUI();
    }

    private void initUI() {


        // all set text or set image will be set based on data from firebase
        TextView item_name = (TextView) findViewById(R.id.view_post_name);
        item_name.setText("Item Name");
        TextView seller_name = (TextView) findViewById(R.id.view_post_seller);
        seller_name.setText("Seller Name");
        TextView item_price = (TextView) findViewById(R.id.view_post_price);
        item_price.setText("Item Price");
        TextView description = (TextView) findViewById(R.id.view_post_description);
        description.setText("Description of Item or Items being sold.");

        //  image related
        // still not sure how to add in images or how to check if the xml image code is correct
        ImageView image = (ImageView) findViewById(R.id.view_post_picture1);
        //image.setImageURI();

        TextView item_tags = (TextView) findViewById(R.id.view_post_tags);
        item_tags.setText("Comma Seperated Item Tags");

        meeting_location = (EditText) findViewById(R.id.view_post_meeting_location);

        meeting_time = (EditText) findViewById(R.id.view_post_meeting_time);



    }
}
