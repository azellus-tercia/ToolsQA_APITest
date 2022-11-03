package runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class BaseTest extends BaseRunner {
    public static String getJSONObject(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("src/test/resources/requests/" + fileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
