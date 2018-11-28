import java.util.ArrayList;
import java.util.List;
import net.jini.core.lease.*;
import net.jini.space.JavaSpace;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.*;


public class BulletinBoardClientManager {
    private JavaSpace javaSpace;
    private String username;
    private TransactionManager transactionManager;

    public int getUserId() {
        return 1;
    }

    public void connectToJavaSpace() throws Exception {
		javaSpace = SpaceUtils.getSpace();
		if (javaSpace == null){
            throw new Exception("Java space not found");
		}
        transactionManager = SpaceUtils.getManager();
        if (transactionManager == null){
            System.err.println("Failed to find the transaction manager");
            System.exit(1);
        }
    }

    public void authenticateUser(String username) {
        this.username = username;
    }

    public Topic[] getTopicList() {
        TopicLastID lastID = getLastTopicID(null);
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

    public TopicLastID getLastTopicID(Transaction transaction) {
        TopicLastID template = new TopicLastID();
        try {
            TopicLastID lastID = (TopicLastID) javaSpace.readIfExists(
                template,
                transaction,
                1000
            );
            if(lastID == null) {
                try {
                    lastID = new TopicLastID(0);
                    javaSpace.write(lastID, transaction, Lease.FOREVER);
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
        Transaction.Created trc;
        try {
             trc = TransactionFactory.create(transactionManager, 3000);
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);
             return null;
        }

        Transaction transaction = trc.transaction;

        TopicLastID lastID = getLastTopicID(transaction);
        if(lastID == null) {
            System.out.println("Can't get last topic id object");
            System.exit(1);
        }
        try {
            lastID = (TopicLastID) javaSpace.take(lastID, transaction, Lease.FOREVER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastID.increment();
        Topic topic = new Topic(lastID.getLastID(), title, content, getUserId());
        try {
            topic.setLastPostID(0);
            javaSpace.write(topic, transaction, Lease.FOREVER);
            System.out.println(
                "Published topic ID " + String.valueOf(topic.id) + ", " + topic.title
            );
            Post post = new Post(0, topic.id, getUserId(), content);
            try {
                javaSpace.write(post, transaction, Lease.FOREVER);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
            try {
                javaSpace.write(lastID, transaction, Lease.FOREVER);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            transaction.commit();
            System.out.println("Transaction commited");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return topic;
    }

    public Post addNewPost(int topicID, String content) {
        Transaction.Created trc;
        try {
             trc = TransactionFactory.create(transactionManager, 5000);
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);;
             return null;
        }

        Transaction transaction = trc.transaction;

        Post post;

        try {
            Topic topic = (Topic) javaSpace.take(new Topic(topicID), transaction, 1000);
            topic.incrementLastPostID();

            post = new Post(topic.getLastPostID(), topic.id, getUserId(), content);

            javaSpace.write(topic, transaction, Lease.FOREVER);
            System.out.println("Written topic: " + topic.id + ", " + topic.title + ", last id " + topic.getLastPostID());

            javaSpace.write(post, transaction, Lease.FOREVER);
            System.out.println("Written post: " + post.id + ", " + post.content + ", topic id " + post.topicID);

        } catch(Exception e) {
            e.printStackTrace();
            try {
                System.out.println("Abort transaction");
                transaction.abort();
            } catch(Exception e2) {
                e.printStackTrace();
            }
            return null;
        }
        try {
            transaction.commit();
            System.out.println("Commited transaction");
        } catch(Exception e) {
            System.out.println("Could not commit transaction");
            e.printStackTrace();
        }
        return post;
    }

    public Topic getTopicOfID(int id) {
        System.out.println("Get topic ID " + id);
        Topic topic = new Topic(id);
        try {
            topic = (Topic) javaSpace.read(topic, null, 1000);
            if (topic == null) {
                System.out.println("Can't find the topic of that ID: " + id);
                return null;
            }
            List<Post> postList = new ArrayList<Post>();

            for(int i = 0; i <= topic.getLastPostID(); i++) {
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
