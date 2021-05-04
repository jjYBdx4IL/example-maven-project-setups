package com.github.jjYBdx4IL.maven.examples.jnajni;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scijava.nativelib.NativeLoader;

/**
 * Access some native function using conventional JNI access.
 *
 * @author jjYBdx4IL
 */
public class JNIAccessWithLibLoader {

    static {
//        try {
//            NativeLoader.loadLibrary("jniaccesswithlibloader");
//        } catch (IOException ex) {
//            System.out.println("native loader failed, trying manual load");
//            try {
//                //throw new RuntimeException(ex);
//                File f = new File(JNIAccessWithLibLoader.class.getResource("/META-INF/lib/linux_64/libjniaccesswithlibloader.so").toURI());
//                System.load(f.getPath());
//            } catch (URISyntaxException ex1) {
//                throw new RuntimeException(ex1);
//            }
//        }
    }

    public static native int sum(int[] intArray);

    public static native int inc(int arg);
}
