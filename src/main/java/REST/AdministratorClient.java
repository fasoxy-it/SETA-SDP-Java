package REST;

import java.util.Scanner;

public class AdministratorClient {

    public static Scanner scanner = new Scanner(System.in);

    public static void main (String[] args) {

        System.out.println(
                "\nCOMANDI:\n" +
                "1) Elenco dei taxi presenti nella smart city\n" +
                "2) Elenco delle ultime n statistiche globali\n" +
                "3) Elenco di tutte le statistiche globali\n" +
                "4) Numero medio di consegne tra il timestamp uno e il timestamp due\n" +
                "5) Numero medio di chilometri percorsi tra il timestamp uno e il timestamp due\n" +
                "6) Uscire"
        );

        boolean exit = false;

        while (exit == false) {

            switch (scanner.nextInt()) {
                case 1: System.out.println("Elenco dei taxi presenti nella smart city");
                    break;
                case 2: System.out.println("Elenco delle ultime n statistiche globali");
                    break;
                case 3: System.out.println("Elenco di tutte le statistiche globali");
                    break;
                case 4: System.out.println("Numero medio di consegne tra il timestamp uno e il timestamp due");
                    break;
                case 5: System.out.println("Numero medio di chilometri percorsi tra il timestamp uno e il timestamp due");
                    break;
                case 6: System.out.println("Uscire");
                    exit = true;
                    break;
                default:
                    System.out.println("Inserire un comando valido tra quelli possibili");
            }

        }

    }

}
