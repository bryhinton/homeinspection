package screens;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/29/12
 * Time: 9:02 AM
 */
public class Admin extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doGet(req, rsp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		if(req.getPathInfo() != null && req.getPathInfo().equals("/logout")) {
			Cookie cookie = new Cookie("techid", "");
			cookie.setPath("/");
			rsp.addCookie(cookie);
			rsp.sendRedirect("../admin-login.jsp");
		}

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isSystemAdmin()) {
			PrintWriter writer = Utils.getWriter(rsp);

			ScreenUtil.writeAdminHeader(req, writer, technician);

			//******************
			// Dialogs
			writer.println("<div class='dialog medium' id='add-company-dialog'>");
			writer.println("<div class='header green-gradient'>Add Company</div>");
			writer.println("<div class='dialog-content'>");

			writer.println("<div class='line-wrapper'>");
			writer.println("<label for='new-company-name'>Name:</label>");
			writer.println("<input type='text' name='new-company-name' id='new-company-name'>");
			writer.println("</div>");

			writer.println("<div class='line-wrapper'>");
			writer.println("<label for='company-seed'>Type:</label>");
			writer.println("<select id='company-seed'>");

			for(Map.Entry<Integer, String> defaultCompany : Companies.getDefaultCompanies().entrySet()) {
				writer.println("<option value='" + defaultCompany.getKey() + "'>" + defaultCompany.getValue() + "</option>");
			}

			writer.println("</select><br>");
			writer.println("</div>");

			writer.println("<div class='line-wrapper'>");
			writer.println("<label for='admin-technician'>Admin:</label>");
			writer.println("<div id='admin-technician' class='input-group'>");
			writer.println("<input type='text' id='admin-firstname' placeholder='First Name'>");
			writer.println("<input type='text' id='admin-lastname' placeholder='Last Name'><br>");
			writer.println("<input type='text' id='admin-username' placeholder='Username'>");
			writer.println("<input type='text' id='admin-password' placeholder='Password'>");
			writer.println("</div>");
			writer.println("</div>");

			writer.println("<div class='line-wrapper'>");
			writer.println("<label for='new-devices'>Devices:</label>");
			writer.println("<input type='text' id='new-devices' name='new-devices'>");
			writer.println("</div>");

			writer.println("<div class='buttons'>");
			writer.println("<button type='button' onclick='editCompanyFull(\"new\");'>Save</button>");
			writer.println("<button type='button' class='cancel' onclick='hideDialog();'>Cancel</button>");
			writer.println("</div>");

			writer.println("</div>");
			writer.println("</div>");

			//******************
			// Companies
			writer.println("<div class='section' id='companies'>");
			writer.println("<div class='header'>Companies</div>");

			for(Company company : Companies.getAll()) {
				int companyID = company.getID();
				writer.print("<div class='company row' onclick='toggleHiddenList(\"company-info-" + companyID + "\");'>");
				writer.print("<div class='cell name' id='company-name-text-" + companyID + "')>" + company.getName() + "</div>");
				writer.print("<div class='cell isactive " + (company.isActive() ? "active" : "inactive") + "' id='company-active-text-" + companyID + "'>" + (company.isActive() ? "Active" : "Inactive") + "</div>");
				writer.println("</div>");

				writer.println("<div class='company-info section' id='company-info-" + companyID + "'>");
				writer.print("<div id='company-" + companyID + "'><label>Company Name:</label>");
				writer.print("<input type='text' id='company-name-" + companyID + "' value='" + company.getName() + "'>");
				writer.print("<label>Active:</label><input type='checkbox' id='company-active-" + companyID + "'" + (company.isActive() ? " checked=checked": "") + ">");
				writer.println("<button type='button' class='company-save-button' onclick='editCompanyFull(\"" + companyID + "\");'>Save</button>");

				String dateString = "";

				if(company.getTrialExpiration() != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					dateString = dateFormat.format(company.getTrialExpiration());
				}

				writer.print("<div><label>Trial Expiration:</label><input type='date' id='company-trial-" + companyID + "' value='" + dateString + "'></div>");
				writer.println("</div>");

				writer.println("<div class='subheader'>Devices</div>");

				List<CompanyLicense> licenses = CompanyLicenses.getAllForCompany(company.getID());

				int i = 1;
				for(CompanyLicense license : licenses) {
					writer.println("<div class='license row no-hover' id='license-" + license.getID() + "')>");
					writer.print("<div class='cell number'>" + i + ".</div>");

					Technician lastLoginTech = Technicians.getTechnician(license.getLastTech());

					if(lastLoginTech != null) {
						writer.print("<div class='cell techname'>" + lastLoginTech.getFirstName() + " " + lastLoginTech.getLastName() + "</div>");
					}
					else {
						writer.print("<div class='cell unused'>Unused</div>");
					}

					writer.print("<button type='button' onclick='resetLicense(\"" + license.getID() + "\");'>Reset</button>");
					writer.print("<button type='button' onclick='deleteLicense(\"" + license.getID() + "\");'>Delete</button>");

					writer.println("</div>");

					i++;
				}

				writer.println("<div class='license' id='license-new-" + company.getID() + "'>");
				writer.println("<button type='button' onclick='addLicense(\"" + company.getID() + "\")' id='add-license-" + company.getID() + "'>Add Device...</button>");
				writer.println("</div>");

				writer.println("</div>");
			}

			writer.println("<div class='company' id='company-new'>");
			writer.println("<button type='button' onclick='openDialog(\"add-company-dialog\")' id='add-company'>Add Company...</button>");
			writer.println("</div>");

			writer.println("</div>");


			writer.println("</div>");

			ScreenUtil.writerFooter(writer);
		}
		else {
			rsp.sendRedirect("admin-login.jsp");
		}
	}
}
