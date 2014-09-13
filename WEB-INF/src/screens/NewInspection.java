package screens;

import sqlrow.Inspection;
import sqlrow.InspectionArea;
import sqlrow.InspectionAreaLineItem;
import sqlrow.InspectionAreaLineItems;
import sqlrow.InspectionAreas;
import sqlrow.Utils;

import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@SuppressWarnings("serial")
public class NewInspection extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		if("true".equals(req.getParameter("new-inspection")))
		{
			Inspection inspection = new Inspection();
			inspection.setFirstName("Bryan");
			inspection.setLastName("Hinton");
			inspection.setDate(new Timestamp(System.currentTimeMillis()));

			inspection.insert();

			resp.sendRedirect("inspection.jsp?id=" + inspection.getID());
		}
		else if("true".equals(req.getParameter("finished")))
		{
			int inspectionID = Utils.parseInt(req.getParameter("inspectionid"), -1);

			List<InspectionArea> inspectionAreas = InspectionAreas.getAllForInspection(inspectionID);

			for (InspectionArea area : inspectionAreas)
			{
				List<InspectionAreaLineItem> inspectionLineItems = InspectionAreaLineItems.getAllForArea(area.getID());

				for (InspectionAreaLineItem item : inspectionLineItems)
				{
					String comment = req.getParameter("comment_" + item.getLineItem());
					if(Utils.notEmpty(comment))
					{
						item.setComment(comment);
						item.update();
					}
				}
			}

			resp.sendRedirect("inspection-reviewDEMO.jsp?id=" + inspectionID);
		}
	}
}
