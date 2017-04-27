package com.a461group5.utbuysell;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SubmitPostTest {
    private FirebaseAuth mAuth;
    
    @Test
    public void validateLogIn() {
        mAuth = FirebaseAuth.getInstance();
        Task<AuthResult> result = mAuth.signInWithEmailAndPassword("test@utexas.edu", "testing123"); //dummy account
        while(!result.isComplete()); //wait till done
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertNotNull(user);
        assertEquals("test@utexas.edu", user.getEmail());
    }
}
