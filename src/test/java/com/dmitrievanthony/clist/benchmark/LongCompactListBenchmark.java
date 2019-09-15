package com.dmitrievanthony.clist.benchmark;

import com.dmitrievanthony.clist.CompactList;
import com.dmitrievanthony.clist.CompactListFactory;
import com.dmitrievanthony.clist.ObjectCompactList;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * JMH benchmark.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3)
@Measurement(iterations = 5)
public class LongCompactListBenchmark {
    /** Main of the JMH benchmark. */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(LongCompactListBenchmark.class.getSimpleName())
            .forks(1)
            .build();

        new Runner(opt).run();
    }

    /** Benchmark of the default implementation based on object array. */
    @Benchmark
    public void benchmarkObjectCompactList(Blackhole bh) {
        CompactList<Long> list = new ObjectCompactList<>();

        // Write values.
        for (long i = 0; i < 10_000; i++) {
            list.add(i);
            bh.consume(i);
        }

        // Read values.
        for (int i = 0; i < 10_000; i++) {
            long val = list.get(i);
            bh.consume(val);
        }
    }

    /** Benchmak of the optimized implementation based on primitive array. */
    @Benchmark
    public void benchmarkLongCompactList(Blackhole bh) {
        CompactList<Long> list = new CompactListFactory().newCompactList(Long.class);

        // Write values.
        for (long i = 0; i < 10_000; i++) {
            list.add(i);
            bh.consume(i);
        }

        // Read values.
        for (int i = 0; i < 10_000; i++) {
            long val = list.get(i);
            bh.consume(val);
        }
    }
}
