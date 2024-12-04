import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.GatewayService;
import services.RabbitMqService;

public class GatewaysApp {

    public static void main(String[] args) {
        try {
            RabbitMqService rabbitMqService = new RabbitMqService();
            GatewayService gatewayService = new GatewayService(rabbitMqService);

            Server server = ServerBuilder
                    .forPort(8081)
                    .addService(gatewayService)
                    .build();
            server.start();
            System.out.println("[Gateways App] Starting server on port 8081");
            server.awaitTermination();
        } catch (Exception e) {
            System.err.println("[Gateways App] Unexpected error occurred: " + e.getMessage());
        }
    }
}
