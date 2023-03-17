package ch.progradler.rat_um_rad.client.command_line;

import java.util.Scanner;

public class CommandReader {
    final Scanner scanner;

    public CommandReader() {
        scanner = new Scanner(System.in);
    }

    public String readInputWithPrompt(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }
}
