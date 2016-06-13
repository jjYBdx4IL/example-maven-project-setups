package com.github.jjYBdx4IL.maven.examples.jnajni;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 *
 * @author jjYBdx4IL
 */
public class JNIAccessPerformanceTest extends PerformanceTestBase {

    @Benchmark
    public void benchmarkJNIArray(BenchmarkState state, Blackhole bh) {

        for (int i = 0; i < state.list.length; i++) {
            state.intArray[0] = state.list[i];
            bh.consume(JNIAccess.sum(state.intArray));
        }
    }

    @Benchmark
    public void benchmarkJNIIntArg(BenchmarkState state, Blackhole bh) {

        for (int i = 0; i < state.list.length; i++) {
            bh.consume(JNIAccess.inc(state.list[i]));
        }
    }
}
