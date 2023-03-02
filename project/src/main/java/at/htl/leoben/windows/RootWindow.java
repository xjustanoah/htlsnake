package at.htl.leoben.windows;

import java.util.Arrays;
import at.htl.leoben.engine.InteractiveWindow;
import at.htl.leoben.engine.RenderEngine;
import at.htl.leoben.engine.Window;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.AlignmentVertical;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import at.htl.leoben.engine.configurations.data.AlignmentTypeVertical;
import at.htl.leoben.socket.CharacterReaderThread;
import at.htl.leoben.socket.data.SpecialCharacterKey;
import at.htl.leoben.windows.tools.UserInputWindow;
import at.htl.leoben.windows.tools.inputFilter.AlphaNumericFilter;
import at.htl.leoben.windows.tools.inputModifier.UpperCaseModifier;
import at.htl.leoben.windows.games.GameWindowBase;
import at.htl.leoben.windows.games.SnakeGame;
import at.htl.leoben.windows.tools.AlertWindow;
import at.htl.leoben.windows.tools.MenuWindow;

public class RootWindow extends InteractiveWindow {
    private final CharacterReaderThread reader;
    private Window root = new Window(32, 64);

    public RootWindow(CharacterReaderThread reader) {
        super();
        this.reader = reader;
    }

    @Override
    public Window getWindow() {
        return root;
    }

    @Override
    public void onStart() {
        switchState("menu");
    }

    /*
     * ### menu STATE
     */

    private MenuWindow menuWindow = null;
 
    public void onStart__menu() {
        String menuItems[] = {"Start Game", "Join Game"};

        menuWindow = new MenuWindow(reader, menuItems, "Select what to do");
        Window win = RenderEngine.register(menuWindow);
        if(win != null)
            root.setWindow(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), new AlignmentVertical(AlignmentTypeVertical.MIDDLE), win);
    }

    public void onTick__menu() {
        SpecialCharacterKey key = reader.getSpecialKey(this);
        if(key == SpecialCharacterKey.ENTER) {
            int selection = menuWindow.getExchangeableData();
            if(selection == 0)
                switchState("create_game");
            if(selection == 1)
                switchState("join_game");
        }
    }

    public void onClear__menu() {
        root.delWindow(menuWindow.getWindow());
        RenderEngine.unregister(menuWindow);
    }

    /*
     * ### join_game STATE
     */

    private UserInputWindow userInputWindow = null;

    public void onStart__join_game() {
        userInputWindow = new UserInputWindow(reader, "Enter Game ID", new AlphaNumericFilter(), new UpperCaseModifier());
        RenderEngine.register(userInputWindow);
        Window win = userInputWindow.getWindow();
        if(win != null)
            root.setWindow(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), new AlignmentVertical(AlignmentTypeVertical.MIDDLE), win);
    }

    public void onTick__join_game() {
        SpecialCharacterKey key = reader.getSpecialKey(this);
        if(key == SpecialCharacterKey.ENTER) {
            String gameID = userInputWindow.getExchangeableData();
            GameWindowBase<?> window = WindowManager.getWindow(gameID);
            if(window == null) {
                switchState("join_game_invalid_game_id");
            } else {
                game = window;
                switchState("create_game");
            }
        }
        if(key == SpecialCharacterKey.ESCAPE)
            switchState("menu");
    }

    public void onClear__join_game() {
        root.delWindow(userInputWindow.getWindow());
        RenderEngine.unregister(userInputWindow);
    }

    /*
     * ### join_game_invalid_game_id STATE
     */

    private AlertWindow alertWindow = null;

    public void onStart__join_game_invalid_game_id() {
        alertWindow = new AlertWindow("Invalid GameID", Arrays.asList("The provided GameID is invalid", "Please try again."));
        RenderEngine.register(alertWindow);
        Window win = alertWindow.getWindow();
        if(win != null)
            root.setWindow(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), new AlignmentVertical(AlignmentTypeVertical.MIDDLE), win);
    }

    public void onTick__join_game_invalid_game_id() {
        SpecialCharacterKey key = reader.getSpecialKey(this);
        if(key == SpecialCharacterKey.ENTER) {
            switchState("join_game");
        }
    }

    public void onClear__join_game_invalid_game_id() {
        root.delWindow(alertWindow.getWindow());
        RenderEngine.unregister(alertWindow);
    }

    /*
     * ### create_game STATE
     */

    private GameWindowBase<?> game = null;

    public void onStart__create_game() {
        if(game == null)
            game = new SnakeGame();
        game.addPlayer(reader);
        Window win = RenderEngine.register(game);
        if(win != null)
            root.setWindow(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), new AlignmentVertical(AlignmentTypeVertical.MIDDLE), win);
    }

    public void onTick__create_game() {

    }

    public void onClear__create_game() {
        root.delWindow(game.getWindow());
        RenderEngine.unregister(game);
        game = null;
    }

    @Override
    public void onTick() throws Exception {
   
    }
}
