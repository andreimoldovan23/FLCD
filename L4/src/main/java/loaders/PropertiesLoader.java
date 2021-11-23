package loaders;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader {
    private static final Logger log = LoggerFactory.getLogger("properties");
    private static final Properties props = loadProperties("app.properties");

    private static Properties loadProperties(String resourceFileName) {
        try {
            Properties configuration = new Properties();
            InputStream inputStream = PropertiesLoader.class
                    .getClassLoader()
                    .getResourceAsStream(resourceFileName);
            configuration.load(inputStream);
            if (inputStream != null) inputStream.close();
            return configuration;
        } catch (IOException ioe) {
            log.error("Couldn't load properties file");
            throw new RuntimeException();
        }
    }

    public static List<String> loadFile() {
        String filename = props.getProperty("base.path") + props.getProperty("file");
        try {
            return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)
                    .stream()
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            log.error("Couldn't read from FA file");
            throw new RuntimeException();
        }
    }
}
