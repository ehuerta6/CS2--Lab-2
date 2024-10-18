import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color ACCENT_COLOR = new Color(0, 122, 255);
    private static final Color STAT_BAR_COLOR = new Color(240, 240, 240);
    private static JTextArea battleLogArea;

    private static void log(String message) {
        String timestamp = timeFormat.format(new Date());
        String logMessage = "[" + timestamp + "] " + message;
        System.out.println(logMessage);
        System.out.println("----------------------------------------");
        
        if (battleLogArea != null) {
            battleLogArea.append(logMessage + "\n");
            battleLogArea.setCaretPosition(battleLogArea.getDocument().getLength());
        }
    }

    private static void consoleLog(String message) {
        System.out.println(message);
        System.out.println("----------------------------------------");
    }

    private static void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Action Result", JOptionPane.INFORMATION_MESSAGE);
        log(message);
    }

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
        boolean[] lives = {true, true};

        if (currentGachaHero.getEspeed() > currentGachaVillain.getEspeed()) {
            consoleLog(currentGachaHero.getEname() + " attacks first due to higher speed!");
            int heroDamage = calculateDamage(currentGachaHero.getErarity(), currentGachaHero.getEattack(), currentGachaHero.getEluck(), currentGachaHero.getEspeed(), currentGachaVillain.getEdefense(), true);
            currentGachaVillain.setEhp(Math.max(0, currentGachaVillain.getEhp() - heroDamage));

            consoleLog(currentGachaHero.getEname() + " dealt " + heroDamage + " damage to " + currentGachaVillain.getEname());
            consoleLog(currentGachaVillain.getEname() + " HP: " + currentGachaVillain.getEhp());

            if (currentGachaVillain.getEhp() <= 0) {
                lives[1] = false;
                return lives;
            }

            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack() / 2, 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(Math.max(0, currentGachaHero.getEhp() - villainDamage));

            consoleLog(currentGachaVillain.getEname() + " dealt " + villainDamage + " damage to " + currentGachaHero.getEname());
            consoleLog(currentGachaHero.getEname() + " HP: " + currentGachaHero.getEhp());

            if (currentGachaHero.getEhp() <= 0) {
                lives[0] = false;
                return lives;
            }
        } else {
            consoleLog(currentGachaVillain.getEname() + " attacks first due to higher speed!");
            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack() / 2, 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(Math.max(0, currentGachaHero.getEhp() - villainDamage));

            consoleLog(currentGachaVillain.getEname() + " dealt " + villainDamage + " damage to " + currentGachaHero.getEname());
            consoleLog(currentGachaHero.getEname() + " HP: " + currentGachaHero.getEhp());

            if (currentGachaHero.getEhp() <= 0) {
                lives[0] = false;
                return lives;
            }

            int heroDamage = calculateDamage(currentGachaHero.getErarity(), currentGachaHero.getEattack(), currentGachaHero.getEluck(), currentGachaHero.getEspeed(), currentGachaVillain.getEdefense(), true);
            currentGachaVillain.setEhp(Math.max(0, currentGachaVillain.getEhp() - heroDamage));

            consoleLog(currentGachaHero.getEname() + " dealt " + heroDamage + " damage to " + currentGachaVillain.getEname());
            consoleLog(currentGachaVillain.getEname() + " HP: " + currentGachaVillain.getEhp());

            if (currentGachaVillain.getEhp() <= 0) {
                lives[1] = false;
                return lives;
            }
        }

        return lives;
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
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel startScreen = createStartScreen();
        mainPanel.add(startScreen, "startScreen");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JPanel createStartScreen() {
        JPanel startScreen = new JPanel(new GridBagLayout());
        startScreen.setBackground(BACKGROUND_COLOR);

        JButton buttonDrawHero = new JButton("Draw Hero");
        buttonDrawHero.setFont(new Font("Arial", Font.BOLD, 18));
        buttonDrawHero.setBackground(ACCENT_COLOR);
        buttonDrawHero.setForeground(Color.BLACK);
        buttonDrawHero.setFocusPainted(false);
        buttonDrawHero.setBorder(BorderFactory.createRaisedBevelBorder());

        startScreen.add(buttonDrawHero);

        buttonDrawHero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeButtonBehaviorDrawHero(gachaHeroArray, gachaPoolHero);

                JPanel imageTextScreen = createImageTextScreen((CardLayout) startScreen.getParent().getLayout(), (JPanel) startScreen.getParent());
                ((JPanel) startScreen.getParent()).add(imageTextScreen, "imageTextScreen");

                ((CardLayout) startScreen.getParent().getLayout()).show((JPanel) startScreen.getParent(), "imageTextScreen");
            }
        });

        return startScreen;
    }

    public static JPanel createImageTextScreen(CardLayout cardLayout, JPanel mainPanel) {
        // Ensure a hero is selected
        if (currentGachaHero == null) {
            currentGachaHero = gachaHeroArray[gachaPoolHero.singleDraw()];
            log("You drew a new hero: " + currentGachaHero.getEname());
        }

        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hero image
        ImageIcon originalIcon = new ImageIcon("hero-sprite.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 3));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hero name and rarity
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);

        JLabel nameLabel = new JLabel(currentGachaHero.getEname());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel rarityLabel = new JLabel(currentGachaHero.getErarity() + "-Star Hero");
        rarityLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        rarityLabel.setForeground(ACCENT_COLOR);
        rarityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(rarityLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Hero stats
        String[] statNames = {"HP", "Attack", "Defense", "Speed", "Luck", "MP"};
        int[] statValues = {
            currentGachaHero.getEhp(),
            currentGachaHero.getEattack(),
            currentGachaHero.getEdefense(),
            currentGachaHero.getEspeed(),
            currentGachaHero.getEluck(),
            currentGachaHero.getEmp()
        };

        for (int i = 0; i < statNames.length; i++) {
            infoPanel.add(createStatPanel(statNames[i], statValues[i]));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Battle button
        JButton battleButton = new JButton("BATTLE");
        battleButton.setFont(new Font("Arial", Font.BOLD, 20));
        battleButton.setBackground(ACCENT_COLOR);
        battleButton.setForeground(Color.WHITE); // Changed to white for better visibility
        battleButton.setFocusPainted(false);
        battleButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        battleButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        battleButton.addActionListener(e -> {
            JPanel battleMenu = createBattleMenu();
            mainPanel.add(battleMenu, "battleMenu");
            cardLayout.show(mainPanel, "battleMenu");
        });

        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(battleButton);

        // Add components to main panel
        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createStatPanel(String statName, int statValue) {
        JPanel statPanel = new JPanel(new BorderLayout(10, 0));
        statPanel.setBackground(BACKGROUND_COLOR);
        statPanel.setMaximumSize(new Dimension(300, 30));

        JLabel nameLabel = new JLabel(statName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel valueLabel = new JLabel(String.valueOf(statValue));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(ACCENT_COLOR);

        JProgressBar statBar = new JProgressBar(0, 100);
        statBar.setValue(statValue);
        statBar.setStringPainted(false);
        statBar.setBackground(STAT_BAR_COLOR);
        statBar.setForeground(ACCENT_COLOR);

        statPanel.add(nameLabel, BorderLayout.WEST);
        statPanel.add(statBar, BorderLayout.CENTER);
        statPanel.add(valueLabel, BorderLayout.EAST);

        return statPanel;
    }

    public static void executeButtonBehaviorDrawHero(GachaHero[] gachaHeroArray, GachaGame gachaPool) {
        System.out.println("Button was clicked! - Drawing hero");
        currentGachaHero = gachaHeroArray[gachaPool.singleDraw()];
        currentGachaHero.eprintGachaHeroInfo();
    }

    public static JPanel createBattleMenu() {
        if (currentGachaVillain == null || currentGachaVillain.getEhp() <= 0) {
            currentGachaVillain = gachaVillainArray[gachaPoolVillain.singleDraw()];
            log("A new villain appears: " + currentGachaVillain.getEname());
            showNewVillainDialog(currentGachaVillain);
        }

        JPanel battleMenu = new JPanel(new BorderLayout(20, 20));
        battleMenu.setName("battleMenu"); // Set name for identification
        battleMenu.setBackground(BACKGROUND_COLOR);
        battleMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hero Panel
        JPanel heroPanel = createCharacterPanel(currentGachaHero.getEname(), currentGachaHero.getEhp(), "hero-sprite.png");
        battleMenu.add(heroPanel, BorderLayout.WEST);

        // Villain Panel
        JPanel villainPanel = createCharacterPanel(currentGachaVillain.getEname(), currentGachaVillain.getEhp(), "villain-sprite.png");
        battleMenu.add(villainPanel, BorderLayout.EAST);

        // Battle Log
        battleLogArea = new JTextArea(10, 30);
        battleLogArea.setEditable(false);
        battleLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        battleLogArea.setBackground(new Color(240, 240, 240));
        battleLogArea.setForeground(TEXT_COLOR);
        JScrollPane scrollPane = new JScrollPane(battleLogArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Battle Log"));
        battleMenu.add(scrollPane, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        actionPanel.setBackground(BACKGROUND_COLOR);

        String[] actions = {"Attack", "Defend", "Draw New Hero", "Run"};
        for (String action : actions) {
            JButton actionButton = createActionButton(action);
            actionPanel.add(actionButton);
        }

        battleMenu.add(actionPanel, BorderLayout.SOUTH);

        return battleMenu;
    }

    private static JPanel createCharacterPanel(String name, int hp, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        panel.add(imageLabel, BorderLayout.CENTER);

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(TEXT_COLOR);
        panel.add(nameLabel, BorderLayout.NORTH);

        JProgressBar hpBar = new JProgressBar(0, 100);
        hpBar.setValue(hp);
        hpBar.setStringPainted(true);
        hpBar.setString("HP: " + hp);
        hpBar.setForeground(ACCENT_COLOR);
        panel.add(hpBar, BorderLayout.SOUTH);

        return panel;
    }

    private static JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        button.addActionListener(e -> {
            switch (text) {
                case "Attack":
                    log(currentGachaHero.getEname() + " attacks " + currentGachaVillain.getEname() + "!");
                    boolean[] outcome = attackSequence();
                    handleBattleOutcome(outcome, (JPanel) button.getParent().getParent());
                    break;
                case "Defend":
                    int originalDefense = currentGachaHero.getEdefense();
                    currentGachaHero.setEdefense(originalDefense * 2);
                    log(currentGachaHero.getEname() + " doubles their defense for this turn!");
                    outcome = attackSequence();
                    currentGachaHero.setEdefense(originalDefense);
                    handleBattleOutcome(outcome, (JPanel) button.getParent().getParent());
                    break;
                case "Draw New Hero":
                    currentGachaHero = gachaHeroArray[gachaPoolHero.singleDraw()];
                    log("You drew a new hero: " + currentGachaHero.getEname());
                    consoleLog(currentGachaHero.getEname() + " " + currentGachaHero.getErarity() + "-Star " + 
                               currentGachaHero.getEhp() + " HP, " + currentGachaHero.getEattack() + " Attack, " + 
                               currentGachaHero.getEdefense() + " Defense, " + currentGachaHero.getEspeed() + " Speed, " + 
                               currentGachaHero.getEmp() + " MP, " + currentGachaHero.getEluck() + " Luck");
                    showNewHeroDialog(currentGachaHero);
                    refreshBattleMenu((CardLayout) button.getParent().getParent().getParent().getLayout(), (JPanel) button.getParent().getParent().getParent());
                    break;
                case "Run":
                    log("You attempt to escape from " + currentGachaVillain.getEname() + "!");
                    if (Math.random() < 0.5) {
                        log("Escape successful!");
                        JPanel startScreen = createStartScreen();
                        JPanel parentPanel = (JPanel) button.getParent().getParent().getParent();
                        parentPanel.add(startScreen, "startScreen");
                        ((CardLayout) parentPanel.getLayout()).show(parentPanel, "startScreen");
                    } else {
                        log("Escape failed!");
                        outcome = attackSequence();
                        handleBattleOutcome(outcome, (JPanel) button.getParent().getParent());
                    }
                    break;
            }
        });

        return button;
    }

    private static void handleBattleOutcome(boolean[] outcome, JPanel battleMenu) {
        if (!outcome[0]) {
            log("Game Over! " + currentGachaHero.getEname() + " has been defeated.");
            showHeroDefeatScreen();
        } else if (!outcome[1]) {
            log("Victory! You defeated " + currentGachaVillain.getEname() + "!");
            showVillainDefeatScreen();
            currentGachaVillain = null; // Reset the villain
            refreshBattleMenu((CardLayout) battleMenu.getParent().getLayout(), (JPanel) battleMenu.getParent());
        } else {
            refreshBattleMenu((CardLayout) battleMenu.getParent().getLayout(), (JPanel) battleMenu.getParent());
        }
    }

    public static void refreshBattleMenu(CardLayout cardLayout, JPanel mainPanel) {
        JPanel newBattleMenu = createBattleMenu();
        mainPanel.add(newBattleMenu, "battleMenu");
        cardLayout.show(mainPanel, "battleMenu");
    }

    // Updated method to create a cooler ending screen
    public static JPanel createEndingScreen() {
        JPanel endingScreen = new JPanel(new BorderLayout());
        endingScreen.setBackground(Color.BLACK);

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setHorizontalAlignment(JLabel.CENTER);
        endingScreen.add(gameOverLabel, BorderLayout.CENTER);

        JLabel heroDeadLabel = new JLabel(currentGachaHero.getEname() + " has fallen in battle");
        heroDeadLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        heroDeadLabel.setForeground(Color.WHITE);
        heroDeadLabel.setHorizontalAlignment(JLabel.CENTER);
        endingScreen.add(heroDeadLabel, BorderLayout.SOUTH);

        return endingScreen;
    }

    private static void showNewHeroDialog(GachaHero hero) {
        JPanel heroPanel = new JPanel(new BorderLayout(10, 10));
        heroPanel.setBackground(BACKGROUND_COLOR);
        heroPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ImageIcon originalIcon = new ImageIcon("hero-sprite.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        heroPanel.add(imageLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BACKGROUND_COLOR);

        JLabel nameLabel = new JLabel(hero.getEname());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rarityLabel = new JLabel(hero.getErarity() + "-Star Hero");
        rarityLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        rarityLabel.setForeground(ACCENT_COLOR);
        rarityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(rarityLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] stats = {"HP", "Attack", "Defense", "Speed", "MP", "Luck"};
        int[] values = {hero.getEhp(), hero.getEattack(), hero.getEdefense(), hero.getEspeed(), hero.getEmp(), hero.getEluck()};

        for (int i = 0; i < stats.length; i++) {
            JPanel statPanel = createStatPanel(stats[i], values[i]);
            infoPanel.add(statPanel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        heroPanel.add(infoPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, heroPanel, "New Hero Drawn", JOptionPane.PLAIN_MESSAGE);
    }

    private static void showNewVillainDialog(GachaVillain villain) {
        JPanel villainPanel = new JPanel(new BorderLayout(10, 10));
        villainPanel.setBackground(new Color(50, 50, 50));
        villainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ImageIcon originalIcon = new ImageIcon("villain-sprite.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        villainPanel.add(imageLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(50, 50, 50));

        JLabel nameLabel = new JLabel(villain.getEname());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(Color.RED);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rarityLabel = new JLabel("Tier " + villain.getErarity() + " Villain");
        rarityLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        rarityLabel.setForeground(Color.ORANGE);
        rarityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(rarityLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] stats = {"HP", "Attack", "Defense", "Speed"};
        int[] values = {villain.getEhp(), villain.getEattack(), villain.getEdefense(), villain.getEspeed()};

        for (int i = 0; i < stats.length; i++) {
            JPanel statPanel = createStatPanel(stats[i], values[i]);
            statPanel.setBackground(new Color(50, 50, 50));
            ((JLabel)statPanel.getComponent(0)).setForeground(Color.WHITE);
            ((JLabel)statPanel.getComponent(2)).setForeground(Color.WHITE);
            infoPanel.add(statPanel);
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        villainPanel.add(infoPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, villainPanel, "New Villain Appears", JOptionPane.PLAIN_MESSAGE);
    }

    private static void showHeroDefeatScreen() {
        JPanel defeatPanel = new JPanel(new BorderLayout());
        defeatPanel.setBackground(Color.BLACK);

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel heroDefeatedLabel = new JLabel(currentGachaHero.getEname() + " has fallen in battle");
        heroDefeatedLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        heroDefeatedLabel.setForeground(Color.WHITE);
        heroDefeatedLabel.setHorizontalAlignment(JLabel.CENTER);

        defeatPanel.add(gameOverLabel, BorderLayout.CENTER);
        defeatPanel.add(heroDefeatedLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, defeatPanel, "Hero Defeated", JOptionPane.PLAIN_MESSAGE);
        System.exit(0); // Exit the game after hero defeat
    }

    private static void showVillainDefeatScreen() {
        JPanel victoryPanel = new JPanel(new BorderLayout());
        victoryPanel.setBackground(new Color(0, 100, 0)); // Dark green background

        JLabel victoryLabel = new JLabel("VICTORY!");
        victoryLabel.setFont(new Font("Arial", Font.BOLD, 48));
        victoryLabel.setForeground(Color.YELLOW);
        victoryLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel villainDefeatedLabel = new JLabel(currentGachaVillain.getEname() + " has been defeated!");
        villainDefeatedLabel.setFont(new Font("Arial", Font.ITALIC, 24));
        villainDefeatedLabel.setForeground(Color.WHITE);
        villainDefeatedLabel.setHorizontalAlignment(JLabel.CENTER);

        victoryPanel.add(victoryLabel, BorderLayout.CENTER);
        victoryPanel.add(villainDefeatedLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, victoryPanel, "Villain Defeated", JOptionPane.PLAIN_MESSAGE);
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