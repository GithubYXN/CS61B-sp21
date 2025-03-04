package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;
import static gitlet.Log.*;

public class Init {

    // Default branch and commit.
    public static final String DEFAULT_BRANCH = "master";
    public static final Commit DEFAULT_COMMIT = new Commit("initial commit",
            null, null, new TreeMap<>());

    // The current HEAD point's position.
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    // The log files.
    public static final File LOG_DIR = join(GITLET_DIR, "logs");
    // The objects.
    public static final File OBJECT_DIR = join(GITLET_DIR, "objects");
    public static final File COMMIT_OBJECT_DIR = join(OBJECT_DIR, "commit");
    public static final File FILE_OBJECT_DIR = join(OBJECT_DIR, "file");
    // The refs.
    public static final File REF_DIR = join(GITLET_DIR, "refs");
    // HEADs of each local branch.
    public static final File HEAD_DIR = join(REF_DIR, "heads");
    // Staging area information.
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File ADD_DIR = join(STAGING_DIR, "add");
    public static final File REMOVE_DIR = join(STAGING_DIR, "remove");
    // Master branch.
    public static final File DEFAULT_HEAD = join(HEAD_DIR, DEFAULT_BRANCH);
    public static final File DEFAULT_LOG = join(LOG_DIR, DEFAULT_BRANCH);

    public static void setup() {
        // make directories
        LOG_DIR.mkdirs();
        OBJECT_DIR.mkdirs();
        COMMIT_OBJECT_DIR.mkdirs();
        FILE_OBJECT_DIR.mkdirs();
        REF_DIR.mkdirs();
        HEAD_DIR.mkdirs();
        STAGING_DIR.mkdirs();
        ADD_DIR.mkdirs();
        REMOVE_DIR.mkdirs();

        // make HEAD files
        try {
            HEAD.createNewFile();
            DEFAULT_HEAD.createNewFile();
            DEFAULT_LOG.createNewFile();
        } catch (IOException e) {
            throw error("Can not init.");
        }

        // set HEAD point and record the HEAD and log.
        Blob initialCommit = new Blob(DEFAULT_COMMIT);
        writeContents(HEAD, DEFAULT_HEAD.getAbsolutePath());
        writeContents(DEFAULT_HEAD, initialCommit.getSha1id());
        writeLog(DEFAULT_COMMIT, initialCommit.getSha1id());
        
        // save the initial commit
        initialCommit.save();
    }
}
