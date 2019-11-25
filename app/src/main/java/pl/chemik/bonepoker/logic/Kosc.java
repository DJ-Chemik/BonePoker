package pl.chemik.bonepoker.logic;

import java.util.Random;

public class Kosc {
    private int liczbaOczek = 0;

    public Kosc() {
        losujLiczbe();
    }

    public void losujLiczbe(){
        Random generator = new Random();
        liczbaOczek=generator.nextInt(5)+1; //zakres 1-6
    }

    public int getLiczbaOczek() {
        return liczbaOczek;
    }

}
