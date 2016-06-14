# jna-jni-example
## STATUS

native-lib-loader is not working as expected (not loading libs from os/arch dependent paths),
open issue at https://github.com/scijava/native-lib-loader/issues/15.

JNI and JNA ok, only JNA loads the native libs from jars seamlessly.

## Benchmarks

<pre>
Benchmark                                                                    Mode  Cnt    Score    Error  Units
jjYBdx4IL.maven.examples.jnajni.JNAAccessPerformanceTest.benchmarkJNAArray   avgt   10  414,315 ± 21,764  us/op  - func sig: int sum(int[])
jjYBdx4IL.maven.examples.jnajni.JNAAccessPerformanceTest.benchmarkJNAIntArg  avgt   10  177,994 ±  7,442  us/op  - func sig: int inc(int)
jjYBdx4IL.maven.examples.jnajni.JNIAccessPerformanceTest.benchmarkJNIArray   avgt   10  132,665 ± 4,839  us/op   - func sig: int sum(int[])
jjYBdx4IL.maven.examples.jnajni.JNIAccessPerformanceTest.benchmarkJNIIntArg  avgt   10   12,031 ± 0,453  us/op   - func sig: int inc(int)
</pre>

In the simple case of an increment function implemented in C and called from Java, the JNI way is about 15 times faster
than using JNA.

The not as simple case of calling a C function to return the sum of an int array of length 4, shows a smaller difference:
JNI is faster than JNA by a factor of 3. This is most likely due to how JNI access the int[] at the native level, which
includes releasing pointers and stuff. There might be faster ways to do this with JNI.


