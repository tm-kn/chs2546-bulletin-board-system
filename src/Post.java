import net.jini.core.entry.*;
import java.util.Date;


public class Post implements Entry {
    public Integer id;
    public Integer topicID;
    public Integer authorID;
    public String content;
    public Date datetime;

    public Post() {}

    public Post(int id, int topicID) {
        this.id = id;
        this.topicID = topicID;
    }

    public Post(int topicID) {
        this.topicID = topicID;
    }

    public Post(int id, int topicID, int authorID, String content) {
        this.id = id;
        this.topicID = topicID;
        this.authorID = authorID;
        this.content = content;
    }

    public void setCurrentDateTime() {
        datetime = new Date();
    }
}
