package screens;

import com.sun.deploy.association.utility.AppAssociationReader;
import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/20/12
 * Time: 9:04 PM
 */
public class InspectionInfo extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doGet(req, rsp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			PrintWriter writer = Utils.getWriter(rsp);
			int inspectionID = Utils.parseInt(req.getParameter("inspectionid"), 0);

			Inspection inspection = Inspections.getInspection(inspectionID);

			if(inspection != null) {
				writer.println("<?xml version='1.0' encoding='UTF-8'?>");
				writer.println("<inspectioninfo>");

				inspection.writeXML(writer);

				double standardTotal = 0.0;
				double memberTotal = 0.0;
				DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");
				List<Area> areas = Areas.getAllForCompany(technician.getCompanyID(), false);

				for(Area area : areas) {
					writer.println("<area>");

					writer.println("<id>" + area.getID() + "</id>");
					writer.println("<name>" + area.getName() + "</name>");

					List<LineItem> parents = LineItems.getTopLevelLineItems(area.getID(), false);

					for(LineItem parent : parents) {
						writer.println("<parent>");

						writer.println("<id>" + parent.getID() + "</id>");
						writer.println("<name>" + parent.getName() + "</name>");

						List<InspectionAreaLineItem> failingLineItems = InspectionAreaLineItems.getFailingLineItemsForParent(inspectionID, parent.getID());

						if(failingLineItems.size() > 0) {
							for(InspectionAreaLineItem failingItem : failingLineItems) {
								LineItem lineItem = LineItems.getLineItem(failingItem.getLineItemID());

								writer.println("<lineitem>");

								writer.println("<name>" + lineItem.getName() + "</name>");
								writer.println("<comment>" + failingItem.getComment() + "</comment>");

								for(QuoteItem quoteItem : QuoteItems.getAllForInspectionAreaLineItem(failingItem.getID())) {
									Task task = Tasks.getByID(quoteItem.getTaskID());
									double standard = quoteItem.isAddOn() ? task.getStandardAddOn() : task.getStandard();
									double member = quoteItem.isAddOn() ? task.getMemberAddOn() : task.getMember();

									standardTotal += standard;
									memberTotal += member;

									writer.println("<quoteitem>");
									writer.println("<name>" + Utils.escapeXML(task.getName()) + "</name>");
									writer.println("<description>" + Utils.escapeXML(task.getDescription()) + "</description>");
									writer.println("<addon>" + quoteItem.isAddOn() + "</addon>");
									writer.println("<active>" + quoteItem.isActive() + "</active>");

									task.writeXML(writer);

									writer.println("</quoteitem>");
								}

								writer.println("</lineitem>");
							}
						}

						writer.println("</parent>");
					}

					writer.println("</area>");
				}

				writer.println("<standardtotal>$" + decimalFormat.format(standardTotal) + "</standardtotal>");
				writer.println("<membertotal>$" + decimalFormat.format(memberTotal) + "</membertotal>");
				writer.println("<yoursavedtotal>$" + decimalFormat.format(standardTotal - memberTotal) + "</yoursavedtotal>");
				writer.println("</inspectioninfo>");
			}
		}
	}
}
