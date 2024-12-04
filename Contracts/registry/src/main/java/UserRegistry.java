/*
 * SPDX-License-Identifier: Apache-2.0
 */

 import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
 import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.hyperledger.fabric.contract.Context;
 import org.hyperledger.fabric.contract.ContractInterface;
 import org.hyperledger.fabric.contract.annotation.Contact;
 import org.hyperledger.fabric.contract.annotation.Contract;
 import org.hyperledger.fabric.contract.annotation.Default;
 import org.hyperledger.fabric.contract.annotation.Info;
 import org.hyperledger.fabric.contract.annotation.License;
 import org.hyperledger.fabric.contract.annotation.Transaction;
 import org.hyperledger.fabric.shim.ChaincodeException;
 import org.hyperledger.fabric.shim.ChaincodeStub;
 import org.hyperledger.fabric.shim.ledger.KeyValue;
 import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
 
 import com.owlike.genson.Genson;
 
 @Contract(
         name = "registry",
         info = @Info(
                 title = "User Registry",
                 description = "The hyperlegendary user registry",
                 version = "2.1",
                 license = @License(
                         name = "Apache 2.0 License",
                         url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                 contact = @Contact(
                         email = "a.registry@example.com",
                         name = "Larissa Tomaz",
                         url = "https://hyperledger.example.com")))
 @Default
 public final class UserRegistry implements ContractInterface {
 
    private final Genson genson = new Genson();

    private enum UserRegistryErrors {
        USER_NOT_FOUND,
        USER_ALREADY_EXISTS,
        CREDENTIALS_DONT_MATCH
    }
    

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public User queryUser(final Context ctx, final String key) {
        String userState = ctx.getStub().getStringState(key);

        if (userState.isEmpty()) {
            throw new ChaincodeException("User " + key + " does not exist.", UserRegistryErrors.USER_NOT_FOUND.toString());
        }

        return genson.deserialize(userState, User.class);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteAllUsers(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        List<String> keysToDelete = new ArrayList<>();
        for (KeyValue result : results) {
            keysToDelete.add(result.getKey());
        }
        for (String key : keysToDelete) {
            stub.delState(key);  
        }
    }
 
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public User createUser(final Context ctx, final String username, final String name, 
                           final String role, final String hashedPassword) {
        ChaincodeStub stub = ctx.getStub();
        String userState = stub.getStringState(username);

        if (!userState.isEmpty()) {
            throw new ChaincodeException("User " + username + " already exists.", UserRegistryErrors.USER_ALREADY_EXISTS.toString());
        }

        long createdAtEpoch = Instant.now().getEpochSecond();
        String createdAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                                            .withZone(ZoneId.of("UTC"))
                                            .format(Instant.ofEpochSecond(createdAtEpoch));

        String userId = UUID.randomUUID().toString();

        User user = new User(
                userId,
                name,
                role,
                createdAt,
                username,               
                hashedPassword
        );
        userState = genson.serialize(user);
        stub.putStringState(username, userState);

        return user;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public User login(final Context ctx, final String username, final String password) {
        ChaincodeStub stub = ctx.getStub();
        String userJson = stub.getStringState(username);

        if (userJson == null || userJson.isEmpty()) {
            System.out.println("[Registry Service] No user found for username: " + username);
            return null; 
        }

        User user = genson.deserialize(userJson, User.class);

        boolean valid = user.getUsername().equals(username) && user.getPassword().equals(password);
        if (!valid)
            throw new ChaincodeException("User " + username + " credentials do not match.", UserRegistryErrors.CREDENTIALS_DONT_MATCH.toString());

        return user;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public boolean deleteUser(final Context ctx, final String username) {
        User user = this.queryUser(ctx, username);

        ctx.getStub().delState(user.getUsername());
        return true;
    }

 }
 