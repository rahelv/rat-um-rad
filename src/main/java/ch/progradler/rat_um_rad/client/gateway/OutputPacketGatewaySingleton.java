package ch.progradler.rat_um_rad.client.gateway;

public class OutputPacketGatewaySingleton {
    private static OutputPacketGateway outputPacketGateway;

    public static OutputPacketGateway getOutputPacketGateway() {
        if (outputPacketGateway == null) {
            throw new RuntimeException("outputPacketGateway not set yet!");
        }
        return outputPacketGateway;
    }

    public static void setOutputPacketGateway(OutputPacketGateway outputPacketGateway) {
        OutputPacketGatewaySingleton.outputPacketGateway = outputPacketGateway;
    }
}
