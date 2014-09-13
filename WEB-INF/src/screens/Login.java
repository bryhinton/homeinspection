package screens;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 11/24/12
 * Time: 9:22 AM
 */
public class Login extends HttpServlet {

	private static final String possibleKeyCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		PrintWriter writer = Utils.getWriter(resp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<login>");

		Technician technician = Technicians.login(username, password);

		if(technician != null) {
			String key = Login.checkKey(technician.getCompanyID(), technician.getID(), req.getParameter("key"));

			if(Utils.notEmpty(key)) {
				writer.println("<successful>true</successful>");
				writer.println("<companyid>" + technician.getCompanyID() + "</companyid>");
				writer.println("<techid>" + technician.getID() + "</techid>");
				writer.println("<techfirstname>" + technician.getFirstName() + "</techfirstname>");
				writer.println("<key>" + key + "</key>");
			}
			else {
				writer.println("<successful>false</successful>");
			}
		}
		else {
			writer.println("<successful>false</successful>");
		}


		writer.println("</login>");
	}

	private static String generateKey() {
		StringBuilder newKey = new StringBuilder();

		for(int i = 0; i < 40; i++) {
			newKey.append(possibleKeyCharacters.charAt(((Double) Math.floor(Math.random() * possibleKeyCharacters.length())).intValue()));
		}

		return newKey.toString();
	}

	public static String checkKey(int companyID, int techID, String key) {
//		Company company = Companies.getCompany(companyID);
//
//		if(company != null && company.hasAccess()) {
//			//CompanyLicense companyLicense = null;
//
//			if(Utils.notEmpty(key)) {
//				return generateKey();
//				//companyLicense = CompanyLicenses.getByKey(companyID, key);
//			}
//
//			CompanyLicense companyLicense = CompanyLicenses.getNewLicenseForCompany(companyID);
//
//			if(companyLicense != null) {
//				companyLicense.setKey(generateKey());
//				companyLicense.setLastTech(techID);
//				companyLicense.update();
//
//				return companyLicense.getKey();
//			}
//		}
//
//		return null;
		return "abc";
	}
}
