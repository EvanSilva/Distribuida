import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or

public static void main(String[] args) {
    String serverAddress = "localhost"; // Dirección del servidor
    int port = 5000; // Puerto del servidor

    try (Socket socket = new Socket(serverAddress, port);
         PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
         BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

        System.out.println("Conectado al servidor. Escribe un mensaje:");

        String message;
        while ((message = userInput.readLine()) != null) {
            out.println(message);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

}