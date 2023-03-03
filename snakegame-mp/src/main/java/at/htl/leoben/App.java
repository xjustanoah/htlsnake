package at.htl.leoben;

import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import at.htl.leoben.engine.RenderEngineThread;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.socket.Server;

public class App 
{
    private static final Logger log = Manager.getLogger(App.class);

    public static void main( String[] args ) throws IOException, InterruptedException
    {
        Configurator.setRootLevel(Level.TRACE);
        
        log.info("Application started");
        RenderEngineThread renderEngine = new RenderEngineThread();
        renderEngine.start();
        Server server = new Server();
        server.start(8080);
    }
}