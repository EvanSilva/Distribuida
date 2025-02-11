import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public static void main(String[] args) {
    int port = 5000; // Puerto del servidor

    try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Servidor esperando conexiones en el puerto " + port + "...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + message);
                }
            }

            System.out.println("Cliente desconectado.");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
