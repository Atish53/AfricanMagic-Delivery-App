package com.example.africanmagic_deliveryapp;

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Date;
import java.text.DateFormat;

public class ClassListDeliveries {
    public String fName; //CustomerFirstName
    public String lName; //CustomerLastName
    public Date saleDate;
    public Integer delId;
    public String phoneNo;
    public String address;
    public String orderStatus;
    public Integer saleId;

    public ClassListDeliveries(String firstName, String lastName, Date saled, Integer delivery, String phoneNum, String add, String orderStats, Integer SaleNo)
    {
        this.fName = firstName;
        this.lName = lastName;
        this.saleDate = saled;
        this.delId = delivery;
        this.phoneNo = phoneNum;
        this.address = add;
        this.orderStatus = orderStats;
        this.saleId = SaleNo;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public Integer getDelId() {
        return delId;
    }

    public String getPhoneNo() {return phoneNo;}

    public String getAddress() {return address;}

    public String getOrderStatus() {return orderStatus;}

    public Integer getSaleId() {return saleId;}
}


