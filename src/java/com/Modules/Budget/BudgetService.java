package com.Modules.Budget;

import com.Infrastructure.Base.BaseService;
import com.Infrastructure.Constant.CommonConstant;
import com.Infrastructure.Exception.BadRequestException;
import com.Modules.Transaction.TransactionService;
import com.Infrastructure.Exception.NotFoundException;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;

public class BudgetService extends BaseService {
    private TransactionService transactionService;

    public BudgetService() throws SQLException, ClassNotFoundException {
        super();
    }

    private TransactionService _getTransactionService() throws SQLException, ClassNotFoundException {
        if (this.transactionService == null) {
            this.transactionService = new TransactionService();
        }

        return this.transactionService;
    }

    protected String getTable() {
        return Budget.getTable();
    }

    public ArrayList<Budget> list(int userId) throws SQLException {
        ArrayList<Budget> budgets = this._list(userId);

        return budgets;
    }

    public Budget getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getDetailByJoin(
                "budgets.*, " +
                        "categories.icon as category_icon, categories.name as category_name, " +
                        "sub_categories.icon as sub_category_icon, sub_categories.name as sub_category_name",
                "LEFT JOIN categories ON categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstant.APP_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END " +
                        "LEFT JOIN sub_categories ON sub_categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstant.APP_SUB_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END",
                "budgets.id = " + id
        );

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Budget budget = this._toObject(resultSet);
        this.closeStatement();

