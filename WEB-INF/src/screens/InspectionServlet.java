package screens;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.deploy.net.HttpRequest;
import com.sun.jmx.snmp.tasks.TaskServer;
import email.Email;
import pdf.GeneratePDF;
import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/4/12
 * Time: 6:16 PM
 */
public class InspectionServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);
		int requestID = (int) (Math.random() * 1000);
		long startTime = System.currentTimeMillis();

		System.out.println("Starting request: " + requestID);
		writer.println("<?xml version='1.0' encoding='UTF-8'?>");
		writer.println("<result>");
		int companyID = Utils.parseInt(req.getParameter("inspection[company]"), 0);
		String techID = req.getParameter("inspection[tech]");
		String lastName = req.getParameter("inspection[lastname]");
		String key = Login.checkKey(companyID, Utils.parseInt(techID, 0), req.getParameter("key"));
		String request = req.getParameter("request");

		if(Utils.notEmpty(key) && Utils.isEmpty(request)) {
			try {
				if(companyID > 0 && Utils.parseInt(techID, 0) > 0 && Utils.notEmpty(lastName)) {
					Inspection inspection = new Inspection();

					// Required Fields
					inspection.setCompany(companyID);
					inspection.setTechID(Utils.parseInt(techID, 0));
					inspection.setLastName(lastName);

					// Optional Fields
					inspection.setFirstName(req.getParameter("inspection[firstname]"));
					inspection.setAddress(req.getParameter("inspection[address]"));
					inspection.setCity(req.getParameter("inspection[city]"));
					inspection.setState(req.getParameter("inspection[state]"));
					inspection.setZIP(req.getParameter("inspection[zip]"));
					inspection.setPhone(req.getParameter("inspection[phone]"));
					inspection.setEmail(req.getParameter("inspection[email]"));
					inspection.setDate(new Timestamp(Utils.parseLong(req.getParameter("inspection[date]"), System.currentTimeMillis())));
					inspection.setSendQuote("true".equals(req.getParameter("inspection[sendquote]")));
					inspection.setSignature(req.getParameter("inspection[signature]"));

					inspection.insert();

					// Inspection Areas
					int areaCount = Utils.parseInt(req.getParameter("inspection[inspectionareas][count]"), 0);
					Map<String, Integer> idMap = new HashMap<String, Integer>();

					for(int i = 0; i < areaCount; i++) {
						InspectionArea inspectionArea = new InspectionArea();
						inspectionArea.setAreaID(Utils.parseInt(req.getParameter("inspection[inspectionareas][list][" + i + "][area]"), 0));
						inspectionArea.setName(req.getParameter("inspection[inspectionareas][list][" + i + "][name]"));
						inspectionArea.setInspection(inspection.getID());

						inspectionArea.insert();

						idMap.put(req.getParameter("inspection[inspectionareas][list][" + i + "][id]"), inspectionArea.getID());
					}

					// Inspection Area Line Items
					int lineItemCount = Utils.parseInt(req.getParameter("inspection[inspectionarealineitems][count]"), 0);

					for(int i = 0; i < lineItemCount; i++) {
						String result = req.getParameter("inspection[inspectionarealineitems][list][" + i + "][result]");

						if(Utils.notEmpty(result)) {
							InspectionAreaLineItem lineItem = new InspectionAreaLineItem();
							lineItem.setInspectionArea(idMap.get(req.getParameter("inspection[inspectionarealineitems][list][" + i + "][inspectionarea]")));
							lineItem.setLineItemID(Utils.parseInt(req.getParameter("inspection[inspectionarealineitems][list][" + i + "][lineitem]"), 0));
							lineItem.setResult(result);
							String comment = req.getParameter("inspection[inspectionarealineitems][list][" + i + "][comment]");

							if(Utils.notEmpty(comment) && !"null".equals(comment)) {
								lineItem.setComment(comment);
							}

							lineItem.insert();

							idMap.put(req.getParameter("inspection[inspectionarealineitems][list][" + i + "][id]"), lineItem.getID());
						}
					}

					// Custom Line Items
					int customItemCount = Utils.parseInt(req.getParameter("inspection[customlineitems][count]"), 0);

					for(int i = 0; i < customItemCount; i++) {
						CustomLineItem customLineItem = new CustomLineItem();
						customLineItem.setInspectionArea(idMap.get(req.getParameter("inspection[customlineitems][list][" + i + "][inspectionarea]")));
						customLineItem.setName(req.getParameter("inspection[customlineitems][list][" + i + "][name]"));
						customLineItem.setResult(req.getParameter("inspection[customlineitems][list][" + i + "][result]"));
						customLineItem.setComment(req.getParameter("inspection[customlineitems][list][" + i + "][comment]"));
						customLineItem.setParent(Utils.parseInt(req.getParameter("inspection[customlineitems][list][" + i + "][parent]"), 0));

						customLineItem.insert();

						idMap.put(req.getParameter("inspection[customlineitems][list][" + i + "][id]"), customLineItem.getID());
					}

					// Quote Items
					int quoteItemCount = Utils.parseInt(req.getParameter("inspection[quoteitems][count]"), 0);

					for(int i = 0; i < quoteItemCount; i++) {
						Task task = Tasks.getByNumber(req.getParameter("inspection[quoteitems][list][" + i + "][task]"), companyID);

						if(task != null) {
							QuoteItem quoteItem = new QuoteItem();
							quoteItem.setTaskID(task.getID());
							quoteItem.setInspectionAreaLineItemID(idMap.get(req.getParameter("inspection[quoteitems][list][" + i + "][inspectionarealineitem]")));
							quoteItem.setActive("true".equals(req.getParameter("inspection[quoteitems][list][" + i + "][active]")));
							quoteItem.setAddOn("true".equals(req.getParameter("inspection[quoteitems][list][" + i + "][addon]")));

							quoteItem.insert();
						}
					}

					sendEmail(req.getRequestURL().toString(), inspection, true);
				}

				writer.println("<success>true</success>");
				writer.println("<key>" + key + "</key>");
			}
			catch(Exception e) {
				e.printStackTrace();
				writer.println("<success>false</success>");
			}
		}
		else if("delete".equals(request)) {
			int tech = Utils.parseInt(techID, 0);

			if(tech < 1) {
				tech = Utils.parseInt(Utils.getCookieValue(req, "techid"), -1);
			}

			Technician technician = Technicians.getTechnician(tech);

			if(technician != null && technician.isAdmin()) {
				Inspection inspection = Inspections.getInspection(Utils.parseInt(req.getParameter("inspectionid"), 0));

				if(inspection != null) {
					inspection.delete();

					writer.println("<success>true</success>");
				}
				else {
					writer.println("<success>false</success>");
					writer.println("<message>That inspection does not exist.</message>");
				}
			}
			else {
				writer.println("<success>false</success>");
			}
		}
		else if("contact".equals(request)) {
			int tech = Utils.parseInt(techID, 0);

			if(tech < 1) {
				tech = Utils.parseInt(Utils.getCookieValue(req, "techid"), -1);
			}

			Technician technician = Technicians.getTechnician(tech);

			if(technician != null && technician.isAdmin()) {
				Inspection inspection = Inspections.getInspection(Utils.parseInt(req.getParameter("inspectionid"), 0));

				if(inspection != null) {
					boolean contacted = "true".equals(req.getParameter("contacted"));
					inspection.setContacted(contacted);
					inspection.update();

					writer.println("<success>true</success");
				}
				else {
					writer.println("<success>false</success");
					writer.println("<message>That inspection does not exist.</message>");
				}
			}
		}
		else {
			writer.println("<success>false</success>");
		}

		writer.println("</result>");
		System.out.println("Request " + requestID + " took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		String request = req.getParameter("request");

		if("email".equals(request)) {
			Inspection inspection = Inspections.getInspection(Utils.parseInt(req.getParameter("inspectionid"), 0));

			if(inspection != null) {
				sendEmail(req.getRequestURL().toString(), inspection, true);
			}
		}
	}

	public static void sendEmail(String reqURL, Inspection inspection, boolean sendCompanyNotification) {
		final String requestURL = reqURL;
		final int inspectionID = inspection.getID();
		final int companyID = inspection.getCompany();
		final int techID = inspection.getTechID();
		final String emailAddress = inspection.getEmail();
		final Timestamp date = inspection.getDate();

		Thread thread = new Thread() {

			public void run() {
				Company company = Companies.getCompany(companyID);
				Technician technician = Technicians.getTechnician(techID);

				SimpleDateFormat dateFormat = new SimpleDateFormat("MMM_dd_YYYY");
				String fileName = "Inspection_" + dateFormat.format(date) + "__" + inspectionID;
				String fullFilePath = "C:/PDF/inspections/" + company.getID() + "/" + fileName + ".pdf";

				File file = new File(fullFilePath);
				if(file.exists()) {
					file.delete();
				}

				String fullURL = requestURL;
				fullURL = fullURL.substring(0, fullURL.lastIndexOf("/"));

				GeneratePDF generatePDF = new GeneratePDF();
				generatePDF.genrateCmd("http://localhost/pdf-generator/index.html?inspectionID=" + inspectionID, "inspections", String.valueOf(company.getID()), fileName);

				try{
					Thread.sleep(3000); //Give the PDF Generator a little time to wrap up.
				}
				catch(Exception e) {
					e.printStackTrace();
					System.out.println("COULD NOT SLEEP THREAD");
				}

				if(Utils.notEmpty(emailAddress)) {

					Email email = new Email();
					email.setTo(emailAddress);
					email.setFrom("info@servicetechapps.com");
					email.setSubject(company.getName() + " Home Inspection Results");

					email.setContent("For questions about your inspection results, or to schedule service for failed items please call " + company.getPhone() + ". Your technician was " + technician.getFirstName() + ".");

					email.setFileName(fullFilePath);

					try {
						email.send();
					}
					catch(Exception e) {
						// Sometimes the file hasn't finished being written yet, so let's wait a bit a try again
						try {
							System.out.println("PDF generator may not have finished yet. Waiting 20 seconds to try again...");
							e.printStackTrace();
							Thread.sleep(20000);
							email.send();
						}
						catch (Exception ex) {
							System.out.println("Could not send inspection email to customer. Inspection: " + inspectionID + "; Company: " + company.getName());
							ex.printStackTrace();
						}
					}
				}

				Email companyNotification = new Email();
				companyNotification.setFrom("info@servicetechapps.com");
				companyNotification.setTo(company.getEmail());
				companyNotification.setSubject("New Inspection");
				companyNotification.setContent("A new inspection has been completed by " + technician.getFirstName() + " " + technician.getLastName() + ". Go to your <a href='http://67.186.221.39:8080/HomeInspection/company-login.jsp'>Control Panel</a> to see the results.");
				companyNotification.setFileName(fullFilePath);

				try {
					companyNotification.send();
				}
				catch(Exception e) {
					try {
						System.out.println("PDF generator may not have finished yet. Waiting 20 seconds to try again...");
						e.printStackTrace();
						Thread.sleep(20000);
						companyNotification.send();
					}
					catch (Exception ex) {
						System.out.println("Could not send company notification. Inspection: " + inspectionID + "; Company: " + company.getName());
						ex.printStackTrace();
					}

				}
			}
		};

		thread.start();
	}
}