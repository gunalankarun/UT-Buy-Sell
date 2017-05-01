package com.a461group5.utbuysell;

import android.util.Log;

import com.a461group5.utbuysell.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by irfanhasan on 4/27/17.
 */

public class SubmitUserInfoTest {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private static final String email = "testing@utexas.edu";
    private static final String first_name = "test";
    private static final String last_name = "test";
    private User userModel;
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
    public void sendUser() {
        readDone = false;
        //DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        // Save Post
        //String key = mDatabase.child("posts").push().getKey();
        String token = "abCnbUFYma12AAmm901BbbsbalMKffe";
        userModel = new User(email, first_name,last_name,token);
        //mDatabase = FirebaseDatabase.getInstance().getReference("posts/" + key);

        readDone = true;
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User readUser = dataSnapshot.getValue(User.class);
                assertTrue(userModel.equals(readUser));
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
        assertTrue(userModel.getFirstName().equals(first_name));
    }
}
