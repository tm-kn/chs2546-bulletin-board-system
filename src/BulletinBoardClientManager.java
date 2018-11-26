import net.jini.core.lease.*;
import net.jini.space.JavaSpace;


public class BulletinBoardClientManager {
    private JavaSpace javaSpace;
    private String username;

    public int getUserId() {
        return 1;
    }

    public void connectToJavaSpace() {
		javaSpace = SpaceUtils.getSpace();
		if (javaSpace == null){
            throw new JavaSpaceNotFoundException();
		}
    }

    public void authenticateUser(String username) {
        this.username = username;
    }

    public Topic[] getTopicList() {
        return new Topic[]{};
    }

    public int getNewTopicID() {
        return 0;
    }

    public Topic addNewTopic(String title, String content) {
        int id = getNewTopicID();
        Topic topic = new Topic(id, title, content, getUserId());
        try {
            javaSpace.write(topic, null, Lease.FOREVER);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return topic;
    }
}
