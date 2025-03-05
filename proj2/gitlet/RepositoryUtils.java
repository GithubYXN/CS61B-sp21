package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Init.*;
import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_DIR;
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
            System.out.println("No commit with that id exists.");
            return "no commit";
        } else {
            TreeMap<String, String> blobsMap = designatedCommit.getBlobsMap();
            return blobsMap.get(filename);
        }
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
        List<String> untrackedFiles = getUntrackedInWorking();
        List<String> modifiedNotStagedFiles = getModifiedNotStaged();

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
        for (String modifiedNotStagedFile : modifiedNotStagedFiles) {
            System.out.println(modifiedNotStagedFile);
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String untrackedFile : untrackedFiles) {
            System.out.println(untrackedFile);
        }
        System.out.println();
    }

    // Get the files that tracked in a but are not present in b which will be deleted.
    public static List<String> getToDelete(TreeMap<String, String> current,
                                                        TreeMap<String, String> checkout) {
        List<String> toDelete = new ArrayList<>();
        for (String file : current.keySet()) {
            if (!checkout.containsKey(file)) {
                toDelete.add(file);
            }
        }
        return toDelete;
    }

    // Get the files that untracked in a and are present in b.
    public static List<String> getUntracked(TreeMap<String, String> current,
                                                          TreeMap<String, String> checkout) {
        List<String> workingFiles = plainFilenamesIn(CWD);
        List<String> untracked = new ArrayList<>();
        for (String file : workingFiles) {
            if (!current.containsKey(file) && checkout.containsKey(file)) {
                untracked.add(file);
            }
        }
        return untracked;
    }

    // Get the files that untracked in current working directory.
    public static List<String> getUntrackedInWorking() {
        List<String> workingFiles = plainFilenamesIn(CWD);
        List<String> toAdd = plainFilenamesIn(ADD_DIR);
        List<String> untracked = new ArrayList<>();
        TreeMap<String, String> currentTracked = getTreeMap(getHead());
        for (String filename : workingFiles) {
            if (!toAdd.contains(filename) && !currentTracked.containsKey(filename)) {
                untracked.add(filename);
            }
        }

        return untracked;
    }
    // Get the files that modified but not staged.
    public static List<String> getModifiedNotStaged() {
        List<String> modifiedNotStaged = new ArrayList<>();
        TreeMap<String, String> currentTracked = getTreeMap(getHead());
        for (String filename : currentTracked.keySet()) {
            File f = join(CWD, filename);
            File addF = join(ADD_DIR, filename);
            File removeF = join(REMOVE_DIR, filename);
            if ((!f.exists() && !removeF.exists())
                    || (addF.exists() && !f.exists())) {
                modifiedNotStaged.add(filename + " (deleted)");
            }
            if (f.exists()) {
                Blob b = new Blob(f);
                if ((!addF.exists() && !b.getSha1id().equals(currentTracked.get(filename)))
                        || (addF.exists() && !b.getSha1id().equals(currentTracked.get(filename)))) {
                    modifiedNotStaged.add(filename + " (modified)");
                }
            }
        }
        return modifiedNotStaged;
    }

    // Check if the repository had been initialized.
    public static boolean repositoryExsists() {
        return GITLET_DIR.exists();
    }

    // clear the staging area.
    public static void clearStagingArea() {
        List<String> addStagedFile = plainFilenamesIn(ADD_DIR);
        List<String> removeStagedFile = plainFilenamesIn(REMOVE_DIR);
        if (addStagedFile != null) {
            for (String file : addStagedFile) {
                File f = join(ADD_DIR, file);
                f.delete();
            }
        }
        if (removeStagedFile != null) {
            for (String file : removeStagedFile) {
                File f = join(REMOVE_DIR, file);
                f.delete();
            }
        }
    }

}
