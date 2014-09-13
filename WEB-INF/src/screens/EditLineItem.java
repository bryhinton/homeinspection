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
 * Time: 9:47 AM
 */
public class EditLineItem extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<edit>");

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			String request = req.getParameter("request");

			if("delete".equals(request)) {
				int lineItemID = Utils.parseInt(req.getParameter("lineitemid"), -1);
				int lineItemCount = LineItems.getCountForLineItem(lineItemID);
				boolean shouldForce = "true".equals(req.getParameter("force"));

				if(lineItemCount > 0 && !shouldForce) {
					writer.println("<success>pending</success>");
				}
				else {
					writer.println("<success>true</success>");
					LineItem lineItem = LineItems.getLineItem(lineItemID);

					if(lineItem != null) {
						lineItem.delete();
					}
				}
			}
			else {
				LineItem lineItem;
				boolean isNew = false;

				if(req.getParameter("lineitemid").startsWith("new")) {
					lineItem = new LineItem();
					lineItem.setArea(Utils.parseInt(req.getParameter("areaid"), 0));
					lineItem.setParent(Utils.parseInt(req.getParameter("parentid"), 0));
					isNew = true;

					if(Utils.isEmpty(req.getParameter("name"))) {
						writer.println("<success>false</success>");
						writer.println("</edit>");
						return;
					}
				}
				else {
					int lineItemID = Utils.parseInt(req.getParameter("lineitemid"), 0);
					lineItem = LineItems.getLineItem(lineItemID);
				}

				if(lineItem != null) {
					lineItem.setName(req.getParameter("name"));
					lineItem.setIsActive("checked".equals(req.getParameter("active")));

					try {
						if(isNew) {
							lineItem.insert();
							writer.println("<newid>" + lineItem.getID() + "</newid>");
						}
						else {
							lineItem.update();
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
