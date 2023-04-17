package ch.progradler.rat_um_rad.start;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainArgsParserTest {
    @Test
    void throwsErrorIfArgsAreEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> MainArgsParser.parseArgs(new String[0]));
    }

    @Test
    void throwsErrorIfFirstWordIsNotClientOrServer() {
        assertThrows(IllegalArgumentException.class,
                () -> MainArgsParser.parseArgs(new String[]{"noServerOrClient,", "otherArg"}));
    }

    @Test
    void forClientReturnsIsServerAsTrue() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"server"});
        assertTrue(args.isStartServer());
    }

    @Test
    void forServerReturnsDefaultPortIfNoneGiven() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"server"});
        assertEquals(MainArgsParser.DEFAULT_PORT, args.getPort());
    }

    @Test
    void forServerReturnsDefaultPortIfNoInteger() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"server", "abc"});
        assertEquals(MainArgsParser.DEFAULT_PORT, args.getPort());
    }

    @Test
    void forServerReturnsEnteredPortIfGiven() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"server", "8765"});
        assertEquals(8765, args.getPort());
    }

    @Test
    void forClientReturnsIsServerAsFalse() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client"});
        assertFalse(args.isStartServer());
    }

    @Test
    void forClientReturnsDefaultHostIfNoneGiven() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client"});
        assertEquals(MainArgsParser.LOCAL_HOST, args.getHost());
    }

    @Test
    void forClientReturnsEnteredHostIfGiven() {
        String host = "123.345.6789";
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client", host});
        assertEquals(host, args.getHost());
    }

    @Test
    void forClientReturnsDefaultPortIfNoneGiven() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"server", "123.345.6789"});
        assertEquals(MainArgsParser.DEFAULT_PORT, args.getPort());
    }

    @Test
    void forClientReturnsDefaultPortIfNoInteger() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client", "123.345.6789:abc"});
        assertEquals(MainArgsParser.DEFAULT_PORT, args.getPort());
    }

    @Test
    void forClientReturnsEnteredPortIfGiven() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client", "123.345.6789:8765"});
        assertEquals(8765, args.getPort());
    }

    @Test
    void forClientReturnsNullAsUsernameIfNoneGiven() {
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client", "123.345.6789:8765"});
        assertNull(args.getUsername());
    }

    @Test
    void forClientReturnsGivenUsernameIfGiven() {
        String username = "iAmTheBest";
        MainArgs args = MainArgsParser.parseArgs(new String[]{"client", "123.345.6789:8765", username});
        assertEquals(username, args.getUsername());
    }
}