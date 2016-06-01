package smtp.tester;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by oom on 12.04.2016.
 */
public class PropertyUtils {
    private final Properties properties = new Properties();

    public PropertyUtils() {
        FileInputStream file = null;
        try {
            String path = "./smtp.properties";
            file = new FileInputStream(path);
            properties.load(file);
        } catch (IOException e) {
            System.out.println("Can't load property file" + e);
        } finally {
            if(file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    System.out.println("Can't close smtp.properties");
                }
            }
        }
    }

    public String getValue(String name) {
        return (String) properties.get(name);
    }

    public boolean getBoolean(String name) {
        return Boolean.valueOf(getValue(name));
    }

    public int getInteger(String name) {
        return Integer.valueOf(getValue(name));
    }
}
