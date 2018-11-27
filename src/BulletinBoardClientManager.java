import java.util.ArrayList;
import java.util.List;
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
        TopicLastID lastID = getLastTopicID();
        List<Topic> topicList = new ArrayList<Topic>();
        for (int i = lastID.getLastID(); i > 0 ; i--) {
            try {
                Topic topicTemplate = new Topic(i);
                topicList.add((Topic) javaSpace.read(topicTemplate, null, 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return topicList.toArray(new Topic[topicList.size()]);
    }

    public TopicLastID getLastTopicID() {
        TopicLastID template = new TopicLastID();
        try {
            TopicLastID lastID = (TopicLastID) javaSpace.readIfExists(
                template,
                null,
                1000
            );
            if (lastID == null) {
                try {
                    lastID = new TopicLastID(0);
                    javaSpace.write(lastID, null, Lease.FOREVER);
                    System.out.println("Written TopicLastID object.");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return lastID;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Topic addNewTopic(String title, String content) {
        TopicLastID lastID = getLastTopicID();
        if (lastID == null) {
            return null;
        }
        try {
            lastID = (TopicLastID) javaSpace.take(lastID, null, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(lastID.lastID);
        lastID.increment();
        System.out.println(lastID.lastID);
        Topic topic = new Topic(lastID.getLastID(), title, content, getUserId());
        try {
            javaSpace.write(topic, null, Lease.FOREVER);
            System.out.println(
                "Published topic ID " + String.valueOf(topic.id) + ", " + topic.title
            );
            try {
                javaSpace.write(lastID, null, Lease.FOREVER);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        return topic;
    }
}
