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

@WebServlet(urlPatterns = {"/OwnerBrowse","/OwnerSearch"})
public class OwnerBrowse extends TopServlet{

    final static long serialVersionUID = 630L;
    String bgcolor= Rental.bgcolor;
    static Logger logger = LogManager.getLogger(OwnerBrowse.class);
    /**
     * Generates the search form.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     * @see Rental
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {

	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String message = "";
	boolean success = true, showAll=false;
	String action="", role="";
	String name_num="",oName="",address="",city="",state="",zip="",
	    phone_work="",phone_home="", email="", phone="",
	    notes="",name_opt="starts with",
	    addr_opt="contains";
	String id="", access="";
	String [] ownerList = null; 
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("name_num")){
		name_num = value;
	    }
	    else if (name.equals("oName")){
		oName = value.toUpperCase();
	    }
	    else if (name.equals("address")){
		address=value.toUpperCase();
	    }
	    else if (name.equals("city")){
		city =value.toUpperCase();
	    }
	    else if (name.equals("state")){
		state =value.toUpperCase();
	    }
	    else if (name.equals("zip")){
		zip =value;
	    }
	    else if (name.equals("phone_home")){	  
		phone_home =value;
	    }
	    else if (name.equals("phone_work")){
		phone_work =value;
	    }
	    else if (name.equals("notes")){
		notes =value.toUpperCase();
	    }
	    else if (name.equals("email")){
		email = value;
	    }
	    else if (name.equals("id")) {
		id=value;
	    }
	    else if (name.equals("marked")) {
		ownerList = vals; // array
	    }
	    else if (name.equals("action")){ 
		// add, zoom, edit, delete, startNew
		action = value;  
	    }
	    else if (name.equals("name_opt")) {
		name_opt=value;
	    }
	    else if (name.equals("addr_opt")) {
		addr_opt=value;
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	//
	out.println("<html><head><title>Look for Owners</title>");
	Helper.writeWebCss(out, url);
	out.println("<script language=Javascript>");
	out.println("  function validateForm(){		                 ");
	out.println("     return true;					 ");
	out.println("	}	         				 ");
	out.println("  function validateDelete(){	                 ");
	out.println("   var x = false;                                   ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                       ");
	out.println("	}						  ");
	out.println(" </script>		                                  ");
	out.println("</head><body onLoad=\"document.myForm.oName.focus();\">");
	//
	Helper.writeTopMenu(out, url);	
	out.println("<center><h2>Search Owners/Agent</h2>");
	//
	out.println("<table align=center width=80% border>");
	out.println("<tr><td bgcolor="+bgcolor+">");
	//the real table
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	//
	// 1st block
	out.println("<tr><td><table>");
	out.println("<tr><td>ID:");
	out.println("<input name=name_num value=\""+name_num+"\""+
		    " size=8 maxlength=8>");
	out.println("Name");
	out.println("<input name=oName value=\""+oName+"\""+
		    " size=20 maxlength=20>(You can use partial name)</td></tr>");
	out.println("<tr><td>Address:");
	out.println("<input name=address value=\""+
		    address+"\""+
		    " size=50 maxlength=50>(You can use partial address)</td></tr>");
	//
	// city,state,zip,phones
	out.println("<tr><td><table><tr><td>City</td><td>State</td>");
	out.println("<td>Zip</td><td>Phone</td>"+
		    "</tr>");
	out.println("<tr><td>");
	out.println("<input name=city value=\""+city+"\""+
		    " size=20 maxlength=20></td><td>");
	out.println("<input name=state value=\""+
		    state+"\""+
		    " size=2 maxlength=2></td><td>");
	out.println("<input name=zip value=\""+
		    zip+"\""+
		    " size=10 maxlength=10></td><td>");
	out.println("<input name=phone value=\""+
		    phone+"\""+
		    " size=12 maxlength=12></td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td>"+
		    "<input type='radio' name='w_email' value='y'>");
	out.println("Have email ");
	out.println("<input type='radio' name='w_email' value='n'>");
	out.println("Do not have email. </td></tr>");
	//
	out.println("<tr><td>Email: contain ");
	out.println("<input name=email value=\""+
		    email+"\""+
		    " size=30 maxlength=30></td><td>");	
	out.println("<tr><td>Notes: contain ");
	out.println("<input name=notes value=\""+
		    notes+"\""+
		    " size=30 maxlength=30></td><td>");
	out.println("<tr><td>Owner/Agent?");
	out.println("<select name=owner_or_agent>");
	out.println("<option selected value=\"\">All");
	out.println("<option value=owner>Owners");
	out.println("<option value=agent>Agents");
	out.println("</select></td></tr>");
	out.println("<tr><td>Confirmed Status?");
	out.println("<select name=confirm_status>");
	out.println("<option selected value=\"\">All");
	out.println("<option value=\"Confirmed\">Confirmed");
	out.println("<option value=\"Unconfirmed\">Unconfirmed");
	out.println("</select></td></tr>");

	out.println("</table></td></tr>");

	out.println("<td align=right>");
	out.println("<input type=submit "+
		    "name=action value=Browse>");
	out.println("</td></tr></table>");
	out.println("</form>");
	//
	out.print("</body></html>");
	out.close();

    }
    /**
     * Presents the mathing of the search in a table form.
     *
     * @param req the request
     * @param res the responces
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value, message = "";
	boolean success = true, showAll=false;
	String [] titles = {"ID","Name","Address",
	    "City State Zip",
	    "Phones","Email"};
	boolean [] show = {true,true,true,true,
	    true,true};
	String action="";
	String name_num="",oName="",address="",city="",state="",zip="",
	    phone_work="",phone="",notes="",name_opt="starts with",
	    addr_opt="contains",owner_or_agent="", w_email="", email="";
	String id="", access="";
	String [] ownerList = null; 
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	OwnerList ol = new OwnerList(debug);
		
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("name_num")){
		name_num = value;
	    }
	    else if (name.equals("oName")){
		ol.setFullName(value);
	    }
	    else if (name.equals("address")){
		ol.setAddress(value);
	    }
	    else if (name.equals("confirm_status")){
		ol.setConfirmStatus(value);
	    }						
	    else if (name.equals("city")){
		ol.setCity(value);
	    }
	    else if (name.equals("state")){
		ol.setState(value);
	    }
	    else if (name.equals("zip")){
		ol.setZip(value);
	    }
	    else if (name.equals("email")){
		ol.setEmail(value);
	    }
	    else if (name.equals("w_email")){
		if(value.equals("y"))
		    ol.setWithEmailOnly();
		else
		    ol.setNoEmail();
		// w_email =value;
	    }
	    else if (name.equals("phone")){	  
		ol.setPhone(value);
	    }
	    else if (name.equals("notes")){
		ol.setNotes(value);
	    }
	    else if (name.equals("id")) {
		ol.setId(value);
	    }
	    else if (name.equals("owner_or_agent")) {
		if(value.equals("agent"))
		    ol.setAgentsOnly();
		else if(value.equals("owner"))
		    ol.setOwnersOnly();
		owner_or_agent = value;
	    }
	    else if (name.equals("marked")) {
		ownerList = vals; // array
	    }
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("name_opt")) {
		ol.setNameOpt(value);
	    }
	    else if (name.equals("addr_opt")) {
		ol.setAddrOpt(value);
	    }
	}
	//
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
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
	if(!name_num.equals("")){
	    String str = url+"OwnerServ?name_num="+name_num+"&action=zoom";
	    res.sendRedirect(str);
	    return; 
	}
	OwnerList owners = null;
	String back = ol.lookFor();
	if(back.equals("")){
	    owners = ol;
	    if(owners.size() == 1){
		name_num = owners.get(0).getId();
		String str = url+"OwnerServ?name_num="+name_num+"&action=zoom";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    message += back;
	    success = false;
	}
	out.println("<html><head><title>Owners</title>");
	Helper.writeWebCss(out, url);
	out.println("</head><body>");
	Helper.writeTopMenu(out, url);	
	if(!success && !message.equals("")){
	    out.println("<p><font color=red>"+message+"</font></p>");
	}
	if(owners == null || owners.size() == 0){
	    out.println("<h4>No Matching found </h4>");
	}
	else{
	    out.println("<h4>Total Matching Records "+ owners.size()+" </h4>");
	    out.println("<table border>");
	    out.println("<tr>");
	    for (int c = 0; c < titles.length; c++){ 
		if(show[c] || showAll)
		    out.println("<th>"+titles[c]+"</th>");
	    }	   
	    out.println("</tr>");
	    int cnt = 1;
	    for(Owner owner: owners){
		String str = owner.getId();
		String unconfirmed = "";
		if(owner.isUnconfirmed() && owner_or_agent.equals("Owner")){
		    unconfirmed	=" (Uncofirmed)";
		}
		out.println("<tr><td>");
		out.println("<a href=\""+url+"OwnerServ?"+
			    "&name_num="+str+
			    "&type="+owner_or_agent+
			    "&action=zoom\">"
			    +str+"</a></td>");
		out.println("<td>"+owner.getFullName()+unconfirmed+"</td>");
		out.println("<td>"+owner.getAddress()+"</td>");
		out.println("<td>"+owner.getCityStateZip()+"&nbsp;</td>");
		out.println("<td>"+owner.getPhones()+"&nbsp;</td>");
		out.println("<td>"+owner.getEmail()+"&nbsp;</td>");				
		out.println("</tr>");	
		cnt++;
		if(cnt %21 == 0){
		    out.println("<tr>");
		    for (int c = 0; c < titles.length; c++){ 
			if(show[c] || showAll)
			    out.println("<th>"+titles[c]+"</th>");
		    }	   
		    out.println("</tr>");
		}
	    }
	    out.println("</table><br>");
	}
	out.print("</body></html>");
	out.close();
    }

}






















































