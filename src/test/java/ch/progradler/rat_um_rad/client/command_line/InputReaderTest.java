package ch.progradler.rat_um_rad.client.command_line;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InputReaderTest {

    private static final String PROMPT = "Some prompt";
    private static final String INPUT = "input";

    @Mock
    Scanner scannerMock;

    private InputReader inputReader;

    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    private PrintStream outBackup;
    private PrintStream errBackup;


    @BeforeEach
    void setUp() {
        inputReader = new InputReader(scannerMock);
        when(scannerMock.nextLine()).thenReturn(INPUT);

        outBackup = System.out;
        errBackup = System.err;
        System.setOut(new PrintStream(outStream));
        System.setErr(new PrintStream(errStream));
    }

    /**
     * This method is run after each test.
     * It redirects System.out / System.err back to the normal streams.
     */
    @AfterEach
    public void reestablishStdOutStdErr() {
        System.setOut(outBackup);
        System.setErr(errBackup);
    }

    @Test
    void readInputWithPromptPrintsPrompt() {
        inputReader.readInputWithPrompt(PROMPT);
        String toTest = outStream.toString();
        toTest = removeNewline(toTest);
        assertEquals(PROMPT, toTest);
    }

    private static String removeNewline(String str) {
        return str.replace("\n", "").replace("\r", "");
    }

    @Test
    void readInputWithPromptReadsFromScannerReturnsEmptyStringIfNullIsReturned() {
        when(scannerMock.nextLine()).thenReturn(null);
        String result = inputReader.readInputWithPrompt(PROMPT, false);
        assertEquals("", result);
    }

    @Test
    void readInputWithPromptReadsFromScanner() {
        String result = inputReader.readInputWithPrompt(PROMPT, false);
        assertEquals(INPUT, result);
    }

    @Test
    void inputReaderReadsFromScannerAndStripsIFStripIsTrue() {
        when(scannerMock.nextLine()).thenReturn("    some text unstripped     ");

        String result = inputReader.readInputWithPrompt("prompt", true);
        assertEquals("some text unstripped", result);
    }
}
