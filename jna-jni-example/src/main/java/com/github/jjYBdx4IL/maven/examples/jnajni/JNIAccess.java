package com.github.jjYBdx4IL.maven.examples.jnajni;

/**
 * Access some native function using conventional JNI access.
 *
 * @author jjYBdx4IL
 */
public class JNIAccess {

    static {
        System.loadLibrary("jniaccess");
    }

    public static native int sum(int[] intArray);
    public static native int inc(int arg);
}
