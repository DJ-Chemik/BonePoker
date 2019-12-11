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
import pl.chemik.bonepoker.logic.figures.HashGenerator;
import pl.chemik.bonepoker.logic.figures.Kareta;
import pl.chemik.bonepoker.logic.figures.MalyStrit;
import pl.chemik.bonepoker.logic.figures.Nic;
import pl.chemik.bonepoker.logic.figures.Para;
import pl.chemik.bonepoker.logic.figures.Poker;
import pl.chemik.bonepoker.logic.figures.Trojka;

public class TesterFigur {

    private Gracz gracz;
    private ArrayList<Kosc> kosci = new ArrayList<>();
    private HashGenerator hashGenerator = new HashGenerator();

    public TesterFigur(Gracz gracz) {
        this.gracz = gracz;
        kosci.addAll(gracz.getKosci());
    }


    public Figura znajdzFigury(){
        posortujKosci();
        if (sprawdzCzyToPoker()==true){
            hashGenerator.setHash1(8);
            hashGenerator.setHash2(znajdzLiczbeWiodacaPokera());
            return new Poker();
        }else if (sprawdzCzyToFull()==true){ //Full Wcześniej wyszukiwany niż kareta, bo algorytm na karetę znajduje też fulla
            hashGenerator.setHash1(6);
            hashGenerator.setHash2(znajdzLiczbeWiodacaFulla());
            return new Full();
        }else if (sprawdzCzyToKareta()==true){
            hashGenerator.setHash1(7);
            hashGenerator.setHash2(znajdzLiczbeWiodacaKarety());
            return new Kareta();
        }else if (sprawdzCzyToDuzyStrit()==true){
            hashGenerator.setHash1(5);
            hashGenerator.setHash2(znajdzLiczbeWiodacaDuzegoStrita());
            return new DuzyStrit();
        }else if (sprawdzCzyToMalyStrit()==true){
            hashGenerator.setHash1(4);
            hashGenerator.setHash2(znajdzLiczbeWiodacaMalegoStrita());
            return new MalyStrit();
        }else if (sprawdzCzyToTrojka()==true){
            hashGenerator.setHash1(3);
            hashGenerator.setHash2(znajdzLiczbeWiodacaTrojki());
            return new Trojka();
        }else if (sprawdzCzyToDwiePary()==true){
            hashGenerator.setHash1(2);
            hashGenerator.setHash2(znajdzLiczbeWiodacaDwochPar());
            return new DwiePary();
        }else if (sprawdzCzyToPara()==true){
            hashGenerator.setHash1(1);
            hashGenerator.setHash2(znajdzLiczbeWiodacaPary());
            return new Para();
        }else{
            hashGenerator.setHash1(0);
            hashGenerator.setHash2(znajdzLiczbeWiodacaNiczego());
            return new Nic();
        }

    }

    /**
     * Poker [1-6]
     * @return w zależności z jakich kości składa się Poker
     */
    private int znajdzLiczbeWiodacaPokera(){
        return kosci.get(0).getLiczbaOczek();
    }

    /**
     * Kareta [1-6]
     * @return w zależności z jakich kości składa się Kareta
     */
    private int znajdzLiczbeWiodacaKarety(){
        int liczbaZKtorejSkladaSieKareta=0;
        for (int i = 0; i < kosci.size()-1; i++) {
            if(kosci.get(i)==kosci.get(i+1)){
                liczbaZKtorejSkladaSieKareta = kosci.get(i).getLiczbaOczek();
                break;
            }
        }
        return liczbaZKtorejSkladaSieKareta;
    }

    /**
     *  Full [1-6]
     * @return w zależności z jakich kości składa się Trójka w tym Fullu
     */
    private int znajdzLiczbeWiodacaFulla(){
        int liczbaZKtorejSkladaSieTrojka=0;
        for (int i = 0; i < kosci.size()-1; i++) {
            if(kosci.get(i)==kosci.get(i+1) && kosci.get(i+1)==kosci.get(i+2)){
                liczbaZKtorejSkladaSieTrojka = kosci.get(i).getLiczbaOczek();
                break;
            }
        }
        return liczbaZKtorejSkladaSieTrojka;
    }

