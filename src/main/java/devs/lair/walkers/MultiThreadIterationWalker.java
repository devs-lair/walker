package devs.lair.walkers;


import java.io.File;
import java.util.*;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


public class MultiThreadIterationWalker {
    private final static int DIRS_LIMIT = 50;
    private final List<File> root;

    public MultiThreadIterationWalker(List<File> root) {
        this.root = root;
    }

    public List<File> collect() {
        MultiThreadIterationTask multiThreadIterationTask = new MultiThreadIterationTask(root);
        return multiThreadIterationTask.invoke();
    }

    private static  class MultiThreadIterationTask extends RecursiveTask<List<File>> {
        private final List<File> root;

        public MultiThreadIterationTask(List<File> root) {
            this.root = root;
        }

        @Override
        public List<File> compute() {
            List<File> result = new ArrayList<>();
            Queue<File> queue = new ArrayDeque<>(root);

            while (!queue.isEmpty()) {
                File file = queue.remove();
                if (file.isDirectory()) {
                    queue.addAll(Arrays.stream(file.listFiles()).toList());
                    if (queue.size() > DIRS_LIMIT) {
                        result.addAll(ForkJoinTask.invokeAll(createSubtasks(queue.stream()
                                        .toList())).stream().map(ForkJoinTask::join)
                                .flatMap(Collection::stream).toList());
                        return result;
                    }
                } else {
                    if (file.getName().endsWith(".xml")) {
                        result.add(file);
                    }
                }
            }
            return result;
        }

        private Collection<MultiThreadIterationTask> createSubtasks(List<File> dirs) {
            List<MultiThreadIterationTask> dividedTasks = new ArrayList<>();
            dividedTasks.add(new MultiThreadIterationTask(dirs.subList(0, dirs.size() / 2)));
            dividedTasks.add(new MultiThreadIterationTask(dirs.subList(dirs.size() / 2, dirs.size())));
            return dividedTasks;
        }


    }
}
