package com.a461group5.utbuysell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A register screen that allows users to enter info to register an account.
 */
public class RegisterActivity extends AppCompatActivity  {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private View mProgressView;
    private View mLoginFormView;
    private String FIRST_NAME = null;
    private String LAST_NAME = null;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);


        mPasswordView = (EditText) findViewById(R.id.register_password);
        mFirstNameView = (EditText) findViewById(R.id.register_first_name);
        mLastNameView = (EditText) findViewById(R.id.register_last_name);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.try_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User was registered and is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                    //Update user's display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(FIRST_NAME + " " + LAST_NAME)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });

                    //store user info into database
                    DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    User userDB = new User(user.getEmail(), FIRST_NAME, LAST_NAME);
                    mDatabase.child("users").child(user.getUid()).setValue(userDB);


                    
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                    }
                                }
                            });
                    Toast.makeText(RegisterActivity.this, "A verification email was sent. Please verify your email",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    RegisterActivity.this.startActivity(intent);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration is made.
     */
    private void attemptRegister() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check that a first and last name was entered
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError("Please enter your first name!");
            focusView = mFirstNameView;
            cancel = true;
        } else if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError("Please enter your last name!");
            focusView = mLastNameView;
            cancel = true;
        }
        FIRST_NAME = firstName.trim();
        LAST_NAME = lastName.trim();
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, R.string.auth_pass, Toast.LENGTH_SHORT).show();
                                //try to signin


                            }
                            // If registration fails, display a message to the user
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                }  catch(FirebaseAuthWeakPasswordException e) {
                                }  catch(FirebaseAuthInvalidCredentialsException e) {
                                }  catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                            Toast.LENGTH_LONG).show();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                FIRST_NAME = null;
                                LAST_NAME = null;
                            }
                        }
                    });

        }
    }

    private boolean isEmailValid(String email) {
        String domain = email.substring(email.indexOf('@') + 1);
        return domain.equals("utexas.edu");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }
}

