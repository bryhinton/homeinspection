package screens;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan Hinton
 * Date: 11/13/12
 * Time: 6:42 PM
 */
public class CompanyInfo extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		PrintWriter writer = Utils.getWriter(resp);

		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<areas>");

		Company company = Companies.getCompany(Utils.parseInt(req.getParameter("companyid"), 0));

		if(company != null) {
			int companyID = company.getID();
			int techID = Utils.parseInt(req.getParameter("techid"), 0);

			String key = Login.checkKey(companyID, techID, req.getParameter("key"));

			if(Utils.notEmpty(key)) {
				writer.println("<state>" + (company.getState() != null ? company.getState() : "") + "</state>");
				writer.println("<key>" + key + "</key>");

				List<Area> areas = Areas.getAllForCompany(companyID, false);

				for(Area area : areas) {
					List<LineItem> lineItems = LineItems.getTopLevelLineItems(area.getID(), false);

					if(lineItems.size() > 0) {
						writer.println("<area>");
						writer.println("<id>" + area.getID() + "</id>");
						writer.println("<name>" + Utils.escapeXML(area.getName()) + "</name>");
						writer.println("<lineitems>");

						for(LineItem lineItem : lineItems) {
							writer.println("<lineitem>");
							writer.println("<id>" + lineItem.getID() + "</id>");
							writer.println("<name>" + Utils.escapeXML(lineItem.getName()) + "</name>");
							writer.println("<area>" + area.getID() + "</area>");

							writer.println("<subitems>");

							List<LineItem> subItems = LineItems.getChildren(lineItem.getID(), false);

							for(LineItem subItem : subItems) {
								writer.println("<subitem>");
								writer.println("<id>" + subItem.getID() + "</id>");
								writer.println("<name>" + Utils.escapeXML(subItem.getName()) + "</name>");
								writer.println("<parent>" + subItem.getParent() + "</parent>");
								writer.println("<area>" + area.getID() + "</area>");
								writer.println("</subitem>");
							}

							writer.println("</subitems>");

							writer.println("</lineitem>");
						}

						writer.println("</lineitems>");
						writer.println("</area>");
					}
				}
			}
		}

		writer.println("</areas>");
	}
}
