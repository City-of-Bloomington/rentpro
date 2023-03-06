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

@WebServlet(urlPatterns = {"/PeriodReport"})
public class PeriodReport extends TopServlet{

    final static long serialVersionUID = 660L;
    String bgcolor = Rental.bgcolor;

    int maxlimit = 100; // limit on records
    String email = "", dept="", fullname="", phone="";
    String allAssignee ="";
    //
    // list access to time statistics
    String[] admins ={"skjervok","routonr","sibow","volang"};
    //
    String[][] CATS_SUB_ARR = null; //Rental.CATS_SUB_ARR;
    String CATEGORY_SELECT = null; // Rental.CATEGORY_SELECT;
    String[] CATS_ARR = null; // Rental.CATS_ARR; 
    String[] busCatArr = null; //RentalBrowse.busCatArr; 
    static boolean allowed = false;

    static Logger logger = LogManager.getLogger(PeriodReport.class);
    /**
     * Generates the entry form for report generator.
     *
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();

	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	String name, value;
	String action="";
	String id="";
	String username = "";

	String date_to="", date_from="";
	String time_spent_to="", time_spent_from="";
	String mm_to="",  dd_to="",  yy_to="";
	String mm_from="",  dd_from="",  yy_from="";

	int category = 0, resolved_month=0;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("username")){
		username = value;
	    }
	}
	if(url.equals("")){
	    url  = getServletContext().getInitParameter("url");
	    String debug2 = getServletContext().getInitParameter("debug");
	    if(debug2.equals("true")) debug = true;
	}
	// 
	out.println("<html><head><title> Report</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println(" function moveToNext(item, size, nextItem, e){ ");
	out.println(" keyCode = (window.Event) ? e.which: e.keyCode;  ");
	//out.println("  alert(\" keycode = \"+keyCode);  ");
	out.println("  if(keyCode > 47 && keyCode < 58){  "); // only numbers
	out.println("  if(item.value.length > size-1){         ");
	out.println("  eval(nextItem.focus());            ");
	out.println("   document.myForm.period[2].checked=true; ");
	out.println("  }}}                                ");
	out.println("  function validateForm(){		        ");
       	out.println("  if(document.myForm.period[2].checked){           ");
	out.println("  	if(document.myForm.date_from.value.length==0 && ");
	out.println("  	 document.myForm.date_to.value.length==0){   ");
	out.println("     alert(\"Date range not set \");	                ");
	out.println("     return false;				       	");
	out.println("	}}                                              ");
	out.println("     return true;				        ");
	out.println("	}	         			        ");
	out.println(" function checkPeriod(item){ ");
	out.println("  if(item.value.length > 0){         ");
	out.println("   document.myForm.period[2].checked=true; ");
	out.println("  }}                                ");
	out.println("  function validateForm(){		        ");
       	out.println("  if(document.myForm.period[2].checked){           ");
	out.println("  	if(document.myForm.date_from.value.length==0 && ");
	out.println("  	 document.myForm.date_to.value.length==0){   ");
	out.println("     alert(\"Date range not set \");	                ");
	out.println("     return false;				       	");
	out.println("	}}                                              ");
	out.println("     return true;				        ");
	out.println("	}	         			        ");
	//
	out.println(" </script>");
	//
	out.println("</head><body>");
	Helper.writeTopMenu(out, url);
	out.println("<form name=\"myForm\" method=\"post\" "+
		    "onSubmit=\"return validateForm()\">");

	//
	out.println("<center><h2>Statistics Report</h2>");
	out.println("<table border=\"1\" width=\"90%\">");
	out.println("<tr><td bgcolor=\""+bgcolor+"\">");
	out.println("<table width=\"100%\">");
	// 
	//
	out.println("<tr><td><b>Select from the Options below</b></td></tr>");
	//
	// rentals in date range
	out.println("<tr><td>");
       	out.println("<input type=\"radio\" name=\"action\" "+
		    "value=\"stats\" checked=\"checked\" />Count of Permits/Units: Registered, "+
		    "issued, expired, billed, received, pulled, inspected. "+
		    "</td></tr>");
	//
	// inspectios/inspector
	out.println("<tr><td>");
       	out.println("<input type=\"radio\" name=\"action\" "+
		    "value=\"inspects\" />Count of Inspections per Inspector "+
		    "</td></tr>");
	//
	// permit by agent
	out.println("<tr><td>");
       	out.println("<input type=\"radio\" name=\"action\" "+
		    "value=\"permAgent\" />Count of Permits per Agent "+
		    "</td></tr>");
	//
	// permits by owners
	out.println("<tr><td>");
       	out.println("<input type=\"radio\" name=\"action\" "+
		    "value=\"permOwn\" />Count of Permits per Owner "+
		    "</td></tr>");
	//
	// permits by pull reason
	out.println("<tr><td>");
       	out.println("<input type=\"radio\" name=\"action\" "+
		    "value=\"permPull\" />Count of Permits per Pull Reason "+
		    "</td></tr>");
	out.println("<tr><td>&nbsp;</td></tr>");
	// 
	// date 
	//
	out.println("<tr><td><b> Date Range: </b>");
	out.println("<input type=\"radio\" name=\"period\" checked=\"checked\""+
		    " value=\"week\" />Week,");
	out.println("<input type=\"radio\" name=\"period\" "+
		    "value=\"month\" />Month,");
	out.println("<input type=\"radio\" name=\"period\" "+
		    "value=\"specified\" />As specified below");
	out.println("</td></tr>");
	//
	//
	out.println("<tr><td align=\"center\">");
	out.println("<font color=\"green\" size=\"-1\">"+
		    "<br />Enter the date range below "+
		    "</font></td></tr>");
	//
	out.println("<tr><td align=\"center\">");
	out.println("<table><tr><td><b>from:</b></td>");
	out.println("<td><input type=\"text\" name=\"date_from\" value=\"\""+
		    " onchange=\"checkPeriod(this)\" "+
		    " size=\"10\" maxlength=\"10\" class=\"date\" /></td>");
	out.println("<td> <b>To</b> </td>");
	out.println("<td><input type=\"text\" name=\"date_to\" value=\"\""+
		    " onchange=\"checkPeriod(this)\" "+
		    " size=\"10\" maxlength=\"10\" class=\"date\" /> ");
	out.println("</td></tr></table>");
	out.println("</td></tr>");
	//
	out.println("<tr><td><hr /></td></tr>");
	out.println("<tr><td align=\"right\"><input type=\"submit\" " +
		    "value=\"Submit\" /></td></tr>");
	out.println("</table></td></tr>");
	// 
	out.println("</table>");
	out.println("</form><br />");
	Helper.writeWebFooter(out, url);	
	out.print("</center></body></html>");
	out.close();

    }
    /**
     * Generates the report according to users request.
     *
     * @param req
     * @param res
     */  
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");

