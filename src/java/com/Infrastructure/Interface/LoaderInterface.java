package com.Infrastructure.Interface;

import com.Modules.Wallet.Wallet;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;

public interface LoaderInterface {
    void loadPresenter() throws SQLException, ClassNotFoundException;

    void setWalletIndex(IntegerProperty walletIndex);

    void setWallets(ObservableList<Wallet> wallets) throws IOException, SQLException, InterruptedException;
}
