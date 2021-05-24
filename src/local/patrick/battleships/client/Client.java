package local.patrick.battleships.client;

import java.util.Arrays;
import java.util.regex.Pattern;

public class Client {
    private final String serverAddress;

    public Client(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void connect(){

    }

    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("Requires a target ip:port");
        }
        var ipPattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)", Pattern.CASE_INSENSITIVE);
        var matcher = ipPattern.matcher(args[0]);
        System.out.println(Arrays.toString(args));

        if (matcher.matches()){
            System.out.println("arg 0 matches");
            var client = new Client(args[0]);
        }else{
            System.out.println("Argument 0 has to be an ip:port like 172.0.0.1:5555");
        }
    }
}
