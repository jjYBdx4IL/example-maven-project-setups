package com.github.jjYBdx4IL.maven.examples.server.chat;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

/**
 *
 * @author jjYBdx4IL
 */
public class GWTSerializationUtils {

    public static Object deserializeMessage(String data) throws SerializationException {
        ServerSerializationStreamReader streamReader = new ServerSerializationStreamReader(
                Thread.currentThread().getContextClassLoader(), new CustomSerializationPolicyProvider());
        // Filling stream reader with data
        streamReader.prepareToRead(data);
        // Reading deserialized object from the stream
        return streamReader.readObject();
    }

    public static String serializeMessage(final Object obj) throws SerializationException {
        ServerSerializationStreamWriter serverSerializationStreamWriter
                = new ServerSerializationStreamWriter(new SimpleSerializationPolicy());
        serverSerializationStreamWriter.writeObject(obj);
        String result = serverSerializationStreamWriter.toString();
        return result;
    }

    private GWTSerializationUtils() {
    }
}
