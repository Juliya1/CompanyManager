package com.test.model;

import com.test.exception.CompanyException;

import java.util.ArrayList;
import java.util.List;

public class Company {

    protected String name;
    protected int earnings;
    protected String parentName;
    protected List<Company> children = new ArrayList<>();
    protected int nestingLevel;

    public Company(String name, int earnings) {
        this.name = name;
        this.earnings = earnings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEarnings() {
        return earnings;
    }

    public void setEarnings(int earnings) {
        this.earnings = earnings;
    }

    public List<Company> getChildren() {
        return children;
    }

    public void setChildren(List<Company> children) {
        this.children = children;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        throw new CompanyException("Non-subsidiary company can't have a parent!");
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public void setNestingLevel(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }

    public void addCompany(Company company) {
        children.add(company);
    }

    @Override
    public String toString() {
        return name;
    }
}
