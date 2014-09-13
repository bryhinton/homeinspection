package api;

import sqlrow.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 7/18/13
 * Time: 10:16 PM
 */
public class Service extends HttpServlet {

	private static Map<String, Class> services = new HashMap<String, Class>();
	static {
		services.put("area", Area.class);
		services.put("category", Category.class);
		services.put("company", Company.class);
		services.put("companyApp", CompanyApp.class);
		services.put("companyLicense", CompanyLicense.class);
		services.put("customLineItem", CustomLineItem.class);
		services.put("field", Field.class);
		services.put("fieldValue", FieldValue.class);
		services.put("inspection", Inspection.class);
		services.put("inspectionArea", InspectionArea.class);
		services.put("inspectionAreaLineItem", InspectionAreaLineItem.class);
		services.put("lineItem", LineItem.class);
		services.put("quoteItem", QuoteItem.class);
		services.put("task", Task.class);
		services.put("technician", Technician.class);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse rsp) {
		rsp.addHeader("CONTENT-TYPE", "application/json");
		Technician technician = Technicians.getTechnician(Utils.parseInt(req.getParameter("userid"), 0));

		if(technician == null && req.getParameterMap().containsKey("username") && req.getParameterMap().containsKey("password")) {
			technician = Technicians.login(req.getParameter("username"), req.getParameter("password"));
		}

		if(technician != null && technician.isAdmin()) {
			String objectType = getObjectType(req);
			SQLRow sqlRow;

			try {
				sqlRow = (SQLRow) services.get(objectType).newInstance();
			}
			catch(Exception e) {
				System.out.println("COULD NOT GET SQLRow OBJECT");
				e.printStackTrace();
				return;
			}

			String action = getAction(req);
			String[] nestedFields = req.getParameterMap().containsKey("fields") ? req.getParameter("fields").split(",") : new String[]{};


			if("search".equals(action)) {
				Map<String, String> searchMap = new HashMap<String, String>();

				for(Object obj : req.getParameterMap().keySet()) {
					String key = (String) obj;
					if(!key.equals("userid") &&
						!key.equals("fields")) {
						searchMap.put(key, req.getParameter(key));
					}
				}

				List<SQLRow> sqlRows = search(objectType, sqlRow.getClass(), searchMap, Arrays.asList(nestedFields), sqlRow.getDefaultSort());
				writeJSON(sqlRows, rsp);
			}
			else {
				int objectID = getObjectID(req);

				sqlRow.load(objectID);
				sqlRow.includedNested = Arrays.asList(nestedFields);

				String objectAction = req.getParameter("action");

				if(Utils.notEmpty(objectAction) && sqlRow.actions.containsKey(objectAction)) {
					try {
						writeJSON(sqlRow.actions.get(objectAction).invoke(sqlRow, null), rsp);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				else {
					writeJSON(Arrays.asList(sqlRow), rsp);
				}
			}
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse rsp) {
		Technician technician = Technicians.getTechnician(Utils.parseInt(req.getParameter("userid"), 0));

		if(technician != null && technician.isAdmin()) {
			SQLRow sqlRow = getSQLRow(req);
			saveSQLRow(req, rsp, sqlRow);
		}
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse rsp, SQLRow sqlRow) {
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse rsp) {
		Technician technician = Technicians.getTechnician(Utils.parseInt(req.getParameter("userid"), 0));

		if(technician != null && technician.isAdmin()) {
			SQLRow sqlRow = getSQLRow(req);
			sqlRow.delete();
		}
	}

	protected SQLRow getSQLRow(HttpServletRequest req) {
		String objectType = getObjectType(req);
		int objectID = getObjectID(req);
		SQLRow sqlRow;

		try {
			sqlRow = (SQLRow) services.get(objectType).newInstance();
		}
		catch(Exception e) {
			System.out.println("COULD NOT GET SQLRow OBJECT");
			e.printStackTrace();
			return null;
		}

		sqlRow.load(objectID);
		return sqlRow;
	}

	protected String getObjectType(HttpServletRequest req) {
		String[] urlParts = req.getRequestURI().split("/");
		return urlParts[urlParts.length - 2];
	}

	protected int getObjectID(HttpServletRequest req) {
		String[] urlParts = req.getRequestURI().split("/");
		return Utils.parseInt(urlParts[urlParts.length - 1], 0);
	}

	protected String getAction(HttpServletRequest req) {
		String[] urlParts = req.getRequestURI().split("/");
		return urlParts[urlParts.length - 1];
	}

	protected void writeJSON(Object object, HttpServletResponse rsp) {
		Utils.getWriter(rsp).print(Utils.getGson().toJson(object));
	}

	protected void saveSQLRow(HttpServletRequest req, HttpServletResponse rsp, SQLRow sqlRow) {
		Map parameterMap = req.getParameterMap();

		for(Object key : parameterMap.keySet()) {
			if(!"userid".equals(key)) {
				try {
					Method method = sqlRow.loadList.get(key);

					if(method.getParameterTypes()[0].equals(Integer.TYPE)) {
						method.invoke(sqlRow, Utils.parseInt(((String[]) parameterMap.get(key))[0], 0));
					}

					if(method.getParameterTypes()[0].equals(Double.class)) {
						method.invoke(sqlRow, Utils.parseDouble(((String[]) parameterMap.get(key))[0], 0));
					}

					if(method.getParameterTypes()[0].equals(String.class)) {
						method.invoke(sqlRow, ((String[]) parameterMap.get(key))[0]);
					}

					if(method.getParameterTypes()[0].equals(Boolean.TYPE)) {
						method.invoke(sqlRow, "true".equals(((String[]) parameterMap.get(key))[0]));
					}

					if(method.getParameterTypes()[0].equals(Timestamp.class)) {
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.standardDate);
						Timestamp timestamp = new Timestamp(simpleDateFormat.parse(((String[]) parameterMap.get(key))[0]).getTime());
						method.invoke(sqlRow, timestamp);
					}

				}
				catch(Exception e) {
					System.out.println("COULD NOT SET PARAMETER: " + key + " ON COMPANY");
				}
			}
		}

		if(sqlRow.hasInfo()) {
			sqlRow.resetInfo(req.getParameter("info"));
		}

		if(sqlRow.getID() > 0) {
			sqlRow.update();
		}
		else {
			sqlRow.insert();
		}

		writeJSON(Arrays.asList(sqlRow), rsp);
		rsp.addHeader("CONTENT-TYPE", "application/json");
	}

	public List<SQLRow> search(String table, Class sqlRowClass, Map<String, String> parameters, List<String> includedNested, String defaultSort) {
		List<SQLRow> sqlRows = new ArrayList<SQLRow>();

		Connection db = null;
		ResultSet results = null;

		try
		{
			db = Utils.openConnection(Utils.HOME_INSPECTION);
			StringBuilder query = new StringBuilder();
			query.append("SELECT * FROM " + table);

			if(parameters.size() > 0) {
				query.append(" WHERE ");
			}

			int i = 0;
			for(Map.Entry<String, String> entry : parameters.entrySet()) {
				if(!entry.getKey().equals("userid")) {
					if(i > 0) {
						query.append("AND ");
					}

					String value = entry.getValue();
					double doubleValue = Utils.parseDouble(value, Integer.MIN_VALUE);

					query.append(entry.getKey() + "=" + (doubleValue > Integer.MIN_VALUE ? doubleValue : "'" + value + "'") + " ");
					i++;
				}
			}

			if(Utils.notEmpty(defaultSort)) {
				query.append(" ORDER BY " + defaultSort);
			}

			//query.append("LIMIT 50");

			PreparedStatement stmt = db.prepareStatement(query.toString());
			results = stmt.executeQuery();

			while(results.next()) {
				results.previous(); // This needs to happen because SQLRow.load() will call results.next() again, and we don't want to skip rows.
				SQLRow sqlRow = ((SQLRow) sqlRowClass.newInstance());
				sqlRow.load(results);
				sqlRow.includedNested.addAll(includedNested);

				sqlRows.add(sqlRow);
			}
		}
		catch (Exception e)
		{
			Utils.rollbackAndClose(db);
			System.out.println(e.getMessage());
		}
		finally
		{
			Utils.commitAndClose(db);
		}

		return sqlRows;
	}
}
