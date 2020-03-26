import java.io.*;
import java.util.*;
import java.net.*;

public class Client { // Главный класс

    private final int port = 1304;
    private String address = "127.0.0.1";
    private Socket cs;

    public Client() {
        try {
            cs = new Socket(address, port);
            new InputFromServer();
            new OutputToServer();
        } catch (IOException ex) {
        }
    }

    private class InputFromServer extends Thread { // Подкласс, который принимиает информацию от сервера

        private DataInputStream input;

        public InputFromServer() {
            try {
                input = new DataInputStream(cs.getInputStream());
                start();
            } catch (IOException ex) {
            }
        }

        public void run() {
            try {
                while (true) {
                    String data = input.readUTF();
                    if (data != null) {
                        receiveText(data);
                    }
                }
            } catch (IOException ex) {
            }
        }

        private void receiveText(String text) {
            System.out.println(text);
        }

        private void receiveFile() {

        }
    }

    private class OutputToServer extends Thread { // Подкласс, который отправляет информацию серверу

        private DataOutputStream output;
        private Scanner scan;

        public OutputToServer() {
            try {
                output = new DataOutputStream(cs.getOutputStream());
                scan = new Scanner(System.in);
                start();
            } catch (IOException ex) {
            }
        }

        public void run() {
            while (true) {
                String data = scan.nextLine();
                if (data != null) {
                    if (data.equals("Send file")) {
                        sendText(data);
                        System.out.println("Enter file path:");
                        String filePath = scan.nextLine();
                        sendFile(filePath);
                    } else {
                        sendText(data);
                    }
                }
            }
        }

        private void sendText(String text) {
            try {
                output.writeUTF(text);
            } catch (IOException ex) {
            }
        }

        private void sendFile(String filePath) {
            try {
                File file = new File(filePath);
                output.writeLong(file.length());
                output.writeUTF(file.getName());
                FileInputStream inputFile = new FileInputStream(file);
                byte[] buffer = new byte[64 * 1024];
                int count;
                while ((count = inputFile.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }
                output.flush();
                inputFile.close();
            } catch (IOException ex) {
            }
        }
    }
}