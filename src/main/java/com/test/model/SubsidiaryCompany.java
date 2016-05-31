package com.test.model;

public class SubsidiaryCompany extends Company {

    public SubsidiaryCompany(String name, int earnings, String parentName) {
        super(name, earnings);
        this.parentName = parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
