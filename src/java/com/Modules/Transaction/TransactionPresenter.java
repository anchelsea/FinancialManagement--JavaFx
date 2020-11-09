package com.Modules.Transaction;

import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Helper.CurrencyHelper;
import com.Modules.Wallet.Wallet;
import com.Other.Base.PagePresenter;
import com.Modules.Category.CategoryPresenter;
import com.Modules.Wallet.WalletController;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Iterator;

public class TransactionPresenter extends PagePresenter {
    private TransactionController transactionController;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private StringProperty handledTransactionId = new SimpleStringProperty();

    private LocalDate tabDate = LocalDate.now();

    private CategoryPresenter categoryPresenter;
    private WalletController walletController;


    public void loadPresenter() throws SQLException, ClassNotFoundException {
        this.transactionController = new TransactionController();
    }

    private void getTransactionsByDate(int walletId, LocalDate date, char operator) throws SQLException {
        this.transactions.setAll(
                this.transactionController.listByMonth(walletId, date, operator)
        );
    }

    private static void _addNewTransaction(ObservableList<Transaction> transactions, Transaction newTransaction) {
        transactions.add(newTransaction);
        TransactionPresenter.reservedSortTransactions(transactions);
    }

    public static void sortTransactions(ObservableList<Transaction> transactions) {
        transactions.sort(
                Comparator.comparingInt(e -> ((Transaction) e).getTransactedAt().getDayOfMonth())
                        .thenComparingInt(e -> ((Transaction) e).getId())
        );
    }

    public static void reservedSortTransactions(ObservableList<Transaction> transactions) {
        TransactionPresenter.sortTransactions(transactions);
        FXCollections.reverse(transactions);
    }

