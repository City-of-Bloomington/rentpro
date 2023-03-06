package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/ReportMenu"})
public class ReportMenu extends TopServlet {

    final static long serialVersionUID = 880L;
    int maxlimit = 100; // limit on records
    // String allAgents = null;
    String allOwners = null;
    String allQueryOptions ="<option>\n<option>is<option>contains"+
	"<option>starts with<option>ends with</select>";
    String allDateOptions ="<option>\n<option>at<option>before"+
	"<option>after</select>";
    String bgcolor = Rental.bgcolor;
    static Logger logger = LogManager.getLogger(ReportMenu.class);
    //
    // Global sharable arrays
    //
    String [] zoneIdArr = null;
    String [] zoneArr = null;
    String [] pullIdArr = null;
    String [] pullArr = null;
    String [] typeIdArr = null;
    String [] typeArr = null;
    String yesNoOpts ="<option selected value=\"\">\n"+
	"<option value=Y>Yes"+
	"<option value=N>No"+
	"</select>";
    /**
     * Generates the form for the report menu options.
     *
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	boolean success = true;
	String name, value;
	String date_from="";
	String date_to="", message="";

	String byLocation="", byOwner="";
	String byProp="",byAll="",byPermit="";
	String registr_d="", issue_d="",cycle_d="",expire_d="",access="",
	    bill_d="",rec_d="",pull_d="",insp_d="";
	String pull_reason = "";
	//
	// Global temporary sharable arrays
	//
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("byLocation")){
		byLocation = value;
	    }
	    else if (name.equals("byOwner")){
		byOwner = value;
	    }
	    else if (name.equals("byPermit")){
		byPermit = value;
	    }
	    else if (name.equals("byProp")){
		byProp = value;
	    }
	    else if (name.equals("byAll")){
		byAll = value;
	    }
	    else if (name.equals("registr_d")){
		registr_d = value;
	    }
	    else if (name.equals("issue_d")){
		issue_d = value;
	    }
	    else if (name.equals("cycle_d")){
		cycle_d = value;
	    }
	    else if (name.equals("expire_d")){
		expire_d = value;
	    }
	    else if (name.equals("bill_d")){
		bill_d = value;
	    }
	    else if (name.equals("rec_d")){
		rec_d = value;
	    }
	    else if (name.equals("insp_d")){
		insp_d = value;
	    }
	    else{
		System.err.println("Unknown "+name+" "+value);
	    }
	}
	OwnerList agents = new OwnerList(debug);
	PullList pulls = new PullList(debug);
	InspectTypeList inspTypes = new InspectTypeList(debug);
	if(true){
	    agents.setAgentsOnly();
	    String back = agents.findAbbreviated();			
	    if(!back.equals("")){
		success = false;
		message += back;
	    }
	    back = pulls.find();
	    if(!back.equals("")){
		success = false;
		message += back;
	    }
	    back = inspTypes.find();
	    if(!back.equals("")){
		success = false;
		message += back;
	    }				
	}

	
       	//
	out.println("<html><head><title>Rental</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("                            ");
	out.println(" function moveToNext(item, size, nextItem, e){ ");
	out.println("  var keyCode = \" \";  ");
	out.println(" keyCode = (window.Event) ? e.which: e.keyCode;  ");
	//out.println("  alert(\" keycode = \"+keyCode);  ");
	out.println("  if(keyCode > 47 && keyCode < 58){  "); // only numbers
	out.println("  if(item.value.length > size-1){         ");
	out.println("  eval(nextItem.focus());      ");
	out.println("  }}}      ");
	out.println("  function checkDate(dd){		                   ");
	out.println("   if(dd.length == 0) return true;                "); 
	out.println("   else if(dd.length != 10){                      "); 
	out.println("      return false; }                             ");
	out.println("   else {                                         "); 
	out.println("   var m = dd.substring(0,2);                     "); 
	out.println("   var d = dd.substring(3,5);                     "); 
	out.println("   var y = dd.substring(6,10);                    "); 
	out.println(" if(!(dd.charAt(2) == \"/\" && dd.charAt(5)== \"/\")){ ");
	out.println("      return false; }                             ");
	out.println("   if(isNaN(m) || isNaN(d) || isNaN(y)){ ");
	out.println("      return false; }                             ");
	out.println("   if( !((m > 0 && m < 13) && (d > 0 && d <32) && ");
	out.println("    (y > 1900 && y < 2099))){                     "); 
	out.println("      return false; }                             ");
	out.println("       }                                          ");
	out.println("    return true;                                  ");
	out.println("    }                                             ");	
	//
	// validate form 
	out.println("  function validateForm(){		        ");
	out.println("  	 if ((document.myForm.date_from.value.length > 0)){ ");
	out.println("  if(!checkDate(document.myForm.date_from.value)){ ");  
	out.println("   document.myForm.date_from.focus();          ");
	out.println("     alert(\"Invalid date \");	          ");
	out.println("     return false;		       	       	  ");
	out.println("	}}               ");
	out.println(" if((document.myForm.date_to.value.length > 0)){ ");
	out.println(" if(!checkDate(document.myForm.date_to.value)){ ");     
	out.println("     alert(\"Invalid date \");	    ");
	out.println("   document.myForm.date_to.focus();      ");
	out.println("     return false;			    ");
	out.println("	}}                                  ");
       	out.println("  if(document.myForm.report[12].checked){          ");
	out.println("  var ss = document.myForm.agent.selectedIndex;    ");
	out.println("  if (ss < 1){                                     ");
	out.println("     alert(\"Need to select an agent \");          ");
	out.println("     return false;				       	");
	out.println("	}}                                              ");
       	out.println("  if(document.myForm.report[13].checked){          ");
	out.println("  var ss = document.myForm.name_num.value.length;  ");
	out.println("  if (ss == 0){                                    ");
	out.println("     alert(\"Need to enter an owner ID \");        ");
	out.println("     return false;				       	");
	out.println("	}}                                              ");
	//
	out.println("     return true;			            ");
	out.println("	}                                           ");
	out.println("	function checkPull(){                       ");
	out.println("    if(document.myForm.report[2].checked ||    ");
	out.println("      document.myForm.report[3].checked){      ");
	// out.println("     alert(\"Option changed \");            ");
	out.println("      document.myForm.req_d[6].checked = true; ");
	out.println(" }			    ");
	out.println("    else if(document.myForm.report[7].checked || ");
	out.println("      document.myForm.report[8].checked ||      ");
	out.println("      document.myForm.report[9].checked ||      ");	
	out.println("      document.myForm.report[10].checked){      ");
	// out.println("     alert(\"Option changed \");            ");
	out.println("      document.myForm.req_d[7].checked = true; ");
	out.println(" } else {			            ");
	out.println("      document.myForm.req_d[0].checked = true; ");
	out.println(" }}			            ");
	out.println(" function copyDates(){     ");
	out.println(" var dd = document.getElementById('date_from').value; ");
	out.println(" if(dd !== ''){        ");
	out.println("      document.getElementById('date_from2').value = dd;");
	out.println("   }			            ");
	out.println(" dd = document.getElementById('date_to').value; ");
	out.println(" if(dd !== ''){        ");
	out.println("      document.getElementById('date_to2').value = dd;");
	out.println("   }			            ");
	out.println(" }			            ");		
	out.println(" </script>				    ");
	out.println("              </head><body>            ");
	//
	Helper.writeTopMenu(out, url);	
	
	out.println("<center><h2>Report Options</h2>");
	if(!message.equals("")){
	    out.println(message);
	}
	out.println("<table align=center border width=80%>");
	//
	// form
	out.println("<tr><td><table width=100%>");
	out.println("<form name=\"myForm\" method=\"post\" action=\""+url+
		    "Report\" onSubmit=\"return validateForm()\">");
	//
	out.println("<tr><td><b>Select One of the Options</b></td></tr>");
	//
	String gStar = "<font color=\"green\" size=\"+2\">*</font>";
	out.println("<tr><td><ol>");
	//
	// Addresses
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"addr\">List of Addresses</li>");
	//
	// Invalid Addresses
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" checked "+
		    "value=\"inaddr\">Invalid Addresses</li>");
	//
	// Pull
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"pull\">Pulled Dates</li>");
	//
	// Pull reason
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"pullReas\">Pulled Dates w/Reasons");
	out.println("<select name=\"pull_reason\">");
	out.println("<option value=\"\">All</option>");
	for(Item item:pulls){
	    out.println("<option value=\""+item.getId()+"\">"+item); 
	}
	out.println("</select>");
	out.println("</li>");
	//
	// Agents
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"agents\">List of Agents</li>");
	//
	// Owners
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"owners\">List of Owners</li>");
	//
	// Owners and their properties
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"ownNprop\">List of Owners and their "+
		    "properties</li>");
	//
	// Cycle
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"cycle\">New Cycle "+
		    "(takes long time to finish)</li>");
	//
	// Inspections
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"inspect\">Inspections of type: ");
	out.println("<select name=inspection_type "+
		    "onChange=\"suggestName()\">");
	out.println("<option value=\"\">All</option>");
	for(Item item:inspTypes){
	    out.println("<option value=\""+item.getId()+
			"\">"+item);
	}
	out.println("</select> (May be combined with report on 11) </li>");
	//
	//
	// Inspection types
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"inspType\">Inspection Types</li>");
	// violations
	out.println("<li><input type=\"radio\" "+
		    "onClick=\"checkPull()\" "+
		    "name=\"report\" "+
		    "value=\"inspectViolation\">Inspections with more than ");
	out.println("<input name=\"violations\" value=\"\" size=\"3\" /> violations,");
	out.println(" Building Type: ");
	out.println("<select name=\"building_type\">");
	out.println("<option value=\"\">All</option>");
	for(String btype:Helper.buildTypes){
	    out.println("<option>"+btype+"</option>");
	}				
	out.println("</select></li>");
	//
	// Overdue bills
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"overdue\">"+gStar+"Overdue Bills</li>");
	//
	// Properties by agent
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"propAgent\">"+gStar+
		    "Properties Managed by Agent:");
	out.println("<select name=\"agent\">");
	out.println("<option value=\"\">All</option>");
	for(Owner agent:agents){
	    if(agent.isLegit())
		out.println("<option value=\""+agent.getId()+"\">"+agent.getFullName()+"</option>");
	}
	out.println("</select></li>");
	//
	// Properties by Owners
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"propOwn\">"+gStar+
		    "Properties Managed by Owner with ID:");
	out.println("<input name=name_num size=8 maxlength=>");
	out.println("</li>");
	//
	// List of permits without owner or agent
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"noOwnAgent\">New Format"+
		    " Properties with NO Owner nor Agent.");
	out.println("</li>");
	//
	// Owners/agents emails
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"emailList\" />Email List of ");
	out.println("<input type=\"radio\" name=\"who\" value=\"owner\" "+
		    "checked />Owners");
	out.println("<input type=\"radio\" name=\"who\" value=\"agent\" />"+
		    "Agents</li>");
				
	// Owners/agents emails
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"noEmail\" />Owners/Agents without Emails (You can narrow the list to permits that will expire withing date range below)</li>");
	//
	// List of permits without owner or agent
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"variancePermit\" />"+
		    "Properties with Variance.</li>");
	//
	// List of permits without pull date
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"noPullDate\" />"+
		    "Permits without pull date.</li>");
	//
	out.println("<li><input type=\"radio\" "+
		    "name=\"report\" "+
		    "value=\"oldUnitFormat\" />"+
		    "Permits with old structure/unit format.</li>");				
	//
	out.println("</ol>");
	out.println("</td></tr>");
	//
	// Dates
	out.println("<tr><td><b>Dates</td></tr>");
	out.println("<tr><td align=center>");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"registr_d\" "+
		    registr_d+" />Registered, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"issue_d\" "+
		    issue_d+" />Issued, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"cycle_d\" "+
		    cycle_d+" />Last Cycle, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"expire_d\" "+
		    expire_d+" />Expire, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"bill_d\" "+
		    bill_d+" />Billed, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"rec_d\" "+
		    rec_d+" />Received, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"pull_d\" "+
		    pull_d+" />Pulled, ");
	out.println("<input type=\"radio\" name=\"req_d\" value=\"insp_d\" "+
		    insp_d+" />Inspected ");
	out.println("</td></tr>");
	//
	// date for all
	out.println("<tr><td><center><table border>");
	out.println("<tr><td>&nbsp;</td>"+
		    "<td>From mm/dd/yyyy</td>");
	out.println("<td>To mm/dd/yyyy</td></tr>");
	//
	out.println("<tr><td align=\"right\">Date</td>");
	// from
	out.println("<td><input name=\"date_from\" value=\""+
		    date_from+"\" size=\"10\" maxlength=\"10\" "+
		    "id=\"date_from\" class=\"date\" /></td> ");
	//
	// To
	out.println("<td><input name=\"date_to\" "+
		    "id=\"date_to\" class=\"date\" "+					
		    " value=\""+date_to +"\" size=\"10\" maxlength=\"10\" />");
	out.println("</td></tr>");
	//
	out.println("</table>"); // end of date table
	out.println("</td></tr>");
				
	out.println("<tr><td><b>Sort by:</b>");
	out.println("<select name=\"sortby\">");
	out.println("<option value=\"\"></option>");
	out.println("<option value=\"ad.street_name\">Address</option>");
	out.println("<option value=\"pd.permit_expires\">Expire Date</option>");
	out.println("<option value=\"pd.registered_date\">Register Date</option>");
	out.println("</select>");
	out.println("</td></tr>"); 
	out.println("<tr><td><hr /></td></tr>");
	out.println("<tr><td align=right><input type=\"submit\" "+
		    "name=\"browse\" "+
		    "value=\"Submit\" /></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("</td></tr>");
	out.println("</form>");
	out.println("<tr><td><table>");
	out.println("<tr><td>");
	out.println("<form name=\"report\" method=\"post\" action=\""+url+"ReportMenu\" onsubmit=\"copyDates()\";>");
	out.println("<p>Select the report type from the list below.");
	out.println(" Note that these reports are data intensive. They may take long time to finish</p>");
	out.println("<ul>");
	out.println("<li>");
	out.println("<input type=\"radio\" name=\"report\" value=\"unitsInfo\" checked=\"checked\" />");
	out.println("Rental address, Owner, Agent, buildings, units, occupant load ");
	out.println(" (Permits with <input type=\"text\" name=\"units_from\" value=\"\" size=\"3\" /> or more units and/or Bedrooms = <input type=\"text\" name=\"beds_cnt\" value=\"\" size=\"2\" />)</li>");
	out.println("<li>");
	out.println("<input type=\"radio\" name=\"report\" value=\"unit_range\" />");
	out.println("Rental address, Owner Contact, Agent Contact, Buildings, Units, Occupant load, etc ");
	out.println(" (Permits with from <input type=\"text\" name=\"unit_from\" value=\"\" size=\"3\" /> units to <input type=\"text\" name=\"unit_to\" value=\"\" size=\"3\" />) units. For example 1-4 you would enter 1 and 4	");			
				
	out.println("<li>* ");
	out.println("<input type=\"radio\" name=\"report\" value=\"unitsOwnerInfo\" />");
	out.println("Rental address, Owners, Agent, w/Total buildings, Bedrooms, etc ");
	out.println("</li>");
	out.println("<li>");
	out.println("<input type=\"radio\" name=\"report\" value=\"inspections\" />");
	out.println("Inspections (use date range above) ");
	out.println("</li>");				
	out.println("<li>");		
	out.println("<input type=\"radio\" name=\"report\" value=\"ownersInfo\" />");		
	out.println("Owners info and rental addresses");	
	out.println("</li>");
	out.println("<li>");		
	out.println("<input type=\"radio\" name=\"report\" value=\"addrRange\" />");		
	out.println("Rental Owners info for address range");	
	out.println("</li>");
	out.println("<p>Enter Address Range</p>");
	out.println("<ul>");
	out.println("<li>");
	out.println("Street Dir<select name=\"stDir\">");
	out.println("<option value=\"\">All</option>");
	out.println("<option>E</option>");
	out.println("<option>N</option>");
	out.println("<option>S</option>");
	out.println("<option>W</option>");
	out.println("</select></li>");
	out.println("<li>Street Name<input name=\"stName\" value=\"\" size=\"10\" /></li>");
	out.println("<li>");
	out.println("Street Type<select name=\"stType\">");
	out.println(Rental.allStreetType);
	out.println("</li>");
	out.println("<li>Street Num (low)<input name=\"stNumLow\" value=\"\" size=\"4\" /></li>");
	out.println("<li>Street Num (high)<input name=\"stNumHigh\" value=\"\" size=\"4\" /></li>");
		
	out.println("</li>");
	out.println("</ul>");
	out.println("<p>Select the output format</p>");
	out.println("<ul>");	
	out.println("<li><input type=\"radio\" name=\"format\" value=\"html\" checked=\"checked\" />HTML </li>");
	out.println("<li><input type=\"radio\" name=\"format\" value=\"csv\" />CSV (Excel format)</li>");
	out.println("</ul>");
		
	out.println("<input type=\"hidden\" id=\"date_from2\" name=\"date_from\" value=\"\" />");
	out.println("<input type=\"hidden\" id=\"date_to2\" name=\"date_to\" value=\"\" />");
	out.println("<p align=\"right\">");
	out.println("<input type=\"submit\" value=\"Submit\" />");
	out.println("</p>");
	out.println("</form>");
	out.println("</td></tr>");	
	out.println("</table></td></tr>");
	out.println("</table>");
	out.println(gStar+
		    "<font color=green>Dates are not needed for these options</font>");
	out.println("<br>");
	//
	Helper.writeWebFooter(out, url);	
	//
	out.println("</script>");			
	out.print("</body></html>");
	out.close();

    }
    /**
     * Generates the report menu form.
     *
     * Note that the output of the request is processed in 
     * RentalBrowse
     * @param req
     * @param res
     * @see RentalBrowse
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean success = true;
	//
	PrintWriter out = null;			  
	out = res.getWriter();
	String name, value;
	String action="", message="";
	String date_from="",date_to="", format="csv";
	String dataType="new", report="", units_from="", beds_cnt="",
	    unit_from="", unit_to="";
	String stDir = "", stName="", stNumLow="", stNumHigh="", stType="";
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

       	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	   
	    if (name.equals("date_from")) {
		date_from = value;
	    }
	    else if (name.equals("date_to")) {
		date_to = value;
	    }
	    else if (name.equals("format")) {
		format = value;
	    }
	    else if (name.equals("dataType")) {
		dataType = value;
	    }
	    else if (name.equals("report")) {
		report = value;
	    }
	    else if (name.equals("units_from")) {
		units_from = value;
	    }
	    else if (name.equals("unit_from")) { // unit_range report
		unit_from = value;
	    }
	    else if (name.equals("unit_to")) { // unit_range report
		unit_to = value;
	    }						
	    else if (name.equals("beds_cnt")) {
		beds_cnt = value;
	    }						
	    else if (name.equals("stDir")) {
		stDir = value;
	    }
	    else if (name.equals("stName")) {
		stName = value.toUpperCase();
	    }
	    else if (name.equals("stNumLow")) {
		stNumLow = value;
	    }
	    else if (name.equals("stNumHigh")) {
		stNumHigh = value;
	    }
	    else if (name.equals("stType")) {
		stType = value;
	    }		
	}
	//
	// for special reports
	// report = "unitsOwnerInfo2";
	//
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		success = false;
		message += " could not connect to database";
		logger.error(message);
	    }
	}
	catch(Exception ex){
	    success = false;
	    message += " could not connect to database "+ex;
	    logger.error(message);
	}		
	// if the submit came from ReportMenu class
	//
	if(!success){
	    res.setContentType("text/html");
	    out.println("<h2>Error: Could not connect to Database</h2>");
	    out.println("<h3>"+message+"</h3>");
	    out.println("<h2>Check with ITS</h2>");
	    out.println("</body></html>");
	    return;
	}
	Calendar current_cal = Calendar.getInstance();
	int thisYear = current_cal.get(Calendar.YEAR);
	int expYear = 2020;
	String all = "";
	if(format.equals("csv")){
	    res.reset();
	    res.setHeader("Expires", "0");
	    res.setHeader("Cache-Control","must-revalidate, post-check=0, pre-check=0");
	    res.setHeader("Pragma", "public");
	    res.setHeader("Content-Disposition"," attachment; filename=rental.csv");										
	    res.setContentType("application/vnd.ms-excel");
	    if(report.equals("unitsInfo")){
		all = "\"ID\",\"Registered Date\",\"Expire Date\",\"Address\",\"Location ID\",\"Owners\",\"Owner Address\",\"Owner City\",\"Owner State\",\"Owner Zip\",\"Agent\",\"Agent Address\",\"Agent Email\",\"Agent Zip\",\"Buiding #\",\"Units\",\"Bedrooms per Unit\",\"Efficiency per Unit\",\"Occupant Load per Unit\"";
	    }
	    else if(report.equals("unit_range")){
		all = "\"ID\",\"Registered Date\",\"Expire Date\",\"Address\",\"Location ID\",\"Owners\",\"Owner Address\",\"Owner City\",\"Owner State\",\"Owner Zip\",\"Owner Emails\",\"Agent\",\"Agent Address\",\"Agent Email\",\"Agent Zip\",\"Agent Emails\",\"Buiding #\",\"Units\",\"Bedrooms per Unit\",\"Efficiency per Unit\",\"Occupant Load per Unit\"";
	    }						
	    else if(report.startsWith("unitsOwnerInfo")){
		all = "\"ID\",\"Registered Date\",\"Expire Date\",\"Status\",\"Address\",\"Location ID\",\"Owner\",\"Owner Address\",\"Owner City\",\"Owner State\",\"Owner Zip\",\"Agent\",\"Agent Address\",\"Agent Email\",\"Agent Zip\",\"Buidings\",\"Units\",\"Bedrooms\",\"Efficiency\",\"Occupants\"";
	    }			
	    else if(report.equals("ownersInfo")){
		all = "\"Owner Name\",\"Owner Address\",\"Rental Address\",\"Permit Expire Date\"";
	    }
	    else if(report.equals("inspections")){
		all = "\"Inspection ID\",\"Rental ID\",\"Address\",\"Building Type\",\"Inspection Date\",\"Inspection Type\",\"Compliance Date\",Violations\",\"Smoke Detectors\",\"Life Safety\",\"Inspection Duration (hrs)\",\"Inspected By\",\"Insp Time Status\"";

	    }
	    else{
		all = "\"Owner Name\",\"Owner Address\",\"Owner City, State Zipcode\",\"Owner Contact\",\"Rental Address\"";

	    }
	}
	else{
	    res.setContentType("text/html");
	    all = "<html><head></head><body>\n";			
	    if(report.equals("unitsInfo")){		
		all += "<table border=\"1\"><tr><th>ID</th>"+
		    "<td>Registered Date</td>"+
		    "<td>Expire Date</td>"+
		    "<th>Address</th>"+
		    "<th>Location ID</th>"+
		    "<th>Owners</th>"+
		    "<th>Owner Address</th>"+
		    "<th>Owner City</th>"+
		    "<th>Owner State</th>"+
		    "<th>Owner Zip</th>"+
		    "<th>Agent</th>"+
		    "<th>Agent Address</th>"+
                    "<th>Agent Email</th>"+
		    "<th>Agent Zip</th>"+					
		    "<th>Building #</th>"+
		    "<th>Units</th>"+
		    "<th>Bedrooms per Unit</th>"+
		    "<th>Efficiency per Unit</th>"+
		    "<th>Occ Load per Unit</th>"+
		    "</tr>\n";
	    }
	    else if(report.equals("unit_range")){		
		all += "<table border=\"1\"><tr><th>ID</th>"+
		    "<td>Registered Date</td>"+
		    "<td>Expire Date</td>"+
		    "<th>Address</th>"+
		    "<th>Location ID</th>"+
		    "<th>Owners</th>"+
		    "<th>Owner Address</th>"+
		    "<th>Owner City</th>"+
		    "<th>Owner State</th>"+
		    "<th>Owner Zip</th>"+
		    "<th>Owner Emails</th>"+
		    "<th>Agent</th>"+
		    "<th>Agent Address</th>"+
                    "<th>Agent Email</th>"+
		    "<th>Agent Zip</th>"+
		    "<th>Agent Emails</th>"+
		    "<th>Building #</th>"+
		    "<th>Units</th>"+
		    "<th>Bedrooms per Unit</th>"+
		    "<th>Efficiency per Unit</th>"+
		    "<th>Occ Load per Unit</th>"+
		    "</tr>\n";
	    }						
	    else if(report.startsWith("unitsOwnerInfo")){		
		all += "<table border=\"1\"><tr>"+
		    "<th>ID</th>"+
		    "<td>Registered Date</td>"+
		    "<td>Expire Date</td>"+
		    "<td>Status</td>"+
		    "<th>Address</th>"+
		    "<th>Location ID</th>"+
		    "<th>Owner</th>"+
		    "<th>Owner Address</th>"+
		    "<th>Owner City</th>"+
		    "<th>Owner State</th>"+
		    "<th>Owner Zip</th>"+
		    "<th>Agent</th>"+
		    "<th>Agent Address</th>"+
                    "<th>Agent Email</th>"+
		    "<th>Agent Zip</th>"+					
		    "<th>Buildings</th>"+
		    "<th>Units</th>"+
		    "<th>Bedrooms</th>"+
		    "<th>Efficiencies</th>"+
		    "<th>Occ Load</th>"+
		    "</tr>\n";
	    }			
	    else if(report.equals("ownersInfo")){
		all += "<table><tr><th>Owner Name</th>"+
		    "<td>Owner Address</td>"+
		    "<th>Owner City, State Zipcode</th>"+
		    "<th>Rental Address</th>"+
		    "<th>Permit Expire Date</th>"+
		    "</tr>\n";
								
	    }
	    else if(report.equals("inspections")){
		all += "<table>"+
		    "<tr>"+
		    "<td>Inspection ID</td>"+
		    "<td>Rental ID</td>"+
		    "<td>Address</td>"+
		    "<td>Building type</td>"+
		    "<td>Inspection Date</td>"+
		    "<td>Inspection Type</td>"+
		    "<td>Compliance Date</td>"+
		    "<td>Violations</td>"+
		    "<td>Smoke Detectors</td>"+
		    "<td>Life Safety</td>"+
		    "<td>Insp Duration (hrs)</td>"+
		    "<td>Inspected by</td>"+
		    "<td>Insp Time Status</td>"+
		    "</tr>";
	    }
	    else{
		all += "<table><tr><th>Owner Name</th>"+
		    "<td>Owner Address</td>"+
		    "<th>Owner City, State Zipcode</th>"+
		    "<th>Owner Contact</th>"+					
		    "<th>Rental Address</th>"+
		    "</tr>\n";
	    }
	}
	out.println(all);
	if(report.equals("unitsInfo")){
	    int units_fromInt = 0, beds_cntInt=0;;
	    String qq = "select pd.id "+
		" from registr pd, rental_structures rs ";
	    String qw = " where pd.id = rs.rid and "+
		" pd.permit_expires > to_date('01/01/"+expYear+"','mm/dd/yyyy') ";
	    if(!units_from.equals("")){
		qq += ", rental_units ru ";
		qw += " and rs.id = ru.sid and ru.units >= "+units_from;
		try{
		    units_fromInt = Integer.parseInt(units_from);
		}catch(Exception ex){}
	    }
	    if(!beds_cnt.equals("")){
		if(units_from.equals("")){
		    qq += ", rental_units ru ";
		    qw += " and rs.id = ru.sid ";
		}
		qw += " and ru.bedrooms = "+beds_cnt;
		try{
		    beds_cntInt = Integer.parseInt(beds_cnt);
		}catch(Exception ex){}								
	    }
	    // qw += " and rownum < 100 "; // for test purpose we limit 100
	    qq = qq + qw;
	    // System.err.println(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		List<Structure> structs = null;
		List<Unit> units = null;
		List<Address> addrs = null;
		List<Owner> owners = null;
		String prevId = "";
		int jj = 0;
		while(rs.next()){
		    all = "";
		    String row = "", nextRow ="";
		    String id = rs.getString(1);
		    if(id.equals(prevId)) continue;
		    prevId = id;
		    Rent rent = new Rent(id, true, debug);
		    addrs = rent.getAddresses();
		    structs = rent.getStructs();
		    owners = rent.getOwners();
		    Owner agent = rent.getAgent();
		    if(agent == null) agent = new Owner(debug);
		    if(owners == null || addrs == null) continue;
		    String str = rent.getRegistered_date();
		    if(str == null) str = "";
		    String str2 = rent.getPermit_expires();
		    if(str2 == null) str2 = "";					
		    if(format.equals("csv")){
			row += "\""+id+"\",";
			row += "\""+str+"\",";
			row += "\""+str2+"\",";
			nextRow = "\""+id+"\",\"\",\"\",\"\",";
			nextRow += "\"\",\"\",\"\",\"\",\"\",";
			nextRow += "\"\",\"\",\"\",\"\",\"\"";
		    }
		    else{
			row += "<tr><td>"+id+"</td>";
			row += "<td>"+str+"</td>";
			row += "<td>"+str2+"</td>";
			nextRow = "<tr><td>"+id+"</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td></tr>";
		    }
					
		    String ownNames = "", ownAddr="",ownCity="",
			ownState = "",ownZip = "";
		    for(Owner owner:owners){
			if(!ownNames.equals("")) ownNames += ", "; 
			ownNames += owner.getFullName();
						
			String oaddr = owner.getAddress();
			if(ownAddr.indexOf(oaddr) == -1){
			    if(!ownAddr.equals("")) ownAddr += ", ";
			    ownAddr += oaddr;
			}
			str = owner.getCity();
			if(!str.equals("") && ownCity.indexOf(str) == -1){
			    if(!ownCity.equals("")) ownCity += ", ";
			    ownCity += str;
			}
			str = owner.getState();
			if(!str.equals("") && ownState.indexOf(str) == -1){
			    if(!ownState.equals("")) ownState += ", ";
			    ownState += str;
			}
			str = owner.getZip();
			if(!str.equals("") && ownZip.indexOf(str) == -1){
			    if(!ownZip.equals("")) ownZip += ", ";
			    ownZip += str;
			}
		    }
		    if(addrs != null && addrs.size() > 0){
			if(format.equals("csv")){	
			    row += "\""+addrs.get(0).getAddress()+"\",";
			    row += "\""+addrs.get(0).getLocation_id()+"\",";
			}
			else{
			    row += "<td>"+addrs.get(0).getAddress()+"</td>";
			    row += "<td>"+addrs.get(0).getLocation_id()+"</td>";
			}
		    }
		    else{
			if(format.equals("csv"))	
			    row += "\"\",\"\",";
			else
			    row += "<td>&nbsp;</td><td>&nbsp;</td>";
		    }
		    if(format.equals("csv")){	
			row += "\""+ownNames+"\",";
			row += "\""+ownAddr+"\",";
			row += "\""+ownCity+"\",";
			row += "\""+ownState+"\",";
			row += "\""+ownZip+"\",";
		    }
		    else{
			row += "<td>&nbsp;"+ownNames+"</td>";
			row += "<td>&nbsp;"+ownAddr+"</td>";
			row += "<td>&nbsp;"+ownCity+"</td>";
			row += "<td>&nbsp;"+ownState+"</td>";
			row += "<td>&nbsp;"+ownZip+"</td>";
		    }
		    if(format.equals("csv")){	
			row += "\""+agent.getFullName()+"\",";
			row += "\""+agent.getAddress()+"\",";
			row += "\""+agent.getEmail()+"\",";
			row += "\""+agent.getZip()+"\",";
		    }
		    else{
			row += "<td>&nbsp;"+agent.getFullName()+"</td>";
			row += "<td>&nbsp;"+agent.getAddress()+"</td>";
			row += "<td>&nbsp;"+agent.getEmail()+"</td>";
			row += "<td>&nbsp;"+agent.getZip()+"</td>";
		    }						
		    int ttlUnits=0, ttlBeds = 0, ttlOccLod = 0, ttlEff = 0;
		    if(structs != null){
			jj = 1;
			int jb = 0; // building number
			for(Structure strc: structs){
			    units = strc.getUnits();
			    if(units != null){
				jb++;
				int size = units.size();
				for(Unit unt: units){
				    int u = unt.getUnits();
				    if(u < units_fromInt) continue;
				    int b = unt.getBedrooms();
				    if(b < beds_cntInt) continue;
				    int ol = unt.getOccLoad();
				    if(jj == 1) all = row;
				    else
					all = nextRow;
				    if(format.equals("csv")){
					all += "\""+jb+"\",";
					all += "\""+u+"\",";
					all += "\""+b+"\",";
					if(b == 0)
					    all += "\""+1+"\",";
					else
					    all += "\"0\",";
										
					all += "\""+ol+"\"";
				    }
				    else{
					all += "<td>"+jb+"</td>";
					all += "<td>"+u+"</td>";
					all += "<td>"+b+"</td>";
					if(b == 0)
					    all += "<td>"+1+"</td>";
					else
					    all += "<td>0</td>";
					all += "<td>"+ol+"</td></tr>";
										
				    }
				    ttlUnits += u;
				    ttlBeds += u*b;
				    ttlOccLod += u*ol;
				    if(b == 0){
					ttlEff += u;
				    }
				    out.println(all);
				    jj++;
				}
			    }
			}
		    }
		}
				
	    }catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}
	else if(report.equals("unit_range")){
	    int unit_fromInt = 0, unit_toInt=500; // large number
	    String qq = "select pd.id ";
	    String qf = " from registr pd, rental_structures rs ";
	    String qw = " where pd.id = rs.rid and "+
		" pd.permit_expires > to_date('01/01/"+expYear+"','mm/dd/yyyy') ";
	    if(!unit_from.equals("")){
		qf += ", rental_units ru ";
		qw += " and rs.id = ru.sid and ru.units >= "+unit_from;
		try{
		    unit_fromInt = Integer.parseInt(unit_from); // used later
		}catch(Exception ex){}
	    }
	    if(!unit_to.equals("")){
		if(unit_from.isEmpty()){
		    qf += ", rental_units ru ";
		    qw += " and rs.id = ru.sid ";
		}
		qw += " and ru.units <= "+unit_to;
		try{
		    unit_toInt = Integer.parseInt(unit_to); // used later
		}catch(Exception ex){}
	    }						
	    // qw += " and rownum < 100 "; // for test purpose we limit 100
	    qq = qq + qf + qw;
	    // System.err.println(qq);
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		List<Structure> structs = null;
		List<Unit> units = null;
		List<Address> addrs = null;
		List<Owner> owners = null;
		String prevId = "";
		int jj = 0;
		while(rs.next()){
		    all = "";
		    String row = "", nextRow ="";
		    String id = rs.getString(1);
		    if(id.equals(prevId)) continue;
		    prevId = id;
		    Rent rent = new Rent(id, true, debug);
		    addrs = rent.getAddresses();
		    structs = rent.getStructs();
		    owners = rent.getOwners();
		    Owner agent = rent.getAgent();
		    if(agent == null) agent = new Owner(debug);
		    if(owners == null || addrs == null) continue;
		    String reg_date = rent.getRegistered_date();
		    if(reg_date == null) reg_date = "";
		    String exp_date = rent.getPermit_expires();
		    if(exp_date == null) exp_date = "";					
		    if(format.equals("csv")){
			row += "\""+id+"\",";
			row += "\""+reg_date+"\",";
			row += "\""+exp_date+"\",";
			nextRow = "\""+id+"\",\"\",\"\",\"\",";
			nextRow += "\"\",\"\",\"\",\"\",\"\",";
			nextRow += "\"\",\"\",\"\",\"\",\"\",\"\",\"\"";
		    }
		    else{
			row += "<tr><td>"+id+"</td>";
			row += "<td>"+reg_date+"</td>";
			row += "<td>"+exp_date+"</td>";
			nextRow = "<tr><td>"+id+"</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td>";
			nextRow += "<td>&nbsp;</td><td>&nbsp;</td></tr>";
		    }
					
		    String ownNames = "", ownAddr="",ownCity="", str="",
			ownState = "",ownZip = "", ownEmail="", agentEmail="";
		    for(Owner owner:owners){
			if(!ownNames.equals("")) ownNames += ", "; 
			ownNames += owner.getFullName();
						
			String oaddr = owner.getAddress();
			if(ownAddr.indexOf(oaddr) == -1){
			    if(!ownAddr.equals("")) ownAddr += ", ";
			    ownAddr += oaddr;
			}
			str = owner.getCity();
			if(!str.equals("") && ownCity.indexOf(str) == -1){
			    if(!ownCity.equals("")) ownCity += ", ";
			    ownCity += str;
			}
			str = owner.getState();
			if(!str.equals("") && ownState.indexOf(str) == -1){
			    if(!ownState.equals("")) ownState += ", ";
			    ownState += str;
			}
			str = owner.getZip();
			if(!str.equals("") && ownZip.indexOf(str) == -1){
			    if(!ownZip.equals("")) ownZip += ", ";
			    ownZip += str;
			}
			if(owner.hasEmail()){
			    ownEmail = owner.getEmail();
			}
		    }
		    if(addrs != null && addrs.size() > 0){
			if(format.equals("csv")){	
			    row += "\""+addrs.get(0).getAddress()+"\",";
			    row += "\""+addrs.get(0).getLocation_id()+"\",";
			}
			else{
			    row += "<td>"+addrs.get(0).getAddress()+"</td>";
			    row += "<td>"+addrs.get(0).getLocation_id()+"</td>";
			}
		    }
		    else{
			if(format.equals("csv"))	
			    row += "\"\",\"\",";
			else
			    row += "<td>&nbsp;</td><td>&nbsp;</td>";
		    }
		    if(format.equals("csv")){	
			row += "\""+ownNames+"\",";
			row += "\""+ownAddr+"\",";
			row += "\""+ownCity+"\",";
			row += "\""+ownState+"\",";
			row += "\""+ownZip+"\",";
			row += "\""+ownEmail+"\",";
		    }
		    else{
			row += "<td>&nbsp;"+ownNames+"</td>";
			row += "<td>&nbsp;"+ownAddr+"</td>";
			row += "<td>&nbsp;"+ownCity+"</td>";
			row += "<td>&nbsp;"+ownState+"</td>";
			row += "<td>&nbsp;"+ownZip+"</td>";
			row += "<td>&nbsp;"+ownEmail+"</td>";
		    }
		    if(format.equals("csv")){	
			row += "\""+agent.getFullName()+"\",";
			row += "\""+agent.getAddress()+"\",";
			row += "\""+agent.getEmail()+"\",";
			row += "\""+agent.getZip()+"\",";

		    }
		    else{
			row += "<td>&nbsp;"+agent.getFullName()+"</td>";
			row += "<td>&nbsp;"+agent.getAddress()+"</td>";
			row += "<td>&nbsp;"+agent.getEmail()+"</td>";
			row += "<td>&nbsp;"+agent.getZip()+"</td>";

		    }						
		    int ttlUnits=0, ttlBeds = 0, ttlOccLod = 0, ttlEff = 0;
		    if(structs != null){
			jj = 1;
			int jb = 0; // building number
			for(Structure strc: structs){
			    units = strc.getUnits();
			    if(units != null){
				jb++;
				int size = units.size();
				for(Unit unt: units){
				    int u = unt.getUnits();
				    if(u < unit_fromInt) continue;
				    if(u > unit_toInt) continue;
				    int b = unt.getBedrooms();
				    int ol = unt.getOccLoad();
				    if(jj == 1) all = row;
				    else
					all = nextRow;
				    if(format.equals("csv")){
					all += "\""+jb+"\",";
					all += "\""+u+"\",";
					all += "\""+b+"\",";
					if(b == 0)
					    all += "\""+1+"\",";
					else
					    all += "\"0\",";
										
					all += "\""+ol+"\"";
				    }
				    else{
					all += "<td>"+jb+"</td>";
					all += "<td>"+u+"</td>";
					all += "<td>"+b+"</td>";
					if(b == 0)
					    all += "<td>"+1+"</td>";
					else
					    all += "<td>0</td>";
					all += "<td>"+ol+"</td></tr>";
										
				    }
				    ttlUnits += u;
				    ttlBeds += u*b;
				    ttlOccLod += u*ol;
				    if(b == 0){
					ttlEff += u;
				    }
				    out.println(all);
				    jj++;
				}
			    }
			}
		    }
		}
				
	    }catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}				
	else if(report.equals("unitsOwnerInfo")){
	    String qq = "select pd.id "+
		" from registr pd, rental_structures rs ";			
	    String qw = " where pd.id = rs.rid and "+
		" (pd.property_status = 'R' or pd.property_status='D') and "+
		" pd.permit_expires > to_date('01/01/"+expYear+"','mm/dd/yyyy') ";
	    if(!units_from.equals("")){
		qq += ", rental_units ru ";
		qw += " and rs.id = ru.sid and ru.units >= "+units_from;
	    }
	    if(!beds_cnt.equals("")){
		if(units_from.equals("")){
		    qq += ", rental_units ru ";
		    qw += " and rs.id = ru.sid ";
		}
		qw += " and ru.bedrooms = "+beds_cnt;
	    }						
	    // qw += " and rownum < 100 "; // for test purpose we limit 100
	    qq = qq +qw;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		List<Structure> structs = null;
		List<Unit> units = null;
		List<Address> addrs = null;
		List<Owner> owners = null;
		String prevId = "";
		int jj = 0;
		while(rs.next()){
		    all = "";
		    String row = "", part1 ="", part2="";
		    String id = rs.getString(1);
		    if(id.equals(prevId)) continue;
		    prevId = id;
		    Rent rent = new Rent(id, true, debug);
		    addrs = rent.getAddresses();
		    structs = rent.getStructs();
		    owners = rent.getOwners();
		    Owner agent = rent.getAgent();
		    PropStatus status = rent.getPropStatus();
		    if(agent == null) agent = new Owner(debug);
		    if(owners == null || addrs == null) continue;
		    String str = rent.getRegistered_date();
		    if(str == null) str = "";
		    String str2 = rent.getPermit_expires();
		    if(str2 == null) str2 = "";
		    String addrStr = "";
		    String addrId = "";
		    if(addrs != null && addrs.size() > 0){
			addrStr = addrs.get(0).getAddress();
			addrId = addrs.get(0).getLocation_id();
		    }
		    if(format.equals("csv")){
			part1 += "\""+id+"\",";
			part1 += "\""+str+"\",";
			part1 += "\""+str2+"\",";
			part1 += "\""+status+"\",";
			part1 += "\""+addrStr+"\",";
			part1 += "\""+addrId+"\",";
		    }
		    else{
			part1 += "<tr><td>"+id+"</td>";
			part1 += "<td>"+str+"</td>";
			part1 += "<td>"+str2+"</td>";
			part1 += "<td>"+status+"</td>";
			part1 += "<td>&nbsp;"+addrStr+"</td>";
			part1 += "<td>&nbsp;"+addrId+"</td>";
		    }
		    // start part2 
		    if(format.equals("csv")){	
			part2 = "\""+agent.getFullName()+"\",";
			part2 += "\""+agent.getAddress()+"\",";
			part2 += "\""+agent.getEmail()+"\",";
			part2 += "\""+agent.getZip()+"\",";
		    }
		    else{
			part2 = "<td>&nbsp;"+agent.getFullName()+"</td>";
			part2 += "<td>&nbsp;"+agent.getAddress()+"</td>";
			part2 += "<td>&nbsp;"+agent.getEmail()+"</td>";
			part2 += "<td>&nbsp;"+agent.getZip()+"</td>";
		    }
		    int ttlUnits=0, ttlBeds = 0, ttlOccLod = 0, ttlEff = 0;
		    jj = 0;
		    if(structs != null){
			jj = 1;
			for(Structure strc: structs){
			    units = strc.getUnits();
			    if(units != null){
				int size = units.size();
				for(Unit unt: units){
				    int u = unt.getUnits();
				    int b = unt.getBedrooms();
				    int ol = unt.getOccLoad();
				    ttlUnits += u;
				    ttlBeds += u*b;
				    ttlOccLod += u*ol;
				    if(b == 0){
					ttlEff += u;
				    }
				}
				jj++;
			    }
			}
		    }
		    if(format.equals("csv")){	
			part2 += "\""+jj+"\",";
			part2 += "\""+ttlUnits+"\",";
			part2 += "\""+ttlBeds+"\",";
			part2 += "\""+ttlEff+"\",";
			part2 += "\""+ttlOccLod+"\"";
		    }
		    else{
			part2 += "<td>&nbsp;"+jj+"</td>";
			part2 += "<td>&nbsp;"+ttlUnits+"</td>";
			part2 += "<td>&nbsp;"+ttlBeds+"</td>";
			part2 += "<td>&nbsp;"+ttlEff+"</td>";
			part2 += "<td>&nbsp;"+ttlOccLod+"</td></tr>";
		    }
					
		    String ownName = "", ownAddr="",ownCity="",
			ownState = "",ownZip = "";
		    for(Owner owner:owners){
			ownName = owner.getFullName();
			ownAddr = owner.getAddress();
			ownCity = owner.getCity();
			ownState = owner.getState();
			ownZip = owner.getZip();
			all = part1;
			if(format.equals("csv")){	
			    all += "\""+ownName+"\",";
			    all += "\""+ownAddr+"\",";
			    all += "\""+ownCity+"\",";
			    all += "\""+ownState+"\",";
			    all += "\""+ownZip+"\",";
			}
			else{
			    all += "<td>&nbsp;"+ownName+"</td>";
			    all += "<td>&nbsp;"+ownAddr+"</td>";
			    all += "<td>&nbsp;"+ownCity+"</td>";
			    all += "<td>&nbsp;"+ownState+"</td>";
			    all += "<td>&nbsp;"+ownZip+"</td>";
			}
			all += part2;
			out.println(all);								
		    }
		}
	    }catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}
	else if(report.equals("unitsOwnerInfo2")){
	    /**
	     * this is special for certain type of request
	     * cutomized to request needs and specifications
	     */
	    int[] loc_id ={
		14419,14421,14424,14425,14426,14427,14428,14429,
		14430,14432,14645,14647,14648,14649,14650,14653,
		14654,14658,14659,14660,14661,14662,14663,17162,
		17165,17170,17172,17173,17178,17179,17180,17189,
		17190,17191,17192,17194,17195,17196,17197,17198,
		17199,17200,17201,17202,17204,17206,17207,17208,
		17209,17210,17211,17212,17213,17214,17215,17216,
		17217,17219,17220,17221,17223,17224,17225,17226,
		17227,17228,17229,17230,17231,17232,17233,17235,
		17236,17239,17240,17245,17246,17247,17248,17249,

		17237,17238,17263,17264,17267,17268,17269,17270,
		17271,17272,17274,17275,17277,17278,19810,20691,
		20694,21135,21139,21151,21152,21321,21322,21326,
		21328,21329,21330,21331,21337,21338,21339,21340,
		21341,21342,21343,21344,21349,21363,21373,21374,
		22047,22532,22536,24435,24721,24722,24723,24724,
		24916,24917,24918,25022,25023,25024,25025,25026,
		25027,25277,25278,25279,25280,25281,25298,25299,
		25300,25301,25302,25303,25304,25305,25306,25307,25308,
		25309,25310,25311,25312,25313,25314,25315,25316,25323,
		25324,25325,25326,25327,25328,25339,25345,25346,
		25347,25350,25351,25352,25354,25355,25356,25357,
		25358,25359,25360,25361,25378,25379,25380,25381,
		25397,25422,25423,25424,25425,25447,25448,25449,
						
		25450,25451,25468,25469,25470,25471,25472,25500,
		25501,25502,25503,25504,25505,25506,25550,25551,
		25552,25553,25558,25559,25560,25583,25584,25585,
		25586,25587,25588,25589,25590,25591,25592,25593,
		25594,25595,25596,25597,25598,25599,25803,25804,
		25805,25806,25807,25808,25809,25810,25811,25813,
		25816,25819,25820,25821,25822,25823,25824,25825,
		25837,25838,25839,25840,25841,25842,25843,25844,
		25845,25846,25847,25848,25849,25850,25851,25852,
		25853,25854,25880,25881,25882,25988,25989,26092,
						
		26093,26094,26095,26116,26117,26118,26119,26120,
		26232,26233,26234,26235,26236,26239,26240,26261,
		26262,26263,26264,26270,26271,26272,26273,26274,
		26275,26276,26449,26456,26457,26458,26459,26460,
		26461,26462,26612,26613,26614,26766,26767,26768,
		26769,26770,26771,26942,26943,26944,26945,28244,
		28245,28246,28247,28248,28249,28250,28251,28252,
		28253,28254,28255,28256,28257,28258,28259,28260,
		28261,28262,28263,29246,29247,29248,29249,30436,
		30437,30438,30439,30451,30452,30453,30454,30455,
						
		30456,30457,30458,30460,30507,30618,30619,30620,
		30621,30622,30623,30624,30625,30626,31225,31255,
		31261,31263,31265,31267,31268,31269,31270,31937,
		31938,31939,31940,31941,32237,32238,32239,32240,
		32241,32242,32243,32244,32245,32246,32247,32248,
		32249,32250,32251,32252,32253,32254,32255,32256,
		32257,32258,32910,32912,32913,32915,32951,33600,
		33601,33736,33770,33771,33773,33774,34023,34028,
		34233,34314,34317,34390,34399,34400,34401,34498,
		34499,34911,35113,35231,35275,35276,35277,35724,
						
		35752,35753,35754,35755,35756,35757,35758,35761,
		35937,36716,36773,36782,36834,36843,36849,37013,
		37014,37015,37016,37017,37182,37183,37184,37238,
		37240,37241,37242,37243,37244,37245,37250,37251,
		37366,37467,37584,37585,37586,37587,37605,37606,
		37607,37608,37609,37610,37611,37612,37613,37614,
		37615,37616,37617,37618,37619,37620,37621,37622,
		37623,37624,37629,37766,37881,38089,38090,38091,
		38092,38093,38094,38095,38096,38097,38098,38099,
		38100,38101,38102,38103,38526,38731,38756,38768,
						
		38769,38770,38771,38772,38773,38774,38846,38847,
		38857,38868,38870,38871,38939,38964,39362,39373,
		39374,39388,39390,39484,39693,39694,40052,40053,
		40054,40055,40413,40414,40415,40416,40417,40418,
		40419,40420,40421,40422,40623,40656,40663,41003,
		41224,41240,41293,41408,41928,42120,42218,42219,
		42506,42507,4253,4255,4257,4258,4259,4260,4261,
		4262,4263,4265,4266,4269,4270,4271,4273,4274,
		42745,4275,4276,4277,4279,4280,4283,4284,4285,
		4286,4287,4289,4290,4292,4293,4294,4295,4296,
						
		4298,4299,4300,4301,4302,4303,4304,4305,4306,
		4307,4308,4309,4311,4312,4316,4317,4318,4319,
		4320,4321,4322,4323,4324,4325,4326,4328,4329,
		4330,4332,4336,4337,4338,4339,4340,4341,4342,
		4343,4344,4345,4348,4349,4350,4354,4355,4356,
		4357,4358,4359,4360,4361,4362,4367,4368,4369,
		4370,4371,4377,4379,4380,4381,4382,4383,4384,
		4385,4386,4387,4388,4390,4391,4394,4398,4399,
		4400,4401,4402,4403,4404,4405,4406,4407,4408,
		4409,4410,4411,4412,4413,4414,4415,4416,4417,
						
		4418,4419,4420,4421,4422,4423,4424,4425,4426,
		4427,44273,4428,4429,4430,4431,4432,4433,4434,
		4435,4436,4437,4438,44481,44482,44483,44484,
		44485,44486,44487,44488,44489,44490,44491,44492,
		44493,44494,4451,4452,4453,4454,4455,44557,
		4456,4457,4458,44598,44599,44600,44601,44602,
		44603,4461,44898,44965,45366,45655,46042,46043,
		46044,46045,46046,46147,46400,46600,46668,46669,
		46670,46671,46672,46673,46980,47835,48323,48743,
		48744,48745,49413,49414,49415,49416,49613,4962,
						
		4972,4976,4977,4978,4979,4980,4981,4984,
		4985,4986,4987,4988,4989,4994,4995,4997,
		4998,4999,5000,5003,5006,5008,5011,5012,
		5013,5014,5015,5016,5018,5019,5021,5022,
		5023,5024,5025,5026,5027,5028,5029,5030,
		5031,5032,5033,5034,50413,50535,5080,50898,
		50993,51333,51334,51335,51336,5139,5140,5141,
		5142,5143,5144,5145,5146,5147,5148,5185,
		52173,52174,52175,52176,52177,52573,52574,52575,
		52576,52577,52578,52579,52580,52581,52582,52583};

