package com.Modules.Transaction;


import com.Infrastructure.Base.BaseModel;

import java.time.LocalDate;

public class Transaction extends BaseModel {
    private int walletId;

    private int typeId;

    private int categoryId;

    private int subCategoryId;

    private LocalDate transactedAt;

    private float amount;

    private String note;

    private String categoryName;

    private String categoryIcon;

    private String categoryMoneyType;

    private String subCategoryName;

    public String getSubCategoryIcon() { return subCategoryIcon; }

    public void setSubCategoryIcon(String subCategoryIcon) { this.subCategoryIcon = subCategoryIcon; }

    private String subCategoryIcon;

    public String getSubCategoryName() { return subCategoryName; }

    public void setSubCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; }

    public static String getTable() {
        return "transactions";
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }


    public LocalDate getTransactedAt() {
        return transactedAt;
    }

    public void setTransactedAt(LocalDate transactedAt) {
        this.transactedAt = transactedAt;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryMoneyType() {
        return categoryMoneyType;
    }

    public void setCategoryMoneyType(String categoryMoneyType) {
        this.categoryMoneyType = categoryMoneyType;
    }

    public int getSubCategoryId() { return subCategoryId; }

    public void setSubCategoryId(int subCategoryId) { this.subCategoryId = subCategoryId; }

}
