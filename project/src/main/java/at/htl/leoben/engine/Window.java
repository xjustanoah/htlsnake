package at.htl.leoben.engine;

import static org.fusesource.jansi.Ansi.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import at.htl.leoben.engine.configurations.AdvancedColor;
import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.AlignmentVertical;
import at.htl.leoben.engine.configurations.TextStyle;
import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import at.htl.leoben.engine.data.WindowElementItem;
import at.htl.leoben.logging.Manager;
import at.htl.leoben.windows.style.DefaultStyle;
import lombok.Getter;

public class Window {
    private final Logger log = Manager.getLogger(this);
    
    @Getter
    private final int height;
    @Getter
    private final int width;
    private HashMap<PrintStream,String[]> currentTiles = new HashMap<>();
    @Getter
    private String[] tiles;
    private HashMap<Window,Integer> subWindows = new HashMap<>();
    private ArrayList<WindowElementItem> textElements = new ArrayList<>();
    private String borderChar;
    private String borderStyle;
    public TextStyle formatter;

    public Window(int height, int width, String borderStyle) {
        this(height, width);
        this.borderStyle = borderStyle;
    }

    public Window(int height, int width, String borderStyle, AdvancedColor backgroundColor) {
        this(height, width, borderStyle);
        this.formatter = new TextStyle(backgroundColor);
    }

    public Window(int height, int width) {
        log.debug("Creating new window with h={},w={}", height, width);
        this.borderChar = null;
        this.borderStyle = null;
        this.height = height;
        this.width = width;
        int maxlen = this.height*this.width;
        this.tiles = new String[maxlen];
        this.formatter = new TextStyle(DefaultStyle.backgroundColor);
        this.borderStyle = DefaultStyle.borderFormat;
    }

    public void initTiles() {
        int maxlen = this.height*this.width;
        String initChar = formatter.getDefaultFormat() + " " + ansi().reset();
        for(int pos = 0; pos < maxlen; pos++)
            this.tiles[pos] = initChar;
    }

    public void setWindow(AlignmentHorizontal x, AlignmentVertical y, Window window) {
        int aligned_x = getAlignmentIndex(x, window.getWidth());
        int aligned_y = getAlignmentIndex(y, window.getHeight());
        this.setWindow(aligned_x, aligned_y, window);
    }

    public void setWindow(AlignmentHorizontal x, int y, Window window) {
        int aligned_x = getAlignmentIndex(x, window.getWidth());
        this.setWindow(aligned_x, y, window);
    }

    public void setWindow(int x, AlignmentVertical y, Window window) {
        int aligned_y = getAlignmentIndex(y, window.getHeight());
        this.setWindow(x, aligned_y, window);
    }

    public void setWindow(int x, int y, Window window) {
        int pos = y * this.width + x;
        this.subWindows.put(window, pos);
    }

    public void delWindow(Window window) {
        if(subWindows.containsKey(window))
            this.subWindows.remove(window);
    }

    private void drawWindows() {
        for (Entry<Window, Integer> windowEntry : subWindows.entrySet())
            this.drawWindow(windowEntry.getValue(),windowEntry.getKey());
    }

    private void drawWindow(int window_pos, Window window) {
        window.predraw();
        int x = Math.floorMod(window_pos, this.width);
        int y = window_pos / this.width;
        String win_tiles[] = window.getTiles();
        for(int pos = 0; pos < win_tiles.length; pos++) {
            int win_x = Math.floorMod(pos, window.getWidth());
            int win_y = pos / window.getWidth();
            this.print(win_x+x, win_y+y, win_tiles[pos]);
        }
    }

    public void border() {
        if(borderStyle == null) borderStyle = TextStyle.getBorderFormat(DefaultStyle.backgroundColor);
        if(borderChar == null) borderChar = " ";

        final String rowBorderIcon = borderStyle + borderChar + ansi().reset().toString();
        final String columnBorderIcon = borderStyle + borderChar + ansi().reset().toString();

        // Top - Bottom
        for(int x = 0; x < this.width; x++) {
            this.tiles[x] = rowBorderIcon;
            this.tiles[(this.height-1)*width + x] = rowBorderIcon;
        }

        // Left - Right
        for(int y = 0; y < this.height; y++) {
            this.tiles[y * this.width] = columnBorderIcon;
            this.tiles[(y + 1) * this.width - 1] = columnBorderIcon;
        }
    }

    public void print(int x, int y, String value) {
        this.print(x, y, value, true);
    }


    public void print(int x, int y, String value, boolean allowBorderWriting) {
        // Allow print on border with y = -1
        if(x >= 0 && x < this.width - 2 && y >= (allowBorderWriting ? -1 : 0) && y < this.height - (allowBorderWriting ? 1 : 2))
            this.tiles[(y+1)*this.width + x + 1] = value;
    }

    public WindowElementItem setText(int x, int y, String text, String config) {
        WindowElementItem reference = new WindowElementItem(x, y, text, config, null, null, true);
        textElements.add(reference);
        return reference;
    }

