package screens;

import sqlrow.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 6/29/13
 * Time: 9:10 AM
 */
public class FieldInfo extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		PrintWriter writer = Utils.getWriter(resp);

		writer.println("{ \"fields\": [");

		Company company = Companies.getCompany(Utils.parseInt(req.getParameter("companyid"), 0));

		if(company != null) {
			int companyID = company.getID();
			int techID = Utils.parseInt(req.getParameter("techid"), 0);

			String key = Login.checkKey(companyID, techID, req.getParameter("key"));

			if(Utils.notEmpty(key)) {
				List<Field> fields = Fields.getAllForCompany(companyID);
				for(int i = 0; i < fields.size(); i++) {
					Field field = fields.get(i);
					if(field.isEnabled()) {
						writer.println("{");

						writer.println("\"name\": \"" + field.getName() + "\",");
						writer.println("\"type\": \"" + field.getType() + "\",");
						writer.println("\"required\": \"" + field.isRequired() + "\"");

						writer.println("}" + (i < (fields.size() - 1) ? "," : ""));
					}
				}
			}
		}

		writer.println("]}");
	}
}
