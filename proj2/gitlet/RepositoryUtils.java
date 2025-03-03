package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Init.*;
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
    public static String getCurrentBranch() {
        File head = new File(readContentsAsString(HEAD));
        return head.getName();
    }

    // Get all branches.
    public static List<String> getAllBranches() {
        return plainFilenamesIn(HEAD_DIR);
    }

    // Get the tree map of commit with commit id.
    public static TreeMap<String, String> getTreeMap(String commitId) {
        Commit c = Commit.fromFile(commitId);
        return c.getBlobsMap();
    }

    // Get the designated version of file f.
    public static String getDesignatedVersion(String commitId, String filename) {
        Commit designatedCommit = Commit.fromFile(commitId);
        if (designatedCommit == null) {
            throw error("No commit with that id exists.");
        }
        TreeMap<String, String> blobsMap = designatedCommit.getBlobsMap();
        return blobsMap.get(filename);
    }

    // Get the current version of file f.
    public static String getCurrentVersion(String filename) {
        return getDesignatedVersion(getHead(), filename);
    }

    // Get staged files.
    public static List<String> getStagedFiles() {
        return plainFilenamesIn(ADD_DIR);
    }

    // Get removed files.
    public static List<String> getRemovedFiles() {
        return plainFilenamesIn(REMOVE_DIR);
    }

    // Display status.
    public static void display() {
        List<String> branches = getAllBranches();
        List<String> stagedFiles = getStagedFiles();
        List<String> removedFiles = getRemovedFiles();

        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals(getCurrentBranch())) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String stagedFile : stagedFiles) {
            System.out.println(stagedFile);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String removedFile : removedFiles) {
            System.out.println(removedFile);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    // Get the files that tracked in a but are not present in b which will be delete.
    public static List<String> getToDelete(TreeMap<String, String> a, TreeMap<String, String> b) {
        List<String> toDelete = new ArrayList<>();
        for (String file : a.keySet()) {
            if (!b.containsKey(file)) {
                toDelete.add(file);
            }
        }
        return toDelete;
    }

    // Get the files that untracked in a and are present in b.
    public static List<String> getUntracked(TreeMap<String, String> a, TreeMap<String, String> b) {
        return getToDelete(b, a);
    }

}
