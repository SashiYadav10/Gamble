import java.io.*;
import java.util.*;

public class GamblingGame {


    private static final int total_no_of_draws = 1000;  // 1000 draws as per the task

    private static final int total_numbers = 10; //no. of  different numbers to be chosen

    public static void main(String[] args) {
        // List of test case files to be read
        String[] testFiles = {"t1.txt", "t2.txt", "t3.txt", "t4.txt", "t5.txt"};

        // Iterate over each test case file
        for (String testFile : testFiles) {
            System.out.println("Running simulation for file: " + testFile);
            String file_path = "src/testcases_isys1/" + testFile;
            double[] cumulative_probability = reading_probabilities(file_path);

            // Simulate multiple rounds and reset for each round
            double gesamtGewinn = 0.0;
            int numberOfRounds = 1000;  // For example, 100 rounds of 1000 draws each
            double[] round_Gewinn = new double[numberOfRounds];  // Array to store gains for each round

            for (int round = 0; round < numberOfRounds; round++) {
                // just to see progress
                // System.out.println("Starting Round " + (round + 1));
                double gewinn = spieleRunde(cumulative_probability);  // Play one independent round
                gesamtGewinn += gewinn;
                round_Gewinn[round] = gewinn;  // Store the gain of each round
            }

            // Calculate the mean
            double mittelwert = gesamtGewinn / numberOfRounds;

            // Calculate the standard deviation
            double sumSquaredDifferences = 0.0;
            for (int round = 0; round < numberOfRounds; round++) {
                double difference = round_Gewinn[round] - mittelwert;
                sumSquaredDifferences += difference * difference;
            }
            double standard_deviation = Math.sqrt(sumSquaredDifferences / numberOfRounds);

            // Output the results
            System.out.println("Mittlerer Gewinn über " + numberOfRounds + " Runden: " + mittelwert + "€");
            System.out.println("Standardabweichung des Gewinns über " + numberOfRounds + " Runden: " + standard_deviation + "€");
            System.out.println("-------------------------");
        }
    }

    /**
     * Simulation with given probabilities
     * Each round is independent and does not carry over data from previous rounds.
     *
     */

    /**
     *
     * @param cumulative_probability
     * @return
     */
    private static double spieleRunde(double[] cumulative_probability) {
        Random random = new Random();
        double gewinn = 0;

        // Initialize counts and probabilities
        int[] counts = new int[total_numbers];  // Counts for each number
        double[] estimated_probability = new double[total_numbers];
        int total_draws_in_round = 0;

        int set_number = -1;  // Initialize with an invalid number to indicate no number is chosen yet

        for (int draw = 0; draw < total_no_of_draws; draw++) {  // Play 1000 draws

            // Ziehe eine Zahl basierend auf den kumulativen Wahrscheinlichkeiten
            int chosen_number = withdraw_number(cumulative_probability, random);


            total_draws_in_round++;


            for (int i = 0; i < total_numbers; i++) {
                estimated_probability[i] = (counts[i] + 1.0) / (total_draws_in_round + total_numbers);
            }

            // Calculate expected gain for each number
            double maxErwarteterGewinn = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < total_numbers; i++) {
                double erwarteterGewinn = estimated_probability[i] * (10 * (i + 1)) + (1- estimated_probability[i]) * (-10);
                if (erwarteterGewinn > maxErwarteterGewinn) {
                    maxErwarteterGewinn = erwarteterGewinn;
                    set_number = i;
                }
            }

            // Calculate gain or loss
            if (set_number == chosen_number) {
                gewinn += 10 * (set_number + 1);  // Correct mapping to game number
            } else {
                gewinn -= 10;
            }
            // Update counts
            counts[chosen_number]++;
        }

        return gewinn;
    }

    /**
     * read the probabilities from the given file and calculates the cumulative probability for binary search.
     */
    private static double[] reading_probabilities(String filePath) {
        double[] probabilities = new double[total_numbers];
        double[] cumulative_probabilities = new double[total_numbers];
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String lines = br.readLine();
            String[] value = lines.trim().split("\\s+");
            double sum = 0.0;
            for (int i = 0; i < total_numbers; i++) {
                double prob = Double.parseDouble(value[i]);
                probabilities[i] = prob;
                sum += prob;
                cumulative_probabilities[i] = sum;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        return cumulative_probabilities;
    }

    /**
     * draws a random number based on the cumulative probability using binary search.
     */
    private static int withdraw_number(double[] cumulative_probabilities, Random random) {
        double r = random.nextDouble(); //random number between 0-1

        int left = 0;
        int right = cumulative_probabilities.length - 1;

        while (left < right) {
            int mid = (left + right) / 2;
            if (r < cumulative_probabilities[mid]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }
}
