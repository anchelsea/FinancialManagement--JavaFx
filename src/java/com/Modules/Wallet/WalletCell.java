package com.Modules.Wallet;

import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Helper.CurrencyHelper;
import com.Modules.User.User;
import com.Infrastructure.Interface.DialogInterface;
import com.Modules.Currency.CurrencyPresenter;
import com.Modules.Transaction.TransactionPresenter;
import com.Modules.User.UserPresenter;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.SQLException;

public class WalletCell extends ListCell<Wallet> implements DialogInterface {
    private StringProperty handledWalletId;

    private CurrencyPresenter currencyPresenter;

    private com.Modules.Wallet.WalletController walletController; // Get Detail when update

    private UserPresenter userPresenter;

    private Wallet wallet;

    public WalletCell(StringProperty handledWalletId) throws IOException {
        this.handledWalletId = handledWalletId;
        this._loadCell();
    }

    private void _loadCell() throws IOException {
        FXMLLoader walletCellLoader = new FXMLLoader(getClass().getResource("/fxml/wallet/wallet_cell.fxml"));
        walletCellLoader.setController(this);
        this.walletCell = walletCellLoader.load();
    }

    public void setWallet(Wallet wallet) throws SQLException, ClassNotFoundException {
        this. walletController= new WalletController();
        this.userPresenter= new UserPresenter();
        this.wallet = wallet;
    }

    /*========================== Draw ==========================*/
    private HBox walletCell;

    @FXML
    private Label labelWalletName;

    @FXML
    private Label labelWalletAmount;

    @FXML
    private TextField textFieldTransactionName;

    @FXML
    private TextField textFieldWalletAmount;

    @FXML
    private Button selectCurrency;

    private IntegerProperty selectedCurrencyId = new SimpleIntegerProperty(0);

    protected void updateItem(Wallet item, boolean empty) {
        super.updateItem(item, empty);
        this.wallet = item;

        if (empty) {
            setGraphic(null);
            return;
        }

        float amount = item.getAmount();

        if (amount < 0) {
            this.labelWalletAmount.getStyleClass().add("danger-color");
        } else {
            this.labelWalletAmount.getStyleClass().add("success-color");
        }

        this.labelWalletName.setText(item.getName());
        this.labelWalletAmount.setText(CurrencyHelper.toMoneyString(amount, item.getMoneySymbol()));
        setGraphic(this.walletCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addEditPopup((Node) e.getSource());
    }

    @FXML
    private void listCurrencies() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/currency/choose_currency.fxml")
        );
        Parent parent = fxmlLoader.load();
        CurrencyPresenter currencyPresenter = fxmlLoader.getController();
        currencyPresenter.setSelectedCurrencyId(this.selectedCurrencyId);

        this.createScreen(parent, "Choose Currency", 400, 300,"/image/Other/currency_icon1.png");
    }

    @FXML
    private void edit() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/wallet/wallet_save.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.currencyPresenter = new CurrencyPresenter(this.selectedCurrencyId);
        this.loadWalletData();
        this.textFieldWalletAmount.setDisable(true);
        this.selectCurrency.setDisable(true);
        this.createScreen(parent, "Edit Wallet", 500, 115,"/image/Other/currency_icon1.png");
    }

    private void loadWalletData() throws SQLException, ClassNotFoundException {
        this.selectedCurrencyId.set(0);
        this.currencyPresenter.setSelectedCurrencyId(this.selectedCurrencyId);
        this.currencyPresenter.handleSelectedCurrencyId(this.selectCurrency);
        this.selectedCurrencyId.set(this.wallet.getCurrencyId());
        this.textFieldTransactionName.setText(this.wallet.getName());
        this.textFieldWalletAmount.setText(CurrencyHelper.toMoney(this.wallet.getAmount()));
    }

    @FXML
    private void changeAmount() {
        TransactionPresenter.parseTextFieldMoney(this.textFieldWalletAmount);
    }

    @FXML
    private void saveWallet(Event event) throws SQLException, ClassNotFoundException, NotFoundException {

        User user = UserPresenter.getUser();
        int userId = user.getId();
        String name = this.textFieldTransactionName.getText().trim();
        String amountText = this.textFieldWalletAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.replaceAll("[^\\d.]", ""));
        int currencyId = this.selectedCurrencyId.get();
        String validation = WalletPresenter.validateWallet(name, currencyId, amount);

        if (validation != null) {
            this.showErrorDialog(validation);
            return;
        }

        try {
            (new WalletController()).update(new Wallet(userId, name,currencyId, amount), this.wallet.getId());
            this.handledWalletId.set(null);
            this.handledWalletId.set("UPDATE-" + this.wallet.getId());
            this.closeScene(event);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    @FXML
    private void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();

        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.wallet.getId();
                (new WalletController()).delete(id);
                this.handledWalletId.set("DELETE-" + id);
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }

    @FXML
    public void closeScene(Event e) {
        DialogInterface.closeScene(e);
    }
}
