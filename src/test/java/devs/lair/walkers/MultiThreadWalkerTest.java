package devs.lair.walkers;

import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1)
@Fork(value = 1, warmups = 1)
public class MultiThreadWalkerTest {

    @State(Scope.Benchmark)
    public static class BenchmarkPath {

        @Param({"/home/sigma.sbrf.ru@21946350/dev/notifly-task",
                "/home/sigma.sbrf.ru@21946350/dev/snuil",
                "/home/sigma.sbrf.ru@21946350/dev"})
        protected String path;

        public String getPath() {
            return path;
        }
    }

    @Benchmark
    public void fileVisitor(BenchmarkPath path) {
        FilesVisitor filesWalk = new FilesVisitor();
        try {
            filesWalk.collect(Paths.get(path.getPath()));
        } catch (IOException ignored) {}
    }

    @Benchmark
    public void filesWalk(BenchmarkPath path) {
        FilesWalk filesWalk = new FilesWalk();
        try {
            filesWalk.collect(path.getPath(), Integer.MAX_VALUE);
        } catch (IOException ignored) {
        }
    }

    @Benchmark
    public void recursionWalker(BenchmarkPath path) {
        RecursionWalker recursionWalker = new RecursionWalker();
        recursionWalker.collect(new File(path.getPath()), new ArrayList<>());
    }

    @Benchmark
    public void iterationOnQueueWalker(BenchmarkPath path) {
        IterationOnQueueWalk  it = new IterationOnQueueWalk();
        it.collect(new File(path.getPath()));
    }

    @Benchmark
    public void multiThreadWalker(BenchmarkPath path) {
        MultiThreadWalker<File> multiThreadWalker = new MultiThreadWalker<>(
                new File(path.getPath()),
                f -> f.getName().endsWith(".xml"),
                File::isDirectory,
                File::listFiles);
        multiThreadWalker.collect();
    }

    @Benchmark
    public void multiTreadIterationWalker(BenchmarkPath path) {
        MultiThreadIterationWalker multiThreadIterationWalker = new MultiThreadIterationWalker(
                List.of(new File(path.getPath())));
        multiThreadIterationWalker.collect();
    }


    @Benchmark
    public void hybridWalker(BenchmarkPath path) {
        HybridWalker hw = new HybridWalker(new File[]{ new File(path.getPath())});
        hw.collect();
    }

    @Benchmark
    public void noIsDirectoryWalker(BenchmarkPath path) {
        MultiThreadFileWalkerOptimize multiThreadWalker = new MultiThreadFileWalkerOptimize(new File(path.getPath()));
        multiThreadWalker.collect();
    }
}
