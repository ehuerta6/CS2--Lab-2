public class GachaVillain {
    // instance variables
    private String ename;       // villain's name
    private int erarity;        // villain's rarity tier
    private int ehp;            // villain's health points
    private int eattack;        // villain's attack points
    private int edefense;       // villain's defense points
    private int espeed;         // villain's speed points
    private int originalHp;

    // constructor
    public GachaVillain(String ename, int erarity, int ehp, int eattack, int edefense, int espeed) {
        this.ename = ename;      // initialize name
        this.erarity = erarity;  // initialize rarity tier
        this.ehp = ehp;          // initialize health points
        this.eattack = eattack;  // initialize attack points
        this.edefense = edefense; // initialize defense points
        this.espeed = espeed;    // initialize speed points
        this.originalHp = ehp;
    }

    // setters
    // this method sets the villain's name
    public void setEname(String ename) {
        this.ename = ename;
    }

    // this method sets the villain's rarity tier
    public void setErarity(int erarity) {
        this.erarity = erarity;
    }

    // this method sets the villain's health points
    public void setEhp(int ehp) {
        this.ehp = ehp;
    }

    // this method sets the villain's attack points
    public void setEattack(int eattack) {
        this.eattack = eattack;
    }

    // this method sets the villain's defense points
    public void setEdefense(int edefense) {
        this.edefense = edefense;
    }

    // this method sets the villain's speed points
    public void setEspeed(int espeed) {
        this.espeed = espeed;
    }

    // getters
    // this method gets the villain's name
    public String getEname() {
        return ename;
    }

    // this method gets the villain's rarity tier
    public int getErarity() {
        return erarity;
    }

    // this method gets the villain's health points
    public int getEhp() {
        return ehp;
    }

    // this method gets the villain's attack points
    public int getEattack() {
        return eattack;
    }

    // this method gets the villain's defense points
    public int getEdefense() {
        return edefense;
    }

    // this method gets the villain's speed points
    public int getEspeed() {
        return espeed;
    }

    public int getOriginalHp() {
        return originalHp;
    }

    // method to print villain details
    // this method prints detailed information about a specific villain
    public void eprintGachaVillainInfo() {
        System.out.println(ename + " " + erarity + "-Tier " + ehp + " HP, " + eattack + " Attack, " + edefense + " Defense, " + espeed + " Speed");
    }

    // method to print all villains
    // this method prints information about all villains in the provided array
    public static void eprintAllVillains(GachaVillain[] evillainsArray) {
        if (evillainsArray != null) {
            System.out.println("All Villains:");
            for (int i = 0; i < evillainsArray.length; i++) {
                evillainsArray[i].eprintGachaVillainInfo();
            }
        } else {
            System.out.println("No villains to display!!");
        }
    }
}
