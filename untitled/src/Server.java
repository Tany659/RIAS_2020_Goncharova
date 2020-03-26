import java.io.*;
import java.util.*;
import java.net.*;

public class Server {

    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private final int port = 1304;
    private ServerSocket ss;

    public Server() {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started");
            while (true) {
                Socket socket = ss.accept();
                connections.add(new Connection(socket));
                System.out.println("We have new client");
            }
        } catch (IOException ex) {
        }
    }

    private class Connection extends Thread {

        private DataInputStream input;
        private DataOutputStream output;
        private Socket socket;
        private String name = "";

        public Connection(Socket socket) {
            try {
                this.socket = socket;
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                start();
            } catch (IOException ex) {
            }
        }

        public void run() {
            try {
                output.writeUTF("Enter your nick:");
                name = input.readUTF();

                while (true) {
                    String data = input.readUTF();
                    if (data != null) {
                        if (data.equals("Send file")) {
                            receiveFile();
                        } else {
                            receiveText(data);
                        }
                    }
                }
            } catch (IOException ex) {
            }
        }

        private void receiveText(String text) {
            System.out.println(name + " send text: " + text);
            sendText(text);
        }

        private void receiveFile() {
            try {
                long fileSize = input.readLong();
                String fileName = input.readUTF();
                byte[] buffer = new byte[64 * 1024];
                FileOutputStream outputFile = new FileOutputStream(fileName);
                int count, total = 0;
                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    outputFile.write(buffer, 0, count);
                    if (total == fileSize) {
                        break;
                    }
                }
                outputFile.flush();
                outputFile.close();
            } catch (IOException ex) {
            }
        }

        private void sendText(String text) {
            try {
                Iterator<Connection> iter = Server.connections.iterator();
                while (iter.hasNext()) {
                    iter.next().output.writeUTF(name + ": " + text);
                }
            } catch (IOException ex) {
            }
        }

        private void sendFile(String filePath) {

        }
    }
}