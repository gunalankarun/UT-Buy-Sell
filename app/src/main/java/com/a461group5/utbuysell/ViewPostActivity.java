package com.a461group5.utbuysell;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a461group5.utbuysell.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private FloatingActionButton floatingActionButton;

    String postId;


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

        meeting_location = (TextView) findViewById(R.id.view_post_meeting_location);
        meeting_location.setText("Meeting Location");

        meeting_time = (TextView) findViewById(R.id.view_post_meeting_time);
        meeting_time.setText("Meeting Time");

        mFavoriteButton = (Button) findViewById(R.id.view_post_favorite);
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoritePost();
            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_message_button);
        //floatingButtonAnimateIn();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageSeller();
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
                        Intent i = new Intent(ViewPostActivity.this, MessageActivity.class);
                        i.putExtra("sellerId", currentPost.seller);
                        startActivity(i);
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
