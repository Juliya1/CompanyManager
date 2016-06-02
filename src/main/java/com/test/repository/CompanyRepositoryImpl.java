package com.test.repository;

import com.test.exception.CompanyException;
import com.test.model.Company;
import com.test.model.SubsidiaryCompany;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompanyRepositoryImpl implements CompanyRepository {

    private static final String INSERT_COMPANY = "INSERT INTO companies (name, earnings, parent, nesting_level) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_COMPANIES = "SELECT * FROM companies";
    private static final String SELECT_COMPANY = "SELECT * FROM companies WHERE name = ?";
    private static final String UPDATE_COMPANY = "UPDATE companies SET earnings = ? WHERE name = ?";

    Connection connection;

    public CompanyRepositoryImpl() {
        try {
            Class.forName("org.postgresql.Driver");
            String dbUrl = System.getenv("JDBC_DATABASE_URL");
            connection = DriverManager.getConnection(dbUrl);
            if (connection == null) {
                throw new SQLException("No connection to DB");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new CompanyException("Internal server error");
        }
    }

    @Override
    public void create(Company company) {
        try (PreparedStatement companyInsertStatement = connection.prepareStatement(INSERT_COMPANY);
             PreparedStatement companySelectStatement = connection.prepareStatement(SELECT_COMPANY)) {
            companySelectStatement.setString(1, company.getName());
            if (companySelectStatement.executeQuery().isBeforeFirst()) {
                throw new CompanyException("Company with this name already exists!");
            }

            companyInsertStatement.setString(1, company.getName());
            companyInsertStatement.setInt(2, company.getEarnings());
            String parent = null;
            int nestingLevel = 0;
            if (company.getParentName() != null) {
                parent = company.getParentName();
                if (parent.equals(company.getName())) {
                    throw new CompanyException("Invalid parent - same as provided company");
                }
                companySelectStatement.setString(1, parent);
                ResultSet rs = companySelectStatement.executeQuery();
                if (!rs.isBeforeFirst()) {
                    throw new CompanyException("Invalid parent - parent not found");
                }
                rs.next();
                nestingLevel = rs.getInt("nesting_level");
                companyInsertStatement.setInt(4, nestingLevel + 1);
            } else {
                companyInsertStatement.setInt(4, nestingLevel);
            }
            companyInsertStatement.setString(3, parent);

            companyInsertStatement.execute();
        } catch (SQLException e) {
            throw new CompanyException("SQL error");
        }
    }

    @Override
    public Company retrieve(String name) {
        ResultSet rs;
        try (PreparedStatement companySelectStatement = connection.prepareStatement(SELECT_COMPANY)) {
            companySelectStatement.setString(1, name);
            rs = companySelectStatement.executeQuery();
            if (!rs.isBeforeFirst()) {
                throw new CompanyException("Company with this name not found");
            }
            rs.next();
            String companyName = rs.getString("name");
            int earnings = rs.getInt("earnings");
            String parent = rs.getString("parent");

            if (parent == null) {
                return new Company(companyName, earnings);
            } else {
                return new SubsidiaryCompany(companyName, earnings, parent);
            }

        } catch (SQLException e) {
            throw new CompanyException("SQL error");
        }

    }

    @Override
    public List<Company> retrieveAll() {
        List<Company> companies = new ArrayList<>();
        try (PreparedStatement retrieveAllStatement = connection.prepareStatement(SELECT_ALL_COMPANIES)) {

            ResultSet rs = retrieveAllStatement.executeQuery();
            while (rs.next()) {
                String parent = rs.getString("parent");
                if (parent == null){
                    Company c = new Company(rs.getString("name"), rs.getInt("earnings"));
                    c.setNestingLevel(rs.getInt("nesting_level"));
                    companies.add(c);
                } else {
                    Company c = new SubsidiaryCompany(rs.getString("name"), rs.getInt("earnings"), parent);
                    c.setNestingLevel(rs.getInt("nesting_level"));
                    companies.add(c);
                }
            }
        } catch (SQLException e) {
            throw new CompanyException("SQL error");
        }

        return createTree(companies);
    }

    private List<Company> createTree(List<Company> companies) {
        List<Company> subsidiaryCompanies
                = companies.stream().filter(e -> e.getParentName() != null).collect(Collectors.toList());
        companies.removeAll(subsidiaryCompanies);

        return sumEarnings(createTree(companies, companies, subsidiaryCompanies));
    }

    private List<Company> createTree(final List<Company> mainCompaniesImmutable,
                                     List<Company> mainCompanies, List<Company> subsidiaryCompanies) {

        List<Company> nextMainCompanies = new ArrayList<>();
        for (Company c : mainCompanies) {
            List<Company> toRemove = new ArrayList<>();
            for (Company s : subsidiaryCompanies) {
                if (s.getParentName().equals(c.getName())) {
                    c.addCompany(s);
                    toRemove.add(s);
                    nextMainCompanies.add(s);
                }
            }
            subsidiaryCompanies.removeAll(toRemove);
        }

        return subsidiaryCompanies.isEmpty() ? mainCompaniesImmutable :
                createTree(mainCompaniesImmutable, nextMainCompanies, subsidiaryCompanies);
    }

    private List<Company> sumEarnings(List<Company> companies) {
        companies.forEach(c -> sumEarnings(c, c.getChildren()));
        companies.forEach(c -> sumEarnings(c.getChildren()));
        return companies;
    }

    private void sumEarnings(Company company, List<Company> companies) {
        int sum = companies.stream().mapToInt(Company::getEarnings).sum();
        company.addChildrenEarnings(sum);
        companies.forEach(c -> sumEarnings(company, c.getChildren()));
    }


    private List<Company> getChildrenList(List<Company> companies, List<Company> result) {

        companies.forEach(result::add);

        List<Company> companies1 = new ArrayList<>();
        companies.forEach(c -> companies1.addAll(c.getChildren()));

        if (companies1.isEmpty()) {
            return result;
        }

        return getChildrenList(companies1, result);
    }

    private Company getCompanyWithChildren(String name , List<Company> companies) {
        for (Company c : companies) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        List<Company> result = new ArrayList<>();
        companies.forEach(c -> result.addAll(c.getChildren()));
        return getCompanyWithChildren(name, result);
    }

    @Override
    public void update(Company company) {
        try (PreparedStatement companyStatement = connection.prepareStatement(UPDATE_COMPANY);
             PreparedStatement companySelectStatement = connection.prepareStatement(SELECT_COMPANY)) {
            companySelectStatement.setString(1, company.getName());
            if (!companySelectStatement.executeQuery().isBeforeFirst()) {
                throw new CompanyException("Company with this name not found");
            }
            companyStatement.setInt(1, company.getEarnings());
            companyStatement.setString(2, company.getName());
            companyStatement.execute();
        } catch (SQLException e) {
            throw new CompanyException("SQL error");
        }
    }

    @Override
    public void delete(String name) {
        try (Statement deleteCompanyStatement = connection.createStatement();
             PreparedStatement companySelectStatement = connection.prepareStatement(SELECT_COMPANY)) {
            companySelectStatement.setString(1, name);
            if (!companySelectStatement.executeQuery().isBeforeFirst()) {
                throw new CompanyException("Company with this name not found");
            }
            String where = createWhereForDelete(getCompanyWithChildren(name, retrieveAll()));
            String sql = "DELETE FROM companies where name =" + where;
            deleteCompanyStatement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CompanyException("SQL error");
        }
    }

    private String createWhereForDelete(Company company) {
        StringBuilder sb = new StringBuilder();
        sb.append("'");
        sb.append(company.getName());
        sb.append("'");
        getChildrenList(company.getChildren(), new ArrayList<>()).forEach(c -> {
            sb.append(" OR name=");
            sb.append("'");
            sb.append(c.getName());
            sb.append("'");
        });
        return sb.toString();
    }
}
