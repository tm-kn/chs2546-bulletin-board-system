import net.jini.core.entry.*;


public class Topic implements Entry {
    public Integer id;
    public String title;
    public Integer ownerId;
    public Integer lastPostID = 1;
    private Post[] postList;

    public Topic() {}

    public Topic(int id) {
        this.id = id;
    }

    public Integer getLastPostID() {
        return lastPostID;
    }

    public Topic(int id, String title, String content, Integer ownerId) {
        this.id = id;
        this.title = title;
        this.ownerId = ownerId;
    }

    public void incrementLastPostID() {
        lastPostID++;
    }

    public String toString() {
        return title;
    }

    public Post[] getPostList() {
        return postList;
    }

    public void setPostList(Post[] postList) {
        this.postList = postList;
    }
}
