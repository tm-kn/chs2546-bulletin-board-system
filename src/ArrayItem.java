import net.jini.core.entry.*;


public class ArrayItem implements Entry {
    private Integer index;
    private Integer value;
    private String arrayName;

    public ArrayItem() {}

    public ArrayItem(String arrayName, int index, int value) {
        this.arrayName = arrayName;
        this.index = index;
        this.value = value;
    }
}
