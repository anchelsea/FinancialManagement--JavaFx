package com.Modules.Budget;


import com.Infrastructure.Constant.CommonConstant;
import com.Infrastructure.Interface.DialogInterface;
import com.Infrastructure.Exception.BadRequestException;
import com.Infrastructure.Helper.CurrencyHelper;
import com.Infrastructure.Helper.DateHelper;
import com.Modules.Time.CustomDate;
import com.Modules.Transaction.TransactionController;
import com.Modules.Transaction.Transaction;
import com.Modules.User.User;
import com.Modules.User.UserController;
import com.Modules.User.UserPresenter;
import com.Modules.User.UserService;
import com.Modules.Wallet.Wallet;
import com.Modules.Category.CategoryPresenter;
import com.Modules.Wallet.WalletController;
import com.Other.Base.PagePresenter;
import com.Modules.Report.ReportPresenter;
import com.Modules.Transaction.TransactionPresenter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


public class BudgetCell extends ListCell<Budget> implements DialogInterface {
    private CategoryPresenter categoryPresenter;

    private Budget budget;

    private VBox budgetCell;

    private StringProperty handledBudgetId;

    private  UserController userController;

    private static User user;

    public com.Modules.User.UserService userService;


    private IntegerProperty walletIndex;

    private ObservableList<Transaction> transactions;

    public com.Modules.Wallet.WalletController walletController;

    private LocalDate currentDate = LocalDate.now();

    public BudgetCell(StringProperty handledBudgetId) throws IOException, ClassNotFoundException, SQLException {
        this.handledBudgetId = handledBudgetId;
        this._loadCell();
    }

    private void _loadCell() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader budgetCellLoader = new FXMLLoader(getClass().getResource("/fxml/budget/budget_cell.fxml"));
        budgetCellLoader.setController(this);
        this.budgetCell = budgetCellLoader.load();
    }
    public static User getUser() {
        return user;
    }

   public ArrayList<Wallet>  wallets ;




    public void setWallets(ArrayList<Wallet> wallets) throws SQLException, ClassNotFoundException {
        this.wallets = wallets;
    }


