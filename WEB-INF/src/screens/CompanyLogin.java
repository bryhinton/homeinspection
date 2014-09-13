package screens;

import sqlrow.Technician;
import sqlrow.Technicians;
import sqlrow.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/20/12
 * Time: 7:46 PM
 */
public class CompanyLogin extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doGet(req, rsp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		String username = req.getParameter("username");
		String password = req.getParameter("password");

		Technician technician = Technicians.login(username, password);

		if(technician != null) {
			if("true".equals(req.getParameter("adminlogin")) && technician.isSystemAdmin()) {
				Cookie cookie = new Cookie("techid", String.valueOf(technician.getID()));
				cookie.setPath("/");
				rsp.addCookie(cookie);
				rsp.sendRedirect("admin");
				return;
			}
			else if(technician.isAdmin()) {
				Cookie cookie = new Cookie("techid", String.valueOf(technician.getID()));
				cookie.setPath("/");
				rsp.addCookie(cookie);
				rsp.sendRedirect("control-panel/inspections");
				return;
			}
		}

		rsp.addCookie(new Cookie("techid", ""));
		rsp.sendRedirect("company-login.jsp?error=true");
	}
}
