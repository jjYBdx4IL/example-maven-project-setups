package com.github.jjYBdx4IL.maven.examples.server.chat;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.IChatMessagePseudoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyProvider;

/**
 * https://www.slideshare.net/gwtcon/gwt20-websocket20and20data20serialization
 * 
 * @author jjYBdx4IL
 */
public class CustomSerializationPolicyProvider implements SerializationPolicyProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CustomSerializationPolicyProvider.class);
    
    @Override
    public SerializationPolicy getSerializationPolicy(String moduleBaseURL, String serializationPolicyStrongName) {
        LOG.info("getSerializationPolicy");
        ConfigurableSerializationPolicy policy = new ConfigurableSerializationPolicy();
        policy.addAllowedPackage(IChatMessagePseudoService.class.getPackage().getName());
        return policy;
    }

 }

