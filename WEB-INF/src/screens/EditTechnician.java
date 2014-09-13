package screens;

import sqlrow.Technician;
import sqlrow.Technicians;
import sqlrow.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/26/12
 * Time: 8:44 PM
 */
public class EditTechnician extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<edit>");

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			Technician editTechnician;
			boolean isNew = false;

			if("new".equals(req.getParameter("editid"))) {
				editTechnician = new Technician();
				editTechnician.setCompanyID(technician.getCompanyID());
				isNew = true;
			}
			else {
				int editID = Utils.parseInt(req.getParameter("editid"), 0);
				editTechnician = Technicians.getTechnician(editID);
			}

			if(editTechnician != null) {
				editTechnician.setFirstName(req.getParameter("firstname"));
				editTechnician.setLastName(req.getParameter("lastname"));
				editTechnician.setUsername(req.getParameter("username"));
				editTechnician.setPassword(req.getParameter("password"));
				editTechnician.setIsActive(req.getParameter("active").equals("checked"));
				editTechnician.setIsAdmin(req.getParameter("admin").equals("checked"));

				if(isNew) {
					editTechnician.insert();
				}
				else {
					editTechnician.update();
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
