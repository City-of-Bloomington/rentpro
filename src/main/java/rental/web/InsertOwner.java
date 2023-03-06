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

@WebServlet(urlPatterns = {"/InsertOwner"})
public class InsertOwner extends TopServlet{

    final static long serialVersionUID = 400L;
    String bgcolor = Rental.bgcolor;
    static Logger logger = LogManager.getLogger(InsertOwner.class);
    /**
     * Generates the search owner form and then list the matching records.
     *
     * The user can check a selection of these and presses the add as owner
     * or add as agent.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * @link AddUser#doGet
     * @see #doGet
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	String message = "";
	boolean success = true, showAll = false;
	String [] titles = {"Select","ID","Name","Address",
	    "City, State Zip",
	    "Phones"};
	String action="";
	String name_num="",oName="",address="",city="",state="",zip="",
	    phone="", notes="",name_opt="starts with",
	    addr_opt="contains";
	String id="", access="";
	String [] owner_ids = null, rent_ids = null;
	List<Rent> rents = null;
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
	    else if (name.equals("phone")){	  
		phone =value;
	    }
	    else if (name.equals("notes")){
		notes =value.toUpperCase();
	    }
	    else if (name.equals("id")) {
		id = value;
	    }
	    else if (name.equals("owner_ids")) {
		owner_ids = vals; // array
	    }
	    else if (name.equals("rent_ids")) {
		rent_ids = vals; // array
	    }						
	    else if (name.equals("action")){ 
		action = value;  
	    }
	    else if (name.equals("name_opt")) {
		name_opt=value;
	    }
	    else if (name.equals("addr_opt")) {
		addr_opt=value;
	    }
	}
	HttpSession session = null;
	User user = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=InsertOwner&id="+id;
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=InsertOwner&id="+id;
	    res.sendRedirect(str);
	    return; 
	}
	if(!id.equals("")){
	    Rent rent = new Rent(id, debug);
	    List<Owner> owners = rent.getOwners();
	    if(owners != null && owners.size() > 0){
		Owner one = owners.get(0);
		if(one != null){
		    List<Rent> ones = one.getRents();
		    // we ignore if we get one property only
		    if(ones != null && ones.size() > 1){
			rents = ones;
		    }
		}
	    }
	}
	if(action.startsWith("Add") && owner_ids != null){
	    if(debug){
		logger.debug("Adding owners to permit");
		logger.debug("Vals = "+owner_ids.length);
		logger.debug("Vals = "+owner_ids[0]);
	    }
	    Rent rent = new Rent(id, debug);
	    if(user.canEdit()){
		String str = rent.addOwners(owner_ids);
		if(!str.equals("")){
		    success = false;
		    message += str;
		}
	    }
	}
	else if(action.startsWith("Insert") && // adding agent
		owner_ids != null){
	    if(owner_ids != null){ 
		if(user.canEdit()){
		    if(rent_ids != null){
			for(String rr:rent_ids){
			    Rent rent = new Rent(rr, debug);
			    String str = rent.updateAgent(owner_ids);
			    if(!str.equals("")){
				message += str;
				success = false;
			    }
			}
		    }
		    else{ // only one property case
			Rent rent = new Rent(id, debug);
			String str = rent.updateAgent(owner_ids);
			if(!str.equals("")){
			    message += str;
			    success = false;
			}
		    }
		}
	    }
	}

	out.println("<html><head><title>Water</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateForm(){		                 ");
	out.println("     return true;					             ");
	out.println("	}	         				                 ");
	out.println("  function validateDelete(){	                 ");
	out.println("   var x = false;                               ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                   ");
	out.println("	}						                      ");
	out.println(" </script>		                                  ");
	out.println("</head><body onLoad=\"document.myForm.oName.focus();\">");
	out.println("<center><h2>Insert Owners/Agent</h2>");
	Helper.writeTopMenu(out, url);	
	out.println("<h3>Look for Owners/Agents</h3>");
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");
	}
	else{
	    if(!message.equals(""))
		out.println("<h3><font color='red'>"+message+"</font></h3>");
	}	
	if(action.startsWith("Add") && owner_ids != null && 
	   owner_ids.length>0 ){
	    out.println("<font color=green>");
	    out.println("<h3>The selected owners added successfully</h3>");
	    out.println("</font>");
	}
	else if(action.startsWith("Insert") && owner_ids != null && 
		owner_ids.length>0 ){
	    out.println("<font color=green>");
	    out.println("<h3>The selected Agent added successfully</h3>");
	    out.println("</font>");
	}
	else if(action.startsWith("Add") || action.startsWith("Insert")){
	    out.println("<font color=red>");
	    out.println("<p>No owners have been selected yet. Use the "+
			"browser Back button to go back and select the owners"+
			" </p>");
	    out.println("</font>");
	}
	out.println("<table align=\"center\" width=\"80%\" border>");
	out.println("<tr><td bgcolor=\""+bgcolor+"\">");
	//
	out.println("<form name=\"myForm\" method=\"post\" "+
		    "onSubmit=\"return validateForm()\">");
	//
	out.println("<input type=\"hidden\" name=\"id\" value=\""+
		    id+"\">");
	//
	// 1st block
	out.println("<tr><td><table>");
	out.println("<tr><td>ID:");
	out.println("<input name=\"name_num\" value=\""+name_num+"\""+
		    " size=\"8\" maxlength=\"8\" />");
	out.println("Name");
	out.println("<input name=\"oName\" value=\""+oName+"\""+
		    " size=\"20\" maxlength=\"20\" />(You can use partial name)</td></tr>");
	out.println("<tr><td>Address:");
	out.println("<input name=\"address\" value=\""+
		    address+"\""+
		    " size=\"50\" maxlength=\"50\" />(You can use partial address)</td></tr>");
	//
	// city,state,zip,phones
	out.println("<tr><td><table><tr><td>City</td><td>State</td>");
	out.println("<td>Zip</td><td>Phone"+
		    "</td></tr>");
	out.println("<tr><td>");
	out.println("<input name=\"city\" value=\""+city+"\""+
		    " size=\"20\" maxlength=\"20\" /></td><td>");
	out.println("<input name=\"state\" value=\""+
		    state+"\""+
		    " size=\"2\" maxlength=\"2\" /></td><td>");
	out.println("<input name=\"zip\" value=\""+
		    zip+"\""+
		    " size=\"10\" maxlength=\"10\" /></td><td>");
	out.println("<input name=\"phone\" value=\""+
		    phone+"\""+
		    " size=\"12\" maxlength=\"12\" /></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("</table></td></tr>");
	//
	out.println("<tr><td align=\"right\">");
	out.println("<input type=\"submit\" "+
		    "name=\"action\" value=\"Browse\" />");
	out.println("</td></tr></table>");
	out.println("<li><a href=\""+url+"Rental?id="+id+
		    "&action=zoom\">Back to Related Permit</a>");
	if(action.equals("Browse")){
	    OwnerList ol = new OwnerList(debug);
	    
	    if(!name_num.equals("")){
		ol.setName_num(name_num);
	    }
	    if(!city.equals("")){
		ol.setCity(city);
	    }
	    if(!zip.equals("")){
		ol.setZip(zip);
	    }
	    if(!state.equals("")){
		ol.setState(state);
	    }
	    if(!phone.equals("")){
		ol.setPhone(phone);
	    }
	    if(!address.equals("")){
		ol.setAddress(address);
		if(!addr_opt.equals(""))
		    ol.setAddrOpt(addr_opt);
	    }
	    if(!oName.equals("")){
		ol.setFullName(oName);
		if(!name_opt.equals(""))
		    ol.setNameOpt(name_opt);
	    } 
	    String back = ol.lookFor();
	    if(!back.equals("")){
		message = back;
		success = false;
		out.println("<p>"+message+"</p>");
	    }
	    else{
		OwnerList owners = ol;
		if(owners == null || owners.size() == 0){
		    out.println("<h4>No match found </h4>");
		}
		else{
		    if(rents != null && rents.size() > 1){
			out.println("<p>Note: since this owner has multiple properties you may choose one or more properties below to add/change the agent</p>");
			out.println("<table border=\"1\">");
			out.println("<caption>Properties</caption>");
			out.println("<tr><td>Select</td><td>ID</td><td>Address</td><td>Agent</td><td>Registered </td><td>Expire</td></tr>");
			for(Rent one:rents){
			    String str = one.getId();
			    out.println("<tr><td><input type=\"checkbox\" name=\"rent_ids\" value=\""+str+"\" /></td><td>"+str+"</td>");
			    List<Address> addrs = one.getAddresses();
			    str = "";
			    if(addrs != null && addrs.size() > 0){
				for(Address addr: addrs){
				    if(str.equals("")) str += "<br />";
				    str += addr.getAddress();
				}
			    }
			    if(str.equals("")) str = "&nbsp;";
			    out.println("<td>"+str+"</td>");
			    Owner agency = one.getAgent();
			    str = "";
			    if(agency != null){
				str += agency.getFullName();
			    }
			    if(str.equals("")) str += "No Agent";
			    out.println("<td>"+str+"</td>");												
			    str = one.getRegistered_date();
			    if(str.equals("")) str = "&nbsp;";
			    out.println("<td>"+str+"</td>");
			    //	
			    str = one.getPermit_expires();
			    if(str.equals("")) str = "&nbsp;";
			    out.println("<td>"+str+"</td></tr>");
			}
			out.println("</table>");
		    }
		    out.println("<h4>Total Matching "+ owners.size() +" </h4>");
		    out.println("<font color=\"green\">");
		    out.println("Select the owners from the list and press "+
				"Add Owners button<br />");
		    out.println("Or, select one Agent only and press "+
				"Insert Agent button<br />");
		    out.println("</font>");
		    out.println("<table border>");
		    out.println("<tr>");
		    for (int c = 0; c < titles.length-1; c++){ 
			out.println("<th>"+titles[c]+"</th>");
		    }	   
		    out.println("</tr>");
		    for(Owner owner: owners){
			String str = owner.getName_num();
			out.println("<tr><td>");
			out.println("<input type=\"checkbox\" name=\"owner_ids\" "+
				    "value=\""+str+"\" /></td>");
			out.println("<td>"+str+"</td>");
			out.println("<td>"+owner.getFullName()+"</td>");
			out.println("<td>"+owner.getAddress()+"</td>");
			out.println("<td>"+owner.getCityStateZip()+"</td>");
			out.println("<td>"+owner.getPhones()+"&nbsp;</td>");
			out.println("</tr>");	
		    }
		    out.println("</table><br />");
		    if(user.canEdit()){
			out.println("<table width=\"80%\"><tr><td>");
			out.println("<input type=\"submit\" name=\"action\" "+
				    "value=\"Add Owners to Permit\" />");
			out.println("</td><td align=\"right\">");
			out.println("<input type=\"submit\" name=\"action\" "+
				    "value=\"Insert As Agent\" />");
			out.println("</td></tr></table><br /><br />");
		    }
		}
	    }
	}
	out.println("</form>");
	out.print("</body></html>");
	out.close();

    }

}






















































