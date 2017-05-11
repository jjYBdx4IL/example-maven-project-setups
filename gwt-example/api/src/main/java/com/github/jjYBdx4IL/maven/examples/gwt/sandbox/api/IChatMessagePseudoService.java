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

    ChatMessage bla(ChatMessage bla);
}
