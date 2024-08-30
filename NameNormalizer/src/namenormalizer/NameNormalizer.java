package namenormalizer;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.lang.System.out;
import static java.lang.System.err;

/**
 *
 * @author salvador
 */
public class NameNormalizer {

    String APP_NAME = getAppName();

    public String getAppName() {
        var jarName = new File(
                getClass()
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getFile())
                .getName();
        return jarName;
    }

    public void renameFiles(Path path, boolean deepMode) {
        try {
            try (var list = Files.list(path);) {
                list.forEach(item -> {
                    var file = item.toFile();

                    if (file.getName().equals(APP_NAME) || file.getName().equals(".DS_Store")) {
                        return;
                    }

                    var newName = file.getName().toLowerCase().replace(" ", "-");
                    var newFile = new File(file.getPath().replace(file.getName(), newName));

                    if (!file.renameTo(newFile)) {
                        err.println("Something went wrong renaming the file: " + file.getName());
                    } else if (deepMode && newFile.isDirectory()) {
                        renameFiles(newFile.toPath(), deepMode);
                    }
                });
            }

        } catch (IOException e) {
            err.println("Error reading file directory: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        var deepMode = false;
        var path = Path.of("./");

        if (args.length > 0 && args[0].equals("-deep")) {
            deepMode = true;
        }

        var nameNormalizer = new NameNormalizer();
        nameNormalizer.renameFiles(path, deepMode);

        out.println("Files renamed successfully");
    }

}