	    int[] loc_id2 = { 
		52584,52585,52673,52773,52774,52775,52776,52777,
		52778,52813,52814,52815,52816,52817,52818,52819,
		52820,52821,52822,52823,52824,53316,53317,53375,
		53376,53377,53378,53379,53380,53381,53382,53383,
		53435,53436,53437,53438,53439,53557,53701,53702,
		53713,53714,53715,53716,53717,53718,53993,53994,
		54035,54036,54037,54038,54039,54040,54098,54099,
		54100,54101,54102,54103,54104,54105,54106,54107,
		54108,54109,54110,54111,54112,54113,54114,54115,
		54116,54117,54118,54119,54120,54121,54122,54123,
						
		54124,54125,54126,54127,54128,54129,54130,54131,
		54132,54133,54134,54135,54136,54137,54138,54139,
		54140,54141,54142,54143,54144,54145,54146,54147,
		54148,54149,54150,54151,54152,54153,54154,54155,
		54156,54157,54158,54159,54160,54161,54162,54163,
		54164,54165,54166,54533,54534,54535,54536,54537,
		54538,54539,54540,54541,54542,54543,54544,54545,
		54546,54547,54548,54549,54550,54551,54552,54553,
		54554,54555,54793,54794,54795,54796,54797,54798,
		55073,55074,55075,55076,55077,55078,55079,55080,
						
		55081,55082,55083,55084,55085,55086,55087,55088,
		55089,55090,55091,55092,55093,55094,55095,55096,
		55097,55098,55099,55100,55101,55102,55103,55104,
		55105,55106,55107,55108,55109,55110,55111,55112,
		55113,55114,55115,55116,55117,55118,55119,55120,
		55121,55122,55123,55124,55125,55126,55127,55128,
		55129,55130,55131,55132,55133,55134,55135,55136,
		55137,55138,55139,55167,55168,55169,55170,55171,
		55172,55173,55174,55175,55176,55177,55178,55179,
		55180,55181,55182,55183,55184,55185,55186,55187,
						
		55188,55189,55190,55191,55192,55193,55194,55195,
		55196,55197,55198,55199,55200,55201,55202,55203,
		55204,55205,55206,55207,55208,55209,55210,55211,
		55212,55213,55214,55215,55216,55217,55218,55219,
		55220,55221,55222,55223,55224,55225,55226,55227,
		55228,55229,55230,55231,55232,55233,55234,55235,
		55236,55237,55238,55239,55240,55241,55242,55243,
		55244,55245,55246,55247,55248,55249,55250,55251,
		55252,55253,55254,55255,55256,55257,55258,55259,
		55260,55261,55262,55263,55264,55265,55266,55267,
						
		55268,55269,55270,55271,55272,55273,55274,55275,
		55276,55277,55278,55279,55280,55281,55282,55283,
		55284,55285,55286,55287,55288,55289,55290,55291,
		55292,55293,55294,55295,55296,55297,55298,55299,
		55300,55301,55302,55303,55304,55305,55306,55307,
		55308,55309,55310,55311,55312,55313,55314,55315,
		55316,55317,55318,55319,55320,55321,55322,55323,
		55324,55325,55326,55327,55328,55329,55330,55331,
		55332,55333,55334,55335,55336,55337,55338,55339,
		55340,55341,55342,55343,55344,55345,55346,55347,
						
		55348,55349,55350,55351,55352,55353,55354,55355,
		55356,55357,55358,55359,55360,55361,55362,55363,
		55364,55365,55366,55367,55368,55369,55370,55371,
		55372,55373,55374,55375,55376,55377,55378,55379,
		55380,55381,55382,55383,55384,55385,55386,55387,
		55388,55389,55390,55391,55392,55393,55394,55395,
		55396,55397,55413,55414,55415,55553,55972,56974,
		57113,57114,57115,57116,57117,57118,57119,57414,
		57453,57733,57734,57735,57736,57737,57738,57739,
		57740,57741,57742,57743,57744,57745,57746,57953,
						
		57954,57955,58193,58194,58195,58196,58197,58198,
		58199,58200,58201,58202,58203,58213,58413,58453,
		58516,58518,58519,58520,58654,58655,58656,58657,
		58658,58659,58660,58661,58662,58663,58664,58665,
		58666,58667,58668,58669,58753,58754,58755,58756,
		58757,58758,58759,58760,58761,58762,58763,58764,
		58765,58766,58767,58768,58769,58770,58771,58772,
		58773,58774,58775,58776,58777,58778,58793,58794,
		58795,58796,58797,58798,58799,58800,58801,58802,
		58803,58804,58805,58806,58807,58808,58809,58810,
						
		58811,58812,58813,58814,58815,58816,58817,58818,
		59113,59114,59193,59194,59213,59333,59334,59373,
		59613,60053,60054,60055,60056,60057,60233,60234,
		60235,60236,60237,60238,60593,60594,60595,60596,
		60597,60598,60599,60600,60601,60602,60603,60604,
		60605,60606,60607,60608,60609,60610,60611,60612,
		60613,60614,60615,60616,60617,60618,60619,60620,
		60621,60622,60623,60624,60625,60626,60627,60628,
		60629,60630,60631,60632,60633,60634,60635,61393,
		61394,61395,61396,61397,61398,61399,61400,61401,

		61402,61403,61404,61405,61406,61407,61408,61409,
		61410,61411,61412,61413,61713,61813,61814,61815,
		61816,61817,61818,61819,61820,61893,61894,61895,
		62168,62416,62497,62498,62499,62500,62501,62502,
		62503,62504,62505,62506,62507,62508,62509,62510,
		62511,62512,62513,62514,62515,62516,62517,62518,
		62519,62520,62521,62522,62523,62524,62525,62526,
		62527,62528,62529,62530,62531,62532,62533,62534,
		62535,62536,62575,62576,62635,62636,62637,62638,
		62639,63616,63619,63620,63621,64257,65335,65595};						
	    String qq = "select pd.id "+
		" from registr pd, address2 ad ";			
	    String qw = " where ad.registr_id = pd.id and "+
		" pd.permit_expires > to_date('01/01/"+expYear+"','mm/dd/yyyy') ";
	    qw += " and ad.location_id in ";
	    String set = "(";
	    for(int xx:loc_id){
		set += xx;
		set += ",";
	    }
	    set += "99999)";
	    String qAll = qq + qw + set;
	    qAll += " union ";
	    set = "(";
	    for(int xx:loc_id2){
		set += xx;
		set += ",";
	    }
	    set += "99999)";
	    qAll += qq + qw + set;						
	    if(debug){
		logger.debug(qAll);
	    }
	    try{
		rs = stmt.executeQuery(qAll);
		List<Structure> structs = null;
		List<Unit> units = null;
		List<Address> addrs = null;
		List<Owner> owners = null;
		String prevId = "";
		int jj = 0;
		while(rs.next()){
		    all = "";
		    String row = "", part1 ="", part2="";
		    String id = rs.getString(1);
		    if(id.equals(prevId)) continue;
		    prevId = id;
		    Rent rent = new Rent(id, true, debug);
		    addrs = rent.getAddresses();
		    structs = rent.getStructs();
		    owners = rent.getOwners();
		    Owner agent = rent.getAgent();
		    PropStatus status = rent.getPropStatus();
		    if(agent == null) agent = new Owner(debug);
		    if(owners == null || addrs == null) continue;
		    String str = rent.getRegistered_date();
		    if(str == null) str = "";
		    String str2 = rent.getPermit_expires();
		    if(str2 == null) str2 = "";
		    String addrStr = "";
		    String addrId = "";
		    if(addrs != null && addrs.size() > 0){
			addrStr = addrs.get(0).getAddress();
			addrId = addrs.get(0).getLocation_id();
		    }
		    if(format.equals("csv")){
			part1 += "\""+id+"\",";
			part1 += "\""+str+"\",";
			part1 += "\""+str2+"\",";
			part1 += "\""+status+"\",";
			part1 += "\""+addrStr+"\",";
			part1 += "\""+addrId+"\",";
		    }
		    else{
			part1 += "<tr><td>"+id+"</td>";
			part1 += "<td>"+str+"</td>";
			part1 += "<td>"+str2+"</td>";
			part1 += "<td>"+status+"</td>";
			part1 += "<td>&nbsp;"+addrStr+"</td>";
			part1 += "<td>&nbsp;"+addrId+"</td>";
		    }
		    // start part2 
		    if(format.equals("csv")){	
			part2 = "\""+agent.getFullName()+"\",";
			part2 += "\""+agent.getAddress()+"\",";
			part2 += "\""+agent.getEmail()+"\",";
			part2 += "\""+agent.getZip()+"\",";

		    }
		    else{
			part2 = "<td>&nbsp;"+agent.getFullName()+"</td>";
			part2 += "<td>&nbsp;"+agent.getAddress()+"</td>";
			part2 += "<td>&nbsp;"+agent.getEmail()+"</td>";
			part2 += "<td>&nbsp;"+agent.getZip()+"</td>";												
		    }
		    int ttlUnits=0, ttlBeds = 0, ttlOccLod = 0, ttlEff = 0;
		    jj = 0;
		    if(structs != null){
			jj = 1;
			for(Structure strc: structs){
			    units = strc.getUnits();
			    if(units != null){
				int size = units.size();
				for(Unit unt: units){
				    int u = unt.getUnits();
				    int b = unt.getBedrooms();
				    int ol = unt.getOccLoad();
				    ttlUnits += u;
				    ttlBeds += u*b;
				    ttlOccLod += u*ol;
				    if(b == 0){
					ttlEff += u;
				    }
				}
				jj++;
			    }
			}
		    }
		    if(format.equals("csv")){	
			part2 += "\""+jj+"\",";
			part2 += "\""+ttlUnits+"\",";
			part2 += "\""+ttlBeds+"\",";
			part2 += "\""+ttlEff+"\",";
			part2 += "\""+ttlOccLod+"\"";
		    }
		    else{
			part2 += "<td>&nbsp;"+jj+"</td>";
			part2 += "<td>&nbsp;"+ttlUnits+"</td>";
			part2 += "<td>&nbsp;"+ttlBeds+"</td>";
			part2 += "<td>&nbsp;"+ttlEff+"</td>";
			part2 += "<td>&nbsp;"+ttlOccLod+"</td></tr>";
		    }
					
		    String ownName = "", ownAddr="",ownCity="",
			ownState = "",ownZip = "";
		    for(Owner owner:owners){
			ownName = owner.getFullName();
			ownAddr = owner.getAddress();
			ownCity = owner.getCity();
			ownState = owner.getState();
			ownZip = owner.getZip();
			all = part1;
			if(format.equals("csv")){	
			    all += "\""+ownName+"\",";
			    all += "\""+ownAddr+"\",";
			    all += "\""+ownCity+"\",";
			    all += "\""+ownState+"\",";
			    all += "\""+ownZip+"\",";
			}
			else{
			    all += "<td>&nbsp;"+ownName+"</td>";
			    all += "<td>&nbsp;"+ownAddr+"</td>";
			    all += "<td>&nbsp;"+ownCity+"</td>";
			    all += "<td>&nbsp;"+ownState+"</td>";
			    all += "<td>&nbsp;"+ownZip+"</td>";
			}
			all += part2;
			out.println(all);								
		    }
		}
	    }catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}				
	else if(report.equals("ownersInfo")){
	    String qq = "select pd.id  "; 
	    qq += " from registr pd, regid_name rn ";
	    qq += " where pd.id=rn.id and rn.name_num > 0 "+
		" order by pd.id ";
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		List<Address> addrs = null;
		List<Owner> owners = null;
		String prevId = "";
		while(rs.next()){
		    all = "";
		    String id = rs.getString(1);
		    if(id.equals(prevId)) continue;
		    prevId = id; // to avoid duplicate
		    Rent rent = new Rent(id, true, debug);
		    addrs = rent.getAddresses();
		    owners = rent.getOwners();
		    if(owners == null || addrs == null) continue;
		    for(Owner owner:owners){
			if(format.equals("csv")){
			    for(Address addr:addrs){
				if(addr != null){
				    all += "\""+owner.getFullName()+"\",";
				    all += "\""+owner.getAddress()+"\",";
				    all += "\""+owner.getCityStateZip()+"\",";
				    all += "\""+addr.getAddress()+"\",";
				    all += "\""+rent.getPermit_expires()+"\"\n";
				}
			    }
			}
			else{
			    for(Address addr:addrs){
				if(addr != null){		
				    all += "<tr><td>"+owner.getFullName()+"</td>";
				    all += "<td>"+owner.getAddress()+"</td>";
				    all += "<td>"+owner.getCityStateZip()+"</td>";
				    all += "<td>"+addr.getAddress()+"</td>";
				    all += "<td>"+rent.getPermit_expires()+"</td>";
				    all += "</tr>\n";
				}
			    }					
			}
		    }
		    out.print(all);
		}
	    }catch(Exception ex){
		logger.error(ex+":"+qq);
	    }
	}
        else if(report.equals("inspections")){
	    String qq = "select insp.insp_id,insp.id,"+
		" initcap(ad.street_num||' '||ad.street_dir||' '"+
		"||ad.street_name"+
		"||' '||ad.street_type||' '||ad.sud_type||' '||"+
		"ad.sud_num),"+
		"pd.building_type,"+ 
		"to_char(insp.inspection_date,'mm/dd/yyyy'),"+
		"it.insp_desc,to_char(insp.compliance_date,'mm/dd/yyyy'),"+
		"insp.violations,insp.smook_detectors,insp.life_safety,"+ // 7
		"insp.time_spent,"+
		"initcap(ds.name),insp.time_status";
	    String qf = " from inspections insp,registr pd,inspection_types it,"+
		" inspectors ds,address2 ad ";
	    String qw =" where insp.id=pd.id and pd.id=ad.registr_id and "+
		"it.insp_type=insp.inspection_type and ds.initials=insp.inspected_by and pd.inactive is null ";
	    if(!date_from.isEmpty()){
		qw += " and insp.inspection_date >= to_date('"+date_from+"','mm/dd/yyyy')";
	    }
	    if(!date_to.isEmpty()){
		qw +=" and insp.inspection_date <= to_date('"+date_to+"','mm/dd/yyyy')";
	    }
	    qw += " order by insp.insp_id desc ";
	    qq += qf+qw;
	    // System.err.println(qq);
	    try{
		rs = stmt.executeQuery(qq);
		all = "";
		while(rs.next()){
		    int jj = 1;
		    String inspId=rs.getString(jj++);
		    String regId = rs.getString(jj++);
		    String addr = rs.getString(jj++);
		    String buildType = rs.getString(jj++);
		    String inspDate = rs.getString(jj++);
		    String inspType = rs.getString(jj++);
		    String compDate = rs.getString(jj++);
		    String viols = rs.getString(jj++);
		    String smokes = rs.getString(jj++);
		    String life = rs.getString(jj++);
		    String time = rs.getString(jj++);
		    String by = rs.getString(jj++);
		    String status = rs.getString(jj++);
		    if(format.equals("csv")){
			String row = "";
			if(addr == null) addr = "";
			if(buildType == null) buildType = "";
			if(inspDate == null) inspDate = "";
			if(inspType == null) inspType = "";
			if(compDate == null) compDate = "";
			if(viols == null) viols = "";
			if(smokes == null) smokes = "";
			if(life == null) life = "";
			if(time == null) time = "";
			if(by == null) by = "";
			if(status == null) status = "";
			all += "\""+inspId+"\",\""+regId+"\",\""+addr+"\",";
			all += "\""+buildType+"\",\""+inspDate+"\",";
			all += "\""+inspType+"\",\""+compDate+"\",";
			all += "\""+viols+"\",\""+smokes+"\",";
			all += "\""+life+"\",\""+time+"\",";
			all += "\""+by+"\",\""+status+"\"\n";
		    }
		    else{
			String row = "";
			if(addr == null) addr = "&nbsp;";
			if(buildType == null) buildType = "&nbsp;";
			if(inspDate == null) inspDate = "&nbsp;";
			if(inspType == null) inspType = "&nbsp;";
			if(compDate == null) compDate = "&nbsp;";
			if(viols == null) viols = "&nbsp;";
			if(smokes == null) smokes = "&nbsp:";
			if(life == null) life = "&nbsp;";
			if(time == null) time = "&nbsp;";
			if(by == null) by = "&nbsp;";
			all += "<tr><td>"+inspId+"</td><td>"+regId+"</td>";
			all += "<td>"+addr+"</td>";
			all += "<td>"+buildType+"</td><td>"+inspDate+"</td>";
			all += "<td>"+inspType+"</td><td>"+compDate+"</td>";
			all += "<td>"+viols+"</td><td>"+smokes+"</td>";
			all += "<td>"+life+"</td><td>"+time+"</td>";
			all += "<td>"+by+"</td>";
			all += "<td>"+status+"</td>";
			all += "</tr>\n";												
		    }
		}
		out.println(all);

	    }catch(Exception ex){
		System.err.println(ex+" "+qq);
	    }
	}
	else{ // for address ranges
	    String[][] strInfo = {{"100","1889","E","10TH",""},
				  {"100","199","W","10TH",""},
				  {"200","1199","E","11TH",""},
				  {"100","999","W","11TH",""},
				  {"500","1199","E","13TH",""},
								  
				  {"100","1099","E","14TH",""},
				  {"100","199","W","14TH",""},
				  {"100","999","E","15TH",""},
				  {"100","199","W","15TH",""},
				  {"100","499","E","16TH",""}, // 10
								  
				  {"100","1599","E","17TH",""},
				  {"100","199","W","17TH",""},
				  {"300","499","E","18TH",""},
				  {"100","499","E","19TH",""},
				  {"200","425","E","20TH",""},
								  
				  {"100","1999","E","2ND",""},
				  {"100","197","W","2ND",""},
				  {"100","2016","E","3RD",""},
				  {"100","599","W","3RD",""},
				  {"100","199","W","4TH",""}, // 20
								  
				  {"100","599","E","6TH",""},
				  {"100","199","W","6TH",""},
				  {"100","1999","E","7TH",""},
				  {"100","199","W","7TH",""},
				  {"100","999","E","8TH",""},
								  
				  {"100","199","W","8TH",""},
				  {"100","899","E","9TH",""},
				  {"100","199","W","9TH",""},
				  {"400","499","E","ALICE",""},
				  {"500","599","S","ANITA",""}, // 30
								  
				  {"300","520","S","ARBUTUS","DR"},
				  {"300","1999","E","ATWATER","AVE"},
				  {"100","599","S","BALLANTINE","RD"},
				  {"150","450","E","BROWNSTONE","DR"},
				  {"300","599","N","CAMPBELL",""},
								  
				  {"300","499","S","CLIFTON","AVE"},
				  {"100","1799","N","COLLEGE","AVE"},
				  {"100","499","S","COLLEGE","AVE"},
				  {"100","1099","E","COTTAGE GROVE","AVE"},
				  {"100","1699","N","DUNN",""}, // 40
								  
				  {"100","499","S","DUNN",""},
				  {"300","599","S","EASTSIDE","AVE"},
				  {"300","499","S","FACULTY","AVE"},
				  {"300","1299","N","FESS","AVE"},
				  {"300","599","S","FESS","AVE"},
								  
				  {"100","1299","N","FORREST","AVE"},
				  {"900","1099","N","FOSTER","DR"},
				  {"100","1499","N","GRANT",""},
				  {"100","499","S","GRANT",""},
				  {"400","420","N","HAROLD",""}, // 50
								  
				  {"200","599","S","HAWTHORNE","DR"},
				  {"300","499","S","HENDERSON",""},
				  {"300","500","S","HIGHLAND","AVE"},
				  {"600","1999","E","HUNTER","AVE"},
				  {"100","1399","N","INDIANA","AVE"},
								  
				  {"100","399","S","INDIANA","AVE"},
				  {"1400","1899","E","JONES","AVE"},
				  {"100","1499","N","JORDAN","AVE"},
				  {"100","599","S","JORDAN","AVE"},
				  {"300","399","S","KIRBY","ALY"}, // 60
								  
				  {"100","1099","E","KIRKWOOD","AVE"},
				  {"100","199","W","KIRKWOOD","AVE"},
				  {"1300","1899","E","LAW","LN"},
				  {"100","1699","N","LINCOLN",""},
				  {"100","499","S","LINCOLN",""},
								  
				  {"1600","1999","E","LINGELBACH","LN"},
				  {"300","599","S","MITCHELL",""},
				  {"300","1099","N","PARK","AVE"},
				  {"400","599","S","PARK","AVE"},
				  {"500","535","N","PROW","AVE"}, // 70
								  
				  {"100","599","N","ROSE","AVE"},
				  {"100","550","E","SMITH","AVE"},
				  {"100","199","W","SMITH","AVE"},
				  {"1450","1499","E","STATE","CT"},
				  {"300","599","N","SUNRISE","DR"},
								  
				  {"300","599","S","SWAIN","AVE"},
				  {"1100","1199","N","UNION","CT"},
				  {"100","1199","N","UNION",""},
				  {"100","299","S","UNION",""},
				  {"100","1699","N","WALNUT",""}, // 80
								  
				  {"100","499","S","WALNUT",""},
				  {"600","1299","N","WALNUT GROVE","AVE"},
				  {"100","1775","N","WASHINGTON",""},
				  {"100","499","S","WASHINGTON",""},
				  {"300","1299","N","WOODLAWN","AVE"},
								  
				  {"100","599","S","WOODLAWN","AVE"}, 
				  {"0","0","E","ATWATER","TRN"} // 87
								  
	    };
	    findAddrRange(stNumLow,
			  stNumHigh,
			  stDir,
			  stName,
			  stType,				  
			  expYear,
			  stmt,
			  rs,
			  out,
			  format
			  );
	}
	if(!format.equals("csv")){
	    all = "";
	    all += "</table>";
	    all += "</body></html>";
	    out.println(all);
	}
	out.flush();
	out.close();			
	Helper.databaseDisconnect(con, stmt, rs);		
    }
    void findAddrRange(String stNumLow,
		       String stNumHigh,
		       String stDir,
		       String stName,
		       String stType,
		       int expYear,
		       Statement stmt,
		       ResultSet rs,
		       PrintWriter out,
		       String format
		       ){
	String qq = "", prevId="", all="";
	qq = "select id, str_num, addr from (select pd.id id,"+
	    " trim(ad.street_num) str_num,"+
	    " ad.street_num||' '||ad.street_dir||' '||initcap(ad.street_name)||' '||initcap(ad.street_type)||' '||ad.sud_type||' '||ad.sud_num addr "+
	    " from registr pd, address2 ad where "+
	    " ad.registr_id > 0 and pd.id=ad.registr_id and "+
	    " pd.permit_expires > to_date('1/1/"+expYear+"','mm/dd/yyyy') ";
		
	if(!stDir.equals("")){
	    qq += " and ad.street_dir='"+stDir+"' ";
	}
	qq += " and ad.street_name like '"+stName+"' ";
	if(!stType.equals("")){
	    qq += " and ad.street_type = '"+stType+"' ";
	}
	if(!stNumLow.equals("") || !stNumHigh.equals("")){
	    qq += " and REGEXP_LIKE(ad.street_num, '^\\d+$', '') ";
	    qq += ") where ";
	    if(!stNumLow.equals(""))
		qq += " to_number(str_num) >= "+stNumLow;
	    if(!stNumHigh.equals("")){
		if(!stNumLow.equals("")) qq += " and ";								
		qq += " to_number(str_num) <= "+stNumHigh;
	    }
	}
	else{
	    qq += ")";
	}
	// System.err.println(qq);
	if(debug){
	    logger.debug(qq);
	}
	try{
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		String id  = rs.getString(1);
		String addr = rs.getString(3);
		all = "";
		if(id.equals(prevId)) continue;
		prevId = id; // to avoid duplicate
		Rent rent = new Rent(id, true, debug);
		List<Owner> owners = rent.getOwners();
		if(owners == null) continue;
		for(Owner owner:owners){
		    if(format.equals("csv")){
			all += "\""+owner.getFullName()+"\",";
			all += "\""+owner.getAddress()+"\",";
			all += "\""+owner.getCityStateZip()+"\",";
			all += "\""+owner.getWorkPhone()+"\",";
			all += "\""+addr+"\"\n";
		    }
		    else{
			all += "<tr><td>"+owner.getFullName()+"</td>";
			all += "<td>"+owner.getAddress()+"</td>";
			all += "<td>"+owner.getCityStateZip()+"</td>";
			all += "<td>"+owner.getWorkPhone()+"</td>";
			all += "<td>"+addr+"</td>";
			all += "</tr>\n";
		    }
		}
		out.print(all);
	    }
	}catch(Exception ex){
	    // System.err.println(ex);
	    logger.error(ex+":"+qq);
	}
    }
    /**
     // per permit, number of structures, units
     select pd.id ID,count(distinct rs.id) buildings,sum(ru.units) units                          from registr pd, rental_structures rs, rental_units ru                          where pd.id = rs.rid and                                                        pd.permit_expires > to_date('01/01/2017','mm/dd/yyyy')                          and rs.id = ru.sid and ru.units >= 2 group by pd.id 


			 
    */

}






















































