package game.controllers;

import game.model.Game;
import game.model.Store;
import game.model.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ReceiptWindowController {
	@FXML
	private TextArea receiptTextArea;
	private Transaction currentTransaction;
	private Store store;

    /**
     * @param text The receipt text to be displayed.
     */
	public void setReceiptText(String text) {
		receiptTextArea.setText(text);
	}
	
	public void initialize() {
		// Set the font of the TextArea to a monospaced font
		receiptTextArea.setFont(Font.font("monospaced", FontWeight.NORMAL, 12));
	}

	@FXML
	private void onAccept() {
		if (currentTransaction != null && store != null) {
			store.addCompletedTransaction(currentTransaction);
		}
		closeWindow();
	}
    /**
     * @param transaction The current transaction.
     */
	public void setCurrentTransaction(Transaction transaction) {
		this.currentTransaction = transaction;
		setReceiptText(currentTransaction.getReceipt());
	}

    /**
     * @param store The store instance.
     */
	public void setStore(Store store) {
		this.store = store;
	}

	@FXML
	private void onCancel() {
	    if (currentTransaction != null) {
	        for (Game game : currentTransaction.getGamesList()) {
	            store.returnGame(game.getTitle(), game.getFormat());
	        }
	    }
	    closeWindow();
	}

	private void closeWindow() {
		((Stage) receiptTextArea.getScene().getWindow()).close();
	}
}
