import net.jini.core.entry.*;


public class Topic implements Entry {
    private Integer id;
    private String title;
    private Integer ownerId;

    public Topic() {}

    public Topic(int id, String title, String content, Integer ownerId) {
        this.id = id;
        this.title = title;
        this.ownerId = ownerId;
    }

    public String toString() {
        return title;
    }
}
