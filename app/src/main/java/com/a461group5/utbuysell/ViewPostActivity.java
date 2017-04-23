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

        TextView item_name = (TextView) findViewById(R.id.view_post_name);
        item_name.setText("Item Name");
        TextView description = (TextView) findViewById(R.id.view_post_description);
        description.setText("Description of Item or Items being sold.");
        TextView seller_name = (TextView) findViewById(R.id.view_post_seller);
        seller_name.setText("Seller Name");
        ImageView image = (ImageView) findViewById(R.id.view_post_picture1);


    }
}
