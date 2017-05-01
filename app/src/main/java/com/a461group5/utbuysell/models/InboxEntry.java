package com.a461group5.utbuysell.models;

/**
 * Created by irfanhasan on 4/26/17.
 * Represents the data that an inbox entry will take care off
 */

public class InboxEntry {
    private String senderName;
    private String chatId;


    public InboxEntry(String senderName, String chatId) {
        this.senderName = senderName;
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
    /**
     * currently we are using an ArrayAdapter to list all inbox entries, and
     * ArrayAdapter fills a TextView with toString of the objects its holding.
     */
    @Override
    public String toString() {
        return senderName;
    }


}
