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
            File head = join(HEAD_DIR, getBranch());
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
        File toRemove = join(CWD, filename);
        if (isAdd.exists()) {
            isAdd.delete();
        } else {
            File toStage = join(REMOVE_DIR, filename);
            String currentVersion = getCurrentVersion(toStage);
            if (currentVersion != null) {
                writeContents(toStage, currentVersion);
                if (toRemove.exists()) {
                    restrictedDelete(toRemove);
                }
            } else {
                throw error("No reason to remove the file.");
            }
        }
    }

    // Print the logs.
    public static void log() {
        readLog();
    }
}
