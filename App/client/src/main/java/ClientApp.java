import services.ClientService;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner;
        try (ClientService clientService = new ClientService("localhost", 8081)) {
            scanner = new Scanner(System.in);
            printInstructions();
            
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Shutting down client...");
                    break;
                }

                try {
                    handleCommand(input, clientService);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println("Error initializing client service: " + e.getMessage());
        }
    }

    private static void printInstructions() {
        System.out.println("======================================================");
        System.out.println("                      ClientApp                       ");
        System.out.println("======================================================");
        System.out.println("Available operations:");
        System.out.println();
        System.out.println("  register <username> <name> <role> <password>");
        System.out.println("      - Register a new user.");
        System.out.println();
        System.out.println("  login <username> <password>");
        System.out.println("      - Login as an existing user.");
        System.out.println();
        System.out.println("  createchannel <channelName>");
        System.out.println("      - Create a new channel.");
        System.out.println();
        System.out.println("  listchannels");
        System.out.println("      - List all available channels.");
        System.out.println();
        System.out.println("  exit");
        System.out.println("      - Exit the client.");
        System.out.println();
        System.out.println("======================================================");
        System.out.println("Please enter a command:");
    }

    private static void handleCommand(String input, ClientService clientService) throws Exception {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "register":
                if (parts.length < 5) {
                    System.out.println("Usage: register <username> <name> <role> <password>");
                } else {
                    String username = parts[1];
                    String name = parts[2];
                    String role = parts[3];
                    String password = parts[4];
                    clientService.registerUser(username, name, role, password);
                    System.out.println("User registered successfully!");
                }
                break;

            case "login":
                if (parts.length < 3) {
                    System.out.println("Usage: login <username> <password>");
                } else {
                    String username = parts[1];
                    String password = parts[2];
                    clientService.loginUser(username, password);
                    System.out.println("User logged in successfully!");
                }
                break;

            case "createchannel":
                if (parts.length < 2) {
                    System.out.println("Usage: createchannel <channelName>");
                } else {
                    String channelName = parts[1];
                    clientService.createChannel(channelName);
                    System.out.println("Channel created successfully!");
                }
                break;

            case "listchannels":
                clientService.listChannels();
                System.out.println("Channels listed successfully!");
                break;

            default:
                System.out.println("Unknown command. Available commands: register, login, createchannel, listchannels, exit.");
        }
    }
}
