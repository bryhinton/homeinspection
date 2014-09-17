package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write("<!DOCTYPE html>\r\n");
      out.write("<html manifest=\"cachefiles.appcache\">\r\n");
      out.write("\t<head>\r\n");
      out.write("\t\t<title>Home Inspection</title>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/jquery-1.7.1.min.js\"></script>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/database.js\"></script>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/inspection.js\"></script>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/utils/utils.js\"></script>\r\n");
      out.write("\t\t<!--[if lt IE 9]>\r\n");
      out.write("\t\t<script type=\"text/javascript\" src=\"scripts/flashcanvas.js\"></script>\r\n");
      out.write("\t\t<![endif]-->\r\n");
      out.write("\t\t<script src=\"scripts/jSignature.min.js\"></script>\r\n");
      out.write("\t\t<link rel=\"stylesheet\" href=\"stylesheets/main.css\">\r\n");
      out.write("\t\t<link rel=\"icon\" type=\"image/png\"  href=\"favicon.ico\">\r\n");
      out.write("\t\t<link rel=\"apple-touch-icon-precomposed\" href=\"favicon.ico\"/>\r\n");
      out.write("\t\t<meta name='viewport' content='width=device-width, minimum-scale=1.0, maximum-scale=1.0' >\r\n");
      out.write("\t\t<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\r\n");
      out.write("\t</head>\r\n");
      out.write("    <body onload='setTimeout(function() { window.scrollTo(0, 1) }, 100);'>\r\n");
      out.write("\t\t<div id=\"loading\">\r\n");
      out.write("\t\t\t<div class=\"background\"></div>\r\n");
      out.write("\t\t\t<div class=\"loading-text\">Loading...</div>\r\n");
      out.write("\t\t\t<button type=\"button\" onclick=\"showResetDialog();\" class=\"reset-button blue-gradient\">Reset</button>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"message\">\r\n");
      out.write("\t\t\t<div class=\"loading-text\"></div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"delete-inspection-dialog\" class=\"dialog\">\r\n");
      out.write("\t\t\t<span class=\"info-i\">i</span>\r\n");
      out.write("\t\t\t<div class=\"confirm-text\">\r\n");
      out.write("\t\t\t\tAre you sure you want to delete this inspection?\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"toggleMask(); deleteInspectionAndReload(null, false);\">Yes</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"toggleMask()\">Cancel</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"logout-dialog\" class=\"dialog\">\r\n");
      out.write("\t\t\t<span class=\"info-i\">i</span>\r\n");
      out.write("\t\t\t<div class=\"confirm-text\">\r\n");
      out.write("\t\t\t\tAre you sure you want to logout? This will delete this inspection.\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"deleteInspectionAndReload(null, true);\">Yes</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"toggleMask()\">Cancel</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"reset-dialog\" class=\"dialog\">\r\n");
      out.write("\t\t\t<span class=\"info-i\">i</span>\r\n");
      out.write("\t\t\t<div class=\"confirm-text\">\r\n");
      out.write("\t\t\t\tAre you sure you want to reset this device? This cannot be undone.\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"$('#reset-dialog').css('display', 'none'); logout();\">Yes</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"toggleMask()\">Cancel</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"unfinished-inspection-dialog\" class=\"dialog\">\r\n");
      out.write("\t\t\t<span class=\"info-i\">i</span>\r\n");
      out.write("\t\t\t<div class=\"confirm-text\">\r\n");
      out.write("\t\t\t\tYou have an unfinished inspection. Would you like to continue?\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<input type=\"hidden\" id=\"unfinished-id\">\r\n");
      out.write("\t\t\t<input type=\"hidden\" id=\"handle-unfinished-inspection\">\r\n");
      out.write("\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"loadInspection();\">Yes</div>\r\n");
      out.write("\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"sendUnfinishedInspection()\">Start New</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"login\" class=\"light-blue-gradient-background\">\r\n");
      out.write("\t\t\t<input type=\"hidden\" id=\"company-id\" name=\"companyID\" value=\"\">\r\n");
      out.write("\t\t\t<input type=\"hidden\" id=\"tech-id\" name=\"techID\" value=\"\">\r\n");
      out.write("\r\n");
      out.write("\t\t\t<div class='title'>\r\n");
      out.write("\t\t\t\t<div class=\"title-text\">Home Safety Inspection</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\r\n");
      out.write("\t\t\t<div class=\"area-name\">Login</div>\r\n");
      out.write("\t\t\t<div class=\"gap\"></div>\r\n");
      out.write("\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Username</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"username\" autocapitalize=\"off\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Password</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"password\" id=\"password\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'></div>\r\n");
      out.write("\t\t\t\t<div class='value'><button type=\"button\" class=\"table-button\" onclick=\"login()\">Login</button></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class=\"label\"></div>\r\n");
      out.write("\t\t\t\t<div class=\"value login-message\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<button type=\"button\" onclick=\"showResetDialog();\" class=\"reset-button login blue-gradient\">Reset</button>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"customer-info\" class=\"light-blue-gradient-background\">\r\n");
      out.write("\t\t\t<div class='title'>\r\n");
      out.write("\t\t\t\t<div class=\"title-text\">Home Safety Inspection</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\r\n");
      out.write("\t\t\t<div class=\"area-name\">Start New Inspection</div>\r\n");
      out.write("\t\t\t<div class=\"gap\"></div>\r\n");
      out.write("\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>First Name</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"first-name\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Last Name</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"last-name\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Address</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"address\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>City</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"city\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>State</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"state\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>ZIP Code</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"zip-code\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Phone Number</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"phone-number\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Email</div>\r\n");
      out.write("\t\t\t\t<div class='value'><input type=\"text\" id=\"email\" autocapitalize=\"off\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'>Technician</div>\r\n");
      out.write("\t\t\t\t<div class='value' id=\"tech-first-name\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div class=\"line-item\">\r\n");
      out.write("\t\t\t\t<div class='label'></div>\r\n");
      out.write("\t\t\t\t<div class='value'><button type=\"button\" id=\"start-inspection\" class=\"table-button\" onclick=\"validateCustomerInfo()\">Start Inspection</button></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"inspection\">\r\n");
      out.write("\t\t\t<input type=\"hidden\" id=\"inspection-id\">\r\n");
      out.write("\t\t\t<div id=\"add-area-dialog\" class=\"dialog\">\r\n");
      out.write("\t\t\t\t<div class=\"add-area-title\">\r\n");
      out.write("\t\t\t\t\tArea Name\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<input type=\"text\" class=\"dialog-text\" id=\"new-area-name\">\r\n");
      out.write("\t\t\t\t<input type=\"hidden\" id=\"new-area-id\">\r\n");
      out.write("\t\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"addArea();\">Add</div>\r\n");
      out.write("\t\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"toggleMask()\">Cancel</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div id=\"confirm-finish-dialog\" class=\"dialog\">\r\n");
      out.write("\t\t\t\t<span class=\"info-i\">i</span>\r\n");
      out.write("\t\t\t\t<div class=\"confirm-text\">\r\n");
      out.write("\t\t\t\t\tDo you want to finish and review this inspection?\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"reviewInspection();\">Yes</div>\r\n");
      out.write("\t\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"toggleMask()\">Cancel</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div id=\"line-items\" class=\"line-items light-blue-gradient-background\">\r\n");
      out.write("\t\t\t\t<div id=\"area-name\" class=\"area-name\">Select an area by tapping the Add button</div>\r\n");
      out.write("\t\t\t\t<div id=\"line-item-list\" class=\"line-item-list\">\r\n");
      out.write("\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div id=\"side-panel\" class=\"side-panel\">\r\n");
      out.write("\t\t\t\t<div class='title' onclick=\"showTitleMenu(event, true);\">\r\n");
      out.write("\t\t\t\t\t<div class=\"title-text\">Home Safety Inspection</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<ul class='title-menu'>\r\n");
      out.write("\t\t\t\t\t<li class='online'>Online</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"reviewCustomerInfo();\">Review Customer Info</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"showDeleteInspectionDialog();\">Delete Inspection</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"showLogoutDialog();\">Logout</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"showResetDialog();\">Reset Device</li>\r\n");
      out.write("\t\t\t\t</ul>\r\n");
      out.write("\t\t\t\t<div class='customer'>\r\n");
      out.write("\t\t\t\t\t<div class='name' id=\"inspection-name\">Place Holder</div>\r\n");
      out.write("\t\t\t\t\t<div class='date' id=\"inspection-date\">Oct. 12, 2012</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div id=\"areas\" class=\"areas\">\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div class='bottom-buttons'>\r\n");
      out.write("\t\t\t\t\t<div class='add-button' onclick=\"toggleHiddenListElement(event, 'add-list');\">\r\n");
      out.write("\t\t\t\t\t\tAdd\r\n");
      out.write("\t\t\t\t\t\t<ul id=\"add-list\" class=\"hidden-list\">\r\n");
      out.write("\t\t\t\t\t\t</ul>\r\n");
      out.write("\t\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t\t<div class='finish-button' onclick=\"confirmReview()\">\r\n");
      out.write("\t\t\t\t\t\tFinish\r\n");
      out.write("\t\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"review\">\r\n");
      out.write("\t\t\t<div id=\"quote\" class=\"dialog full\">\r\n");
      out.write("\t\t\t\t<div class=\"sub-mask\" id=\"quote-sub-mask\"></div>\r\n");
      out.write("\t\t\t\t<div id=\"signature-div\" class=\"dialog sub-dialog\">\r\n");
      out.write("\t\t\t\t\t<div class=\"add-area-title\">Customer Signature Required</div>\r\n");
      out.write("\t\t\t\t\t<div id=\"signature\"></div>\r\n");
      out.write("\t\t\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"hideSubMask(); saveSignature(); showEmailCustomerDialog();\">Confirm</div>\r\n");
      out.write("\t\t\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"$('#signature').jSignature('clear'); hideSubMask();\">Cancel</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div id=\"email-dialog\" class=\"dialog sub-dialog\">\r\n");
      out.write("\t\t\t\t\t<div class=\"add-area-title\">\r\n");
      out.write("\t\t\t\t\t\tEmail Address\r\n");
      out.write("\t\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t\t<input type=\"text\" class=\"dialog-text\" id=\"add-email-address\" autocapitalize=\"off\" onkeypress=\"enterNewEmail(event)\">\r\n");
      out.write("\t\t\t\t\t<input type=\"hidden\" id=\"email-address\">\r\n");
      out.write("\t\t\t\t\t<div class=\"info-text\"></div>\r\n");
      out.write("\t\t\t\t\t<button type=\"button\" class=\"add-email-button pass\" onclick=\"addEmail()\">Add Email</button>\r\n");
      out.write("\t\t\t\t\t<div class=\"existing-emails\" id=\"existing-emails\"></div>\r\n");
      out.write("\t\t\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"addEmail(); hideSubMask(); prepareSendInspection();\">Finish</div>\r\n");
      out.write("\t\t\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"hideSubMask();\">Cancel</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div class=\"add-area-title\">Quote</div>\r\n");
      out.write("\t\t\t\t<div class=\"quote-items\"></div>\r\n");
      out.write("\t\t\t\t<div class=\"totals\"></div>\r\n");
      out.write("\t\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"showSignature();\">Finish</div>\r\n");
      out.write("\t\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"toggleMask()\">Cancel</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div id=\"pricing-guide\" class=\"dialog large\">\r\n");
      out.write("\t\t\t\t<div class=\"sub-mask\" id=\"pricing-sub-mask\"></div>\r\n");
      out.write("\t\t\t\t<div class=\"dialog sub-dialog large\" id=\"review-lineitem-quote-dialog\">\r\n");
      out.write("\t\t\t\t\t<div id=\"review-lineitem-name\" class=\"add-area-title\">Review</div>\r\n");
      out.write("\t\t\t\t\t<div id=\"review-lineitem-quote\"></div>\r\n");
      out.write("\t\t\t\t\t<div class=\"dialog-submit dialog-button\" onclick=\"setQuoteItemCount(); hideSubMask(); toggleMask();\">Finish</div>\r\n");
      out.write("\t\t\t\t\t<div class=\"dialog-cancel dialog-button\" onclick=\"hideSubMask();\">Add More</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<input type=\"hidden\" id=\"pricing-id\">\r\n");
      out.write("\t\t\t\t<input type=\"hidden\" id=\"pricing-area\">\r\n");
      out.write("\t\t\t\t<input type=\"hidden\" id=\"pricing-lineitem-parent\">\r\n");
      out.write("\t\t\t\t<input type=\"hidden\" id=\"pricing-lineitem\">\r\n");
      out.write("\t\t\t\t<div class=\"add-area-title\">\r\n");
      out.write("\t\t\t\t\tPricing\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div class=\"loading\">We couldn't load your pricing.</div>\r\n");
      out.write("\t\t\t\t<div id=\"pricing-table\"></div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div id=\"review-line-items\" class=\"line-items light-blue-gradient-background\">\r\n");
      out.write("\t\t\t\t<div id=\"review-area-name\" class=\"area-name\">Select an area by tapping its name</div>\r\n");
      out.write("\t\t\t\t<div id=\"review-line-item-list\" class=\"line-item-list\">\r\n");
      out.write("\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t\t<div id=\"review-side-panel\" class=\"side-panel\">\r\n");
      out.write("\t\t\t\t<div class='title' onclick=\"showTitleMenu(event, false);\">\r\n");
      out.write("\t\t\t\t\t<div class=\"title-text\">Home Safety Inspection</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<ul class='title-menu'>\r\n");
      out.write("\t\t\t\t\t<li class='online'>Online</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"reviewCustomerInfo();\">Review Customer Info</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"showDeleteInspectionDialog();\">Delete Inspection</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"showLogoutDialog();\">Logout</li>\r\n");
      out.write("\t\t\t\t\t<li class=\"menu-item\" onclick=\"showResetDialog();\">Reset Device</li>\r\n");
      out.write("\t\t\t\t</ul>\r\n");
      out.write("\t\t\t\t<div class='customer'>\r\n");
      out.write("\t\t\t\t\t<div class='name' id=\"review-name\">Place Holder</div>\r\n");
      out.write("\t\t\t\t\t<div class='date' id=\"review-date\">Oct. 12, 2012</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div id=\"review-areas\" class=\"areas\">\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t<div class='bottom-buttons'>\r\n");
      out.write("\t\t\t\t\t<div class='add-button' onclick=\"$('#unfinished-id').val($('#inspection-id').val()); loadInspection();\">\r\n");
      out.write("\t\t\t\t\t\tBack\r\n");
      out.write("\t\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t\t<div class='finish-button' onclick=\"showQuote();\">\r\n");
      out.write("\t\t\t\t\t\tFinish\r\n");
      out.write("\t\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t<div id=\"debug\"></div>\r\n");
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
