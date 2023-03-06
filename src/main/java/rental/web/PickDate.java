package rental.web;

import java.util.*;
import java.text.*;
import java.util.Date;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;
/**
 * Generates a monthly calendar page with all programs and events in that
 * month.
 *
 */
@WebServlet(urlPatterns = {"/PickDate"})
public class PickDate extends TopServlet {

    String calColor = "black";
    String headerBG = "white";
    String headerFR = "blue";
    String headerWKFR ="red";
    String headerWKBG = "white";
    String calPPBG = "#DDDDDD";
    String calBG = "#CDC9A3";
    String calTDBG = "#BBBBBB"; 
    final static long serialVersionUID = 700L;
    /**
     * Generates a monthly calendar page with programs and events.
     * @param req request input parameters
     * @param res reponse output parameters
     * @throws IOException
     * @throws ServletException
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	Enumeration<String> values = req.getParameterNames();

	String name= "";
	String value = "";
	String action = "", monthType="";
	String wdate = "",whichForm="";
	int month = 0;
	int year = 0;
	int day = 0;
	int nextMonth = 0;
	int nextMonthYear = 0;
	int prevMonth = 0;
	int prevMonthYear = 0;
	PrintWriter out = res.getWriter();

	String[] Days_full = {"Sunday","Monday","Tuesday","Wednesday",
	    "Thursday","Friday","Saturday"};
	String[] Days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	String[] Months = {"January","February","March","April","May",
	    "June","July","August","September","October",
	    "November","December"};
	String[] MonthShort = {"", "Jan","Feb","March","April","May",
	    "June","July","Aug","Sept","Oct",
	    "Nov","Dec"};
	String[] monthTxt = {"","01","02","03","04","05","06","07","08","09",
	    "10","11","12"};
        String [] vals;
	//

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	    if (name.equals("month")){ 
		if(!value.equals(""))
		    month = Integer.parseInt(value);
	    }
	    else if (name.equals("year")){
		if(!value.equals(""))
		    year = Integer.parseInt(value);
	    }
	    else if (name.equals("day")){
		if(!value.equals(""))
		    day = Integer.parseInt(value);
	    }
	    else if (name.equals("wdate")){ // which date field
		wdate = value;
	    }
	    else if (name.equals("whichForm")){ // which form
		whichForm = value;
	    }
	    else if (name.equals("monthType")){ // month type text/digit
		monthType = value;
	    }
	    else if (name.equals("prev")){
		action = "prev";
	    }
	    else if (name.equals("next")){
		action = "next";
	    }
	}
	GregorianCalendar cal = new GregorianCalendar();

	int current_month = cal.get(Calendar.MONTH) + 1;
	int current_day = cal.get(Calendar.DATE);
	int current_year = cal.get(Calendar.YEAR); 
	int today[] ={ current_month,current_day,current_year};
	if (month == 0) {
	    month = current_month;
	} 
	if (year == 0) {
	    year = current_year;
	} 
	if (day == 0) {
	    day = current_day;
	} 
	//
	// get next month and year
	GregorianCalendar tcal = new GregorianCalendar();
	// Calendar tcal = Calendar.getInstance();
	tcal.set(Calendar.MONTH, month-1);
	tcal.set(Calendar.YEAR, year);
	// Notice 
	// to avoid problems of 30,31 when adding
	// or subtracting months, we set the day date 1 for next 
	// and prev buttons only
	//
	tcal.set(Calendar.DATE, 1);   // fixed a problem

	tcal.add(Calendar.MONTH, 1);
	nextMonth = tcal.get(Calendar.MONTH)+1;
	nextMonthYear = tcal.get(Calendar.YEAR);
	//
	// get prev month and year
	tcal.add(Calendar.MONTH, -2);
	prevMonth = tcal.get(Calendar.MONTH)+1;
	prevMonthYear = tcal.get(Calendar.YEAR);

	int days_in_month = get_days_in_month(month, year);
	// System.err.println("Days in month: "+days_in_month);

	int first_day_of_month = get_first_day_of_month(month, year);

	out.println("<html><head><title>Pick Date</title>");
	out.println("<STYLE TYPE=\"text/css\"><!--");
	out.println("A:link     {text-decoration: none; color: " + calColor + 
		    ";}");
	out.println("A:visited  {text-decoration: none; color: " + calColor + 
		    ";}");
	out.println("A:active   {text-decoration: none; color: " + calColor + 
		    ";}");
	out.println("A:hover    {text-decoration: none; color: " + calColor + 
		    ";}");
	out.println("--></STYLE>");
	
	out.println("<script type=\"text/javascript\"> ");
	out.println(" function picked(day){   ");
	out.println(" var daytxt=\"\",monthtxt=\"\";");		
	if(monthType.equals("")){
	    out.println(" var month="+month+";");
	    out.println(" if(month< 10){ monthtxt=\"0\"+month;} ");
	    out.println(" else { monthtxt = month; }           ");
	    out.println(" if(day< 10){ daytxt=\"0\"+day;} ");
	    out.println(" else { daytxt = day; }           ");
	    out.println(" var pdate = \"\"+monthtxt+\"/\"+daytxt+\"/"+year+"\"; ");			
	}
	else{ // text type for month Mon dd, yyyy
	    out.println(" var pdate = \""+MonthShort[month]+" \"+day+\", "+year+"\"; ");
	}
	if(whichForm.equals(""))
	    out.println("  opener.document.forms[0]."+wdate+".value=pdate;");
	else
	    out.println("  opener.document."+whichForm+"."+wdate+
			".value=pdate;");
	out.println("  window.close(); ");
	out.println("  }               ");
	out.println("</SCRIPT> ");
	out.println("</head><body >");	
	
	cal.set(Calendar.YEAR, year);
	cal.set(Calendar.MONTH, month - 1);
	cal.set(Calendar.DATE, day);
	out.println("<center>");
	out.println("<font color=blue><b> Pick a  "+
		    "Date </b></font><br>");
	out.println("<font size=+1> "+Months[month-1]+ " "+year+
		    "</font><br>");
	out.println("<table border=2 CELLSPACING=0 cols=7 width=\"90%\" ");
	String dd = "&nbsp;";
	int ddd = 0;
	int nxt = 0;
	boolean pass = false, more_rows = true;
	
	for (int row = 0; row < 7 && more_rows; row++){
	    out.println("<tr>");
	    
	    for (int col = 0; col < 7; col++){
		if (row != 0) {
		    if (col == first_day_of_month - 1){ 
			pass = true;
		    }
		}	
		if (pass){	
		    ddd++;
		    dd = Integer.toString(ddd);
		}
		if (ddd > days_in_month){ 
		    dd = "&nbsp;";     
		}
		if (ddd >= days_in_month){ 
		    more_rows = false;
		}
		//
		// first row
		// write day names
		//
		if (row == 0){ 
		    if (col == 0 || col == 6) // weekends  	
			out.println("<td bgcolor=" + headerWKBG + 
				    " align=center valign=top><font color=" + 
				    headerWKFR + ">" + Days[col] + 
				    "</font><b></td>");
		    else	// other days			
			out.println("<td bgcolor=" + headerBG +
				    " ALIGN=CENTER VALIGN=TOP><font color=" +
				    headerFR + ">" + Days[col] +
				    "</font></td>");
		} 
		else{
		    // other rows 
		    if (dd.equals("&nbsp;")) {	
			if (col == 0 || col == 6) 
			    out.println("<td bgcolor=white>"+dd+"</td>"); 
			else
			    out.println("<td bgcolor="+calBG+">"+dd+"</td>"); 
		    }
		    else if(!dd.equals("")) {		
			String str ="";
			if (col == 0 || col == 6) 
			    str += "<td BGCOLOR=white" ;   // calTDBG + 
			else 
			    str += "<td BGCOLOR="+calBG ; 
			str += " valign=top align=left>"+
			    "<font face=\"Courier new\" size=-1><a href=javascript:picked(" + 
			    dd +");>"+dd+ 
			    "</a></font></td>";
			out.println(str);
		    }
		    nxt++;
		}
	    }
	    out.println("</tr>");    
	}
	out.println("</table>");
	out.println("<form name=myForm method=post>");
	out.println("<input type=hidden name=month value=\""+prevMonth+"\">");
	out.println("<input type=hidden name=year value=\""+prevMonthYear+ 
		    "\">");
	out.println("<input type=hidden name=wdate value=\""+wdate+"\">");
	if(!whichForm.equals(""))
	    out.println("<input type=hidden name=whichForm value=\""+
			whichForm+"\">");
	if(!monthType.equals(""))
	    out.println("<input type=hidden name=monthType value=\""+
			monthType+"\">");
	out.println("<table border=0 width=90%>");
	out.println("<tr><td valign=top align=left>");
	out.println("<input type=submit name=prev value=\"Prev Month\">");
	out.println("</td><td valign=top align=right>");
	out.println("</form>");
	out.println("<form name=myForm2 method=post>");
	out.println("<input type=hidden name=month value=\""+nextMonth+"\">");
	out.println("<input type=hidden name=year value=\""+nextMonthYear+ 
		    "\">");
	out.println("<input type=hidden name=wdate value=\""+wdate+"\">");
	if(!whichForm.equals(""))
	    out.println("<input type=hidden name=whichForm value=\""+
			whichForm+"\">");
	if(!monthType.equals(""))
	    out.println("<input type=hidden name=monthType value=\""+
			monthType+"\">");		
	out.println("<input type=submit name=next value=\"Next Month\">");
	out.println("</td></tr>");
	out.println("<tr><td colspan=2 align=center>");
	out.println("<a href=javascript:window.close();>Close</a>");
	out.println("</td></tr></table>");
	out.println("</form>");

	out.println("</body></html>");
	out.flush();
	out.close();

    }
    /**
     * Finds out the starting day of a given month.
     * @param MM month
     * @param YYYY the year
     * @return a number representing the first day of month or the day 
     * of the weeek
     */
    public int get_first_day_of_month(int MM, int YYYY){

	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.YEAR, YYYY);
	cal.set(Calendar.MONTH, MM - 1);
	cal.set(Calendar.DAY_OF_MONTH, 1);
      	return cal.get(Calendar.DAY_OF_WEEK);

    }
    /**
     * Finds the number of days in a given month.
     * @param mm the month
     * @param yy the year
     * @return the number of days in the month
     */
    public int get_days_in_month(int mm, int yy) {

	GregorianCalendar cal = new GregorianCalendar();
	cal.set(Calendar.YEAR, yy);
	cal.set(Calendar.MONTH, mm - 1);
	//
	// WS just February 28, 29
	//
	// checking when a day is not in that month
	//
	for (int t = 26; t < 33; t++) {
	    cal.set(Calendar.DAY_OF_MONTH, t);
	    if (cal.get(Calendar.MONTH) + 1 != mm) {
		return t - 1;
	    }     
	}
	return 0;  // should not happen
    }
    /**
     * Generates a monthly calendar page where the use can pick a date
     * @param req request input parameters
     * @param res reponse output parameters
     * @throws IOException
     * @throws ServletException
     */
  
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req, res);
    }
    /**
     * @see Facility#doubleApostrify
     */

    int[] updateCurrentDate(int month, int day, int year){
	int daysInMonth[] = { 31, 28, 31, 30, 31, 30, 31,
	    31, 30, 31, 30, 31, 30, 31};
	int [] newDate = new int[3];
	if(day + 1 <= daysInMonth[month-1]) day++;
	else if(month == 2) { //February
	    if(day+1 < get_days_in_month(month, year)){
		day++;
	    }
	}
	else if(month+1 < 13){
	    day = 1;
	    month++;
	}
	else { // the last day of the year
	    day = 1;
	    month = 1;
	    year++;
	}
	newDate[0] = month;
	newDate[1] = day;
	newDate[2] = year;
	return newDate;
    }

}






















































