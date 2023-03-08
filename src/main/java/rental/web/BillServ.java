package rental.web;

import java.util.*;
import java.io.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/BillServ"})
public class BillServ extends TopServlet{

    final static long serialVersionUID = 120L;
    static Logger logger = LogManager.getLogger(BillServ.class);	    
    final static Locale local = new Locale("en","US");
    final static NumberFormat curformat = NumberFormat.getCurrencyInstance(local);
    String bgcolor = Rental.bgcolor;

    boolean userFoundFlag = false;
    public static String[] allmonths = {"\n","Jan","Feb","March",
	"April","May","June",
	"July","August","Sept",
	"Oct","Nov","Dec"};

    static final String MONTH_SELECT = "<option>JAN\n" + 
	"<option>FEB\n" + 
	"<option>MAR\n" + 
	"<option>APR\n" + 
	"<option>MAY\n" + 
	"<option>JUN\n" + 
	"<option>JUL\n" + 
	"<option>AUG\n" + 
	"<option>SEP\n" + 
	"<option>OCT\n" + 
	"<option>NOV\n" + 
	"<option>DEC\n" + 
	"</select>";
	

    /**
     * Generates the bill.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	boolean success = true;
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String bul_rate="", message="",
	    unit_rate="",
	    bath_rate="", 
	    reinsp_rate="",
	    noshow_rate="",
	    summary_rate="",
	    IDL_rate="";
	String bul_cnt="0",unit_cnt="0",bath_cnt="0";
	String reinsp_date="",reinsp_cnt="0",noshow_cnt="0",
	    noshow_date="",status="", prop_type="",
	    paid="",check_no="",invoice_num="",credit="0",
	    due_date="",today="",issue_date="",paidSum="0",
	    summary_flag="",IDL_flag="";
	String other_fee_title = "";
	double insp_fee=0,total=0,reinsp_fee=0,noshow_fee=0,balance=0,
	    summary_fee=0,IDL_fee=0, summary_cnt=0,IDL_cnt=0, other_fee=0;
	//
	// receipt items
	String rec_date="",rec_from="",bhqa_fine="",rec_sum="",
	    appeal="",appeal_fee="20";
	//
	String name, value;
	String action="", id="", bid="";

	String propAddr="";

	Enumeration<String> values = req.getParameterNames();

	out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
	out.println("<html><head><title>Billing </title>");
	Helper.writeWebCss(out, url);
	out.println("</head><body>");
	Helper.writeTopMenu(out, url);
	//
	Bill bill = new Bill(debug);
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
		bill.setId(value);
	    }
	    else if (name.equals("bid")){
		bid = value;
		bill.setBid(value);
	    }
	    else if (name.equals("appeal")){
		if(!value.equals("")){
		    appeal = "Y";
		    bill.setAppeal("Y");
		}
	    }
	    else if (name.equals("summary_flag")){
		if(!value.equals("")){
		    summary_flag = "Y";
		    bill.setSummary_flag("Y");
		}
	    }
	    else if (name.equals("IDL_flag")){
		if(!value.equals(""))
		    IDL_flag = "Y";
		bill.setIDL_flag("Y");
	    }
	    else if (name.equals("appeal_fee")){
		bill.setAppeal_fee(value);
	    }
	    else if (name.equals("bul_rate")) {
		bill.setBul_rate(value);
	    }
	    else if (name.equals("unit_rate")) {
		bill.setUnit_rate(value);
	    }
	    else if (name.equals("bath_rate")) {
		bill.setBath_rate(value);
	    }
	    else if (name.equals("noshow_rate")) {
		bill.setNoshow_rate(value);
	    }
	    else if (name.equals("reinsp_rate")) {
		bill.setReinsp_rate(value);
	    }
	    else if (name.equals("summary_rate")) {
		bill.setSummary_rate(value);
	    }
	    else if (name.equals("IDL_rate")) {
		bill.setIDL_rate(value);
	    }
	    else if (name.equals("bul_cnt")) {
		bill.setBul_cnt(value);
	    }
	    else if (name.equals("summary_cnt")) {
		bill.setSummary_cnt(value);
	    }
	    else if (name.equals("IDL_cnt")) {
		bill.setIDL_cnt(value);
	    }			
	    else if (name.equals("credit")) {
		bill.setCredit(value);
	    }
	    else if (name.equals("unit_cnt")) {
		bill.setUnit_cnt(value);
	    }
	    else if (name.equals("bath_cnt")) {
		bill.setBath_cnt(value);
	    }
	    else if (name.equals("reinsp_cnt")) {
		bill.setReinsp_cnt(value);
	    }
	    else if (name.equals("noshow_cnt")) {
		bill.setNoshow_cnt (value);
	    }
	    else if (name.equals("bhqa_fine")) {
		bill.setBhqa_fine (value);
	    }
	    else if (name.equals("reinsp_date")) {
		bill.setReinsp_date (value);
	    }
	    else if (name.equals("noshow_date")) {
		bill.setNoshow_date (value);
	    }
	    else if (name.equals("due_date")) {
		bill.setDue_date (value);
	    }
	    else if (name.equals("issue_date")) {
		bill.setIssue_date (value);
	    }
	    else if (name.equals("other_fee_title")) {
		bill.setOther_fee_title (value);
	    }
	    else if (name.equals("other_fee")) {
		bill.setOther_fee(value);
	    }
	    else if (name.equals("other_fee2_title")) {
		bill.setOther_fee2_title (value);
	    }
	    else if (name.equals("other_fee2")) {
		bill.setOther_fee2(value);
	    }						
	    else if (name.equals("status")){ 
		bill.setStatus(value);  
	    }
	    else if (name.equals("action")){ 
		// bill, change pay status
		action = value;  
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		// System.err.println(" no user found in session");
		String str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
	Rent rent = new Rent(id, debug);
	List<Owner> owners = null;
	Owner agent = null;
	List<Address> addresses = null;
	if(true){
	    String back = rent.doSelect();
	    if(!back.equals("")){
		message += " could not retrieve rental data "+back;
		success = false;
	    }
	    else{
		owners = rent.getOwners();
		agent = rent.getAgent();
		addresses = rent.getAddresses();
		propAddr = "";
		for(Address addr:addresses){
		    if(!propAddr.equals("")) propAddr += ", ";
		    propAddr += addr.getAddress();
		}
	    }
	}		
	//
	// we need these for default values of dates
	Calendar current_cal = Calendar.getInstance();
	today = allmonths[current_cal.get(Calendar.MONTH)+1] 
	    + "-" +current_cal.get(Calendar.DATE) + "-" +  
	    current_cal.get(Calendar.YEAR);
	//
	if(action.startsWith("Create")){
	    //
	    // billing stuff
	    //
	    String back = bill.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	else if(action.equals("Update")){
	    String back = bill.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	else if(action.startsWith("Delete")){
	    if(user.canEdit()){
		String back = bill.doSelect();
		if(back.equals("")){
		    id = bill.getId();
		    rent = new Rent(id, debug);
		    back = rent.doSelect();
					
		}
		back = bill.doDelete();
		if(!back.equals("")){
		    message += back;
		    success = false;
		}
		else{
		    message = "Deleted+Successfully";					
		    String str = url+"BillServ?id="+id+"&message="+message;
		    res.sendRedirect(str);
		    return; 
		}
	    }
	}		
	if(!message.equals("")){
	    if(!success)
		out.println("<h3><font color=\"red\">"+message+"</font></h3>");
	}
	out.println("&nbsp;<br>&nbsp;<br>&nbsp;<br><br><br><br>");
	out.println("<center><font face=\"helvetica\" size=\"+1\"><b>"+
		    "BILLING STATEMENT</font></b></center><br>");
	out.println("<b>DATE: &nbsp;</b>"+bill.getIssue_date()+"<br><br>");
	// 
	// Owner info
	// Left side table
	//
	out.println("<left><table><tr><td valign=top><b>OWNER:</b></td>");
	if(owners != null){
	    for(Owner owner:owners){
		out.println("<td><b><font size=\"-1\">");
		out.println(owner.getFullName()+"<br />");
		out.println(owner.getAddress()+"<br />");
		out.println(owner.getCityStateZip()+"</b></font></td>");
	    }
	}
	out.println("</tr>");
	//
	// Extra space
	out.println("<tr><td>&nbsp;</td></tr>");
	out.println("<tr><td valign=top><b>AGENT:</b></td><td>");
	out.println("<font size=-1><b>");
	if(agent != null){
	    out.println(agent.getFullName()+"<br>");
	    out.println(agent.getAddress()+"<br>");
	    out.println(agent.getCityStateZip());
	}
	out.println("</b></font></td></tr></table>");
	//
	out.println("<br>");
	//
	// prop address
	out.println("<left><table><tr><td>"+
		    "<b>RENTAL PROPERTY ADDRESS:</b></td><td>");
	out.println(propAddr+"</td></tr>");
	out.println("<tr><td align=right><b>NUMBER OF UNITS:</b>");
	out.println("</td><td>"+bill.getUnit_cnt()+"</td></tr>");
	out.println("<tr><td align=right><b>NUMBER OF BUILDINGS:</b>");
	out.println("</td><td>"+bill.getBul_cnt()+"</td></tr>");
	if(rent.getProp_type().startsWith("Room")){
	    out.println("<tr><td align=right><b>NUMBER OF BATHROOMS:</b>");
	    out.println("</td><td>"+bill.getBath_cnt()+"</td></tr>");
	}
	out.println("</table>");
	out.println("<center><h3>ASSESSMENT</h3></center>");
	//
	if(bill.getAppeal().equals("")){
	    //
	    // inspection fees
	    out.println("<left><table><tr><td align=right>"+
			"<b>Inspection Fee:</b></td><td align=\"right\"> ");
	    out.println(curformat.format(bill.getInsp_fee())+"</td></tr>");
	    out.println("<tr><td align=\"right\">"+
			"<b>Reinspection Fee:</b></td><td align=\"right\"> ");
	    out.println(curformat.format(bill.getReinsp_fee())+"</td><td>"+
			bill.getReinsp_date()+
			"</td></tr>");
	    out.println("<tr><td align=right>"+
			"<b>No Show Fee:</b></td><td align=right> ");
	    out.println(curformat.format(bill.getNoshow_fee())+"</td><td>"+
			bill.getNoshow_date()+
			"</td></tr>");
	    out.println("<tr><td align=\"right\"><b>"+
			"Failure to Timely Provide Summary of Rights & "+
			"Responsibilities:</b></td><td valign=\"bottom\" align=\"right\"> ");
	    out.println(curformat.format(bill.getSummary_fee())+"</td><td>"+
			"</td></tr>");
	    out.println("<tr><td align=\"right\"><b>"+
			"Failure to Timely Provide Inventory & Damage "+
			"List:</b>"+
			"</td><td valign=\"bottom\" align=\"right\"> ");
	    out.println(curformat.format(bill.getIDL_fee())+"</td><td>"+
			"</td></tr>");
	    if(bill.getCredit() > 0){
		out.println("<tr><td align=\"right\">"+
			    "<b>Credits:</b></td><td align=\"right\"> ");
		out.println(curformat.format(bill.getCredit())+"</td><td>"+
			    "</td></tr>");
	    }
	    out.println("<tr><td align=\"right\">"+
			"<b>Fines:</b></td><td align=\"right\"> ");
	    out.println(curformat.format(bill.getBhqa_fine())+"</td><td>&nbsp;"+
			"</td></tr>");
	    if(!bill.getOther_fee_title().equals("")){
		out.println("<tr><td align=\"right\"><b>"+ bill.getOther_fee_title()+"</b></td><td align=\"right\">"+curformat.format(bill.getOther_fee())+"</td></tr>");
	    }
	    if(!bill.getOther_fee2_title().equals("")){
		out.println("<tr><td align=\"right\"><b>"+ bill.getOther_fee2_title()+"</b></td><td align=\"right\">"+curformat.format(bill.getOther_fee2())+"</td></tr>");
	    }						
	}
	else{
	    // appeal purpose 
	    out.println("<left><table><tr><td align=\"right\">"+
			"<b>APPEALS:</b></td><td>");
	    if(bill.getAppeal_fee() > 0){
		out.println(curformat.format(bill.getAppeal_fee()));
	    }
	    out.println("</td></tr>");
	    out.println("<td>&nbsp;</td><td>&nbsp;"+
			"</td></tr>");

	}
	//
	// total
	//
	out.println("<tr><td align=\"right\">"+
		    "<b>TOTAL AMOUNT DUE:</b></td><td align=\"right\"> ");
	out.println(curformat.format(bill.getTotal()));
	out.println("</td></tr>");
	out.println("<tr><td align=\"right\">DUE BY: </td><td>"+bill.getDue_date()+"</td></tr>");
										
	out.println("</table>");
	out.println("* RENTAL PERMIT WILL BE ISSUED UPON RECEIPT OF "+
		    "PAYMENT </font></b>");
	out.println("<br><br>");
	out.println("<p>Cash, check, money order and credit cards are acceptable payment types. ");
	out.println("Please make your check or money order payable to \"City of Bloomington\". ");
	out.println("A copy of this statement must be returned with "+
		    "your payment within 30 days to: City of "+
		    "Bloomington, Housing and Neighborhood Development,");
	out.println(" P.O. Box 100, Bloomington, IN 47402. ");
	out.println("</p>");
	//
	out.println("<p><b>If payment is not received within 30 days, any long-term occupancy permit will ");
	out.println("revert to a three-year permit, and this matter will be referred to the City Legal Department.");
	out.println("You are responsible for all fees incurred regardless of whether you complete the entire inspection process or the property will no longer be used as a rental.");
	out.println("</b></p>");
	//
	out.println("</body>");
	out.println("</html>");		
    }
    //
    /**
     * Generates the query form for bills.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	boolean success = true;
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name="", value="";
	String id="",str="", message="";
	int total=0;
	//
	// these are the default fees that could change on
	// annual basis
	//
	String bul_rate="50",  // building rate
	    unit_rate="12",    // unit rate
	    bath_rate="12",    // 
	    reinsp_rate="50",  // reinspection fees
	    noshow_rate="35",  // no show fees
	    appeal_fee="20",
	    summary_rate="25",
	    IDL_rate="25";
	String bul_cnt="1",unit_cnt="1",bath_cnt="", insp_fee="";
	String reinsp_date="",reinsp_cnt="",due_date="",noshow_date="";
	String action="",bid="",issue_date="",status="Unpaid",bhqa_fine="",
	    noshow_cnt="", paidSum="0",balance="0",prop_type="",
	    appeal="",credit="0",summary_flag="",
	    IDL_flag="";
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
		
	Bill bill = new Bill(debug);
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("bid")){
		bid = value;
		bill.setBid(value);
	    }
	    else if (name.equals("id")){
		id = value;
		bill.setId(id);
	    }
	    else if (name.equals("message")){
		message = value;
	    }
	    else if (name.equals("action")){
		action = value;
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
	Calendar cal = Calendar.getInstance();
	cal.add(Calendar.MONTH, 1);
	String nextMonth = ""+(cal.get(Calendar.MONTH)+1) 
	    + "/" +cal.get(Calendar.DATE) + "/" +  
	    cal.get(Calendar.YEAR);
	//
	Rent rent = null;
	if(action.equals("zoom")){
	    String back = bill.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		rent = bill.getRent();
		id = bill.getId();
		bid = bill.getBid();
	    }
	}
	else{
	    rent = new Rent(id, debug);
	    String back = rent.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    bill.setIssue_date(Helper.getToday());
	    bill.setDue_date(nextMonth);
	    unit_cnt = rent.getUnits();
	    bul_cnt = rent.getStructureCount();
	    bath_cnt = rent.getBath_count();
	    bill.setUnit_cnt(unit_cnt);
	    bill.setBul_cnt(bul_cnt);
	    bill.setBath_cnt(bath_cnt);
	}

	//
	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");		
	out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "+
		    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"); 
	out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
		
	out.println("<head><title>Rental Billing </title>");
	out.println("<script type=\"text/javascript\">");
	out.println("//<![CDATA[  ");		
	out.println("  function validateYear(yy){	           ");
	out.println(" var len = yy.length;                         ");
	out.println(" if(!(len == 2 || len == 4)){                 ");
	out.println("     return false;				   ");
	out.println("  }else {                         ");
	out.println("    if(isNaN(yy)){      ");
	out.println("     return false;				   ");
	out.println("  }}                                           ");
	out.println("     return true;				   ");
	out.println("  }                                           ");
	out.println("  function validateDay(dd){	           ");
	out.println(" var len = dd.length;                         ");
	out.println("    if(isNaN(dd)){                  "); 
	out.println("     return false;				   ");
	out.println("    }                             ");
	out.println("    if(len == 2){                  "); 	
	out.println("     if(dd.charAt(0) < 0 || dd.charAt(0) > 3 ){    ");
	out.println("     return false;				   ");
	out.println("     } else if(!dd.charAt(0) < 3 && dd.charAt(1) > 9){");
	out.println("     return false;				   ");
	out.println("     } else if(!dd.charAt(0) == \"3\" && dd.charAt(1) > 1){");
	out.println("     return false;				    ");
	out.println("    }}			                    ");
	out.println("     return true;				    ");
	out.println("  }                                            ");
	//
	out.println("  function validateForm(){		            ");

	out.println("     return true;				    ");
	out.println("	}	         			    ");
        out.println("  function makeSure(){                         ");
	out.println(" var rep = confirm(\"Are you sure you want to change the"+
		    " payment status, the status should be changed only after"+
		    " the bill is paid.\");                ");
	out.println("  if(rep){                            ");
	out.println("    document.myForm.submit();         ");
	out.println("    }                                 ");
	out.println("  }                                   ");
        out.println("  function makeSure2(){               ");
	out.println(" var rep = confirm(\"Are you sure you want to change the"+
		    " payment status, the status should be changed only after"+
		    " a bill is issued.\");                ");
	out.println("  if(rep){                            ");
	out.println("    document.myForm.submit();         ");
	out.println("    }                                 ");
	out.println("  }                                   ");
        out.println("  function updateDate(){              ");
	out.println("  var today = document.myForm.issue_date.value;  ");
	out.println("  var len = today.length;              ");
	out.println("  if(len == 0) return;                 ");
	out.println("  var n1 = today.indexOf('/');         ");
	out.println("   var mon = today.substr(0,n1);       ");
	out.println("   var rest = today.substr(n1+1,len);  ");
	out.println("  var n2 = rest.indexOf('/');          ");
	out.println("  var len2 = rest.length;              ");
	out.println("   var day = rest.substr(0,n2);        ");
	out.println("   var yyyy = rest.substr(n2+1,len2);  ");
	out.println("   if(!validateMonth(mon)){            ");
	out.println("   alert(\"invalid month \"+mon);      ");
	out.println("   return;                             ");
	out.println("   }                                   ");
	out.println("   if(!validateDay(day)){              ");
	out.println("   alert(\"invalid date \"+day);       ");
	out.println("   return;                             ");
	out.println("   }                                   ");
	out.println("   if(!validateYear(yyyy)){            ");
	out.println("   alert(\"invalid year \"+yyyy);      ");
	out.println("   return;                             ");
	out.println("   }                                   ");
	out.println(" var curDate = new Date();             ");
	out.println("      curDate.setMonth(mon-1);      "); //this nonth
	out.println("      curDate.setDate(day);            ");
	out.println("      curDate.setYear(yyyy);           ");
	out.println(" var nextDate = new Date(yyyy,mon-1,day); ");
	out.println("  nextDate.setDate(curDate.getDate()+31); ");// 31 later

	out.println("     mon =  nextDate.getMonth()+1;      ");
	out.println("     day =  nextDate.getDate();         ");
	out.println("     yyyy =  nextDate.getFullYear();    ");
	out.println("document.myForm.due_date.value=mon+\"/\"+day+\"/\"+yyyy;");
	out.println("  }                                       ");
	out.println("  function doSum(){                       ");
	out.println("  var prop_type = document.myForm.prop_type.value;  ");
	out.println("  var total = 0;                          ");
	out.println("  var cr = document.myForm.bul_rate.value;       ");
	out.println("  var cn = document.myForm.bul_cnt.value;        ");
	out.println("  var af = document.myForm.appeal_fee.value;     ");
	out.println("  var cd = document.myForm.credit.value;         ");
	out.println("  var smr = document.myForm.summary_rate.value;  ");
	out.println("  var idl = document.myForm.IDL_rate.value;      ");
	out.println("  var smn = document.myForm.summary_cnt.value;   ");
	out.println("  var idn = document.myForm.IDL_cnt.value;   ");	
	// appeal only
	out.println("  if(document.myForm.appeal.checked){        ");
	out.println("    total += af*1;                           ");
	out.println("    document.myForm.insp_fee.value=0;        ");
	out.println("   }                                         ");
	out.println("   else{                                     ");
	// non appeal
	out.println("  if(cr > 0 && cn > 0){                      ");
	out.println("      total += cr*cn;                            ");
	out.println("   }         ");
	out.println("  if(prop_type.indexOf('Room') == -1){        ");
	out.println("  cr = document.myForm.unit_rate.value;       ");
	out.println("  cn = document.myForm.unit_cnt.value;        ");
	out.println("  }else{        ");
	out.println("  cr = document.myForm.bath_rate.value;  ");
	out.println("  cn = document.myForm.bath_cnt.value;   ");
	out.println("  }         ");
	out.println("  if(cr > 0 && cn > 0){    ");
	out.println("  total += cr*cn;          ");
	out.println("  }                        ");
	out.println("  if(document.myForm.summary_flag.checked){     ");
	out.println("    if(smn == 0) smn = cn;                      ");
	out.println("    total += smr*smn; }                          ");
	out.println("  if(document.myForm.IDL_flag.checked){         ");
	out.println("    if(idn == 0) idn = cn;                      ");		
	out.println("    total += idl*idn; }                          ");
	out.println("  document.myForm.insp_fee.value=total;         ");
	out.println("  if(cd > 0){                                   ");
	out.println("     total = total - cd;                        ");
	out.println("  }                                             ");
	out.println("  cr = document.myForm.reinsp_rate.value;       ");
	out.println("  cn = document.myForm.reinsp_cnt.value;        ");
	out.println("  if(cr > 0 && cn > 0){                         ");
	out.println("  total += cr*cn;                               ");
	out.println("  }                                             ");
	out.println("  cr = document.myForm.noshow_rate.value;       ");
	out.println("  cn = document.myForm.noshow_cnt.value;        ");
	out.println("  if(cr > 0 && cn > 0){                         ");
	out.println("  total += cr*cn;                               ");
	out.println("  }                                             ");
	out.println("  cr = document.myForm.bhqa_fine.value;         ");
	out.println("  if(cr > 0 ){                                  ");
	out.println("  total += cr*1;                                ");
	out.println("     }                                          ");
	out.println("  cr = document.myForm.other_fee.value;         ");
	out.println("  if(cr > 0 ){                                  ");
	out.println("  total += cr*1;                                ");
	out.println("     }                                          ");
	out.println("  cr = document.myForm.other_fee2.value;         ");
	out.println("  if(cr > 0 ){                                  ");
	out.println("  total += cr*1;                                ");
	out.println("     }                                          ");
	out.println("  }                                             ");
	out.println(" document.myForm.total.value=total;             ");
	out.println(" cr = document.myForm.paidSum.value;            ");
	out.println(" document.myForm.balance.value=total*1 - cr*1;  ");
	out.println(" }                                             ");
	out.println("  function validateDelete(){	             ");
	out.println("   var x = false;                               ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     if(x){ document.myForm.submit();              ");
	out.println("	}					         ");
	out.println("     return x;                                      ");
	out.println("	}					         ");
	out.println(" //]]>                            ");		
	out.println(" </script>		                          ");
	out.println("  </head><body onload=\"doSum()\"> ");
	if(bid.equals(""))
	    out.println("<h2 align=\"center\">New Rental Bill </h2>");
	else 
	    out.println("<h2 align=\"center\">Rental Bill "+bid+"</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=\"red\">"+message+"</font></h3>");	 
	}
	//the real table
	out.println("<form name=\"myForm\" method=\"post\" "+
		    "action=\"BillServ?\" "+
		    "onsubmit=\"return validateForm();\">");
	out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	out.println("<input type=\"hidden\" name=\"prop_type\" " +
		    " value=\""+rent.getProp_type()+"\" />");
	if(!bid.equals(""))
	    out.println("<input type=\"hidden\" name=\"bid\" value=\""+bid+"\" />");
	if(!bill.getStatus().equals(""))
	    out.println("<input type=\"hidden\" name=\"status\" " +
			" value=\""+bill.getStatus()+"\" />");		
	out.println("<input type=\"hidden\" name=\"paidSum\" "+
		    "value=\""+bill.getPaidSum()+"\" />");
	out.println("<input type=\"hidden\" name=\"balance\" "+
		    "value=\""+bill.getBalance()+"\" />");
		
	out.println("<table align=\"center\" width=\"90%\" border=\"1\">");
	out.println("<tr><td bgcolor=\""+bgcolor+"\" align=\"center\">");
	//
	// Add/Edit record
	//
	out.println("<table width=\"100%\">");
	//
	// Fee rates
	out.println("<tr><td colspan=\"4\" align=\"center\">Fees </td></tr>");
	// row 1
	out.println("<tr><td align=\"right\"><b>Building:</b></td>");
	out.println("<td>$<input name=\"bul_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getBul_rate()+"\""+
		    " size=\"8\" /></td>");
	out.println("<td align=\"right\"><b>Unit:</b></td>");
	out.println("<td>$<input name=\"unit_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getUnit_rate()+
		    "\" size=\"8\" /></td></tr>");
	// row 2
	out.println("<tr><td align=\"right\"><b>Room/bath:</b></td>");
	out.println("<td>$<input name=\"bath_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getBath_rate()+
		    "\" size=\"8\" /></td>");
	out.println("<td align=\"right\"><b>Reinspection Fee:</b></td>");
	out.println("<td>$<input name=\"reinsp_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getReinsp_rate()+
		    "\" size=\"8\" /></td></tr>");
	// row 3
	out.println("<tr><td align=\"right\"><b>No Show Fee:</b></td>");
	out.println("<td>$<input name=\"noshow_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getNoshow_rate()+
		    "\" size=\"8\" /></td>");
	out.println("<td align=\"right\"><b>B.H.Q.A Fine</b></td>");
	out.println("<td>$<input name=\"bhqa_fine\" maxlength=\"8\" "+
		    "value=\""+bill.getBhqa_fine()+"\" onchange=\"doSum()\" "+
		    "size=\"8\" /></td></tr>");
	// row 4
	out.println("<tr><td align=\"right\"><b>Summary Failure Rate:</b></td>");
	out.println("<td>$<input name=\"summary_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getSummary_rate()+
		    "\" size=\"8\" /></td>");
	out.println("<td align=\"right\"><b>IDL rate:</b></td><td>"+
		    "$<input name=\"IDL_rate\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getIDL_rate()+
		    "\" size=\"8\" /></td></tr>");
	// row 5
	out.println("<tr><td align=\"right\"><b>Appeal Fee:</b></td>");
	out.println("<td>$<input name=\"appeal_fee\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" "+
		    "value=\""+bill.getAppeal_fee()+"\" size=\"8\" /></td>");
	out.println("<td align=\"right\"><b>Credits:</b></td><td>"+
		    "$<input name=\"credit\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" "+
		    "value=\""+bill.getCredit()+"\" size=\"8\" /></td></tr>");
	out.println("<tr><td align=\"right\"><b>Property Type:</b></td>");
	out.println("<td colspan=\"3\">"+rent.getProp_type()+" (if this is not correct fixed on the registration page)</td></tr>");
	//
	out.println("</table></td></tr>");
	//
	// Second table
	out.println("<tr><td><table width=\"100%\">");
	//
	out.println("<tr><td align=\"right\"><b>Buildings:</b></td><td>");
	out.println("<input name=\"bul_cnt\" maxlength=\"3\" "+
		    "onchange=\"doSum()\" value=\""+bill.getBul_cnt()+"\""+
		    " size=\"3\" />&nbsp;<b>Units:</b>");
	out.println("<input name=\"unit_cnt\" maxlength=\"3\" "+
		    "onchange=\"doSum()\" value=\""+bill.getUnit_cnt()+"\""+
		    " size=\"3\" />&nbsp;<b>Rooms/bath:</b>");
	out.println("<input name=\"bath_cnt\" maxlength=\"3\" "+
		    "onchange=\"doSum()\" value=\""+bill.getBath_cnt()+"\""+
		    " size=\"4\" />");
	out.println("</td></tr>");
	//
	out.println("<tr><td align=\"right\"><b>Inspection Fee:</b></td>");
	out.println("<td>$<input name=\"insp_fee\" maxlength=\"8\" "+
		    "onchange=\"doSum()\" value=\""+bill.getInsp_fee()+
		    "\" size=\"8\" />&nbsp;&nbsp;");
	appeal = bill.getAppeal();
	if(!appeal.equals("")) appeal = "checked=\"checked\"";
	out.println("<input type=\"checkbox\" name=\"appeal\" value=\"Y\" "+appeal+
		    " onclick=\"doSum()\""+
		    " /><b>Appeal Bill</b></td></tr>");
	//
	out.println("<tr><td align=\"right\"><b>Number of Reinspections</b></td>");
	out.println("<td><input name=\"reinsp_cnt\" maxlength=\"3\" "+
		    "onchange=\"doSum()\" "+
		    "value=\""+bill.getReinsp_cnt()+"\" size=\"3\" />");
	out.println("&nbsp;&nbsp;<b>Number of No Show</b>");
	out.println("<input name=\"noshow_cnt\" maxlength=\"3\" "+
		    "onchange=\"doSum()\" "+
		    "value=\""+bill.getNoshow_cnt()+"\" size=\"3\" /></td></tr>");
	//
	out.println("<tr><td align=\"right\"><b>Dates of Reinspections</b></td>");
	out.println("<td><input name=\"reinsp_date\" maxlength=\"80\" "+
		    "value=\""+bill.getReinsp_date()+"\" size=\"40\" />");
	out.println("</td></tr>");
	//
	out.println("<tr><td align=\"right\">");
	summary_flag = bill.getSummary_flag();
	if(!summary_flag.equals("")) summary_flag="checked=\"checked\"";
	out.println("<input type=\"checkbox\" name=\"summary_flag\" value=\"Y\" "+
		    summary_flag+" onclick=\"doSum()\""+
		    " /></td><td><b>Summary Failure Fees</b>");
	out.println("&nbsp; <b>Count</b><input name=\"summary_cnt\" maxlength=\"2\" "+
		    " onchange=\"doSum()\" "+
		    " value=\""+bill.getSummary_cnt()+"\" size=\"2\" /></td></tr>");		
	//
	out.println("<tr><td align=\"right\">");
	IDL_flag = bill.getIDL_flag();
	if(!IDL_flag.equals("")) IDL_flag="checked=\"checked\"";
	out.println("<input type=\"checkbox\" name=\"IDL_flag\" value=\"Y\" "+IDL_flag+
		    " onclick=\"doSum()\" "+
		    " /></td><td><b>IDL Fees</b>");
	out.println("&nbsp; <b>Count</b><input name=\"IDL_cnt\" maxlength=\"2\" "+
		    " onchange=\"doSum()\" "+
		    "value=\""+bill.getIDL_cnt()+"\" size=\"2\" /></td></tr>");
	//
	out.println("<tr><td align=\"right\"><b>Dates of No Show</b></td>");
	out.println("<td><input name=\"noshow_date\" maxlength=\"80\" "+
		    "value=\""+bill.getNoshow_date()+"\" "+
		    "size=\"40\" /></td></tr>");
	out.println("<tr><td align=\"right\" colspan=\"2\">You may add other Fees below (if any)</td></tr>");
	out.println("<tr><td align=\"right\">Fee Title: <input name=\"other_fee_title\" maxlength=\"80\" value=\""+bill.getOther_fee_title()+"\" "+
		    "size=\"30\" /></td>");
	out.println("<td align=\"left\">Value: <input name=\"other_fee\" maxlength=\"8\" value=\""+bill.getOther_fee()+"\" size=\"8\" /></td></tr>");
	//
	out.println("<tr><td align=\"right\">Fee Title: <input name=\"other_fee2_title\" maxlength=\"80\" value=\""+bill.getOther_fee2_title()+"\" "+
		    "size=\"30\" /></td>");
	out.println("<td align=\"left\">Value: <input name=\"other_fee2\" maxlength=\"8\" value=\""+bill.getOther_fee2()+"\" size=\"8\" /></td></tr>");										
	out.println("<tr><td align=\"right\"><b>Total:</b>$</td>");
	out.println("<td><input name=\"total\" maxlength=\"8\" value=\""+
		    bill.getTotal()+"\" size=\"8\" />");
	out.println("&nbsp;<b>Status:</b>"+bill.getStatus()+"</td></tr>");
	//
	out.println("<tr><td align=\"right\"><b>Bill Issue Date:</b></td>");
	out.println("<td><input name=\"issue_date\" maxlength=\"10\" "+
		    "onchange=\"updateDate()\" value=\""+
		    bill.getIssue_date()+"\" size=\"10\" />&nbsp;<b>Due by:</b>");
	out.println("<input name=\"due_date\" maxlength=\"10\" "+
		    "value=\""+bill.getDue_date()+"\" size=\"10\" />");
	out.println("</td></tr>");
	if(!bid.equals("")){
	    out.println("<tr><td align=\"right\"><b>Total Paid:</b></td><td>$");
	    out.println(bill.getPaidSum());
	    out.println("&nbsp;&nbsp;<b>Balance:</b>$"+bill.getBalance());
	    out.println("</td></tr>");
	}
	out.println("</table></td></tr>");
	//
	// submit
	if(user.canEdit()){
	    if(bid.equals("")){
		out.println("<tr><td><table width=\"100%\"><tr>");
		out.println("<td align=\"right\" "+
			    "valign=\"top\"><input type=\"submit\" name=\"action\" "+
			    "value=\"Create Bill\" /></td>");
		out.println("</tr></table></td></tr>");
		out.println("</table>");
	    }
	    else{
		out.println("<tr><td><table width=\"100%\"><tr>");
		if(user.canDelete()){
		    out.println("<td align=\"right\" "+
				"valign=\"top\"><input type=\"submit\" name=\"action\" "+
				"value=\"Update\" /></td>");
		}
		if(bill.getBalance() > 0){
		    out.println("<td align=\"right\" valign=\"top\">");
		    out.println("<button type=\"button\" onclick=\"document.location="+		
				"'"+url+"ReceiptServ?bid="+bid+"&id="+id+"'\" "+
				">Receipt</butoon></td>");
		}
		out.println("</tr></table></td></tr>");
		if(user.canDelete()){
		    out.println("<tr><td colspan=\"2\" align=\"right\">");
		    out.println("<input type=\"submit\" name=\"action\" "+
				" onclick=\"validateDelete()\" "+
				"value=\"Delete This Bill\" />");
		    out.println("</td></tr>");
		}
		out.println("</table>");
	    }		
	}
	out.println("</form>");
	//
	// Check if there are bills issued before
	//
	if(!id.equals("")){
	    BillList bills = new BillList(debug, id);
	    String back = bills.find();
	    if(back.equals("") && bills.size() > 0){
		out.println("<br />");
		out.println("<h3 align=\"center\"><a href=\""+url+"BillListServ?id="+id+"\">Show Billing History</a></h3>");
	    }
	}
	out.println("<p align=\"center\">");
	out.println("<a href=\"javascript:window.close()\">Close This Window</a>");
	out.println("<br />");
	out.println("<br />");
	out.println("</p>");
	out.print("</body></html>");
	out.close();
    }

}























































