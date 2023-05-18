package ch.progradler.rat_um_rad.client.gateway;

/**
 * InputPacketGateway is a Singleton, so we can add Listeners for ServerResponses.
 */
public class InputPacketGatewaySingleton {
    private static ServerInputPacketGateway inputPacketGateway;

    public static ServerInputPacketGateway getInputPacketGateway() {
        if (inputPacketGateway == null) {
            throw new RuntimeException("inputPacketGateway not set yet!");
        }
        return inputPacketGateway;
    }

    public static void setInputPacketGateway(ServerInputPacketGateway outputPacketGateway) {
        InputPacketGatewaySingleton.inputPacketGateway = outputPacketGateway;
    }
}
