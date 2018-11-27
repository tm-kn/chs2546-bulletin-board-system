import net.jini.core.entry.*;


public class Topic implements Entry {
    public Integer id;
    public String title;
    public Integer ownerId;

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
