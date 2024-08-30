package copyFile;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.lang.System.out;
import static java.lang.System.err;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author salvador
 */
public class CopyLastElement {

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

    public void copyFiles(Path path, int times, boolean ignoreRoot, String name) {
        try {
            try (var list = Files.list(path).filter(item -> {
                var file = item.toFile();
                return !file.getName().equals(APP_NAME) && !file.getName().equals(".DS_Store");
            }).sorted();) {
                if (ignoreRoot) {
                    list.forEach(item -> {
                        if (item.toFile().isDirectory()) {
                            copyFiles(item, times, false, name);
                        }
                    });
                    return;
                }

                var items = list.toArray(Path[]::new);
                var item = items[0];

                for (int i = 0; i < times; i++) {
                    var file = item.toFile();

                    var extension = Files.probeContentType(item).split("/")[1];

                    var newName = name == null ? file.getName().split("\\.")[0] + " " + i + "." + extension : name + " " + i + "." + extension;
                    var newPath = Paths.get(file.getPath().replace(file.getName(), newName));

                    Files.copy(item, newPath, StandardCopyOption.REPLACE_EXISTING);

                    if (!newPath.toFile().exists()) {
                        err.println("Something went wrong copying the file: " + newName);
                    }
                }

            }

        } catch (IOException e) {
            err.println("Error reading file directory: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        var ignoreRoot = false;
        var path = Path.of("./");

        if (args.length == 0) {
            err.println("Please input a limit number first.");
            System.exit(1);
        }

        int times = 0;
        String name = null;

        try {
            times = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            err.println("Please input a valid times number.");
            System.exit(1);
        }

        if (args.length > 1 && args[1].equals("-ignoreRoot")) {
            ignoreRoot = true;
        }

        if (args.length > 2) {
            name = args[2];
        }

        var copyFirstElement = new CopyLastElement();
        copyFirstElement.copyFiles(path, times, ignoreRoot, name);

        out.println("Files copied successfully");
    }

}
