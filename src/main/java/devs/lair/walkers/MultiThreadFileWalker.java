package devs.lair.walkers;

import java.io.File;
import java.util.concurrent.RecursiveTask;

public class MultiThreadFileWalker {
    private final File root;

    public MultiThreadFileWalker(File root) {
        if (root == null) {
            throw new IllegalArgumentException("Root cannot be null");
        }

        if (!root.exists()) {
            throw new IllegalArgumentException("Root do not exists");
        }

        this.root = root;
    }

    public File[] collect() {
        WalkTask task = new WalkTask(root);
        return task.invoke();
    }

    public static class WalkTask extends RecursiveTask<File[]> {
        private final File root;

        public WalkTask(File root) {
            this.root = root;
        }

        @Override
        protected File[] compute() {

            File[] rootDir = root.listFiles();
            WalkTask[] tasks = new WalkTask[rootDir.length];
            File[] files = new File[rootDir.length];
            int rIndex = 0;

            for (int i = 0; i < rootDir.length; i++) {
                File file = rootDir[i];
                if (file.isDirectory()) {
                    WalkTask walkTask = new WalkTask(file);
                    walkTask.fork();
                    tasks[i] = walkTask;
                }

                if (file.isFile() && file.getName().endsWith(".xml")) {
                    files[rIndex] = file;
                    rIndex += 1;
                }
            }

            File[] result = new File[rIndex];
            System.arraycopy(files, 0, result, 0, result.length);

            File[] common;

            for (WalkTask task : tasks) {
                if (task == null) continue;

                File[] forkResult = task.join();
                if (forkResult.length == 0) continue;

                common = new File[forkResult.length + result.length];
                System.arraycopy(result, 0, common, 0, result.length);
                System.arraycopy(forkResult, 0, common, result.length, forkResult.length);
                result = common;
            }

            return result;
        }
    }
}