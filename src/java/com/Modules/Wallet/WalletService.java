package com.Modules.Wallet;

import com.Infrastructure.Base.BaseService;
import com.Infrastructure.Exception.NotFoundException;
import com.Modules.Wallet.WalletController;

import java.sql.*;
import java.util.ArrayList;

public class WalletService extends BaseService {
    public WalletService() throws SQLException, ClassNotFoundException {
        super();
    }

    protected String getTable() {
        return Wallet.getTable();
    }

    public ArrayList<Wallet> list() throws SQLException {
        ArrayList<Wallet> wallets = this._list();

        return wallets;
    }

    /**
     * @param userId
     * @return
     * @throws SQLException
     */
    public ArrayList<Wallet> list(int userId) throws SQLException {
        ArrayList<Wallet> wallets = this._list(userId);

        return wallets;
    }

    public Wallet getDetail(int id) throws SQLException, NotFoundException {
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN users ON wallets.user_id = users.id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id",
                "wallets.id = " + id
        );

        if (!resultSet.next()) {
            throw new NotFoundException();
        }

        Wallet wallet = this._toObject(resultSet);
        this.closeStatement();

        return wallet;
    }

    public Wallet create(Wallet wallet) throws SQLException, NotFoundException {
        int id = this._create(wallet);
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN currencies ON wallets.currency_id = currencies.id " +
                        "INNER JOIN users ON wallets.user_id=users.id",
                "wallets.id = " + id
        );

        if (resultSet.next()) {
            wallet = this._toObject(resultSet);
        }

        this.closeStatement();

        return wallet;
    }


    public void update(Wallet wallet, int id) throws SQLException {
        this._update(wallet, id);
    }

    public void setAmount(float amount, int id, boolean isRollback) throws SQLException {
        if (isRollback) {
            amount = -amount;
        }

        this._setAmount(amount, id);
    }

    public boolean delete(int id) throws SQLException {
        this.deleteBy("wallets", "id = " + id);
        //this.deleteBy("budgets", "wallet_id = " + id);
        this.deleteBy("transactions", "wallet_id = " + id);

        return this.deleteById(id);
    }

    /*====================================================================================*/

    private ArrayList<Wallet> _list() throws SQLException {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN user_wallet ON wallets.id = user_wallet.wallet_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id"
        );

        while (resultSet.next()) {
            wallets.add(this._toObject(resultSet));
        }

        this.closeStatement();

        return wallets;
    }
    private ArrayList<Wallet> _listWalletbyUser(int userId) throws SQLException {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN user_wallet ON wallets.id = user_wallet.wallet_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id" ,
                        "user_id= "+userId
        );

        while (resultSet.next()) {
            wallets.add(this._toObject(resultSet));
        }

        this.closeStatement();

        return wallets;
    }

    private ArrayList<Wallet> _list(int userId) throws SQLException {
        ArrayList<Wallet> wallets = new ArrayList<>();
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN users ON users.id = wallets.user_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id",
                "user_id = " + userId
        );

        while (resultSet.next()) {
            wallets.add(this._toObject(resultSet));
        }

        this.closeStatement();

        return wallets;
    }

    private int _create(Wallet wallet) throws SQLException {
        String statementString = "INSERT INTO " + getTable() + "(user_id, name, currency_id,amount) VALUES (?, ?, ?,?)";
        PreparedStatement statement = this.getPreparedStatement(statementString, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, wallet.getUser_id());
        statement.setString(2, wallet.getName());
        statement.setInt(3, wallet.getCurrencyId());
        statement.setFloat(4, wallet.getAmount());

        statement.executeUpdate();
        int id = this.getIdAfterCreate(statement.getGeneratedKeys());
        this.closePreparedStatement();

        return id;
    }


    private void _update(Wallet wallet, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET currency_id = ?, name = ?, amount = ? WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setInt(1, wallet.getCurrencyId());
        statement.setNString(2, wallet.getName());
        statement.setFloat(3, wallet.getAmount());
        statement.setInt(4, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    private void _setAmount(float amount, int id) throws SQLException {
        String statementString = "UPDATE " + getTable() + " SET amount = amount + ?  WHERE id = ?";
        PreparedStatement statement = this.getPreparedStatement(statementString);
        statement.setFloat(1, amount);
        statement.setInt(2, id);
        statement.executeUpdate();
        this.closePreparedStatement();
    }

    public int getUserIdbyWallet(int id) throws SQLException, NotFoundException {
        Wallet wallet=new Wallet();
        String statement="SELECT user.id FROM users INNER JOIN users ON  wallets.user_id=users.id ";
        PreparedStatement statement1= this.getPreparedStatement(statement);
        ResultSet resultSet =statement1.executeQuery(statement);
        if(!resultSet.next()){
            throw new NotFoundException();
        }
        this.closeStatement();

        return  resultSet.getInt("user_id");
    }

    public int getWalletId() throws SQLException, NotFoundException {
        Wallet wallet=new Wallet();
        String statement="SELECT wallets.id FROM wallets INNER JOIN users ON  wallets.user_id=users.id";
        PreparedStatement statement1= this.getPreparedStatement(statement);
        ResultSet resultSet =statement1.executeQuery(statement);
        if(!resultSet.next()){
            throw new NotFoundException();
        }
        this.closeStatement();

        return  resultSet.getInt("id");
    }

    @Override
    protected Wallet _toObject(ResultSet resultSet) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setId(resultSet.getInt("id"));
        wallet.setUser_id(resultSet.getInt("user_id"));
        wallet.setCurrencyId(resultSet.getInt("currency_id"));
        wallet.setName(resultSet.getNString("name"));
        wallet.setAmount(resultSet.getFloat("amount"));
        wallet.setMoneySymbol(resultSet.getNString("money_symbol"));

        return wallet;
    }

}
