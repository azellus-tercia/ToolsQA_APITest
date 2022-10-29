package runner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseProperties {

    private static final String ENV_APP_OPTIONS = "APP_OPTIONS";

    private static Properties properties;

    private static void initProperties() {
        if (properties == null) {
            properties = new Properties();
            if (isServerRun()) {
                for (String option : System.getenv(ENV_APP_OPTIONS).split(";")) {
                    String[] optionArr = option.split("=");
                    properties.setProperty(optionArr[0], optionArr[1]);
                }
            } else {
                try {
                    InputStream inputStream = BaseProperties.class.getClassLoader().getResourceAsStream("local.properties");
                    if (inputStream == null) {
                        System.out.println("ERROR: The \u001B[31mlocal.properties\u001B[0m file not found in src/test/resources/ directory.");
                        System.out.println("You need to create it from local.properties.TEMPLATE file.");
                        System.exit(1);
                    }
                    properties.load(inputStream);
                } catch (IOException ignore) {
                }
            }
        }
    }

    static boolean isServerRun() {
        return System.getenv("CI_RUN") != null;
    }

    static {
        initProperties();
    }

    static Properties getProperties() {
        return properties;
    }

    public static void logf(String str, Object... arr) {
        System.out.printf(str, arr);
        System.out.println();
    }
}
