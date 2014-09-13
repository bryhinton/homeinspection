package screens;

import sqlrow.InspectionAreaLineItem;
import sqlrow.InspectionAreaLineItems;
import sqlrow.InspectionResult;
import sqlrow.LineItems;
import sqlrow.Utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@SuppressWarnings("serial")
public class InspectionResultServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		PrintWriter writer = Utils.getWriter(resp);

		try
		{
			int lineItemID = Utils.parseInt(req.getParameter("lineitemid"), 0);
			int inspectionAreaID = Utils.parseInt(req.getParameter("inspectionareaid"), 0);

			if(inspectionAreaID > 0 && lineItemID > 0)
			{
				InspectionResult result = InspectionResult.valueOf(req.getParameter("result"));
				boolean insert = false;
				InspectionAreaLineItem inspectionAreaLineItem = InspectionAreaLineItems.get(inspectionAreaID, lineItemID);

				if(inspectionAreaLineItem == null)
				{
					inspectionAreaLineItem = new InspectionAreaLineItem();
					insert = true;
				}

				inspectionAreaLineItem.setInspectionArea(inspectionAreaID);
				inspectionAreaLineItem.setLineItemID(lineItemID);
				inspectionAreaLineItem.setResult(result.toString());

				if(insert)
				{
					inspectionAreaLineItem.insert();
				}
				else
				{
					inspectionAreaLineItem.update();
				}

				writer.print(result.toString().toLowerCase());
			}
			else if(inspectionAreaID > 0)
			{
				int topLevelID = Utils.parseInt(req.getParameter("toplevelid"), -1);

				int lineItemCount = LineItems.getCountForInspectionAreaTopLevel(inspectionAreaID, topLevelID);

				List<InspectionAreaLineItem> lineItems = InspectionAreaLineItems.getAllForTopLevelLineItem(inspectionAreaID, topLevelID);

				boolean allPassed = true;
				for (InspectionAreaLineItem lineItem : lineItems)
				{
					if(InspectionResult.FAIL.name().equalsIgnoreCase(lineItem.getResult()))
					{
						allPassed = false;
						break;
					}
				}

				String areaResult;
				if(!allPassed)
				{
					areaResult = "fail";
				}
				else if(allPassed && lineItemCount == lineItems.size())
				{
					areaResult = "pass";
				}
				else
				{
					areaResult = "inc";
				}

				writer.print(areaResult);
			}
		}
		catch (IllegalArgumentException e)
		{
			System.out.println(req.getParameter("result") + " is not a valid InspectionResult enum");
		}
	}
}
