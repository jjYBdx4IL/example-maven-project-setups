package com.github.jjYBdx4IL.maven.examples.jnajni;

import org.junit.Ignore;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 *
 * @author jjYBdx4IL
 */
public class JNIAccessWithLibLoaderPerformanceTest extends PerformanceTestBase {

    // not working yet, open issue at https://github.com/scijava/native-lib-loader/issues/15
    @Ignore
    @Override
    public void testRunner() {}

    //@Benchmark
    public void benchmarkJNIArray(BenchmarkState state, Blackhole bh) {

        for (int i = 0; i < state.list.length; i++) {
            state.intArray[0] = state.list[i];
            bh.consume(JNIAccessWithLibLoader.sum(state.intArray));
        }
    }

    //@Benchmark
    public void benchmarkJNIIntArg(BenchmarkState state, Blackhole bh) {

        for (int i = 0; i < state.list.length; i++) {
            bh.consume(JNIAccessWithLibLoader.inc(state.list[i]));
        }
    }
}
