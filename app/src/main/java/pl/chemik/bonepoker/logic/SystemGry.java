package pl.chemik.bonepoker.logic;

import java.util.ArrayList;

public class SystemGry {
    private int liczbaGraczy = 2;
    private ArrayList<Gracz> listaGraczy = new ArrayList<>();
    private int numerRundy = 0;
    private int numerTury = 0;

    public SystemGry() {
        dodajGraczy();
    }

    public SystemGry(int liczbaGraczy) {
        this.liczbaGraczy=liczbaGraczy;
        dodajGraczy();
    }

    public ArrayList<Gracz> getListaGraczy() {
        return listaGraczy;
    }

    public void dodajGracza(){
        listaGraczy.add(new Gracz());
    }

    public void dodajGracza(Gracz gracz){
        listaGraczy.add(gracz);
    }

    public void dodajGraczy(){
        for (int i=0; i < liczbaGraczy; i++) {
            listaGraczy.add(new Gracz());
        }
    }

    public int getNumerRundy() {
        return numerRundy;
    }

    public void setNumerRundy(int numerRundy) {
        this.numerRundy = numerRundy;
    }

    public int getNumerTury() {
        return numerTury;
    }

    public void setNumerTury(int numerTury) {
        this.numerTury = numerTury;
    }
}
