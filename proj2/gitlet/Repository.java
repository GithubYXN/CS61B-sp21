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
            System.out.println("A Gitlet version-control system already exists in the current directory.");
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
            throw error("File does not exist.");
        }
        Blob blob = new Blob(toAdd);
        blob.add(filename);
    }

    // Make a commit.
    public static void commit(String message) {
        String parentId = getHead();
        Commit parentCommit = Commit.fromFile(parentId);
        TreeMap<String, String> blobsMap = parentCommit.getBlobsMap();
        Commit toCommit = new Commit(message, parentId, null, blobsMap);
        List<String> stagingFiles = plainFilenamesIn(ADD_DIR);
        if (stagingFiles != null && !stagingFiles.isEmpty()) {
            // Add tracking file to map and persistence, then delete it from staging area.
            for (String filename : stagingFiles) {
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
                throw error("No reason to remove the file.");
            }
        }
    }

    // Print the logs.
    public static void log() {
        readLog();
    }

    // Print the global-log.
    public static void globalLog() {
        readGlobalLog();
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
            throw error("Found no commit with that message.");
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
            throw error("File does not exist in that commit.");
        }
        File designated = join(FILE_OBJECT_DIR, designatedVersion);
        File toCheckout = join(CWD, filename);
        writeContents(toCheckout, readContentsAsString(designated));
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
            TreeMap<String, String> headMap = getTreeMap(head);
            if (!getUntracked(currentMap, headMap).isEmpty()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
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

            // Set the HEAD to new head.
            writeContents(HEAD, head);
        }
    }
}
