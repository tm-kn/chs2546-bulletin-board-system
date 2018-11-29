import net.jini.core.entry.*;


public class Topic implements Entry {
    public Integer id;
    public String title;
    public Integer ownerId;
    public Integer lastPostID;
    public Boolean deleted;
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

    public void setLastPostID(int lastPostID) {
        this.lastPostID = lastPostID;
    }

    public String toString() {
        if (title == null) {
            return "(no title set)";
        }
        return title;
    }

    public Post[] getPostList() {
        return postList;
    }

    public boolean isDeleted() {
        if (deleted == null) {
            return false;
        }
        return deleted;
    }

    public void setPostList(Post[] postList) {
        this.postList = postList;
    }

    public void setDeleted() {
        deleted = true;
        lastPostID = null;
        title = null;
        ownerId = null;
    }
}