	PrintWriter out = res.getWriter();			  
	String username = "", password = "";
	String name, value;
	String action="", status="", message="";
	String id="";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean success = true;
	String date_to="", date_from="", date_from2="";
	String date_assigned="", busCat="";
	String mm_to="", dd_to="", yy_to="";
	String mm_from="", dd_from="", yy_from="";
	String period="";

	boolean week=false, month=false, period2=false;
	boolean stats=true, permAgent=false, permOwn=false, permPull=false,
	    inspects=false;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("username")){
		username = value;
	    }
	    else if (name.equals("date_from")) {
		date_from=value;
	    }
	    else if (name.equals("date_to")) {
		date_to=value;
	    }
	    else if (name.equals("period")){
		period = value;
	    }
	    else if (name.equals("dept")){
		dept = value;
	    }
	    else if (name.equals("status")){
		status = value;
	    }
	    else if (name.equals("action")){
		action = value;
		stats = false;
		if(value.equals("stats"))
		    stats = true;
		else if(value.equals("inspects"))
		    inspects = true;
		else if(value.equals("permAgent"))
		    permAgent = true;
		else if(value.equals("permOwn"))
		    permOwn = true;
		else if(value.equals("permPull"))
		    permPull = true;
	    }
	    /*
	      else if (name.equals("mm_from")) {
	      mm_from=value;
	      }
	      else if (name.equals("dd_from")) {
	      dd_from=value;
	      }
	      else if (name.equals("yy_from")) {
	      yy_from=value;
	      }
	      else if (name.equals("mm_to")) {
	      mm_to=value;
	      }
	      else if (name.equals("dd_to")) {
	      dd_to=value;
	      }
	      else if (name.equals("yy_to")) {
	      yy_to=value;
	      }
	    */
	}
	if(url.equals("")){

	    url  = getServletContext().getInitParameter("url");
	    String debug2 = getServletContext().getInitParameter("debug");
	    if(debug2.equals("true")) debug = true;
	}
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
			
	//
	// check if this user is allowed to 
	// access these data
	//
	// System.err.println("period: "+period);
	if(period.equals("week")) week = true;
	else if(period.equals("month")) month = true;
	else if(period.equals("specified")) period2 = true;
	//
	GregorianCalendar current_cal = new GregorianCalendar();
	String startDate = "", endDate="";
	//       
	// endDate is set to today
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	//
	if(week){
	    endDate = mm+"/"+dd+"/"+yyyy;
	    //
	    // find the start of the week
	    GregorianCalendar temp_cal = new GregorianCalendar();
	    temp_cal.add(Calendar.DATE, -7);
	    //
	    startDate = (temp_cal.get(Calendar.MONTH)+1)+"/"+
		temp_cal.get(Calendar.DATE)+"/"+
		temp_cal.get(Calendar.YEAR);
	    date_from = startDate;
	    date_to = endDate; 
	}
	else if(month){
	    endDate = mm+"/"+dd+"/"+yyyy;
	    // find the start of the month
	    GregorianCalendar temp_cal = new GregorianCalendar();
	    temp_cal.add(Calendar.MONTH, -1);
	    startDate = (temp_cal.get(Calendar.MONTH)+1)+"/"+
		temp_cal.get(Calendar.DATE)+"/"+
		temp_cal.get(Calendar.YEAR);
	    date_from2 = (temp_cal.get(Calendar.MONTH)+1)+"/"+
		temp_cal.get(Calendar.DATE)+"/"+
		temp_cal.get(Calendar.YEAR-1);
	    date_from = startDate;
	    date_to = endDate; 
	}
	else{
	    //
	    startDate = date_from;
	    endDate = date_to;
	    // System.err.println("Req from to:"+date_from+" "+date_to);
	    //
	    // This will not happen, javascript will check
	    // if the user did not set the date limits, we set it for
	    // the last 3 months
	    //
	    if(startDate.equals("")){
		//
		// find the start of the last 3 months
		GregorianCalendar temp_cal = new GregorianCalendar();
		temp_cal.add(Calendar.MONTH, -3);
		startDate = (temp_cal.get(Calendar.MONTH)+1)+"/"+
		    temp_cal.get(Calendar.DATE)+"/"+
		    temp_cal.get(Calendar.YEAR);
	    }
	}
	//
	out.println("<html><head><title>Statistics Report</title>");
	Vector<String> wherecases = new Vector<String>();
	//
	//
	// period statistics
	//
	out.println("<br>");
	out.println("<center><font size=+1><b>");
	out.println("HAND Rental<br>");
	if(!startDate.equals("") && !endDate.equals(""))
	    out.println("Statistics for the Period of "+
			startDate+
			" to "+endDate);
	else if(!startDate.equals(""))
	    out.println("Statistics for the Period Starting at "+
			startDate);
	else if(!endDate.equals(""))
	    out.println("Statistics for the Period Ending at "+
			endDate);
	else 
	    out.println("Statistics for the whole period Up "+
			"to Now ");
	out.println("</b></font><br><br>");
	//
	String qy="",qf="",qw="",qw2="",qw3="",qq="";
	String str = "",str2="",str3="",str4="";
	boolean andFlag = false;
	int total = 0, nt = 0;
	if(stats){
	    out.println("<table border width=75%>");
	    out.println("<tr><td><table width=100%>");
	    out.println("<tr><th>&nbsp;</th><th align=left>Permits</th>"+
			"<th align=left>Buildings</th>"+
			"<th align=left>Units</th>"+
			"<th align=left>Room-Units</th>"+
			"</tr>");
	    //
	    // all
	    qy = " select count(*),sum(structures),sum(units) ";
	    qy += ",sum(bath_count) ";
	    qf = " from registr pd ";
			
	    qw = " where "+
		" pd.inactive is null "; // to pd.
			
			
	    if(!date_from.equals("")){
		qw += " and ";
		qw += " registered_date >= to_date('"+date_from+
		    "','mm/dd/yyyy')";
	    }
	    if(!date_to.equals("")){
		qw +=" and ";
		qw += " registered_date <= to_date('"+date_to+
		    "','mm/dd/yyyy')";
	    }
	    if(wherecases.size()>0){
		for (int c = 0; c < wherecases.size(); c++){
		    qw2 += " and ";
		    qw2 += wherecases.elementAt(c);
		}
	    }
	    qw3 += " and ";
	    qw3 += " pd.property_status=";//R,O,C,V reg, owner, commerc,vacant
	    try{
		qq = qy+qf+qw+qw2+qw3+"'R'";
		//
		if(debug){
		    logger.debug(qq);
		}
		//
		// in registered status
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Registered",str2,str3,str4);
		}
		qq = qy+qf+qw+qw2+qw3+"'V'";
		//
		if(debug){
		    logger.debug(qq);
		}
		//
		// in registered status
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Vacant",str2,str3,str4);
		}
		qq = qy+qf+qw+qw2+qw3+"'C'";
		//
		if(debug){
		    logger.debug(qq);
		}
		//
		// in registered status
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Commercial",str2,str3,str4);
		}
		qq = qy+qf+qw+qw2+qw3+"'O'";
		//
		if(debug){
		    logger.debug(qq);
		}
		//
		// in registered status
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Owner Occupied",str2,str3,str4);
		}
		qq = qy+qf+qw+qw2;
		//
		if(debug){
		    logger.debug(qq);
		}
		//
		// Total registered
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Total",str2,str3,str4);
		}
		writeItem3(out,"<hr width=100%>","<hr width=100%>","<hr width=100%>","<hr width=100%>","<hr width=100%>");
		//
		// issued
		qw3 = "";
				
		if(!date_from.equals("")){
		    qw3 += " and ";
		    qw3 += " permit_issued >= to_date('"+date_from+
			"','mm/dd/yyyy')";
		}
		if(!date_to.equals("")){
		    qw3 += " and ";
		    qw3 += " permit_issued <= to_date('"+date_to+
			"','mm/dd/yyyy')";
		}
		qq = qy+qf+qw+qw2+qw3;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    // writeItem(out, str,"Issued");
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Issued",str2,str3,str4);
		}
		// 
		// expire
		qw3 = "";
				
		if(!date_from.equals("")){
		    qw3 += " and ";
		    qw3 += " permit_expires >= to_date('"+date_from+
			"','mm/dd/yyyy')";
		}
		if(!date_to.equals("")){
		    qw3 += " and ";
		    qw3 += " permit_expires <= to_date('"+date_to+
			"','mm/dd/yyyy')";
		}
		qq = qy+qf+qw+qw2+qw3;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Expired",str2,str3,str4);
		    // writeItem(out, str,"Expired");
		}
		//
		// billed
		qw3 = "";
				
		if(!date_from.equals("")){
		    qw3 += " and date_billed >= to_date('"+date_from+
			"','mm/dd/yyyy')";
		}
		if(!date_to.equals("")){
		    qw3 += " and date_billed <= to_date('"+date_to+
			"','mm/dd/yyyy')";
		}
		qq = qy+qf+qw+qw2+qw3;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Billed",str2,str3,str4);
		}
		// received
		qw3 = "";
				
		if(!date_from.equals("")){
		    qw3 = " and date_rec >= to_date('"+date_from+
			"','mm/dd/yyyy')";
		}
		if(!date_to.equals("")){
		    qw3 += " and date_rec <= to_date('"+date_to+
			"','mm/dd/yyyy')";
		}
		qq = qy+qf+qw+qw2+qw3;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Received Bills",str2,str3,str4);
		    //  writeItem(out, str,"Received");
		}
		// pulled
		qw3 = "";
				
		if(!date_from.equals("")){
		    //
		    // for the new permit issued during this period
		    // could very well registered before, so we limited
		    // the registration date to no more than one year 
		    // before
		    qw3 = " and registered_date >= to_date('"+date_from2+
			"','mm/dd/yyyy')";
		    qw3 += " and permit_issued >= to_date('"+date_from+
			"','mm/dd/yyyy')";
		}
		if(!date_to.equals("")){
		    qw3 += " and ";
		    qw3 += " permit_issued <= to_date('"+date_to+
			"','mm/dd/yyyy')";
		}
		//
		qq = qy+qf+qw+qw2+qw3;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"New Permits",str2,str3,str4);
		    //  writeItem(out, str,"Pulled");
		}
		// inspected
		qy = " select count(*),sum(structures),sum(units) ";
		qy += ",sum(bath_count) ";
		qy += " from registr pd ";
		qw = " where "+
		    //" pd.id = ps.id  and "+ // delete
		    " pd.inactive is null "+ // to pd
		    " and pd.id in ( select distinct(it.id) from inspections it "+
		    "where ";
		//
		boolean IN = false;
		if(!date_from.equals("")){
		    qw += " it.inspection_date >= to_date('"+date_from+
			"','mm/dd/yyyy')";
		    IN = true;
		}
		if(!date_to.equals("")){
		    if(IN) qw += " and ";
		    qw += " it.inspection_date <= to_date('"+date_to+
			"','mm/dd/yyyy')";
		}
		qw += ")";
		qq = qy+qw;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    str3 = rs.getString(3);
		    str4 = rs.getString(4);
		    if(str2 == null)str2="0";
		    if(str3 == null)str3="0";
		    if(str4 == null)str4="0";
		    writeItem3(out, str,"Inspected",str2,str3,str4);
		    //  writeItem(out, str,"Inspected");
		}
		out.println("</table></td></tr></table>");
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+" : "+qq);
	    }
	}
	else if(inspects){
	    out.println("<table border width=60%><tr><td><table width=100%>");
	    qy = " select count(*),sum(pd.units),initcap(its.name)";
	    qf = " from registr pd,inspections it,inspectors its ";
	    qw = " where it.inspected_by=its.initials and it.id=pd.id and "+
		" it.inspection_type like 'CY%' "+
		" and pd.inactive is null "; // to pd.

	    if(!date_from.equals("")){
		qw += " and it.inspection_date >= to_date('"+date_from+
		    "','mm/dd/yyyy')";
	    }
	    if(!date_to.equals("")){
		qw +=" and ";
		qw += " it.inspection_date <= to_date('"+date_to+
		    "','mm/dd/yyyy')";
	    }
	    if(wherecases.size()>0){
		for (int c = 0; c < wherecases.size(); c++){
		    qw2 += " and ";
		    qw2 += wherecases.elementAt(c);
		}
	    }
	    qw2 += " group by its.name ";
	    qq = qy+qf+qw+qw2;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		int nt2=0,total2=0;
		rs = stmt.executeQuery(qq);
		out.println("<tr><th align=left>Inspector</th>"+
			    "<th align=left>Cycle Inspections</th>"+
			    "<th align=left>Units</th></tr>");
		out.println("<tr><td colspan=3><hr width=100%></td></tr>");
		while(rs.next()){
		    nt = rs.getInt(1);
		    nt2 = rs.getInt(2);
		    str2 = rs.getString(3);
		    writeItem2(out, ""+nt,""+nt2,str2);
		    total += nt;
		    total2 += nt2;
		}
		out.println("<tr><td colspan=3><hr width=100%></td></tr>");
		writeItem2(out, ""+total,""+total2,"Total");
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+" : "+qq);
	    }
	    out.println("</table></td></tr></table>");
	}
	else if(permAgent){
	    out.println("<table border width=70%><tr><td><table width=100%>");
	    qy = " select count(*),initcap(od.name)";
	    qf = " from registr pd,name od ";
	    qw = " where pd.agent=od.name_num and pd.agent > 0 "+
		" and pd.inactive is null "; // to pd.
	    if(!date_from.equals("")){
		qw += " and pd.registered_date >= to_date('"+date_from+
		    "','mm/dd/yyyy')";
	    }
	    if(!date_to.equals("")){
		qw += " and pd.registered_date <= to_date('"+date_to+
		    "','mm/dd/yyyy')";
	    }
	    if(wherecases.size()>0){
		for (int c = 0; c < wherecases.size(); c++){
		    qw2 += " and ";
		    qw2 += wherecases.elementAt(c);
		}
	    }
	    qw2 += " group by od.name ";
	    qq = qy+qf+qw+qw2;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		out.println("<tr><th align=left>Agent</th>"+
			    "<th align=left>Permits</th></tr>");
		out.println("<tr><td colspan=2><hr width=100%></td></tr>");
		while(rs.next()){
		    nt = rs.getInt(1);
		    str2 = rs.getString(2);
		    writeItem(out, ""+nt,str2);
		    total += nt;
		}
		out.println("<tr><td colspan=2><hr width=100%></td></tr>");
		writeItem(out, ""+total,"Total");
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+" : "+qq);
	    }
	    out.println("</table></td></tr></table>");
	    //
	}
	else if(permOwn){
	    out.println("<table border width=70%><tr><td><table width=100%>");
	    qy = " select count(*),initcap(od.name)";
	    qf = " from registr pd,name od,regid_name rn ";
	    qw = " where pd.id=rn.id and od.name_num=rn.name_num and od.name_num > 0 "+
		" and pd.inactive is null"; // to pd.
			
	    if(!date_from.equals("")){
		qw += " and pd.registered_date >= to_date('"+date_from+
		    "','mm/dd/yyyy')";
	    }
	    if(!date_to.equals("")){
		qw += " and pd.registered_date <= to_date('"+date_to+
		    "','mm/dd/yyyy')";
	    }
	    if(wherecases.size()>0){
		for (int c = 0; c < wherecases.size(); c++){
		    qw2 += " and ";
		    qw2 += wherecases.elementAt(c);
		}
	    }
	    qw2 += " group by od.name ";
	    qq = qy+qf+qw+qw2;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		out.println("<tr><th align=left>Owner</th>"+
			    "<th align=left>Permits</th></tr>");
		out.println("<tr><td colspan=2><hr width=100%></td></tr>");
		while(rs.next()){
		    nt = rs.getInt(1);
		    str2 = rs.getString(2);
		    writeItem(out, ""+nt,str2);
		    total += nt;
		}
		out.println("<tr><td colspan=2><hr width=100%></td></tr>");
		writeItem(out, ""+total,"Total");
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+" : "+qq);
	    }
	    out.println("</table></td></tr></table>");
	}
	else if(permPull){
	    qw2 = "";
			
	    out.println("<table border width=70%><tr><td><table width=100%>");
	    qy = " select count(*),initcap(pr.pull_text)";
	    qf = " from registr pd,pull_reas pr ";
	    qw = " where pd.pull_reason=pr.p_reason "+
		" and pd.inactive is null "; // to pd.
	    if(!date_from.equals("")){
		qw2 += " and pd.pull_date >= to_date('"+date_from+
		    "','mm/dd/yyyy')";
	    }
	    if(!date_to.equals("")){
		qw2 += " and pd.pull_date <= to_date('"+date_to+
		    "','mm/dd/yyyy')";
	    }
	    if(wherecases.size()>0){
		for (int c = 0; c < wherecases.size(); c++){
		    qw2 += " and ";
		    qw2 += wherecases.elementAt(c);
		}
	    }
	    qw2 += " group by pr.pull_text ";
	    qq = qy+qf+qw+qw2;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		out.println("<tr><th align=left>Pull Reason </th>"+
			    "<th align=left>Permits</th></tr>");
		out.println("<tr><td colspan=2><hr width=100%></td></tr>");
		while(rs.next()){
		    nt = rs.getInt(1);
		    str2 = rs.getString(2);
		    writeItem(out, ""+nt,str2);
		    total += nt;
		}
		out.println("<tr><td colspan=2><hr width=100%></td></tr>");
		writeItem(out, ""+total,"Total");
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+" : "+qq);
	    }
	    out.println("</table></td></tr></table>");
	}
	out.println("<br>");
	out.println("</body>");
	out.println("</html>");
	Helper.databaseDisconnect(con,stmt,rs);
		
    }
    /**
     * Writes  a pair of item title in a table.
     */
    void writeItem(PrintWriter out,String item, String title){
	out.println("<tr><td>"+title+"</td><td>"+item+"</td></tr>");
    }
    void writeItem2(PrintWriter out,String item, String item2, String title){
	out.println("<tr><td>"+title+"</td><td>"+item+"</td><td>"+item2+
		    "</td></tr>");
    }
    /**
     *
     */
    void writeItem3(PrintWriter out,String item, String title,String item2,String item3,String item4){
	out.println("<tr><td>"+title+"</td><td>"+item+"</td>");
	out.println("<td>"+item2+"</td><td>"+item3+"</td>"+
		    "<td>"+item4+"</td></tr>");
    }

}






















































