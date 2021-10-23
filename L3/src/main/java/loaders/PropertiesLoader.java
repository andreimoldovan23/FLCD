package loaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
            inputStream.close();
            return configuration;
        } catch (IOException ioe) {
            log.error("Couldn't load properties file");
            throw new RuntimeException();
        }
    }

    public static String getSourceCodePath() {
        return (String) props.get("base.path") + props.get("sourceCode");
    }

    public static String getTokensPath() {
        return (String) props.get("base.path") + props.get("tokens");
    }

    public static String getPIFOutPath() {
        return (String) props.get("base.path") + props.get("PIFOut");
    }

    public static String getSTIDOutPath() {
        return (String) props.get("base.path") + props.get("STIDOut");
    }

    public static String getSTCTOutPath() {
        return (String) props.get("base.path") + props.get("STCTOut");
    }

    public static Integer getInitialSlots() {
        return Integer.valueOf((String) props.get("initialSlots"));
    }

    public static Double getThreshold() {
        return Double.valueOf((String) props.get("threshold"));
    }
}
