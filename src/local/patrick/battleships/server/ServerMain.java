package local.patrick.battleships.server;

import local.patrick.battleships.common.Constants;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        try {
            var port = Constants.DEFAULT_PORT;
            if (args.length > 0)
                port = Integer.parseInt(args[0]);
            var gateway = new ServerGateway(port);
            gateway.run();
        }catch (NumberFormatException e){
            System.out.println("The server takes either no or one argument, the port has to be a number, defaults to " + Constants.DEFAULT_PORT);
        }
    }
}
