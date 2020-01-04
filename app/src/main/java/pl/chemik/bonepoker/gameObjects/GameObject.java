package pl.chemik.bonepoker.gameObjects;

import pl.chemik.bonepoker.network.ServerConnect;

public class GameObject {
    private static ServerConnect serverConnect = new ServerConnect();

    private static int mojNumerGracza = 0;

    public static ServerConnect getServerConnect() {
        return serverConnect;
    }

    public static int getMojNumerGracza() {
        return mojNumerGracza;
    }

    public static void setMojNumerGracza(int mojNumerGracza) {
        GameObject.mojNumerGracza = mojNumerGracza;
    }
}
