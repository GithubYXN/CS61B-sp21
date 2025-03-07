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
// if operating system is windows, replace / in path with \\\\.
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
        commit(message, null);
    }

    private static void commit(String message, String secondParentId) {
        String parentId = getHead();
        Commit parentCommit = Commit.fromFile(parentId, COMMIT_OBJECT_DIR);
        TreeMap<String, String> blobsMap = parentCommit.getBlobsMap();
        Commit toCommit = new Commit(message, parentId, secondParentId, blobsMap);
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
                }
            }

            if (removeStagingFiles != null && !removeStagingFiles.isEmpty()) {
                for (String filename : removeStagingFiles) {
                    blobsMap.remove(filename);
                }
            }

            // clear staging area.
            clearStagingArea();

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
            if (currentVersion != null) {
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
        System.out.print(log);
    }

    // Print the global-log.
    public static void globalLog() {
        String globalLog = readGlobalLog();
        System.out.print(globalLog);
    }

    // Find commit ids with the given message.
    public static void find(String message) {
        List<String> commits = plainFilenamesIn(COMMIT_OBJECT_DIR);
        int found = 0;
        for (String commitId : commits) {
            Commit c = Commit.fromFile(commitId, COMMIT_OBJECT_DIR);
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
        } else if (designatedVersion.equals("no commit")) {
            return;
        } else {
            File designated = join(FILE_OBJECT_DIR, designatedVersion);
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
                checkoutWith(currentMap, headMap);
            }

            // Set the HEAD to new head.
            writeContents(HEAD, branchHead.getAbsolutePath());
        }
    }

    private static void checkoutWith(TreeMap<String, String> current,
                                           TreeMap<String, String> checkout) {
        if (!getUntracked(current, checkout).isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
            System.exit(0);
        }
        List<String> toDelete = getToDelete(current, checkout);
        for (String filename : checkout.keySet()) {
            File file = join(FILE_OBJECT_DIR, checkout.get(filename));
            File toOverwrite = join(CWD, filename);
            writeContents(toOverwrite, readContentsAsString(file));
        }

        for (String filename : toDelete) {
            File f = join(CWD, filename);
            f.delete();
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

    // Reset the current head to given commit.
    public static void reset(String commitId) {
        Commit givenCommit = Commit.fromFile(commitId, COMMIT_OBJECT_DIR);
        if (givenCommit == null) {
            System.out.println("No commit with that id exists.");
        } else {
            // checkout
            TreeMap<String, String> givenCommitMap = getTreeMap(commitId);
            TreeMap<String, String> currentMap = getTreeMap(getHead());
            checkoutWith(currentMap, givenCommitMap);

            // set head to given commit
            String branch = getCurrentBranch();
            File branchHead = join(HEAD_DIR, branch);
            writeContents(branchHead, commitId);

            // clear staging area.
            clearStagingArea();

            // overwrite log.
            resetLog(commitId);
        }
    }

    // Merge two branches.
    public static void merge(String branch) {
        if (!join(HEAD_DIR, branch).exists()) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        String splitPoint = getSplitPoint(getCurrentBranch(), branch);
        String branchHead = readContentsAsString(join(HEAD_DIR, branch));
        String currentHead = getHead();

        if (branch.equals(getCurrentBranch())) {
            System.out.println("Cannot merge a branch with itself.");
        } else if (!getStagedFiles().isEmpty() || !getRemovedFiles().isEmpty()) {
            System.out.println("You have uncommitted changes.");
        } else if (!getUntracked(getTreeMap(currentHead), getTreeMap(branchHead)).isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
        } else {
            if (branchHead.equals(splitPoint)) {
                System.out.println("Given branch is an ancestor of the current branch.");
            } else if (currentHead.equals(splitPoint)) {
                checkout(branch);
                System.out.println("Current branch fast-forwarded.");
            } else {
                TreeMap<String, String> branchHeadMap = getTreeMap(branchHead);
                TreeMap<String, String> currentHeadMap = getTreeMap(currentHead);
                TreeMap<String, String> splitPointMap = getTreeMap(splitPoint);
                boolean isConflict = false;

                for (String filename : branchHeadMap.keySet()) {
                    File f = join(CWD, filename);
                    String currentFile = currentHeadMap.get(filename);
                    String branchFile = branchHeadMap.get(filename);
                    String splitPointFile = splitPointMap.get(filename);
                    if (splitPointFile != null && !branchFile.equals(splitPointFile)
                            && currentFile != null && currentFile.equals(splitPointFile)) {
                        writeContents(f, readContents(join(FILE_OBJECT_DIR, branchFile)));
                        add(filename);
                    } else if (!splitPointMap.containsKey(filename)
                            && !currentHeadMap.containsKey(filename)) {
                        checkout(branchHead, filename);
                        add(filename);
                    } else if (hasMergeConflict(splitPointMap,
                            currentFile, branchFile, splitPointFile, filename)) {
                        String currentContent = readContentsAsString(
                                join(FILE_OBJECT_DIR, currentFile));
                        String branchContent = readContentsAsString(
                                join(FILE_OBJECT_DIR, branchFile));

                        writeMergeConflict(currentContent, branchContent, f, filename);
                        isConflict = true;
                    }
                }
                for (String filename : splitPointMap.keySet()) {
                    String currentFile = currentHeadMap.get(filename);
                    String splitPointFile = splitPointMap.get(filename);
                    String branchFile = branchHeadMap.get(filename);
                    if (currentFile != null && currentFile.equals(splitPointFile)
                            && !branchHeadMap.containsKey(filename)) {
                        remove(filename);
                    } else if (hasMergeConflict(splitPointMap,
                            currentFile, branchFile, splitPointFile, filename)) {
                        String currentContent = currentFile == null ? ""
                                : readContentsAsString(join(FILE_OBJECT_DIR, currentFile));
                        String branchContent = branchFile == null ? ""
                                : readContentsAsString(join(FILE_OBJECT_DIR, branchFile));

                        writeMergeConflict(currentContent, branchContent,
                                join(CWD, filename), filename);
                        isConflict = true;
                    }
                }

                String message = "Merged " + branch + " into " + getCurrentBranch() + ".";
                commit(message, branchHead);
                if (isConflict) {
                    System.out.println("Encountered a merge conflict.");
                }
            }
        }
    }

    // Remote commands below;

    public static void addRemote(String remoteName, String remoteDirectory) {
        File remote = join(REMOTE_DIR, remoteName);
        if (remote.exists()) {
            System.out.println("A remote with that name already exists.");
        } else {
            String directory = remoteDirectory.replace("/", File.separator);
            writeContents(remote, directory);
        }
    }

    public static void removeRemote(String remoteName) {
        File remote = join(REMOTE_DIR, remoteName);
        if (!remote.exists()) {
            System.out.println("A remote with that name does not exist.");
        }
        remote.delete();
    }

    public static void push(String remoteName, String remoteBranchName) {
        File remoteDir = getRemoteDirectory(remoteName);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
        } else if (!isHistory(remoteDir, remoteBranchName)) {
            System.out.println("Please pull down remote changes before pushing.");
        } else {
            File remoteCommitDir = join(remoteDir, "/objects/commit");
            pushCommitsTo(remoteCommitDir);

            File remoteFileDir = join(remoteDir, "/objects/file");
            pushFilesTo(remoteFileDir);

            TreeMap<String, String> headMap = getTreeMap(getHead());
            String remote = remoteDir.getAbsolutePath();
            File remoteWorkingDir = new File(remote.substring(0, remote.length() - 8));
            List<String> workingFiles = plainFilenamesIn(remoteWorkingDir);
            for (String filename : workingFiles) {
                if (!headMap.containsKey(filename)) {
                    restrictedDelete(join(remoteWorkingDir, filename));
                }
            }
            for (String filename : headMap.keySet()) {
                File f = join(remoteWorkingDir, filename);
                writeContents(f, (Object) readContents(
                        join(FILE_OBJECT_DIR, headMap.get(filename))));
            }

            File remoteLog = join(remoteDir, "/logs/" + remoteBranchName);
            String localLog = readLog();
            writeContents(remoteLog, localLog);

            File remoteBranchHead = join(remoteDir, "/refs/heads/" + remoteBranchName);
            writeContents(remoteBranchHead, getHead());
            File remoteHead = join(remoteDir, "HEAD");
            writeContents(remoteHead, remoteBranchHead.getAbsolutePath());
        }
    }

    public static void fetch(String remoteName, String remoteBranchName) {
        File remoteDir = getRemoteDirectory(remoteName);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteCommitDir = join(remoteDir, "/objects/commit");
        File remoteBranchDir = join(remoteDir, "/refs/heads");
        File remoteBranch = join(remoteBranchDir, remoteBranchName);
        if (!remoteBranch.exists()) {
            System.out.println("That remote does not have that branch.");
            return;
        }
        List<String> allCommits = queueToList(
                getAllCommits(remoteBranchName, remoteBranchDir, remoteCommitDir));
        for (String commit : allCommits) {
            File localCommit = join(COMMIT_OBJECT_DIR, commit);
            if (!localCommit.exists()) {
                byte[] content = readContents(join(remoteCommitDir, commit));
                writeContents(localCommit, content);
            }
        }

        File remoteFileDir = join(remoteDir, "/objects/file");
        String remoteHeadId = readContentsAsString(join(remoteBranchDir, remoteBranchName));
        Commit remoteHead = Commit.fromFile(remoteHeadId, remoteCommitDir);
        TreeMap<String, String> remoteHeadMap = remoteHead.getBlobsMap();
        for (String filename : remoteHeadMap.keySet()) {
            File localFile = join(FILE_OBJECT_DIR, remoteHeadMap.get(filename));
            if (!localFile.exists()) {
                byte[] content = readContents(join(remoteFileDir, remoteHeadMap.get(filename)));
                writeContents(localFile, content);
            }
        }

        File remoteBranchLog = join(remoteDir, "/logs/" + remoteBranchName);
        String logContent = readContentsAsString(remoteBranchLog);
        File localBranchLogDir = join(LOG_DIR, remoteName);
        localBranchLogDir.mkdirs();
        File localBranchLog = join(localBranchLogDir, remoteBranchName);
        writeContents(localBranchLog, logContent);

        File localBranchHeadDir = join(HEAD_DIR, remoteName);
        localBranchHeadDir.mkdirs();
        File localBranchHead = join(localBranchHeadDir, remoteBranchName);
        writeContents(localBranchHead, remoteHeadId);
    }

    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        merge(remoteName + "/" + remoteBranchName);
    }
}
