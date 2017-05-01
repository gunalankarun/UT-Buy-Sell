package com.a461group5.utbuysell;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Created by irfanhasan on 4/27/17.
 */

public class LogInTest {
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Test
    public void logIn() {
        mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> result = mAuth.signInWithEmailAndPassword("test@utexas.edu", "testing123"); //dummy account
        while(!result.isComplete()); //wait till done
        user = FirebaseAuth.getInstance().getCurrentUser();
        assertNotNull(user);
        assertEquals("test@utexas.edu", user.getEmail());
    }

}
