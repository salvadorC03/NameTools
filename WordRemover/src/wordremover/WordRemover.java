package wordremover;

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
public class WordRemover {

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

    public void renameFiles(Path path, String keyword, boolean deepMode) {
        try {
            try (var list = Files.list(path);) {
                list.forEach(item -> {
                    var file = item.toFile();

                    if (file.getName().equals(APP_NAME)) {
                        return;
                    }

                    var newName = file.getName().replace(keyword, "");
                    var newFile = new File(file.getPath().replace(file.getName(), newName));

                    if (!file.renameTo(newFile)) {
                        err.println("Something went wrong renaming the file: " + file.getName());
                    } else if (deepMode && newFile.isDirectory()) {
                        renameFiles(newFile.toPath(), keyword, deepMode);
                    }
                });
            }

        } catch (IOException e) {
            err.println("Error reading file directory: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            err.println("Please provide a keyword to match");
            System.exit(1);
        }

        var deepMode = false;
        var path = Path.of("./");

        var keyword = args[0];

        if (args.length > 1 && args[1].equals("-deep")) {
            deepMode = true;
        }

        var wordRemover = new WordRemover();
        wordRemover.renameFiles(path, keyword, deepMode);

        out.println("Files renamed successfully");
    }

}
