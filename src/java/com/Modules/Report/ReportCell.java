package com.Modules.Report;

import com.Infrastructure.Constant.CommonConstant;
import com.Infrastructure.Helper.CurrencyHelper;
import com.Infrastructure.Helper.DateHelper;
import com.Modules.Time.CustomDate;
import com.Modules.Transaction.Transaction;
import com.Modules.Wallet.Wallet;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportCell extends ListCell<Pair<CustomDate, ObservableList<Transaction>>> {
    private HBox reportCell;

    private Wallet wallet;

    private LocalDate startDate, endDate;

    public ReportCell(LocalDate startDate, LocalDate endDate) throws IOException {
        this.startDate = startDate;
        this.endDate = endDate;
        this._loadCell();
    }

    private void _loadCell() throws IOException {
        FXMLLoader reportCellLoader = new FXMLLoader(getClass().getResource("/fxml/report/report_cell.fxml"));
        reportCellLoader.setController(this);
        this.reportCell = reportCellLoader.load();
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    /*========================== Draw ==========================*/
    @FXML
    private Label labelDateTime;

    @FXML
    private Label labelIncome;

    @FXML
    private Label labelOutcome;

    @FXML
    private Label labelAmount;

    protected void updateItem(Pair<CustomDate, ObservableList<Transaction>> item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        ObservableList<Transaction> transactions = item.getValue();
        float income = 0;
        float outcome = 0;

        for (Transaction transaction: transactions) {
            float transactionAmount = transaction.getAmount();

            if (transactionAmount < 0) {
                outcome += transactionAmount;
            } else {
                income += transactionAmount;
            }
        }

        float amount = income - Math.abs(outcome);
        CustomDate date = item.getKey();
        String month = (date.getMonthNumber() < 10) ? "0" + date.getMonthNumber() : Integer.toString(date.getMonthNumber()),
                day = (date.getDayOfMonth() < 10) ? "0" + date.getDayOfMonth() : Integer.toString(date.getDayOfMonth()),
                dateString = date.getYear() + "-" + month + "-" + day,
                moneySymbol = this.wallet.getMoneySymbol(),
                time;
        LocalDate localDate = LocalDate.parse(dateString);

        switch (DateHelper.getDateRange(this.startDate, this.endDate)) {
            case CommonConstant.DAY_RANGE:
                time = localDate.format(DateTimeFormatter.ofPattern("EEEE MM/dd/Y"));
                break;
            case (CommonConstant.MONTH_RANGE):
                time = localDate.format(DateTimeFormatter.ofPattern("MM/dd/Y"));
                break;
            default:
                time = localDate.format(DateTimeFormatter.ofPattern("MM/Y"));
                break;
        }

        labelDateTime.setText(time);
        labelIncome.setText(CurrencyHelper.toMoneyString(income, moneySymbol));
        labelOutcome.setText(CurrencyHelper.toMoneyString(outcome, moneySymbol));
        labelAmount.setText(CurrencyHelper.toMoneyString(amount, moneySymbol));
        setGraphic(this.reportCell);
    }
}
