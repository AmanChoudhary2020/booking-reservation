package com.amanchoudhary.booking_reservation;

// DELETE THIS FILE LATER

public class Booking {
    private String username;
    private String inventoryType;
    private String originCity;
    private String destinationCity;
    private String hotelName;
    private String airlineCode;
    private String roomType;
    private String startDate;
    private String endDate;
    private String cardLast4;
    private double baseAmount;
    private double taxAmount;
    private double totalAmount;
    private String currency;

    // Constructor
    public Booking(String username, String inventoryType, String originCity, String destinationCity,
            String hotelName, String airlineCode, String roomType, String startDate,
            String endDate, String cardLast4, double baseAmount, double taxAmount,
            double totalAmount, String currency) {
        this.username = username;
        this.inventoryType = inventoryType;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.hotelName = hotelName;
        this.airlineCode = airlineCode;
        this.roomType = roomType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cardLast4 = cardLast4;
        this.baseAmount = baseAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
