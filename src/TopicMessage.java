import net.jini.core.entry.*;


class TopicMessage implements Entry {
    private Integer id;
    private Integer topicId;
    private String message;
    private Integer authorId;

    public TopicMessage(Integer topicId, String message, Integer authorId) {
        this.topicId = topicId;
        this.message = message;
        this.authorId = authorId;
    }
}
