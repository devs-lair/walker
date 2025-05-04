package devs.lair.walkers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class HybridWalker {
    private final File[] root;
    private final static int DIRS_LIMIT = 50;

    public HybridWalker(File[] root) {
        this.root = root;
    }

    public static class HybridTask extends RecursiveTask<List<File>> {
        private final File[] root;

        public HybridTask(File[] root) {
            this.root = root;
        }

        @Override
        protected List<File> compute() {
            return computeRecursively(root, new ArrayList<>());
        }

        private List<File> computeRecursively(File[] rootDirs, List<File> results) {
            List<File> dirs = new ArrayList<>();

            for (File dir : rootDirs) {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    if (file.isDirectory()) {
                        dirs.add(file);
                    } else if (file.getName().endsWith(".xml")) {
                        results.add(dir);
                    }

                }
            }

            if (dirs.size() >= DIRS_LIMIT) {
                results.addAll(ForkJoinTask.invokeAll(createSubtasks(dirs)).stream().map(ForkJoinTask::join)
                        .flatMap(List::stream).toList());
                return results;
            } else {
                if (!dirs.isEmpty()) {
                    return computeRecursively(dirs.toArray(new File[0]), results);
                }
            }
            return results;
        }

        private Collection<HybridTask> createSubtasks(List<File> dirs) {
            List<HybridTask> dividedTasks = new ArrayList<>();
            List<File> left = dirs.subList(0, dirs.size() / 2);
            List<File> right = dirs.subList(dirs.size() / 2, dirs.size());
            dividedTasks.add(new HybridTask(left.toArray(new File[0])));
            dividedTasks.add(new HybridTask(right.toArray(new File[0])));
            return dividedTasks;
        }
    }

    public List<File> collect() {
        HybridTask cleverWalkerTask = new HybridTask(root);
        return cleverWalkerTask.invoke();
    }
}
