package com.Modules.Transaction;


import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Helper.CurrencyHelper;
import com.Infrastructure.Interface.DialogInterface;
import com.Modules.Wallet.Wallet;
import com.Modules.Category.CategoryPresenter;
import com.Other.Base.PagePresenter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionCell extends ListCell<Transaction> implements DialogInterface {
    private HBox transactionCell;

    private Transaction transaction;

    private Wallet wallet;

    private ObservableList<Wallet> wallets;

    private CategoryPresenter categoryPresenter;


    private StringProperty handledTransactionId;

    private IntegerProperty walletIndex;

    public TransactionCell() throws IOException {
        this._loadCell();
    }

    public TransactionCell(StringProperty handledTransactionId) throws IOException {
        this.handledTransactionId = handledTransactionId;
        this._loadCell();
    }

    public void setWalletIndex(IntegerProperty walletIndex) {
        this.walletIndex = walletIndex;
    }

    private void _loadCell() throws IOException {
        FXMLLoader transactionCellLoader = new FXMLLoader(
                getClass().getResource("/fxml/transaction/transaction_cell.fxml")
        );
        transactionCellLoader.setController(this);
        transactionCell = transactionCellLoader.load();
    }

    public void setWallets(ObservableList<Wallet> wallets) {
        this.wallets = wallets;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /*========================== Draw ==========================*/
    @FXML
    private ImageView imageTransactionCategory;

    @FXML
    private Label labelTransactionCategoryName, labelTransactionTime, labelTransactionNote, labelAmount;

    @FXML
    private Button buttonOptions, selectCategory, selectFriend;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldTransactionAmount, textFieldNote;

    @FXML
    private DatePicker datePickerTransactedAt;

    @FXML
    private CheckBox checkBoxIsNotReported;

    @FXML
    private VBox vBoxSelectFriend;

    private IntegerProperty
            walletId = new SimpleIntegerProperty(0),
            selectedType = new SimpleIntegerProperty(0),
            selectedCategory = new SimpleIntegerProperty(0),
            selectedSubCategory = new SimpleIntegerProperty(0),
            selectedFriend = new SimpleIntegerProperty(0);

    public void setDisableOptions(boolean disableOptions) {
        if (disableOptions) {
            this.buttonOptions.setDisable(true);
        }
    }

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);
        this.transaction = item;

        if (empty) {
            setGraphic(null);
            return;
        }

        String moneySymbol,
                text = item.getSubCategoryName(),
                imageUrl = "/image/category/" + item.getSubCategoryIcon() + ".png";

        if (this.wallet != null) {
            moneySymbol = this.wallet.getMoneySymbol();
        } else {
            moneySymbol = this.wallets.get(this.walletIndex.get()).getMoneySymbol();
        }

        if (text == null || text.equals("")) {
            text = item.getCategoryName();
            imageUrl = "/image/category/" + item.getCategoryIcon() + ".png";
        }

        this.labelTransactionTime.setText(
                item.getTransactedAt().format(DateTimeFormatter.ofPattern("MM/dd/YYYY"))
        );
        this.imageTransactionCategory.setImage(new Image(imageUrl));
        this.labelTransactionCategoryName.setText(text);

        if (item.getNote().length() > 50) {
            this.labelTransactionNote.setText(item.getNote().substring(0, 30) + "...");
            this.labelTransactionNote.setTooltip(new Tooltip(item.getNote()));
        } else {
            this.labelTransactionNote.setText(item.getNote());
        }

        this.labelAmount.setText(CurrencyHelper.toMoneyString(item.getAmount(), moneySymbol));
        this.labelAmount.getStyleClass().removeAll("danger-color", "success-color");

        if (item.getAmount() < 0) {
            this.labelAmount.getStyleClass().add("danger-color");
        } else {
            this.labelAmount.getStyleClass().add("success-color");
        }

        setGraphic(this.transactionCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addEditPopup((Node) e.getSource());
    }

    @FXML
    private void edit() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/transaction/transaction_save.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
        this.loadTransactionData();
        this.createScreen(parent, "Edit Transaction", 500, 230,"/image/Other/transaction_icon.jpg");
    }

    private void loadTransactionData() {
        this.walletId.set(this.transaction.getWalletId());
        this.textFieldNote.setText(this.transaction.getNote());
        this.textFieldTransactionAmount.setText(CurrencyHelper.toMoney(Math.abs(this.transaction.getAmount())));
        /* TODO: reload update after transaction cell is edit again */
        this.selectedFriend.set(0);
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
        PagePresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.selectedType.set(this.transaction.getTypeId());
        this.selectedCategory.set(this.transaction.getCategoryId());
        this.selectedSubCategory.set(this.transaction.getSubCategoryId());
        this.datePickerTransactedAt.setValue(this.transaction.getTransactedAt());
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void changeAmount() {
        TransactionPresenter.parseTextFieldMoney(this.textFieldTransactionAmount);
    }

    @FXML
    private void saveTransaction(Event event) {
        String amountText = this.textFieldTransactionAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.replaceAll("[^\\d.]", ""));
        LocalDate transactedAt = this.datePickerTransactedAt.getValue();
        int walletId = this.walletId.get();
        int categoryId = this.selectedCategory.get();
        int subCategoryId = this.selectedSubCategory.get();
        String note = this.textFieldNote.getText();
        String validation = TransactionPresenter.validateData(walletId, categoryId, amount, note);

        if (validation != null) {
            this.showErrorDialog(validation);
            return;
        }

        Transaction transaction = new Transaction();
        transaction.setWalletId(walletId);
        transaction.setTypeId(this.selectedType.get());
        transaction.setCategoryId(categoryId);
        transaction.setSubCategoryId(subCategoryId);
        transaction.setAmount(amount);
        transaction.setNote(note);
        transaction.setTransactedAt(transactedAt);

        try {
            int id = this.transaction.getId();
            (new TransactionController()).update(transaction, id);
            this.handledTransactionId.set(null);
            this.handledTransactionId.set("UPDATE-" + id);
            this._clearPresenter();
            this.closeScene(event);
        } catch (SQLException | NotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @FXML
    private void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.transaction.getId();
                (new TransactionController()).delete(id);
                this.handledTransactionId.set("DELETE-" + id);
            } catch (SQLException | NotFoundException | ClassNotFoundException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }

    private void _clearPresenter() {
        this.categoryPresenter = null;
    }

    @FXML
    public void closeScene(Event e) {
        DialogInterface.closeScene(e);
    }
}