    public WindowElementItem setText(AlignmentHorizontal x, int y, String text, String textConfig) {
        WindowElementItem reference = new WindowElementItem(0, y, text, textConfig, x, null, true);
        textElements.add(reference);
        return reference;
    }

    public WindowElementItem setText(int x, AlignmentVertical y, String text, String textConfig) {
        WindowElementItem reference = new WindowElementItem(x, 0, text, textConfig, null, y, true);
        textElements.add(reference);
        return reference;
    }

    public WindowElementItem setText(AlignmentHorizontal x, AlignmentVertical y, String text, String textConfig) {
        WindowElementItem reference = new WindowElementItem(0, 0, text, textConfig, x, y, true);
        textElements.add(reference);
        return reference;
    }

    public WindowElementItem setTitle(String text) {
        return setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), -1, text, formatter.getFormat(AdvancedColor.WHITE, Attribute.INTENSITY_BOLD, DefaultStyle.borderFormat));
    }

    public WindowElementItem setTitle(String text, String style) {
        return setText(new AlignmentHorizontal(AlignmentTypeHorizontal.CENTER), -1, text, style);
    }

    public WindowElementItem setElement(int x, int y, char c, String style) {
        WindowElementItem item = this.setText(x, y, String.valueOf(c), style);
        item.setBorderWritingAllowed(false);
        return item;
    }

    public void delText(WindowElementItem item) {
        if(textElements.contains(item))
            textElements.remove(item);
    }

    public void drawTexts() {
        for (WindowElementItem textElement : textElements)
            if(textElement.getText() != null && textElement.getText().length() > 0)
                drawText(textElement);
    }

    public void drawText(WindowElementItem element) {
        int x = element.getAlignmentX() == null ? element.getX() : getAlignmentIndex(element.getAlignmentX(), element.getText().length());
        int y = element.getAlignmentY() == null ? element.getY() : getAlignmentIndex(element.getAlignmentY(), 1);
        String text = element.getText();
        String textStyle = element.getStyle();
        String data[] = text.split("");
        String prefix = textStyle == null ? formatter.getDefaultFormat() : textStyle;
        String suffix = ansi().reset().toString();
        this.print(x, y, prefix + data[0] + suffix, element.isBorderWritingAllowed());
        for(int i = 1; i < data.length - 1; i++)
            this.print(x + i, y, prefix + data[i] + suffix, element.isBorderWritingAllowed());
        this.print(x + data.length - 1, y, prefix + data[data.length - 1] + suffix, element.isBorderWritingAllowed());
    }


    private int getAlignmentIndex(AlignmentVertical alignment, int elementLength) {
        int columns = alignment.getColumns();
        int index = alignment.getIndex();
        int column_length = (this.getHeight() - 2) / columns;
        int start = index * column_length;
        int end = start + column_length;
        int x = 0;
        switch (alignment.getType()) {
            case TOP:
                x = start;
                break;
            case MIDDLE:
                x = start + (column_length - elementLength) / 2;
                break;
            case BOTTOM:
                x = end - elementLength;
                break;
            default:
                x = 0;
                break;
        }
        return x;
    }

    private int getAlignmentIndex(AlignmentHorizontal alignment, int elementLength) {
        int columns = alignment.getColumns();
        int index = alignment.getIndex();
        int column_length = (this.getWidth() - 2) / columns;
        int start = index * column_length;
        int end = start + column_length;
        int x = 0;
        switch (alignment.getType()) {
            case LEFT:
                x = start;
                break;
            case CENTER:
                x = start + (column_length - elementLength) / 2;
                break;
            case RIGHT:
                x = end - elementLength;
                break;
            default:
                x = 0;
                break;
        }
        return x;
    }

    public synchronized void predraw() {
        this.initTiles();
        this.drawWindows();
        this.border();
        this.drawTexts();
    }

    public void hideCursor(PrintStream stream) {
        stream.print("\033[?25l");
    }

    public void clearScreen(PrintStream stream) {
        stream.print(ansi().eraseScreen());
    }

    public synchronized void registerStream(PrintStream stream) {
        this.clearScreen(stream);
        this.hideCursor(stream);
        int maxlen = this.height*this.width;
        String[] currentTile = new String[maxlen];
        currentTiles.put(stream, currentTile);
        for(int pos = 0; pos < maxlen; pos++)
            currentTile[pos] = "";
    }

    public synchronized void unregisterStream(PrintStream stream) {
        currentTiles.remove(stream);
    }

    public synchronized void draw() {
        this.predraw();
        for (Entry<Window,Integer> windowEntry : subWindows.entrySet())
            windowEntry.getKey().draw();
        for (Entry<PrintStream,String[]> entry : currentTiles.entrySet()) {
            PrintStream stream = entry.getKey();
            String[] currentTile = entry.getValue();
            for(int pos = 0; pos < this.width * this.height; pos++) {
                if(!tiles[pos].equals(currentTile[pos])) {
                    int x = Math.floorMod(pos, this.width);
                    int y = pos / this.width;
                    stream.print(ansi().cursor(y + 1, x + 1));
                    stream.print(tiles[pos]);
                    currentTile[pos] = tiles[pos];
                }
            }                
        }
    }
}
