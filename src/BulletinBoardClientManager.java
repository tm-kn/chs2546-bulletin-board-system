import net.jini.space.JavaSpace;


public class BulletinBoardClientManager {
    private JavaSpace javaSpace;
    private String username;

    public void connectToJavaSpace() {
		javaSpace = SpaceUtils.getSpace();
		if (javaSpace == null){
            throw new JavaSpaceNotFoundException();
		}
    }

    public void authenticateUser(String username) {
        this.username = username;
    }
}
