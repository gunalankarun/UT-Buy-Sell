package com.a461group5.utbuysell;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.a461group5.utbuysell.models.Post;
import com.a461group5.utbuysell.models.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class ViewPostActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private TextView item_name;
    private TextView seller_name;
    private TextView item_price;
    private TextView description;
    private TextView categories;

    private ImageView image1;

    private Button mFavoriteButton;
    private FloatingActionButton floatingActionButton;

    String postId;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        postId = bundle.getString("postId");

        initUI();
    }

    private void initUI() {
        // TODO: Do ImageView Stuff

        // all set text or set image will be set based on data from firebase
        item_name = (TextView) findViewById(R.id.view_post_name);
        seller_name = (TextView) findViewById(R.id.view_post_seller);
        item_price = (TextView) findViewById(R.id.view_post_price);
        description = (TextView) findViewById(R.id.view_post_description);
        categories = (TextView) findViewById(R.id.view_post_tags);
        mFavoriteButton = (Button) findViewById(R.id.view_post_favorite);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_message_button);
        image1 = (ImageView) findViewById(R.id.view_post_picture1);

        context = ViewPostActivity.this;

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoritePost();
            }
        });

        //floatingButtonAnimateIn();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageSeller();
            }
        });

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        CoordinatorLayout layoutContainer = (CoordinatorLayout) findViewById(R.id.view_post_container);
        layoutContainer.startAnimation(fadeIn);

        FirebaseDatabase.getInstance().getReference("posts/" + postId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post currentPost = dataSnapshot.getValue(Post.class);

                        item_name.setText(currentPost.title);
                        item_price.setText("Price: $" + String.format("%.2f", currentPost.price));
                        description.setText("Description: " + currentPost.description);
                        String cats = "";
                        for (String c : currentPost.categories.keySet()) {
                            cats += c + ", ";
                        }
                        categories.setText("Categories: " + cats.substring(0,cats.length()-2));

                        FirebaseDatabase.getInstance().getReference("users/" + currentPost.seller).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User seller_user = dataSnapshot.getValue(User.class);
                                        seller_name.setText("Seller: " + seller_user.getName());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                        if(currentPost.imagePaths != null) {
                            for (String imgPath : currentPost.imagePaths.keySet()) {
                                Task<Uri> uri = FirebaseStorage.getInstance().getReference().child("postImages/").
                                        child(postId).child(imgPath).getDownloadUrl();

                                uri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        //holder.mImageView.setImageBitmap(getImageBitmap(task.getResult().toString()));
                                        Uri uri = task.getResult();
                                        Glide
                                                .with(context)
                                                .load(uri) // the uri you got from Firebase
                                                .centerCrop()
                                                .into(image1); //Your imageView variable
                                    }
                                });

                            }
                        } else {
                            //put default picture here
                            image1.setImageDrawable(context.getDrawable(R.drawable.shopping));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void favoritePost() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Add favoritePost reference to User
        mDatabase.child("users").child(user.getUid()).child("favoritePosts").child(postId).setValue(true);

        //Add user to Post's favorite Map
        mDatabase.child("posts").child(postId).child("favoritedUsers").child(user.getUid()).setValue(true);

    }

    private void messageSeller() {
        favoritePost();



        FirebaseDatabase.getInstance().getReference("posts/" + postId).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Post currentPost = dataSnapshot.getValue(Post.class);
                        checkAndSwitchToChat(currentPost.seller);
                        //Intent i = new Intent(ViewPostActivity.this, MessageActivity.class);
                        //i.putExtra("sellerId", currentPost.seller);
                        //startActivity(i);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void checkAndSwitchToChat(final String sellerId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/convos").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Map<String, String> convos = (Map<String, String>) dataSnapshot.getValue();
                            if (convos.containsKey(sellerId)) {
                                Intent i = new Intent(ViewPostActivity.this, MessageActivity.class);
                                i.putExtra("CHAT_ID", convos.get(sellerId));
                                startActivity(i);
                            } else {
                                Intent i = new Intent(ViewPostActivity.this, MessageActivity.class);
                                i.putExtra("sellerId", sellerId);
                                startActivity(i);
                            }
                        } else {
                            Intent i = new Intent(ViewPostActivity.this, MessageActivity.class);
                            i.putExtra("sellerId", sellerId);
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void floatingButtonAnimateIn() {
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setAlpha(0f);
        floatingActionButton.setScaleX(0f);
        floatingActionButton.setScaleY(0f);
        floatingActionButton.animate()
                .alpha(1)
                .scaleX(1)
                .scaleY(1)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        floatingActionButton.animate()
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }
}
