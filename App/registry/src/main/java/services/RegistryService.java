package services;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClientParams;
import models.*;
import pbfProto.Auth;

import org.hyperledger.fabric.gateway.*;
import utils.HashUtils;

import java.io.IOException;
import java.security.SecureRandom;
import com.mongodb.MongoException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RegistryService {

    private final HyperledgerService hyperledgerService;
    private final static int TOKEN_LENGTH = 32;
    private final Map<String, User> authenticatedUsers;

    // For tests
    public RegistryService() throws Exception {
        this.hyperledgerService = new HyperledgerService();
        this.authenticatedUsers = new HashMap<>();
    }

    public void loadHyperledgerService() throws Exception {
        //System.out.println("[Registry Service] Enrolling users...");
        this.hyperledgerService.enrollAdminUser();
        //System.out.println("Enrolled admin user");
        
        List<String> users = new ArrayList<>();
        users.add("dataUser");
        
        this.hyperledgerService.registerServicesUsers(
                users,
                "org1.department1",
                "Org1MSP"
        );
    }

    public void clearAllUsers() {
        try {
            //userRepo.deleteAllUsers();
            hyperledgerService.clearAllUsers();
        } catch (ContractException | IOException | InterruptedException | TimeoutException e) {
            System.err.println("[Registry Service] Hyperledger error: " + e.getMessage());
        }
    }


    public User registerUser(String name, Auth.ROLE role, String username, String password) {

        try {
            if (username == null || username.isEmpty()) {
                return null;
            }

            String hashedPassword = HashUtils.hashPassword(password);

            System.out.println("[Registry Service] Registering user:\n" +
                        "Username: " + username +
                        "\nName: " +  name +
                        "\nRole: " + role +
                        "\nPassword: " + hashedPassword
            );

            
            User user = this.hyperledgerService.registerUser(
                name, 
                role,
                username, 
                hashedPassword
            );

            if (user != null) {
                System.out.println("[Registry Service] Successfully registered user with username: " + username);
            } else {
                System.out.println("[Registry Service] Failed to register user with username: " + username + ". " +
                        "Invalid data provided.");
            }
              
            return user;

        } catch (IOException | ContractException | InterruptedException | TimeoutException e) {
            System.err.println("[Registry Service] Failed to register user: " + e.getMessage());
            return null;
        }
    }


    public boolean logoutUser(String userToken) {
        User user = this.getUserByToken(userToken);
        if (user != null) {
            this.authenticatedUsers.remove(userToken);
            System.out.println("[Registry Service] Logged out user " + user.getUsername() + " successfully.");
            return true;
        } else {
            System.err.println("[Registry Service] Failed to log out user with token " +
                    userToken + ". It is not logged in.");
            return false;
        }
    }

    public boolean deleteUser(String authorToken) {
        User user = this.getUserByToken(authorToken);
        if (user == null) {
            System.err.println("[Registry Service] Failed to delete user ");
            return false;
        }

        try {
            System.out.println("[Registry Service] Deleted user " + user.getUsername() + " successfully.");
            return this.hyperledgerService.deleteUser(user.getUsername());
        } catch (IOException | ContractException | InterruptedException | TimeoutException e) {
            System.err.println("[Registry Service] Failed to delete user " + user.getUsername() + ": " + e.getMessage());
            return false;
        }
    }
    

    public String authenticateUser(String username, String password) {
        try {
            String hashedPassword = HashUtils.hashPassword(password);
            
            models.User user = this.hyperledgerService.login(username, hashedPassword);
            String token = this.generateToken();


            if (this.authenticatedUsers.containsValue(user)) {
                System.out.println("[Registry Service] User " + user.getUsername() + " is already authenticated.");
                return null;
            }

            this.insertUserToken(user, token);

            //System.out.println("[Registry Service] Authenticated user " + user.getUsername() + " successfully with token " + token);
            return token;
        } catch (IOException | ContractException e) {
            System.err.println("[Registry Service] Failed to login user: " + e.getMessage());
            return null;
        }
    }
    
    public String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[RegistryService.TOKEN_LENGTH];
        random.nextBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes);
    }

    public User getUserByToken(String token) {
        return this.authenticatedUsers.get(token);
    }

    public void insertUserToken(User user, String token) {
        this.authenticatedUsers.put(token, user);
    }

    public void removeUserToken(String token) {
        this.authenticatedUsers.remove(token);
    }
}
