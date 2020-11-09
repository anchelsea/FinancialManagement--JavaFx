package com.Modules.Budget;

import com.Infrastructure.Constant.CommonConstant;
import com.Infrastructure.Exception.BadRequestException;
import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Helper.DateHelper;
import com.Modules.User.User;
import com.Modules.User.UserPresenter;
import com.Modules.User.UserService;
import com.Modules.Wallet.Wallet;
import com.Modules.Category.CategoryPresenter;
import com.Modules.Wallet.WalletController;
import com.Other.Base.PagePresenter;
import com.Modules.Transaction.TransactionPresenter;
import com.jfoenix.controls.JFXDatePicker;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BudgetPresenter extends PagePresenter {
    private CategoryPresenter categoryPresenter;

    private BudgetController budgetController;

    private UserPresenter userPresenter;

    private ObservableList<Budget>
            onGoingBudgets = FXCollections.observableArrayList(),
            finishingBudgets = FXCollections.observableArrayList();

    private LocalDate currentDate;

    private StringProperty handledBudgetId = new SimpleStringProperty();

    public com.Modules.Wallet.WalletController walletController; // Get Detail when update
    public com.Modules.User.UserService userService;
    public void loadPresenter() throws SQLException, ClassNotFoundException {
        this.walletController = new WalletController();
        this.userService= new UserService();
    }

    public BudgetPresenter() throws SQLException, ClassNotFoundException {
        this.budgetController = new BudgetController();
        this.currentDate = LocalDate.now();
    }

    private static void _sortFinishingBudgets(
            ObservableList<Budget> onGoingBudgets,
            ObservableList<Budget> finishedBudgets,
            ArrayList<Budget> budgets
    ) {
        onGoingBudgets.clear();
        finishedBudgets.clear();
        LocalDate now = LocalDate.now();

        for (Budget budget: budgets) {
            LocalDate endDate = budget.getEndedAt();

            if (DateHelper.isLaterThan(endDate, now)) {
                finishedBudgets.add(budget);
            } else {
                onGoingBudgets.add(budget);
            }
        }
    }

    /*========================== Draw ==========================*/
    @FXML
    private TabPane budgetsTabPane;

    @FXML
    private ListView listViewOngoingTab, listViewFinishingTab;

    @FXML
    private Button selectCategory;

    @FXML
    private MenuButton selectWallet;

    @FXML
    private TextField textFieldBudgetAmount;

    @FXML
    private JFXDatePicker datePickerStartedAt, datePickerEndedAt;

    private IntegerProperty
            walletId = new SimpleIntegerProperty(0),
            selectedCategory = new SimpleIntegerProperty(0),
            selectedSubCategory = new SimpleIntegerProperty(0);


    private void loadBudgets(int userId) throws SQLException {
       // this.users=users;
        ArrayList<Budget> budgets = this.budgetController.list(userId);
        BudgetPresenter._sortFinishingBudgets(this.onGoingBudgets, this.finishingBudgets, budgets);
        this.handleBudgetId();
        this._setListViewBudgets(this.listViewOngoingTab, this.onGoingBudgets);
        this._setListViewBudgets(this.listViewFinishingTab, this.finishingBudgets);
    }

    @Override
    public void setWallets(ObservableList<Wallet> wallets) throws SQLException, InterruptedException {
        this.wallets = wallets;
        this.wallets.setAll(this.walletController.list(UserPresenter.getUser().getId()));
        this.loadHeaderWallets();
        this.loadBudgets(UserPresenter.getUser().getId());
    }

    private void _setListViewBudgets(ListView listView, ObservableList<Budget> budgets) {
        if (budgets.size() == 0) {
            listView.setPlaceholder(new Label("No Budget In List"));
        }

        listView.setItems(budgets);
        listView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                try {
                    BudgetCell budgetCell = new BudgetCell(handledBudgetId);
                  //  budgetCell.setWallets(wallets);
                    //budgetCell.setWalletIndex(walletIndex);

                    return budgetCell;
                } catch (IOException | ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
    }

    private void handleBudgetId() {
        this.handledBudgetId.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            try {
                this.handleBudgetIdDetail(this.onGoingBudgets, newValue);
                this.handleBudgetIdDetail(this.finishingBudgets, newValue);
            } catch (NotFoundException | SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleBudgetIdDetail(ObservableList<Budget> budgets, String type) throws NotFoundException, SQLException {
        int id = Integer.parseInt(type.substring(7)),
                i = 0,
                index = -1;

        for (Budget budget: budgets) {
            if (budget.getId() == id) {
                index = i;
                break;
            }
            i++;
        }

        if (index >= 0) {
            budgets.remove(index);

            if (type.contains("UPDATE")) {
                Budget budget = this.budgetController.getDetail(id);

                if (budget.getUserId() == UserPresenter.getUser().getId()) {
                    this._setBudget(budget);
                }
            }
        }
    }

    @FXML
    private void changeTab(Event e) {
        this.activeTab(e, this.budgetsTabPane);
    }

    @FXML
    private void chooseCategory() throws IOException {
        this.categoryPresenter.showCategoryDialog();
    }

    private void _addNewBudget(Budget budget) {
        LocalDate endedAt = budget.getEndedAt();

        if (DateHelper.isLaterThan(endedAt, this.currentDate)) {
            this.finishingBudgets.add(0, budget);
        } else {
            this.onGoingBudgets.add(0, budget);
        }
    }

    private void _setBudget(Budget budget) {
        LocalDate endedAt = budget.getEndedAt();

        if (DateHelper.isLaterThan(endedAt, this.currentDate)) {
            this._addBudget(this.finishingBudgets, budget);
        } else {
            this._addBudget(this.onGoingBudgets, budget);
        }
    }

    private void _addBudget(ObservableList<Budget> budgets, Budget newBudget) {
        int i = 0;
        LocalDateTime newBudgetDate = newBudget.getCreatedAt();

        for (Budget budget: budgets) {
            if (i < budgets.size() && (newBudgetDate.isAfter(budget.getCreatedAt())
                    || newBudgetDate.isEqual(budget.getCreatedAt()))) {
                break;
            }

            i++;
        }

        budgets.add(i, newBudget);
    }

    @FXML
    private void createBudget() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/budget/budget_save.fxml"));
        fxmlLoader.setController(this);
        GridPane parent = fxmlLoader.load();

        if (this.categoryPresenter == null) {
            this.categoryPresenter = new CategoryPresenter(this.selectedCategory, this.selectedSubCategory);
            this.categoryPresenter.setOnlyExpenseCategories(true);
        }

        this.loadBudgetData();
        this.createScreen(parent, "Create Budget", 500, 170,"/image/Other/budget_icon.png");
    }

    private void loadBudgetData() {
        this.selectedCategory.set(0);
        this.selectedSubCategory.set(0);
      //  PagePresenter.loadStaticWallets(this.selectWallet, this.walletId, this.wallets);
        this.categoryPresenter.handleSelectedCategoryId(this.selectedCategory, this.selectCategory, "category");
        this.categoryPresenter.handleSelectedCategoryId(this.selectedSubCategory, this.selectCategory, "subCategory");
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
        budget.setSpentAmount(0);
        budget.setStartedAt(startedAt);
        budget.setEndedAt(endedAt);
        BudgetPresenter.addCategory(budget, categoryId, subCategoryId);

        try {
            budget = this.budgetController.create(budget);

                this._addNewBudget(budget);

            this.closeScene(event);
        } catch (SQLException | NotFoundException | ClassNotFoundException e) {
            e.printStackTrace();
            this.showErrorDialog("An error has occurred");
        } catch (BadRequestException e) {
            this.showErrorDialog("Same budget existed");
        }
    }

    public static String validateBudget(int walletId, int categoryId, int subCategoryId, float amount, LocalDate startedAt, LocalDate endedAt) {
        if (walletId == 0) {
            return "Wallet is not selected";
        }
        if (categoryId == 0 && subCategoryId == 0) {
            return "Category is not selected";
        }
        if (amount <= 0) {
            return "Amount is not valid";
        }
        if (DateHelper.isLaterThan(endedAt, startedAt)) {
            return "Budget time is not valid";
        }

        return null;
    }

    public static void addCategory(Budget budget, int categoryId, int subCategoryId) {
        if (subCategoryId != 0) {
            budget.setBudgetableId(subCategoryId);
            budget.setBudgetableType(CommonConstant.APP_SUB_CATEGORY);
        } else {
            budget.setBudgetableId(categoryId);
            budget.setBudgetableType(CommonConstant.APP_CATEGORY);
        }
    }

//    @Override
//    public void loadPresenter() {}
}
