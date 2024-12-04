package models;

import com.owlike.genson.annotation.JsonCreator;
import com.owlike.genson.annotation.JsonProperty;

public class Request {

    @JsonProperty("status")
    private String status;

    @JsonProperty("id")
    private String id;

    @JsonProperty("requester")
    private String requester;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("admin")
    private String admin;

    @JsonCreator
    public Request(@JsonProperty("status") String status,
                   @JsonProperty("id") String id,
                   @JsonProperty("requester") String requester,
                   @JsonProperty("channel") String channel,
                   @JsonProperty("admin") String admin) {
        this.status = status;
        this.id = id;
        this.requester = requester;
        this.channel = channel;
        this.admin = admin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Request{" +
                "status='" + status + '\'' +
                ", id='" + id + '\'' +
                ", requester='" + requester + '\'' +
                ", channel='" + channel + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }
}

