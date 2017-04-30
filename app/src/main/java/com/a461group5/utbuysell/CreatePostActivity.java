package com.a461group5.utbuysell;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.a461group5.utbuysell.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText mTitleField;
    private EditText mDescriptionField;
    private EditText mPriceField;
    private EditText mCategoriesField;
    private Button mSubmitButton;
    private Button mCancelButton;
    private Button mPostPictures;

    private StorageReference mStorageRef;
    private Uri mOutputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        initUI();
    }

    private void initUI() {

        mTitleField = (EditText) findViewById(R.id.create_post_title);
        mDescriptionField = (EditText) findViewById(R.id.create_post_description);
        mPriceField = (EditText) findViewById(R.id.create_post_price);
        mCategoriesField = (EditText) findViewById(R.id.create_post_categories);
        mPostPictures = (Button) findViewById(R.id.create_post_images);
        mPostPictures.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

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


    private void openImageIntent() {
        File outputFile = null;
        try {
            outputFile = File.createTempFile("tmp", ".jpg", getCacheDir());
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        mOutputFileUri = Uri.fromFile(outputFile);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, 42);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 42) {
                boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (!isCamera) {
                    mOutputFileUri = data == null ? null : data.getData();
                }

            }
        }
    }



    private void submitPost() {
        // Disable submit button to avoid double submits
        mSubmitButton.setEnabled(false);

        String title = mTitleField.getText().toString().trim();
        String description = mDescriptionField.getText().toString().trim();
        String priceText = mPriceField.getText().toString().trim();
        String categoriesText = mCategoriesField.getText().toString().trim();

        // All are required
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(priceText) || TextUtils.isEmpty(categoriesText)) {
            Toast.makeText(CreatePostActivity.this, "All fields are required",
                    Toast.LENGTH_SHORT).show();
            mSubmitButton.setEnabled(true);
            return;
        }

        float price = Float.parseFloat(priceText);
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

        if (mOutputFileUri != null) {
            // Saves Picture into storage
            String uniqueName = getUniqueName();
            StorageReference photoRef = mStorageRef.child("postImages").child(key).child(uniqueName);
            mDatabase.child("posts").child(key).child("imagePaths").child(uniqueName).setValue(true);
            photoRef.putFile(mOutputFileUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CreatePostActivity.this, "Created Post!",
                                    Toast.LENGTH_SHORT).show();


                            mSubmitButton.setEnabled(true);

                            Intent myIntent = new Intent(CreatePostActivity.this, MainActivity.class);
                            CreatePostActivity.this.startActivity(myIntent);
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(CreatePostActivity.this, "Image Upload ERROR",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(CreatePostActivity.this, "Created Post!",
                    Toast.LENGTH_SHORT).show();
            
            mSubmitButton.setEnabled(true);
            Intent myIntent = new Intent(CreatePostActivity.this, MainActivity.class);
            CreatePostActivity.this.startActivity(myIntent);
        }

    }

    private String getUniqueName() {
        return "img_"+ System.currentTimeMillis();
    }
}
