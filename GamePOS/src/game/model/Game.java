package game.model;

public class Game {
	private String title;
	private String format; // 'N' for Nintendo, 'P' for PS4, 'X' for Xbox
	private int numberOfCopies;

	/**
	 * @param title The title of the game.
	 * @param format The format of the game ('N', 'P', 'X').
	 * @param numberOfCopies The number of copies available for rent.
	 */
	public Game(String title, String format, int numberOfCopies) {
		this.title = title;
		this.format = format;
		this.numberOfCopies = numberOfCopies;
	}

	// Getters and Setters
	public void setTitle(String title) {
		this.title = title;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public void setNumberOfCopies(int numberOfCopies) {
		this.numberOfCopies = numberOfCopies;
	}
	public String getTitle() {
		return title;
	}
	public String getFormat() {
		return format;
	}
	public int getNumberOfCopies() {
		return numberOfCopies;
	}
	/**
	 * @return True if a copy was successfully rented, false if no copies were available.
	 */
	public boolean rentGame() {
		if (numberOfCopies > 0) {
			numberOfCopies--;
			return true;  
		}else {
			return false;
		}
	}
    /**
     * @return A string representation of the game.
     */
	@Override
	public String toString() {
		return String.format("%s - Format: %s, Copies Available: %d", title, format, numberOfCopies);
	}

	public void returnGame() {
		numberOfCopies++;
		
	}
}
