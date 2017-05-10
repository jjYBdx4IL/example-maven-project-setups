package com.github.jjYBdx4IL.maven.examples.gwt.server.chat;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleJettyConfigTest extends WebSocketChatTestBase {

    @Override
    protected void configureServer() {
        server.setHandler(chatHandler);
    }

}
