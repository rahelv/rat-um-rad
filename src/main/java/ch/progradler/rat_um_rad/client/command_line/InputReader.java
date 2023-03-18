package ch.progradler.rat_um_rad.client.command_line;

import java.util.Scanner;

/**
 * Can take user input in text form from command line.
 */
public class InputReader {
    final Scanner scanner;

    public InputReader() {
        scanner = new Scanner(System.in);
    }

    public String readInputWithPrompt(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }
}
