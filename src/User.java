import net.jini.core.entry.*;


class User implements Entry {
    private Integer id;
    private String username;

    public User(String username) {
        this.username = username;
    }
}
