package ch.progradler.rat_um_rad.client.input_handler;

import java.util.Scanner;

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
