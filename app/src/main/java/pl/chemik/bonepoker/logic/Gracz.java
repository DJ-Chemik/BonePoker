package pl.chemik.bonepoker.logic;

import java.util.ArrayList;

public class Gracz {

    private ArrayList<Kosc> kosci = new ArrayList<>();

    public Gracz() {
        for (int i = 0; i < 5; i++) {
            kosci.add(new Kosc());
        }

    }

    public ArrayList<Kosc> getKosci() {
        return kosci;
    }

    public void losujKosc(int numerKosci){
        this.kosci.get(numerKosci).losujLiczbe();
    }
    
    public void losujWszystkieKosci(){
        for (Kosc k: kosci) {
            k.losujLiczbe();
        }
    }
}
