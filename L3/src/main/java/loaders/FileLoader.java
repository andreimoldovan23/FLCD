package loaders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLoader {
    private static final Logger log = LoggerFactory.getLogger("fileloader");

    public static List<String> getLinesFromFile(String path) {
        Path filePath = Paths.get(path);
        Charset utf8Charset = StandardCharsets.UTF_8;
        try {
            return Files.readAllLines(filePath, utf8Charset).stream()
                    .filter(str -> !str.isEmpty())
                    .map(String::trim)
                    .map(str -> {
                        if (str.equalsIgnoreCase("space"))
                            return " ";
                        return str;
                    })
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            log.error("Couldn't read from file: {}", path);
            return null;
        }
    }

    public static void writeToFile(String path, String text) {
        try {
            Path filePath = Paths.get(path);

            List<String> lines = Arrays.asList(text.split("\n"));
            Files.write(filePath, lines);
        } catch (IOException ioe) {
            log.error("Couldn't write to file: {}", path);
        }
    }
}
