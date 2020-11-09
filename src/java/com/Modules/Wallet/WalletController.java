package com.Modules.Wallet;


import com.Infrastructure.Base.BaseService;
import com.Infrastructure.Exception.NotFoundException;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WalletController extends BaseService {
    private WalletService service;

    public WalletController() throws SQLException, ClassNotFoundException {
        service = new WalletService();
    }

    @Override
    protected Object _toObject(ResultSet resultSet) throws SQLException {
        return null;
    }

    @Override
    protected String getTable() {
        return null;
    }

    public ArrayList<Wallet> list() throws SQLException {
        ArrayList<Wallet> wallets = this.service.list();

        return wallets;
    }

    public ArrayList<Wallet> list(int userId) throws SQLException {
        ArrayList<Wallet> wallets = this.service.list(userId);

        return wallets;
    }

    public Wallet getDetail(int id) throws SQLException, NotFoundException {
        Wallet wallet = this.service.getDetail(id);

        return wallet;
    }



    public Wallet create(Wallet wallet) throws SQLException, NotFoundException {
        Wallet newWallet = this.service.create(wallet);

        return newWallet;
    }



    public int getUserId(int id) throws NotFoundException, SQLException {
        int userId=this.service.getUserIdbyWallet(id);

        return userId;
    }

    public int getWalletId() throws NotFoundException, SQLException {
        int WalletId=this.service.getWalletId();

        return WalletId;
    }

    public Wallet getWalletByUser(int userId) throws SQLException {
        ResultSet resultSet = this.getByJoin(
                "wallets.*, currencies.symbol as money_symbol",
                "INNER JOIN users ON users.id = wallets.user_id " +
                        "INNER JOIN currencies ON wallets.currency_id = currencies.id",
                "user_id = " + userId
        );
        Wallet wallet=this.service._toObject(resultSet);
        return wallet;

    }

    public boolean update(Wallet wallet, int id) throws SQLException {
        this.service.update(wallet, id);

        return true;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.delete(id);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        WalletController w=new WalletController();
        ArrayList<Wallet> wallets= w.list(59);
        for (Wallet wallet:wallets){
            System.out.println(wallet);
        }

    }
}

