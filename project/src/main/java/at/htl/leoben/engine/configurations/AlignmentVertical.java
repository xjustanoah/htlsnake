package at.htl.leoben.engine.configurations;

import at.htl.leoben.engine.configurations.data.AlignmentTypeVertical;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AlignmentVertical {
    private final AlignmentTypeVertical type;
    private final int columns;
    private final int index;

    public AlignmentVertical(AlignmentTypeVertical type, int columns, int index) {
        this.type = type;
        this.columns = columns;
        this.index = index;
    }

    public AlignmentVertical(AlignmentTypeVertical type) {
        this(type, 1, 0);
    }
}
