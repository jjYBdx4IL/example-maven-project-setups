package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api;

import java.io.Serializable;

/**
 *
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class ChatMessage implements Serializable {

    private String room;
    private String text;
    
    public ChatMessage() {}
    
    public ChatMessage(String room, String text) {
        this.room = room;
        this.text = text;
    }
    
    /**
     * @return the room
     */
    public String getRoom() {
        return room;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" + "room=" + room + ", text=" + text + '}';
    }

}
