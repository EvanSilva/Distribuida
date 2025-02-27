package edu.badpals;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {  // 🔥 Esto se ejecuta cuando el hilo inicia
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {  // Recibe pregunta del cliente
                System.out.println("Pregunta recibida: " + message);

                // 🔹 OBTENER RESPUESTAS DE LA BASE DE DATOS
                List<String> respuestas = obtenerRespuestas(message);

                // 🔹 MEZCLAR Y ENVIAR UNA RESPUESTA AL CLIENTE
                Collections.shuffle(respuestas);
                if (!respuestas.isEmpty()) {
                    out.println(respuestas.get(0));  // Envía una respuesta al cliente
                } else {
                    out.println("No se encontraron respuestas para la pregunta.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }

    private List<String> obtenerRespuestas(String pregunta) {
        List<String> respuestas = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/Preguntas_RespuestasBD";
        String usuario = "root";
        String contraseña = "root";

        String sql = """
            SELECT cadena_respuesta
            FROM respuestas
            WHERE id IN (
                SELECT id_respuesta
                FROM preguntas_respuestas
                WHERE id_pregunta IN (
                    SELECT id
                    FROM preguntas
                    WHERE cadena_pregunta = ?
                )
            )
        """;

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pregunta);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    respuestas.add(rs.getString("cadena_respuesta"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return respuestas;
    }

    private void cerrarConexion() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Cliente desconectado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
