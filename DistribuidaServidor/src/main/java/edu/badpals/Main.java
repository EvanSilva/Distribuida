import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

public static void main(String[] args) {
    int port = 5000; // Puerto del servidor

    try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Servidor esperando conexiones en el puerto " + port + "...");

        while (true) {

            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente conectado desde " + clientSocket.getInetAddress());
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);


            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String message;

                while ((message = in.readLine()) != null) {

                    String url = "jdbc:mysql://localhost:3306/Preguntas_RespuestasBD";
                    String usuario = "root";
                    String contraseña = "root";

                    String sql = """
                            SELECT cadena_respuesta
                                    FROM respuestas
                                        where id IN (
                                                    SELECT id_respuesta
                                                        FROM preguntas_respuestas
                                                            where id_pregunta IN (
                                                                                    SELECT id
                                                                                        FROM preguntas
                                                                                            WHERE cadena_pregunta = ?
                                                                    ) 
                                            )
                            """;

                    List<String> respuestas = new ArrayList<>();

                    try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {

                        pstmt.setString(1, message);

                        try (ResultSet rs = pstmt.executeQuery()) {

                            if (rs != null) {
                                while (rs.next()) {
                                    String respuesta = rs.getString("cadena_respuesta");
                                    System.out.println("Respuesta: " + respuesta);
                                    respuestas.add(respuesta);
                                }
                            } else {
                                respuestas.add("No se encontraron respuestas para la pregunta.");
                            }

                        }catch (IndexOutOfBoundsException e){
                            respuestas.add("No se encontraron respuestas para la pregunta.");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Collections.shuffle(respuestas);

                    if (respuestas.size() > 0) {
                        out.println(respuestas.get(0));
                    } else {
                        out.println("No se encontraron respuestas para la pregunta.");
                    }


                }
            }

            System.out.println("Cliente desconectado.");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
