package gitlet;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import static gitlet.RepositoryUtils.*;
import static gitlet.Utils.*;
import static gitlet.Init.*;
import static gitlet.Log.*;

/**
 * Represents a gitlet repository.
 *
 *  @author yangfx
 */
public class Repository {
    // The current working directory.
    public static final File CWD = new File(System.getProperty("user.dir"));
    // The .gitlet directory.
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    // Initial the repository.
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println(
                    "A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        } else {
            GITLET_DIR.mkdirs();
            Init.setup();
        }
    }

    // Add file to staging area.
    public static void add(String filename) {
        File toAdd = join(CWD, filename);
        if (!toAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        File isDelete = join(REMOVE_DIR, filename);
        if (isDelete.exists()) {
            isDelete.delete();
        } else {
            Blob blob = new Blob(toAdd);
            blob.add(filename);
        }
    }

    // Make a commit.
    public static void commit(String message) {
        String parentId = getHead();
        Commit parentCommit = Commit.fromFile(parentId);
        TreeMap<String, String> blobsMap = parentCommit.getBlobsMap();
        Commit toCommit = new Commit(message, parentId, null, blobsMap);
        List<String> addStagingFiles = plainFilenamesIn(ADD_DIR);
        List<String> removeStagingFiles = plainFilenamesIn(REMOVE_DIR);
        if (addStagingFiles != null && !addStagingFiles.isEmpty()
                || (removeStagingFiles != null && !removeStagingFiles.isEmpty())) {
            if (addStagingFiles != null && !addStagingFiles.isEmpty()) {
                // Add tracking file to map and persistence, then delete it from staging area.
                for (String filename : addStagingFiles) {
                    //update map
                    File file = join(ADD_DIR, filename);
                    String fileHash = readContentsAsString(file);
                    blobsMap.put(filename, fileHash);
                    // persistence
                    Blob fileBlob = new Blob(join(CWD, filename));
                    fileBlob.save();
                    //delete
                    file.delete();
                }
            }
            if (removeStagingFiles != null && !removeStagingFiles.isEmpty()) {
                for (String filename : removeStagingFiles) {
                    File file = join(REMOVE_DIR, filename);
                    file.delete();
                }
            }
            toCommit.setBlobsMap(blobsMap);

            // Persistence commit.
            Blob commitBlob = new Blob(toCommit);
            commitBlob.save();

            // Update HEAD point.
            File head = join(HEAD_DIR, getCurrentBranch());
            writeContents(head, commitBlob.getSha1id());

            // Write log.
            writeLog(toCommit, commitBlob.getSha1id());
        } else {
            System.out.println("No changes added to the commit.");
        }
    }

    /**
     * Unstage the file if it's staged or stage it for removal if it's tracked in current commit
     * and delete it from working directory.
     */
    public static void remove(String filename) {
        File isAdd = join(ADD_DIR, filename);
        if (isAdd.exists()) {
            isAdd.delete();
        } else {
            File toStage = join(REMOVE_DIR, filename);
            File toRemove = join(CWD, filename);
            String currentVersion = getCurrentVersion(filename);
            if (currentVersion != null && toRemove.exists()) {
                writeContents(toStage, currentVersion);
                restrictedDelete(toRemove);
            } else {
                System.out.println("No reason to remove the file.");
            }
        }
    }

    // Print the logs.
    public static void log() {
        String log = readLog();
        System.out.println(log);
    }

    // Print the global-log.
    public static void globalLog() {
        String globalLog = readGlobalLog();
        System.out.println(globalLog);
    }

    // Find commit ids with the given message.
    public static void find(String message) {
        List<String> commits = plainFilenamesIn(COMMIT_OBJECT_DIR);
        int found = 0;
        for (String commitId : commits) {
            Commit c = Commit.fromFile(commitId);
            if (c.getMessage().equals(message)) {
                found++;
                System.out.println(commitId);
            }
        }
        if (found == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    // Display the current status.
    public static void status() {
        display();
    }

    // Checkout
    public static void checkout(String commitId, String filename) {
        String designatedVersion = getDesignatedVersion(commitId, filename);
        if (designatedVersion == null) {
            System.out.println("File does not exist in that commit.");
        } else {
            File designated = join(FILE_OBJECT_DIR, designatedVersion);
            if (!designated.exists()) {
                return;
            }
            File toCheckout = join(CWD, filename);
            writeContents(toCheckout, readContentsAsString(designated));
        }
    }

    public static void checkout(String branch) {
        String currentHead = getHead();
        TreeMap<String, String> currentMap = getTreeMap(currentHead);

        File branchHead = join(HEAD_DIR, branch);
        if (!branchHead.exists()) {
            System.out.println("No such branch exists.");
        } else if (branch.equals(getCurrentBranch())) {
            System.out.println("No need to checkout the current branch.");
        } else {
            String head = readContentsAsString(branchHead);
            // If two branch are not at the same head.
            if (!currentHead.equals(head)) {
                TreeMap<String, String> headMap = getTreeMap(head);
                if (!getUntracked(currentMap, headMap).isEmpty()) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                    System.exit(0);
                }
                List<String> toDelete = getToDelete(currentMap, headMap);
                for (String filename : headMap.keySet()) {
                    File file = join(FILE_OBJECT_DIR, headMap.get(filename));
                    File toOverwrite = join(CWD, filename);
                    writeContents(toOverwrite, readContentsAsString(file));
                }

                for (String filename : toDelete) {
                    File f = join(CWD, filename);
                    f.delete();
                }
            }

            // Set the HEAD to new head.
            writeContents(HEAD, branchHead.getAbsolutePath());
        }
    }

    // Create a new branch with the given name.
    public static void branch(String branch) {
        File newBranch = join(HEAD_DIR, branch);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
        } else {
            writeContents(newBranch, getHead());
            // Copy the logs.
            String log = readLog();
            File newBranchLog = join(LOG_DIR, branch);
            writeContents(newBranchLog, log);
        }
    }

    // Remove the branch with the given name.
    public static void rmBranch(String branch) {
        File toRemoveBranch = join(HEAD_DIR, branch);
        if (!toRemoveBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
        } else {
            String currentBranch = getCurrentBranch();
            if (currentBranch.equals(branch)) {
                System.out.println("Cannot remove the current branch.");
                System.exit(0);
            }
            toRemoveBranch.delete();
        }
    }
}
