package services;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import models.*;
import pbfProto.Auth;

import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class HyperledgerService {

    private final HFCAClient hfcaClient;
    private final Wallet wallet;
    private final Genson genson;


    private final static String REGISTRY_CHANNEL = "registry";
    private final static String REGISTRY_CONTRACT = "registry";

    public HyperledgerService() throws Exception {
        Properties properties = new Properties();
        properties.put("pemFile", "resources/org1.example.com/ca/ca.org1.example.com-cert.pem");
        properties.put("allowAllHostNames", "true");

        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
        this.hfcaClient = HFCAClient.createNewInstance("https://localhost:7054", properties);
        this.hfcaClient.setCryptoSuite(CryptoSuiteFactory.getDefault().getCryptoSuite());
        this.wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
        this.genson = new Genson();
    }

    public void enrollAdminUser() throws Exception {
        if (this.wallet.get("admin") != null) {
            //System.out.println("The admin user already exists. Skipping admin user creation...");
            return;
        }

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
        enrollmentRequest.addHost("localhost");
        enrollmentRequest.setProfile("tls");
        Enrollment enrollment = this.hfcaClient.enroll("admin", "adminpw", enrollmentRequest);

        Identity adminUser = Identities.newX509Identity("Org1MSP", enrollment);
        this.wallet.put("admin", adminUser);

        //System.out.println("The admin user was successfully registered.");
    }

    public void registerServicesUsers(List<String> usernames, String affiliation, String mspId) throws Exception {
        X509Identity adminIdentity = (X509Identity) this.wallet.get("admin");
        if (adminIdentity == null) {
            System.out.println("The admin user is not registered. Failed to register a new Hyperledger Fabric user.");
            return;
        }

        User adminUser = new User() {
            @Override
            public String getName() {
                return "admin";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return null;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {
                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return mspId;
            }
        };


        for (String username : usernames) {
            X509Identity userIdentity = (X509Identity) this.wallet.get(username);

            if (userIdentity != null) {
                //System.out.println("The user " + username + " is already registered. ");
                continue;
            }
            RegistrationRequest registrationRequest = new RegistrationRequest(username);
            registrationRequest.setAffiliation(affiliation);
            registrationRequest.setEnrollmentID(username);
            String enrollmentSecret = this.hfcaClient.register(registrationRequest, adminUser);
            Enrollment enrollment = this.hfcaClient.enroll(username, enrollmentSecret);
            this.wallet.put(username, Identities.newX509Identity(mspId, enrollment));
            //System.out.println("Successfully registered user " + username + " and enrolled them.");
        }
    }

    public Contract getContract(Gateway gateway) {
        return gateway.getNetwork(REGISTRY_CHANNEL).getContract(REGISTRY_CONTRACT);
    }

    public Gateway getGateway() throws IOException {
        Path networkConfigPath = Paths.get("resources", "org1.example.com", "connection-org1.json");
        //System.out.println("network config path"+networkConfigPath);
        return Gateway.createBuilder()
                .identity(this.wallet, "admin")
                .networkConfig(networkConfigPath)
                .discovery(true)
                .connect();
    }

    public void clearAllUsers() throws IOException, ContractException, InterruptedException, TimeoutException {
        Gateway gateway = this.getGateway();
        Contract contract = this.getContract(gateway);

        contract.submitTransaction("deleteAllUsers");

        gateway.close();
    }

    public models.User registerUser(String name, Auth.ROLE role, String username, String hashedPassword) throws IOException, ContractException, InterruptedException, TimeoutException {
        
        Gateway gateway = this.getGateway();
        Contract contract = this.getContract(gateway);

        byte[] user = contract.submitTransaction("createUser",
                            username, 
                            name,
                            role.toString(),
                            hashedPassword
        );

        gateway.close();
        
        return this.genson.deserialize(user, models.User.class);
    }

    public models.User login(String username, String hashedPassword) throws IOException, ContractException {
        Gateway gateway = this.getGateway();
        Contract contract = this.getContract(gateway);

        byte[] user = contract.evaluateTransaction("login", username, hashedPassword);

        gateway.close();
        return this.genson.deserialize(user, models.User.class);
    }

    public boolean deleteUser(String username) throws IOException, ContractException,
            InterruptedException, TimeoutException {
        Gateway gateway = this.getGateway();
        Contract contract = this.getContract(gateway);

        byte[] result = contract.submitTransaction("deleteUser", username);

        gateway.close();
        return Boolean.parseBoolean(new String(result));

    }


}