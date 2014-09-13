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
 * Date: 12/29/12
 * Time: 9:33 AM
 */
public class EditCompanyLicense extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<edit>");

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isSystemAdmin()) {
			CompanyLicense license;
			boolean isNew = false;

			if(req.getParameter("licenseid").startsWith("new")) {
				license = new CompanyLicense(Utils.parseInt(req.getParameter("companyid"), 0));
				isNew = true;
			}
			else {
				license = CompanyLicenses.getByID(Utils.parseInt(req.getParameter("licenseid"), 0));

				if(license != null) {
					String action = req.getParameter("action");

					if("reset".equals(action)) {
						license.setKey("");
						license.setLastTech(0);
					}
					else if("delete".equals(action)) {
						license.delete();
						writer.println("<success>true</success>");
						writer.println("</edit>");
						return;
					}
				}
			}

			if(license != null) {
				if(isNew) {
					license.insert();
					writer.println("<newid>" + license.getID() + "</newid>");
				}
				else {
					license.update();
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
