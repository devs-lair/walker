package devs.lair.walkers;


import java.io.File;
import java.util.*;

public class IterationOnStackWalk {

    public List<File> collect(File root) {
        List<File> result = new ArrayList<>();
        Stack<File> queue = new Stack<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            File file = queue.pop();
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
