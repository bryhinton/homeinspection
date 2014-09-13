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
 * Date: 12/27/12
 * Time: 9:46 AM
 */
public class EditArea extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<edit>");

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			String request = req.getParameter("request");
			String areaIDString = req.getParameter("areaid");

			if("delete".equals(request)) {
				int areaID = Utils.parseInt(areaIDString, -1);
				int areaCount = InspectionAreas.getCountForArea(areaID);
				boolean shouldForce = "true".equals(req.getParameter("force"));

				if(areaCount > 0 && !shouldForce) {
					writer.println("<success>pending</success>");
				}
				else {
					writer.println("<success>true</success>");
					Area area = Areas.getArea(areaID);

					if(area != null) {
						area.delete();
					}
				}
			}
			else {
				Area area;
				boolean isNew = false;

				if(areaIDString.startsWith("new")) {
					area = new Area();
					area.setCompanyID(technician.getCompanyID());
					isNew = true;
				}
				else {
					int areaID = Utils.parseInt(areaIDString, 0);
					area = Areas.getArea(areaID);
				}

				if(area != null) {
					area.setName(req.getParameter("name"));
					area.setIsActive("checked".equals(req.getParameter("active")));

					try {
						if(isNew) {
							area.insert();
							writer.println("<newid>" + area.getID() + "</newid>");
						}
						else {
							area.update();
						}

						writer.println("<success>true</success>");
					}
					catch(Exception e) {
						e.printStackTrace();
						writer.println("<success>false</success>");
					}
				}
				else {
					writer.println("<success>false</success>");
				}
			}
		}
		else {
			writer.println("<success>false</success>");
		}

		writer.println("</edit>");
	}
}
