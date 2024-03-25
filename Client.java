import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            String IP = "192.168.3.111";
            Socket socket = new Socket(IP, 1234);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Get and print the server's welcome message
            String welcomeMessage = in.readLine();
            System.out.println("Server: " + welcomeMessage);

            // Get the client's username
            System.out.print("Enter your username: ");
            BufferedReader usernameReader = new BufferedReader(new InputStreamReader(System.in));
            String username = usernameReader.readLine();

            // Send the username to the server
            out.println(username);

            // Start a new thread for receiving messages from the server
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Read messages from the console and send them to the server
            String userInput;
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while ((userInput = consoleReader.readLine()) != null) {
                out.println(userInput);
            }

            // Close resources
            socket.close();
            in.close();
            out.close();
            usernameReader.close();
            consoleReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}