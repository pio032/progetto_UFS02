import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class Server_Socket {

    // creazione variabile di controllo globale
    public static boolean lose = false;
    public static boolean colpo = false;

    // scrittra messaggi sul file
    public static void WriteFile(String username, String password, String nome, int win) throws IOException {
     
        /*
         * leggo il file, concateno la stringa e riscrivo --> stesso codice che ho nel
         * main per la lettura
         */

        String jsonContent = "[" + "{\n" +
                "  \"username\": \"" + username + "\",\n" +
                "  \"password\": \"" + password + "\",\n" +
                "  \"vittorie\": \"" + String.valueOf(win) + "\"\n" +

                "}" + "]";

        // Scrivere nel file JSON
        try (FileWriter writer = new FileWriter("./" + nome + ".json")) {
            writer.write(jsonContent);
            System.out.println("File JSON scritto correttamente!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // aggiungi vittoria, aggionra file json e cincrementa la vittoria
    //DA RIVEDERE 
    public static void updateWin(String username, String nuoveVittorie) throws IOException {
            File file = new File(username+".json");

            String jsonString = new String(Files.readAllBytes(file.toPath()));
            // Trova la parte corrispondente all'utente con lo username specificato
            int startIndex = jsonString.indexOf("\"username\": \"" + username + "\"");
                        
            if (startIndex != -1) {
                // Trova l'inizio e la fine del campo "vittorie" per quell'utente
                int vittorieIndex = jsonString.indexOf("\"vittorie\":", startIndex);
                int endIndex=0;
                if (vittorieIndex != -1) {
                    // Trova la fine del valore "vittorie"
                     endIndex = jsonString.indexOf(",", vittorieIndex);
                    if (endIndex == -1) {
                        // Se non c'è la virgola (campo "vittorie" è l'ultimo), trova la parentesi chiusa
                        endIndex = jsonString.indexOf("}", vittorieIndex);
                    }
                }
                  // prendo il valore corrente delle vittorie
                String currentVittorieString = jsonString.substring(vittorieIndex + "\"vittorie\":".length(), endIndex).trim();
                System.out.println("il valore delle vittorie attuali sono: "+currentVittorieString);
                    // Costruisci la nuova stringa con il valore aggiornato
                    int x = Integer.parseInt(nuoveVittorie);
                    String updatedJsonString = "["+"{"+jsonString.substring(x, vittorieIndex + 11) // fino a "vittorie":
                            + "\"" + nuoveVittorie + "\"" +"}"+"]";  // nuovo valore di vittorie
                    //controllo doppio graffa
                    boolean ctrl = false;
                    for (int i =0; i<updatedJsonString.length(); i++){
                        if (updatedJsonString.charAt(i)=='{') {
                            if (updatedJsonString.charAt(i+1)=='{') {
                                ctrl = true;

                            }
                        }
                        if (ctrl) {
                            //oggetto String builder per modificare la stringa
                            StringBuilder sb = new StringBuilder(updatedJsonString);
                            sb.setCharAt((i+1), ' ');
                            updatedJsonString=sb.toString();
                            break;
                        }

                    }

                    // Scrive la stringa aggiornata nel file
                  
                    Files.write(file.toPath(), updatedJsonString.getBytes());

                    System.out.println("Vittorie aggiornate per l'utente " + username);
                }


    }
    //VERIFICA L ESISTENZA DI UN UTENTE E NEL CASO NON CI FOSSE LI CREA IL FILE!
     public static boolean UserExist(String username){
     boolean tr=false;
     String path = "./"+username+".json";
     File a = new File(path);
     if(a.exists()){
     tr=true;
     }else{
    try {
        a.createNewFile();
    } catch (IOException e) {
        e.printStackTrace();
    }
     }
     return tr;
     }
     public static int getWin(String username) throws IOException{
        File file = new File("./"+username + ".json");
        String currentVittorieString="";
        String toInt="";
        // Leggi il contenuto del file JSON
        String jsonString = new String(Files.readAllBytes(file.toPath()));

        // Trova la posizione dell'utente specificato
        int startIndex = jsonString.indexOf("\"username\": \"" + username + "\"");

        if (startIndex != -1) {
            // Trova la posizione del campo "vittorie" per quell'utente
            int vittorieIndex = jsonString.indexOf("\"vittorie\":", startIndex);

            if (vittorieIndex != -1) {
                // Trova la fine del valore "vittorie"
                int endIndex = jsonString.indexOf(",", vittorieIndex);
                if (endIndex == -1) {
                    // Se non c'è la virgola (campo "vittorie" è l'ultimo), trova la parentesi chiusa
                    endIndex = jsonString.indexOf("}", vittorieIndex);
                }

                // Estrai il valore attuale di "vittorie" (come stringa)
                 currentVittorieString = jsonString.substring(vittorieIndex + "\"vittorie\":".length(), endIndex).trim();
                
                 for(int i=0; i<currentVittorieString.length(); i++){
                    if (currentVittorieString.charAt(i)!='"') {
                        toInt = toInt + currentVittorieString.charAt(i);
                    }
                 }
                System.out.println("Valore attuale di 'vittorie': " + Integer.parseInt(toInt));
            } else {
                System.out.println("Campo 'vittorie' non trovato per l'utente " + username);
            }
        } else {
            System.out.println("Utente con username '" + username + "' non trovato.");
        }
    return Integer.parseInt(toInt);
     }
    public static boolean login(String username, String pw){
    boolean tr = false;
            // Percorso del file JSON
            String filePath = "./"+username+".json";
        
            // Leggi il file come una stringa
            try {
                String jsonString = new String(Files.readAllBytes(new File(filePath).toPath()));
                
                // Rimuovere i caratteri inutili (es. spazi e parentesi)
                jsonString = jsonString.trim();
                
                // Rimuoviamo le parentesi graffe
                jsonString = jsonString.substring(1, jsonString.length() - 1).trim();
                
                // Separiamo le coppie chiave-valore
                String[] pairs = jsonString.split(",");
                System.out.println("eccomi qui! sono io");
                for (String pair : pairs) {
                    // Separiamo la chiave dal valore
                    String[] keyValue = pair.split(":");
                    
                    // Rimuoviamo gli spazi e i doppi apici
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    
                    // Stampa la chiave e il valore
                    System.out.println(key + ": " + value);
                    if(key.equals("password") && value.equals(pw)){
                        tr = true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Errore nella lettura del file JSON: " + e.getMessage());
            }
    return tr;
    }



    // leggo dal file json
    public static String ReadFile(String usr) throws IOException {
        usr = "./" + usr + ".json";
        StringBuilder contenuto = new StringBuilder(); // oggetto lettura
        try (BufferedReader reader = new BufferedReader(new FileReader(usr))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contenuto.append(line).append("\n");
            }
        }
        return contenuto.toString();
    }
     
      //estrai la key dalla stringa
    public static String extractKey(String estratto, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        String valore = "";
        // cerco la corrispondenza del pattern con la chiave e il valore
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = r.matcher(estratto);
        if (m.find()) {
            valore = m.group(1); // Estrai il valore catturato
        }
        return valore;
    }
    /*
     * 
     * 
     * INIZIO   DEL     GIOCO   
     * 
     * 
     */
    // SPOSTAMENTI NAVICELLA E CAMPO !!

    public static char[][] scendi(char[][] campo) {
        for (int i = 0; i < 10; i++) {
            for (int j = 9; j > -1; j--) {
                try {
                    if (campo[j][i] == '#') {
                        if (campo[j + 1][i] == '@') {
                            lose = true;
                            campo[j][i] = '.';
                            campo[j + 1][i] = '#';
                        }
                        campo[j][i] = '.';
                        campo[j + 1][i] = '#';
                    }
                } catch (IndexOutOfBoundsException e) {
                    lose = true;
                }
            }
        }
        return campo;
    }

    // generazione campo
    public static char[][] StartGame() {
        char[][] campo = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 9 && j == 9) {
                    campo[i][j] = '@';
                } else {

                    campo[i][j] = '.';
                }

            }
        }
        return campo;
    }

    // generzioneNemici
    public static char[][] gen(char[][] campo) {
        for (int i = 0; i < 25; i++) { 
            int c = (int) (Math.random() * 10); // 0 a 9
            int r = (int) (Math.random() * 5);  // 0 a 4
            if (campo[r][c] == '#') {
                i--;
            } else {
                campo[r][c] = '#';
            }
        }
        return campo;
    }

    // spostamento della navicella
    public static char[][] updateCampo(char[][] campo, String a) {

        if ('a' == a.charAt(0)) {
            // spostamento sx
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (campo[i][j] == '@') {
                        int spostamento = a.length();
                        try {
                            if (campo[i][j - spostamento] == '#') {
                                lose = true;
                            } else {
                                campo[i][j] = '.';
                                campo[i][j - spostamento] = '@';
                            }
                        } catch (IndexOutOfBoundsException e) {
                            campo[i][j] = '@';

                        }
                    }
                }
            }

        } else {
            // spostamento dx
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (campo[i][j] == '@') {
                        try {
                            int spostamento = a.length();
                            campo[i][j] = '.';
                            campo[i][j + spostamento] = '@';
                        } catch (IndexOutOfBoundsException e) {
                            campo[i][j] = '@';

                        }
                        break;
                    }
                }
            }
        }

        return campo;
    }

    // comparsa del colpo sulla matrice
    public static char[][] shot(char[][] campo) {
        // ricerca della navicella
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (campo[i][j] == '@') {
                    if (campo[i - 1][j] == '#') {
                        campo[i - 1][j] = '.'; // tolgo l alieno e metto una stella
                        colpo = true;
                    } else {
                        campo[i - 1][j] = '-'; // se non c'è alieno compare la bomba, SEMPRE PENULTIMA RIGA
                    }

                }
            }
        }
        return campo;
    }

    // il colpo viene fatto salire verso l alto
    public static char[][] colpisci_nemico(char[][] campo) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (campo[i][j] == '-') {
                    try {
                        if (campo[i - 1][j] == '.') {
                            campo[i - 1][j] = '-';
                            campo[i][j] = '.';
                        } else {
                            campo[i - 1][j] = '.';
                            campo[i][j] = '.';
                            colpo = true;
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {

                        campo[i][j] = '.';
                        colpo = true;
                        break;

                    }
                    break;
                }
            }
        }
        return campo;
    }

    public static boolean check(char[][] campo) {
        boolean tr = false;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (campo[i][j] == '#') {
                    tr = true;
                }
            }
        }
        if (!tr) {
            return true;
        } else {
            return false;
        }
    }
     /*
      *         THREAD & NETWORK
      */
    // creazione class handler
    public static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        // thread run
        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                boolean a = true;
                String userF = "";
                int win = 0;

                // LOGIN
                out.print("Ciao! Dimmi il tuo nome utente: \n");
                out.flush();
                String user = in.readLine();
                out.print("Ciao! Dimmi la tua password: \n");
                out.flush();
                String pw = in.readLine();
                boolean scelta = false;
                // Verifica se l'utente esiste
                if(UserExist(user)){
               if(login(user, pw)){
                //login avvenuto con successo
                out.print("Benvenuto " + user + "\n");
                userF = user;
                win = getWin(userF);
                out.print("Il numero delle tue vittorie è: " + win + "\n");
                 scelta = true;
               }else{
                //login fail la password è sbagliata
                out.print("Hai sbagliato la password! rieffetua il login \n");
                 scelta = false;
               }
                }else{
                    out.print("Non hai ancora un utente? Nessun problema, te lo creo io! Il tuo username è: " + user
                    + "\n\n");
                    userF = user;
                        if (!user.equals("null") && !pw.equals("")) {
                            WriteFile(user, pw, user, 0);
                        }
                         scelta = true;
                }

              
               
                while (scelta) { 
                System.out.println("sini di nuovo qua!");    
                
                // stampo il campo di gioco
                char[][] campo = StartGame();
                // lo riempio di nemici
                char[][] finito = gen(campo);
                while (a) {
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            if (finito[i][j] == '@') {
                                out.print("🚀");

                            } else {
                                if (finito[i][j] == '#') {
                                    out.print("👽");

                                } else {
                                    out.print("🌟");

                                }

                            }

                        }
                        out.println();
                    }
                    // in utente
                    String clientMessage = (String) in.readLine();

                    if (!clientMessage.equals(" ")) {
                        // sposto navicella
                        finito = updateCampo(finito, clientMessage);
                    } else {
                        // colpo
                        finito = shot(finito);
                        // sale il colpo
                        while (!colpo) {
                            finito = colpisci_nemico(finito);
                            for (int i = 0; i < 10; i++) {
                                for (int j = 0; j < 10; j++) {
                                    if (finito[i][j] == '@') {
                                        out.print("🚀");

                                    } else {
                                        if (finito[i][j] == '#') {
                                            out.print("👽");

                                        } else {
                                            if (finito[i][j] == '-') {
                                                out.print("💣");
                                            } else {
                                                out.print("🌟");
                                            }

                                        }

                                    }
                                }
                                out.println();
                            }
                            Thread.sleep(900);
                            out.println();
                            out.println();
                        }
                        colpo = false;
                    }

                    finito = scendi(finito);
                    // conotrollo perdita
                    if (lose) {
                        a = false;
                        out.println("mi dispiace hai perso, i nemici sono arrivati!");
                    }
                    Thread.sleep(1000);
                    // verifica vittoria
                    if (check(finito)) {
                        out.println("Hai vinto!!!");
                        win = win + 1;

                        // invio dei dati al metodo
                        updateWin(user, String.valueOf(win));
                        a = false;
                        break;
                    }
                    
                }
                System.out.println("passato!!");
                   out.println("ciao vuoi continuare a giocare? (1)si (2)no");
                    String sce= in.readLine();
                    if (sce.equals("1")) {
                    out.println("ok riniziamo! \n"); 
                    a=true;   
                    }else{
                    out.println("va bene, alla prossima! \n");
                    scelta=false;
                    a=false;
                    this.clientSocket.close();
                    }
                // Chiudi le connessioni e gli stream
                
            }
            } catch (IOException ex) {
            } catch (InterruptedException ex) {
            }
        
        }
    }

    // MAIN
    public static void main(String[] args) throws InterruptedException, IOException {
        // Porta su cui il server ascolta
        int port = 1234;
        try {
            // Crea un ServerSocket che ascolta sulla porta specificata
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server in ascolto sulla porta " + port);

            // Accetta nuove connessioni e gestisci ogni connessione con un nuovo thread
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connesso da " + clientSocket.getInetAddress());
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}