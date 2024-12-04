/*
 * SPDX-License-Identifier: Apache-2.0
 */

 import java.nio.channels.Channel;
 import java.util.List;
 
 import org.hyperledger.fabric.contract.annotation.DataType;
 import org.hyperledger.fabric.contract.annotation.Property;
 import com.owlike.genson.annotation.JsonProperty;
 
 @DataType()
 public final class User {
 
     @Property()
     private String userId;
 
     @Property()
     private String username;

     @Property()
     private String name;
 
     @Property()
     private String role;
 
     @Property()
     private String password;

     @Property()
     private String registrationDate;
     
     public User(@JsonProperty("userId") final String userId,
                 @JsonProperty("name") final String name,
                 @JsonProperty("role") final String role,
                 @JsonProperty("registrationDate") final String registrationdate,
                 @JsonProperty("username") final String username,
                 @JsonProperty("password") final String password) {
         this.name = name;
         this.registrationDate = registrationdate;
         this.userId = userId;
         this.role = role;
         this.username = username;
         this.password = password; 
    }
     
    public String getName() {
         return name;
    }
 
    public void setName(String name) {
         this.name = name;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
 
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
         return role;
    }
 
    public void setRole(String role) {
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
 