import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import net.jini.core.entry.*;


public class User implements Entry {
    public Integer id;
    public String username;
    public String password;
    public String salt;
    public Date joinedAt;
    public Integer[] subscribedTopics;
    private char[] charList;

    public User() {}

    public User(int id) {
        this.id = id;
    }

    public User(String username) {
        this.username = username;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public String toString() {
        return username;
    }

    public void setPassword(String plainTextPassword) {
        generateSalt();
        password = hashPassword(plainTextPassword);
    }

    public boolean comparePassword(String plainTextPassword) {
        return hashPassword(plainTextPassword).equals(password);

    }

    private void generateSalt(int length) {
        if (charList == null) {
            charList = new char[126-33+1];
            for (int i = 33; i <= 126; i++) {
                charList[i - 33] = (char) i;
            }
        }

		StringBuffer randStr = new StringBuffer(length);
		SecureRandom secureRandom = new SecureRandom();
		for( int i = 0; i < length; i++ ) {
			randStr.append(charList[secureRandom.nextInt(charList.length)]);
        }
		String salt = randStr.toString();
        this.salt = salt;
    }

    private void generateSalt() {
        generateSalt(10);
    }

    public String hashPassword(String plainTextPassword) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Hash algorithm not installed.");
        }
        try {
            byte[] hash = messageDigest.digest((salt + plainTextPassword).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash){
                sb.append(String.format("%02x", b&0xff));
            }
            return sb.toString();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot decode password from UTF-8");
        }
    }

    public void addSubscribedTopic(int topicID) {
        if (subscribedTopics == null) {
            subscribedTopics = new Integer[0];
        }
        List topics = new ArrayList<Integer>(Arrays.asList(subscribedTopics));
        topics.add(topicID);
        Set<Integer> topicsSet = new HashSet<Integer>(topics);
        subscribedTopics = topicsSet.toArray(new Integer[topicsSet.size()]);
    }

    public void removeSubscribedTopic(int topicID) {
        if (subscribedTopics == null) {
            subscribedTopics = new Integer[0];
        }

        List topics = new ArrayList<Integer>(Arrays.asList(subscribedTopics));
        topics.remove((Integer) topicID);
        Set<Integer> topicsSet = new HashSet<Integer>(topics);
        subscribedTopics = topicsSet.toArray(new Integer[topicsSet.size()]);
    }

    public boolean isSubscribedToTopic(int topicID) {
        if (subscribedTopics == null) {
            return false;
        }

        for(int id: subscribedTopics) {
            if (id == topicID) {
                return true;
            }
        }

        return false;
    }

    public void setCurrentJoinedAtDate() {
        joinedAt = new Date();
    }
}
