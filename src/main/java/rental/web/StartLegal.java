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

@WebServlet(urlPatterns = {"/StartLegal"})
public class StartLegal extends TopServlet{

    final static long serialVersionUID = 910L;
    static Logger logger = LogManager.getLogger(StartLegal.class);
    final static String bgcolor = "silver";// #bfbfbf gray
    List<CaseType> caseTypes = null;

    String mysqlDbStr = "";
    //
    /**
     * Generates the varince form and processes view, add, update and delete
     * operations.
     *
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
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
	String name, value;
	String action="",reason="",startDate="",status="",
	    startBy="", startByName="",
	    attention="Legal"; // from HAND to Legal (default)
	//
	HttpSession session = null;
	session = req.getSession(false);

	String id="", rental_id="", case_id="", case_type="", message="",
	    address = "";
	boolean success = true;
	User user = null;

	Enumeration<String> values = req.getParameterNames();
	String[] vals;
	List<Address> addresses = null;
	String [] addressId = null;
	Legal legal = new Legal(debug);
	Case cCase = new Case(debug);
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		id = value;
		legal.setId(value);
	    }
	    else if (name.equals("rental_id")) {
		legal.setRental_id(value);
		rental_id = value;
	    }
	    else if (name.equals("rid")) {
		legal.setRental_id(value);
		rental_id = value;
	    }			
	    else if (name.equals("case_type")) {
		case_type = value;
		cCase.setCase_type(value);
	    }
	    else if (name.equals("reason")) {
		legal.setReason(value);
		reason = value; // we need this for update
	    }
	    else if (name.equals("attention")) {
		legal.setAttention(value);
		attention = value;
	    }
	    else if (name.equals("startBy")) {
		legal.setStartBy(value);
		startBy = value;
	    }
	    else if (name.equals("addressId")) {
		addressId = vals; // array
	    }
	    else if (name.equals("startDate")) {
		legal.setStartDate(value);
		startDate = value;
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	}
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=StartLegal&rid="+rental_id+"&id="+id;
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=StartLegal&rid="+rental_id+"&id="+id;
	    res.sendRedirect(str);
	    return; 
	}
	if(caseTypes == null){
	    resetTypes();
	}
	List<Defendant> defendants = null;
	LegalList legals = new LegalList(debug, rental_id);
	legals.setStatus("both"); // New or Pending
	legals.setSortbyLast();
	message = legals.lookFor();
	System.err.println(" action "+action+" id "+id);
	//
	if(action.equals("zoom") ||
	   action.startsWith("print") ||
	   action.equals("Update") ||
	   action.equals("Edit")){
	    //
	    String back = legal.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		rental_id = legal.getRental_id();
		Case cc = legal.getCase();
		if(cc != null){
		    cCase = cc;
		    List<LegalAddress> addrs = cCase.getAddresses();
		    if(addrs != null && addrs.size() > 0){
			address = addrs.get(0).getAddress();
		    }
		}
	    }
	}
	else if(action.equals("Save") && user.canEdit()){
	    //
	    String qq = "", str="", back="";
	    Rent rent = new Rent(rental_id, debug);
	    back = rent.doSelect();
	    if(!cCase.getCase_type().equals("")){
		str = cCase.findLawyerFromCaseType(); // sets lawyerid
	    }
	    cCase.setStatus("PD"); // pending
	    back = cCase.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		case_id = cCase.getId();
	    }
	    //
	    // insert address data
	    //
	    if(addressId != null){
		for(int i=0;i<addressId.length;i++){
		    Address addr = new Address(debug, addressId[i]);
		    back = addr.doSelect();
		    if(!back.equals("")){
			logger.error("Could not retreive address "+back);
			success = false;
		    }
		    else{
			address = addr.getAddress();
			LegalAddress laddr = new LegalAddress(debug);
			laddr.setCase_id(case_id);
			laddr.setStreet_num(addr.getStreet_num());
			laddr.setStreet_dir(addr.getStreet_dir());
			laddr.setStreet_name(addr.getStreet_name());
			laddr.setStreet_type(addr.getStreet_type());
			laddr.setSud_num(addr.getSud_num());
			laddr.setSud_type(addr.getSud_type());
			laddr.setPost_dir(addr.getPost_dir());
			laddr.setInvalid_addr(addr.getInvalid_addr());
			laddr.setRental_addr("Y");
			laddr.setStreetAddress(addr.getAddress());
			back = laddr.doSave();
			if(!back.equals("")){
			    message += "Could not save address "+back;
			    success = false;
			}
		    }
		}
		legal.setStartBy(user.getUsername());
		legal.setStartByName(user.getFullName());
		legal.setStatus("New");
		legal.setAttention("Legal");
		legal.setCase_id(case_id);
		legal.setPull_date(rent.getPull_date());
		legal.setPull_reason(rent.getPullReason().getName());
		back = legal.doSave();
		if(!back.equals("")){
		    message="Could not save data "+back;
		    success = false;
		}
		else{
		    id = legal.getId();
		}
	    }
	}
	else if(action.equals("Delete") && user.isAdmin()){
	    //
	    String back = legal.doDelete();
	    if(!back.equals("")){
		message="Could not delete "+back;
		success = false;
	    }			
	}
	if(action.equals("Update")){
	    if(legal.getStartBy().equals(user.getUsername())){
		legal.setReason(reason);
		String back = legal.doUpdate();
		if(!back.equals("")){
		    message="Could not update "+back;
		    success = false;
		}
		else{
		    message="Updated Successfully";
		}
	    }
	}
	else if(action.equals("New") || action.equals("Delete") ||
		action.equals("")){
	    //
	    Rent rent = new Rent(rental_id, debug);
	    String back = rent.doSelect();						
	    legal = new Legal(debug);
	    legal.setStartBy(user.getUsername());
	    legal.setStartByName(user.getFullName());
	    legal.setStartDate(Helper.getToday());
	    legal.setPull_date(rent.getPull_date());
	    legal.setPull_reason(rent.getPullReason().getName());						
	    legal.setRental_id(rental_id);
	    legal.setAttention("Legal");
	    System.err.println("legal fullname "+legal.getStartByName());
	    reason = "";
	    startDate=Helper.getToday();id="";
	    startBy=user.getUsername();
	    AddressList al = new AddressList(debug, rental_id);
	    String str = al.lookFor();
	    if(str.equals("")){
		addresses = al.getAddresses();
	    }
	    else{
		message += "Drror retreiving addresses "+str;
		success = false;
	    }
	}
	//
	// Next step adding owners and agents (if any)
	//
	if(action.equals("Save") && success){
	    //
	    // redirect to adding owner info
	    //
	    String str = url+"LegalAddOwner?rental_id="+legal.getRental_id()+"&case_id="+legal.getCase_id()+"&id="+legal.getId();
	    res.sendRedirect(str);
	    return;
			
	}
	out.println("<html><head><title>Rental Start Legal</title>");
	Helper.writeWebCss(out, url);
	out.println("<script language=Javascript>");
	out.println("  function validateDelete(){	                      ");
	out.println("   var x = false;                                    ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                       ");
	out.println("	}						                          ");
	out.println("  function validateForm(){	                          ");
	out.println("     return true;                                    ");
	out.println("	}						                          ");
	out.println(" </script>		                                      ");
	out.println("</head><body>                                        ");
	out.println("<center><h2>Start Legal (Title 16)</h2>              ");
	Helper.writeTopMenu(out, url);	
	if(!message.equals("")){
	    if(success)
		out.println(message+"<br />");
	    else
		out.println("<font color='red'>"+message+"</font><br />");
	}
	if(action.equals("zoom") || action.startsWith("print")){
	    //
	    if(action.equals("zoom") && user.hasRole("Edit")){
		out.println("<form name=\"myForm\" method=\"post\">");
		out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
		out.println("<input type=\"hidden\" name=\"rental_id\" value=\""+rental_id+"\" />");
	    }
	    //
	    out.println("<table width=\"80%\" border><tr><td>");
	    out.println("<table width=\"100%\">");
	    out.println("<tr><td><b>ID:</b></td><td> "+id);
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Case ID: </b></td><td>"+legal.getCase_id());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Rental ID: </b></td><td>"+legal.getRental_id());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Legal Start Date: </b></td><td>");
	    out.println(legal.getStartDate());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Violation Type: </b></td><td>");
	    if(!cCase.getCase_type().equals("") && caseTypes != null){
		for(CaseType type: caseTypes){
		    if(cCase.getCase_type().equals(type.getId()))
			out.println(type);
		}
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Violation Address: </b></td><td>"+address);
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Defendants: </b></td><td>");
	    defendants = cCase.getDefendants();
	    if(defendants != null && defendants.size() > 0){
		out.println("<table>");
		for(Defendant def: defendants){
		    out.println("<tr><td colspan=\"2\">"+def.getFullName()+
				"</td></tr>");
		}
		out.println("</table></td></tr>");
	    }
	    else{
		out.println("No defendants added yet</td></tr>");
	    }
	    out.println("<tr><td><b>Started by: </b></td><td>"+legal.getStartByName());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Current Attention: </b></td>");
	    out.println("<td>"+legal.getAttention()+"</td></tr>");
	    out.println("<tr><td><b>Status: </b></td><td>");
	    out.println(legal.getStatus());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Start Legal Reason: <b></td><td>");
	    out.println(legal.getReason());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Last Pull Date: <b></td><td>");
	    out.println(legal.getPull_date());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Last Pull Reason: <b></td><td>");
	    out.println(legal.getPull_reason());
	    out.println("</td></tr>");						
	    out.println("</table></td></tr>");
	    if(action.equals("zoom") && user.canEdit()){
		out.println("<tr><td align=right>");
		out.println("<input type=\"submit\" name=\"action\" value=\"Edit\" />");
		out.println("</td></tr>");
	    }
	    out.println("</table>");
	    out.println("</form>");			
	}
	else if(action.equals("") ||
		action.equals("New") ||
		action.equals("Delete")){
	    //
	    // new record
	    //
	    out.println("<form name=\"myForm\" method=\"post\" "+
			"onSubmit=\"return validateForm()\">");
	    out.println("<table width=\"80%\" border><tr><td>");
	    out.println("<table width=\"100%\">");			
	    out.println("<tr><td>Violation Type: ");
	    out.println("<select name=\"case_type\">");
	    out.println("<option value=\"\">\n");
	    if(caseTypes != null){
		for(CaseType type: caseTypes){
		    out.println("<option value=\""+
				type.getId()+"\">"+type+"</option>");
		}
	    }
	    out.println("</select></td></tr>");
	    if(addresses != null && addresses.size() > 0){
		//
		// if only one address, no need to give choices
		//
		if(addresses.size() == 1){
		    for(Address addr: addresses){
			out.println("<tr><td>Violation Address: ");	
			out.println(addr.getAddress());
			out.println("</td></tr>");
			out.println("<input type=\"hidden\" name=\"addressId\" value=\""+addr.getId()+"\" />");
		    }
		}
		else{
		    //
		    // for multiple addresses, the user need to pick one
		    //
		    out.println("<tr><td>Please pick one or more Address from the following: ");
		    out.println("</td></tr>");
		    for(Address addr: addresses){
			out.println("<tr><td>");
			out.println("<input type=\"checkbox\" name=\"addressId\" ");
			out.println("value=\""+addr.getId()+"\" />"+
				    addr.getAddress());
			out.println("</td></tr>");
		    }
		}
	    }
	    out.println("<tr><td>Date:"+legal.getStartDate());
	    out.println("</td></tr>");
	    out.println("<tr><td>Started by: "+legal.getStartByName());
	    out.println("</td></tr>");
	    out.println("<tr><td>Attention: "+legal.getAttention());
	    out.println("</td></tr>");
	    out.println("<tr><td>Last Pull Date: "+legal.getPull_date());
	    out.println("</td></tr>");
	    out.println("<tr><td>Last Pull reason: "+legal.getPull_reason());
	    out.println("</td></tr>");						
	    out.println("<tr><td>Reasons:<font color=green> up to 500 "+
			"characters</font>");
	    out.println("</td></tr>");
	    out.print("<tr><td><textarea name=\"reason\" rows=\"8\" cols=\"80\" wrap>");
	    out.print(legal.getReason());
	    out.println("</textarea></td></tr>");

	    out.println("<input type=hidden name=rental_id value="+rental_id+">");
	    if(user.canEdit()){
		out.println("<tr><td align=right>"+
			    "<input type=submit name=action value=Save>"+
			    "&nbsp;&nbsp;</td></tr>");
	    }
	    out.println("</table></td></tr>");
	    out.println("</table>");
	}
	else {
	    // Edit mode
	    out.println("<form name=myForm method=post "+
			"onSubmit=\"return validateForm()\">");
	    out.println("<input type=\"hidden\" name=\"rental_id\" value=\""+legal.getRental_id()+"\" />");
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	    out.println("<table width=\"80%\" border><tr><td>");			
	    out.println("<table width=\"100%\">");
	    out.println("<tr><td><b>Record ID: </b></td><td>"+legal.getId());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Date: </b></td><td>"+legal.getStartDate());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Violation Type: </b></td><td>");
	    if(!cCase.getCase_type().equals("") && caseTypes != null){
		for(CaseType type: caseTypes){
		    if(cCase.getCase_type().equals(type.getId()))
			out.println(type);
		}
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Violation Address: </b> </td><td>"+address);
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Defendants: </b></td><td>");
	    defendants = cCase.getDefendants();
	    if(defendants != null && defendants.size() > 0){
		out.println("<table>");
		for(Defendant def: defendants){
		    out.println("<tr><td>"+def.getFullName()+
				"</td></tr>");
		}
		out.println("</table></td></tr>");
	    }
	    else{
		out.println("No defendants added yet</td></tr>");
	    }
	    out.println("<tr><td><b>Started by: </b></td><td>"+legal.getStartByName());
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Current Attention: </b></td>");
	    out.println("<td>"+legal.getAttention()+"</td></tr>");
	    out.println("<tr><td><b>Status: </b></td>");
	    out.println("<td>"+legal.getStatus()+"</td></tr>");
	    out.println("<tr><td colspan=\"2\"><b>Reasons:</b><font color=green> up to 500 "+
			"characters</font>");
	    out.println("</td></tr>");
	    out.println("<tr><td colspan=\"2\"><textarea name=\"reason\" rows=\"8\" "+
			"cols=\"80\" wrap>");
	    out.println(legal.getReason());
	    out.println("</textarea></td></tr>");
	    //
	    out.println("<tr><td>Last Pull Date: "+legal.getPull_date());
	    out.println("</td></tr>");
	    out.println("<tr><td>Last Pull reason: "+legal.getPull_reason());
	    out.println("</td></tr>");								
	    //
	    out.println("</tr></table></td></tr>");
	    out.println("<tr><td>");
	    out.println("<table width=100%><tr>");
	    if(user.canEdit()){
		out.println("<td valign=\"top\" align=\"right\">");
		out.println("<input type=\"submit\" name=\"action\" "+
			    "value=\"Update\">&nbsp;&nbsp;</td>");
	    }
	    out.println("<td valign=\"top\" align=\"right\">");
	    out.println("<input type=\"submit\" name=\"action\" "+
			"value=\"New\">&nbsp;&nbsp;</td>");
	    //
	    out.println("<td valign=\"top\" align=\"right\">");
	    out.println("<input type=\"button\" onclick=\"window.open('"+url+
			"StartLegal?action=printable&id="+id+"&rental_id="+rental_id+
			"');\" name=action value=\"Printable\" /></td>");
	    out.println("</form>");
	    if(user.isAdmin()){
		out.println("<form name=\"delForm\" method=\"post\" "+
			    "onSubmit=\"return validateDelete()\">");
		out.println("</td><td valign=\"top\" align=\"right\">");
		out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
		out.println("<input type=\"hidden\" name=\"rental_id\" value=\""+
			    rental_id+"\" />");
		out.println("<input type=\"submit\" name=\"action\" value=\"Delete\" />");
		out.println("</td>");
		out.println("</form>");
	    }
	    //
	    out.println("</table></td></tr>");
	    out.println("</table>");			
	}
	out.println("<table><tr><td>");
	out.println("<a href='"+url+"Rental?action=zoom&id="+
		    rental_id+"'>Back to Rental ID "+rental_id+"</a>");
	out.println("</td></tr>");
	out.println("</table>");
	//	
	if(legals.size() > 0){
	    String [] titles = {"ID","Date","Started By","Reason","Status"};
	    out.println("<table border width=\"80%\">");
	    out.println("<caption>Active Legal Cases Related to this Rental</caption>");
	    out.println("<tr>");
	    for(int i=0; i<titles.length; i++){
		out.println("<td>"+titles[i]+"</td>");
	    }
	    out.println("</tr>");
						
	    for(Legal llg:legals){
		out.println("<tr>");						
		out.println("<td><a href=\""+url+"StartLegal?id="+llg.getId()+
			    "&rental_id="+llg.getRental_id()+
			    "&action=zoom\">"+llg.getId()+"</a></td>");
		out.println("<td>"+llg.getStartDate()+"</td>");
		out.println("<td>"+llg.getStartByName()+"</td>");
		out.println("<td>"+llg.getReason()+"</td>");
		out.println("<td>"+llg.getStatus()+"</td>");
		out.println("</tr>");
	    }
	    out.println("</table>");
	}
	LegalItEmailLogList legalLogs = new LegalItEmailLogList(debug, rental_id);
	message = legalLogs.find();
	if(message.equals("") && legalLogs.size() > 0){
	    out.println("<table border>");
	    out.println("<caption>LegalIt Email Logs</caption>");
	    out.println("<tr><th>Date</th>"+
			"<th>Status</th>"+
			"<th>From</th>"+
			"<th>To</th>"+
			"<th>CC</th>"+
			"<th>Subject</th>"+
			"<th>Message</th></tr>");
	    for(LegalItEmailLog one:legalLogs){
		out.println("<tr><td>"+one.getDate()+"</td>");
		out.println("<td>"+one.getStatus()+"</td>");
		out.println("<td>"+one.getFrom()+"</td>");
		out.println("<td>"+one.getTo()+"</td>");
		out.println("<td>"+one.getCc()+"</td>");
		out.println("<td>"+one.getSubject()+"</td>");
		out.println("<td>"+one.getMsg()+"</td>");
		out.println("<td>"+one.getError()+"</td>");
		out.println("</tr>");
	    }
	    out.println("</table>");
	}
	out.print("</body></html>");
	out.close();
    }
    //
    void resetTypes(){
		
	String caseTypeUrl = "";
	try{
	    HandleTypes ha = new HandleTypes(caseTypeServiceUrl, debug);
	    List<CaseType> types = ha.getTypes();
	    if(types != null){
		caseTypes = types;
	    }
	}catch(Exception ex){
	    logger.error(" "+ex);
	}
    }

}





















































