package devs.lair.walkers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MultiThreadFileWalkerOptimize
{
    private final File root;

    public MultiThreadFileWalkerOptimize(File root) {
        this.root = root;
    }

    public List<File> collect() {
        WalkTask task = new WalkTask(root);
        List<String> paths = task.invoke();

        return paths.stream().map(File::new).filter(f->!f.isDirectory()).toList();
    }

    public static class WalkTask extends RecursiveTask<List<String>> {
        private final File root;

        public WalkTask(File root) {
            this.root = root;
        }

        @Override
        protected List<String> compute() {
            List<WalkTask> tasks = new ArrayList<>();
            List<String> result = new ArrayList<>();
            String[] files = root.list();

            if (files == null) {
                return Collections.emptyList();
            }

            for (String file : files) {
                if (file.endsWith(".xml")) {
                    result.add(file);
                } else {
                    tasks.add(new WalkTask(new File(root, file)));
                }
            }

            result.addAll(ForkJoinTask.invokeAll(tasks)
                    .stream()
                    .map(ForkJoinTask::join).flatMap(Collection::stream).toList());

            return result;
        }
    }
}