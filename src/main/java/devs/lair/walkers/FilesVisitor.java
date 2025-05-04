package devs.lair.walkers;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FilesVisitor {

    public List<Path> collect(Path path) throws IOException {
        List<Path> result = new ArrayList<>();
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (Files.isRegularFile(file) && file.endsWith(".xml")) {
                    result.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }
}
