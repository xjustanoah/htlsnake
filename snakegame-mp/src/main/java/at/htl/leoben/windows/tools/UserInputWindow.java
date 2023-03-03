package at.htl.leoben.windows.tools;

import org.fusesource.jansi.Ansi.Attribute;
import at.htl.leoben.engine.TalkyInteractiveWindow;
import at.htl.leoben.engine.Window;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import at.htl.leoben.engine.data.WindowElementItem;
import at.htl.leoben.socket.CharacterReaderThread;
import at.htl.leoben.socket.data.SpecialCharacterKey;
import at.htl.leoben.windows.tools.inputFilter.InputFilter;
import at.htl.leoben.windows.tools.inputModifier.InputModifier;

public class UserInputWindow extends TalkyInteractiveWindow<String> {
    private final CharacterReaderThread reader;
    private final Window root;
    private final String infoText;
    private InputFilter filter;
    private InputModifier modifier;
    private String userInput = "";
    private WindowElementItem userInputElement;

    public UserInputWindow(CharacterReaderThread reader, String infoText) {
        super();
        reader.register(this);
        this.reader = reader;
        this.infoText = infoText;
        this.root = new Window(5, 32);
    }

    public UserInputWindow(CharacterReaderThread reader, String infoText, InputFilter filter) {
        this(reader, infoText);
        this.filter = filter;
    }

    public UserInputWindow(CharacterReaderThread reader, String infoText, InputFilter filter, InputModifier modifier) {
        this(reader, infoText, filter);
        this.modifier = modifier;
    }

    @Override
    public Window getWindow() {
        return root;
    }

    @Override
    public void onStart() throws Exception {
        setExchangeableData(userInput);
        root.setTitle(infoText);
        userInputElement = root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), 1, null, root.formatter.getFormat(Attribute.INTENSITY_BOLD));
    }

    @Override
    public void onTick() throws Exception {
        char key = (char)reader.getKey(this);
        SpecialCharacterKey specialKey = reader.getSpecialKey(this);

        if(specialKey == SpecialCharacterKey.BACKSPACE && userInput.length() > 0) {
            userInput = userInput.substring(0, userInput.length() - 1);
            userInputElement.setText(userInput);
            setExchangeableData(userInput);
        }

        boolean condition = filter != null ? filter.filter(key) : key > 0;
        if(condition) {
            char modifiedKey = modifier == null ? key : modifier.modify(key);
            userInput += modifiedKey;
            userInputElement.setText(userInput);
            setExchangeableData(userInput);
        }
    }

    public void resetInput() {
        userInput = "";
    }
}
