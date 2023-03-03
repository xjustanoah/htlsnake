package at.htl.leoben.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.engine.InteractiveWindow;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.socket.data.SpecialCharacterKey;

public class CharacterReaderThread extends Thread {
    private final Logger log;
    private final InputStream stream;
    private HashMap<InteractiveWindow, Integer> key = new HashMap<>();
    private HashMap<InteractiveWindow, SpecialCharacterKey> specialKey = new HashMap<>();

    public CharacterReaderThread(Socket socket) throws IOException {
        this.stream = socket.getInputStream();
        this.log = Manager.getLogger(this.getClass().getSimpleName() + "@"+ socket.getRemoteSocketAddress().toString());
        setName("CharacterReader");
    }

    public synchronized void register(InteractiveWindow window) {
        key.put(window, 0);
        specialKey.put(window, SpecialCharacterKey.NONE);
    }

    @Override
    public void run() {
        super.run();
        try {
            // Read trash before actual input
            while(stream.available() > 0)
                stream.read();
            int character = -1;

            // // Only for debugging new keys
            // while((character = stream.read()) != -1)
            //     System.out.println(character);

            while((character = stream.read()) != -1) {
                if(character == 0x1B) {
                    if(stream.available() == 0) {
                        updateSpecialKey(SpecialCharacterKey.ESCAPE);
                        continue;
                    }
                    byte escapedSequence[] = stream.readNBytes(stream.available());
                    if(escapedSequence.length != 2) continue;

                    if(escapedSequence[0] == 0x5B) {
                        switch (escapedSequence[1]) {
                            case 65: // UP
                                updateSpecialKey(SpecialCharacterKey.UP);
                                break;
                            case 66: // DOWN
                                updateSpecialKey(SpecialCharacterKey.DOWN);
                                break;
                            case 67: // RIGHT
                                updateSpecialKey(SpecialCharacterKey.RIGHT);
                                break;
                            case 68: // LEFT
                                updateSpecialKey(SpecialCharacterKey.LEFT);
                                break;
                            default:
                                break;
                        }
                    }
                }
                // 0xD is normal Enter, 0xA is numpad enter
                if(character == 0xD || character == 0xA)
                    updateSpecialKey(SpecialCharacterKey.ENTER);
                if(character == 127)
                    updateSpecialKey(SpecialCharacterKey.BACKSPACE);
                // if(character == 27)
                //     updateSpecialKey(SpecialCharacterKey.ESCAPE);
                if(character >= 0x20 && character <= 126)
                    updateKey(character);
            }    
        } catch (Exception e) {
            log.info("Exception during input read:", e.toString());
            return;
        }
    }

    public void updateKey(int value) {
        log.trace("User Input: {} ({})", value, (char)value);
        for (Entry<InteractiveWindow, Integer> entry : key.entrySet())
            entry.setValue(value);
    }

    public void updateSpecialKey(SpecialCharacterKey value) {
        log.trace("User Input: {}", value);
        for (Entry<InteractiveWindow, SpecialCharacterKey> entry : specialKey.entrySet())
            entry.setValue(value);
    }

    public synchronized int getKey(InteractiveWindow window) {
        int returnKey = key.get(window);
        key.put(window, 0);
        return returnKey;
    }

    public synchronized SpecialCharacterKey getSpecialKey(InteractiveWindow window) {
        SpecialCharacterKey returnSpecialKey = specialKey.get(window);
        specialKey.put(window, SpecialCharacterKey.NONE);
        return returnSpecialKey;
    }
}
