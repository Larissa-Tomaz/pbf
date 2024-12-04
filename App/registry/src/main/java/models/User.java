package models;

import java.util.List;

import javax.management.relation.Role;

import com.owlike.genson.annotation.JsonCreator;
import com.owlike.genson.annotation.JsonProperty;

import utils.HashUtils;
import pbfProto.Auth.ROLE;


public class User {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("name")
    private String name;

    @JsonProperty("role")
    private ROLE role;

    @JsonProperty("password")
    private String password;

    @JsonProperty("registrationDate") 
    private String registrationDate;

    @JsonCreator
    public User(@JsonProperty("userId") String userId, 
                @JsonProperty("username") String username, 
                @JsonProperty("registrationDate") String registrationDate, 
                @JsonProperty("role") ROLE role, 
                @JsonProperty("name") String name, 
                @JsonProperty("password") String password) {
        this.userId = userId;
        this.username = username;
        this.registrationDate = registrationDate;
        this.role = role;
        this.name = name;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ROLE getRole() {
        return role;
    }
    
    public void setRole(ROLE role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean checkPassword(String plainTextPassword) {
        return HashUtils.checkPassword(plainTextPassword, this.password);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", registrationDate'" + registrationDate + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role.toString() +
                '}';
    }
}