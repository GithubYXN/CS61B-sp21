package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Init.*;
import static gitlet.RepositoryUtils.*;
import static gitlet.Utils.*;

public class Blob {
    private final String sha1id;
    private final byte[] data;
    private final Serializable obj;

    public Blob(Serializable obj) {
        if (obj instanceof File) {
            data = readContents((File) obj);
        } else {
            data = Utils.serialize(obj);
        }
        sha1id = Utils.sha1((Object) data);
        this.obj = obj;
    }

    // Add this blob to staging area.
    public void add(String name) {
        if (obj instanceof File) {
            // Check if the current commit version is the same as working version.
            File toAdd = join(ADD_DIR, name);
            String currentVersion = getCurrentVersion(name);
            if (currentVersion != null && currentVersion.equals(sha1id)) {
                if (toAdd.exists()) {
                    toAdd.delete();
                }
                return;
            }

            if (!toAdd.exists() || !readContentsAsString(toAdd).equals(sha1id)) {
                writeContents(toAdd, sha1id);
            }
        } else {
            throw error("No need to add a " + obj.getClass().getSimpleName() + " to staging.");
        }
    }

    // Persistence data.
    public void save() {
        File f;
        if (obj instanceof File) {
            f = join(FILE_OBJECT_DIR, sha1id);
        } else {
            f = join(COMMIT_OBJECT_DIR, sha1id);
        }
        writeContents(f, data);
    }

    // Getter and setter function.
    public String getSha1id() {
        return sha1id;
    }

    public byte[] getData() {
        return data;
    }

    public Serializable getObj() {
        return obj;
    }

}
