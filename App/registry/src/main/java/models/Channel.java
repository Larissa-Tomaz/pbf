package models;

import java.util.List;

import com.owlike.genson.annotation.JsonCreator;
import com.owlike.genson.annotation.JsonProperty;

public class Channel {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("members")
    private List<String> members;

    @JsonCreator
    public Channel(@JsonProperty("id") String id, 
                   @JsonProperty("name") String name, 
                   @JsonProperty("members") List<String> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                '}';
    }
}