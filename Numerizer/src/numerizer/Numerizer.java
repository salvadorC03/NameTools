package numerizer;

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
public class Numerizer {

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

    public void renameFiles(Path path, int limit, boolean deepMode, boolean ignoreRoot) {
        try {
            try (var list = Files.list(path).sorted();) {
                int a = 1;
                int b = 1;

                for (var item : list.toArray(Path[]::new)) {
                    var file = item.toFile();
                    var name = file.getName();

                    if (file.getName().equals(APP_NAME) || file.getName().equals(".DS_Store")) {
                        continue;
                    }
                    
                    if (ignoreRoot) {
                        if (file.isDirectory()) {
                            renameFiles(file.toPath(), limit, deepMode, false);
                        }
                        continue;
                    }

                    var newName = a + "." + b + ".png";
                    var newFile = new File(file.getPath().replace(file.getName(), newName));

                    if (!file.renameTo(newFile)) {
                        err.println("Something went wrong renaming the file: " + file.getName());
                    } else if (deepMode && newFile.isDirectory()) {
                        renameFiles(newFile.toPath(), limit, deepMode, ignoreRoot);
                    }

                    if (b == limit) {
                        a++;
                        b = 1;
                    } else {
                        b++;
                    }
                }
            }

        } catch (IOException e) {
            err.println("Error reading file directory: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        var deepMode = false;
        var ignoreRoot = false;
        var path = Path.of("./");

        if (args.length == 0) {
            err.println("Please input a limit number first.");
            System.exit(1);
        }

        int limit = 0;

        try {
            limit = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            err.println("Please input a valid limit number.");
            System.exit(1);
        }

        if (args.length > 1) {
            if (args[1].equals("-deep")) {
                deepMode = true;
            } else if (args[1].equals("-ignoreRoot")) {
                ignoreRoot = true;
            }
        }

        if (args.length > 2) {
            if (args[2].equals("-deep")) {
                deepMode = true;
            } else if (args[2].equals("-ignoreRoot")) {
                ignoreRoot = true;
            }
        }

        var numerizer = new Numerizer();
        numerizer.renameFiles(path, limit, deepMode, ignoreRoot);

        out.println("Files renamed successfully");
    }

}
