import net.jini.core.entry.*;

public class TopicLastID implements Entry{
    public Integer lastID;

    public TopicLastID() {
    }

    public TopicLastID (int lastID){
        this.lastID = lastID;
    }

    public void increment(){
        lastID++;
    }

    public Integer getLastID() {
        return lastID;
    }
}
