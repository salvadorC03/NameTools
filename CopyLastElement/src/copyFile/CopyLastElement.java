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

    public void copyFiles(Path path, int times, boolean ignoreRoot) {
        try {
            try (var list = Files.list(path).sorted();) {
                if (ignoreRoot) {
                    list.forEach(item -> {
                        if (item.toFile().isDirectory()) {
                            copyFiles(item, times, false);
                        }
                    });
                    return;
                }

                var items = list.toArray(Path[]::new);
                var item = items[items.length - 1];

                for (int i = 0; i < times; i++) {
                    var extension = Files.probeContentType(item).split("/")[1];
                    var file = item.toFile();

                    if (file.getName().equals(APP_NAME) || file.getName().equals(".DS_Store")) {
                        break;
                    }

                    var newName = file.getName().split("\\.")[0] + " " + i + "." + extension;
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

        try {
            times = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            err.println("Please input a valid times number.");
            System.exit(1);
        }

        if (args.length > 1 && args[1].equals("-ignoreRoot")) {
            ignoreRoot = true;
        }

        var copyLastElement = new CopyLastElement();
        copyLastElement.copyFiles(path, times, ignoreRoot);

        out.println("Files copied successfully");
    }

}
