package at.htl.leoben.windows.tools.inputFilter;

public class AlphaNumericFilter implements InputFilter {
    @Override
    public boolean filter(char c) {
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }
}
