package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Init.COMMIT_OBJECT_DIR;
import static gitlet.Init.LOG_DIR;
import static gitlet.Utils.*;
import static gitlet.RepositoryUtils.*;

public class Log {

    public static String branch = getCurrentBranch();
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

    public static void readGlobalLog() {
        StringBuilder log = new StringBuilder();
        List<String> commits = plainFilenamesIn(COMMIT_OBJECT_DIR);
        for (String commitId : commits) {
            Commit c = Commit.fromFile(commitId);
            log.append("===\n" + "commit ").append(commitId).append("\n")
                    .append("Date: ").append(c.getTimestamp()).append("\n")
                    .append(c.getMessage()).append("\n\n");
        }
        System.out.println(log);
    }
}
