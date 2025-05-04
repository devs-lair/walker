package devs.lair.walkers;


import java.io.File;
import java.util.*;

public class IterationOnQueueWalk {

    public List<File> collect(File root) {
        List<File> result = new ArrayList<>();
        Queue<File> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            File file = queue.remove();
            if (file.isDirectory()) {
                queue.addAll(Arrays.stream(file.listFiles()).toList());
            } else {
                if (file.getName().endsWith(".xml")) {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
