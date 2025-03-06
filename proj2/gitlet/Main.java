package gitlet;

import static gitlet.RepositoryUtils.getHead;

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
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                } else {
                    Repository.init();
                }
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.add(args[1]);
                }
                break;
            case "commit":
                if (args.length != 2 || args[1].isEmpty()) {
                    System.out.println("Please enter a commit message.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.commit(args[1]);
                }
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.remove(args[1]);
                }
                break;
            case "log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.log();
                }
                break;
            case "global-log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.globalLog();
                }
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.find(args[1]);
                }
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.status();
                }
                break;
            case "checkout":
                if (args.length != 2 && args.length != 3 && args.length != 4) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else if (args.length == 2) {
                    Repository.checkout(args[1]);
                } else if (args.length == 3) {
                    if (!args[1].equals("--")) {
                        System.out.println("Incorrect operands.");
                    } else {
                        Repository.checkout(getHead(), args[2]);
                    }
                } else {
                    if (!args[2].equals("--")) {
                        System.out.println("Incorrect operands.");
                    } else {
                        Repository.checkout(args[1], args[3]);
                    }
                }
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.branch(args[1]);
                }
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.rmBranch(args[1]);
                }
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.reset(args[1]);
                }
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                } else if (!RepositoryUtils.repositoryExsists()) {
                    System.out.println("Not in an initialized Gitlet directory.");
                } else {
                    Repository.merge(args[1]);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }
}
