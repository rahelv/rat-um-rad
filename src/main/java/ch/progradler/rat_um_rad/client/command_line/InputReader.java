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

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * @see InputReader#readInputWithPrompt(String, boolean)
     */
    public String readInputWithPrompt(String prompt) {
        return readInputWithPrompt(prompt, true);
    }

    /**
     * Displays prompt to user and takes user input.
     *
     * @param prompt: Is displayed to user
     * @param strip: Whether to strip input before returning.
     * @return String: User Input
     */
    public String readInputWithPrompt(String prompt, boolean strip) {
        System.out.println(prompt);
        String input = scanner.nextLine();
        if(input == null) {
            return "";
        }
        if (strip) return input.strip();
        return input;
    }
}
