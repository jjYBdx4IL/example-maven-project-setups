package com.github.jjYBdx4IL.maven.examples.server.chat;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ConfigurableSerializationPolicy extends SimpleSerializationPolicy {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableSerializationPolicy.class);
    
    private final List<String> allowedPackages = new ArrayList<>();
    
    @Override
    public boolean shouldDeserializeFields(Class<?> clazz) {
        if (!isValidPackage(clazz)) {
            return false;
        }
        return super.shouldDeserializeFields(clazz);
    }

    @Override
    public boolean shouldSerializeFields(Class<?> clazz) {
        if (!isValidPackage(clazz)) {
            return false;
        }
        return super.shouldSerializeFields(clazz);
    }

    protected boolean isValidPackage(Class<?> clazz) {
        String className = clazz.getName();
        for (String allowedPackageName : allowedPackages) {
            if (allowedPackageName.isEmpty()) {
                return true;
            }
            if (className.startsWith(allowedPackageName + ".")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param packageName "a.b" will allow "a.b.ClassB" and "a.b.c.ClassC".
     * To allow every package, add an empty string as allowed package name.
     */
    public void addAllowedPackage(String packageName) {
        LOG.info("allowing package " + packageName + " for (de-)serialization");
        allowedPackages.add(packageName);
    }
    
}
