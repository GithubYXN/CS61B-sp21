package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import static gitlet.Init.*;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author yangx
 */
public class Commit implements Serializable {

    /**
     *  A Commit consists of log message, parent, second parent(for merge),
     *  timestamp and a mapping of file names to blobs.
     */

    private String message;
    private String parent;
    private String secondParent;
    private String timestamp;
    private TreeMap<String, String> blobsMap;

    private final String format = "%1$ta %1$tb %1$td %1$tH:%1$tM:%1$tS %1$tY %1$tz";
    private static final int FULL_COMMIT_LEN = 40;

    public Commit(String message, String parent,
                  String secondParent, TreeMap<String, String> blobsMap) {
        this.message = message;
        this.parent = parent;
        this.secondParent = secondParent;
        this.blobsMap = blobsMap;

        Date date;
        if (parent == null) {
            date = new Date(0);
        } else {
            date = new Date();

        }
        this.timestamp = String.format(Locale.US, format, date);
    }

    // Deserialize a commit using a commitID;
    public static Commit fromFile(String commitId) {
        int len = commitId.length();
        if (len != FULL_COMMIT_LEN) {
            List<String> commits = plainFilenamesIn(COMMIT_OBJECT_DIR);
            for (String commit : commits) {
                if (commit.substring(0, len).equals(commitId)) {
                    File commitFile = join(COMMIT_OBJECT_DIR, commit);
                    return readObject(commitFile, Commit.class);
                }
            }
            // Not found.
            return null;
        } else {
            File commitFile = join(COMMIT_OBJECT_DIR, commitId);
            if (!commitFile.exists()) {
                return null;
            }
            return readObject(commitFile, Commit.class);
        }
    }

    // Getter and Setter function.
    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getParent() {
        return parent;
    }

    public String getSecondParent() {
        return secondParent;
    }

    public TreeMap<String, String> getBlobsMap() {
        return blobsMap;
    }

    public void setBlobsMap(TreeMap<String, String> blobsMap) {
        this.blobsMap = blobsMap;
    }
}
