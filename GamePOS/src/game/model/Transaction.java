package game.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaction {

	private List<Game> games;
	private final long timestamp; 
	private final double PRICE_PER_NIGHT = 2.00;
	private static int transactionCount = 0; 
	private int transactionNumber = 0000;

	public Transaction() {
		this.games = new ArrayList<>();
		this.timestamp = System.currentTimeMillis(); // Capture the current time
		this.transactionNumber = ++transactionCount;
	}

	/**
	 * @param game The game to add to the transaction.
	 */
	public void addGame(Game game) {
		games.add(game);
	}
	/**
	 * @return A list of games.
	 */
	public List<Game> getGamesList() {
		return games;
	}
	/**
	 * @return A string representing the formatted receipt.
	 */
	public String getReceipt() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat priceFormat = new DecimalFormat("$0.00");
		StringBuilder receipt = new StringBuilder();

		receipt.append("Transaction Date: ").append(dateFormat.format(new Date(timestamp))).append("\n");
		receipt.append("Transaction Number: ").append(String.format("%04d", transactionNumber)).append("\n");
		receipt.append(String.format("%10s %34s %11s\n", "Title", "Format", "Price"));
		receipt.append(String.format("%15s\n", "----------------------------------------------------------"));
		for (Game game : games) {
			receipt.append(String.format("%-40s %-10s %s\n", 
					game.getTitle(), 
					game.getFormat(), 
					priceFormat.format(PRICE_PER_NIGHT))); 
		}
		// Total Price
		receipt.append("\n").append(String.format("%1s %51s", 
				"Total", 
				priceFormat.format(calculateTotalPrice())));

		return receipt.toString();
	}
    /**
     * @return The total price for the transaction.
     */
	public double calculateTotalPrice() {
		return games.size()*PRICE_PER_NIGHT;
	}
    /**
     * @return The timestamp as a long value.
     */
	public long getTimestamp() {
		return timestamp;
	}
    /**
     * @return The total price as calculated by calculateTotalPrice().
     */
	public double getTotalPrice() {
		return calculateTotalPrice();
	}

}


