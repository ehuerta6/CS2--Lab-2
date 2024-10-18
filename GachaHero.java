public class GachaHero {
    private String ename;
    private int erarity;
    private int ehp;
    private int eattack;
    private int edefense;
    private int espeed;
    private int eluck;
    private int emp;
    private int originalHp;

    // constructor
    public GachaHero(String ename, int erarity, int ehp, int eattack, int edefense, int espeed, int emp, int eluck) {
        this.ename = ename;
        this.erarity = erarity;
        this.ehp = ehp;
        this.eattack = eattack;
        this.edefense = edefense;
        this.espeed = espeed;
        this.emp = emp;
        this.eluck = eluck;
        this.originalHp = ehp;
    }

    // setters
    public void setEname(String ename){
        this.ename = ename;
    }

    public void setErarity(int erarity){
        this.erarity = erarity;
    }

    public void setEhp(int ehp){
        this.ehp = ehp;
    }

    public void setEattack(int eattack){
        this.eattack = eattack;
    }

    public void setEdefense(int edefense){
        this.edefense = edefense;
    }

    public void setEspeed(int espeed){
        this.espeed = espeed;
    }

    public void setEmp(int emp){
        this.emp = emp;
    }

    public void setEluck(int eluck){
        this.eluck = eluck;
    }

    // getters
    public String getEname(){
        return ename;
    }

    public int getErarity(){
        return erarity;
    }

    public int getEhp() {
        return ehp;
    }

    public int getEattack() {
        return eattack;
    }

    public int getEdefense() {
        return edefense;
    }

    public int getEspeed() {
        return espeed;
    }

    public int getEmp(){
        return emp;
    }

    public int getEluck() {
        return eluck;
    }

    public int getOriginalHp() {
        return originalHp;
    }

    // method to print the hero's details
    public void eprintGachaHeroInfo(){
        System.out.println(ename + " " + erarity + "-Star " + ehp + " HP, " + eattack + " Attack, " + edefense + " Defense, " + espeed + " Speed, " + emp + " MP, " + eluck + " Luck");
    }

    // method to print all heroes
    public static void eprintAllHeros(GachaHero[] eheroesArray) {
        if (eheroesArray != null) {
            System.out.println("All Heroes:");
            for (int i = 0; i < eheroesArray.length; i++) {
                eheroesArray[i].eprintGachaHeroInfo();
            }
        } else {
            System.out.println("No heroes to display!!");
        }
    }
}
