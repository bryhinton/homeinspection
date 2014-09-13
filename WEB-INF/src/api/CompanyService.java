package api;

import sqlrow.Companies;
import sqlrow.Company;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 7/17/13
 * Time: 8:16 PM
 */
public class CompanyService extends Service {

	// Read
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse rsp) {
		super.doGet(req, rsp);
	}

	// Create/Update
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse rsp) {
		super.doPost(req, rsp);
	}

	//Files
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse rsp) {
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse rsp) {
		super.doDelete(req, rsp);
	}
}
