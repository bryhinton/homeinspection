package screens;

import sqlrow.*;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/20/12
 * Time: 8:23 PM
 */
public class ScreenUtil {

	public static void writeHeader(HttpServletRequest req, PrintWriter writer, Technician technician) {
		writer.println("<!DOCTYPE html>");
		writer.println("<html>");

		writer.println("<head>");
		writer.println("<title>Control Panel</title>");
		writer.println("<script type='text/javascript' src='../scripts/jquery-1.7.1.min.js'></script>");
		writer.println("<script type='text/javascript' src='../scripts/controlpanel.js'></script>");
		writer.println("<link rel='stylesheet' href='../stylesheets/controlpanel.css'>");
		writer.println("</head>");

		writer.println("<body>");
		writer.println("<input type='hidden' id='tech-id' value='" + technician.getID() + "'>");
		writer.println("<input type='hidden' id='company-id' value='" + technician.getCompanyID() + "'>");
		writer.println("<div class='header-title'>Home Safety Inspection</div>");

		writer.println("<a class='logout' href='logout'>Logout</a>");

		Company company = Companies.getCompany(technician.getCompanyID());
		writer.println("<div class='subheader' id='page-subheader'>Control Panel - " + company.getName() + "</div>");

		writer.println("<div class='side-panel'>");

		boolean isInspections = req.getPathInfo() == null || req.getPathInfo().equals("/inspections");

		writer.println("<div class='item'>");
		writer.println("<a href='inspections'>" + (isInspections ? "> " : "") + "Inspections</a>");

		if(isInspections) {
			writer.println("<div class='sub-nav' id='by-date-range'>By Date Range");

			String currentDateFilter = req.getParameter("date-filter") != null ? req.getParameter("date-filter") : Utils.getCookieValue(req, "datefilter");

			writer.println("<ul class='hidden-list' id='date-range'>");
			writer.println("<li" + ("all".equals(currentDateFilter) || Utils.isEmpty(currentDateFilter) ? " class='selected'" : "") + "><a href='inspections?date-filter=all'>All</a></li>");
			writer.println("<li" + ("30days".equals(currentDateFilter) ? " class='selected'" : "") + "><a href='inspections?date-filter=30days'>Last 30 days</a></li>");
			writer.println("<li" + ("3months".equals(currentDateFilter) ? " class='selected'" : "") + "><a href='inspections?date-filter=3months'>Last 3 months</a></li>");
			writer.println("<li" + ("lastyear".equals(currentDateFilter) ? " class='selected'" : "") + "><a href='inspections?date-filter=lastyear'>Last Year</a></li>");
			writer.println("</ul>");

			writer.println("</div>");

			writer.println("<div class='sub-nav' id='by-technician'>By Technician");

			String currentTechnician = req.getParameter("tech-filter") != null ? req.getParameter("tech-filter") : Utils.getCookieValue(req, "techfilter");
			List<Technician> technicians = Technicians.getAllForCompany(technician.getCompanyID());

			writer.println("<ul class='hidden-list' id='technician-list'>");
			writer.println("<li" + ("all".equals(currentTechnician) || Utils.isEmpty(currentTechnician) ? " class='selected'" : "") + "><a href='inspections?tech-filter=all'>All</a></li>");

			for(Technician tech : technicians) {
				writer.println("<li" + (String.valueOf(tech.getID()).equals(currentTechnician) ? " class='selected'" : "") + "><a href='inspections?tech-filter=" + tech.getID() + "'>" + tech.getFirstName() + " " + tech.getLastName() + "</a></li>");
			}

			writer.println("</ul>");
			writer.println("</div>");

			writer.println("<div class='sub-nav' id='by-options'>By Options");
			String currentOption = req.getParameter("option") != null ? req.getParameter("option") : Utils.getCookieValue(req, "option");

			writer.println("<ul class='hidden-list' id='options'>");
			writer.println("<li" + ("all".equals(currentOption) || Utils.isEmpty(currentOption) ? " class='selected'" : "") + "><a href='inspections?option=all'>None</a></li>");
			writer.println("<li" + ("uncontacted".equals(currentOption) ? " class='selected'" : "") + "><a href='inspections?option=uncontacted'>Uncontacted</a></li>");
			writer.println("</ul>");
			writer.println("</div>");

			writer.println("<div class='sub-nav' id='search-nav'>Search");
			writer.println("</div>");

			String searchTerm = req.getParameter("search");

			writer.println("<div class='hidden-search" + (Utils.notEmpty(searchTerm) ? " show" : "") + "' id='hidden-search'>");
			writer.println("<input type='text' id='search' value='" + (Utils.notEmpty(searchTerm) ? searchTerm : "") + "'>");
			writer.println("<button type='button' onclick='clearSearch();'>Clear</button>");
			writer.println("<button type='button' onclick='search();'>Search</button>");
			writer.println("<div class='info'>Enter first name, last name, or city.</div>");
			writer.println("</div>");
		}

		writer.println("</div>");

		writer.println("<div class='item'>");
		writer.println("<a href='settings'>" + (!isInspections ? "> " : "") + "Settings</a>");
		writer.println("</div>");

		writer.println("</div>");
		writer.println("</div>");

		writer.println("<div class='page-content'>");
	}

	public static void writeAdminHeader(HttpServletRequest req, PrintWriter writer, Technician technician) {
		writer.println("<!DOCTYPE html>");
		writer.println("<html>");

		writer.println("<head>");
		writer.println("<title>Admin</title>");
		writer.println("<script type='text/javascript' src='scripts/jquery-1.7.1.min.js'></script>");
		writer.println("<script type='text/javascript' src='scripts/controlpanel.js'></script>");
		writer.println("<link rel='stylesheet' href='stylesheets/controlpanel.css'>");
		writer.println("</head>");

		writer.println("<body class='admin'>");
		writer.println("<div id='mask'></div>");
		writer.println("<input type='hidden' id='tech-id' value='" + technician.getID() + "'>");
		writer.println("<div class='admin header-title'>Home Safety Inspection</div>");

		writer.println("<a class='logout' href='admin/logout'>Logout</a>");

		writer.println("<div class='subheader' id='page-subheader'>Admin</div>");

		writer.println("<div class='side-panel'>");

		writer.println("<div class='item'>");
		writer.println("<a href='admin'>> Companies</a>");
		writer.println("</div>");

		writer.println("</div>");

		writer.println("<div class='page-content'>");
		writer.println("<div class='save-message'><span>Your work has been saved.</span></div>");

		if("true".equals(req.getParameter("save"))) {
			writer.println("<script type='text/javascript'>");
			writer.println("$(\".save-message\").css(\"display\", \"block\");setTimeout(hideSaveMessage, 1000);");
			writer.println("</script>");
		}
	}

	public static void writerFooter(PrintWriter writer) {
		writer.println("</div>");
		writer.println("</body>");
		writer.println("</html>");
	}
}
