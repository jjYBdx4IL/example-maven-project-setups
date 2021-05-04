package com.github.jjYBdx4IL.maven.examples.jnajni;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

/**
 * Access native function through JNA.
 *
 * @author jjYBdx4IL
 */
public class JNAAccess {

    static {
        NativeLibrary lib = NativeLibrary.getInstance("jnaaccess");
        Native.register(lib);
    }

    public static native int sum(int[] intArr, int arrayLength);
    public static native int inc(int arg);
}
