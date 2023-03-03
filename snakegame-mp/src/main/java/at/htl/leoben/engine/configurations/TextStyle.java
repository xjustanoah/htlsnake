package at.htl.leoben.engine.configurations;

import static org.fusesource.jansi.Ansi.*;
import org.fusesource.jansi.Ansi.Attribute;

public class TextStyle {
    private AdvancedColor defaultBackgroundColor;

    public TextStyle() {
        this.defaultBackgroundColor = AdvancedColor.BLACK;
    }

    public TextStyle(AdvancedColor defaultBackgroundColor) {
        this.defaultBackgroundColor = defaultBackgroundColor;
    }

    public String getFormat(AdvancedColor foregroundColor) {
        
        return fg(foregroundColor) + bg(defaultBackgroundColor);
    }

    public String getFormat(AdvancedColor foregroundColor, AdvancedColor backgroundColor) {
        return fg(foregroundColor) + bg(backgroundColor);
    }

    public String getFormat(AdvancedColor foregroundColor, AdvancedColor backgroundColor, Attribute attribute) {
        return fg(foregroundColor) + bg(backgroundColor) + ansi().a(attribute).toString();
    }

    public String getFormat(AdvancedColor foregroundColor, Attribute attribute, String additionalFormat) {
        return fg(foregroundColor) + bg(defaultBackgroundColor) + ansi().a(attribute).toString() + additionalFormat;
    }

    public String getFormat(AdvancedColor foregroundColor, Attribute attribute) {
        return fg(foregroundColor) + bg(defaultBackgroundColor) + ansi().a(attribute).toString();
    }


    public String getFormat(Attribute attribute) {
        return ansi().a(attribute) + bg(defaultBackgroundColor).toString() + fg(AdvancedColor.WHITE);
    }

    public String getFormat(Attribute attribute, String additionalFormat) {
        return ansi().a(attribute) + additionalFormat;
    }

    public String getDefaultFormat() {
        return bg(defaultBackgroundColor) + fg(AdvancedColor.WHITE);
    }

    public static String getBorderFormat(AdvancedColor foregroundColor, AdvancedColor backgroundColor) {
        return fg(foregroundColor) + bg(backgroundColor);
    }


    public static String getBorderFormat(AdvancedColor backgroundColor) {
        return bg(backgroundColor);
    }

    public static String fg(AdvancedColor color) {
        return "\033[38;5;" + color.getValue() + "m";
    }

    public static String bg(AdvancedColor color) {
        return "\033[48;5;" + color.getValue() + "m";
    }
}
