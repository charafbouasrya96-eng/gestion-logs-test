
package ma.gestionlogs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SyslogTcpReceiver {

    private static final int PORT = 5514;

    public static void main(String[] args) {
        System.out.println("Demarrage du recepteur TCP sur le port " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("En attente de messages Syslog...");

            while (true) {
                try (Socket client = server.accept();
                     BufferedReader reader = new BufferedReader(
                         new InputStreamReader(
                             client.getInputStream(),
                             StandardCharsets.UTF_8))) {

                    System.out.println("Connexion depuis : "
                            + client.getInetAddress());

                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println("MESSAGE RECU : " + message);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur client : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Impossible de demarrer le recepteur : "
                    + e.getMessage());
        }
    }
}
