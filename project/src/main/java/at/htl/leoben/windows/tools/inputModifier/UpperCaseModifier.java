package at.htl.leoben.windows.tools.inputModifier;

public class UpperCaseModifier implements InputModifier {
    @Override
    public char modify(char c) {
        return String.valueOf(c).toUpperCase().charAt(0);
    }
}
