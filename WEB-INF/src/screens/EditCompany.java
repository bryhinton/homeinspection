package screens;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/27/12
 * Time: 7:38 AM
 */
public class EditCompany extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<edit>");

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			Company company;
			boolean isNew = false;

			if(req.getParameter("companyid").startsWith("new") && technician.isSystemAdmin()) {
				company = new Company();
				company.setName("New Company");

				isNew = true;
			}
			else {
				int companyID = Utils.parseInt(req.getParameter("companyid"), 0);
				company = Companies.getCompany(companyID);
			}

			if(company != null) {
				company.setName(req.getParameter("name"));

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

				try {
					Date expiration = dateFormat.parse(req.getParameter("trial"));
					company.setTrialExpiration(new Timestamp(expiration.getTime()));
				}
				catch(Exception exception) {
					company.setTrialExpiration(null);
				}

				String email = req.getParameter("email");
				String phone = req.getParameter("phone");

				if(Utils.notEmpty(email)) {
					company.setEmail(email);
				}

				if(Utils.notEmpty(phone)) {
					company.setPhone(phone);
				}

				company.setSendQuote("true".equals(req.getParameter("sendquote")));

				if(technician.isSystemAdmin()) {
					company.setIsActive("checked".equals(req.getParameter("active")));
				}

				if(isNew) {
					company.insert();
					int companyID = company.getID();

					CompanyApp companyApp = new CompanyApp();
					companyApp.setCompanyID(companyID);
					companyApp.setApp(CompanyApps.INSPECTION);
					companyApp.insert();

					Technician newAdmin = new Technician();
					newAdmin.setFirstName(req.getParameter("admin-firstname"));
					newAdmin.setLastName(req.getParameter("admin-lastname"));
					newAdmin.setUsername(req.getParameter("admin-username"));
					newAdmin.setPassword(req.getParameter("admin-password"));
					newAdmin.setCompanyID(companyID);
					newAdmin.setIsActive(true);
					newAdmin.setIsAdmin(true);
					newAdmin.insert();

					int newDevices = Utils.parseInt(req.getParameter("new-devices"), 0);

					for(int i = 0; i < newDevices; i++) {
						new CompanyLicense(companyID).insert();
					}

					int seedID = Utils.parseInt(req.getParameter("seed"), -1);
					Company seed = Companies.getCompany(seedID);

					if(seed != null) {
						List<Area> areas = Areas.getAllForCompany(seedID, false);

						for(Area area : areas) {
							int areaID = area.getID();

							Area newArea = new Area();
							newArea.setIsActive(true);
							newArea.setCompanyID(companyID);
							newArea.setName(area.getName());
							newArea.insert();

							List<LineItem> parents = LineItems.getTopLevelLineItems(areaID, false);

							for(LineItem parent : parents) {
								LineItem newParent = new LineItem();
								newParent.setIsActive(true);
								newParent.setArea(newArea.getID());
								newParent.setName(parent.getName());
								newParent.setParent(0);
								newParent.insert();

								List<LineItem> lineItems = LineItems.getChildren(parent.getID(), false);

								for(LineItem lineItem : lineItems) {
									LineItem newLineItem = new LineItem();
									newLineItem.setArea(areaID);
									newLineItem.setIsActive(true);
									newLineItem.setName(lineItem.getName());
									newLineItem.setParent(newParent.getID());
									newLineItem.insert();
								}
							}
						}
					}

					writer.println("<newid>" + company.getID() + "</newid>");
				}
				else {
					company.update();
				}

				writer.println("<success>true</success>");
			}
			else {
				writer.println("<success>false</success>");
			}
		}
		else {
			writer.println("<success>false</success>");
		}

		writer.println("</edit>");
	}
}
