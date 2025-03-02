package gitlet;

import java.io.File;
import java.util.TreeMap;

import static gitlet.Init.HEAD;
import static gitlet.Utils.*;

public class RepositoryUtils {
    // Get the HEAD point(sh1id).
    public static String getHead() {
        File currentHead = new File(readContentsAsString(HEAD));
        if (!currentHead.exists()) {
            throw error("Could not find HEAD file");
        }
        return readContentsAsString(currentHead);
    }

    // Get current branch.
    public static String getBranch() {
        File head = new File(readContentsAsString(HEAD));
        return head.getName();
    }

    // Check if the file is same as in current HEAD.
    public static String getCurrentVersion(File f) {
        String currentCommitId = getHead();
        Commit current = Commit.fromFile(currentCommitId);
        TreeMap<String, String> blobsMap = current.getBlobsMap();
        return blobsMap.get(f.getName());
    }
}
