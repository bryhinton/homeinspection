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
 * Date: 6/27/13
 * Time: 9:13 PM
 */
public class EditField extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<edit>");

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			Field field;
			boolean isNew = false;

			if("new".equals(req.getParameter("editid"))) {
				field = new Field();
				field.setCompanyID(technician.getCompanyID());
				isNew = true;
			}
			else {
				int editID = Utils.parseInt(req.getParameter("editid"), 0);
				field = Fields.getField(editID);
			}

			if(field != null) {
				field.setName(req.getParameter("name"));
				field.setType(req.getParameter("type"));
				field.setRequired(req.getParameter("required").equals("checked"));
				field.setEnabled(req.getParameter("enabled").equals("checked"));
				field.setSum(req.getParameter("sum").equals("checked"));

				if(isNew) {
					field.insert();
				}
				else {
					field.update();
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
