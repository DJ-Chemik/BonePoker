package pl.chemik.bonepoker.network;

import java.net.*;
import java.io.*;

public class ServerConnect {
    private int portServer = 1234;
    private String ipServer = "192.168.0.108";
    private Socket socket;
    private byte[] buffer = new byte[1024];


    public void connect(){
        InetAddress serverAddr = null;

        try {
            serverAddr = InetAddress.getByName(ipServer);

            socket = new Socket(serverAddr, portServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void send(String toSend){
        OutputStream outputstream = null;
        try {
            outputstream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter out = new PrintWriter(outputstream);

        out.write(toSend);
        out.flush();
    }

    public String recv(){
        InputStream in;
        String output = "Brak danych... :-(\n";

        try {
            in = socket.getInputStream();

            int read;
            while ((read = in.read(buffer)) != -1) {
                output = new String(buffer, 0, read);
                System.out.print(output);
                System.out.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }


    public int getPortServer() {
        return portServer;
    }

    public void setPortServer(int portServer) {
        this.portServer = portServer;
    }

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }
}
