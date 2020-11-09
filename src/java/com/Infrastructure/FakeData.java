package com.Infrastructure;


import com.Infrastructure.Constant.CommonConstant;
import com.Infrastructure.Exception.NotFoundException;
import com.Infrastructure.Helper.UpdatableBcrypt;
import com.Modules.Budget.BudgetController;
import com.Modules.Budget.Budget;
import com.Modules.Category.CategoryController;
import com.Modules.Category.Category;
import com.Modules.Currency.CurrencyController;
import com.Modules.Currency.Currency;
import com.Modules.SubCategory.SubCategoryController;
import com.Modules.SubCategory.SubCategory;
import com.Modules.Transaction.TransactionController;
import com.Modules.Transaction.Transaction;
import com.Modules.Type.TypeController;
import com.Modules.Type.Type;
import com.Modules.User.UserController;
import com.Modules.User.User;
import com.Modules.Wallet.WalletController;
import com.Modules.Wallet.Wallet;
import javafx.util.Pair;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class FakeData {
    private static FakeData fakeData;


    private CurrencyController currencyController;

    private UserController userController;

    private WalletController walletController;

    private TypeController typeController;

    private CategoryController categoryController;

    private SubCategoryController subCategoryController;

    private TransactionController transactionController;

    private BudgetController budgetController;


    public FakeData() throws SQLException, ClassNotFoundException {
        this.currencyController = new CurrencyController();
        this.userController = new UserController();
        this.walletController = new WalletController();
        this.typeController = new TypeController();
        this.categoryController = new CategoryController();
        this.subCategoryController = new SubCategoryController();
        this.transactionController = new TransactionController();
        this.budgetController = new BudgetController();
    }

    public static void main(String args[]) throws SQLException, ClassNotFoundException, NotFoundException {
        try {
            fakeData = new FakeData();
         // fakeData.createCurrencies();
//            fakeData.createUser();
//            fakeData.createWallets();
         //  fakeData.createTypes();
           // fakeData.createCategories();
//            fakeData.createTransactions();
//            fakeData.createBudgets();
        } catch (Exception e) {
            throw e;
        }
    }

    public void createCurrencies() throws NotFoundException, SQLException {
        Currency vnd = new Currency();
        vnd.setName("Việt Nam Đồng");
        vnd.setCode("VND");
        vnd.setSymbol("₫");
        vnd.setIcon("currency__vnd");
        this.currencyController.create(vnd);

        Currency usd = new Currency();
        usd.setName("United States Dollar");
        usd.setCode("USD");
        usd.setSymbol("$");
        usd.setIcon("currency__usd");
        this.currencyController.create(usd);

        Currency jpy = new Currency();
        jpy.setName("Yen");
        jpy.setCode("JPY");
        jpy.setSymbol("¥");
        jpy.setIcon("currency__jpy");
        this.currencyController.create(jpy);
    }





    public void createTypes() throws NotFoundException, SQLException {
        ArrayList<Type> types = new ArrayList<>();
        types.add(new Type(CommonConstant.DEBT_LOAN,"Debt/Loan"));
        types.add(new Type(CommonConstant.EXPENSE, "Expense"));
        types.add(new Type(CommonConstant.INCOME, "Income"));

        this.typeController.create(types);
    }

    public void createCategories() throws SQLException, NotFoundException {
        ArrayList<Type> types = this.typeController.list();
        ArrayList<Category> categories = new ArrayList<>();

        Type debtLoan = types.get(0);
        ArrayList<Pair<String, String>> debtLoanCategories = new ArrayList<>();
        debtLoanCategories.add(new Pair<>("Debt", "i_debt"));
        debtLoanCategories.add(new Pair<>("Debt Collection", "i_debt-collection"));
        debtLoanCategories.add(new Pair<>("Loan", "i_loan"));
        debtLoanCategories.add(new Pair<>("Repayment", "i_repayment"));

        for (Pair<String, String> pair: debtLoanCategories) {
            categories.add(new Category(
                    debtLoan.getId(),
                    debtLoan.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Type expense = types.get(1);
        ArrayList<Pair<String, String>> expenseCategories = new ArrayList<>();
        expenseCategories.add(new Pair<>("Bills & Utilities", "i_bill"));
        expenseCategories.add(new Pair<>("Business", "i_business"));
        expenseCategories.add(new Pair<>("Education", "i_education"));
        expenseCategories.add(new Pair<>("Entertainment", "i_entertainment"));
        expenseCategories.add(new Pair<>("Family", "i_family"));
        expenseCategories.add(new Pair<>("Fees & Charges", "i_fee"));
        expenseCategories.add(new Pair<>("Food & Beverage", "i_fee"));
        expenseCategories.add(new Pair<>("Friends & Lover", "i_friend"));
        expenseCategories.add(new Pair<>("Gifts & Donations", "i_gift"));
        expenseCategories.add(new Pair<>("Health & Fitness", "i_health"));
        expenseCategories.add(new Pair<>("Insurances", "i_insurances"));
        expenseCategories.add(new Pair<>("Investment", "i_investment"));
        expenseCategories.add(new Pair<>("Others", "i_others"));
        expenseCategories.add(new Pair<>("Shopping", "i_shopping"));
        expenseCategories.add(new Pair<>("Transportation", "i_transportation"));
        expenseCategories.add(new Pair<>("Travel", "i_travel"));
        expenseCategories.add(new Pair<>("Withdrawal", "i_withdrawal"));

        for (Pair<String, String> pair: expenseCategories) {
            categories.add(new Category(
                    expense.getId(),
                    expense.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Type income = types.get(2);
        ArrayList<Pair<String, String>> incomeCategories = new ArrayList<>();
        incomeCategories.add(new Pair<>("Award", "i_award"));
        incomeCategories.add(new Pair<>("Gift", "i_give"));
        incomeCategories.add(new Pair<>("Interest money", "i_interest-money"));
        incomeCategories.add(new Pair<>("Others", "i_others"));
        incomeCategories.add(new Pair<>("Salary", "i_salary"));
        incomeCategories.add(new Pair<>("Selling", "i_selling"));

        for (Pair<String, String> pair: incomeCategories) {
            categories.add(new Category(
                    income.getId(),
                    income.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        this.categoryController.create(categories);
    }

    public void createSubCategories() throws NotFoundException, SQLException {
        ArrayList<SubCategory> subCategories = new ArrayList<>();

        Category bill = this.categoryController.getDetail("Bills & Utilities");
        ArrayList<Pair<String, String>> billSubCategories = new ArrayList<>();
        billSubCategories.add(new Pair<>("Gas", bill.getIcon() + "_gas"));
        billSubCategories.add(new Pair<>("Internet", bill.getIcon() + "_internet"));
        billSubCategories.add(new Pair<>("Phone", bill.getIcon() + "_phone"));
        billSubCategories.add(new Pair<>("Rentals", bill.getIcon() + "_rentals"));
        billSubCategories.add(new Pair<>("Television", bill.getIcon() + "_television"));
        billSubCategories.add(new Pair<>("Water", bill.getIcon() + "_water"));

        for (Pair<String, String> pair: billSubCategories) {
            subCategories.add(new SubCategory(
                    bill.getTypeId(),
                    bill.getId(),
                    bill.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category education = this.categoryController.getDetail("Education");
        subCategories.add(new SubCategory(
                education.getTypeId(),
                education.getId(),
                education.getMoneyType(),
                "Books",
                education.getIcon() + "_book"
        ));

        Category entertainment = this.categoryController.getDetail("Entertainment");
        ArrayList<Pair<String, String>> entertainmentSubCategories = new ArrayList<>();
        entertainmentSubCategories.add(new Pair<>("Games", entertainment.getIcon() + "_game"));
        entertainmentSubCategories.add(new Pair<>("Movies", entertainment.getIcon() + "_movie"));

        for (Pair<String, String> pair: entertainmentSubCategories) {
            subCategories.add(new SubCategory(
                    entertainment.getTypeId(),
                    entertainment.getId(),
                    entertainment.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category family = this.categoryController.getDetail("Family");
        ArrayList<Pair<String, String>> familySubCategories = new ArrayList<>();
        familySubCategories.add(new Pair<>("Children & Babies", family.getIcon() + "_children"));
        familySubCategories.add(new Pair<>("Home Improvement", family.getIcon() + "_home-improvement"));
        familySubCategories.add(new Pair<>("Home Services", family.getIcon() + "_home-services"));
        familySubCategories.add(new Pair<>("Pets", family.getIcon() + "_pets"));

        for (Pair<String, String> pair: familySubCategories) {
            subCategories.add(new SubCategory(
                    family.getTypeId(),
                    family.getId(),
                    family.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category food = this.categoryController.getDetail("Food & Beverage");
        ArrayList<Pair<String, String>> foodSubCategories = new ArrayList<>();
        foodSubCategories.add(new Pair<>("Café", food.getIcon() + "_cafe"));
        foodSubCategories.add(new Pair<>("Restaurants", food.getIcon() + "_restaurant"));

        for (Pair<String, String> pair: foodSubCategories) {
            subCategories.add(new SubCategory(
                    food.getTypeId(),
                    food.getId(),
                    food.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category gift = this.categoryController.getDetail("Gifts & Donations");
        ArrayList<Pair<String, String>> giftSubCategories = new ArrayList<>();
        giftSubCategories.add(new Pair<>("Charity", gift.getIcon() + "_charity"));
        giftSubCategories.add(new Pair<>("Funeral", gift.getIcon() + "_funeral"));
        giftSubCategories.add(new Pair<>("Marriage", gift.getIcon() + "_marriage"));

        for (Pair<String, String> pair: giftSubCategories) {
            subCategories.add(new SubCategory(
                    gift.getTypeId(),
                    gift.getId(),
                    gift.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category health = this.categoryController.getDetail("Health & Fitness");
        ArrayList<Pair<String, String>> healthSubCategories = new ArrayList<>();
        healthSubCategories.add(new Pair<>("Doctor", health.getIcon() + "_doctor"));
        healthSubCategories.add(new Pair<>("Personal Care", health.getIcon() + "_personal-care"));
        healthSubCategories.add(new Pair<>("Pharmacy", health.getIcon() + "_pharmacy"));
        healthSubCategories.add(new Pair<>("Sports", health.getIcon() + "_sports"));

        for (Pair<String, String> pair: healthSubCategories) {
            subCategories.add(new SubCategory(
                    health.getTypeId(),
                    health.getId(),
                    health.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category shopping = this.categoryController.getDetail("Shopping");
        ArrayList<Pair<String, String>> shoppingSubCategories = new ArrayList<>();
        shoppingSubCategories.add(new Pair<>("Accessories", shopping.getIcon() + "_accessory"));
        shoppingSubCategories.add(new Pair<>("Clothing", shopping.getIcon() + "_clothing"));
        shoppingSubCategories.add(new Pair<>("Electronics", shopping.getIcon() + "_electronic"));
        shoppingSubCategories.add(new Pair<>("Footwear", shopping.getIcon() + "_footwear"));

        for (Pair<String, String> pair: shoppingSubCategories) {
            subCategories.add(new SubCategory(
                    shopping.getTypeId(),
                    shopping.getId(),
                    shopping.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        Category transportation = this.categoryController.getDetail("Transportation");
        ArrayList<Pair<String, String>> transportationSubCategories = new ArrayList<>();
        transportationSubCategories.add(
                new Pair<>("Maintenance", transportation.getIcon() + "_maintenance")
        );
        transportationSubCategories.add(
                new Pair<>("Parking Fees", transportation.getIcon() + "_parking-fee")
        );
        transportationSubCategories.add(new Pair<>("Petrol", transportation.getIcon() + "_petrol"));
        transportationSubCategories.add(new Pair<>("Taxi", transportation.getIcon() + "_taxi"));

        for (Pair<String, String> pair: transportationSubCategories) {
            subCategories.add(new SubCategory(
                    transportation.getTypeId(),
                    transportation.getId(),
                    transportation.getMoneyType(),
                    pair.getKey(),
                    pair.getValue()
            ));
        }

        this.subCategoryController.create(subCategories);
    }

//    private void createTransactions() throws SQLException, NotFoundException {
//        ArrayList<Transaction> transactions = new ArrayList<>();
//        ArrayList<Wallet> wallets = this.walletController.list(1004);
//        ArrayList<Type> types = this.typeController.list();
//        ArrayList<Category> categories = this.categoryController.list();
//        ArrayList<SubCategory> subCategories = this.subCategoryController.list();
//        int walletsQuantity = wallets.size();
//        int typesQuantity = types.size();
//        int categoriesQuantity = categories.size();
//        int subCategoriesQuantity = subCategories.size();
//
//        for (int i = 0; i < 300; i++) {
//            Transaction transaction = new Transaction();
//            transaction.setWalletId(wallets.get((int)(Math.random() * (walletsQuantity - 1))).getId());
//            transaction.setTypeId(types.get((int)(Math.random() * (typesQuantity - 1))).getId());
//            transaction.setCategoryId(categories.get((int)(Math.random() * (categoriesQuantity - 1))).getId());
//            transaction.setSubCategoryId(subCategories.get((int)(Math.random() * (subCategoriesQuantity - 1))).getId());
//            transaction.setTransactedAt(LocalDate.now());
//            transaction.setNote("This is note!");
//
//            transactions.add(transaction);
//        }
//
//        this.transactionController.create(transactions);
//    }



}

