import edu.badpals.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public static void main(String[] args) {

    int port = 5000;  // Puerto donde escucha el servidor

    try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Servidor esperando conexiones en el puerto " + port + "...");

        while (true) {  // Bucle infinito para aceptar múltiples clientes
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());

            // 🔹 CREAR Y EJECUTAR UN HILO PARA EL CLIENTE
            ClientHandler clienteHandler = new ClientHandler(clientSocket);
            clienteHandler.start();  // 🔥 Aquí se inicia el hilo
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


