package at.htl.leoben.engine;
import org.apache.logging.log4j.Logger;

import at.htl.leoben.logging.Manager;

public class RenderEngineThread extends Thread {
    private final Logger log = Manager.getLogger(this);
    private int refreshRate = 100;

    public RenderEngineThread() {
        setName("RenderEngine");
    }

    @Override
    public void run() {
        log.info("Starting render engine");
        while(true) {
            RenderEngine.onTick();
            try {
                Thread.sleep(refreshRate);
            } catch (InterruptedException e) {
                log.warn("Could not sleep for {}ms: {}", refreshRate, e.toString());
            }
        }
    }
}
