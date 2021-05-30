package local.patrick.battleships.client;

import local.patrick.battleships.common.Constants;

public class ClientMain {
    /**
     * Parses command line parameters and then launches the client
     * @param args array with either 0, 1 or 2+ arguments, with args[0] being address and [1] port
     */
    public static void main(String[] args) {
        // Command line parameter parsing
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

        try(var client = new Client(addr, port)){
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
