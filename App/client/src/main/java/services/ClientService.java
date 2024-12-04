package services;

import pbfProto.Auth;
import pbfProto.Auth.ROLE;
import pbfProto.GatewaysGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientService implements AutoCloseable {

    private final GatewaysGrpc.GatewaysBlockingStub stub;
    private final ManagedChannel channel;
    String userToken;
    String username;                          

    public ClientService(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = GatewaysGrpc.newBlockingStub(this.channel);
    }

    public void registerUser(String username, String name, String role, String password)
            throws Exception {

        Auth.UserRegistrationRequest userRegistrationRequest = Auth.UserRegistrationRequest.newBuilder()
                .setUsername(username)
                .setName(name)
                .setRole(Auth.ROLE.valueOf(role))
                .setPassword(password)
                .build();

        System.out.println("[Terminal Service] Registering user with username " + username + "\n");
        Auth.UserRegistrationResponse userRegistrationResponse = this.stub.registerUser(userRegistrationRequest);

        if (userRegistrationResponse.hasErrorMessage()) {
            throw new Exception(userRegistrationResponse.getErrorMessage().getDescription());
        }

        System.out.println("\n=======================================");
        System.out.println(">>> User Registered Successfully <<<");
        System.out.println("---------------------------------------");
        System.out.println("User ID: " + userRegistrationResponse.getUser().getUserId());
        System.out.println("Role: " + userRegistrationResponse.getUser().getRole());
        System.out.println("Registration Date: " + userRegistrationResponse.getUser().getRegistrationDate());
        System.out.println("=======================================\n");
    }

    public void loginUser(String username, String password) throws Exception {
        Auth.UserAuthenticationRequest authenticationRequest = Auth.UserAuthenticationRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        System.out.println("[Client Service] Logging in user: " + username + "\n");
        Auth.UserAuthenticationResponse authenticationResponse = this.stub.authenticateUser(authenticationRequest);
        if (authenticationResponse.hasErrorMessage()) {
            throw new Exception(authenticationResponse.getErrorMessage().getDescription());
        }

        this.userToken = authenticationResponse.getToken();
        this.username = username;

        System.out.println("\n=======================================");
        System.out.println(">>> Login Successful <<<");
        System.out.println("---------------------------------------");
        System.out.println("Token: " + this.userToken);
        System.out.println("=======================================\n");
    }

    public void createChannel(String name) throws Exception {
        Auth.CreateChannelRequest createChannelRequest = Auth.CreateChannelRequest.newBuilder()
            .setChannelName(name)
            .setToken(this.userToken)
            .build();

        System.out.println("[Client Service] Creating channel: " + name + "\n");
        Auth.CreateChannelResponse createChannelResponse = this.stub.createChannel(createChannelRequest);

        if (createChannelResponse.hasErrorMessage()) {
            throw new Exception(createChannelResponse.getErrorMessage().getDescription());
        }
    }

    public void listChannels() throws Exception {
        Auth.ListChannelsRequest listChannelsRequest = Auth.ListChannelsRequest.newBuilder()
            .setToken(this.userToken)
            .build();

        System.out.println("[Client Service] Listing channels..." + "\n");
        Auth.ListChannelsResponse listChannelsResponse = this.stub.listChannels(listChannelsRequest);

        if (listChannelsResponse.hasErrorMessage()) {
            throw new Exception(listChannelsResponse.getErrorMessage().getDescription());
        }
    }
    
    @Override
    public void close() {
        this.channel.shutdown();
    }
}