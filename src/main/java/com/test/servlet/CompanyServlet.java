package com.test.servlet;

import com.test.exception.CompanyException;
import com.test.model.Company;
import com.test.model.SubsidiaryCompany;
import com.test.service.CompanyService;
import com.test.service.CompanyServiceImpl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CompanyServlet extends HttpServlet {

    private CompanyService companyService = new CompanyServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");

        response.setContentType("text/html");

        String name = request.getParameter("companyName");
        String errorMessage = null;
        String earnings = request.getParameter("earnings");

        String parentName = request.getParameter("parent");
        Company retrievedCompany = null;

        try {
            if ("create".equals(action)) {
                companyService.create(createCompany(name, getEarnings(earnings), parentName));
            } else if ("update".equals(action)) {
                companyService.update(createCompany(name, getEarnings(earnings), parentName));
            } else if ("retrieve".equals(action)) {
                retrievedCompany = companyService.retrieve(name);
            } else if ("delete".equals(action)) {
                companyService.delete(name);
            }
        } catch (CompanyException e) {
            errorMessage = e.getMessage();
        }

        String jsp;
        if (retrievedCompany != null) {
            jsp = "/?resultCompanyName=" + retrievedCompany.getName() + "&resultEarnings=" + retrievedCompany.getEarnings()
                    + "&resultParentCompany=" + retrievedCompany.getParentName();
        } else if (errorMessage != null){
            jsp = "/?errorMessage=" + errorMessage;
        } else {
            jsp = "/";
        }

        List<Company> companies = companyService.retrieveAll();
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(jsp);
        request.setAttribute("companies", companies);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Company> companies = companyService.retrieveAll();
        String jsp = "/";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(jsp);
        request.setAttribute("companies", companies);
        dispatcher.forward(request, response);
    }

    private Company createCompany(String name, int earnings, String parentName) {
        if (parentName == null || parentName.isEmpty()) {
            return new Company(name, earnings);
        } else {
            return new SubsidiaryCompany(name, earnings, parentName);
        }
    }

    private int getEarnings(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new CompanyException("Invalid earnings amount");
        }
    }
}
