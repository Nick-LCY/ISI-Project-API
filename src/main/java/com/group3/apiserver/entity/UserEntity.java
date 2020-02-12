package com.group3.apiserver.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user", schema = "isi")
public class UserEntity {
    private int id;
    private String name;
    private String email;
    private String pwd;
    private String shippingAddr;
    private boolean isVendor;
    private String token;
    private String createTime;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "pwd")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Basic
    @Column(name = "shipping_addr")
    public String getShippingAddr() {
        return shippingAddr;
    }

    public void setShippingAddr(String shippingAddr) {
        this.shippingAddr = shippingAddr;
    }

    @Basic
    @Column(name = "is_vendor")
    public boolean getIsVendor() {
        return isVendor;
    }

    public void setIsVendor(boolean isVendor) {
        this.isVendor = isVendor;
    }

    @Basic
    @Column(name = "token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Basic
    @Column(name = "create_time")
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return id == that.id &&
                isVendor == that.isVendor &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(pwd, that.pwd) &&
                Objects.equals(shippingAddr, that.shippingAddr) &&
                Objects.equals(token, that.token) &&
                Objects.equals(createTime, that.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, pwd, shippingAddr, isVendor, token, createTime);
    }
}
