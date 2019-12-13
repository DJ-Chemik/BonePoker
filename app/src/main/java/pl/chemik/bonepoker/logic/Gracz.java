package pl.chemik.bonepoker.logic;

import java.util.ArrayList;

public class Gracz {

    private ArrayList<Kosc> kosci = new ArrayList<>();
    ArrayList<Integer> numeryKosciDoWymiany = new ArrayList<>();

    public Gracz() {
        for (int i = 0; i < 5; i++) {
            kosci.add(new Kosc());
        }

    }

    public ArrayList<Kosc> getKosci() {
        return kosci;
    }

    /**
     *
     * @param numerKosci - zakres 1 do 5
     * @return
     */
    public Kosc getKosc(int numerKosci){
        return kosci.get(numerKosci-1);
    }


    /**
     *
     * @param numerKosci - wartość od 1 do 5
     */
    public void losujKosc(int numerKosci){
        this.kosci.get(numerKosci-1).losujLiczbe();
    }
    
    public void losujWszystkieKosci(){
        for (Kosc k: kosci) {
            k.losujLiczbe();
        }
    }


    public ArrayList<Integer> getNumeryKosciDoWymiany() {
        return numeryKosciDoWymiany;
    }

    public void setNumeryKosciDoWymiany(ArrayList<Integer> numeryKosciDoWymiany) {
        this.numeryKosciDoWymiany = numeryKosciDoWymiany;
    }

    /**
     *
     * @param numer - wartość od 1 do 5
     */
    public void addNumerKosciDoWymiany(Integer numer){
        this.numeryKosciDoWymiany.add(numer);
    }

    /**
     *
     * @param numer - wartość od 1 do 5
     */
    public void removeNumerKosciDoWymiany(Integer numer){
        this.numeryKosciDoWymiany.remove(numer);
    }
}
