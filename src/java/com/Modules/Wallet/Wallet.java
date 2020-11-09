package com.Modules.Wallet;

import com.Infrastructure.Base.BaseModel;

public class Wallet extends BaseModel {


    private int user_id;

    private int currencyId;

    private String name;

    private float amount;

    private String moneySymbol;

    public Wallet() {}

    public Wallet(int user_id, String name,int currencyId, float amount) {
        this.user_id = user_id;
        this.currencyId = currencyId;
        this.name = name;
        this.amount = amount;
    }

    public static String getTable() {
        return "wallets";
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getMoneySymbol() {
        return moneySymbol;
    }

    public void setMoneySymbol(String moneySymbol) {
        this.moneySymbol = moneySymbol;
    }
}