    private void handleTransactionId() {
        this.handledTransactionId.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            int id = Integer.parseInt(newValue.substring(7));

            for (Iterator<Transaction> it = this.transactions.iterator(); it.hasNext();) {
                Transaction transaction = it.next();

                if (transaction.getId() == id) {
                    it.remove();
                    break;
                }
            }

            if (newValue.contains("UPDATE")) {
                try {
                    Transaction updatedTransaction = this.transactionController.getDetail(id);
                    LocalDate transactedAt = updatedTransaction.getTransactedAt();

                    if (updatedTransaction.getWalletId() == this.getWalletIndexId()
                            && transactedAt.getMonthValue() == this.tabDate.getMonthValue()
                            && transactedAt.getYear() == this.tabDate.getYear()) {
                        TransactionPresenter._addNewTransaction(this.transactions, updatedTransaction);
                    }
                } catch (SQLException | NotFoundException e) {
                    e.printStackTrace();
                }
            }

            this._calculateStatistic();
        });
    }

    /*========================== Draw ==========================*/
    @FXML
    private Label labelInflow, labelOutflow, labelRemainingAmount;

    @FXML
    private Button leftTimeRange, middleTimeRange, rightTimeRange, selectCategory, selectFriend;

    @FXML
    private ListView listViewTransactions;

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

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException, InterruptedException {
        super.setWallets(wallets);
        this.getTransactionsByDate(getWallet().getId(), tabDate, '=');
        this._setListViewTransactions();
        this._calculateStatistic();
    }

    private void _calculateStatistic() {
        float inflow = 0, outflow = 0;
        String moneySymbol = this.wallets.get(this.walletIndex.get()).getMoneySymbol();

        for (Transaction transaction: this.transactions) {
            if (transaction.getAmount() > 0) {
                inflow += transaction.getAmount();
            } else {
                outflow += transaction.getAmount();
            }
        }

        this.labelInflow.setText(CurrencyHelper.toMoneyString(inflow, moneySymbol));
        this.labelOutflow.setText(CurrencyHelper.toMoneyString(outflow, moneySymbol));
        this.labelRemainingAmount.setText(CurrencyHelper.toMoneyString(inflow + outflow, moneySymbol));
    }

    private void _setListViewTransactions() {
        this.handleTransactionId();

        if (this.transactions.size() == 0) {
            this.listViewTransactions.setPlaceholder(new Label("No Transaction In List"));
        }

        this.listTransactions(
                this.listViewTransactions,
                this.transactions,
                this.wallets,
                this.handledTransactionId
        );
    }

    private void listTransactions(
            ListView listView,
            ObservableList<Transaction> transactions,
            ObservableList<Wallet> wallets,
            StringProperty handledTransactionId
    ) {
        listView.setItems(transactions);
        listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    TransactionCell transactionCell = new TransactionCell(handledTransactionId);
                    transactionCell.setWallets(wallets);
                    transactionCell.setWalletIndex(walletIndex);

                    return transactionCell;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void changeTime(Event e) throws SQLException {
        Node button = (Node) e.getSource();
        int selectedTimeRange = Integer.parseInt(button.getUserData().toString());
        LocalDate currentDate = LocalDate.now();

        if (this.tabDate.getMonthValue() == currentDate.getMonthValue()
                && this.tabDate.getYear() == currentDate.getYear()
                && selectedTimeRange == 1) {
            // TODO: Set current tab text is future, hide
            this.getTransactionsByDate(this.getWallet().getId(), this.tabDate, '>');
            this.leftTimeRange.setText("THIS MONTH");
            this.middleTimeRange.setText("FUTURE");
            this.rightTimeRange.setVisible(false);
        } else {
            this.rightTimeRange.setVisible(true);
            this._setDay(selectedTimeRange, currentDate);
        }

        this._calculateStatistic();
    }

    private void _setDay(int selectedTimeRange, LocalDate currentDate) throws SQLException {
        int month = this.tabDate.getMonthValue();
        int year = this.tabDate.getYear();

        if (this.leftTimeRange.getText().equals("THIS MONTH")) {
            this.tabDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
        } else if (month == 1 && selectedTimeRange == -1) {
            this.tabDate = LocalDate.parse((year - 1) + "-12-01");
        } else if (month == 12 && selectedTimeRange == 1) {
            this.tabDate = LocalDate.parse((year + 1) + "-01-01");
        } else if (selectedTimeRange == -1) {
            month--;
            this.tabDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
        } else if (selectedTimeRange == 1) {
            month++;
            this.tabDate = LocalDate.parse(year + "-" + (month > 9 ? month : "0" + month) + "-01");
        }

        this.getTransactionsByDate(this.getWallet().getId(), this.tabDate, '=');
        month = this.tabDate.getMonthValue();
        year = this.tabDate.getYear();
        String displayedMonth = (month >= 10) ? Integer.toString(month) : "0" + month;
        int currentMonth = currentDate.getMonthValue();
        int prevMonth = (currentMonth == 1) ? 12 : currentMonth - 1;
        int prevMonth2 = (prevMonth == 1) ? 12 : prevMonth - 1;
        int currentYear = currentDate.getYear();
        int prevYear = (currentMonth == 1) ? currentYear - 1 : currentYear;
        int prevYear2 = (prevMonth == 1) ? prevYear - 1 : prevYear;

        if (year == currentYear && month == currentMonth) {
            this.leftTimeRange.setText("LAST MONTH");
            this.middleTimeRange.setText("THIS MONTH");
            this.rightTimeRange.setText("FUTURE");
        } else if (year == prevYear && month == prevMonth) {
            this.leftTimeRange.setText(this._getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText("LAST MONTH");
            this.rightTimeRange.setText("THIS MONTH");
        } else if (year == prevYear2 && month == prevMonth2) {
            this.leftTimeRange.setText(this._getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText(displayedMonth + "/" + year);
            this.rightTimeRange.setText("LAST MONTH");
        } else {
            this.leftTimeRange.setText(this._getTimeRangeText(month - 1, year));
            this.middleTimeRange.setText(displayedMonth + "/" + year);
            this.rightTimeRange.setText(this._getTimeRangeText(month + 1, year));
        }
    }

    private String _getTimeRangeText(int month, int year) {
        String text;
        if (month == 0) {
            text = "12/" + (year - 1);
        } else if (month == 13) {
            text = "01/" + (year + 1);
        } else {
            text = (month >= 10) ? Integer.toString(month) : "0" + month;
            text += "/" + year;
        }

        return text;
    }

    @FXML
    private void createTransaction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/transaction/transaction_save.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();

        if (this.categoryPresenter == null) {
            this.categoryPresenter = new CategoryPresenter(this.selectedType, this.selectedCategory, this.selectedSubCategory);
        }

        this.loadTransactionData();
        this.createScreen(parent, "Add Transaction", 500, 230,"/image/Other/transaction_icon.jpg");
    }

    private void loadTransactionData() {
        this.walletId.set(0);
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
        PagePresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.datePickerTransactedAt.setValue(LocalDate.now());
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }


    @FXML
    private void changeAmount() {
        TransactionPresenter.parseTextFieldMoney(this.textFieldTransactionAmount);
    }

    public static void parseTextFieldMoney(TextField textFieldAmount) {
        String amountText = CurrencyHelper.parseToCurrency(textFieldAmount.getText());
        int position = amountText.length();
        textFieldAmount.setText(amountText);
        textFieldAmount.positionCaret(position);
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
            transaction = this.transactionController.create(transaction);
            transactedAt = transaction.getTransactedAt();

            if (transaction.getWalletId() == this.getWalletIndexId()
                    && transactedAt.getMonthValue() == this.tabDate.getMonthValue()
                    && transactedAt.getYear() == this.tabDate.getYear()) {
                TransactionPresenter._addNewTransaction(this.transactions, transaction);
                this._calculateStatistic();
            }

            this.closeScene(event);
        } catch (SQLException | NotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        }
    }

    public static String validateData(int walletId, int categoryId, float amount, String note) {
        if (walletId == 0) {
            return "Wallet is not selected";
        }
        if (categoryId == 0) {
            return "Category is not selected";
        }
        if (amount <= 0) {
            return "Amount is not valid";
        }
        if (note.length() > 255) {
            return "Note is too long";
        }

        return null;
    }
}

