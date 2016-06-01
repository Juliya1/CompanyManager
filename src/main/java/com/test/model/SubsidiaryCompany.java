package com.test.model;

public class SubsidiaryCompany extends Company {

    public SubsidiaryCompany(String name, int earnings, Company parent) {
        super(name, earnings);
        this.parent = parent;
    }

    public SubsidiaryCompany(String name, int earnings, String parentName) {
        super(name, earnings);
        this.parentName = parentName;
    }

    @Override
    public Company getParent() {
        return parent;
    }

    @Override
    public String getParentName() {
        return parentName;
    }
}
