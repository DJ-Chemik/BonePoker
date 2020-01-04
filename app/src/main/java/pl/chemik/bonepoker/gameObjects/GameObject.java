package pl.chemik.bonepoker.gameObjects;

import pl.chemik.bonepoker.network.ServerConnect;

public class GameObject {
    private static ServerConnect serverConnect = new ServerConnect();

    private static int mojNumerGracza = 0;
    private static int punktyGracza = 0;
    private static int punktyPrzeciwnika = 0;

    public static ServerConnect getServerConnect() {
        return serverConnect;
    }

    public static int getMojNumerGracza() {
        return mojNumerGracza;
    }

    public static void setMojNumerGracza(int mojNumerGracza) {
        GameObject.mojNumerGracza = mojNumerGracza;
    }

    public static void setServerConnect(ServerConnect serverConnect) {
        GameObject.serverConnect = serverConnect;
    }

    public static int addPunktyGracza(int ileDodac){
        punktyGracza+=ileDodac;
        return punktyGracza;
    }

    public static int addPunktyPrzeciwnika(int ileDodac){
        punktyPrzeciwnika+=ileDodac;
        return punktyPrzeciwnika;
    }

    public static int getPunktyGracza() {
        return punktyGracza;
    }

    public static void setPunktyGracza(int punktyGracza) {
        GameObject.punktyGracza = punktyGracza;
    }

    public static int getPunktyPrzeciwnika() {
        return punktyPrzeciwnika;
    }

    public static void setPunktyPrzeciwnika(int punktyPrzeciwnika) {
        GameObject.punktyPrzeciwnika = punktyPrzeciwnika;
    }
}
