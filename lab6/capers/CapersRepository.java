package capers;

import java.io.File;
import java.io.IOException;

import static capers.Dog.DOG_FOLDER;
import static capers.Utils.*;

/** A repository for Capers 
 * @author
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 *
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD, ".capers");

    /** Story File. */
    static final File STORY_FILE = join(CAPERS_FOLDER, "story");

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        if (!CAPERS_FOLDER.exists()) {
            CAPERS_FOLDER.mkdir();
        }

        if (!DOG_FOLDER.exists()) {
            DOG_FOLDER.mkdir();
        }

        if (!STORY_FILE.exists()) {
            try {
                STORY_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        String content = readContentsAsString(STORY_FILE);
        content += text + "\n";
        writeContents(STORY_FILE, content);
        System.out.print(content);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        Dog newDog = new Dog(name, breed, age);
        newDog.saveDog();
        System.out.println(newDog);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        Dog birthdayDog = Dog.fromFile(name);
        if (birthdayDog != null) {
            birthdayDog.haveBirthday();
            File dogFile = join(DOG_FOLDER, name);
            writeObject(dogFile, birthdayDog);
        }
    }
}
