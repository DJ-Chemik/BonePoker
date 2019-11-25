package pl.chemik.bonepoker.logic;

import java.util.ArrayList;

public class System {
    private int liczbaGraczy = 2;
    private ArrayList<Gracz> listaGraczy = new ArrayList<>();

    public System() {
        dodajGraczy();
    }

    public System(int liczbaGraczy) {
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
}
