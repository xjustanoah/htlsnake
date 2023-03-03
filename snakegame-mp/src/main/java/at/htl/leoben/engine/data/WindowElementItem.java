package at.htl.leoben.engine.data;

import at.htl.leoben.engine.configurations.AlignmentHorizontal;
import at.htl.leoben.engine.configurations.AlignmentVertical;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WindowElementItem {
    private int x;
    private int y;
    private String text;
    private String style;
    private AlignmentHorizontal alignmentX;
    private AlignmentVertical alignmentY;
    private boolean borderWritingAllowed = false;

    public int incrementX() {
        return ++x;
    }


    public int decrementX() {
        return --x;
    }

    public int modX(int mod) {
        x = (x + mod) % mod;
        return x;
    }

    public int incrementY() {
        return ++y;
    }

    public int decrementY() {
        return --y;
    }

    public int modY(int mod) {
        y = (y + mod) % mod;
        return y;
    }
}
