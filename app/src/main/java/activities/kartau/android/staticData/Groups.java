package activities.kartau.android.staticdata;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by Artom on 2015-07-23.
 */
public class Groups implements Comparable<Groups> {

    private String
            status,
            type,
            name,
            token,
            created;

    public LinkedList<Users> userList = new LinkedList<Users>();

    public void setStatus(String status){
        this.status = status;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setToken(String token){
        this.token = token;
    }
    public void setCreated(String created){
        this.created = created;
    }
    public String getStatus(){
        return status;
    }
    public String getType(){
        return type;
    }
    public String getName(){
        return name;
    }
    public String getToken(){
        return token;
    }
    public String getCreated() { return created; }


    @Override
    public int compareTo(Groups another) {
        return this.name.compareTo(another.name);
    }

    public static Comparator<Groups> GroupNameComparator
            = new Comparator<Groups>() {

        public int compare(Groups obj1, Groups obj2) {

            String groupName1 = obj1.getName().toUpperCase();
            String groupName2 = obj2.getName().toUpperCase();

            //ascending order
            return groupName1.compareTo(groupName2);

            //descending order
            //return groupName2.compareTo(groupName1);
        }

    };
}
