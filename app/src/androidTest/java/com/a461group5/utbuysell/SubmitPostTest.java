package com.a461group5.utbuysell;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.a461group5.utbuysell.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SubmitPostTest {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private static final String title = "test item";
    private static final String description = "this is for testing";
    private static final String[] categories = {"electronics", "shoes", "clothes"};
    private static final int price = 20;
    private Post post;
    boolean readDone;

    @Before
    public void logIn() {
        mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> result = mAuth.signInWithEmailAndPassword("test@utexas.edu", "testing123"); //dummy account
        while(!result.isComplete()); //wait till done
        user = FirebaseAuth.getInstance().getCurrentUser();
        assertNotNull(user);
        assertEquals("test@utexas.edu", user.getEmail());
    }


    @Test
    public void sendPost() {
        readDone = false;
        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // Save Post
        //String key = mDatabase.child("posts").push().getKey();
        post = new Post(title, description, user.getUid(), price, categories);
        //mDatabase = FirebaseDatabase.getInstance().getReference("posts/" + key);

        readDone = true;
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post readPost = dataSnapshot.getValue(Post.class);
                assertTrue(post.equals(readPost));
                readDone = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...

                fail("Database error");
            }
        };
        //mDatabase.addListenerForSingleValueEvent(postListener);
        while(!readDone);
        assertTrue(post.title.equals(title));
    }
}
