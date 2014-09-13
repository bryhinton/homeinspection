package screens;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/20/12
 * Time: 8:14 PM
 */
public class ControlPanel extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doGet(req, rsp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		if(req.getPathInfo() == null) {
			rsp.sendRedirect("control-panel/inspections");
			return;
		}

		int techID = Utils.parseInt(Utils.getCookieValue(req, "techid"), 0);

		Technician technician = Technicians.getTechnician(techID);

		if(technician != null && technician.isAdmin()) {
			PrintWriter writer = Utils.getWriter(rsp);

			ScreenUtil.writeHeader(req, writer, technician);

			if(req.getPathInfo().equals("/logout")) {
				Cookie cookie = new Cookie("techid", "");
				cookie.setPath("/");
				rsp.addCookie(cookie);
				rsp.sendRedirect("../company-login.jsp");
			}
			else if(req.getPathInfo().equals("/inspections") || req.getPathInfo().equals("/control-panel")) {
				Map<String, String> filters = new HashMap<String, String>();
				String dateFilter = req.getParameter("date-filter");
				String techFilter = req.getParameter("tech-filter");
				String optionFilter = req.getParameter("option");
				String search = req.getParameter("search");

				if(Utils.notEmpty(dateFilter)) {
					filters.put("date", dateFilter);
					rsp.addCookie(new Cookie("datefilter", dateFilter));
				}
				else {
					filters.put("date", Utils.getCookieValue(req, "datefilter"));
				}

				if(Utils.notEmpty(techFilter)) {
					filters.put("tech", techFilter);
					rsp.addCookie(new Cookie("techfilter", techFilter));
				}
				else {
					filters.put("tech", Utils.getCookieValue(req, "techfilter"));
				}

				if(Utils.notEmpty(optionFilter)) {
					filters.put("option", optionFilter);
					rsp.addCookie(new Cookie("option", optionFilter));
				}
				else {
					filters.put("option", Utils.getCookieValue(req, "option"));
				}

				if(Utils.notEmpty(search)) {
					filters.put("search", search);
				}

				writer.println("<div class='inspection-table section'>");
				List<Inspection> inspections = Inspections.getInspectionsForCompany(technician.getCompanyID(), filters);

				writer.println("<div class='header'>Inspections</div>");

				for(Inspection inspection : inspections) {
					int inspectionID = inspection.getID();
					Technician inspectionTech = Technicians.getTechnician(inspection.getTechID());
					writer.println("<div class='inspection-row row" + (inspection.isContacted() ? " row-contacted" : "") + "' onclick='loadInspection(event, \"" + inspectionID + "\");' inspection-id='" + inspectionID + "'>");

					String firstName = inspection.getFirstName();
					writer.println("<div class='cell name'>" + (Utils.notEmpty(firstName) ? firstName + " " : "") + inspection.getLastName() + "</div>");
					writer.println("<div class='cell date'>" + new SimpleDateFormat(DateUtil.shortDate).format(inspection.getDate()) + "</div>");
					writer.print("<div class='contacted'>");
					writer.print("<input type='checkbox' onchange='toggleContact(" + inspectionID + ")' name='contacted-" + inspectionID + "' id='contacted-" + inspectionID + "' " + (inspection.isContacted() ? "checked=checked ": "") + "> ");
					writer.println("<label for='contacted-" + inspectionID + "'>Contacted</label></div>");
					writer.println("<div class='cell tech'>" + inspectionTech.getFirstName() + " " + inspectionTech.getLastName() + "</div>");
					writer.println("<div class='delete-x' onclick='deleteInspection(\"" + inspectionID + "\");'>&#x2716;</div>");

					writer.println("</div>");
				}

				writer.println("</div>");
			}
			else if(req.getPathInfo().equals("/settings")) {
				writer.println("<div class='save-message'><span>Your work has been saved.</span></div>");
				Company company = Companies.getCompany(technician.getCompanyID());
				int companyID = company.getID();

				List<CompanyApp> companyApps = CompanyApps.getAllForCompany(companyID);

				// *****************
				// Pricing File Upload

				writer.println("<div class='section' id='pricing'>");
				writer.println("<div class='header'>Pricing File Upload</div>");

				File file = null;

				try {
					file = new File("/pricing/pricing_" + companyID + ".xml");
				}
				catch(Exception e) {
				}

				if(file != null && file.exists()) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(file.lastModified()));
					writer.println("<div class='file-message'>Your pricing file was uploaded on " + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + ".</div>");
				}
				else {
					writer.println("<div class='file-message'>You have not yet uploaded a pricing file.</div>");
				}

				writer.println("<form action='../pricing-upload' enctype='multipart/form-data' method='POST'>");
				writer.println("<input type='hidden' name='companyid' value='" + companyID + "'>");
				writer.println("<label>Choose a new pricing file to upload:</label>");
				writer.println("<input type='file' name='pricing-file'>");
				writer.println("<button type='submit'>Upload</button>");
				writer.println("</form>");

				writer.println("</div>");

				// *****************
				// Areas / Line Items
				if(CompanyApps.contains(companyApps, CompanyApps.INSPECTION)) {
					writer.println("<div class='section' id='areas'>");
					writer.println("<div class='header'>Inspection Areas</div>");

					for(Area area : Areas.getAllForCompany(companyID, true)) {
						int areaID = area.getID();
						writer.println("<div class='area row' id='area-" + areaID + "' data-id='" + areaID + "' onclick='toggleHiddenList(\"area-info-" + areaID + "\");'>");
						writer.println("<div class='cell' id='area-name-text-" + areaID + "')>" + area.getName() + "</div>");
						writer.println("<div class='delete-x' onclick='deleteArea(\"" + areaID + "\");'>&#x2716;</div>");
						writer.println("</div>");

						writer.println("<div class='area-info' id='area-info-" + areaID + "' data-id='" + areaID + "'>");
						writer.print("<div id='area-" + areaID + "'><label>Area Name:</label>");
						writer.print("<input type='text' id='area-name-" + areaID + "' value='" + area.getName() + "'>");
						writer.print("<label>Active:</label><input type='checkbox' id='area-active-" + areaID + "'" + (area.isActive() ? " checked=checked": "") + ">");
						writer.println("</div>");

						for(LineItem parent : LineItems.getTopLevelLineItems(areaID, true)) {
							int parentID = parent.getID();
							writer.println("<div id='parent-" + parentID + "'>");
							writer.println("<div class='subheader'>Section</div>");
							writer.println("<div class='parent' data-id='" + parentID + "'>");
							writer.print("<div id='lineitem-" + parentID + "'><label>Name:</label>");
							writer.print("<input type='text' id='lineitem-name-" + parentID + "' value='" + parent.getName() + "'>");
							writer.print("<label>Active:</label><input type='checkbox' id='lineitem-active-" + parentID + "'" + (parent.isActive() ? " checked=checked": "") + ">");
							writer.println("</div>");
							writer.println("<div class='delete-x' onclick='deleteLineItem(\"" + parentID + "\", true);'>&#x2716;</div>");

							writer.println("<div class='subheader'>Line Items</div>");

							for(LineItem lineItem : LineItems.getChildren(parentID, true)) {
								int lineItemID = lineItem.getID();
								writer.print("<div class='lineitem row no-hover' id='lineitem-" + lineItemID +"' data-id='" + lineItemID + "'>");
								writer.print("<label>Name:</label><input type='text' id='lineitem-name-" + lineItemID + "' value='" + lineItem.getName() + "'>");
								writer.print("<label>Active:</label><input type='checkbox' id='lineitem-active-" + lineItemID + "'" + (lineItem.isActive() ? " checked=checked": "") + ">");
								writer.println("<div class='delete-x' onclick='deleteLineItem(\"" + lineItemID + "\", false);'>&#x2716;</div>");
								writer.println("</div>");
							}

							writer.println("<div class='lineitem row no-hover' id='lineitem-" + parentID + "-new'>");
							writer.println("<button type='button' onclick='addLineItem(\"" + parentID + "\", \"" + areaID + "\");' id='add-lineitem-" + parentID + "' style='display:block'>Add Line Item...</button>");
							writer.println("</div>");

							writer.println("</div>");
							writer.println("</div>");
						}

						writer.println("<div class='subheader'></div>");
						writer.println("<div class='parent'>");
						writer.println("<button type='button' onclick='addParent(\"" + areaID + "\");' id='add-parent-" + areaID + "' style='display:block'>Add Section...</button>");
						writer.println("</div>");

						writer.println("</div>");
					}

					writer.println("<div class='area' id='area-new'>");
					writer.println("<button type='button' onclick='addArea()' id='add-area'>Add Area...</button>");
					writer.println("</div>");

					writer.println("</div>");
				}

				// *****************
				// Timecard Fields
				if(CompanyApps.contains(companyApps, CompanyApps.TIMECARD)) {
					writer.println("<div class='section' id='timecard-fields'>");
					writer.println("<div class='header'>Timecard Fields</div>");

					List<Field> fields = Fields.getAllForCompany(companyID);

					int i = 1;
					for(Field field : fields) {
						int fieldID = field.getID();
						writer.println("<div class='field row' id='field-" + fieldID + "' onclick='toggleField(" + fieldID + ");'>");
						writer.println("<div class='cell number'>" + i + ".</div>");
						writer.println("<div class='cell field-name'>" + field.getName() + "</div>");
						writer.println("<div class='cell isactive " + (field.isEnabled() ? "active" : "inactive") + "'>" + (field.isEnabled() ? "Active" : "Inactive") + "</div>");

						writer.println("</div>");

						writer.println("<div class='field-info' id='field-info-" + fieldID + "')>");
						writer.println("<label>Name:</label><input type='text' id='field-name-" + fieldID + "' value='" + field.getName() + "'><br>");
						writer.println("<label>Type:</label><select id='field-type-" + fieldID + "' value='" + field.getType() + "'>");

						for(FieldType type : FieldType.values()) {
							writer.println("<option value='" + type.getValue() + "'>" + type.getLabel() + "</option>");
						}
						writer.println("</select><br>");

						writer.println("<label>Required:</label><input type='checkbox' id='field-required-" + fieldID + "'" + (field.isRequired() ? " checked=checked'" : "") + "'><br>");
						writer.println("<label>Active:</label><input type='checkbox' id='field-active-" + fieldID + "'" + (field.isEnabled() ? " checked=checked'" : "") + "'><br>");
						writer.println("<label>Sum field on report:</label><input type='checkbox' id='field-sum-" + fieldID + "'" + (field.shouldSum() ? " checked=checked'" : "") + "'><br>");
						writer.println("<button type='button' onclick='editField(\"" + fieldID + "\");'>Save</button>");
						writer.println("</div>");

						i++;
					}

					writer.println("<div class='field-info' id='field-info-new')>");
					writer.println("<label>Name:</label><input type='text' id='field-name-new'><br>");
					writer.println("<label>Type:</label><select id='field-type-new'>");

					for(FieldType type : FieldType.values()) {
						writer.println("<option value='" + type.getValue() + "'>" + type.getLabel() + "</option>");
					}
					writer.println("</select><br>");

					writer.println("<label>Required:</label><input type='checkbox' id='field-required-new'><br>");
					writer.println("<label>Active:</label><input type='checkbox' id='field-active-new'><br>");
					writer.println("<label>Sum field on report:</label><input type='checkbox' id='field-sum-new'><br>");
					writer.println("<button type='button' onclick='editField(\"new\");'>Save</button>");
					writer.println("</div>");

					writer.println("<div class='field' id='field-new'>");
					writer.println("<button type='button' onclick='toggleField(\"new\")'>Add Field...</button>");
					writer.println("</div>");

					writer.println("</div>");
				}

				// *****************
				// Company Licenses
				writer.println("<div class='section' id='licenses'>");
				writer.println("<div class='header'>Devices</div>");

				List<CompanyLicense> licenses = CompanyLicenses.getAllForCompany(companyID);

				int i = 1;
				for(CompanyLicense license : licenses) {
					writer.println("<div class='license row no-hover'>");
					writer.println("<div class='cell number'>" + i + ".</div>");

					Technician lastLoginTech = Technicians.getTechnician(license.getLastTech());

					if(lastLoginTech != null) {
						writer.println("<div class='cell techname'>" + lastLoginTech.getFirstName() + " " + lastLoginTech.getLastName() + "</div>");
					}
					else {
						writer.println("<div class='cell unused'>Unused</div>");
					}

					writer.println("</div>");

					i++;
				}

				writer.println("</div>");

				//******************
				// Technicians
				writer.println("<div class='section' id='technicians'>");
				writer.println("<div class='header'>Technicians</div>");


				for(Technician tech : Technicians.getAllForCompany(technician.getCompanyID())) {
					writer.println("<div class='technician row' id='technician-" + tech.getID() + "' onclick='toggleTechnician(" + tech.getID() + ");'>");
					writer.println("<div class='cell firstname'>" + tech.getFirstName() + "</div>");
					writer.println("<div class='cell lastname'>" + tech.getLastName() + "</div>");
					writer.println("<div class='cell isactive " + (tech.isActive() ? "active" : "inactive") + "'>" + (tech.isActive() ? "Active" : "Inactive") + "</div>");

					writer.println("</div>");

					writer.println("<div class='technician-info' id='technician-info-" + tech.getID() + "')>");
					writer.println("<label>First Name:</label><input type='text' id='firstname-" + tech.getID() + "' value='" + tech.getFirstName() + "'>");
					writer.println("<label>Last Name:</label><input type='text' id='lastname-" + tech.getID() + "' value='" + tech.getLastName() + "'>");
					writer.println("Active:<input type='checkbox' id='active-" + tech.getID() + "'" + (tech.isActive() ? " checked=checked'" : "") + "'><br>");
					writer.println("<label>Username:</label><input type='text' id='username-" + tech.getID() + "' value='" + tech.getUsername() + "'>");
					writer.println("<label>Password:</label><input type='text' id='password-" + tech.getID() + "' value='" + tech.getPassword() + "'>");
					writer.println("Administrator:<input type='checkbox' id='admin-" + tech.getID() + "'" + (tech.isAdmin() ? " checked=checked'" : "") + "' " + (tech.getID() == techID ? " disabled=true" : "") + "><br>");
					writer.println("<button type='button' onclick='editTechnician(\"" + tech.getID() + "\");'>Save</button>");
					writer.println("</div>");
				}

				writer.println("<div class='technician' id='technician-new'>");
				writer.println("<button type='button' onclick='toggleTechnician(\"new\")'>Add Technician...</button>");
				writer.println("<div class='technician-info' id='technician-info-new')>");
				writer.println("<label>First Name:</label><input type='text' id='firstname-new'>");
				writer.println("<label>Last Name:</label><input type='text' id='lastname-new'>");
				writer.println("Active:<input type='checkbox' id='active-new' checked='checked'><br>");
				writer.println("<label>Username:</label><input type='text' id='username-new'>");
				writer.println("<label>Password:</label><input type='text' id='password-new'>");
				writer.println("Administrator:<input type='checkbox' id='admin-new'><br>");
				writer.println("<button type='button' onclick='editTechnician(\"new\");'>Save</button>");
				writer.println("</div>");
				writer.println("</div>");

				writer.println("</div>");

				// *********************
				// Company Info
				writer.println("<div class='section' id='company-info'>");
				writer.println("<div class='header'>Company Info</div>");

				File logoFile = null;

				try {
					logoFile = new File("C:/images/" + companyID + "/" + company.getLogo());
				}
				catch(Exception e) {
				}

				if(logoFile != null && logoFile.exists()) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(logoFile.lastModified()));
					writer.println("<div class='file-message'>You uploaded <b>" + company.getLogo() + "</b> on " + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR) + ".</div>");
				}
				else {
					writer.println("<div class='file-message'>You have not yet uploaded a logo.</div>");
				}

				writer.println("<form action='../logo-upload' enctype='multipart/form-data' method='POST'>");
				writer.println("<input type='hidden' name='companyid' value='" + companyID + "'>");
				writer.println("<label>Choose a new logo file to upload:</label>");
				writer.println("<input type='file' name='logo-file'>");
				writer.println("<button type='submit'>Upload</button>");
				writer.println("</form>");

				writer.println("<div class='company-name'><label>Name:</label><input type='text' id='company-name' value='" + company.getName() + "'></div>");
				writer.println("<div class='company-name'><label>Email:</label><input type='text' id='company-email' value='" + company.getEmail() + "'></div>");
				writer.println("<div class='company-name'><label>Phone:</label><input type='text' id='company-phone' value='" + company.getPhone() + "'></div>");

				writer.println("</div>");

				// *********************
				// Other Settings
				writer.println("<div class='section' id='other-settings'>");
				writer.println("<div class='header'>Other Settings</div>");
				writer.println("<div class='other-settings'><label>Send Quote with Customer Email:</label><input type='checkbox' id='send-quote' value='true' " + (company.shouldSendQuote() ? "checked='checked'" : "") + "></div>");

				writer.println("</div>");
			}

			ScreenUtil.writerFooter(writer);
		}
		else {
			rsp.sendRedirect("../company-login.jsp");
		}
	}
}
