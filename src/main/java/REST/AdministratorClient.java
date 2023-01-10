package REST;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.util.Scanner;

public class AdministratorClient {

    public static Scanner scanner = new Scanner(System.in);

    public static void main (String[] args) {

        Client client = Client.create();

        System.out.println(
                "\nCOMANDI:\n" +
                "1) Elenco dei taxi presenti nella smart city\n" +
                "2) Elenco dei valori medi delle ultime n statistiche locali (chilometri percorsi, livello batteria, livello inquinamento, numero di corse effettuate) di uno specifico taxi t\n" +
                "3) Elenco dei valori medi delle statistiche globali (chilometri percorsi, livello batteria, livello inquinamento, numero di corse effettuate) registrate tra il timestamp uno (t1) e il timestamp due (t2)\n" +
                "4) Uscire"
        );

        boolean exit = false;

        while (!exit) {

            switch (scanner.nextInt()) {

                case 1: System.out.println("Elenco dei taxi presenti nella smart city");

                    try {

                        WebResource webResource = client.resource("http://localhost:1337/taxis");

                        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
                        response.getStatus();
                        System.out.println(response);
                        System.out.println(response.getEntity(String.class));

                    } catch (ClientHandlerException clientHandlerException) {
                        clientHandlerException.printStackTrace();
                    }

                    break;

                case 2: System.out.println("Elenco dei valori medi delle ultime n statistiche locali (chilometri percorsi, livello batteria, livello inquinamento, numero di corse effettuate) di uno specifico taxi t");

                    System.out.println("Inserire il numero:");
                    String n = scanner.next();

                    System.out.println("Inserire il taxi:");
                    String t = scanner.next();

                    try {

                        WebResource webResource = client.resource("http://localhost:1337/reports/get/" + n + "/" + t);

                        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
                        response.getStatus();
                        System.out.println(response);
                        System.out.println(response.getEntity(String.class));

                    } catch (ClientHandlerException clientHandlerException) {
                        clientHandlerException.printStackTrace();
                    }

                    break;

                case 3: System.out.println("Elenco dei valori medi delle statistiche globali (chilometri percorsi, livello batteria, livello inquinamento, numero di corse effettuate) registrate tra il timestamp uno (t1) e il timestamp due (t2)");

                    try {

                        System.out.println("Inserire il timestamp uno:");
                        String t1D = scanner.next();
                        String t1T = scanner.next();

                        System.out.println("Inserire il timestamp due:");
                        String t2D = scanner.next();
                        String t2T = scanner.next();

                        WebResource webResource = client.resource("http://localhost:1337/reports/getT/" + t1D + "+" + t1T + "/" + t2D + "+" + t2T);

                        ClientResponse response = webResource.type("application/json").get(ClientResponse.class);
                        response.getStatus();
                        System.out.println(response);
                        System.out.println(response.getEntity(String.class));

                    } catch (ClientHandlerException clientHandlerException) {
                        clientHandlerException.printStackTrace();
                    }

                    break;

                case 4: System.out.println("Uscire");

                    exit = true;

                    break;

                default:

                    System.out.println("Inserire un comando valido tra quelli possibili");

            }

        }

    }

}
