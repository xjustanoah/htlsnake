package at.htl.leoben.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.logging.Manager;

public class Server {
    private final Logger log = Manager.getLogger(this);
    ServerSocket serverSocket;

    public void start(int port) throws IOException, InterruptedException {
        serverSocket = new ServerSocket(port);
        log.info("TCP Server running on {}:{}", serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
        log.info("Listening for connections");
        while(true) {
            Socket clientSocket = serverSocket.accept();
            log.info("Client connected: {}", clientSocket.getRemoteSocketAddress().toString());
            ClientHandlerThread handler = new ClientHandlerThread(clientSocket);
            handler.start();
        }
    }
}
