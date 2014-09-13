package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import sqlrow.*;
import java.util.List;
import java.util.Calendar;
import java.util.Map;

public final class inspection_002dreview_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("<html>\r\n");
      out.write("\t<head>\r\n");
      out.write("\t\t<title>Home Inspection</title>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/jquery-1.7.1.min.js\"></script>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/inspection.js\"></script>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/database.js\"></script>\r\n");
      out.write("\t\t<link rel=\"stylesheet\" href=\"stylesheets/mobile.css\">\r\n");
      out.write("\t\t<meta name='viewport' content='width=device-width, minimum-scale=1.0, maximum-scale=1.0' >\r\n");
      out.write("\t</head>\r\n");
      out.write("\t<body onload='setTimeout(function() { window.scrollTo(0, 1) }, 100);'>\r\n");
      out.write("\t\t\r\n");
      out.write("\t\t");
 	String inspectionID = request.getParameter("id"); 
			Inspection inspection = Inspections.getInspection(Integer.parseInt(inspectionID));
		
      out.write("\r\n");
      out.write("\t\t<h1>");
      out.print( inspection.getFirstName() );
      out.write(' ');
      out.print( inspection.getLastName() );
      out.write("</h1>\r\n");
      out.write("\t\t");
 	Calendar date = Calendar.getInstance();
			date.setTime(inspection.getDate());
		
      out.write("\r\n");
      out.write("\t\t<h3>");
      out.print( date.get(Calendar.MONTH) + 1 );
      out.write('/');
      out.print( date.get(Calendar.DAY_OF_MONTH));
      out.write('/');
      out.print( date.get(Calendar.YEAR));
      out.write("</h3>\r\n");
      out.write("\t\t\r\n");
      out.write("\t\t<div class='popup' id='popup-repairs'>\r\n");
      out.write("\t\t\t<div class='close-X' onclick=\"$('#popup-repairs').css('display', 'none');\">X</div>\r\n");
      out.write("\t\t\t<table>\r\n");
      out.write("\t\t\t\t<tr><td class='repair-name'>Repair Task 1</td><td class='repair-price'>$149.99</td></tr>\r\n");
      out.write("\t\t\t\t<tr><td class='repair-name'>Repair Task 2<span class='recommended'>Recommended</span></td><td class='repair-price'>$189.99</td></tr>\r\n");
      out.write("\t\t\t\t<tr><td class='repair-name'>Repair Task 3</td><td class='repair-price'>$209.99</td></tr>\r\n");
      out.write("\t\t\t\t<tr><td class='see-more'>See More...</td></tr>\r\n");
      out.write("\t\t\t</table>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t\r\n");
      out.write("\t\t");
 	List<InspectionArea> areas = InspectionAreas.getAllForInspection(Utils.parseInt(inspectionID, 0));
		
			for(InspectionArea area : areas)
			{ 
      out.write("\r\n");
      out.write("\t\t\t\t<div class='area-name'>");
      out.print( area.getName() );
      out.write("</div>\r\n");
      out.write("\t\t\t\r\n");
      out.write("\t\t\t\t");
 	List<LineItem> topLevelLineItems = LineItems.getTopLevelLineItems(area.getAreaID(), false);
				
					for(LineItem topLevel : topLevelLineItems)
					{ 
      out.write("\r\n");
      out.write("\t\t\t\t\t\t<div class='top-level line-item' id='toplevel_");
      out.print( topLevel.getID() );
      out.write('\'');
      out.write('>');
      out.print( topLevel.getName() );
      out.write("<span class='result'></span></div>\r\n");
      out.write("\t\t\t\t\t\t<div class='indent'>\r\n");
      out.write("\t\t\t\t\t\t\t<table class='result-table'>\r\n");
      out.write("\t\t\t\t\t\t");
	List<LineItem> childItems = LineItems.getChildren(topLevel.getID(), false);
							Map<Integer, InspectionAreaLineItem> inspectionLineItems = InspectionAreaLineItems.getMapForArea(area.getID());
						
							for(LineItem child : childItems)
							{ 
								String result = inspectionLineItems.get(child.getID()) != null ? inspectionLineItems.get(child.getID()).getResult() : null;
								String comment = inspectionLineItems.get(child.getID()) != null ? inspectionLineItems.get(child.getID()).getComment() : null;
								
								if("FAIL".equals(result))
								{
							
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t\t<tr>\r\n");
      out.write("\t\t\t\t\t\t\t\t\t<td class='child line-item'>");
      out.print( child.getName() );
      out.write("</td>\r\n");
      out.write("\t\t\t\t\t\t\t\t\t<td class='result-cell ");
      out.print( "PASS".equals(result) ? " pass" : "FAIL".equals(result) ? " fail" : " na" );
      out.write("'>\r\n");
      out.write("\t\t\t\t\t\t\t\t\t\t");
      out.print( "PASS".equals(result) ? "&#x2713;" : "FAIL".equals(result) ? "X" : "n/a" );
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t\t\t\t<img src=\"images/blueArrow.gif\" class=\"arrow\" onclick=\"$('#popup-repairs').css('display', 'block');\">\r\n");
      out.write("\t\t\t\t\t\t\t\t\t</td>\r\n");
      out.write("\t\t\t\t\t\t\t\t</tr>\r\n");
      out.write("\t\t\t\t\t\t\t\t");
 	if(Utils.notEmpty(comment))
									{ 
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t\t\t\t<tr><td class='review-comment' colspan='2'>");
      out.print( comment );
      out.write("</td></tr>\r\n");
      out.write("\t\t\t\t\t\t\t\t");
	} 
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t");
	} 
      out.write("\r\n");
      out.write("\t\t\t\t\t\t");
	} 
      out.write("\r\n");
      out.write("\t\t\t\t\t\t\t</table>\r\n");
      out.write("\t\t\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t\t\t<script type=\"text/javascript\">getAreaResult('");
      out.print( area.getID() );
      out.write("', '");
      out.print( topLevel.getID() );
      out.write("');</script>\r\n");
      out.write("\t\t\t\t");
	} 
      out.write("\r\n");
      out.write("\t\t");
 	} 
      out.write("\r\n");
      out.write("\t</body>\r\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
