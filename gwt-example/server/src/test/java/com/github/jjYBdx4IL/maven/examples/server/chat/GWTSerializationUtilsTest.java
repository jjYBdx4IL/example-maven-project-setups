package com.github.jjYBdx4IL.maven.examples.server.chat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.ChatMessage;
import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.IChatMessagePseudoService;
import com.github.jjYBdx4IL.maven.examples.server.chat.GWTSerializationUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import org.junit.Ignore;

public class GWTSerializationUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(GWTSerializationUtilsTest.class);

    // TODO: needs to be run via GWTTestCase
    @Ignore
    @Test
    public void test() throws SerializationException {
        String serialized = GWTSerializationUtils.serializeMessage(new ChatMessage("room", "text"));
        LOG.info(">>" + serialized + "<<");
        // messages encoded by client and server have different encodings.... so we can't decode for testing
        ChatMessage result = deserializeMessage(serialized);
        LOG.info("result: " + result);
    }

    public ChatMessage deserializeMessage(String data) {
        try {
            SerializationStreamFactory factory = (SerializationStreamFactory)
                    GWT.create(IChatMessagePseudoService.class);
            final SerializationStreamReader streamReader = factory.createStreamReader(data);
            final ChatMessage message = (ChatMessage) streamReader.readObject();
            return message;
        } catch (final SerializationException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
}
