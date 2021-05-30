import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {
    public static final int PORT = 7002;
    public static LinkedList<Connect> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        InvertedIndex Index;
        Index=InvertedIndex.main();


        try {
            System.out.println("Сервер запущено.");
            while (true) {
                Socket socket = server.accept();
                System.out.println("\nНовий користувач.");
                try {
                    serverList.add(new Connect(socket,Index));
                } catch (IOException e) {
                    socket.close();
                    System.out.print(e);
                }
            }
        } finally {
            server.close();
        }
    }

    static class Connect extends Thread {

        private Socket socket;
        private BufferedReader input;
        private BufferedWriter output;
        InvertedIndex Index;


        public Connect(Socket socket, InvertedIndex Ind) throws IOException {
            this.socket = socket;
            this.Index= Ind;
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));//Получение данных
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//Отправка данных
            start();
        }

        @Override
        public void run() {
            String word;
            String Answer;

            try {
                for (Connect vr : serverList) {
                    vr.SendToClient("Введіть слово для пошуку");
                }
                while (true) {
                    word = input.readLine();//Отримання даних

                    if(word.equals("Стоп")) {
                        break;
                    }

                    else{
                        Answer =InvertedIndex.search(Arrays.asList(word.split("\\W+")),Index.Index,Index.Files);
                    }

                    for (Connect vr : serverList) {
                        vr.SendToClient(Answer+"\n");
                    }
                }

            } catch (IOException e) {
                System.out.print(e);
            }
        }

        private void SendToClient(String msg) {
            try {
                output.write(msg + "\n");
                output.flush();
            } catch (IOException ignored) {}
        }
    }
}
