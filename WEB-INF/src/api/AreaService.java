package api;

import sqlrow.Area;
import sqlrow.Technician;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 7/21/13
 * Time: 9:06 AM
 */
public class AreaService extends Service {
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
