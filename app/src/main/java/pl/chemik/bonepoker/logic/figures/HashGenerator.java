package pl.chemik.bonepoker.logic.figures;

import pl.chemik.bonepoker.logic.TesterFigur;

public class HashGenerator {

    private int hash; //cały hash
    private int hash0; //cyfra najbardziej po lewo
    private int hash1;
    private int hash2;
    private int hash3; //cyfra najbardziej na prawo

    public HashGenerator() {
        hash0=7;
        hash1=0;
        hash2=0;
        hash3=0;
    }


    public int getHash() {
        hash = (hash0 * 1000) + (hash1 * 100) + (hash2 * 10) + hash3;
        return hash;
    }

    public void setHash0(int hash0) {
        this.hash0 = hash0;
    }

    public void setHash1(int hash1) {
        this.hash1 = hash1;
    }

    public void setHash2(int hash2) {
        this.hash2 = hash2;
    }

    public void setHash3(int hash3) {
        this.hash3 = hash3;
    }
}
