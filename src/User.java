import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

import net.jini.core.entry.*;


public class User implements Entry {
    public Integer id;
    public String username;
    public String password;
    public String salt;
    private char[] charList;

    public User() {}

    public User(int id) {
        this.id = id;
    }

    public User(String username) {
        this.username = username;
    }

    public String toString() {
        return username;
    }

    public void setPassword(String plainTextPassword) {
        password = hashPassword(plainTextPassword);
        System.out.println(password);
    }

    public boolean comparePassword(String plainTextPassword) {
        return hashPassword(plainTextPassword) == password;

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

    public String hashPassword(String password) {
        generateSalt();
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Hash algorithm not installed.");
        }
        try {
            byte[] hash = messageDigest.digest((salt + password).getBytes("UTF-8"));
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
}
