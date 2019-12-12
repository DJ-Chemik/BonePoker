package pl.chemik.bonepoker.logic.figures;

public class HashGenerator {

    private int hash; //cały hash
    private int hash0; //cyfra najbardziej po lewo
    private int hash1;
    private int hash2a;
    private int hash2b;
    private int hash3a;
    private int hash3b;//cyfra najbardziej na prawo
    public final int PREFIX_INFORMATION_C2S = 1;
    public final int PREFIX_CONFIGURATION_C2S = 9;

    public HashGenerator() {
        hash0 = 7;
        hash1 = 0;
        hash2a = 0;
        hash2b = 0;
        hash3a = 0;
        hash3b = 0;
    }


    public int getHash() {
        hash = (hash0 * (100000)) + (hash1 * (10000)) + (hash2a * (1000)) + (hash2b * (100)) +
                (hash3a * (10)) + (hash3b * (1));
        return hash;
    }

    public void setHash0(int hash0) {
        this.hash0 = hash0;
    }

    public void setHash1(int hash1) {
        this.hash1 = hash1;
    }

    public void setHash2a(int hash2a) {
        this.hash2a = hash2a;
    }

    public void setHash2b(int hash2b) {
        this.hash2b = hash2b;
    }

    public void setHash3a(int hash3a) {
        this.hash3a = hash3a;
    }

    public void setHash3b(int hash3b) {
        this.hash3b = hash3b;
    }

    //////////////////////////

    public int getHash0() {
        return hash0;
    }

    public int getHash1() {
        return hash1;
    }

    public int getHash2a() {
        return hash2a;
    }

    public int getHash2b() {
        return hash2b;
    }

    public int getHash3a() {
        return hash3a;
    }

    public int getHash3b() {
        return hash3b;
    }
}
