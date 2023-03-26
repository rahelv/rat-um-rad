package ch.progradler.rat_um_rad.client.protocol.pingpong;

import ch.progradler.rat_um_rad.client.gateway.OutputPacketGateway;

import static ch.progradler.rat_um_rad.shared.util.TimeUtils.SECOND;

/**
 * Sends pong-messages back to the clients and detects whether they return
 * pong-messages or not. If not, connection is broken up.
 */
public class ClientPingPongRunner implements Runnable {
    /**
     * Time in seconds until the server is seen as disconnected
     */
    static final int TIME_FOR_DISCONNECT = 16;

    private final ClientPingPongHandler pingPongHandler;

    public ClientPingPongRunner(OutputPacketGateway outputPacketGateway) {
        pingPongHandler = new ClientPingPongHandler(outputPacketGateway);
    }

    /**
     * ServerResponseHandler sets pingArrived true if ping-message arrived.
     */
    public void pingArrived() {
        synchronized (pingPongHandler) {
            pingPongHandler.setPingArrived(true);
        }
    }

    /**
     * Sends pong-messages back to the clients and detects whether they return pong-messages or not. If not, connection is broken up.
     * <p>
     * First, it waits until the first ping-message arrives. Then, it begins to count
     * the time passed until the next ping message arrives.
     * If it takes longer than {@link ClientPingPongRunner#TIME_FOR_DISCONNECT}
     * milli seconds, the connection is broken up.
     */
    @Override
    public void run() {
        waitUntilFirstPingArrived();

        int arrivedPingCounter = 1;
        while (true) {
            waitForPingResponseAndHandle(arrivedPingCounter);
            arrivedPingCounter++;
        }
    }

    private void waitUntilFirstPingArrived() {
        while (!pingPongHandler.isPingArrived()) {
            waitOneSecond();
        }
    }

    private void waitForPingResponseAndHandle(int arrivedPingCounter) {
        int timeCounter = waitUntilNextPingArrived();
        pingPongHandler.handleTimeForDisconnectOver(arrivedPingCounter, timeCounter);
    }

    /**
     * @return time in seconds until next ping arrived
     * times out {@link ClientPingPongRunner#TIME_FOR_DISCONNECT}
     */
    private int waitUntilNextPingArrived() {
        int timeCounter = 0; // in seconds
        synchronized (pingPongHandler) {
            pingPongHandler.setPingArrived(false);// in seconds
        }
        while (timeCounter != TIME_FOR_DISCONNECT && !pingPongHandler.isPingArrived()) {
            waitOneSecond();
            timeCounter++;
        }
        return timeCounter;
    }

    /**
     * Current thread sleeps for 1 second.
     */
    private void waitOneSecond() {
        try {
            Thread.sleep(SECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO Exception handling
        }
    }
}
