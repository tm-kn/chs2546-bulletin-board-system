import java.util.ArrayList;
import java.util.List;
import java.rmi.server.UnicastRemoteObject;
import net.jini.core.lease.*;
import net.jini.core.event.*;
import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;
import net.jini.space.AvailabilityEvent;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.*;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;


public class BulletinBoardClientManager {
    private JavaSpace javaSpace;
    private User user;
    private TransactionManager transactionManager;
    private NotificationEventListener onNotification;

    public BulletinBoardClientManager(NotificationEventListener onNotification) {
        this.onNotification = onNotification;
    }

    public User getUserOfId(int id) {
        try {
            return (User) javaSpace.read(new User(id), null, 1000);
        } catch(Exception e) {
            System.out.println("Cannot obtain user of id " + id);
            e.printStackTrace();
            return null;
        }
    }

    public void refreshUser() {
        User user = getUserOfId(this.user.id);
        if (user == null) {
            System.out.println("User not authenticated.");
            System.exit(1);
        }
        this.user = user;
    }

    public int getUserId() {
        refreshUser();
        System.out.println("Current user ID:" + user.id);
        return user.id;
    }

    private JavaSpace05 getJavaSpace05() {
        return (JavaSpace05) javaSpace;
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

    public JavaSpace getSpace() {
        return javaSpace;
    }

    public boolean addTopicSubscription(int topicID) {
        Transaction.Created trc;

        try {
             trc = TransactionFactory.create(transactionManager, 3000);
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);
             return false;
        }

        Transaction transaction = trc.transaction;

        try {
            User user = (User) javaSpace.take(new User(getUserId()), transaction, 1000);
            if (user == null) {
                transaction.abort();
                return false;
            }
            user.addSubscribedTopic(topicID);
            javaSpace.write(user, transaction, Lease.FOREVER);
            transaction.commit();
            System.out.println("Added subscription for topic " + topicID);
            registerNotificationsForTopics(new Integer[]{topicID});
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean removeTopicSubscription(int topicID) {
        Transaction.Created trc;

        try {
             trc = TransactionFactory.create(transactionManager, 3000);
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);
             return false;
        }

        Transaction transaction = trc.transaction;

        try {
            User user = (User) javaSpace.take(new User(getUserId()), transaction, 1000);
            if (user == null) {
                transaction.abort();
                return false;
            }
            user.removeSubscribedTopic(topicID);
            javaSpace.write(user, transaction, Lease.FOREVER);
            transaction.commit();
            System.out.println("Removed subscription for topic " + topicID);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean authenticateUser(String username, String password) {
        if (user != null) {
            throw new RuntimeException("Already authenticated");
        }

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        User user;

        try {
            user = (User) javaSpace.read(new User(username), null, 1000);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        if (user == null) {
            System.out.println("Cannot find user: " + username);

            return false;
        }

        if (user.comparePassword(password)) {
            this.user = user;

            registerNotificationsForUserTopics();

            return true;
        }

        System.out.println("Password is incorrect");

        return false;
    }

    public boolean isUserSubscribedToTopic(int topicID) {
        refreshUser();
        return user.isSubscribedToTopic(topicID);
    }

    private Exporter getExporter() {
        return new BasicJeriExporter(
            TcpServerEndpoint.getInstance(0),
            new BasicILFactory(),
            false,
            true
        );
    }

    public void registerNotificationsForUserTopics() {
        registerNotificationsForTopics(user.getTopicSubscriptions());
    }

    public void registerNotificationsForTopics(Integer[] topicIDs) {
        List<Topic> templates = new ArrayList<Topic>();

        for(Integer topicID: topicIDs) {
            templates.add(new Topic(topicID));
        }

        if (templates.size() == 0) {
            System.out.println("No topics to be subscribed.");
            return;
        }

        RemoteEventListener topicListener = new RemoteEventListener() {
            public void notify(RemoteEvent remoteEvent) {
                AvailabilityEvent availabilityEvent = (AvailabilityEvent) remoteEvent;
                try {
                    Topic topic = (Topic) availabilityEvent.getEntry();

                    System.out.println("Received notification event about topic " + topic.id);
                    String message = null;

                    if (topic.isDeleted() && topic.ownerId != getUserId()) {
                        message = "Topic " + topic + " has been deleted";
                        removeTopicSubscription(topic.id);
                    } else if (!topic.isDeleted()) {
                        topic = getTopicOfID(topic.id);
                        Post post = topic.getLastPost();
                        if (
                            post.authorID != getUserId() && !post.isPrivate
                        ) {
                            message = "New public posts added in topic " + topic;
                        } else if (
                            post.authorID != getUserId()
                            && post.isPrivate
                            && topic.ownerId == getUserId()
                        ) {
                            message = "New private posts added in topic " + topic;
                        }
                    }

                    if (message == null) {
                        return;
                    }

                    System.out.println("Notification message: " + message);
                    if (onNotification != null) {
                        onNotification.onEvent(message);
                    }
                } catch(Exception e) {
                    System.out.println("Could not receive object from the space");
                    e.printStackTrace();
                }
            }
        };

        try {
            UnicastRemoteObject.exportObject(topicListener, 0);
            getJavaSpace05().registerForAvailabilityEvent(
                templates, null, false, topicListener, Lease.FOREVER, null
            );
            System.out.println("Subscribed to topic notifications");
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Could not subscribe to topic notifications");
        }
    }

    private UserLastID getLastUserID(Transaction transaction) {
        UserLastID lastID;
        try {
            lastID = (UserLastID) javaSpace.readIfExists(
                new UserLastID(),
                transaction,
                1000
            );

            if(lastID == null) {
                try {
                    lastID = new UserLastID(-1);
                    javaSpace.write(lastID, transaction, Lease.FOREVER);
                    System.out.println("Written UserLastID object.");
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

    public boolean isUsernameTaken(String username, Transaction transaction) {
        try {
            User user = (User) javaSpace.readIfExists(
                new User(username), transaction, Lease.FOREVER
            );
            return user != null;
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public User createUser(String username, String password) {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        Transaction.Created trc;

        try {
             trc = TransactionFactory.create(transactionManager, 3000);
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);

             return null;
        }

        Transaction transaction = trc.transaction;

        try {
            if(isUsernameTaken(username, transaction)) {
                transaction.abort();
                return null;
            }
            UserLastID lastID = getLastUserID(transaction);
            if (lastID == null) {
                transaction.abort();
                return null;
            }
            lastID = (UserLastID) javaSpace.take(lastID, transaction, 1000);
            lastID.increment();
            User user = new User(lastID.getLastID(), username);
            user.setPassword(password);
            user.setCurrentJoinedAtDate();
            javaSpace.write(user, transaction, Lease.FOREVER);
            javaSpace.write(lastID, transaction, Lease.FOREVER);
            transaction.commit();
            return user;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Topic[] getTopicList() {
        TopicLastID lastID = getLastTopicID(null);
        List<Topic> topicList = new ArrayList<Topic>();
        for(int i = lastID.getLastID(); i >= 0 ; i--) {
            try {
                Topic topicTemplate = new Topic(i);
                Topic topic = (Topic) javaSpace.read(topicTemplate, null, 1000);
                if (topic != null && !topic.isDeleted()) {
                    topicList.add(topic);
                }
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
                    lastID = new TopicLastID(-1);
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
        topic.setCurrentDateTime();

        try {
            topic.setLastPostID(0);
            javaSpace.write(topic, transaction, Lease.FOREVER);
            System.out.println(
                "Published topic ID " + String.valueOf(topic.id) + ", " + topic.title
            );
            Post post = new Post(0, topic.id, getUserId(), content);
            post.setCurrentDateTime();
            post.setPublic();
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

    public Post addNewPost(int topicID, String content, boolean isPrivate) {
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
            post.setCurrentDateTime();

            if (isPrivate) {
                post.setPrivate();
            } else {
                post.setPublic();
            }

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

            if (!topic.isDeleted()) {
                List<Post> postList = new ArrayList<Post>();

                for(int i = 0; i <= topic.getLastPostID(); i++) {
                    try {
                        Post postTemplate = new Post(i, topic.id);
                        Post post = (Post) javaSpace.read(postTemplate, null, 1000);

                        if (post == null) {
                            continue;
                        }

                        // Skip private posts.
                        if (
                            post.isPrivate
                            && !topic.ownerId.equals(this.user.id)
                            && !post.authorID.equals(this.user.id)
                        ) {
                            continue;
                        }

                        postList.add(post);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                topic.setPostList(postList.toArray(new Post[postList.size()]));
            }

        } catch(Exception e) {
            e.printStackTrace();

            return null;
        }

        return topic;
    }

    public Boolean deleteTopic(int topicID) throws Exception {
        Transaction.Created trc;
        try {
             trc = TransactionFactory.create(transactionManager, 3000);
        } catch (Exception e) {
             System.out.println("Could not create transaction " + e);

             return null;
        }

        Transaction transaction = trc.transaction;
        Topic topic;
        try {
            topic = (Topic) javaSpace.take(new Topic(topicID), transaction, 10000);
            System.out.println("Deleted topic " + topicID);
        } catch(Exception e) {
            e.printStackTrace();

            return null;
        }

        if (getUserId() != topic.ownerId) {
            transaction.abort();
            throw new Exception("You must be an owner of a topic to delete it");
        }

        topic.setDeleted();

        try {
            javaSpace.write(topic, transaction, Lease.FOREVER);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }

        Post post;

        do {
            post = (Post) javaSpace.take(new Post(topicID), transaction, 1000);

            if (post != null) {
                System.out.println("Deleted post " + post.id + " of topic " + topicID);
            }
        } while(post != null);

        try {
            transaction.commit();
            System.out.println("Transaction commited");
        } catch(Exception e) {
            System.out.println("Transaction not commited");
            e.printStackTrace();

            return null;
        }

        return true;
    }
}
