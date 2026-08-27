import java.util.List;

/**
 * Handles user interaction: printing messages and simple prompts.
 */
public class Ui {
    private static final String SEP = "____________________________________________________________";

    public void printBanner() {
        String banner = """
                         ____       _      _        ____    _   _
                        |  _ \\     / \\    | |      |  _ \\  | | | |
                        | |_) |   / _ \\   | |      | |_) | | |_| |
                        |  _ <   / ___ \\  | |___   |  __/  |  _  |
                        |_| \\_\\ /_/   \\_\\ |_____|  |_|     |_| |_|
                        Hello! I'm Ralph.
                        """;
        System.out.println(banner);
    }

    public void printPrompt() {
        System.out.println("What can I do for you?");
    }

    public void printSeparator() {
        System.out.println(SEP);
    }

    public void showLoadingError() {
        System.out.println(" Warning: could not load saved tasks.");
    }

    public void showError(String msg) {
        System.out.println(" Oh no! " + msg);
    }

    public void showExit() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void printTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println(" No tasks.");
            return;
        }
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    public void showAdded(Task task, int total) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + total + " tasks in the list.");
    }

    public void showRemoved(Task task, int total) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + total + " tasks in the list.");
    }

    public void showMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    public void showUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }
}