        return budget;
    }

    public Budget getDetail(String name) throws SQLException, NotFoundException {
        ResultSet resultSet = this._getDetailBy("name = '" + name + "'");

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Budget budget = this._toObject(resultSet);
        this.closeStatement();

        return budget;
    }

    public Budget create(Budget budget) throws SQLException, NotFoundException, ClassNotFoundException, BadRequestException {
        this.verifySameBudget(budget);
        int id = this._create(budget);
        float amount = this._getTransactionService().getAmountByBudget(budget);
        this._setSpentAmount(Math.abs(amount), id);

        return this.getDetail(id);
    }

    public boolean create(ArrayList<Budget> budgets) throws SQLException {
        this._create(budgets);

        return true;
    }

    public void update(Budget budget, int id) throws SQLException, ClassNotFoundException, BadRequestException {
        this.verifySameBudget(budget);
        this._update(budget, id);
        float amount = this._getTransactionService().getAmountByBudget(budget);
        this._setSpentAmount(Math.abs(amount), id);
    }

    public void increaseSpentAmount(float amount, int typeId, String type, LocalDate transactedAt) throws SQLException {
        this._increaseSpentAmount(amount, typeId, type, transactedAt);
    }

    private void verifySameBudget(Budget budget) throws SQLException, BadRequestException {
        String idCondition = "";

        if (budget.getId() != 0) {
            idCondition = "id != " + budget.getId();
        }

        ResultSet resultSet = this._getDetailBy(
                "user_id = " + budget.getUserId(),
                "budgetable_id = " + budget.getBudgetableId(),
                "budgetable_type = '" + budget.getBudgetableType() + "'",
                "started_at = '" + budget.getStartedAt().toString() + "'",
                "ended_at = '" + budget.getEndedAt().toString() + "'",
                idCondition
        );

        if (resultSet.next()) {
            this.closeStatement();
            throw new BadRequestException();
        }

        this.closeStatement();
    }

    /*====================================================================================*/

    private ArrayList<Budget> _list(int userId) throws SQLException {
        ArrayList<Budget> budgets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "budgets.*, " +
                        "categories.icon as category_icon, categories.name as category_name, " +
                        "sub_categories.icon as sub_category_icon, sub_categories.name as sub_category_name",
                "LEFT JOIN categories ON categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstant.APP_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END " +
                        "LEFT JOIN sub_categories ON sub_categories.id = " +
                        "CASE WHEN budgets.budgetable_type = '" + CommonConstant.APP_SUB_CATEGORY + "' " +
                        "THEN budgets.budgetable_id ELSE null END",
                "user_id = " + userId
        );

        while (resultSet.next()) {
            budgets.add(this._toObject(resultSet));
        }

        return budgets;
    }

    private int _create(Budget budget) throws SQLException {
        String statementString = "INSERT INTO budgets(user_id, budgetable_id, budgetable_type, started_at, ended_at, amount, spent_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.handleCreateProcess(budget, statementString);
        statement.setTimestamp(8, this.getCurrentTime());
        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }

    private void _create(ArrayList<Budget> budgets) throws SQLException {
        String statementString = "INSERT INTO budgets(user_id, budgetable_id, budgetable_type, started_at, ended_at, amount, spent_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        int i = 0;

        for (Budget budget : budgets) {
            LocalDateTime currentDate = LocalDateTime.now().plusSeconds(i);
            statement.setInt(1, budget.getUserId());
            statement.setInt(2, budget.getBudgetableId());
            statement.setString(3, budget.getBudgetableType());
            statement.setDate(4, Date.valueOf(budget.getStartedAt().toString()));
            statement.setDate(5, Date.valueOf(budget.getEndedAt().toString()));
            statement.setFloat(6, budget.getAmount());
            statement.setFloat(7, budget.getSpentAmount());
            statement.setTimestamp(8, Timestamp.valueOf(currentDate));
            statement.addBatch();
            i++;
            if (i % 1000 == 0 || i == budgets.size()) {
                statement.executeBatch(); // Execute every 1000 items.
                statement.clearBatch();
            }
        }

        this.closePreparedStatement();
    }

    private void _update(Budget budget, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET user_id = ?, budgetable_id = ?, budgetable_type = ?, started_at = ?, ended_at = ?, amount = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.handleCreateProcess(budget, statementString);
        statement.setTimestamp(7, this.getCurrentTime());
        statement.setInt(8, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    private void _increaseSpentAmount(float amount, int typeId, String type, LocalDate transactedAt) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET spent_amount = spent_amount + ?, updated_at = ? WHERE budgetable_id = ? AND budgetable_type = ? AND started_at <= CAST(? AS DATE) AND ended_at > CAST(? AS DATE)";
        String transactedAtText = transactedAt.toString();
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setFloat(1, amount);
        statement.setTimestamp(2, this.getCurrentTime());
        statement.setInt(3, typeId);
        statement.setString(4, type);
        statement.setString(5, transactedAtText);
        statement.setString(6, transactedAtText);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    private void _setSpentAmount(float amount, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET spent_amount = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setFloat(1, amount);
        statement.setTimestamp(2, this.getCurrentTime());
        statement.setInt(3, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    private PreparedStatement handleCreateProcess(Budget budget, String statementString) throws SQLException {
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, budget.getUserId());
        statement.setInt(2, budget.getBudgetableId());
        statement.setString(3, budget.getBudgetableType());
        statement.setDate(4, Date.valueOf(budget.getStartedAt().toString()));
        statement.setDate(5, Date.valueOf(budget.getEndedAt().toString()));
        statement.setFloat(6, budget.getAmount());

        if (statementString.contains("INSERT")) {
            statement.setFloat(7, budget.getSpentAmount());
        }

        return statement;
    }

    @Override
    protected Budget _toObject(ResultSet resultSet) throws SQLException {
        Budget budget = new Budget();
        budget.setId(resultSet.getInt("id"));
        budget.setUserId(resultSet.getInt("user_id"));
        budget.setBudgetableId(resultSet.getInt("budgetable_id"));
        budget.setBudgetableType(resultSet.getString("budgetable_type"));
        budget.setStartedAt(resultSet.getDate("started_at").toLocalDate());
        budget.setEndedAt(resultSet.getDate("ended_at").toLocalDate());
        budget.setAmount(resultSet.getFloat("amount"));
        budget.setSpentAmount(resultSet.getFloat("spent_amount"));
        budget.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        budget.setUpdatedAt(this.getUpdatedAt(resultSet.getTimestamp("updated_at")));

        if (budget.getBudgetableType().equals(CommonConstant.APP_SUB_CATEGORY)) {
            budget.setCategoryIcon(resultSet.getString("sub_category_icon"));
            budget.setCategoryName(resultSet.getString("sub_category_name"));
        } else {
            budget.setCategoryIcon(resultSet.getString("category_icon"));
            budget.setCategoryName(resultSet.getString("category_name"));
        }

        return budget;
    }
}
