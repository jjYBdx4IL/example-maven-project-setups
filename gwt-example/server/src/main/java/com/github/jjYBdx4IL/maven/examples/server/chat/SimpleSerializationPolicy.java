package com.github.jjYBdx4IL.maven.examples.server.chat;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleSerializationPolicy extends SerializationPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleSerializationPolicy.class);
    
    @Override
    public boolean shouldDeserializeFields(Class<?> clazz) {
        LOG.info("shouldDeserializeFields " + clazz);
        return isSerializable(clazz);
    }

    @Override
    public boolean shouldSerializeFields(Class<?> clazz) {
        LOG.info("shouldSerializeFields " + clazz);
        return isSerializable(clazz);
    }

    private boolean isSerializable(Class<?> clazz) {
        LOG.info("isSerializable " + clazz);
        if (clazz != null) {
            if (clazz.isPrimitive()
                    || Serializable.class.isAssignableFrom(clazz)
                    || IsSerializable.class.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void validateDeserialize(Class<?> clazz) throws SerializationException {
        LOG.info("validateDeserialize " + clazz);
    }

    @Override
    public void validateSerialize(Class<?> clazz) throws SerializationException {
        LOG.info("validateSerialize " + clazz);
    }
}
