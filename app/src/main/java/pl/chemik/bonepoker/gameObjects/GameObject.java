package pl.chemik.bonepoker.gameObjects;

import pl.chemik.bonepoker.network.ServerConnect;

public class GameObject {
    private static ServerConnect serverConnect = new ServerConnect();

    public static ServerConnect getServerConnect() {
        return serverConnect;
    }
}
