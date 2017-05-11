package com.github.jjYBdx4IL.maven.examples.gwt.server.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.ChatMessage;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

/**
 *
 * @author jjYBdx4IL
 */
public class GWTSerializationUtils {

    public static ChatMessage deserializeMessage(String data) throws SerializationException {
        ServerSerializationStreamReader streamReader = new ServerSerializationStreamReader(
                Thread.currentThread().getContextClassLoader(), new CustomSerializationPolicyProvider());
        // Filling stream reader with data
        streamReader.prepareToRead(data);
        // Reading deserialized object from the stream
        final ChatMessage message = (ChatMessage) streamReader.readObject();
        return message;
    }

    public static String serializeMessage(final ChatMessage messageDto) throws SerializationException {
        ServerSerializationStreamWriter serverSerializationStreamWriter
                = new ServerSerializationStreamWriter(new SimpleSerializationPolicy());
        serverSerializationStreamWriter.writeObject(messageDto);
        String result = serverSerializationStreamWriter.toString();
        return result;
    }

    private GWTSerializationUtils() {
    }
}
