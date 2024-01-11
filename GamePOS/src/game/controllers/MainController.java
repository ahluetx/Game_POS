/**
 * The MainController class handles the main user interface of the game rental system.
 * It manages user interactions with the application's main window, including game selection,
 * adding games to a cart, checking out, and viewing daily totals.
 */
package game.controllers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import game.model.Game;
import game.model.Store;
import game.model.Transaction;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainController {
	static Store gameStore;

	@FXML private ComboBox<String> formatComboBox;
	@FXML private TableView<Game> gamesTable;
	@FXML private TableColumn<Game, String> titleColumn;
	@FXML private TableColumn<Game, String> formatColumn;
	@FXML private TableColumn<Game, Integer> copiesColumn;
	@FXML private TextField searchField;
	@FXML private ListView<String> cartListView;
	private Transaction currentTransaction = new Transaction();
	/**
	 * @param formatLetter The format letter (e.g., 'P', 'X', 'N').
	 * @return The full name of the format.
	 */
	private String formatFullName(String formatLetter) {
		switch (formatLetter) {
		case "P":
			return "PlayStation";
		case "X":
			return "X-Box";
		case "N":
			return "Nintendo";
		default:
			return formatLetter; 
		}
	}
	/**
	 * @param formatFullName The full name of the format.
	 * @return The corresponding format letter.
	 */
	private String getFormatLetter(String formatFullName) {
		switch (formatFullName) {
		case "PlayStation":
			return "P";
		case "X-Box":
			return "X";
		case "Nintendo":
			return "N";
		default:
			return ""; // Fallback for all formats
		}
	}
	/**
	 * @param timestamp The timestamp to check.
	 * @return True if the timestamp is from today, otherwise false.
	 */
	private boolean isToday(long timestamp) {
		LocalDate transactionDate = Instant.ofEpochMilli(timestamp)
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		return transactionDate.equals(LocalDate.now());
	}
	public void initialize() {
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		formatColumn.setCellValueFactory(new PropertyValueFactory<>("format"));
		formatColumn.setCellFactory(column -> new TableCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(formatFullName(item));
				}
			}
		});
		copiesColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfCopies"));
		formatComboBox.setItems(FXCollections.observableArrayList("", "PlayStation", "X-Box", "Nintendo"));
		formatComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
			loadGamesBasedOnFormat(getFormatLetter(newVal));
		});
		if (gameStore != null) {
			List<String> formats = gameStore.getAvailableFormats();
			formatComboBox.setItems(FXCollections.observableArrayList(formats));
			loadGames();
		}
	}
	private void loadGames() {
		List<Game> sortedGames = gameStore.getAvailableGames(null)
				.stream()
				.sorted(Comparator.comparing(Game::getTitle))
				.collect(Collectors.toList());
		gamesTable.setItems(FXCollections.observableArrayList(sortedGames));
	}
	/**
	 * @param store The store instance to set.
	 */
	public void setStore(Store store) {
		MainController.gameStore = store;
		if (MainController.gameStore != null) {
			loadGames();
		}
	}
	public void showFormatBreakdown() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/DailyTotalByFormat.fxml"));
			Parent root = loader.load();

			DailyTotalByFormatController controller = loader.getController();
			controller.setStore(gameStore); 
			controller.setText(controller.calculateFormatBreakdown());

			Stage stage = new Stage();
			stage.setTitle("Format Breakdown");
			stage.setScene(new Scene(root));

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void onSearch() {
		String searchText = searchField.getText().toLowerCase();
		String selectedFormat = formatComboBox.getValue();

		List<Game> filteredGames = gameStore.getAvailableGames(null).stream()
				.filter(game -> (selectedFormat == null || selectedFormat.isEmpty() || game.getFormat().equalsIgnoreCase(selectedFormat)))
				.filter(game -> game.getTitle().toLowerCase().contains(searchText))
				.sorted(Comparator.comparing(Game::getTitle))
				.collect(Collectors.toList());

		gamesTable.setItems(FXCollections.observableArrayList(filteredGames));
	}
	@FXML
	private void onAddToCart() {
		Game selectedGame = gamesTable.getSelectionModel().getSelectedItem();
		if (selectedGame == null) {
			return;
		}
		if (!selectedGame.rentGame()) {
			showAlert("Game is sold out", "This game is sold out");
			return; // Early exit if the game cannot be rented
		}
		currentTransaction.addGame(selectedGame);
		gamesTable.refresh();
	}
	/**
	 * @param title The title of the alert.
	 * @param content The content of the alert.
	 */
	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
	@FXML
	private void onCheckout() {
		if (currentTransaction.getGamesList().isEmpty()) {
			showAlert("Empty Cart", "Your cart is empty. Please add at least one game to proceed.");
			return; 
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReceiptWindow.fxml"));
			Parent root = loader.load();

			ReceiptWindowController receiptController = loader.getController();
			receiptController.setStore(gameStore);
			receiptController.setCurrentTransaction(currentTransaction);

			Stage stage = new Stage();
			stage.setTitle("Receipt");
			stage.setScene(new Scene(root));
			stage.showAndWait(); 

			currentTransaction = new Transaction();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	private void onShowDailyTotals() {
		StringBuilder dailyReceipt = new StringBuilder();
		DecimalFormat priceFormat = new DecimalFormat("$0.00");
		int totalGamesSold = 0;
		double totalMoneyMade = 0;

		for (Transaction transaction : gameStore.getTransactionHistory()) {
			if (isToday(transaction.getTimestamp())) {
				dailyReceipt.append(transaction.getReceipt()).append("\n");
				totalGamesSold += transaction.getGamesList().size();
				totalMoneyMade += transaction.getTotalPrice();
			}
		}
		dailyReceipt.append("\n----------------------------------------------------------\n");
		dailyReceipt.append(String.format("Games Sold: %d\n", totalGamesSold));
		dailyReceipt.append(String.format("Total Sales: %s\n", priceFormat.format(totalMoneyMade)));
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/DailyTotal.fxml"));
			Parent root = loader.load();

			DailyTotalController controller = loader.getController();
			controller.setDailyTotal(dailyReceipt.toString());
			controller.setMainController(this);

			Stage stage = new Stage();
			stage.setTitle("Daily Total");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param format The format to filter games by.
	 */
	private void loadGamesBasedOnFormat(String format) {
		List<Game> filteredGames;
		if (format == null || format.isEmpty()) {
			filteredGames = gameStore.getAvailableGames(null);
		} else {
			filteredGames = gameStore.getAvailableGames(format);
		}
		filteredGames.sort(Comparator.comparing(Game::getTitle));
		gamesTable.setItems(FXCollections.observableArrayList(filteredGames));
	}
}
