package com.test.service;

import com.test.exception.CompanyException;
import com.test.model.Company;
import com.test.repository.CompanyRepository;
import com.test.repository.CompanyRepositoryImpl;

import java.util.List;

public class CompanyServiceImpl implements CompanyService {

    private CompanyRepository companyRepository = new CompanyRepositoryImpl();

    @Override
    public void create(Company company) {
        validateName(company.getName());
        companyRepository.create(company);
    }

    @Override
    public Company retrieve(String name) {
        validateName(name);
        return companyRepository.retrieve(name);
    }

    @Override
    public List<Company> retrieveAll() {
        return companyRepository.retrieveAll();
    }

    @Override
    public void update(Company company) {
        validateName(company.getName());
        companyRepository.update(company);
    }

    @Override
    public void delete(String name) {
        validateName(name);
        companyRepository.delete(name);
    }

    private void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new CompanyException("Company name cannot be empty!");
        }
    }
}
