import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GachaGameBoard {
    public static GachaHero currentGachaHero;
    public static GachaVillain currentGachaVillain;
    private static GachaHero[] gachaHeroArray;
    private static GachaVillain[] gachaVillainArray;
    private static GachaGame gachaPoolHero;
    private static GachaGame gachaPoolVillain;

    // Predefined values for cycling instead of random numbers
    private static int criticalHitIndex = 0; // this variable tracks the index for cycling through critical hit values
    private static int randomFactorIndex = 0; // this variable tracks the index for cycling through random factors
    private static final int[] criticalHits = {1, 2}; // this array holds predefined critical hit values (1 or 2)
    private static final int[] randomFactors = {0, 1, 2}; // this array holds predefined random factors (0, 1, or 2)

    // this method reads hero data from the CSV file and creates an array of GachaHero objects
    public static GachaHero[] scanHero() {
        String filename = "lab2-herodataset - Sheet1.csv";
        GachaHero[] herosArray = null; // this array will store the GachaHero objects

        try {
            File heroFile = new File(filename);
            Scanner scanner = new Scanner(heroFile);

            // skip the header line
            scanner.nextLine();

            int heroCount = 0;

            // this loop counts how many heroes are in the file
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                heroCount++;
            }

            // reinitialize the scanner to read the file again
            scanner = new Scanner(heroFile);
            scanner.nextLine(); // skip header line again

            // initialize the array with the total number of heroes
            herosArray = new GachaHero[heroCount];
            int index = 0;

            // this loop reads each line from the file, splits the attributes, and creates a GachaHero object
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] attributes = line.split(",");

                String heroName = attributes[0];
                int heroRarity = Integer.parseInt(attributes[1].split("-")[0]);
                int heroHP = Integer.parseInt(attributes[2]);
                int heroAttack = Integer.parseInt(attributes[3]);
                int heroDefense = Integer.parseInt(attributes[4]);
                int heroSpeed = Integer.parseInt(attributes[5]);
                int heroMP = Integer.parseInt(attributes[6]);
                int heroLuck = Integer.parseInt(attributes[7]);

                // create a new GachaHero object with the extracted attributes
                GachaHero hero = new GachaHero(heroName, heroRarity, heroHP, heroAttack, heroDefense, heroSpeed, heroMP, heroLuck);

                // add the new hero to the array
                herosArray[index] = hero;
                index++;
            }

            // close the scanner to prevent resource leak
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename); // print error if the file is not found
        }

        return herosArray; // return the array of GachaHero objects
    }

    // this method reads villain data from the CSV file and creates an array of GachaVillain objects
    public static GachaVillain[] escanVillain() {
        String filename = "lab2-villaindataset - Sheet1.csv";
        GachaVillain[] villainsArray = null; // this array will store the GachaVillain objects

        try {
            File villainFile = new File(filename);
            Scanner scanner = new Scanner(villainFile);

            // skip the header line
            scanner.nextLine();
            int villainCount = 0;

            // this loop counts how many villains are in the file
            while (scanner.hasNextLine()) {
                scanner.nextLine();
                villainCount++;
            }

            // reinitialize the scanner to read the file again
            scanner = new Scanner(villainFile);
            scanner.nextLine(); // skip header line again

            // initialize the array with the total number of villains
            villainsArray = new GachaVillain[villainCount];
            int index = 0;

            // this loop reads each line from the file, splits the attributes, and creates a GachaVillain object
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] attributes = line.split(",");

                String villainName = attributes[0];
                int villainRarity = Integer.parseInt(attributes[1].split("-")[0]);
                int villainHP = Integer.parseInt(attributes[2]);
                int villainAttack = Integer.parseInt(attributes[3]);
                int villainDefense = Integer.parseInt(attributes[4]);
                int villainSpeed = Integer.parseInt(attributes[5]);

                // create a new GachaVillain object with the extracted attributes
                GachaVillain villain = new GachaVillain(villainName, villainRarity, villainHP, villainAttack, villainDefense, villainSpeed);

                // add the new villain to the array
                villainsArray[index] = villain;
                index++;
            }

            // close the scanner to prevent resource leak
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename); // print error if the file is not found
        }

        return villainsArray; // return the array of GachaVillain objects
    }

    // this method simulates an attack sequence between the current hero and villain
    public static boolean[] attackSequence() {
        boolean[] lives = {true, true}; // this array tracks if the hero and villain are still alive

        // this block handles the scenario when the hero's speed is greater than the villain's
        if (currentGachaHero.getEspeed() > currentGachaVillain.getEspeed()) {
            int heroDamage = calculateDamage(currentGachaHero.getErarity(), currentGachaHero.getEattack(), currentGachaHero.getEluck(), currentGachaHero.getEspeed(), currentGachaVillain.getEdefense(), true);
            currentGachaVillain.setEhp(currentGachaVillain.getEhp() - heroDamage); // apply the hero's damage to the villain

            System.out.println(currentGachaHero.getEname() + " dealt " + heroDamage + " damage to " + currentGachaVillain.getEname());

            // check if the villain is defeated
            if (currentGachaVillain.getEhp() <= 0) {
                lives[1] = false; // villain is dead
                return lives;
            }

            // villain attacks back with reduced power
            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack() / 2, 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(currentGachaHero.getEhp() - villainDamage); // apply the villain's damage to the hero

            System.out.println(currentGachaVillain.getEname() + " dealt " + villainDamage + " damage to " + currentGachaHero.getEname());

            // check if the hero is defeated
            if (currentGachaHero.getEhp() <= 0) {
                lives[0] = false; // hero is dead
                return lives;
            }
        } else {
            // this block handles the scenario when the villain's speed is greater or equal
            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack() / 2, 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(currentGachaHero.getEhp() - villainDamage);

            System.out.println(currentGachaVillain.getEname() + " dealt " + villainDamage + " damage to " + currentGachaHero.getEname());

            if (currentGachaHero.getEhp() <= 0) {
                lives[0] = false;
                return lives;
            }

            // hero attacks after the villain
            int heroDamage = calculateDamage(currentGachaHero.getErarity(), currentGachaHero.getEattack(), currentGachaHero.getEluck(), currentGachaHero.getEspeed(), currentGachaVillain.getEdefense(), true);
            currentGachaVillain.setEhp(currentGachaVillain.getEhp() - heroDamage);

            System.out.println(currentGachaHero.getEname() + " dealt " + heroDamage + " damage to " + currentGachaVillain.getEname());

            if (currentGachaVillain.getEhp() <= 0) {
                lives[1] = false;
                return lives;
            }
        }

        return lives; // return the updated lives array
    }

    // this method calculates the damage based on rarity, attack, luck, speed, defense, and whether the attacker is a hero
    public static int calculateDamage(int rarity, int attack, int luck, int speed, int defense, boolean isHero) {
        int critical = getNextCritical(); // get the next critical hit value
        int randomFactor = getNextRandomFactor(); // get the next random factor

        // calculate the damage using a formula that considers various attributes
        double damage = (rarity + attack + luck) * 0.2 * (speed * 0.4 + critical * 0.5) + randomFactor * 3;

        // adjust the damage based on the defense of the opponent
        if (defense > 0) {
            damage = damage - (defense / 2);
        }

        // ensure the damage is not negative
        if (damage < 0) {
            damage = 1;
        }

        return (int) damage; // return the calculated damage as an integer
    }

    // this method cycles through predefined critical hit values
    public static int getNextCritical() {
        int value = criticalHits[criticalHitIndex]; // get the current critical hit value
        criticalHitIndex = (criticalHitIndex + 1) % criticalHits.length; // update the index for the next call
        return value; // return the critical hit value
    }

    // this method cycles through predefined random factors
    public static int getNextRandomFactor() {
        int value = randomFactors[randomFactorIndex]; // get the current random factor
        randomFactorIndex = (randomFactorIndex + 1) % randomFactors.length; // update the index for the next call
        return value; // return the random factor
    }

    /**
     * DO NOT TOUCH CODE
     */
    public static void makeGame(GachaHero[] heroArray, GachaVillain[] villainArray, GachaGame poolHero, GachaGame poolVillain) {
        gachaHeroArray = heroArray;
        gachaVillainArray = villainArray;
        gachaPoolHero = poolHero;
        gachaPoolVillain = poolVillain;

        JFrame frame = new JFrame("CS2 Gacha Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        JPanel startScreen = new JPanel();
        startScreen.setLayout(new GridBagLayout());

        JButton buttonDrawHero = new JButton("Draw Hero");
        startScreen.add(buttonDrawHero);

        mainPanel.add(startScreen, "startScreen");

        buttonDrawHero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeButtonBehaviorDrawHero(gachaHeroArray, gachaPoolHero);

                JPanel imageTextScreen = createImageTextScreen(cardLayout, mainPanel, gachaPoolVillain, gachaVillainArray);
                mainPanel.add(imageTextScreen, "imageTextScreen");

                cardLayout.show(mainPanel, "imageTextScreen");
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    public static JPanel createImageTextScreen(CardLayout cardLayout, JPanel mainPanel, GachaGame gachaPoolVillain, GachaVillain[] gachaVillainArray) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        ImageIcon image = new ImageIcon("hero-sprite.png");
        JLabel imageLabel = new JLabel(image);

        JLabel textLabel = new JLabel("<html>YOU HAVE DRAWN " + currentGachaHero.getEname() + " He is a " + currentGachaHero.getErarity() + "-Star" +
            " Hero with the following stats " +
            " HP: " + currentGachaHero.getEhp() + " ATTACK: " + currentGachaHero.getEattack() + " DEFENSE: " + currentGachaHero.getEdefense() +
            " SPEED: " + currentGachaHero.getEspeed() + " LUCK: " + currentGachaHero.getEluck() + "</html>", SwingConstants.CENTER);
        textLabel.setFont(new Font("Serif", Font.BOLD, 24));

        JButton battleButton = new JButton("BATTLE");
        battleButton.setFont(new Font("Serif", Font.BOLD, 20));

        battleButton.addActionListener(e -> {
            JPanel battleMenu = createBattleMenu();
            mainPanel.add(battleMenu, "battleMenu");
            cardLayout.show(mainPanel, "battleMenu");
        });

        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(textLabel, BorderLayout.NORTH);
        panel.add(battleButton, BorderLayout.SOUTH);

        return panel;
    }

    public static void executeButtonBehaviorDrawHero(GachaHero[] gachaHeroArray, GachaGame gachaPool) {
        System.out.println("Button was clicked! - Drawing hero");
        currentGachaHero = gachaHeroArray[gachaPool.singleDraw()];
        currentGachaHero.eprintGachaHeroInfo();
    }

    public static JPanel createBattleMenu() {
        if (currentGachaVillain == null || currentGachaVillain.getEhp() <= 0) {
            System.out.println("Battle was clicked! - Drawing Villain");
            currentGachaVillain = gachaVillainArray[gachaPoolVillain.singleDraw()];
            currentGachaVillain.eprintGachaVillainInfo();
        }

        JPanel battleMenu = new JPanel();
        battleMenu.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10));

        JButton attackButton = new JButton("Attack");
        JButton defendButton = new JButton("Defend");
        JButton itemButton = new JButton("Use Draw");
        JButton runButton = new JButton("Run");

        attackButton.addActionListener(e -> {
            boolean[] outcome = attackSequence();
            if (!outcome[0]) {
                System.err.println("The hero is dead");
                JPanel endingScreen = createEndingScreen();
                JPanel parentPanel = (JPanel) battleMenu.getParent();
                parentPanel.add(endingScreen, "endingScreen");
                ((CardLayout) parentPanel.getLayout()).show(parentPanel, "endingScreen");
            } else {
                System.err.println("Refresh the page");
                refreshBattleMenu((CardLayout) battleMenu.getParent().getLayout(), (JPanel) battleMenu.getParent());
            }
        });

        defendButton.addActionListener(e -> {
            currentGachaHero.setEdefense(currentGachaHero.getEdefense() * 2);
            System.out.println(currentGachaHero.getEname() + " doubled their defense for this turn!");
            boolean[] outcome = attackSequence();
            currentGachaHero.setEdefense(currentGachaHero.getEdefense() / 2);
            handleBattleOutcome(outcome, battleMenu);
        });

        itemButton.addActionListener(e -> {
            System.out.println("You used a Draw!");
            int healAmount = 50 + (int)(Math.random() * 51); // Random heal between 50-100
            currentGachaHero.setEhp(currentGachaHero.getEhp() + healAmount);
            System.out.println(currentGachaHero.getEname() + " healed for " + healAmount + " HP!");
            boolean[] outcome = attackSequence();
            handleBattleOutcome(outcome, battleMenu);
        });

        runButton.addActionListener(e -> {
            System.out.println("You tried to run away!");
            if (Math.random() < 0.5) {
                System.out.println("Escape successful!");
                JPanel startScreen = createStartScreen();
                JPanel parentPanel = (JPanel) battleMenu.getParent();
                parentPanel.add(startScreen, "startScreen");
                ((CardLayout) parentPanel.getLayout()).show(parentPanel, "startScreen");
            } else {
                System.out.println("Escape failed!");
                boolean[] outcome = attackSequence();
                handleBattleOutcome(outcome, battleMenu);
            }
        });

        buttonPanel.add(attackButton);
        buttonPanel.add(defendButton);
        buttonPanel.add(itemButton);
        buttonPanel.add(runButton);

        battleMenu.add(buttonPanel, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        ImageIcon heroImage = new ImageIcon("hero-sprite.png");
        JLabel heroImageLabel = new JLabel(heroImage);

        JLabel heroHPLabel = new JLabel(currentGachaHero.getEname() + " HP: " + currentGachaHero.getEhp());
        heroHPLabel.setFont(new Font("Serif", Font.BOLD, 18));

        leftPanel.add(heroImageLabel, BorderLayout.CENTER);
        leftPanel.add(heroHPLabel, BorderLayout.SOUTH);

        battleMenu.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        ImageIcon villainImage = new ImageIcon("villain-sprite.png");
        JLabel villainImageLabel = new JLabel(villainImage);

        JLabel villainHPLabel = new JLabel(currentGachaVillain.getEname() + " HP: " + currentGachaVillain.getEhp());
        villainHPLabel.setFont(new Font("Serif", Font.BOLD, 18));

        rightPanel.add(villainImageLabel, BorderLayout.CENTER);
        rightPanel.add(villainHPLabel, BorderLayout.SOUTH);

        battleMenu.add(rightPanel, BorderLayout.EAST);

        return battleMenu;
    }

    private static void handleBattleOutcome(boolean[] outcome, JPanel battleMenu) {
        if (!outcome[0]) {
            System.err.println("The hero is dead");
            JPanel endingScreen = createEndingScreen();
            JPanel parentPanel = (JPanel) battleMenu.getParent();
            parentPanel.add(endingScreen, "endingScreen");
            ((CardLayout) parentPanel.getLayout()).show(parentPanel, "endingScreen");
        } else if (!outcome[1]) {
            System.out.println("The villain is defeated!");
            JPanel victoryScreen = createVictoryScreen();
            JPanel parentPanel = (JPanel) battleMenu.getParent();
            parentPanel.add(victoryScreen, "victoryScreen");
            ((CardLayout) parentPanel.getLayout()).show(parentPanel, "victoryScreen");
        } else {
            System.err.println("Refresh the page");
            refreshBattleMenu((CardLayout) battleMenu.getParent().getLayout(), (JPanel) battleMenu.getParent());
        }
    }

    private static JPanel createStartScreen() {
        JPanel startScreen = new JPanel();
        startScreen.setLayout(new GridBagLayout());

        JButton buttonDrawHero = new JButton("Draw Hero");
        startScreen.add(buttonDrawHero);

        buttonDrawHero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeButtonBehaviorDrawHero(gachaHeroArray, gachaPoolHero);

                JPanel imageTextScreen = createImageTextScreen(
                    (CardLayout) startScreen.getParent().getLayout(),
                    (JPanel) startScreen.getParent(),
                    new GachaGame(),  // Add a new GachaGame instance
                    new GachaVillain[0]  // Add an empty GachaVillain array or initialize it properly
                );
                ((JPanel) startScreen.getParent()).add(imageTextScreen, "imageTextScreen");

                ((CardLayout) startScreen.getParent().getLayout()).show((JPanel) startScreen.getParent(), "imageTextScreen");
            }
        });

        return startScreen;
    }

    private static JPanel createVictoryScreen() {
        JPanel victoryScreen = new JPanel();
        victoryScreen.setLayout(new BorderLayout());

        JLabel victoryLabel = new JLabel("You defeated the villain!", SwingConstants.CENTER);
        victoryLabel.setFont(new Font("Serif", Font.BOLD, 24));

        JButton continueButton = new JButton("Continue");
        continueButton.addActionListener(e -> {
            JPanel parentPanel = (JPanel) victoryScreen.getParent();
            JPanel startScreen = createStartScreen();
            parentPanel.add(startScreen, "startScreen");
            ((CardLayout) parentPanel.getLayout()).show(parentPanel, "startScreen");
        });

        victoryScreen.add(victoryLabel, BorderLayout.CENTER);
        victoryScreen.add(continueButton, BorderLayout.SOUTH);

        return victoryScreen;
    }

    // Method to refresh the battle menu
    public static void refreshBattleMenu(CardLayout cardLayout, JPanel mainPanel) {
        JPanel battleMenu = createBattleMenu();
        Component[] components = mainPanel.getComponents(); 
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getName() != null && ((JPanel) component).getName().equals("battleMenu")) { 
                mainPanel.remove(component); 
                break; 
            } 
        }
        mainPanel.add(battleMenu, "battleMenu");
        cardLayout.show(mainPanel, "battleMenu");
    }

    // Method to create the ending screen
    public static JPanel createEndingScreen() {
        JPanel endingScreen = new JPanel();
        endingScreen.add(new JLabel("The hero is dead. Game Over."));
        return endingScreen;
    }

    /**
    *---- END OF DO NOT TOUCH CODE -------------------------------------------------------------------------------
    */

    public static void main(String[] args) {
        // GachaHero and GachaVillain arrays (Task 3 - 4) will be declared here
        GachaHero[] gachaHeroArray = scanHero();
        GachaVillain[] gachaVillainArray = escanVillain();
        
        // Uncomment below at Task 5 to initialize the game
        GachaGame gachaPoolHero = new GachaGame();
        GachaGame gachaPoolVillain = new GachaGame();
        makeGame(gachaHeroArray, gachaVillainArray, gachaPoolHero, gachaPoolVillain);
    }
}
