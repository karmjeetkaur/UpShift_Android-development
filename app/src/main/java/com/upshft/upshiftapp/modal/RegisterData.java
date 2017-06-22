package com.upshft.upshiftapp.modal;

import java.util.ArrayList;

/**
 * Created by new on 12/30/2016.
 */

public class RegisterData
{
    public String name;
    public String email;
    public String password;
    public String phone_number;
    public String gender;
    public String age;
    public String car;
    public String year;
    public String make;
    public String model;
    public String miles;
    public String optional;

    public ArrayList<String> conversationKeys;
    public ArrayList<Grid_Modal> modalArrayList;

    public RegisterData()
    {
    }

    public RegisterData(String name,String email, String password, String phone_number , String gender , String age , String car,
                        String year, String make, String model, String miles, String optional,ArrayList<Grid_Modal> modalArrayList,
                        ArrayList<String> conversationKeys)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone_number = phone_number;
        this.gender = gender;
        this.age = age;
        this.car = car;
        this.year = year;
        this.make = make;
        this.model = model;
        this.miles = miles;
        this.optional = optional;
        this.modalArrayList = modalArrayList;
        this.conversationKeys = conversationKeys;
    }

    public ArrayList<String> getConversationKeys() {
        return conversationKeys;
    }

    public ArrayList<Grid_Modal> nameArrayList() {
        return modalArrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }
}
