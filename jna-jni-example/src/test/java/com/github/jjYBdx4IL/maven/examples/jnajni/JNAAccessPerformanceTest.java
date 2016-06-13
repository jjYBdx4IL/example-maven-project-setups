package com.github.jjYBdx4IL.maven.examples.jnajni;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/**
 *
 * @author jjYBdx4IL
 */
public class JNAAccessPerformanceTest extends PerformanceTestBase {

    @Benchmark
    public void benchmarkJNAArray(BenchmarkState state, Blackhole bh) {

        for (int i = 0; i < state.list.length; i++) {
            state.intArray[0] = state.list[i];
            bh.consume(JNAAccess.sum(state.intArray, 4));
        }
    }
    
    @Benchmark
    public void benchmarkJNAIntArg(BenchmarkState state, Blackhole bh) {

        for (int i = 0; i < state.list.length; i++) {
            bh.consume(JNAAccess.inc(state.list[i]));
        }
    }
}
