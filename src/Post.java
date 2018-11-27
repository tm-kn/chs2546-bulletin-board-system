import net.jini.core.entry.*;

public class Post implements Entry {
    public Integer id;
    public Integer topicID;
    public Integer authorID;
    public String content;

    public Post() {}

    public Post(int id, int topicID, int authorID, String content) {
        this.id = id;
        this.topicID = topicID;
        this.authorID = authorID;
        this.content = content;
    }
}
