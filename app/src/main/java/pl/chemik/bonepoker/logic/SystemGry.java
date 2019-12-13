package pl.chemik.bonepoker.logic;

import java.util.ArrayList;

public class SystemGry {
    private int liczbaGraczy = 2;
    private ArrayList<Gracz> listaGraczy = new ArrayList<>();
    private int numerTury;

    public SystemGry() {
        dodajGraczy();
        numerTury=0;
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

    public int getNumerTury() {
        return numerTury;
    }

    public void setNumerTury(int numerTury) {
        this.numerTury = numerTury;
    }
}
