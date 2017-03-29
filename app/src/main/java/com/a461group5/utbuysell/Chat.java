package com.a461group5.utbuysell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by irfanhasan on 3/29/17.
 */

public class Chat {
    private class Message {
        String body;
        String senderId;
        String receiverId;
    }

    private List<Message> messages;
    private Map<String, Boolean> users;

    public Chat() {

    }

    public Chat(String user1ID, String user2ID) {
        messages = new ArrayList<Message>();
        users = new HashMap<String, Boolean>();
        users.put(user1ID, true);
        users.put(user2ID, true);
    }


}