    /**
     * Duży Strit [0]
     * @return ---- zawsze 0
     */
    private int znajdzLiczbeWiodacaDuzegoStrita(){
        return 0;
    }

    /**
     * Mały Strit [0]
     * @return ----  zawsze 0
     */
    private int znajdzLiczbeWiodacaMalegoStrita(){
        return 0;
    }

    /**
     * Trójka [1-6]
     * @return w zależności z jakich kości składa się Trójka
     */
    private int znajdzLiczbeWiodacaTrojki(){
        int liczbaZKtorejSkladaSieTrojka=0;
        for (int i = 0; i < kosci.size()-1; i++) {
            if(kosci.get(i)==kosci.get(i+1)){
                liczbaZKtorejSkladaSieTrojka = kosci.get(i).getLiczbaOczek();
                break;
            }
        }
        return liczbaZKtorejSkladaSieTrojka;
    }

    /**
     * Dwie Pary [2-12]
     * @return jedna para daje liczbe [1-6] i druga tez. Później sumujemy i suma jest wynikiem
     */
    private int znajdzLiczbeWiodacaDwochPar(){
        int liczbaZKtorejSkladaSiePara1=0;
        int liczbaZKtorejSkladaSiePara2=0;
        for (int i = 0; i < kosci.size()-1; i++) {
            if(kosci.get(i)==kosci.get(i+1) && liczbaZKtorejSkladaSiePara1==0){
                liczbaZKtorejSkladaSiePara1 = kosci.get(i).getLiczbaOczek();
            }
            if(kosci.get(i)==kosci.get(i+1) && liczbaZKtorejSkladaSiePara1!=0){
                liczbaZKtorejSkladaSiePara1 = kosci.get(i).getLiczbaOczek();
            }
        }
        return liczbaZKtorejSkladaSiePara1+liczbaZKtorejSkladaSiePara2;
    }

    /**
     * Para [1-6]
     * @return w zależności z jakich liczb składa się Para
     */
    private int znajdzLiczbeWiodacaPary(){
        int liczbaZKtorejSkladaSiePara=0;
        for (int i = 0; i < kosci.size()-1; i++) {
            if(kosci.get(i)==kosci.get(i+1)){
                liczbaZKtorejSkladaSiePara = kosci.get(i).getLiczbaOczek();
                break;
            }
        }
        return liczbaZKtorejSkladaSiePara;
    }

    /**
     * Nic [5-30]
     * @return suma wszystkich oczek
     */
    private int znajdzLiczbeWiodacaNiczego(){
        int suma=0;
        for (Kosc k: kosci) {
            suma+=k.getLiczbaOczek();
        }
        return suma;
    }

    public String znajdzFiguryIZwrocNazwe(){
        posortujKosci();
        if (sprawdzCzyToPoker()==true){
            hashGenerator.setHash1(8);
            return "Poker";
        }else if (sprawdzCzyToFull()==true){ //Full Wcześniej wyszukiwany niż kareta, bo algorytm na karetę znajduje też fulla
            hashGenerator.setHash1(6);
            return "Full";
        }else if (sprawdzCzyToKareta()==true){
            hashGenerator.setHash1(7);
            return "Kareta";
        }else if (sprawdzCzyToDuzyStrit()==true){
            hashGenerator.setHash1(5);
            return "Duży Strit";
        }else if (sprawdzCzyToMalyStrit()==true){
            hashGenerator.setHash1(4);
            return "Mały Strit";
        }else if (sprawdzCzyToTrojka()==true){
            hashGenerator.setHash1(3);
            return "Trójka";
        }else if (sprawdzCzyToDwiePary()==true){
            hashGenerator.setHash1(2);
            return "Dwie Pary";
        }else if (sprawdzCzyToPara()==true){
            hashGenerator.setHash1(1);
            return "Para";
        }else{
            hashGenerator.setHash1(0);
            return "Nic :(";
        }
    }

    public HashGenerator getHashGenerator() {
        return hashGenerator;
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
