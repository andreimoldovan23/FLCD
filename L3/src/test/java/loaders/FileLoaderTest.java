package loaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.rules.TemporaryFolder;

public class FileLoaderTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private static final String filename = "test.txt";
    private static final String line1 = "Salut";
    private static final String line2 = "Ce faci?";
    private static final String line3 = "Bine, tu?";

    @Test
    public void testFileLoader() throws IOException {
        Path tempFile = tempDir.newFile(filename).toPath();

        FileLoader.writeToFile(tempFile.toAbsolutePath().toString(), line1 + "\n" + line2 + "\n" + line3);

        List<String> lines = FileLoader.getLinesFromFile(tempFile.toAbsolutePath().toString());

        Assertions.assertAll(
                () -> Assert.assertNotNull(lines),
                () -> Assert.assertEquals(lines.size(), 3),
                () -> Assert.assertEquals(lines.get(0), line1),
                () -> Assert.assertEquals(lines.get(1), line2),
                () -> Assert.assertEquals(lines.get(2), line3)
        );
    }

}
