package com.a461group5.utbuysell.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by irfanhasan on 3/29/17.
 */

public class User {
    String email;
    String first_name;
    String last_name;
    String token;
    Map<String, Boolean>  seller_posts;
    Map<String, Boolean> buyer_posts;
    Map<String, String> chats;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String first_name, String last_name, String token) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.token = token;
        seller_posts = new HashMap<String, Boolean>();
        buyer_posts = new HashMap<String, Boolean>();
        chats = new HashMap<String, String>();
    }

    @Exclude
    public String getFirstName()
    {
        return first_name;
    }

    @Exclude
    public String getName() {
        return first_name + " " + last_name;
    }
}
