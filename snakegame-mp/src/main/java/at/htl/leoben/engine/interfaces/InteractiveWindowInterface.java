package at.htl.leoben.engine.interfaces;

import at.htl.leoben.engine.Window;

public interface InteractiveWindowInterface {
    public Window getWindow();
    public void onStart() throws Exception;
    public void onTick() throws Exception;
}
