package api;

import sqlrow.Technician;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 7/18/13
 * Time: 10:31 PM
 */
public class TechnicianService extends Service {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse rsp) {
		super.doGet(req, rsp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse rsp) {
		super.doPost(req, rsp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse rsp) {
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse rsp) {
		super.doDelete(req, rsp);
	}
}
