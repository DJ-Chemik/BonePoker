package pl.chemik.bonepoker.logic;

import java.util.Random;

public class Kosc implements Comparable<Kosc>{
    private int liczbaOczek = 0;

    public Kosc() {
        losujLiczbe();
    }

    public void losujLiczbe(){
        Random generator = new Random();
        liczbaOczek=generator.nextInt(6)+1; //zakres 1-6
    }

    public int getLiczbaOczek() {
        return liczbaOczek;
    }


    @Override
    public int compareTo(Kosc compareKosc) {
        int compareLiczbaOczek = compareKosc.getLiczbaOczek();

        return this.liczbaOczek - compareLiczbaOczek;
    }
}
