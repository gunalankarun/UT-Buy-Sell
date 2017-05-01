package com.a461group5.utbuysell;
import com.a461group5.utbuysell.R;
import com.a461group5.utbuysell.models.Post;
import com.a461group5.utbuysell.models.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FullScreenImage  extends Activity {


    @SuppressLint("NewApi")

    String postId;
    ImageView imgDisplay;
    Button btnClose;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_full);

        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("postId");

        context = FullScreenImage.this;

//        Bundle extras = getIntent().getExtras();
//        Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");


        imgDisplay = (ImageView) findViewById(R.id.imgDisplay);
        btnClose = (Button) findViewById(R.id.btnClose);


        btnClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FullScreenImage.this.finish();
            }
        });

        FirebaseDatabase.getInstance().getReference("posts/" + postId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post currentPost = dataSnapshot.getValue(Post.class);

                        if(currentPost.imagePaths != null) {
                            for (String imgPath : currentPost.imagePaths.keySet()) {
                                Task<Uri> uri = FirebaseStorage.getInstance().getReference().child("postImages/").
                                        child(postId).child(imgPath).getDownloadUrl();

                                uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        Uri uri = task.getResult();
                                        Glide
                                                .with(context)
                                                .load(uri) // the uri you got from Firebase
                                                //.centerCrop()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(imgDisplay); //Your imageView variable
                                    }
                                });

                            }
                        } else {
                            //put default picture here
                            imgDisplay.setImageDrawable(context.getDrawable(R.drawable.shopping));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


}