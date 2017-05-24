package com.github.jjYBdx4IL.maven.examples.aspectj;

import java.io.IOException;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write -javaagent to ${project.basedir}/.mvn/jvm.config so we can use Maven with AspectJ weaving more seamlessly.
 *
 * @author jjYBdx4IL
 */
public class MavenConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(MavenConfigurator.class);
    
    public static void main(String[] args) {
        try {
            new MavenConfigurator().run(args);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void run(String[] args) throws IOException {
        try (PrintWriter pw = new PrintWriter(args[0])) {
            pw.append("-javaagent:" + args[1] + " ");
            pw.append("-Daj.weaving.verbose=true" + System.lineSeparator());
        }
    }
}
