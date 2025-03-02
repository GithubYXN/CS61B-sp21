package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author yangx
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "log":
                Repository.log();
                break;
            case "add":
                Repository.add(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Please enter a commit message.");
                } else {
                    Repository.commit(args[1]);
                }
                break;
            case "rm":
                Repository.remove(args[1]);
                break;
        }
    }
}
