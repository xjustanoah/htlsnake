package at.htl.leoben.windows.games;

import java.util.ArrayList;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi.Attribute;
import at.htl.leoben.engine.TalkyInteractiveWindow;
import at.htl.leoben.engine.Window;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.AlignmentVertical;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import at.htl.leoben.engine.configurations.data.AlignmentTypeVertical;
import at.htl.leoben.engine.data.WindowElementItem;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.socket.CharacterReaderThread;
import at.htl.leoben.socket.data.SpecialCharacterKey;
import at.htl.leoben.windows.WindowManager;

public abstract class GameWindowBase<T> extends TalkyInteractiveWindow<T> {
    private final Logger log = Manager.getLogger(this);
    private final int requiredPlayers;

    public GameWindowBase(int requiredPlayers) {
        String gameID = getGameID();
        WindowManager.registerWindow(gameID, this);
        this.requiredPlayers = requiredPlayers;
        __setState("waitingForPlayers");
     }

    private ArrayList<CharacterReaderThread> reader = new ArrayList<>();

    public boolean isReady() {
        return reader.size() == requiredPlayers;
    }

    public void addPlayer(CharacterReaderThread playerReader) {
        playerReader.register(this);
        reader.add(playerReader);
    }

    public int[] getInputs() {
        int inputs[] = new int[reader.size()];
        for(int i = 0; i < reader.size(); i++)
            inputs[i] = getInput(i);
        return inputs;
    }

    public int getInput(int player) {
        if(reader == null) {
            this.log.error("CharacterReaderThread is empty. Cannot get input.");
            return 0;
        }
        return reader.get(player).getKey(this);
    }

    public SpecialCharacterKey[] getSpecialInputs() {
        SpecialCharacterKey inputs[] = new SpecialCharacterKey[reader.size()];
        for(int i = 0; i < reader.size(); i++)
            inputs[i] = getSpecialInput(i);
        return inputs;
    }

    public SpecialCharacterKey getSpecialInput(int player) {
        if(reader == null) {
            this.log.error("CharacterReaderThread is empty. Cannot get special input.");
            return SpecialCharacterKey.NONE;
        }
        return reader.get(player).getSpecialKey(this);
    }

    private WindowElementItem titleElement;
    private WindowElementItem waitingForPlayersElement;
    private String waitingForPlayers = "Waiting for players";
    private int dots;

    public void onStart__waitingForPlayers() {
        Window root = getWindow();
        if(root == null) {
            log.error("Root window is null");
            return;
        }
        titleElement = root.setTitle("GameID: " + getGameID());
        waitingForPlayersElement = root.setText(
            new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), 
            new AlignmentVertical(AlignmentTypeVertical.MIDDLE), 
            waitingForPlayers,
            root.formatter.getFormat(Attribute.INTENSITY_BOLD)
        );
    }

    private int keepTicks = 5;
    private int amountOfDots = 4;

    public void onTick__waitingForPlayers() {
        if(isReady())
            switchState(null);
        waitingForPlayersElement.setText(waitingForPlayers + " " + ".".repeat((int)Math.floor(dots/keepTicks)));
        dots = (dots + 1)% (amountOfDots * keepTicks);
    }

    public void onClear__waitingForPlayers() {
        Window root = getWindow();
        root.delText(titleElement);
        root.delText(waitingForPlayersElement);
    }

    private String gameID = generateGameID();

    private String generateGameID() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private String getGameID() {
        return gameID;
    }
}
