package at.htl.leoben.socket;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.engine.InteractiveWindow;
import at.htl.leoben.engine.RenderEngine;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.windows.RootWindow;

public class ClientHandlerThread extends Thread {
    private Socket clientSocket;
    private OutputStreamWriter rawOutputStream;
    private PrintStream printStream;
    private CharacterReaderThread inputReader;
    private final Logger log;
    private final int refreshRate = 100;

    public ClientHandlerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.log = Manager.getLogger(this.getClass().getSimpleName() + "@" + clientSocket.getRemoteSocketAddress().toString());
        setName("ClientHandler");
    }

    public void setup() throws UnsupportedEncodingException, IOException {
        this.rawOutputStream = new OutputStreamWriter(clientSocket.getOutputStream(), "ISO-8859-1");
        this.printStream = new PrintStream(clientSocket.getOutputStream());
    }

    public void setupTerminal() throws IOException {
        log.debug("Setup client terminal to character mode");
        // https://stackoverflow.com/questions/4532344/send-data-over-telnet-without-pressing-enter
        int sequence[] = 
        {
            255, 253, 34,  /* IAC DO LINEMODE */
            255, 250, 34, 1, 0, 255, 240, /* IAC SB LINEMODE MODE 0 IAC SE */
            255, 251, 1    /* IAC WILL ECHO */
        };      
        for(int i : sequence)
            rawOutputStream.write((char)i);
        rawOutputStream.flush();

    }

    public void setupReader() throws IOException {
        log.debug("Setup input reader");
        this.inputReader = new CharacterReaderThread(this.clientSocket);
        inputReader.start();
    }

    @Override
    public void run() {
        try {
            this.setup();
            this.setupTerminal();
            this.setupReader();
            this.printWindow();
        } catch (Exception e) {
            log.info("Exception during client setup: {}", e.toString());
        }        
    }

    public void printWindow() {
        log.info("Creating root window.");
        InteractiveWindow window = new RootWindow(inputReader);
        RenderEngine.register(window);
        log.debug("Attach root window to output stream");
        window.getWindow().registerStream(printStream);
        while(true) {
            try {
                window.getWindow().draw();
                Thread.sleep(refreshRate);
            } catch(Exception e) {
                log.warn("Could not sleep for {}ms: {}", refreshRate, e.toString());
            }
        }
    }
}
