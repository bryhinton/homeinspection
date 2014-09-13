package screens;

import sqlrow.Companies;
import sqlrow.Company;
import sqlrow.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 12/17/12
 * Time: 7:07 PM
 */
public class PricingServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		doGet(req, rsp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		PrintWriter writer = Utils.getWriter(rsp);

		int companyID = Utils.parseInt(req.getParameter("companyid"), 0);

		if(companyID > 0) {
			File file = new File("/pricing/pricing_" + companyID + ".xml");
			System.out.println("Attempting to serve pricing file: " + file.getAbsolutePath());
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
				String line=null;
				while((line=reader.readLine())!=null){
					writer.println(line);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
