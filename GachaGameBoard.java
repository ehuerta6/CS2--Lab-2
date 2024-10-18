import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.border.TitledBorder;

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
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static JProgressBar heroHPBar;
    private static JProgressBar villainHPBar;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static final Color STAR_1_COLOR = new Color(0, 191, 255);  // Deep Sky Blue
    private static final Color STAR_2_COLOR = new Color(50, 205, 50);  // Lime Green
    private static final Color STAR_3_COLOR = new Color(138, 43, 226); // Blue Violet
    private static final Color STAR_4_COLOR = new Color(255, 215, 0);  // Gold
    private static final Color STAR_5_COLOR = new Color(255, 0, 0);    // Red

    private static final Color HERO_COLOR = new Color(0, 100, 255);  // Blue
    private static final Color VILLAIN_COLOR = new Color(255, 0, 0);  // Red

    private static StringBuilder battleLog = new StringBuilder();

    private static int villainsDefeated = 0; // Add this line to keep track of defeated villains

    private static int drawChances = 1; // Add this line to track draw chances
    private static int runChances = 1; // Add this line to track run chances

    private static void log(String message) {
        String timestamp = timeFormat.format(new Date());
        String logMessage = "[" + timestamp + "] " + message;
        
        // Color coding for console output
        String consoleMessage = logMessage;
        if (message.contains("attacks")) {
            consoleMessage = ANSI_RED + logMessage + ANSI_RESET;
        } else if (message.contains("dealt")) {
            consoleMessage = ANSI_YELLOW + logMessage + ANSI_RESET;
        } else if (message.contains("HP:")) {
            consoleMessage = ANSI_GREEN + logMessage + ANSI_RESET;
        } else if (message.contains("appears") || message.contains("drew")) {
            consoleMessage = ANSI_BLUE + logMessage + ANSI_RESET;
        }
        
        System.out.println(consoleMessage);
        System.out.println(ANSI_YELLOW + "Villains Defeated: " + villainsDefeated + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "----------------------------------------" + ANSI_RESET);
        
        battleLog.append(logMessage).append("\n");
        if (battleLogArea != null) {
            battleLogArea.setText(battleLog.toString());
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
        StringBuilder battleSequence = new StringBuilder();

        if (currentGachaHero.getEspeed() >= currentGachaVillain.getEspeed()) {
            // Hero attacks first
            int heroDamage = calculateDamage(currentGachaHero.getErarity(), currentGachaHero.getEattack(), currentGachaHero.getEluck(), currentGachaHero.getEspeed(), currentGachaVillain.getEdefense(), true);
            currentGachaVillain.setEhp(Math.max(0, currentGachaVillain.getEhp() - heroDamage));
            
            battleSequence.append(currentGachaHero.getEname()).append(" attacks ").append(currentGachaVillain.getEname()).append("!\n");
            battleSequence.append(currentGachaHero.getEname()).append(" dealt ").append(heroDamage).append(" damage to ").append(currentGachaVillain.getEname()).append("\n");
            battleSequence.append(currentGachaVillain.getEname()).append(" HP: ").append(currentGachaVillain.getEhp()).append("/").append(currentGachaVillain.getOriginalHp()).append("\n");

            if (currentGachaVillain.getEhp() <= 0) {
                lives[1] = false;
                log(battleSequence.toString());
                return lives;
            }

            // Villain counterattacks
            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack(), 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(Math.max(0, currentGachaHero.getEhp() - villainDamage));

            battleSequence.append(currentGachaVillain.getEname()).append(" counterattacks ").append(currentGachaHero.getEname()).append("!\n");
            battleSequence.append(currentGachaVillain.getEname()).append(" dealt ").append(villainDamage).append(" damage to ").append(currentGachaHero.getEname()).append("\n");
            battleSequence.append(currentGachaHero.getEname()).append(" HP: ").append(currentGachaHero.getEhp()).append("/").append(currentGachaHero.getOriginalHp()).append("\n");

            if (currentGachaHero.getEhp() <= 0) {
                lives[0] = false;
            }
        } else {
            // Villain attacks first
            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack(), 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(Math.max(0, currentGachaHero.getEhp() - villainDamage));

            battleSequence.append(currentGachaVillain.getEname()).append(" attacks ").append(currentGachaHero.getEname()).append("!\n");
            battleSequence.append(currentGachaVillain.getEname()).append(" dealt ").append(villainDamage).append(" damage to ").append(currentGachaHero.getEname()).append("\n");
            battleSequence.append(currentGachaHero.getEname()).append(" HP: ").append(currentGachaHero.getEhp()).append("/").append(currentGachaHero.getOriginalHp()).append("\n");

            if (currentGachaHero.getEhp() <= 0) {
                lives[0] = false;
                log(battleSequence.toString());
                return lives;
            }

            // Hero counterattacks
            int heroDamage = calculateDamage(currentGachaHero.getErarity(), currentGachaHero.getEattack(), currentGachaHero.getEluck(), currentGachaHero.getEspeed(), currentGachaVillain.getEdefense(), true);
            currentGachaVillain.setEhp(Math.max(0, currentGachaVillain.getEhp() - heroDamage));

            battleSequence.append(currentGachaHero.getEname()).append(" counterattacks ").append(currentGachaVillain.getEname()).append("!\n");
            battleSequence.append(currentGachaHero.getEname()).append(" dealt ").append(heroDamage).append(" damage to ").append(currentGachaVillain.getEname()).append("\n");
            battleSequence.append(currentGachaVillain.getEname()).append(" HP: ").append(currentGachaVillain.getEhp()).append("/").append(currentGachaVillain.getOriginalHp()).append("\n");

            if (currentGachaVillain.getEhp() <= 0) {
                lives[1] = false;
            }
        }

        log(battleSequence.toString());
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
            resetCharacterHP(currentGachaVillain);
            battleLog = new StringBuilder();
            log("A new villain appears: " + currentGachaVillain.getEname());
            showNewVillainDialog(currentGachaVillain);
        }

        JPanel battleMenu = new JPanel(new BorderLayout(20, 20));
        battleMenu.setBackground(new Color(40, 44, 52)); // Dark background
        battleMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add villain defeat counter, draw chances, and run chances
        JPanel counterPanel = new JPanel(new GridLayout(1, 3));
        counterPanel.setOpaque(false);

        JLabel defeatCountLabel = new JLabel("Villains Defeated: " + villainsDefeated);
        JLabel drawChancesLabel = new JLabel("Draw Chances: " + drawChances);
        JLabel runChancesLabel = new JLabel("Run Chances: " + runChances);

        for (JLabel label : new JLabel[]{defeatCountLabel, drawChancesLabel, runChancesLabel}) {
            label.setForeground(Color.WHITE);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setHorizontalAlignment(JLabel.CENTER);
            counterPanel.add(label);
        }

        battleMenu.add(counterPanel, BorderLayout.NORTH);

        // Center panel for character images, names, HP bars, and stats
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(createCharacterPanel(currentGachaHero, true));
        centerPanel.add(createCharacterPanel(currentGachaVillain, false));
        battleMenu.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for action buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        actionPanel.setOpaque(false);

        String[] actions = {"Attack", "Defend", "Draw New Hero", "Run"};
        Color[] buttonColors = {
            new Color(231, 76, 60),  // Red for Attack
            new Color(52, 152, 219), // Blue for Defend
            new Color(46, 204, 113), // Green for Draw New Hero
            new Color(155, 89, 182)  // Purple for Run
        };

        for (int i = 0; i < actions.length; i++) {
            JButton actionButton = createActionButton(actions[i], buttonColors[i]);
            actionPanel.add(actionButton);
        }

        battleMenu.add(actionPanel, BorderLayout.SOUTH);

        return battleMenu;
    }

    private static JPanel createCharacterPanel(Object character, boolean isHero) {
        JPanel characterPanel = new JPanel(new BorderLayout(10, 10));
        characterPanel.setOpaque(false);

        // Name and Tier Panel
        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        namePanel.setOpaque(false);

        String name = isHero ? ((GachaHero)character).getEname() : ((GachaVillain)character).getEname();
        int rarity = isHero ? ((GachaHero)character).getErarity() : ((GachaVillain)character).getErarity();
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel tierLabel = new JLabel("Tier " + rarity);
        tierLabel.setForeground(getStarColor(rarity));
        tierLabel.setHorizontalAlignment(JLabel.CENTER);
        tierLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        namePanel.add(nameLabel);
        namePanel.add(tierLabel);
        characterPanel.add(namePanel, BorderLayout.NORTH);

        // Image
        String imagePath = isHero ? "hero-sprite.png" : "villain-sprite.png";
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        characterPanel.add(imageLabel, BorderLayout.CENTER);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(isHero ? 5 : 3, 1, 5, 5));
        statsPanel.setOpaque(false);

        String[] statNames = isHero ? 
            new String[]{"Attack", "Defense", "Speed", "MP", "Luck"} :
            new String[]{"Attack", "Defense", "Speed"};
        int[] statValues;

        if (isHero) {
            GachaHero hero = (GachaHero) character;
            statValues = new int[]{hero.getEattack(), hero.getEdefense(), hero.getEspeed(), hero.getEmp(), hero.getEluck()};
        } else {
            GachaVillain villain = (GachaVillain) character;
            statValues = new int[]{villain.getEattack(), villain.getEdefense(), villain.getEspeed()};
        }

        for (int i = 0; i < statNames.length; i++) {
            JPanel statPanel = createColoredStatPanel(statNames[i], statValues[i], rarity);
            statsPanel.add(statPanel);
        }

        characterPanel.add(statsPanel, BorderLayout.EAST);

        // HP Bar
        JProgressBar hpBar = new JProgressBar(0, isHero ? ((GachaHero)character).getOriginalHp() : ((GachaVillain)character).getOriginalHp());
        hpBar.setValue(isHero ? ((GachaHero)character).getEhp() : ((GachaVillain)character).getEhp());
        hpBar.setStringPainted(true);
        hpBar.setString("HP: " + hpBar.getValue() + " / " + hpBar.getMaximum());
        hpBar.setForeground(isHero ? new Color(46, 204, 113) : new Color(231, 76, 60));
        hpBar.setBackground(new Color(44, 62, 80));
        characterPanel.add(hpBar, BorderLayout.SOUTH);

        if (isHero) {
            heroHPBar = hpBar;
        } else {
            villainHPBar = hpBar;
        }

        return characterPanel;
    }

    private static JPanel createColoredStatPanel(String statName, int value, int rarity) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        JLabel nameLabel = new JLabel(statName + ":");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setPreferredSize(new Dimension(60, 20));
        panel.add(nameLabel, BorderLayout.WEST);

        JProgressBar statBar = new JProgressBar(0, 100);
        statBar.setValue(value);
        statBar.setStringPainted(true);
        statBar.setString(String.valueOf(value));
        statBar.setForeground(getStarColor(rarity));
        statBar.setBackground(new Color(44, 62, 80));
        panel.add(statBar, BorderLayout.CENTER);

        return panel;
    }

    private static Color getStarColor(int rarity) {
        switch (rarity) {
            case 1: return STAR_1_COLOR;
            case 2: return STAR_2_COLOR;
            case 3: return STAR_3_COLOR;
            case 4: return STAR_4_COLOR;
            case 5: return STAR_5_COLOR;
            default: return Color.GRAY;
        }
    }

    private static JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        button.addActionListener(e -> {
            switch (text) {
                case "Attack":
                    boolean[] outcome = attackSequence();
                    handleBattleOutcome(outcome);
                    break;
                case "Defend":
                    defend();
                    break;
                case "Draw New Hero":
                    if (drawChances > 0) {
                        currentGachaHero = gachaHeroArray[gachaPoolHero.singleDraw()];
                        resetCharacterHP(currentGachaHero);
                        log("You drew a new hero: " + currentGachaHero.getEname());
                        showNewHeroDialog(currentGachaHero);
                        drawChances--;
                        refreshBattleMenu();
                    } else {
                        showNoChancesDialog("Draw", "draw a new hero");
                    }
                    break;
                case "Run":
                    if (runChances > 0) {
                        attemptRun();
                    } else {
                        showNoChancesDialog("Run", "attempt to run");
                    }
                    break;
            }
        });

        return button;
    }

    private static void handleBattleOutcome(boolean[] lives) {
        if (!lives[0]) {
            showDefeatScreen();
            resetGameState();
            SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "mainMenu"));
        } else if (!lives[1]) {
            villainsDefeated++;
            showVictoryScreen();
            currentGachaVillain = gachaVillainArray[gachaPoolVillain.singleDraw()];
            resetCharacterHP(currentGachaVillain);
            showNewVillainDialog(currentGachaVillain);
            drawChances = 1;
            runChances = 1;
            refreshBattleMenu();
        } else {
            refreshBattleMenu();
        }
    }

    private static void showNoChancesDialog(String action, String actionDescription) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 40));
        panel.setPreferredSize(new Dimension(400, 200));
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        JLabel messageLabel = new JLabel("<html><center>No " + action.toLowerCase() + " chances remaining!<br>Defeat the current villain to earn another chance to " + actionDescription + ".</center></html>");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(messageLabel, BorderLayout.CENTER);

        ImageIcon lockIcon = new ImageIcon("lock-icon.png");
        if (lockIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image scaledImage = lockIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            panel.add(iconLabel, BorderLayout.WEST);
        }

        JOptionPane.showMessageDialog(mainFrame, panel, "No " + action + " Chances", JOptionPane.PLAIN_MESSAGE);
    }

    /**
    *---- END OF DO NOT TOUCH CODE -------------------------------------------------------------------------------
    */

    private static void initializeGame() {
        gachaHeroArray = scanHero();
        gachaVillainArray = escanVillain();
        gachaPoolHero = new GachaGame();
        gachaPoolVillain = new GachaGame();
    }

    public static void startGame() {
        if (mainFrame == null) {
            initializeGame();

            mainFrame = new JFrame("Gacha Game");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(800, 600);

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);
            JPanel mainMenu = createMainMenu();
            mainPanel.add(mainMenu, "mainMenu");

            mainFrame.add(mainPanel);
            mainFrame.setVisible(true);
        }
        cardLayout.show(mainPanel, "mainMenu");
    }

    private static JPanel createMainMenu() {
        JPanel mainMenu = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 70), w, h, new Color(60, 60, 120));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };

        // Title
        JLabel titleLabel = new JLabel("GACHA GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        mainMenu.add(titleLabel, BorderLayout.NORTH);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton startButton = createMenuButton("Start Game", new Color(0, 150, 0));
        JButton instructionsButton = createMenuButton("How to Play", new Color(0, 100, 150));
        JButton exitButton = createMenuButton("Exit Game", new Color(150, 0, 0));

        buttonPanel.add(startButton, gbc);
        buttonPanel.add(instructionsButton, gbc);
        buttonPanel.add(exitButton, gbc);

        mainMenu.add(buttonPanel, BorderLayout.CENTER);

        // Button Actions
        startButton.addActionListener(e -> {
            currentGachaHero = gachaHeroArray[gachaPoolHero.singleDraw()];
            resetCharacterHP(currentGachaHero);
            showNewHeroDialog(currentGachaHero);
            JPanel battleMenu = createBattleMenu();
            mainPanel.add(battleMenu, "battleMenu");
            cardLayout.show(mainPanel, "battleMenu");
        });

        instructionsButton.addActionListener(e -> showInstructions());

        exitButton.addActionListener(e -> showExitGameDialog());

        return mainMenu;
    }

    private static JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(250, 60));
        return button;
    }

    private static void showInstructions() {
        String instructions = 
            "Welcome to Gacha Game!\n\n" +
            "1. Start the game by clicking 'Start Game'.\n" +
            "2. You'll be assigned a random hero to battle with.\n" +
            "3. In battle, you can choose to:\n" +
            "   - Attack: Deal damage to the villain\n" +
            "   - Defend: Increase your defense for one turn\n" +
            "   - Draw New Hero: Replace your current hero\n" +
            "   - Run: Attempt to escape the battle\n" +
            "4. Defeat villains to win, but be careful not to let your hero's HP reach 0!\n" +
            "5. Have fun and may the gacha odds be ever in your favor!";

        JTextArea textArea = new JTextArea(instructions);
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 70));
        panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(mainFrame, panel, "How to Play", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> startGame());
    }

    private static void resetCharacterHP(GachaHero hero) {
        hero.setEhp(hero.getOriginalHp());
    }

    private static void resetCharacterHP(GachaVillain villain) {
        villain.setEhp(villain.getOriginalHp());
    }

    private static void updateHPBar(JProgressBar hpBar, int currentHP, int maxHP) {
        hpBar.setValue(currentHP);
        hpBar.setString("HP: " + currentHP + " / " + maxHP);
    }

    private static void attemptRun() {
        int escapeChance = 50; // 50% chance to escape
        if (Math.random() * 100 < escapeChance) {
            log(currentGachaHero.getEname() + " successfully escaped from " + currentGachaVillain.getEname() + "!");
            showEscapeSuccessScreen();
            runChances--;
            resetGameState();
            SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "mainMenu"));
        } else {
            log(currentGachaHero.getEname() + " failed to escape from " + currentGachaVillain.getEname() + "!");
            showEscapeFailScreen();
            runChances--;
            // The villain gets a free attack
            int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack(), 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
            currentGachaHero.setEhp(Math.max(0, currentGachaHero.getEhp() - villainDamage));
            log(currentGachaVillain.getEname() + " dealt " + villainDamage + " damage to " + currentGachaHero.getEname());
            log(currentGachaHero.getEname() + " HP: " + currentGachaHero.getEhp() + "/" + currentGachaHero.getOriginalHp());
            
            if (currentGachaHero.getEhp() <= 0) {
                handleBattleOutcome(new boolean[]{false, true});
            } else {
                refreshBattleMenu();
            }
        }
    }

    private static void defend() {
        log(currentGachaHero.getEname() + " takes a defensive stance.");
        // Increase defense for one turn (you can implement this logic)
        // For example, you could temporarily increase the hero's defense by 50%
        int originalDefense = currentGachaHero.getEdefense();
        currentGachaHero.setEdefense((int)(originalDefense * 1.5));
        
        // Villain still attacks
        int villainDamage = calculateDamage(currentGachaVillain.getErarity(), currentGachaVillain.getEattack(), 3, currentGachaVillain.getEspeed(), currentGachaHero.getEdefense(), false);
        currentGachaHero.setEhp(Math.max(0, currentGachaHero.getEhp() - villainDamage));
        log(currentGachaVillain.getEname() + " attacked and dealt " + villainDamage + " damage to " + currentGachaHero.getEname());
        log(currentGachaHero.getEname() + " HP: " + currentGachaHero.getEhp() + "/" + currentGachaHero.getOriginalHp());
        
        // Reset defense to original value
        currentGachaHero.setEdefense(originalDefense);
        
        if (currentGachaHero.getEhp() <= 0) {
            handleBattleOutcome(new boolean[]{false, true});
        } else {
            refreshBattleMenu();
        }
    }

    private static void startNewGame() {
        // Draw a new hero
        currentGachaHero = gachaHeroArray[gachaPoolHero.singleDraw()];
        resetCharacterHP(currentGachaHero);
        showNewHeroDialog(currentGachaHero);

        // Draw a new villain
        currentGachaVillain = gachaVillainArray[gachaPoolVillain.singleDraw()];
        resetCharacterHP(currentGachaVillain);
        showNewVillainDialog(currentGachaVillain);

        // Reset any other necessary game state variables

        // Switch to the battle menu
        refreshBattleMenu();
        cardLayout.show(mainPanel, "battleMenu");
    }

    private static void refreshBattleMenu() {
        SwingUtilities.invokeLater(() -> {
            JPanel newBattleMenu = createBattleMenu();
            mainPanel.remove(mainPanel.getComponent(mainPanel.getComponentCount() - 1));
            mainPanel.add(newBattleMenu, "battleMenu");
            cardLayout.show(mainPanel, "battleMenu");
        });
    }

    private static void showNewVillainDialog(GachaVillain villain) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(30, 30, 40));
        panel.setPreferredSize(new Dimension(500, 350));
        panel.setBorder(BorderFactory.createLineBorder(getStarColor(villain.getErarity()), 3));

        JLabel nameLabel = new JLabel(villain.getEname());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(nameLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        // Image
        ImageIcon originalIcon = new ImageIcon("villain-sprite.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBorder(BorderFactory.createLineBorder(getStarColor(villain.getErarity()), 2));
        contentPanel.add(imageLabel, BorderLayout.WEST);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        statsPanel.setOpaque(false);

        addStatBar(statsPanel, "HP", villain.getEhp(), villain.getOriginalHp(), villain.getErarity());
        addStatBar(statsPanel, "Attack", villain.getEattack(), 100, villain.getErarity());
        addStatBar(statsPanel, "Defense", villain.getEdefense(), 100, villain.getErarity());
        addStatBar(statsPanel, "Speed", villain.getEspeed(), 100, villain.getErarity());

        contentPanel.add(statsPanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        JLabel tierLabel = new JLabel("Tier " + villain.getErarity());
        tierLabel.setForeground(getStarColor(villain.getErarity()));
        tierLabel.setHorizontalAlignment(JLabel.CENTER);
        tierLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(tierLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(mainFrame, panel, "New Villain", JOptionPane.PLAIN_MESSAGE);
    }

    private static void showNewHeroDialog(GachaHero hero) {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(30, 30, 40));
        panel.setPreferredSize(new Dimension(500, 350));
        panel.setBorder(BorderFactory.createLineBorder(getStarColor(hero.getErarity()), 3));

        JLabel nameLabel = new JLabel(hero.getEname());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(nameLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);

        // Image
        ImageIcon originalIcon = new ImageIcon("hero-sprite.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setBorder(BorderFactory.createLineBorder(getStarColor(hero.getErarity()), 2));
        contentPanel.add(imageLabel, BorderLayout.WEST);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        statsPanel.setOpaque(false);

        addStatBar(statsPanel, "HP", hero.getEhp(), hero.getOriginalHp(), hero.getErarity());
        addStatBar(statsPanel, "Attack", hero.getEattack(), 100, hero.getErarity());
        addStatBar(statsPanel, "Defense", hero.getEdefense(), 100, hero.getErarity());
        addStatBar(statsPanel, "Speed", hero.getEspeed(), 100, hero.getErarity());
        addStatBar(statsPanel, "MP", hero.getEmp(), 100, hero.getErarity());
        addStatBar(statsPanel, "Luck", hero.getEluck(), 100, hero.getErarity());

        contentPanel.add(statsPanel, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        JLabel tierLabel = new JLabel("Tier " + hero.getErarity());
        tierLabel.setForeground(getStarColor(hero.getErarity()));
        tierLabel.setHorizontalAlignment(JLabel.CENTER);
        tierLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(tierLabel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(mainFrame, panel, "New Hero", JOptionPane.PLAIN_MESSAGE);
    }

    private static void addStatBar(JPanel panel, String statName, int value, int maxValue, int rarity) {
        JPanel statPanel = new JPanel(new BorderLayout(10, 0));
        statPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(statName + ":");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statPanel.add(nameLabel, BorderLayout.WEST);

        JProgressBar statBar = new JProgressBar(0, maxValue);
        statBar.setValue(value);
        statBar.setStringPainted(true);
        statBar.setString(value + " / " + maxValue);
        statBar.setForeground(getStarColor(rarity));
        statBar.setBackground(new Color(60, 60, 60));
        statBar.setFont(new Font("Arial", Font.BOLD, 12));
        statPanel.add(statBar, BorderLayout.CENTER);

        panel.add(statPanel);
    }

    private static void showCompactMessageDialog(String title, String message, Color backgroundColor, Color accentColor, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(backgroundColor);
        panel.setPreferredSize(new Dimension(400, 250));
        panel.setBorder(BorderFactory.createLineBorder(accentColor, 3));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(accentColor);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);

        if (imagePath != null) {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(scaledIcon);
            contentPanel.add(imageLabel, BorderLayout.WEST);
        }

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(mainFrame, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private static void showDefeatScreen() {
        showCompactMessageDialog(
            "DEFEAT",
            currentGachaHero.getEname() + " has fallen in battle.<br>The forces of evil grow stronger...",
            new Color(50, 20, 20),
            Color.RED,
            "hero-sprite.png"
        );
    }

    private static void showVictoryScreen() {
        showCompactMessageDialog(
            "VICTORY",
            "You have vanquished " + currentGachaVillain.getEname() + "!<br>But another foe approaches...",
            new Color(20, 50, 20),
            Color.GREEN,
            "villain-sprite.png"
        );
    }

    private static void showEscapeSuccessScreen() {
        showCompactMessageDialog(
            "ESCAPED",
            "Escaped successfully!<br>You live to fight another day...",
            new Color(20, 20, 50),
            Color.YELLOW,
            "hero-sprite.png"
        );
    }

    private static void showEscapeFailScreen() {
        showCompactMessageDialog(
            "ESCAPE FAILED",
            "Escape failed! Prepare to fight!<br>The villain attacks...",
            new Color(50, 30, 20),
            Color.ORANGE,
            "villain-sprite.png"
        );
    }

    private static void resetGameState() {
        currentGachaHero = null;
        currentGachaVillain = null;
        villainsDefeated = 0;
        drawChances = 1;
        runChances = 1;
        battleLog = new StringBuilder(); // Reset the battle log
        if (battleLogArea != null) {
            battleLogArea.setText("");
        }
    }

    private static void showExitGameDialog() {
        JPanel panel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 70), w, h, new Color(60, 60, 120));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(400, 200));
        panel.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3)); // Gold border

        JLabel titleLabel = new JLabel("Exit Game");
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel messageLabel = new JLabel("<html><center>Are you sure you want to exit?<br>Your progress will be lost.</center></html>");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton yesButton = createStylishButton("Yes", new Color(231, 76, 60)); // Red
        JButton noButton = createStylishButton("No", new Color(46, 204, 113)); // Green

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(mainFrame, "Exit Game", true);
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);

        yesButton.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });

        noButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private static JButton createStylishButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(120, 50));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }
}