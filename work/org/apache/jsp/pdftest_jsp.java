package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import pdf.GeneratePDF;

public final class pdftest_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n");
      out.write("\"http://www.w3.org/TR/html4/loose.dtd\">\n");
      out.write("\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("\t<title>JSP Page</title>\n");
      out.write("</head>\n");
      out.write("<body>\n");

	if ("Generate PDF".equals(request.getParameter("OK"))) {
		String reqURL = request.getRequestURL().toString();
		String reqQuery = "btn=false";
		String folderName = "JavaHunter";
		GeneratePDF obj = new GeneratePDF();
		//obj.genrateCmd(reqURL + "?" + reqQuery, folderName, "786");
	}

      out.write("\n");
      out.write("<form id=\"frmPDF\" action=\"\" name=\"frmPDF\" method=\"post\">\n");
      out.write("\t<table width=\"100%\" border=\"2\" style='background-color:#f2dbdb;color: #943634; line-height:20px;border:1px solid #943634;font-family: arial, verdana; font-size: 13px;'>\n");
      out.write("\t\t<tr>\n");
      out.write("\t\t\t<th style=\"background-color: #943634;color: #F2DBDB;\">First Name</th>\n");
      out.write("\t\t\t<th style=\"background-color: #943634;color: #F2DBDB;\">Last Name</th>\n");
      out.write("\t\t\t<th style=\"background-color: #943634;color: #F2DBDB;\">Salary</th>\n");
      out.write("\t\t\t<th style=\"background-color: #943634;color: #F2DBDB;\">Place</th>\n");
      out.write("\t\t</tr>\n");
      out.write("\t\t");
 for (int i = 1; i <= 10; i++) {
      out.write("\n");
      out.write("\t\t<tr>\n");
      out.write("\t\t\t<td align=\"center\">Taher</td>\n");
      out.write("\t\t\t<td align=\"center\">TINWALA</td>\n");
      out.write("\t\t\t<td align=\"center\">1000");
      out.print(i);
      out.write("</td>\n");
      out.write("\t\t\t<td align=\"center\">Ahmedabad-");
      out.print(i);
      out.write("</td>\n");
      out.write("\t\t</tr>\n");
      out.write("\t\t");
 }
      out.write("\n");
      out.write("\t</table>\n");
      out.write("\t");
if(!"false".equals(request.getParameter("btn"))){
      out.write("\n");
      out.write("\t<div align=\"center\">\n");
      out.write("\t\t<br/>\n");
      out.write("\t\t<input type=\"submit\" id=\"OK\" name=\"OK\" value=\"Generate PDF\"/>\n");
      out.write("\t</div>\n");
      out.write("\t");
}
      out.write("\n");
      out.write("</form>\n");
      out.write("</body>\n");
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
