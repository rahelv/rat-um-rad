package ch.progradler.rat_um_rad.client.gateway;

import ch.progradler.rat_um_rad.client.utils.listeners.IListener;

public class InputPacketGatewaySingleton {
    private static ServerInputPacketGateway inputPacketGateway;

    public static void setInputPacketGateway(ServerInputPacketGateway outputPacketGateway) {
        InputPacketGatewaySingleton.inputPacketGateway = outputPacketGateway;
    }

    public static ServerInputPacketGateway getInputPacketGateway() {
        if (inputPacketGateway == null) {
            throw new RuntimeException("inputPacketGateway not set yet!");
        }
        return inputPacketGateway;
    }
}
