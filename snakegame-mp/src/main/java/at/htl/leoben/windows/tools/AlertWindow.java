package at.htl.leoben.windows.tools;

import java.util.List;
import org.fusesource.jansi.Ansi.Attribute;
import at.htl.leoben.engine.InteractiveWindow;
import at.htl.leoben.engine.Window;
import at.htl.leoben.engine.configurations.AdvancedColor;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.TextStyle;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;

public class AlertWindow extends InteractiveWindow {
    private Window root;
    private final String title;
    private final List<String> messages;

    private final String borderFormat = TextStyle.getBorderFormat(AdvancedColor.COLOR166);
    private final AdvancedColor backgroundColor = AdvancedColor.SANDYBROWN;

    public AlertWindow(String title, List<String> messages) {
        super();
        this.title = title;
        this.messages = messages;

        int maxlen = title.length();
        for (String message : messages)
            if(message.length() > maxlen)
                maxlen = message.length();


        // 2 border, 2 padding, 1 button
        root = new Window(messages.size() + 5, maxlen + 4, borderFormat, backgroundColor);
    }

    @Override
    public Window getWindow() {
        return root;
    }

    @Override
    public void onStart() throws Exception {
        root.setTitle(title, root.formatter.getFormat(AdvancedColor.BLACK ,Attribute.INTENSITY_BOLD, borderFormat));
        for(int i = 0; i < messages.size(); i++)
            root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), i+1, messages.get(i), root.formatter.getFormat(AdvancedColor.BLACK));
        root.setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), root.getHeight() - 3, " OK ", root.formatter.getFormat(AdvancedColor.RED, AdvancedColor.WHITE));
    }

    @Override
    public void onTick() throws Exception {
    }
}