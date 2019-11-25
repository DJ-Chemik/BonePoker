package pl.chemik.bonepoker.logic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import pl.chemik.bonepoker.logic.figures.DuzyStrit;
import pl.chemik.bonepoker.logic.figures.DwiePary;
import pl.chemik.bonepoker.logic.figures.Figura;
import pl.chemik.bonepoker.logic.figures.Full;
import pl.chemik.bonepoker.logic.figures.Kareta;
import pl.chemik.bonepoker.logic.figures.MalyStrit;
import pl.chemik.bonepoker.logic.figures.Nic;
import pl.chemik.bonepoker.logic.figures.Para;
import pl.chemik.bonepoker.logic.figures.Poker;
import pl.chemik.bonepoker.logic.figures.Trojka;

public class TesterFigur {

    private Gracz gracz;
    private ArrayList<Kosc> kosci = new ArrayList<>();

    public TesterFigur(Gracz gracz) {
        this.gracz = gracz;
        kosci.addAll(gracz.getKosci());
    }


    public Figura znajdzFigury(){
        posortujKosci();
        if (sprawdzCzyToPoker()==true){
            return new Poker();
        }else if (sprawdzCzyToFull()==true){ //Full Wcześniej wyszukiwany niż kareta, bo algorytm na karetę znajduje też fulla
            return new Full();
        }else if (sprawdzCzyToKareta()==true){
            return new Kareta();
        }else if (sprawdzCzyToDuzyStrit()==true){
            return new DuzyStrit();
        }else if (sprawdzCzyToMalyStrit()==true){
            return new MalyStrit();
        }else if (sprawdzCzyToTrojka()==true){
            return new Trojka();
        }else if (sprawdzCzyToDwiePary()==true){
            return new DwiePary();
        }else if (sprawdzCzyToPara()==true){
            return new Para();
        }else{
            return new Nic();
        }

    }

    private void posortujKosci(){
        Collections.sort(kosci);
    }

    private boolean sprawdzCzyToPoker(){
        for (int i = 0; i < 4; i++) {
            if (kosci.get(i).getLiczbaOczek()!=kosci.get(i+1).getLiczbaOczek()){
                return false;
            }
        }
        return true;
    }
    private boolean sprawdzCzyToKareta(){
        int licznikBledow = 2; //1 bład sprowadzi do wartości 1, a 2 bledy do 0 i algorytm sie skonczy

        for (int i = 0; i < 4; i++) {

            if (kosci.get(i).getLiczbaOczek()!=kosci.get(i+1).getLiczbaOczek()){
                licznikBledow--;
            }

            if (licznikBledow==0){
                return false;
            }
        }

        return true; //jeśli pętla sie skończy i wystąpi tylko 1 błąd to znaczy że to kareta
    }
    private boolean sprawdzCzyToFull(){

        //konfiguracja typu X-X-X-C-C
        boolean wariant1 = (kosci.get(0).getLiczbaOczek()==kosci.get(1).getLiczbaOczek())
                            && (kosci.get(1).getLiczbaOczek()==kosci.get(2).getLiczbaOczek())
                            && (kosci.get(3).getLiczbaOczek()==kosci.get(4).getLiczbaOczek());

        //konfiguracja typu X-X-C-C-C
        boolean wariant2 = (kosci.get(0).getLiczbaOczek()==kosci.get(1).getLiczbaOczek())
                && (kosci.get(2).getLiczbaOczek()==kosci.get(3).getLiczbaOczek())
                && (kosci.get(3).getLiczbaOczek()==kosci.get(4).getLiczbaOczek());

        if (wariant1 || wariant2){
            return true;
        }else {
            return false;
        }
    }
    private boolean sprawdzCzyToDuzyStrit(){
        if (kosci.get(0).getLiczbaOczek()!=2){
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (kosci.get(i+1).getLiczbaOczek()-kosci.get(i).getLiczbaOczek()!=1){
                return false;
            }
        }
        return true;

    }
    private boolean sprawdzCzyToMalyStrit(){
        if (kosci.get(0).getLiczbaOczek()!=1){
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (kosci.get(i+1).getLiczbaOczek()-kosci.get(i).getLiczbaOczek()!=1){
                return false;
            }
        }
        return true;
    }
    private boolean sprawdzCzyToTrojka(){
        int licznikBledow = 3; //1 bład sprowadzi do wartości 1, a 2 bledy do 0 i algorytm sie skonczy
        ArrayList<Kosc> trojki = new ArrayList<>();
        for (int i = 0; i < 4; i++) {

            if (kosci.get(i).getLiczbaOczek()!=kosci.get(i+1).getLiczbaOczek()){
                licznikBledow--;
            }else {
                if (!trojki.contains(kosci.get(i))){
                    trojki.add(kosci.get(i));
                }
                trojki.add(kosci.get(i+1));
            }

            if (licznikBledow==0){
                return false;
            }
        }

        if (trojki.size()==3){
            return true; //jeśli pętla sie skończy i wystąpi tylko 1 błąd to znaczy że to kareta
        }else{
            return false;
        }
    }
    private boolean sprawdzCzyToDwiePary(){
        int licznikBledow = 3; //1 bład sprowadzi do wartości 1, a 2 bledy do 0 i algorytm sie skonczy

        for (int i = 0; i < 4; i++) {

            if (kosci.get(i).getLiczbaOczek()!=kosci.get(i+1).getLiczbaOczek()){
                licznikBledow--;
            }

            if (licznikBledow==0){
                return false;
            }
        }
        return true; //jeśli pętla sie skończy i wystąpi tylko 1 błąd to znaczy że to kareta
    }
    private boolean sprawdzCzyToPara(){
        int licznikBledow = 4; //1 bład sprowadzi do wartości 1, a 2 bledy do 0 i algorytm sie skonczy

        for (int i = 0; i < 4; i++) {
            if (kosci.get(i).getLiczbaOczek()!=kosci.get(i+1).getLiczbaOczek()){
                licznikBledow--;
            }

            if (licznikBledow==0){
                return false;
            }
        }
        return true; //jeśli pętla sie skończy i wystąpi tylko 1 błąd to znaczy że to kareta
    }
}
