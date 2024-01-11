package game.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Store {
    private static final Double PRICE_PER_NIGHT = 2.00;
	private Map<String, Game> inventory;
    private List<Transaction> transactions;

    public Store() {
        inventory = new HashMap<>();
        transactions = new ArrayList<>();
    }

    /**
     * @param transaction The transaction to be added.
     */
    public void addCompletedTransaction(Transaction transaction) {
    	transactions.add(transaction);
    }
    /**
     * @param filePath The path to the file containing inventory data.
     */
    public void returnGame(String title, String format) {
        Game game = inventory.get(title + format);
        if (game != null) {
            game.returnGame();
        }
    }

    public void loadInventoryFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    try {
                        int numberOfCopies = Integer.parseInt(parts[0].trim());
                        if (numberOfCopies < 1) {
                            throw new IllegalArgumentException("Number of copies less than 1");
                        }
                        String format = parts[1].trim();
                        if (!isValidFormat(format)) {
                            throw new IllegalArgumentException("Unknown format: " + format);
                        }
                        String title = parts[2].trim();
                        Game game = new Game(title, format, numberOfCopies);
                        inventory.put(title + format, game);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in line: " + line + "; Error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid value in line: " + line + "; Error: " + e.getMessage());
                    }
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * @return A map where the key is the game format and the value is an array of two Doubles
     *         (first element is the number of games sold, second is the total sales value).
     */
    public Map<String, Double[]> calculateDailySalesByFormat() {
        Map<String, Double[]> salesByFormat = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (isToday(transaction.getTimestamp())) {
                for (Game game : transaction.getGamesList()) {
                    String format = game.getFormat();
                    Double[] salesData = salesByFormat.getOrDefault(format, new Double[]{0.0, 0.0});

                    salesData[0]++; // Increment the number of games sold
                    salesData[1] += PRICE_PER_NIGHT; // Add to the total sales value

                    salesByFormat.put(format, salesData);
                }
            }
        }
        return salesByFormat;
    } 
    /**
     * @param format The format to be validated.
     * @return true if the format is valid, otherwise false.
     */
    private boolean isValidFormat(String format) {
        return format.equals("P") || format.equals("X") || format.equals("N");
    }
    /**
     * @param title The title of the game.
     * @param format The format of the game.
     * @return The Game object if found, otherwise null.
     */
    public Game getGame(String title, String format) {
        return inventory.get(title + format);
    }
    /**
     * @return A list of unique formats available.
     */
    public List<String> getAvailableFormats() {
        return inventory.values().stream()
                .map(Game::getFormat)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * @param format The format to filter by. If null, all games are returned.
     * @return A list of games matching the specified format.
     */
    public List<Game> getAvailableGames(String format) {
        return inventory.values().stream()
                .filter(game -> format == null || game.getFormat().equals(format))
                .filter(game -> game.getNumberOfCopies() > 0)
                .collect(Collectors.toList());
    }
    /**
     * @return A list of all transactions.
     */
    public List<Transaction> getTransactionHistory() {
        return transactions;
    }
    /**
     * @return The total sales amount for the day.
     */
    public double calculateDailyTotal() {
        return transactions.stream()
                           .filter(t -> isToday(t.getTimestamp()))
                           .mapToDouble(Transaction::getTotalPrice)
                           .sum();
    }
    /**
     * @param timestamp The timestamp to be checked.
     * @return true if the timestamp is of the current day, otherwise false.
     */
    private boolean isToday(long timestamp) {
        LocalDate transactionDate = Instant.ofEpochMilli(timestamp)
                                           .atZone(ZoneId.systemDefault())
                                           .toLocalDate();
        return transactionDate.equals(LocalDate.now());
    }

}

