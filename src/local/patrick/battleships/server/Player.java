package local.patrick.battleships.server;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

class Player implements Runnable {
    public final AtomicReference<Socket> socket;
    private final Thread thread;

    Player(Socket socket) {
        this.socket = socket;

    }


    @Override
    public void run() {

    }
}
