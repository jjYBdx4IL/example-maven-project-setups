package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.chat;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 *
 * @author jjYBdx4IL
 */
@Portable
public class PojoMessage {

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
    
    private String text;
    
}
