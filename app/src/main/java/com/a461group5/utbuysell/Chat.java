package com.a461group5.utbuysell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by irfanhasan on 3/29/17.
 */

public class Chat {
   public static class Message  {
        String body;
        String senderId;
        String receiverId;
        Message() {

        }

        Message(String body, String senderId, String receiverId) {
            this.body = body;
            this.senderId = senderId;
            this.receiverId = receiverId;
        }

        public String getTextBody() {
            return body;
        }

    }

    List<Message> messages;
    Map<String, Boolean> users;

    public Chat() {

    }

    public Chat(String user1ID, String user2ID) {
        messages = new ArrayList<Message>();
        users = new HashMap<String, Boolean>();
        users.put(user1ID, true);
        users.put(user2ID, true);
    }

    public List<Message> getMessages() {return messages;}

    public void sendMessage(String message, String senderID) {
        String recID = "";
        for (String id : users.keySet()) {
            if (!id.equals(senderID)) {
                recID = id;
                break;
            }
        }
        if (messages == null) messages = new ArrayList<Message>();
        messages.add(new Message(message, senderID, recID));
    }

    public Chat.Message getLastMessage() {
        return messages.get(messages.size() - 1);
    }

}
