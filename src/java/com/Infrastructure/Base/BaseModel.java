package com.Infrastructure.Base;

import java.time.LocalDateTime;


abstract public class BaseModel {


    protected int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
