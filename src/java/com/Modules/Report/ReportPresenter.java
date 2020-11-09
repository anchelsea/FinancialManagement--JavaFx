package com.Modules.Report;


import com.Infrastructure.Constant.CommonConstant;
import com.Infrastructure.Helper.DateHelper;
import com.Modules.Category.Category;
import com.Modules.Time.CustomDate;
import com.Modules.Transaction.TransactionController;
import com.Modules.Transaction.Transaction;
import com.Modules.Wallet.Wallet;
import com.Other.Base.PagePresenter;
import com.Modules.Transaction.TransactionCell;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Pair;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;

public class ReportPresenter extends PagePresenter {
    private TransactionController transactionController;

    private LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()),
            endDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

    private ObservableList<Transaction> transactions;

    private ObservableList<Pair<CustomDate, ObservableList<Transaction>>> monthTransactions = FXCollections.observableArrayList();

    private ObservableList<Pair<Category, ObservableList<Transaction>>>
            inflowTransactions = FXCollections.observableArrayList(),
            outflowTransactions = FXCollections.observableArrayList();

    public ReportPresenter() throws SQLException, ClassNotFoundException {
        this.transactionController = new TransactionController();
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException, InterruptedException {
        super.setWallets(wallets);
        Wallet wallet = this.getWallet();
        this.transactions = FXCollections.observableArrayList(
                this.transactionController.listNotReportedByDateRange(wallet.getId(), startDate, endDate)
        );
        this.datePickerStartDate.setValue(this.startDate);
        this.datePickerEndDate.setValue(this.endDate);
        this.sortData(wallet.getMoneySymbol(), this.startDate, this.endDate);
        this.loadBarChart();
        this._loadPieCharts();
    }

    private void sortData(
            String moneySymbol,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (DateHelper.isSameMonth(startDate, endDate)) {
            ReportPresenter.sortTransactionsByDate(this.monthTransactions, transactions, moneySymbol);
        } else {
            ReportPresenter._sortTransactionsByMonth(this.monthTransactions, transactions, moneySymbol);
        }
    }

    private void loadBarChart() {
        this._loadBarChartData(this.monthTransactions, startDate, endDate);
    }

    private void _loadPieCharts() {
        ReportPresenter._sortTransactionByCategories(this.inflowTransactions, this.outflowTransactions, this.transactions);
        this._loadPieChartData(this.incomeChart, this.inflowTransactions);
        this._loadPieChartData(this.expenseChart, this.outflowTransactions);

    }

    private static void _sortTransactionByCategories(
            ObservableList<Pair<Category, ObservableList<Transaction>>> inflowTransactions,
            ObservableList<Pair<Category, ObservableList<Transaction>>> outflowTransactions,
            ObservableList<Transaction> transactions
    ) {
        for (Iterator<Transaction> it = transactions.iterator(); it.hasNext();) {
            Transaction transaction = it.next();
            String moneyType = transaction.getCategoryMoneyType();
            boolean hasCategory = false;

            if (moneyType.equals(CommonConstant.INCOME) || moneyType.equals(CommonConstant.EXPENSE)) {
                boolean isEqualIncome = moneyType.equals(CommonConstant.INCOME);

                for (Pair<Category, ObservableList<Transaction>> flowTransaction: (isEqualIncome ? inflowTransactions : outflowTransactions)) {
                    if (transaction.getCategoryId() == flowTransaction.getKey().getId()) {
                        flowTransaction.getValue().add(transaction);
                        hasCategory = true;
                        it.remove();
                        break;
                    }
                }

                if (!hasCategory) {
                    ReportPresenter.addNewCategory((isEqualIncome ? inflowTransactions : outflowTransactions), transaction);
                }
            }
        }
    }

    public static void addNewCategory(
            ObservableList<Pair<Category, ObservableList<Transaction>>> transactions,
            Transaction newTransaction
    ) {
        Category newCategory = new Category();
        newCategory.setId(newTransaction.getCategoryId());
        newCategory.setName(newTransaction.getCategoryName());
        newCategory.setIcon(newTransaction.getCategoryIcon());
        newCategory.setMoneyType(newTransaction.getCategoryMoneyType());
        transactions.add(new Pair<>(newCategory, FXCollections.observableArrayList(newTransaction)));
    }

    /*========================== Draw ==========================*/
    @FXML
    private BarChart dateRangeChart;

    @FXML
    private PieChart expenseChart, incomeChart;

    @FXML
    private ListView listViewMonthTransactions, listViewTransactions;

    @FXML
    private DatePicker datePickerStartDate, datePickerEndDate;

    @FXML
    private Text title, textDialogTitle;

    public void loadPresenter() {
        // TODO: Calculate data
    }

    private void _loadBarChartData(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.dateRangeChart.getData().clear();
        ObservableList<String> titles = FXCollections.observableArrayList();
        CategoryAxis categoryAxis = (CategoryAxis) this.dateRangeChart.getXAxis();
        String dateRangeType = DateHelper.getDateRange(startDate, endDate);
        XYChart.Series inflowSeries = new XYChart.Series<>();
        XYChart.Series outflowSeries = new XYChart.Series();
        inflowSeries.setName("Inflow");
        outflowSeries.setName("Outflow");

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Pair<CustomDate, ObservableList<Transaction>> transaction = transactions.get(i);
            CustomDate customDate = transaction.getKey();
            String title;
            float inflow = 0;
            float outflow = 0;

            switch (dateRangeType) {
                case CommonConstant.DAY_RANGE:
                    title = Integer.toString(customDate.getDayOfMonth());
                    break;
                case (CommonConstant.MONTH_RANGE):
                    title = Integer.toString(customDate.getMonthNumber());
                    break;
                default:
                    title = customDate.getMonthNumber() + "/" + customDate.getYear();
                    break;
            }

            for (Transaction transactionItem: transaction.getValue()) {
                float amount = transactionItem.getAmount();

                if (amount > 0) {
                    inflow += amount;
                } else {
                    outflow += amount;
                }
            }

            titles.add(title);
            inflowSeries.getData().add(new XYChart.Data<>(title, inflow));
            outflowSeries.getData().add(new XYChart.Data<>(title, outflow));
        }

        categoryAxis.setCategories(titles);
        this.dateRangeChart.getData().addAll(inflowSeries, outflowSeries);
    }

    private void _loadPieChartData(
            PieChart pieChart,
            ObservableList<Pair<Category, ObservableList<Transaction>>> transactions
    ) {
        pieChart.getData().clear();

        for (Pair<Category, ObservableList<Transaction>> transaction: transactions) {
            float value = 0;

            for (Transaction transactionItem: transaction.getValue()) {
                value += transactionItem.getAmount();
            }

            pieChart.getData().add(new PieChart.Data(transaction.getKey().getName(), Math.abs(value)));
        }
    }

    @FXML
    private void loadMonthTransactions() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/report/report_month.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.loadBarChart();
        this._listMonthTransactions();
        this.createScreen(parent, "Report", 600, 700,"/image/Other/report_icon.png");
    }

    private void _listMonthTransactions() {
        this.listViewMonthTransactions.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Pair<CustomDate, ObservableList<Transaction>>>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Pair<CustomDate, ObservableList<Transaction>>> observable,
                            Pair<CustomDate, ObservableList<Transaction>> oldValue,
                            Pair<CustomDate, ObservableList<Transaction>> newValue
                    ) {
                        try {
                            if (newValue == null) {
                                return;
                            }

                            ObservableList<Transaction> transactions = FXCollections.observableArrayList(newValue.getValue());
                            _loadMonthTransactionsDetail(transactions);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        if (this.monthTransactions.size() == 0) {
            this.listViewMonthTransactions.setPlaceholder(new Label("No Transaction In List"));
        }

        this.listViewMonthTransactions.setItems(this.monthTransactions);
        this.listViewMonthTransactions.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    ReportCell reportCell = new ReportCell(startDate, endDate);
                    reportCell.setWallet(getWallet());

                    return reportCell;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void _loadMonthTransactionsDetail(ObservableList<Transaction> transactions) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/budget/budget_detail.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        ReportPresenter.listTransactions(this.listViewTransactions, transactions, this.getWallet());
        String startedAt = startDate.format(DateHelper.getFormat()),
                endedAt = endDate.format(DateHelper.getFormat());
        this.textDialogTitle.setText("Transactions from " + startedAt + " to " + endedAt);
        this.createScreen(parent, "Report Detail", 470, 480,"/image/Other/report_icon.png");
    }

    public static void listTransactions(
            ListView listView,
            ObservableList<Transaction> transactions,
            Wallet wallet
    ) {
        if (transactions.size() == 0) {
            listView.setPlaceholder(new Label("No Transaction In List"));
        }

        listView.setItems(transactions);
        listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    TransactionCell transactionCell = new TransactionCell();
                   transactionCell.setDisableOptions(true);
                    transactionCell.setWallet(wallet);

                    return transactionCell;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    @FXML
    private void changeDayRange() throws SQLException {
        LocalDate startDate = this.datePickerStartDate.getValue();
        LocalDate endDate = this.datePickerEndDate.getValue();

        if (startDate != null && endDate != null
                && (!DateHelper.isSameDay(this.startDate, startDate) || !DateHelper.isSameDay(this.endDate, endDate))) {
            this.startDate = startDate;
            this.endDate = endDate;

            Wallet wallet = this.getWallet();
            this.transactions.setAll(
                    this.transactionController.listNotReportedByDateRange(wallet.getId(), startDate, endDate)
            );
            this.sortData(wallet.getMoneySymbol(), this.startDate, this.endDate);
            this.loadBarChart();
            this._loadPieCharts();
        }
    }

    private static void _sortTransactionsByMonth(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> sortedTransactions,
            ObservableList<Transaction> transactions,
            String moneySymbol
    ) {
        sortedTransactions.clear();

        for (Transaction transaction : transactions) {
            boolean hasMonth = false;
            int month = transaction.getTransactedAt().getMonthValue();

            for (Pair<CustomDate, ObservableList<Transaction>> pair : sortedTransactions) {
                if (pair.getKey().getMonthNumber() == month) {
                    pair.getValue().add(transaction);
                    hasMonth = true;
                    break;
                }
            }

            if (!hasMonth) {
                ReportPresenter.addNewDay(sortedTransactions, transaction, transaction.getTransactedAt(), moneySymbol);
            }
        }
    }

    public static void sortTransactionsByDate(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> sortedTransactions,
            ObservableList<Transaction> transactions,
            String moneySymbol
    ) {
        sortedTransactions.clear();

        for (Transaction transaction : transactions) {
            boolean hasDay = false;
            int day = transaction.getTransactedAt().getDayOfMonth();

            for (Pair<CustomDate, ObservableList<Transaction>> pair : sortedTransactions) {
                if (pair.getKey().getDayOfMonth() == day) {
                    pair.getValue().add(transaction);
                    hasDay = true;
                    break;
                }
            }

            if (!hasDay) {
                ReportPresenter.addNewDay(sortedTransactions, transaction, transaction.getTransactedAt(), moneySymbol);
            }
        }
    }

    public static void addNewDay(
            ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions,
            Transaction newTransaction,
            LocalDate newLocalDate,
            String moneySymbol
    ) {
        CustomDate newCustomDate = new CustomDate();
        newCustomDate.setDayOfMonth(newLocalDate.getDayOfMonth());
        newCustomDate.setDayOfWeek(newLocalDate.getDayOfWeek().toString());
        newCustomDate.setMonth(newLocalDate.getMonth().toString());
        newCustomDate.setMonthNumber(newLocalDate.getMonthValue());
        newCustomDate.setYear(newLocalDate.getYear());
        newCustomDate.setSymbol(moneySymbol);
        transactions.add(new Pair<>(newCustomDate, FXCollections.observableArrayList(newTransaction)));
    }
}
