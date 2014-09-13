package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class company_002dlogin_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(1);
    _jspx_dependants.add("/header.jsp");
  }

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

      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("\t<head>\n");
      out.write("\t\t<title>Home Inspection</title>\n");
      out.write("\t\t<link rel=\"stylesheet\" href=\"stylesheets/controlpanel.css\">\n");
      out.write("\t</head>\n");
      out.write("\t<body>\n");
      out.write("\t\t");
      out.write("<div class='header-title'>Home Safety Inspection</div>\n");
      out.write("<div class='subheader' id='page-subheader'>Control Panel</div>");
      out.write("\n");
      out.write("\n");
      out.write("\t\t<form action=\"company-login\" method=\"post\" class='company-login'>\n");
      out.write("\t\t\t<div>\n");
      out.write("\t\t\t\t<label for=\"username\">Username:</label>\n");
      out.write("\t\t\t\t<input type=\"text\" name=\"username\" id=\"username\">\n");
      out.write("\t\t\t</div>\n");
      out.write("\n");
      out.write("\t\t\t<div>\n");
      out.write("\t\t\t\t<label for=\"password\">Password:</label>\n");
      out.write("\t\t\t\t<input type=\"password\" name=\"password\" id=\"password\">\n");
      out.write("\t\t\t</div>\n");
      out.write("\n");
      out.write("\t\t\t<button type=\"submit\">Login</button>\n");
      out.write("\n");
      out.write("\t\t\t");
 if("true".equals(request.getParameter("error"))) { 
      out.write("\n");
      out.write("\t\t\t\t<div class='error'>Login failed. Please try again.</div>\n");
      out.write("\t\t\t");
 } 
      out.write("\n");
      out.write("\t\t</form>\n");
      out.write("\t</body>\n");
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
