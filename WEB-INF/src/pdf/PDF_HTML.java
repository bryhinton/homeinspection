package pdf;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 4/16/13
 * Time: 7:50 PM
 */
public class PDF_HTML extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		System.out.println("Trying to serve PDF HTML");
		PrintWriter writer = Utils.getWriter(rsp);

		//if("inspectionResult".equals(req.getParameter("request"))) {
			Inspection inspection = Inspections.getInspection(Utils.parseInt(req.getParameter("inspectionID"), -1));

			if(inspection != null) {
				String emailAddress = inspection.getEmail();

				if(emailAddress != null) {
					writer.println("<!DOCTYPE html>");
					writer.println("<html><head><title>Home Safety Inspection Result</title></head>");
					//writer.println("<link rel='stylesheet' href='stylesheets/pdfemail.css'>");
					writer.println("<body>");

					Company company = Companies.getCompany(inspection.getCompany());
					Technician technician = Technicians.getTechnician(inspection.getTechID());
					double standardTotal = 0.0;
					double memberTotal = 0.0;
					DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");

					writer.println("<div style='height:110px; padding-top:40px;'>");

					String fullURL = req.getRequestURL().toString();
					fullURL = fullURL.substring(0, fullURL.lastIndexOf("/"));
					fullURL = fullURL.substring(0, fullURL.lastIndexOf("/"));
					writer.println("<img src='C:/images/" + company.getID() + "/" + company.getLogo() + "' style='position:absolute; height:150px; top:10px; z-index:-1;'>");
					writer.println("<div style='text-align:right; font-size:24px; font-weight:bold;'>" + company.getName() + "</div>");
					writer.println("<div style='text-align:right;'>" + company.getPhone() + "</div>");
					writer.println("<div style='text-align:right;'>" + company.getEmail() + "</div>");

					writer.println("<div style='position:relative; top:40px; '>");
					writer.println("<div style='text-align:right; font-size:24px;'>CUSTOMER:</div>");
					writer.println("<div style='text-align:right; font-size:20px; font-weight:bold;'>" + inspection.getFirstName() + " " + inspection.getLastName() + "</div>");
					writer.println("<div style='text-align:right; font-size:18px;'>" + inspection.getPhone() + "</div>");
					writer.println("</div>");

					writer.println("</div>");
					writer.println("<div style='text-align:center; font-size:40px;'>Your Home Inspection Results</div>");

					List<InspectionArea> inspectionAreas = InspectionAreas.getAllForInspection(inspection.getID());

					for(InspectionArea inspectionArea : inspectionAreas) {
						int areaID = inspectionArea.getAreaID();
						Area area = Areas.getArea(areaID);

						writer.println("<div style='height:20px'></div>");
						writer.println("<div style='font-size:26px;'><b>" + inspectionArea.getName() + "</b></div>");
						List<LineItem> parents = LineItems.getTopLevelLineItems(areaID, false);

						for(LineItem parent : parents) {
							writer.println("<div style='font-size:24px;'>" + parent.getName() + "</div>");
							writer.println("<table style='padding-bottom:40px; width:100%; table-layout:fixed;'>");

							List<InspectionAreaLineItem> inspectionAreaLineItems = InspectionAreaLineItems.getAllForTopLevelLineItem(inspectionArea.getID(), parent.getID());

							for(InspectionAreaLineItem inspectionAreaLineItem : inspectionAreaLineItems) {
								InspectionResult result = InspectionResult.getByValue(inspectionAreaLineItem.getResult());

								if(result != null) {
									boolean failed = result.getValue().equals("fail");
									writer.println("<tr>");
									LineItem lineItem = LineItems.getLineItem(inspectionAreaLineItem.getLineItemID());

									writer.println("<td style='width:77%; font-size:20px; padding:0px 3%; border-bottom:1px dotted black'>" + lineItem.getName() + "</td>");

									writer.println("<td style='width:20%; color:white; font-size:20px; text-align:center; background-color:#" + result.getColor() + ";'>" + result.getLabel() + "</td>");

									String comment = inspectionAreaLineItem.getComment();
									if(Utils.notEmpty(comment) && !comment.equals("null")) {
										writer.println("<tr><td colspan='2' style='font-size:16px; padding-left:10px; padding-left:5%;'>" + comment + "</td></tr>");
									}
									else {
										writer.println("<td></td>");
									}

									writer.println("</tr>");

		//							if(failed) {
		//								for(QuoteItem quoteItem : QuoteItems.getAllForInspectionAreaLineItem(inspectionAreaLineItem.getID())) {
		//									if(quoteItem.isActive()) {
		//										Task task = Tasks.getByID(quoteItem.getTaskID());
		//										double standard = quoteItem.isAddOn() ? task.getStandardAddOn() : task.getStandard();
		//										double member = quoteItem.isAddOn() ? task.getMemberAddOn() : task.getMember();
		//
		//										if(task != null) {
		//											writer.println("<tr><td colspan='2' style='font-size:16px; padding-left:10%;'>" + task.getName() + "</td></tr>");
		//											writer.println("<tr><td colspan='2' style='font-size:14px; color:#999999; padding-left:10%;'>" + task.getDescription() + "</td></tr>");
		//											writer.println("<tr><td colspan='2' style='font-size:16px; padding-left:10px; padding-bottom:30px;'><b>STANDARD:</b> $" + decimalFormat.format(standard) + "<br>");
		//											writer.println("<b>MEMBER:</b> $" + decimalFormat.format(member) + "<br>");
		//											writer.println("<b>YOUR SAVED:</b> $" + decimalFormat.format(standard - member) + "</td></tr>");
		//										}
		//									}
		//								}
		//							}
								}
							}

							List<CustomLineItem> customLineItems = CustomLineItems.getAllForTopLevelLineItem(inspectionArea.getID(), parent.getID());

							for(CustomLineItem customLineItem : customLineItems) {
								InspectionResult result = InspectionResult.getByValue(customLineItem.getResult());

								if(result != null) {
									boolean failed = result.getValue().equals("fail");
									writer.println("<tr>");

									writer.println("<td style='width:350px; font-size:20px; padding:0px 20px;'>" + customLineItem.getName() + "</td>");

									writer.println("<td style='width:100px; padding-right:10px; color:white; font-size:20px; text-align:center; background-color:#" + result.getColor() + ";'>" + result.getLabel() + "</td>");

									String comment = customLineItem.getComment();

									if(Utils.notEmpty(comment)) {
										writer.println("<td style='font-size:18px; padding-left:10px;'>" + comment + "</td>");
									}
									else {
										writer.println("<td></td>");
									}

									writer.println("</tr>");

		//							if(failed) {
		//								for(QuoteItem quoteItem : QuoteItems.getAllForInspectionAreaLineItem(customLineItem.getID())) {
		//									if(quoteItem.isActive()) {
		//										Task task = Tasks.getByID(quoteItem.getTaskID());
		//										double standard = quoteItem.isAddOn() ? task.getStandardAddOn() : task.getStandard();
		//										double member = quoteItem.isAddOn() ? task.getMemberAddOn() : task.getMember();
		//
		//										if(task != null) {
		//											writer.println("<tr><td colspan='2' style='font-size:16px; padding-left:40px;'>" + task.getName() + "</td></tr>");
		//											writer.println("<tr><td colspan='2' style='font-size:14px; color:#999999; padding-left:40px;'>" + task.getDescription() + "</td></tr>");
		//											writer.println("<tr><td colspan='2' style='font-size:16px; padding-left:10px; padding-bottom:30px;'><b>STANDARD:</b> $" + decimalFormat.format(standard) + "<br>");
		//											writer.println("<b>MEMBER:</b> $" + decimalFormat.format(member) + "<br>");
		//											writer.println("<b>YOUR SAVED:</b> $" + decimalFormat.format(standard - member) + "</td></tr>");
		//										}
		//									}
		//								}
		//							}
								}
							}

							writer.println("</table>");
						}
					}

					List<QuoteItem> quoteItems = QuoteItems.getAllForInspection(inspection.getID());
					if(company.shouldSendQuote() && inspection.shouldSendQuote() && quoteItems.size() > 0) {
						writer.println("<div style='font-size:32px; padding-top:40px; border-top:2px solid #666666;'><b>Quote</b></div>");

						Area currentArea = null;
						LineItem currentLineItem = null;

						writer.println("<table style='border-collapse:collapse; margin-bottom:50px;'>");
						boolean haveDrawnHeader = false;

						for(QuoteItem quoteItem : quoteItems) {
							if(quoteItem.isActive()) {
								InspectionAreaLineItem inspectionAreaLineItem = InspectionAreaLineItems.getByID(quoteItem.getInspectionAreaLineItemID());
								Area area = Areas.getAreaFromInspectionArea(inspectionAreaLineItem.getInspectionArea());
								Task task = Tasks.getByID(quoteItem.getTaskID());
								double standard = quoteItem.isAddOn() ? task.getStandardAddOn() : task.getStandard();
								double member = quoteItem.isAddOn() ? task.getMemberAddOn() : task.getMember();

								standardTotal += standard;
								memberTotal += member;

								if(area != null) {
									if(currentArea == null || currentArea.getID() != area.getID()) {
										currentArea = area;

										writer.println("<tr><td style='font-size:26px'>" + currentArea.getName() + "</td>");

										if(!haveDrawnHeader) {
											writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#0C68B3; border-bottom:1px solid white; border-top:1px solid white;'>Standard</td>");
											writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#BDB21C; border-bottom:1px solid white; border-top:1px solid white;'>Member</td>");
											writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#189413; border-bottom:1px solid white; border-top:1px solid white;'>You Saved</td>");
											haveDrawnHeader = true;
										}
										else {
											writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#0C68B3;'</td>");
											writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#BDB21C;'></td>");
											writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#189413;'></td>");
										}

										writer.println("</tr>");

									}

									if(currentLineItem == null || currentLineItem.getID() != inspectionAreaLineItem.getLineItemID()) {
										currentLineItem = LineItems.getLineItem(inspectionAreaLineItem.getLineItemID());
										writer.println("<tr><td style='font-size:22px; padding-left:20px;'>" + currentLineItem.getName() + "</td>");
										writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#0C68B3'></td>");
										writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#BDB21C'></td>");
										writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#189413'></td></tr>");
									}

									writer.println("<tr><td style='width:400px; font-size:16px; padding:0px 30px 0px 40px;'>" + task.getName() + "</td>");
									writer.println("<td rowspan='2' style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#0C68B3; vertical-align:top;'>$" + decimalFormat.format(standard) + "</td>");
									writer.println("<td rowspan='2'style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#BDB21C; vertical-align:top;'>$" + decimalFormat.format(member) + "</td>");
									writer.println("<td rowspan='2'style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#189413; vertical-align:top;'>$" + decimalFormat.format(standard - member) + "</td>");
									writer.println("</tr>");

									writer.println("<tr><td style='width:400px; font-size:14px; color:#999999; padding:0px 30px 0px 40px;'>" + task.getDescription() + "</td><td colspan='3'></td></tr>");

								}
							}
						}

						writer.println("<td></td>");
						writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#0C68B3; border-top:1px solid white;'>$" + decimalFormat.format(standardTotal) + "</td>");
						writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#BDB21C; border-top:1px solid white;'>$" + decimalFormat.format(memberTotal) + "</td>");
						writer.println("<td style='font-size:22px; text-align:center; width:125px; padding:0px 10px; color:white; background-color:#189413; border-top:1px solid white;'>$" + decimalFormat.format(standardTotal - memberTotal) + "</td>");
						writer.println("</tr>");

						writer.println("</table>");
					}

					writer.println("<div style='font-size:16px; padding:10px 0px 50px 0px; border-top:2px solid #666666;'>For questions about your inspection results, or to schedule service for failed items please call " + company.getPhone() + ".<br>Your technician was " + technician.getFirstName() + ".</div>");

					writer.println("</body></html>");
				}
			}
		//}
	}
}
