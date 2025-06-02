package com.example.moneytrackapp.models;

public class Wallet {
    private String id; // ID unik untuk dompet, akan menjadi key di Firebase
    private String name;
    private double amount;
    private String category; // Misalnya: "E-wallet", "Bank Account", "Cash"

    // Konstruktor kosong diperlukan untuk Firebase Realtime Database
    public Wallet() {
    }

    public Wallet(String id, String name, double amount, String category) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    // Setter methods (opsional, tergantung kebutuhan, tapi biasanya diperlukan untuk Firebase)
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}