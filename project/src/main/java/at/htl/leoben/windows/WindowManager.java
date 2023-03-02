package at.htl.leoben.windows;

import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.windows.games.GameWindowBase;

public class WindowManager {
    private static final Logger log = Manager.getLogger(WindowManager.class);
    private static HashMap<String,GameWindowBase<?>> sharedWindows = new HashMap<>();

    public static void registerWindow(String name, GameWindowBase<?> window) {
        log.info("Register window {} with name {}", Manager.getReadableName(window), name);
        sharedWindows.put(name, window);
    }

    public static GameWindowBase<?> getWindow(String name) {
        log.info("Search for name {}", name);
        if(sharedWindows.containsKey(name)) {
            log.info("Found window with name {}", name);            
            return sharedWindows.get(name);
        }
        log.warn("Could not find sharedWindow with name {}", name);
        return null;
    }
}
