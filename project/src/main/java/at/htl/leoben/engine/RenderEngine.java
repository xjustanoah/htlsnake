package at.htl.leoben.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.logging.Manager;

public class RenderEngine {
    private static final Logger log = Manager.getLogger(RenderEngine.class);
    private static HashMap<InteractiveWindow, Integer> windows = new HashMap<>();
    private static ArrayList<InteractiveWindow> registerCache = new ArrayList<>();
    private static ArrayList<InteractiveWindow> unregisterCache = new ArrayList<>();

    public static synchronized  Window register(InteractiveWindow window) {
        if(!windows.containsKey(window)) {
            try {
                window.__onStart();
                registerCache.add(window);
                log.debug("{} with {} added to register cache by {}", 
                    Manager.getReadableName(window),
                    Manager.getReadableName(window.getWindow()),
                    StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName()
                );
            } catch (Exception e) {
                log.error("Could not register window {} due to an exception in __onStart: {}", window.getClass().getSimpleName(), e.toString());
                e.printStackTrace();
                return null;
            }
        }
        return window.getWindow();
    }

    public static synchronized void unregister(InteractiveWindow window) {
        if(windows.containsKey(window)) {
            unregisterCache.add(window);
            log.debug("{} with {} added to unregister cache by {}", 
                Manager.getReadableName(window),
                Manager.getReadableName(window.getWindow()),
                StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName()
            );
        }
    }

    public static synchronized void onTick() {
         for (Entry<InteractiveWindow, Integer> windowEntry : windows.entrySet())
            try {
                windowEntry.getKey().__onTick();
            } catch (Exception e) {
                log.error("Exception in __onTick in {}: {}", windowEntry.getKey().getClass().getSimpleName(), e.toString());
                e.printStackTrace();
            }
        // We should not modify the windowEntry while being in the loop
        // We therefore use caches
        registerFromCache();
        unregisterFromCache();
    }

    private static void registerFromCache() {
        for (InteractiveWindow window : registerCache) {
            log.trace("Register {} from register cache", Manager.getReadableName(window));
            if(!windows.containsKey(window))
                windows.put(window, 0);
            windows.put(window, windows.get(window) + 1);            
        }
        registerCache.clear();
    }

    private static void unregisterFromCache() {
        for (InteractiveWindow window : unregisterCache) {
            log.trace("Unregister {} from unregister cache", Manager.getReadableName(window));
            if(windows.containsKey(window)) {
                int references = windows.get(window);
                if(references == 1)
                    windows.remove(window);
                else
                    windows.put(window, references-1);
            } else {
                log.error("Could not unregister window {} due to it is already unregistered", Manager.getReadableName(window));
            }
        }
        unregisterCache.clear();
    }
}
