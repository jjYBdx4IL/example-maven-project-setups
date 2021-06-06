package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * pseudo interface to make GWT create serialization stuff
 * 
 * @author jjYBdx4IL
 */
@RemoteServiceRelativePath("/bla")
public interface IChatMessagePseudoService extends RemoteService {

    // Types the client wants to receive must be listed as return types somewhere.
    // Types the client wants to send must be listed as call parameters.
    // The same applies vice versa to the server.
    ChatMessage publish(ChatMessage message);
    WebSocketPong ping(WebSocketPing ping);
}
