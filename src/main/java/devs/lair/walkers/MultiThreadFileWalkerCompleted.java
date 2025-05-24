package devs.lair.walkers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountedCompleter;

public class MultiThreadFileWalkerCompleted {
    private final File root;

    public MultiThreadFileWalkerCompleted(File root) {
        this.root = root;
    }

    public List<File> collect() {
        List<String> result = new WalkTask(null, root,
                Collections.synchronizedList(new ArrayList<>())).invoke();
        return result.stream().map(File::new)
                .filter(f->!f.isDirectory()).toList();
    }

    public static class WalkTask extends CountedCompleter<List<String>> {
        private final File root;
        private final List<String> results;

        public WalkTask(CountedCompleter<?> completer, File root, List<String> results) {
            super(completer);
            this.root = root;
            this.results = results;
        }

        @Override
        public List<String> getRawResult() {
            return results;
        }

        @Override
        public void compute() {
            String[] files = root.list();

            if (files != null) {
                for (String file : files) {
                    if (file.endsWith(".xml")) {
                        results.add(file);
                    } else {
                        addToPendingCount(1);
                        new WalkTask(this, new File(root, file), results).fork();
                    }
                }
            }

            tryComplete();
        }
    }


    public static void main(String[] args) {
        MultiThreadFileWalkerCompleted walker =
                new MultiThreadFileWalkerCompleted(new File("/home/devslair/prjs"));
        List<File> collect = walker.collect();
        System.out.println(collect.size());
    }
}