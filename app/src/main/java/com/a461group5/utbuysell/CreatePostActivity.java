package com.a461group5.utbuysell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a461group5.utbuysell.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreatePostActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText mtitleField;
    private EditText mdescriptionField;
    private EditText mpriceField;
    private EditText mcategoriesField;
    private Button mSubmitButton;
    private Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        initUI();
    }

    private void initUI() {

        mtitleField = (EditText) findViewById(R.id.create_post_title);
        mdescriptionField = (EditText) findViewById(R.id.create_post_description);
        mpriceField = (EditText) findViewById(R.id.create_post_price);
        mcategoriesField = (EditText) findViewById(R.id.create_post_categories);

        mSubmitButton = (Button) findViewById(R.id.create_post_submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost();

            }
        });

        mCancelButton = (Button) findViewById(R.id.create_post_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(CreatePostActivity.this, MainActivity.class);
                CreatePostActivity.this.startActivity(myIntent);
            }
        });
    }

    private void submitPost() {
        // Disable submit button to avoid double submits
        mSubmitButton.setEnabled(false);

        String title = mtitleField.getText().toString().trim();
        String description = mdescriptionField.getText().toString().trim();
        String priceText = mpriceField.getText().toString().trim();
        String categoriesText = mcategoriesField.getText().toString().trim();

        // All are required
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceText) || TextUtils.isEmpty(categoriesText)) {
            Toast.makeText(CreatePostActivity.this, "All fields are required",
                    Toast.LENGTH_SHORT).show();
            mSubmitButton.setEnabled(true);
            return;
        }

        int price = Integer.parseInt(priceText);
        String[] categories = categoriesText.split(",");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if User is verified
        if (!user.isEmailVerified()) {
            Toast.makeText(CreatePostActivity.this, "ERROR: You must verify your email in order to Post.",
                    Toast.LENGTH_SHORT).show();
            mSubmitButton.setEnabled(true);
            return;
        }

        // Save Post
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(title, description, user.getUid(), price, categories);
        mDatabase.child("posts").child(key).setValue(post);

        // Add post reference to User
        mDatabase.child("users").child(user.getUid()).child("sellerPosts").child(key).setValue(true);

        // Add post reference to Category
        for (String c : categories){
            String category = c.trim().toLowerCase();
            mDatabase.child("categories").child(category).child(key).setValue(true);
        }


        // Reenable Button
        mSubmitButton.setEnabled(true);

        Toast.makeText(CreatePostActivity.this, "Created Post!",
                Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(CreatePostActivity.this, MainActivity.class);
        CreatePostActivity.this.startActivity(myIntent);



    }
}
