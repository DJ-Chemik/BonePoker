package pl.chemik.bonepoker.network;

import java.net.*;
import java.io.*;

public class ServerConnect {
    private int portServer = 1234;
    private String ipServer = "192.168.0.108";
    private Socket socket;
    private byte[] buffer = new byte[1024];

    private void preConnect() {
        InetAddress serverAddr = null;

        try {
            serverAddr = InetAddress.getByName(ipServer);

            socket = new Socket(serverAddr, portServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void connect(){
        Thread thread;
        Runnable runnable =
                () -> {this.preConnect();};
        thread=new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        thread.interrupt();
    }

    private void preSend(String toSend) {
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

    public void send(int hash){
        Thread thread;
        Runnable runnable =
                () -> {this.preSend(String.valueOf(hash));};
        thread=new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        thread.interrupt();
    }

    public void send(String hash){
        Thread thread;
        Runnable runnable = () -> this.preSend(hash);
        thread=new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        thread.interrupt();
    }

    private String preRecv(boolean czyWyswietlacInfoNaKonsoli) {
        InputStream in;
        String output = "Brak danych... :-(\n";

        try {
            in = socket.getInputStream();

            int read;
            while ((read = in.read(buffer)) != -1) {
                output = new String(buffer, 0, read);
                if (czyWyswietlacInfoNaKonsoli) {
                    System.out.print("Otrzymane dane z serwera: " + output);
                }
                System.out.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String recv(boolean czyWyswietlacInfoNaKonsoli){
        Thread thread;
        String[] result  = new String[1];
                Runnable runnable = () -> {result[0] = this.preRecv(czyWyswietlacInfoNaKonsoli);};

            thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.start();
            thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    public boolean recvSignalToStart(){
        String stringHash = recv(false);
        int hash = Integer.parseInt(stringHash);
        System.out.println(hash);
        if (hash/100000 == 2){
            return true;
        }else{
            return false;
        }
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
