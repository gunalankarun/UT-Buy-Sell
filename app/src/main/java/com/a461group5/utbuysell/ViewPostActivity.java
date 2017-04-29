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
    private TextView item_name;
    private TextView seller_name;
    private TextView item_price;
    private TextView description;
    private TextView meeting_location;
    private TextView meeting_time;
    private Button mMessageButton;
    private Button mFavoriteButton;


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
        item_name = (TextView) findViewById(R.id.view_post_name);
        item_name.setText("Item Name");

        seller_name = (TextView) findViewById(R.id.view_post_seller);
        seller_name.setText("Seller Name");

        item_price = (TextView) findViewById(R.id.view_post_price);
        item_price.setText("Item Price");

        description = (TextView) findViewById(R.id.view_post_description);
        description.setText("Description of Item or Items being sold.");

        //  image related
        // still not sure how to add in images or how to check if the xml image code is correct
        ImageView image = (ImageView) findViewById(R.id.view_post_picture1);
        //image.setImageURI();

        TextView item_tags = (TextView) findViewById(R.id.view_post_tags);
        item_tags.setText("Comma Seperated Item Tags");

        description = (TextView) findViewById(R.id.view_post_meeting_location);
        description.setText("Meeting Location");

        description = (TextView) findViewById(R.id.view_post_meeting_time);
        description.setText("Meeting Time");

        mMessageButton = (Button) findViewById(R.id.view_post_message);
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code here
            }
        });

        mFavoriteButton = (Button) findViewById(R.id.view_post_favorite);
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code here
            }
        });



    }
}
