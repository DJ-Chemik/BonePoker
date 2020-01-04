package pl.chemik.bonepoker.network;

import java.net.*;
import java.io.*;

import pl.chemik.bonepoker.gameObjects.GameObject;

public class ServerConnect {
    private int portServer = 1234;
    private String ipServer = "192.168.0.108";
    private Socket socket;
    private byte[] buffer = new byte[1024];

    //Connection
    public static final int HASH_CONNECT = 911111; //klient pyta czy może dołaczyć
    public static final int HASH_C1_IS_STILL_WAIT = 910011; //klient 1 pyta czy nadal czekać
    public static final int HASH_C2_IS_STILL_WAIT = 920022; //klient 2 pyta czy nadal czekać
    public static final int HASH_PLAYER_NUMBER_1 = 910000; //serwer wysyła do klienta info że jest graczem pierwszym
    public static final int HASH_PLAYER_NUMBER_2 = 920000; //serwer wysyła do klienta info że jest graczem drugim
    public static final int HASH_WAIT_FOR_OPPONENT = 990000; //serwer wysyła informacje żeby gracz 1 czekał na podłączenie gracza 2
    public static final int HASH_OPPONENT_IS_READY = 991111; //serwer wysyła informacje do gracza 1 że gracz 2 sie juz podłączył do gry
     //Game
     public static final int HASH0_PLAYER_1_RESULT = 1; //tak zaczyna się hash gdy klient 1 lub serwer do niego przysyła wyniki
     public static final int HASH0_PLAYER_2_RESULT = 2; //tak zaczyna się hash gdy klient 2 lub serwer do niego przysyła wyniki
     //End
     public static final int HASH_END_GAME = 999999; //Zakończenie gry

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
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void preClose(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        Thread thread;
        Runnable runnable =
                () -> {this.preClose();};
        thread=new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            //thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private int shouldStillWainting(){

        int sekunda = 1000;
        int ileSekundCzekac = 1*sekunda;
        try {
            Thread.sleep(ileSekundCzekac);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GameObject.getServerConnect().connect();
        if (GameObject.getMojNumerGracza()==1){
            GameObject.getServerConnect().send(ServerConnect.HASH_C1_IS_STILL_WAIT);
        }else if (GameObject.getMojNumerGracza()==2){
            GameObject.getServerConnect().send(ServerConnect.HASH_C2_IS_STILL_WAIT);
        }
        String stringHash = GameObject.getServerConnect().recv(false);
        return Integer.parseInt(stringHash);
    }

    public boolean recvSignalToStart(){
        String stringHash = recv(false);
        int hash = Integer.parseInt(stringHash);
        //System.out.println(hash);
        while(true){
            if (hash == HASH_PLAYER_NUMBER_1){
                GameObject.setMojNumerGracza(1);
                hash = shouldStillWainting();
                continue;
            }else if (hash == HASH_PLAYER_NUMBER_2){
                GameObject.setMojNumerGracza(2);
                return true;
            }else if (hash == HASH_WAIT_FOR_OPPONENT){
                hash = shouldStillWainting();
                continue;
            }else if (hash == HASH_OPPONENT_IS_READY){
                return true;
            }else{
                return false;
            }
        }

    }

    public String recvResult() {
        String stringHash = recv(false);
        int hash = Integer.parseInt(stringHash);

        if (hash%100000/10000!=9){
            return stringHash;
        }

        while (true) {
            hash = shouldStillWainting();
            if (hash == HASH_WAIT_FOR_OPPONENT) {
                continue;
            }else if(hash==-1) {
                System.out.println("BLEDNY OTZRYMANY HASH");
                continue;
            }else{
                return String.valueOf(hash);
            }

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
