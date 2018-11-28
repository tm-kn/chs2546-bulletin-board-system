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

    public void connectToJavaSpace() throws Exception {
		javaSpace = SpaceUtils.getSpace();
		if (javaSpace == null){
            throw new Exception("Java space not found");
		}
    }

    public void authenticateUser(String username) {
        this.username = username;
    }

    public Topic[] getTopicList() {
        TopicLastID lastID = getLastTopicID();
        List<Topic> topicList = new ArrayList<Topic>();
        for(int i = lastID.getLastID(); i > 0 ; i--) {
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
            if(lastID == null) {
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
        if(lastID == null) {
            return null;
        }
        try {
            lastID = (TopicLastID) javaSpace.take(lastID, null, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastID.increment();
        Topic topic = new Topic(lastID.getLastID(), title, content, getUserId());
        try {
            javaSpace.write(topic, null, Lease.FOREVER);
            System.out.println(
                "Published topic ID " + String.valueOf(topic.id) + ", " + topic.title
            );
            Post post = new Post(1, topic.id, getUserId(), content);
            try {
                javaSpace.write(post, null, Lease.FOREVER);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
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

    public Topic getTopicOfID(int id) {
        Topic topic = new Topic(id);
        try {
            topic = (Topic) javaSpace.read(topic, null, 1000);
            List<Post> postList = new ArrayList<Post>();

            for(int i = 1; i <= topic.getLastPostID(); i++) {
                try {
                    Post postTemplate = new Post(i, topic.id);
                    postList.add((Post) javaSpace.read(postTemplate, null, 1000));
                    System.out.println(postList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            topic.setPostList(postList.toArray(new Post[postList.size()]));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        return topic;
    }
}
