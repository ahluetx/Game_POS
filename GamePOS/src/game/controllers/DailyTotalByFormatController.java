package game.controllers;

import java.text.DecimalFormat;
import java.util.Map;

import game.model.Store;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DailyTotalByFormatController {
	private Store gameStore;
	@FXML
	private TextArea dailyTotalByFormatTextArea;

    /**
     * @param store The store instance containing game and transaction data.
     */
	public void setStore(Store store) {
		this.gameStore = store;
	}
	public void initialize() {
		// Set the font of the TextArea to a monospaced font
		dailyTotalByFormatTextArea.setFont(Font.font("monospaced", FontWeight.NORMAL, 12));
	}
    /**
     * @param text The formatted text to display in the TextArea.
     */
	public void setText(String text) {
		dailyTotalByFormatTextArea.setText(text);
	}
    /**
     * @return A string representing the formatted breakdown of sales.
     */
	public String calculateFormatBreakdown() {
		if (gameStore == null) {
			return "Store data not available.";
		}
		StringBuilder formatBreakdown = new StringBuilder();
		DecimalFormat priceFormat = new DecimalFormat("$0.00");
		Map<String, Double[]> salesByFormat = gameStore.calculateDailySalesByFormat();
		formatBreakdown.append(String.format("%8s %20s %20s\n", "Format", "Games Sold", "Total Sales"));
		formatBreakdown.append("----------------------------------------------------------\n");
		for (Map.Entry<String, Double[]> entry : salesByFormat.entrySet()) {
			String format = entry.getKey();
			Double[] salesData = entry.getValue();
			formatBreakdown.append(String.format("%6s %18d %20s\n", 
					format, 
					salesData[0].intValue(), 
					priceFormat.format(salesData[1])));
		}
		return formatBreakdown.toString();
	}
}
