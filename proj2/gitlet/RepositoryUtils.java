package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Init.*;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class RepositoryUtils {

    // Get the HEAD point(sh1id).
    public static String getHead() {
        File currentHead = new File(readContentsAsString(HEAD));
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

    // Get the files that tracked in current but are not present in checkout which will be deleted.
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

    // Get the files that untracked in current and are present in checkout.
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
            String currentTrackedF = currentTracked.get(filename);
            if ((!removeF.exists() && !f.exists())
                    || (addF.exists() && !f.exists())) {
                modifiedNotStaged.add(filename + " (deleted)");
            }
            if (f.exists()) {
                Blob b = new Blob(f);
                if ((!addF.exists() && !b.getSha1id().equals(currentTrackedF))
                        || (addF.exists() && !b.getSha1id().equals(currentTrackedF))) {
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

    // Get all commits in given branch.
    public static Queue<List<String>> getAllCommits(String branch) {
        Queue<List<String>> q = new LinkedList<>();
        Queue<List<String>> allCommits = new LinkedList<>();

        File branchHead = join(HEAD_DIR, branch);
        String headCommitId = readContentsAsString(branchHead);

        q.add(List.of(headCommitId));
        allCommits.add(List.of(headCommitId));
        while (!q.isEmpty()) {
            List<String> commits = q.poll();
            List<String> parents = new ArrayList<>();
            for (String commitId : commits) {
                Commit c = Commit.fromFile(commitId);
                String firstParent = c.getParent();
                String secondParent = c.getSecondParent();
                if (firstParent != null) {
                    parents.add(firstParent);
                }
                if (secondParent != null) {
                    parents.add(secondParent);
                }
                if (!parents.isEmpty()) {
                    q.add(parents);
                    allCommits.add(parents);
                }
            }
        }
        return allCommits;
    }

    // Get split point.
    public static String getSplitPoint(String currentBranch, String givenBranch) {
        Queue<List<String>> localCommits = getAllCommits(currentBranch);
        List<String> givenCommits = queueToList(getAllCommits(givenBranch));
        while (!localCommits.isEmpty()) {
            List<String> commits = localCommits.poll();
            for (String commit : commits) {
                if (givenCommits.contains(commit)) {
                    return commit;
                }
            }
        }

        return null;
    }

    public static List<String> queueToList(Queue<List<String>> q) {
        List<String> res = new ArrayList<>();
        while (!q.isEmpty()) {
            List<String> l = q.poll();
            res.addAll(l);
        }
        return res;
    }

    // Check whether there is a merge conflict.
    public static boolean hasMergeConflict(TreeMap<String, String> splitPointMap,
                                             String currentFile, String branchFile,
                                             String splitPointFile, String filename) {
        return (splitPointMap.containsKey(filename)
                && (currentFile != null && branchFile == null
                && !currentFile.equals(splitPointFile)
                || (currentFile == null && branchFile != null
                && !branchFile.equals(splitPointFile))))
                || ((currentFile != null && splitPointMap.containsKey(filename)
                && !currentFile.equals(splitPointFile)
                && !branchFile.equals(splitPointFile)
                && !branchFile.equals(currentFile))
                || (currentFile != null && !splitPointMap.containsKey(filename)
                && !branchFile.equals(currentFile)));
    }

    public static void writeMergeConflict(String currentContent, String branchContent,
                                          File f, String filename) {
        String conflictMessage = "<<<<<<< HEAD\n" + currentContent
                + "=======\n" + branchContent + ">>>>>>>\n";
        writeContents(f, conflictMessage);
        add(filename);
    }

    public static File getRemoteDirectory(String remoteName) {
        String remoteDir = readContentsAsString(join(REMOTE_DIR, remoteName));
        return new File(remoteDir);
    }
    // Check if remote branch's head is in the history of the current local head.
    public static boolean isHistory(File remoteDir, String remoteBranchName) {
        File remoteBranch = join(remoteDir, "\\refs\\heads\\" + remoteBranchName);
        if (!remoteBranch.exists()) {
            return true;
        }
        String remoteBranchCommitId = readContentsAsString(remoteBranch);
        Queue<List<String>> localCommitsQueue = getAllCommits(getCurrentBranch());
        List<String> localCommits = queueToList(localCommitsQueue);
        return localCommits.contains(remoteBranchCommitId);
    }

    public static void pushCommitsTo(File remoteCommitDir) {
        List<String> remoteCommits = plainFilenamesIn(remoteCommitDir);
        List<String> localCommits = queueToList(getAllCommits(getCurrentBranch()));
        for (String localCommit : localCommits) {
            if (!remoteCommits.contains(localCommit)) {
                File commit = join(remoteCommitDir, localCommit);
                byte[] contents = readContents(join(COMMIT_OBJECT_DIR, localCommit));
                writeContents(commit, contents);
            }
        }
    }

    public static void pushFilesTo(File remoteFileDir) {
        List<String> remoteFiles = plainFilenamesIn(remoteFileDir);
        TreeMap<String, String> localTrackedMap = getTreeMap(getHead());
        List<String> localFiles = List.copyOf(localTrackedMap.keySet());
        for (String localFile : localFiles) {
            if (!remoteFiles.contains(localFile)) {
                File file = join(remoteFileDir, localFile);
                byte[] contents = readContents(join(FILE_OBJECT_DIR, localFile));
                writeContents(file, contents);
            }
        }
    }
}
