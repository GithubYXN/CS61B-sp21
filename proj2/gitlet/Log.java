package gitlet;

import java.io.File;

import static gitlet.Init.LOG_DIR;
import static gitlet.Utils.*;
import static gitlet.RepositoryUtils.*;

public class Log {

    public static String branch = getBranch();
    public static File logFile = join(LOG_DIR, branch);

    public static void writeLog(Commit commit, String sh1id) {
        String log = "===\n" +
                "commit " + sh1id + "\n" +
                "Date: " + commit.getTimestamp() + "\n" +
                commit.getMessage() + "\n\n" +
                readContentsAsString(logFile);

        writeContents(logFile, log);
    }

    public static void readLog() {
        String log = readContentsAsString(logFile);
        System.out.print(log);
    }
}
