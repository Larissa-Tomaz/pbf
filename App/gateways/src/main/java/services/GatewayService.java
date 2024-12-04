package services;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClientParams;
import pbfProto.Auth;
import pbfProto.GatewaysGrpc;
import io.grpc.stub.StreamObserver;
import models.Operations;
import models.RpcClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class GatewayService extends GatewaysGrpc.GatewaysImplBase {

    private final RabbitMqService rabbitMqService;

    public GatewayService(RabbitMqService rabbitMqService) {
        this.rabbitMqService = rabbitMqService;
    }

    private RpcClient getRpcClient() throws IOException, TimeoutException {
        Channel channel = this.rabbitMqService.createNewChannel();
        return new RpcClient(new RpcClientParams().channel(channel));
    }

    @Override
    public void registerUser(Auth.UserRegistrationRequest request,
                             StreamObserver<Auth.UserRegistrationResponse> responseObserver) {
        String serviceQueueName = "service_registry";
        try {
            System.out.println("[Gateway Service] Sending registration request for user: " + request.getUsername());
            RpcClient rpcClient = this.getRpcClient();
            final byte[] response = rpcClient.sendRequest(
                    serviceQueueName,
                    rpcClient.getChannel(),
                    Operations.NEW_USER_REQUEST,
                    request.toByteArray()
            );
            rpcClient.close();
            responseObserver.onNext(Auth.UserRegistrationResponse.parseFrom(response));
            responseObserver.onCompleted();
        } catch (IOException | TimeoutException | ExecutionException | InterruptedException e) {
            System.err.println("[Gateway Service] An error occurred while registering a user: " + e.getMessage());
        }
    }

    @Override
    public void createChannel(Auth.CreateChannelRequest request,
                             StreamObserver<Auth.CreateChannelResponse> responseObserver) {
        String serviceQueueName = "service_registry";
        try {
            System.out.println("[Gateway Service] Sending create channel request with name: " + request.getChannelName());
            RpcClient rpcClient = this.getRpcClient();
            final byte[] response = rpcClient.sendRequest(
                    serviceQueueName,
                    rpcClient.getChannel(),
                    Operations.NEW_CHANNEL_REQUEST,
                    request.toByteArray()
            );
            rpcClient.close();
            responseObserver.onNext(Auth.CreateChannelResponse.parseFrom(response));
            responseObserver.onCompleted();
        } catch (IOException | TimeoutException | ExecutionException | InterruptedException e) {
            System.err.println("[Gateway Service] An error occurred while creating channel: " + e.getMessage());
        }
    }

    @Override
    public void listChannels(Auth.ListChannelsRequest request,
                             StreamObserver<Auth.ListChannelsResponse> responseObserver) {
        String serviceQueueName = "service_registry";
        try {
            System.out.println("[Gateway Service] Sending list channels request");
            RpcClient rpcClient = this.getRpcClient();
            final byte[] response = rpcClient.sendRequest(
                    serviceQueueName,
                    rpcClient.getChannel(),
                    Operations.LIST_CHANNELS_REQUEST,
                    request.toByteArray()
            );
            rpcClient.close();
            responseObserver.onNext(Auth.ListChannelsResponse.parseFrom(response));
            responseObserver.onCompleted();
        } catch (IOException | TimeoutException | ExecutionException | InterruptedException e) {
            System.err.println("[Gateway Service] An error occurred while listing channels: " + e.getMessage());
        }
    }

    @Override
    public void authenticateUser(Auth.UserAuthenticationRequest request,
                                 StreamObserver<Auth.UserAuthenticationResponse> responseObserver) {
        String serviceQueueName = "service_registry";
        try {
            System.out.println("[Gateway Service] Sending authentication request for user: " + request.getUsername());
            RpcClient rpcClient = this.getRpcClient();
            final byte[] response = rpcClient.sendRequest(
                    serviceQueueName,
                    rpcClient.getChannel(),
                    Operations.AUTHENTICATION_REQUEST,
                    request.toByteArray()
            );
            rpcClient.close();

            responseObserver.onNext(Auth.UserAuthenticationResponse.parseFrom(response));
            responseObserver.onCompleted();
        } catch (IOException | TimeoutException | ExecutionException | InterruptedException e) {
            System.err.println("[Gateway Service] An error occurred while authenticating a user: " + e.getMessage());
        }
    }

}
