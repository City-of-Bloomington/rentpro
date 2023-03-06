package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.sql.*;
import javax.naming.directory.*;
import javax.naming.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/CheckAddress"})
public class CheckAddress extends TopServlet{
    final static long serialVersionUID = 180L;
    final static String bgcolor = "silver";// #bfbfbf gray
    static Logger logger = LogManager.getLogger(CheckAddress.class);
    //
    /**
     * Generates the check address form.
     * 
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String action="",address="",street_num="";
	String street_name="", street_dir="", street_type="", post_dir="";
	String sud_num="", sud_type="",invalid_addr="",
	    street_opt="starts with",
	    checkType="Master Address";
	//
	HttpSession session = null;
	session = req.getSession(false);
	boolean success = true;
	String allQueryOptions ="<option>\n<option>is<option>contains"+
	    "<option>starts with<option>ends with</select>";
	String id="", message="";

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
			
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    //
	    if (name.equals("id")) {
		id = value;
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	    else if (name.equals("address")) {
		address = value.toUpperCase();
	    }
	    else if (name.equals("street_num")) {
		street_num =value;
	    }
	    else if (name.equals("street_opt")) {
		street_opt =value;
	    }
	    else if (name.equals("street_dir")) {
		street_dir =value;
	    }
	    else if (name.equals("street_name")) {
		street_name =value.toUpperCase();
	    }
	    else if (name.equals("street_type")) {
		street_type =value;
	    }
	    else if (name.equals("post_dir")) {
		post_dir =value;
	    }
	    else if (name.equals("sud_num")) {
		sud_num = value; 
	    }
	    else if (name.equals("sud_type")) {
		sud_type = value;  
	    }
	    else if (name.equals("checkType")) {
		checkType = value; 
	    }
	}
	//
	out.println("<html><head><title>Rental</title>");
	Helper.writeWebCss(out, url);	
	out.println("<script language=javascript>");
	out.println(" function validateForm(){		                ");
	out.println(" if((document.myForm.street_name.value.length == 0)){ "); 
	out.println("     alert(\"Street name is needed\" );   ");
	out.println("    return false;					    ");
	out.println("	}						    ");
	out.println("    return true ;					    ");
	out.println(" }				    ");
	out.println("</script>			    ");
	//
	out.println(" </head><body onload=\"document.myForm.street_name.focus()\" >");
	Helper.writeTopMenu(out, url);	
	out.println("<center><h2>Check Address"+
		    "</h2>");
	if(action.equals("Browse")){
	    if(!message.equals("")){
		if(success)
		    out.println("<h3><font color=red>"+message+
				"</font></h3>");
		else
		    out.println("<h3>"+message+"</h3>");
	    }
	}
	out.println("<form name=myForm method=post onsubmit=\"return "+
		    "validateForm()\">");
	out.println("<font color=green size=-1>Enter basic address fields "+
		    "to get a list of valid addresses.<br>");
	out.println("<font color=green>If the list is long you can narrow "+
		    "the search by entering additional fields. </font><br><br>");
	out.println("<table width=95% border><tr><td>");
	out.println("<table width=100%>");
	//
	// st num
	out.println("<tr><td>Street&nbsp;Num:</td><td>");
	out.println("<input name=street_num size=6 maxlength=6 value="+
		    street_num+">");
	out.println("</td></tr>");
	out.println("<tr><td>Street&nbsp;Dir:</td><td>");
	out.println("<select name=street_dir>");
	out.println("<option selected>"+street_dir);
	out.println(Rental.allStreetDir+"</td></tr>");
	out.println("<tr><td>Street&nbsp;Name:<font color=green>*</font></td><td>");
	out.println("<select name=street_opt>");
	out.println("<option selected>"+street_opt);
	out.println(allQueryOptions);
	out.println("<input type=text name=street_name maxlength=30 value=\""+
		    street_name+"\" size=15>");
	out.println("</td></tr>");
	//
	// st type
	out.println("<tr><td>Street&nbsp;Type:</td><td>");
	out.println("<select name=street_type>");
	if(street_type.equals("")){	    
	    out.println("<option selected value=\"\">\n");
	}
	else{
	    for(int i=0; i<Rental.streetKeys.length; i++){
		if(Rental.streetKeys[i].equals(street_type)){
		    out.println("<option selected value=\""+street_type+
				"\">"+Rental.streetInfo[i]);
		    break;
		}
	    }
	}
	out.println(Rental.allStreetType+"</td></tr>");	    
	//
	// post dir
	out.println("<tr><td>Post Dir:</td><td>");
	out.println("<select name=post_dir>");
	out.println("<option selected>"+post_dir);
	out.println(Rental.allStreetDir+"</td></tr>");
	//
	// sud type
	out.println("<tr><td>Sud Type:</td><td>");
	out.println("<select name=sud_type>");
	for(int i=0; i<Rental.sudKeys.length; i++){
	    if(sud_type.equals(Rental.sudKeys[i]))
		out.println("<option selected value=\""+sud_type+"\">"+
			    Rental.sudInfo[i]);
	}
	out.println(Rental.allSudTypes+"</td></tr>");
	//
	// sud num
	out.println("<tr><td>Sud Num:</td><td>");
	out.println("<input type=text name=sud_num maxlength=4 size=4 "+
		    ">");
	out.println("</td></tr>");
	out.println("<tr><td>Check with:</td><td>");
	out.println("<select name=checkType>");
	out.println("<option selected>"+checkType);
	out.println("<option>Master Address");
	out.println("<option>Rental Address");
	out.println("</select>");
	out.println("</td></tr></table></td></tr>");
	out.println("<tr><td align=right>  "+
		    "<input type=submit name=action "+
		    "value=Browse></td></tr>"); 
	out.println("</table>");
	out.println("</form>");
	out.println("<font color=green>* Required field</font><br>");
	//
	out.print("<br>");
	out.println("<a href=\"javascript:window.close();\">Close This "+
		    "Window</a><br><br>");
	out.flush();
	// 
	out.print("</body></html>");
	out.close();

    }
    /**
     * @link #doGet
     * @see #doGet
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String username = "", message="";
	String name, value;
	String action="",address="",street_num="";
	String street_name="", street_dir="", street_type="", post_dir="";
	String sud_num="", sud_type="",invalid_addr="",street_opt="is",
	    checkType="Master Addresses";
	//
	HttpSession session = null;
	session = req.getSession(false);
	Connection con = null;
	Statement stmt = null, stmt2 = null;
	ResultSet rs = null, rs2 = null;
	boolean success = true;
	String allQueryOptions ="<option>\n<option>is<option>contains"+
	    "<option>starts with<option>ends with</select>";
	String id="";
	Enumeration<String> values = req.getParameterNames();
	Address addr = new Address(debug);
	String [] vals;
		
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    //
	    if (name.equals("id")) {
		id = value;
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	    else if (name.equals("address")) {
		address = value.toUpperCase();
	    }
	    else if (name.equals("street_num")) {
		addr.setStreet_num(value);
		street_num = value;
	    }
	    else if (name.equals("street_opt")) {
		street_opt =value;
	    }
	    else if (name.equals("street_dir")) {
		addr.setStreet_dir(value);
		street_dir = value;
	    }
	    else if (name.equals("street_name")) {
		addr.setStreet_name(value.toUpperCase());
		street_name = value.toUpperCase();
	    }
	    else if (name.equals("street_type")) {
		addr.setStreet_type(value);
		street_type = value;
	    }
	    else if (name.equals("post_dir")) {
		addr.setPost_dir(value);
		post_dir = value;
	    }
	    else if (name.equals("sud_num")) {
		addr.setSud_num(value);
		sud_num = value;
	    }
	    else if (name.equals("sud_type")) {
		addr.setSud_type(value);
		sud_type = value;
	    }
	    else if (name.equals("checkType")) {
		checkType = value; 
	    }
	}
	if(checkType.startsWith("Rental")){
	    try{
		Context initCtx = new InitialContext();
		Context envCtx = (Context) initCtx.lookup("java:comp/env");
		DataSource ds = (DataSource)envCtx.lookup("jdbc/oracle_rent");
		con = ds.getConnection();
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
	}
	//
	out.println("<html><head><title>Rental</title>");
	//
	out.println(" </head><body>");
	out.println("<center><h2>Check Address</h2>");
	if(action.equals("Browse")){
	    if(!success && !message.equals("")){
		out.println("<h3><font color=red>"+message+"</font></h3>");
	    }
	}
	// 
	// give a list of similar addresses 
	//
	if(!action.equals("")){
	    String qc = "", qq="";
	    String qw = "", str="";
	    if(checkType.startsWith("Master")){
		List<Address> addresses = null;
		AddressList al = new AddressList(debug);
		al.setAddress(addr);
		String back = al.findSimilarAddr(checkAddrUrl);
		if(back.equals("")){
		    addresses = al.getAddresses();
		}
		if(addresses == null || addresses.size() == 0){
		    out.println("<font color=red>");
		    out.println("<br><h3>No match found</h3>");
		}
		else{
		    out.println("Total Matching: " +addresses.size()+"<br>");
		    out.println("<table border=1><tr><th>Address</th></tr>");
		    for(Address adr: addresses){
			str = adr.getStreetAddress();
			if(adr != null){
			    List<Subunit> subs = adr.getSubunits();
			    if(subs == null || subs.size() == 0){
				out.println("<tr><td>"+str+"</td></tr>");
			    }
			    else{
				for(int j=0;j<subs.size();j++){
				    Subunit sub = subs.get(j);
				    out.println("<tr><td>"+str+" "+sub.getAddress()+"</td></tr>");	
				}
			    }
			}
		    }
		    out.println("</table>");					
		}
	    }
	    else{
		// rental
		//
		qc = "select count(*) from address2 a ";  
		// 
		// if ignore direction
		//
		if(!street_name.equals("")){
		    if(street_opt.equals("is")|| street_opt.equals(""))
			qw += " a.street_name='"+street_name+"'";
		    else if(street_opt.startsWith("start"))
			qw += " a.street_name like '"+street_name+"%'";
		    else if(street_opt.startsWith("ends"))
			qw += " a.street_name like '%"+street_name+"'";
		    else 
			qw += " a.street_name like '%"+street_name+"%'";
		}
		if(!street_type.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " a.street_type='"+street_type+"'";
		} 
		if(!street_dir.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " a.street_dir='"+street_dir+"'";
		}
		if(!street_num.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " a.street_num='"+street_num+"'";
		}
		if(!sud_num.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " a.sud_num='"+sud_num+"'";
		}
		if(!sud_type.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " a.sud_type='"+sud_type+"'";
		}
		if(!post_dir.equals("")){
		    if(!qw.equals("")) qw += " and ";
		    qw += " a.post_dir='"+post_dir+"'";
		}
		if(!qw.equals("")) qw += " and ";				
		qw += " (a.invalid_addr !='Y' or a.invalid_addr is null) ";
		qw = " where "+qw;
		qq = qc+qw;
		try{
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    int nct = 0;
		    if(rs.next()){
			nct = rs.getInt(1);
		    }
		    if(debug)
			System.err.println(nct);
		    if(nct == 0){
			out.println("<font color=red>");
			out.println("<br><h2>No matching address found</h2>");
			out.println("<h3>Retry your search with the  basic "+
				    "address components only <br>such as Street "+
				    "Name.</h3>");
			out.println("<h3><a href=\"#\" "+
				    "onClick=\"history.go(-1)\">"+
				    "Back To Address Search</a></h3>");
		    }
		    else{
			String that="";
			out.println("Total Matching: " +nct+"<br>");
			qq = "select ";
			qq += " registr_id, ";
			qq += " a.street_num||' '||a.street_dir||' '||"+
			    "initcap(a.street_name)"+
			    "||' '||a.street_type||' '||a.post_dir||' '||"+
			    "a.sud_type||' '||a.sud_num ";
			qq += ",to_char(r.permit_expires,'mm/dd/yyyy') ";  
			qq += " from address2 a,registr r";
			qw += " and a.registr_id=r.id ";
						
			qq += qw +" order by a.street_name,a.street_dir,"+
			    "lpad(a.street_num,6,'0')";
			if(debug){
			    System.err.println(qq);
			    logger.debug(qq);
			}
			rs = stmt.executeQuery(qq);
			out.println("<table border cellspacing=4 "+
				    "cellpadding=2><tr>");
			out.println("<th>Permit ID</th>");
			out.println("<th>Valid Address </th>");
			out.println("<th>Expires</th>");
			out.println("</tr>");
			while(rs.next()){
			    out.println("<tr>");
			    str = rs.getString(1);
			    if(str == null)str = "&nbsp;";
			    that = str;
			    out.println("<td>"+that+"</td>");
			    str = rs.getString(2);
			    if(str == null) str = "&nbsp;";
			    out.println("<td>"+str+"</td>");
			    str = rs.getString(3);
			    if(str == null) str = "&nbsp;";
			    out.println("<td>"+str+"</td>");
			    out.println("</tr>");
			}
			out.println("</table>");
		    }
		}
		catch(Exception ex){
		    System.err.println(ex);
		    logger.error(qq+" : "+ex);
		}
	    }
	}
	out.println("<br><a href=\"javascript:window.close();\">Close This "+
		    "Window</a><br><br>");
	out.print("</body></html>");
	out.close();
	Helper.databaseDisconnect(con,stmt,rs);				
    }

}






















































