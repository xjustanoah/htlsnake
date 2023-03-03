package at.htl.leoben.windows.games;

import at.htl.leoben.engine.InteractiveWindow;
import at.htl.leoben.engine.Window;
import at.htl.leoben.engine.configurations.AdvancedColor;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.AlignmentVertical;
import at.htl.leoben.engine.configurations.TextStyle;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import at.htl.leoben.engine.configurations.data.AlignmentTypeVertical;
import at.htl.leoben.engine.data.WindowElementItem;
import at.htl.leoben.socket.CharacterReaderThread;
import at.htl.leoben.socket.data.SpecialCharacterKey;

public class MovementWindow extends InteractiveWindow {
    private Window root = new Window(8, 31, TextStyle.getBorderFormat(AdvancedColor.BLUE));
    private final CharacterReaderThread reader;

    public MovementWindow(CharacterReaderThread reader) {
        super(); 
        reader.register(this);
        this.reader = reader;
    }

    String startInfoText = null;
    WindowElementItem el = null;
    WindowElementItem el2 = null;
    WindowElementItem traveler = null;
    WindowElementItem startInfo = null;
    WindowElementItem travelerInfo = null;
    SpecialCharacterKey direction = SpecialCharacterKey.NONE;


    @Override
    public Window getWindow() {
        return root;
    }

    @Override
    public void onStart() throws Exception {
        startInfoText = "Press <enter> to start...";        
        // el.decrementX();
        el = root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER, 2, 0), -1, null, root.formatter.getFormat(AdvancedColor.RED, AdvancedColor.WHITE));
        el2 = root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER, 2, 1), -1, null, root.formatter.getFormat(AdvancedColor.RED, AdvancedColor.WHITE));
        traveler = root.setElement(0, 0, 'Y', root.formatter.getFormat(AdvancedColor.BLACK, AdvancedColor.RED));
        startInfo = root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), new AlignmentVertical(AlignmentTypeVertical.MIDDLE), startInfoText, null);
        travelerInfo = root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), root.getHeight()-2, null, root.formatter.getFormat(AdvancedColor.BLACK, AdvancedColor.BLUE));

    }

    @Override
    public void onTick() throws Exception {
        int key = reader.getKey(this);
        SpecialCharacterKey specialKey = reader.getSpecialKey(this);

        if(specialKey == SpecialCharacterKey.ENTER && direction == SpecialCharacterKey.NONE) {
            direction = SpecialCharacterKey.RIGHT;
            startInfo.setText(null);
        } else if(specialKey == SpecialCharacterKey.ENTER && direction != SpecialCharacterKey.NONE) {
            startInfo.setText(startInfoText);
            direction = SpecialCharacterKey.NONE;
        }


        if(specialKey != SpecialCharacterKey.NONE && specialKey != SpecialCharacterKey.ENTER && direction != SpecialCharacterKey.NONE)
            direction = specialKey;

        if(direction == SpecialCharacterKey.UP)
            traveler.decrementY();                    
        if(direction == SpecialCharacterKey.DOWN)
            traveler.incrementY();
        if(direction == SpecialCharacterKey.LEFT)
            traveler.decrementX();
        if(direction == SpecialCharacterKey.RIGHT)
            traveler.incrementX();

        traveler.modX(root.getWidth()-2);
        traveler.modY(root.getHeight()-2);
        travelerInfo.setText("x:" + traveler.getX() + "/y:" + traveler.getY());

        el.setText("Key: " + key);
        el2.setText("SpecialKey: " + specialKey);

    }
}
