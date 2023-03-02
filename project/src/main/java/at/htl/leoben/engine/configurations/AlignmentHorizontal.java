package at.htl.leoben.engine.configurations;

import at.htl.leoben.engine.configurations.data.AlignmentTypeHorizontal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlignmentHorizontal {
    private final AlignmentTypeHorizontal type;
    private final int columns;
    private final int index;

    public AlignmentHorizontal(AlignmentTypeHorizontal type, int columns, int index) {
        this.type = type;
        this.columns = columns;
        this.index = index;
    }

    public AlignmentHorizontal(AlignmentTypeHorizontal type) {
        this(type, 1, 0);
    }
}
