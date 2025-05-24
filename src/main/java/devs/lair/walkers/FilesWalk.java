package devs.lair.walkers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class FilesWalk {

    public List<Path> collect(String dir, int depth) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
            return stream.filter(file -> Files.isRegularFile(file)
                            && file.toString().endsWith(".xml")).toList();
        }
    }
}
