package ma.gestionlogs;

import ma.gestionlogs.repository.MongoConnection;

public class MongoConnectionTest {

    public static void main(String[] args) {
        try (MongoConnection connection = new MongoConnection()) {
            if (connection.ping()) {
                System.out.println("Connexion MongoDB OK");
                System.out.println("Documents dans 'logs' : "
                        + connection.getLogsCollection().countDocuments());
            } else {
                System.out.println("Connexion MongoDB ECHEC");
            }
        }
    }
}