package local.patrick.battleships.client;

import local.patrick.battleships.common.Constants;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        var addr = "localhost";
        var port  = Constants.DEFAULT_PORT;
        if (args.length == 1){
            addr = args[0];
        }else if(args.length > 1){
            addr = args[0];
            try{
                port = Integer.parseInt(args[1]);
            }catch (NumberFormatException e){
                System.out.println("Failed to parse argument 2, port into integer");
                return;
            }
        }
        var client = new Client(addr, port);
        client.run();
    }
}
