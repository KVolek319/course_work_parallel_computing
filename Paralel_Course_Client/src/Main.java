import java.io.*;
import java.net.Socket;

public class Main {

    public static String ipAddr = "localhost";
    public static int port = 7002;

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private BufferedReader inputUser;
    private String ip;
    private int portConnect;


    public Main(String ip, int portConnect) {
        this.ip = ip;
        this.portConnect = portConnect;

        try {
            this.socket = new Socket(ip, portConnect);
        } catch (IOException e) {
            System.err.println("Помилка з'єднання");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            new ReadFromServer().start();
            new WriteToServer().start();
        } catch (IOException e) {

            Main.this.closeConnection();
        }

    }

    private void closeConnection() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                input.close();
                output.close();
            }
        } catch (IOException ignored) {}
    }

    private class ReadFromServer extends Thread {
        public void run() {
            String str;
            try {
                while (true) {
                    str = input.readLine();
                    if (str.equals("Стоп")) {
                        Main.this.closeConnection();
                        break;
                    }
                    System.out.println(str);
                }
            } catch (IOException e) {
                Main.this.closeConnection();
            }
        }
    }

    public class WriteToServer extends Thread {

        public void run() {
            while (true) {
                String userWord;
                try {
                    userWord = inputUser.readLine();
                    if (userWord.equals("Стоп")) {
                        output.write("Стоп" + "\n");
                        Main.this.closeConnection();
                        break;
                    } else {

                        output.write(userWord + "\n");
                    }
                    output.flush();
                } catch (IOException e) {
                    Main.this.closeConnection();
                }
            }
        }
    }

    public static void main(String[] args) {
        new Main(ipAddr, port);
    }
}
