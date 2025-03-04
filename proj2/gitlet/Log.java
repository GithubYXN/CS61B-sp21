package gitlet;

import java.io.File;
import java.util.List;

import static gitlet.Init.COMMIT_OBJECT_DIR;
import static gitlet.Init.LOG_DIR;
import static gitlet.Utils.*;
import static gitlet.RepositoryUtils.*;

public class Log {

    private static final String BRANCH = getCurrentBranch();
    private static final File LOGFILE = join(LOG_DIR, BRANCH);

    public static void writeLog(Commit commit, String sh1id) {
        String log = "===\n"
                + "commit " + sh1id + "\n"
                + "Date: " + commit.getTimestamp() + "\n"
                + commit.getMessage() + "\n\n"
                + readContentsAsString(LOGFILE);

        writeContents(LOGFILE, log);
    }

    public static String readLog() {
        return readContentsAsString(LOGFILE);
    }

    public static String readGlobalLog() {
        StringBuilder log = new StringBuilder();
        List<String> commits = plainFilenamesIn(COMMIT_OBJECT_DIR);
        for (String commitId : commits) {
            Commit c = Commit.fromFile(commitId);
            log.append("===\n" + "commit ").append(commitId).append("\n")
                    .append("Date: ").append(c.getTimestamp()).append("\n")
                    .append(c.getMessage()).append("\n\n");
        }
        return log.toString();
    }
}
