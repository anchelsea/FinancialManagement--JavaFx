package com.Modules.Transaction;

import com.Infrastructure.Base.BaseService;
import com.Modules.Budget.Budget;
import com.Modules.Category.Category;
import com.Infrastructure.Exception.NotFoundException;
import com.Modules.Budget.BudgetService;
import com.Modules.Category.CategoryService;
import com.Infrastructure.Constant.CommonConstant;
import com.Modules.Wallet.WalletService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionService extends BaseService {
    private WalletService walletService;

    private CategoryService categoryService;

    private BudgetService budgetService;

    public TransactionService() throws SQLException, ClassNotFoundException {
        super();
        this.walletService = new WalletService();
        this.categoryService = new CategoryService();
    }

    private BudgetService _getBudgetService() throws SQLException, ClassNotFoundException {
        if (this.budgetService == null) {
            this.budgetService = new BudgetService();
        }

        return this.budgetService;
    }

    protected String getTable() {
        return Transaction.getTable();
    }

    public ArrayList<Transaction> listByMonth(int walletId, LocalDate date, char operator) throws SQLException {
        ArrayList<Transaction> transactions = this._list(walletId, date, operator);

        return transactions;
    }

    public ArrayList<Transaction> listByDateRange(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = this._list(walletId, startDate, endDate);

        return transactions;
    }

    public ArrayList<Transaction> listNotReportedByDateRange(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = this._listNotReported(walletId, startDate, endDate);

        return transactions;
    }

    public ArrayList<Transaction> listByBudget(Budget budget) throws SQLException {
        ArrayList<Transaction> transactions = this._list(budget);

        return transactions;
    }

    public Transaction getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "transactions.id = " + id
        );

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Transaction transaction = this._toObject(resultSet);
        this.closeStatement();

        return transaction;
    }

    public float getAmountByBudget(Budget budget) throws SQLException {
        String condition = "t.category_id = ";

        if (budget.getBudgetableType().equals(CommonConstant.APP_SUB_CATEGORY)) {
            condition = "t.sub_category_id = ";
        }

        condition += budget.getBudgetableId();
        float amount = this._calculate1(
                "SUM(t.amount) as totalAmount " ,
                "totalAmount",
                " LEFT JOIN wallets ON t.wallet_id =wallets.id  " + "LEFT JOIN users ON wallets.user_id=users.id ",

                "user_id = " + budget.getUserId(),
                condition,
                "t.transacted_at >= CAST('" + budget.getStartedAt().toString() + "' AS DATE)",
                "t.transacted_at <= CAST('" + budget.getEndedAt().toString() + "' AS DATE)"
        );

        return amount;
    }

    public Transaction create(Transaction transaction) throws SQLException, NotFoundException {
        Category category = this.categoryService.getDetail(transaction.getCategoryId());
        int id = this._create(transaction, category.getMoneyType()),
                walletId = transaction.getWalletId();
        Transaction newTransaction = this.getDetail(id);
        Thread rollbackUpdatingAmount = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    walletService.setAmount(
                            newTransaction.getAmount(),
                            walletId,
                            false
                    );

                    if (TransactionService.isSpent(category.getMoneyType())) {
                        _increaseBudgetAmount(newTransaction, false);
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        rollbackUpdatingAmount.start();

        return newTransaction;
    }

    public boolean create(ArrayList<Transaction> transactions) throws SQLException, NotFoundException {
        this._create(transactions);

        return true;
    }

    public void update(Transaction transaction, int id) throws SQLException, NotFoundException, ClassNotFoundException {
        Transaction selectedTransaction = _getTransactionById(id);
        Category oldCategory = categoryService.getDetail(selectedTransaction.getCategoryId());
        this.walletService.setAmount(selectedTransaction.getAmount(), selectedTransaction.getWalletId(), true);

        if (TransactionService.isSpent(oldCategory.getMoneyType())) {
            this._increaseBudgetAmount(selectedTransaction, true);
        }
        Category newCategory = categoryService.getDetail(transaction.getCategoryId());
        this._update(transaction, id, newCategory.getMoneyType());

        Thread updateAmount = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Transaction selectedTransaction = _getTransactionById(id);
                    walletService.setAmount(selectedTransaction.getAmount(), selectedTransaction.getWalletId(), false);

                    if (TransactionService.isSpent(newCategory.getMoneyType())) {
                        _increaseBudgetAmount(selectedTransaction, false);
                    }
                } catch (SQLException | NotFoundException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        updateAmount.start();
    }

    private void _increaseBudgetAmount(Transaction transaction, boolean isRevert) throws SQLException, ClassNotFoundException {
        float amount = Math.abs(transaction.getAmount());
        amount = (isRevert) ? -amount : amount;

        if (transaction.getSubCategoryId() != 0) {
            this._getBudgetService().increaseSpentAmount(
                    amount,
                    transaction.getSubCategoryId(),
                    CommonConstant.APP_SUB_CATEGORY,
                    transaction.getTransactedAt()
            );
        }

        this._getBudgetService().increaseSpentAmount(
                amount,
                transaction.getCategoryId(),
                CommonConstant.APP_CATEGORY,
                transaction.getTransactedAt()
        );
    }


    public void delete(int id) throws SQLException, NotFoundException {
        Transaction transaction = this._getTransactionById(id);
        Thread updateAmount = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    walletService.setAmount(transaction.getAmount(), transaction.getWalletId(), true);
                    _increaseBudgetAmount(transaction, true);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        updateAmount.start();
        this.deleteById(id);
    }

    /*====================================================================================*/
    private ArrayList<Transaction> _list(int walletId, LocalDate date, char operator) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String yearCondition = "";

        if (operator == '=') {
            yearCondition = "year(transacted_at) = " + date.getYear();
        } else {
            yearCondition = "year(transacted_at) >= " + date.getYear();
        }

        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id ",
                "wallet_id = " + walletId,
                "month(transacted_at) " + operator + " " + date.getMonthValue(),
                yearCondition
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    private ArrayList<Transaction> _list(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,
                "transacted_at >= CAST('" + startDate.toString() + "' AS DATE) AND transacted_at <= CAST('" + endDate.toString() + "' AS DATE)"
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    private ArrayList<Transaction> _listNotReported(int walletId, LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id",
                "wallet_id = " + walletId,

                "transacted_at >= CAST('" + startDate.toString() + "' AS DATE) AND transacted_at <= CAST('" + endDate.toString() + "' AS DATE)"
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    private ArrayList<Transaction> _list(Budget budget) throws SQLException {
        String categoryCondition;

        if (budget.getBudgetableType().equals(CommonConstant.APP_SUB_CATEGORY)) {
            categoryCondition = "transactions.sub_category_id = " + budget.getBudgetableId();
        } else {
            categoryCondition = "transactions.category_id = " + budget.getBudgetableId();
        }

        ArrayList<Transaction> transactions = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "transactions.*, " +
                        "categories.name as category_name, categories.icon as category_icon, categories.money_type as category_money_type, " +
                        "sub_categories.name as sub_category_name, sub_categories.icon as sub_category_icon",
                "INNER JOIN categories ON transactions.category_id = categories.id " +
                        "LEFT JOIN sub_categories ON transactions.sub_category_id = sub_categories.id "+
                        " LEFT JOIN wallets ON transactions.wallet_id =wallets.id  " + "LEFT JOIN users ON wallets.user_id=users.id ",
                "user_id = " + budget.getUserId(),
                categoryCondition,
                "transacted_at >= CAST('" + budget.getStartedAt().toString() + "' AS DATE) AND transacted_at <= CAST('" + budget.getEndedAt().toString() + "' AS DATE)"
        );

        while (resultSet.next()) {
            transactions.add(this._toObject(resultSet));
        }

        return transactions;
    }

    private int _create(Transaction transaction, String moneyType) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, category_id, sub_category_id, amount ,note,transacted_at) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private PreparedStatement handleCreateProcess(Transaction transaction, String moneyType, String statementString) throws SQLException {
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        int subcategoryId = transaction.getSubCategoryId();
        statement.setInt(1, transaction.getWalletId());
        statement.setInt(2, transaction.getCategoryId());
        statement.setDate(6, Date.valueOf(transaction.getTransactedAt().toString()));
        statement.setNString(5, transaction.getNote());
//        statement.setByte(10, (byte) (transaction.getIsNotReported() ? 1 : 0));

        if (subcategoryId == 0) {
            statement.setNull(3, Types.INTEGER);
        } else {
            statement.setInt(3, subcategoryId);
        }


        if (moneyType.equals(CommonConstant.EXPENSE)
                || moneyType.equals(CommonConstant.DEBT_COLLECTION)
                || moneyType.equals(CommonConstant.LOAN)) {
            statement.setFloat(4, -transaction.getAmount());
        } else {
            statement.setFloat(4, transaction.getAmount());
        }

        return statement;
    }

    private int _create(ArrayList<Transaction> transactions) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(wallet_id, category_id, sub_category_id, amount ,note,transacted_at) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Transaction transaction : transactions) {
            statement.setInt(1, transaction.getWalletId());
            statement.setInt(2, transaction.getCategoryId());
            statement.setInt(3, transaction.getSubCategoryId());
            statement.setFloat(4, transaction.getAmount());
            statement.setDate(6, Date.valueOf(transaction.getTransactedAt().toString()));
            statement.setNString(5, transaction.getNote());
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == transactions.size()) {
                statement.executeBatch(); // Execute every 1000 items.
                statement.clearBatch();
            }
        }

        return statement.executeUpdate();
    }

    private void _update(Transaction transaction, int id, String moneyType) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET wallet_id = ?, category_id = ?, sub_category_id = ?, amount = ?,  note = ?, transacted_at = ? WHERE id = ?";
        PreparedStatement statement = this.handleCreateProcess(transaction, moneyType, statementString);
        statement.setInt(7, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }


    @Override
    protected Transaction _toObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at").toLocalDate());
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setNote(resultSet.getNString("note"));
        transaction.setCategoryName(resultSet.getString("category_name"));
        transaction.setCategoryIcon(resultSet.getString("category_icon"));
        transaction.setCategoryMoneyType(resultSet.getString("category_money_type"));
        transaction.setSubCategoryName(resultSet.getString("sub_category_name"));
        transaction.setSubCategoryIcon(resultSet.getString("sub_category_icon"));

        return transaction;
    }

    protected Transaction _getTransactionById(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getById(id);

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Transaction transaction = this._toNormalObject(resultSet);
        this.closeStatement();

        return transaction;
    }

    protected Transaction _toNormalObject(ResultSet resultSet) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("id"));
        transaction.setWalletId(resultSet.getInt("wallet_id"));
        transaction.setCategoryId(resultSet.getInt("category_id"));
        transaction.setSubCategoryId(resultSet.getInt("sub_category_id"));
        transaction.setTransactedAt(resultSet.getDate("transacted_at").toLocalDate());
        transaction.setAmount(resultSet.getFloat("amount"));
        transaction.setNote(resultSet.getNString("note"));

        return transaction;
    }

    @Override
    protected ResultSet getByJoin(String select, String join, String... args) throws SQLException {
        String condition = this.handleConditions(args);
        String query = "SELECT " + select + " FROM " + getTable() + " " + join + condition + " ORDER BY transacted_at DESC, id DESC";
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    public static boolean isSpent(String moneyType) {
        if (moneyType.equals(CommonConstant.EXPENSE)
                || moneyType.equals(CommonConstant.DEBT_COLLECTION)
                || moneyType.equals(CommonConstant.LOAN)) {
            return true;
        }

        return false;
    }
}