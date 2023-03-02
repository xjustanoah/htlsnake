package at.htl.leoben.engine;

import java.lang.reflect.Method;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.engine.interfaces.InteractiveWindowInterface;
import at.htl.leoben.logging.Manager;

public abstract class InteractiveWindow implements InteractiveWindowInterface {
    private final Logger log = Manager.getLogger(this);
    private String currentState = null;

    public void __onStart() throws Exception {
        log.debug("__onStart called");
        if(currentState == null) {
            this.onStart();
            return;
        }
        runMethod("onStart__" + currentState);
    }

    public void __onTick() throws Exception {
        if(currentState == null) {
            this.onTick();
            return;
        }
        runMethod("onTick__" + currentState);
    }

    public void __setState(String state) {
        currentState = state;
    }

    public void switchState(String state) {
        if(state == null) {}
            log.warn("Switched state to null. Do not switch state to null unless you know what you do.");

        if(state != null) {}
            runMethod("onClear__" + currentState);

        currentState = state;
        try {
            this.__onStart();
        } catch (Exception e) {
            log.error("Exception in __onStart: {}", this.getClass().getSimpleName(), e.toString());
            e.printStackTrace();
        }
    }

    private void runMethod(String methodName) {
        try {
            if(!methodName.startsWith("onTick"))
                this.log.debug("Calling {}", methodName);
            Method method = this.getClass().getMethod(methodName);
            method.invoke(this);
        } catch(NoSuchMethodException e) {
            this.log.error("Could not find method {}", methodName);
        } catch (Exception e) {
            this.log.error("Could not invoke method {}", methodName);
            e.printStackTrace();
        }
    }
}
