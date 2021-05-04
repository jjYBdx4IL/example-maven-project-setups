package com.github.jjYBdx4IL.maven.examples.jnajni;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 *
 * @author jjYBdx4IL
 */
public class PerformanceTestBase {

    @Test
    public void testRunner() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.milliseconds(100))
                .warmupIterations(2)
                .measurementTime(TimeValue.milliseconds(100))
                .measurementIterations(10)
                .threads(2)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        Runner r = new Runner(opt);
        Collection<RunResult> result = r.run();
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }

    @State(Scope.Thread)
    public static class BenchmarkState {

        int[] list = new int[1000];
        int[] intArray = new int[]{0, 1, 2, 3};

        @Setup(Level.Trial)
        public void initialize() {

            Random rand = new Random();

            for (int i = 0; i < list.length; i++) {
                list[i] = rand.nextInt();
            }
        }
    }

}
