import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;

public class Server {
    public static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // System.out.println("Client connected: " + clientSocket);
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new thread to handle the client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Get the client's username
            out.println("Enter your username:");
            username = in.readLine();
            System.out.println("Client username: " + username);

            // Notify the client that they have successfully connected
            out.println("You are now connected. Start chatting!");

            // Broadcast messages to all connected clients
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(username + ": " + message);
                broadcastMessage(username + ": " + message);
            }

            // Client disconnected
            System.out.println(username + " has left the chat.");
            broadcastMessage(username + " has left the chat.");

            // Clean up resources
            in.close();
            out.close();
            clientSocket.close();
        } catch (SocketException e) {
            // Handle the connection reset exception
            broadcastMessage(username + " has left the chat.");
            System.out.println(username + " has left the chat.");

            // Clean up resources
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : Server.clients) {
            client.out.println(message);
        }
    }
}

