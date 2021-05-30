package local.patrick.battleships.server;

import local.patrick.battleships.common.Constants;

import java.io.IOException;

public class ServerMain {
    //TODO introduce command line parameter to set the port
    public static void main(String[] args) throws IOException {
        var gateway = new ServerGateway(Constants.DEFAULT_PORT);
        gateway.run();
    }
}
