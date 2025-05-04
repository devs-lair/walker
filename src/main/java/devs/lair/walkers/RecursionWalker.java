package devs.lair.walkers;

import java.io.File;
import java.util.List;

public class RecursionWalker {

    public List<File> collect(File root, List<File> result) {
        if (root.isDirectory()) {
            for (File f : root.listFiles()) {
                collect(f, result);
            }
        } else {
            if (root.getName().endsWith(".xml")) {
                result.add(root);
            }
        }
        return result;
    }
}
