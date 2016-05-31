package com.test.repository;

import com.test.model.Company;

import java.util.List;

public interface CompanyRepository {

    void create(Company company);
    Company retrieve(String name);
    List<Company> retrieveAll();
    void update(Company company);
    void delete(String name);

}
