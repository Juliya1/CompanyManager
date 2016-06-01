package com.test.model;

import com.test.exception.CompanyException;

import java.util.ArrayList;
import java.util.List;

public class Company {

    protected String name;
    protected int earnings;
    protected Company parent;
    protected String parentName;
    protected List<Company> children = new ArrayList<>();
    protected int nestingLevel;
    protected int childrenEarnings;

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

    public Company getParent() {
        return parent;
    }

    public void setParent(Company parent) {
        throw new CompanyException("Non-subsidiary company can't have a parent!");
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

    public int getChildrenEarnings() {
        return childrenEarnings;
    }

    public void setChildrenEarnings(int childrenEarnings) {
        this.childrenEarnings = childrenEarnings;
    }

    public void addCompany(Company company) {
        children.add(company);
    }

    public void addChildrenEarnings(int earnings) {
        childrenEarnings += earnings;
    }

    @Override
    public String toString() {
        return name;
    }
}
