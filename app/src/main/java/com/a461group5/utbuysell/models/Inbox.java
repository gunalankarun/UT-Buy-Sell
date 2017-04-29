package com.a461group5.utbuysell.models;

/**
 * Created by shammakabir on 4/23/17.
 */

public class Inbox {

    public String name;
    public String last_message;
    public String path;

    public Inbox() {

    }
    public Inbox(String name, String last_message, String path) {
        this.name = name;
        this.last_message = last_message;
        this.path = path;
    }


}
