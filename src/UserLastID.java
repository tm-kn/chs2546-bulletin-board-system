import net.jini.core.entry.*;

public class UserLastID implements Entry{
    public Integer lastID;

    public UserLastID() {
    }

    public UserLastID (int lastID){
        this.lastID = lastID;
    }

    public void increment(){
        lastID++;
    }

    public Integer getLastID() {
        return lastID;
    }
}
