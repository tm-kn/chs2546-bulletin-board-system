import net.jini.core.entry.*;


class Topic implements Entry {
    private Integer id;
    private String title;
    private Integer ownerId;

    public Topic(String title, Integer ownerId) {
        this.title = title;
        this.ownerId = ownerId;
    }
}
