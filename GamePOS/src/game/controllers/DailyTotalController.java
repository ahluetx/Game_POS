package game.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DailyTotalController {
	@FXML
	private TextArea dailyTotalTextArea;

	private MainController mainController;

    /**
     * @param controller The MainController instance.
     */
	public void setMainController(MainController controller) {
		this.mainController = controller;
	}


	public void initialize() {
		// Set the font of the TextArea to a monospaced font
		dailyTotalTextArea.setFont(Font.font("monospaced", FontWeight.NORMAL, 12));
	}

    /**
     * @param totalReceipt The string containing formatted daily total receipt information.
     */
	public void setDailyTotal(String totalReceipt) {
		dailyTotalTextArea.setText(totalReceipt);
	}

	@FXML
	private void onShowFormatBreakdown() {
		if (mainController != null) {
			mainController.showFormatBreakdown();
		}
		else {
		}
	}

    /**
     * @param text The text to be displayed in the TextArea.
     */
	public void setText(String text) {
		dailyTotalTextArea.setText(text);

	}



}
