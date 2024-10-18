import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author : CS2 TA
 * Note from Author: 
 * Changing anything in this class is not requiered to complete the lab
 * It is encouraged to understand what is going on in the code but it is not requiered
 * Use this as an example on how to establish a class and 
 * how objects such as gachaItems call methods
 */
public class GachaGame {

    static int attemptCounter = 0;  // Track the number of attempts
    static List<GachaItem> gachaItems = List.of(
        new GachaItem(1, 50.0),
        new GachaItem(2, 30.0),
        new GachaItem(3, 15.0),
        new GachaItem(4, 4.4),
        new GachaItem(5, 0.6)
    );

    // Define a class to store the star level and its probability
    // This is a great example of class.
    static class GachaItem {
        int starLevel;
        double probability;

        GachaItem(int starLevel, double probability) {
            this.starLevel = starLevel;
            this.probability = probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }

        public void setStarLevel(int starLevel) {
            this.starLevel = starLevel;
        }
    }

    public static List<GachaItem> setProbabilityUpItems(List<GachaItem> gachaItems) {
        attemptCounter++;

        // If it's the 20th attempt, set the 5-star probability to 100%
        if (attemptCounter == 20) {
            for (GachaItem item : gachaItems) {
                if (item.starLevel == 5) {
                    item.probability = 100.0;
                } else {
                    item.probability = 0.0;
                }
            }
        }
        else if(attemptCounter > 20){
            attemptCounter = 0;
            gachaItems = List.of(
                new GachaItem(1, 50.0),
                new GachaItem(2, 30.0),
                new GachaItem(3, 15.0),
                new GachaItem(4, 4.4),
                new GachaItem(5, 0.6)
            );
        } else {
            // Increase the probability of a 5-star slightly, reduce others
            for (GachaItem item : gachaItems) {
                if (item.starLevel == 5) {
                    item.probability += 0.1;  // Increase 5-star probability
                } else {
                    // Decrease probability of lower star levels proportionally
                    item.probability -= (item.probability * 1.0) / (100 - item.probability);
                }
            }

            // Normalize probabilities to sum to 100%
            normalizeProbabilities(gachaItems);
        }

        return gachaItems;
    }

    private static void normalizeProbabilities(List<GachaItem> gachaItems) {
        double totalProbability = gachaItems.stream().mapToDouble(item -> item.probability).sum();

        for (GachaItem item : gachaItems) {
            item.probability = (item.probability / totalProbability) * 100;
        }
    }

    public static int gachaDraw(List<GachaItem> gachaItems) {
        Random random = new Random();
        double randomValue = random.nextDouble() * 100;  // Generate a random number between 0 and 100

        double cumulativeProbability = 0.0;
        for (GachaItem item : gachaItems) {
            cumulativeProbability += item.probability;
            if (randomValue <= cumulativeProbability) {
                return item.starLevel;
            }
        }
        return -1;  // Fallback, should never reach here if probabilities sum to 100%
    }

    // This should return the index depending on the raraty
    public static int singleDraw(){
        gachaItems = setProbabilityUpItems(gachaItems);
        //Testing putposes
        //gachaItems.forEach(item -> System.out.println(item.starLevel + "-Star: " + item.probability + "%"));
        int raraty = gachaDraw(gachaItems);
        
        if(raraty == 1){
            return ThreadLocalRandom.current().nextInt(0, 8);
        } else if(raraty == 2){
            return ThreadLocalRandom.current().nextInt(8, 15);
        } else if(raraty == 3){
            return ThreadLocalRandom.current().nextInt(15, 19);
        } else if(raraty == 4){
            return ThreadLocalRandom.current().nextInt(19, 21);
        } else if(raraty == 5){
            return ThreadLocalRandom.current().nextInt(21, 24);
        } else{
            return -1;
        }
    }

    public static void main(String[] args) {
        // This is the entry point of the application
        System.out.println("Welcome to the Gacha Game!");
        
        // You can add more code here to start your game
        // For example:
        int draw = singleDraw();
        System.out.println("You drew a character with index: " + draw);
    }
}
