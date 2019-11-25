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
        }else if (sprawdzCzyToKareta()==true){
            return new Kareta();
        }else if (sprawdzCzyToFull()==true){
            return new Full();
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
        return false;
    }
    private boolean sprawdzCzyToFull(){
        return false;
    }
    private boolean sprawdzCzyToDuzyStrit(){
        return false;
    }
    private boolean sprawdzCzyToMalyStrit(){
        return false;
    }
    private boolean sprawdzCzyToTrojka(){
        return false;
    }
    private boolean sprawdzCzyToDwiePary(){
        return false;
    }
    private boolean sprawdzCzyToPara(){
        return false;
    }
}
