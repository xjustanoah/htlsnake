package at.htl.leoben.windows.tools;

import at.htl.leoben.engine.TalkyInteractiveWindow;
import at.htl.leoben.engine.Window;
import at.htl.leoben.engine.configurations.AdvancedColor;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import at.htl.leoben.engine.data.WindowElementItem;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.socket.CharacterReaderThread;
import at.htl.leoben.socket.data.SpecialCharacterKey;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi.Attribute;

public class MenuWindow extends TalkyInteractiveWindow<Integer> {
    private final Logger log = Manager.getLogger(this);
    private final CharacterReaderThread reader;
    private final Window root;
    private final String infoText;

    private String selectBoxes[];

    WindowElementItem windowSelectBoxes[] = null;
    WindowElementItem windowSelector = null;
    int selectorIndex = 0;

    public MenuWindow(CharacterReaderThread reader, String selectBoxes[], String infoText) {
        reader.register(this);
        this.reader = reader;
        this.selectBoxes = selectBoxes;
        this.infoText = infoText;
        int maxWidth = infoText.length();
        for (String selectBox : selectBoxes)
            if(maxWidth < selectBox.length())
                maxWidth = selectBox.length();
        this.root = new Window(selectBoxes.length + 3, 2 * maxWidth, null);
    }

    @Override
    public Window getWindow() {
        return root;
    }

    @Override
    public void onStart() throws Exception {
        setExchangeableData(selectorIndex);
        root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), 0, infoText, root.formatter.getFormat(AdvancedColor.WHITE, Attribute.INTENSITY_BOLD));
        windowSelectBoxes = new WindowElementItem[selectBoxes.length];
        windowSelector = root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.RIGHT, 3, 0), selectorIndex+1, "[x] ", null);
        for(int i = 0; i < selectBoxes.length; i++)
            windowSelectBoxes[i] = root.setText(
                new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER, 3, 1), 
                i+1,
                selectBoxes[i], 
                null
            );
    }

    @Override
    public void onTick() throws Exception {
        SpecialCharacterKey key = reader.getSpecialKey(this);
        if(key == SpecialCharacterKey.DOWN) {
            selectorIndex = (selectorIndex + 1 + selectBoxes.length) % selectBoxes.length;
            setExchangeableData(selectorIndex);
        }
        if(key == SpecialCharacterKey.UP) {
            selectorIndex = (selectorIndex - 1 + selectBoxes.length) % selectBoxes.length;
            setExchangeableData(selectorIndex);
        }
        
        if(key == SpecialCharacterKey.DOWN || key == SpecialCharacterKey.UP)
            log.trace("Selection changed: " + selectorIndex);

        for(int i = 0; i < selectBoxes.length; i++) {
            WindowElementItem item = windowSelectBoxes[i];
            if(i == selectorIndex) {
                item.setStyle(root.formatter.getFormat(Attribute.UNDERLINE));
                windowSelector.setY(selectorIndex+1);
            } else {
                item.setStyle(null);
            }
        }
    }
}
