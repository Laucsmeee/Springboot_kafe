package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String birthdate;
    private String phone;
    private String qrCode;
    private int bonusPoints;

    // id - геттер і сеттер (якщо треба)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // username
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    // birthdate
    public String getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    // phone
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // qrCode
    public String getQrCode() {
        return qrCode;
    }
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    // bonusPoints
    public int getBonusPoints() {
        return bonusPoints;
    }
    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }
}
