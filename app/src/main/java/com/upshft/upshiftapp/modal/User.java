package com.upshft.upshiftapp.modal;

import java.util.ArrayList;

/**
 * Created by new on 1/6/2017.
 */
public class User
{
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String gender;
    private String age;
    private String car;
    private String car_year;
    private String car_make;
    private String car_model;
    private String car_miles;
    private String optional;

    public ArrayList<String> tools_arrayList;
    public ArrayList<String> platform_arrayList;
  //  public ArrayList<Grid_Modal> platform_arrayList;

    public User() {
    }

    public User(String id, String name, String phoneNumber, String email, String password , String gender, String age,
    String car, String car_year, String car_make , String car_model, String car_miles , String optional,
                ArrayList<String> platform_arrayList,ArrayList<String> tool_list)
    {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.car = car;
        this.car_year = car_year;
        this.car_make = car_make;
        this.car_model = car_model;
        this.car_miles = car_miles;
        this.optional = optional;
        this.platform_arrayList = platform_arrayList;
        this.tools_arrayList = tool_list;
    }


    public ArrayList<String> getTools_arrayList() {
        return tools_arrayList;
    }

    public ArrayList<String> getPlatform_arrayList() {
        return platform_arrayList;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getCar_year() {
        return car_year;
    }

    public void setCar_year(String car_year) {
        this.car_year = car_year;
    }

    public String getCar_make() {
        return car_make;
    }

    public void setCar_make(String car_make) {
        this.car_make = car_make;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    public String getCar_miles() {
        return car_miles;
    }

    public void setCar_miles(String car_miles) {
        this.car_miles = car_miles;
    }
}