//    public Wallet getWallet() {
//        return this.wallets.get(1);
//    }


    /*========================== Draw ==========================*/
    @FXML
    private ListView listViewTransactions;

    @FXML
    private Label
            labelBudgetTime,
            labelBudgetRemainingTime,
            labelBudgetAmount,
            labelBudgetRemainingAmount,
            labelBudgeSpentAmount,
            labelBudgetNormalDailyAmount,
            labelBudgetDailyAmount,
            labelBudgetStatus,
            labelBudgetDate,
            labelBudgetCategoryName;

    @FXML
    private ProgressBar progressBarRemainingAmount;

    @FXML
    private ImageView imageBudgetCategory;

    @FXML
    private AreaChart areaChartDetail;

    @FXML
    private Button selectCategory;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldBudgetAmount;

    @FXML
    private DatePicker datePickerStartedAt, datePickerEndedAt;

    @FXML
    private Text textDialogTitle;

    private IntegerProperty
            walletId = new SimpleIntegerProperty(0),
            selectedCategory = new SimpleIntegerProperty(0),
            selectedSubCategory = new SimpleIntegerProperty(0);

    @Override
    protected void updateItem(Budget item, boolean empty) {
        super.updateItem(item, empty);
        this.budget = item;

        if (empty) {
            setGraphic(null);
            return;
        }

     //   String moneySymbol = this.getWallet().getMoneySymbol();
        LocalDate startedAt = item.getStartedAt(),
                endedAt = item.getEndedAt();
        long daysLeft = ChronoUnit.DAYS.between(this.currentDate, endedAt);
        daysLeft = daysLeft >= 1 ? daysLeft : 0;
        float remainingAmount = budget.getAmount() - budget.getSpentAmount();
        System.out.println(remainingAmount);
        String startedAtText = startedAt.format(DateTimeFormatter.ofPattern("MM/dd/YYYY")),
                endedAtText = endedAt.format(DateTimeFormatter.ofPattern("MM/dd/YYYY")),
                imageUrl = "/image/category/" + item.getCategoryIcon() + ".png";
        this.labelBudgetRemainingTime.setText(daysLeft + (daysLeft > 1 ? " days" : " day") + " left");
        this.labelBudgetRemainingAmount.getStyleClass().removeAll("success-color", "danger-color");
        this.labelBudgetRemainingAmount.getStyleClass().add((remainingAmount > 0) ? "success-color" : "danger-color");
        this.labelBudgetRemainingAmount.setText(
                (remainingAmount > 0 ? "Left " : "Overspent ") + CurrencyHelper.toMoneyString(remainingAmount, "đ")
        );
        this.labelBudgetTime.setText(startedAtText + " - " + endedAtText);
       this.labelBudgetAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount(), "đ"));
        this.imageBudgetCategory.setImage(new Image(imageUrl));
        this.progressBarRemainingAmount.setProgress(budget.getSpentAmount() / budget.getAmount());
        setGraphic(this.budgetCell);
    }

    @FXML
    private void showPopup(Event e) throws IOException {
        this.addViewPopup((Node) e.getSource());
    }

    @FXML
    private void show() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/budget/budget_show.fxml")
        );
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();
        this._loadAreaChart();
        this._loadBudgetData();
        this.createScreen(parent, "Budget Detail", 470, 500,"/image/Other/budget_icon.png");
    }

    private void _loadBudgetData() {
        long totalDays = Math.abs(ChronoUnit.DAYS.between(this.budget.getStartedAt(), this.budget.getEndedAt()));
        long passingDays = Math.abs(ChronoUnit.DAYS.between(this.budget.getStartedAt(), this.currentDate));
        String moneySymbol = "đ",
                imageUrl = "/image/category/" + this.budget.getCategoryIcon() + ".png"; //this.getWallet().getMoneySymbol()

        if (this._isOverspent(budget)) {
            this.labelBudgetStatus.setText("Overspent");
            this.labelBudgetRemainingAmount.getStyleClass().add("danger-color");
        } else {
            this.labelBudgetStatus.setText("Left");
        }

        this.imageBudgetCategory.setImage(new Image(imageUrl));
        this.labelBudgetDate.setText(
                budget.getStartedAt().format(DateHelper.getFormat()) + " - " + budget.getEndedAt().format(DateHelper.getFormat())
        );
        this.labelBudgetCategoryName.setText(budget.getCategoryName());
        this.labelBudgetAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount(), moneySymbol));
        this.labelBudgetRemainingAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount() - budget.getSpentAmount(), moneySymbol));
        this.labelBudgeSpentAmount.setText(CurrencyHelper.toMoneyString(budget.getSpentAmount(), moneySymbol));
        this.labelBudgetNormalDailyAmount.setText(CurrencyHelper.toMoneyString(budget.getAmount() / totalDays, moneySymbol) + " / day");
        this.labelBudgetDailyAmount.setText(CurrencyHelper.toMoneyString(budget.getSpentAmount() / (passingDays == 0 ? 1 : passingDays), moneySymbol));
    }

    private void _loadAreaChart() throws SQLException, ClassNotFoundException {
        this.transactions = FXCollections.observableArrayList((new TransactionController()).listByBudget(this.budget));
        ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions = FXCollections.observableArrayList();
        ReportPresenter.sortTransactionsByDate(transactions, this.transactions, "đ");//this.getWallet().getMoneySymbol()
        this._loadAreaChartData(transactions, this.budget);
    }

    private void _loadAreaChartData(ObservableList<Pair<CustomDate, ObservableList<Transaction>>> transactions, Budget budget) {
        this.areaChartDetail.getData().clear();
        XYChart.Series series = new XYChart.Series();

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Pair<CustomDate, ObservableList<Transaction>> transaction = transactions.get(i);
            CustomDate customDate = transaction.getKey();
            String title;
            float amount = 0;

            if (DateHelper.isSameYear(budget.getStartedAt(), budget.getStartedAt())) {
                title = Integer.toString(customDate.getDayOfMonth()) + "/" + Integer.toString(customDate.getMonthNumber());
            } else {
                title = Integer.toString(customDate.getDayOfMonth()) + "/" + Integer.toString(customDate.getMonthNumber()) + "/" + Integer.toString(customDate.getYear());
            }

            for (Transaction transactionItem: transaction.getValue()) {
                amount += transactionItem.getAmount();
            }

            series.getData().add(new XYChart.Data<>(title, amount));
        }

        this.areaChartDetail.getData().add(series);
    }

    @FXML
    private void loadBudgetTransactions() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/budget/budget_detail.fxml")
        );
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.walletController=new WalletController();
        this.userService= new UserService();
        this.setWallets(walletController.list(UserPresenter.getUser().getId()));
        User user = UserPresenter.getUser();
        int user_id=user.getId();
        System.out.println(user_id);


        for (Wallet wallet:wallets){
            ReportPresenter.listTransactions(this.listViewTransactions, this.transactions, wallet);
            System.out.println(wallet.getName());
        }


        Budget budget = this.budget;
        String startedAt = budget.getStartedAt().format(DateHelper.getFormat()),
                endedAt = budget.getEndedAt().format(DateHelper.getFormat());
        this.textDialogTitle.setText("Transactions from " + startedAt + " to " + endedAt);
        this.createScreen(parent, "Budget Detail", 470, 480,"/image/Other/budget_icon.png");
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    @FXML
    private void edit() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/budget/budget_save.fxml")
        );
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();
        this.categoryPresenter = new CategoryPresenter(this.selectedCategory, this.selectedSubCategory);
        this.loadBudgetData();
        this.createScreen(parent, "Edit Budget", 500, 170,"/image/Other/budget_icon.png");
    }

    private void loadBudgetData() {
     //   this.walletId.set(this.budget.getUserId());
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
      //  PagePresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
        this.textFieldBudgetAmount.setText(CurrencyHelper.toMoney(this.budget.getAmount()));
        this.datePickerStartedAt.setValue(this.budget.getStartedAt());
        this.datePickerEndedAt.setValue(this.budget.getEndedAt());

        if (this.budget.getBudgetableType().equals(CommonConstant.APP_SUB_CATEGORY)) {
            this.selectedSubCategory.set(this.budget.getBudgetableId());
        } else {
            this.selectedCategory.set(this.budget.getBudgetableId());
        }
    }

    @FXML
    private void changeAmount() {
        TransactionPresenter.parseTextFieldMoney(this.textFieldBudgetAmount);
    }

    @FXML
    private void saveBudget(Event event) {
        User user = UserPresenter.getUser();
        int userId = user.getId();
        int categoryId = this.selectedCategory.get();
        int subCategoryId = this.selectedSubCategory.get();
        String amountText = this.textFieldBudgetAmount.getText();
        float amount = Float.valueOf(amountText.isEmpty() ? "0" : amountText.replaceAll("[^\\d.]", ""));
        LocalDate startedAt = this.datePickerStartedAt.getValue();
        LocalDate endedAt = this.datePickerEndedAt.getValue();
        String validation = BudgetPresenter.validateBudget(userId, categoryId, subCategoryId, amount, startedAt, endedAt);

        if (validation != null) {
            this.showErrorDialog(validation);
            return;
        }

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setAmount(amount);
        budget.setStartedAt(startedAt);
        budget.setEndedAt(endedAt);
        BudgetPresenter.addCategory(budget, categoryId, subCategoryId);

        try {
            int id = this.budget.getId();
            (new BudgetController()).update(budget, id);
            this.handledBudgetId.set(null);
            this.handledBudgetId.set("UPDATE-" + id);
            this.closeScene(event);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        } catch (BadRequestException e) {
            this.showErrorDialog("Same budget existed");
        }
    }

    @FXML
    private void delete() {
        ButtonBar.ButtonData buttonData = this.showDeleteDialog();
        if (buttonData == ButtonBar.ButtonData.YES) {
            try {
                int id = this.budget.getId();
                (new BudgetController()).delete(id);
                this.handledBudgetId.set("DELETE-" + id);
            } catch (SQLException | ClassNotFoundException e1) {
                e1.printStackTrace();
                this.showErrorDialog("An error has occurred");
            }
        }
    }

    private boolean _isOverspent(Budget budget) {
        return budget.getSpentAmount() > budget.getAmount();
    }

    @FXML
    public void closeScene(Event e) {
        DialogInterface.closeScene(e);
    }
}

