package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.naming.*;
import javax.naming.directory.*;
import javax.sql.*;
import rental.web.Rental;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/AddressEdit"})
public class AddressEdit extends TopServlet{

    final static long serialVersionUID = 50L;
    final static String bgcolor = "silver";// #bfbfbf gray
    static Logger logger = LogManager.getLogger(AddressEdit.class);

    //
    /**
     * Generates the edit address form.
     * operations.
     * @param req
     * @param res
     */
    
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req,res);
    }
    /**
     * Generates the edit address form.
     * operations.
     * @param req
     * @param res
     * @link #doGetost
     */

    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String username = "";
	String name, value;
	String action="",id="", rid="", dept_no="", invalid_addr="", message="";
	//
	HttpSession session = null;
	session = req.getSession(false);
	boolean success = true;
	Address addr = new Address(debug);
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    //
	    if (name.equals("id")) {
		id = value;
		addr.setId(value);
	    }
	    else if (name.equals("dept_no")) {
		dept_no = value;
	    }
	    else if (name.equals("rid")) {
		rid = value;
		addr.setRid(value);
	    }
	    else if (name.equals("registr_id")) {
		rid = value;
		addr.setRid(value);
	    }			
	    else if (name.equals("action")) {
		action = value;
		if(action.equals("New")) action = "";
	    }
	    else if (name.equals("street_num")) {
		addr.setStreet_num(value);
	    }
	    else if (name.equals("street_dir")) {
		addr.setStreet_dir(value);				
	    }
	    else if (name.equals("street_name")) {
		addr.setStreet_name(value);
	    }
	    else if (name.equals("street_type")) {
		addr.setStreet_type(value);
	    }
	    else if (name.equals("post_dir")) {
		addr.setPost_dir(value);
	    }
	    else if (name.equals("sud_num")) {
		addr.setSud_num(value);
	    }
	    else if (name.equals("sud_type")) {
		addr.setSud_type(value);				
	    }
	    else if (name.equals("invalid_addr")) {
		addr.setInvalid_addr(value);
	    }
	}

	if(action.equals("zoom")){
	    String str = "";
	    String back = addr.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
		logger.error(message);
	    }
	    else{
		rid = addr.getRid();
	    }
	}
	else if(action.equals("Save")){
	    //
	    if(!addr.getStreet_name().equals("")){
		//
		// Check the address valid or not
		//
		if(!addr.hasMasterAddressInfo(checkAddrUrl)){
		    addr.setInvalid_addr("Y");
		}
		else{
		    addr.setInvalid_addr("");
		}
		String back = addr.doSave();
		if(!back.equals("")){
		    message += " could not save address "+back;
		    success = false;
		    logger.error(message);
		}
		else{
		    id = addr.getId();
		}
	    }
	}
	else if(action.equals("Update")){
	    if(!addr.getStreet_name().equals("")){
		//
		// Check the address valid or not
		//
		if(addr.hasMasterAddressInfo(checkAddrUrl)){
		    addr.setInvalid_addr("");
		}
		else{
		    addr.setInvalid_addr("Y");
		}
		String back = addr.doUpdate();
		if(!back.equals("")){
		    message += " could not save address "+back;
		    success = false;
		    logger.error(message);
		}
		else{
		    message = "Updated Successfully";
		}
	    }
	}
	else if(action.equals("Delete")){
	    String back = addr.doDelete();
	    if(!back.equals("")){
		message += " could not delete address "+back;
		success = false;
		logger.error(message);
	    }
	}
	else{ // new and ""
	    id="";
	}
	//
	out.println("<html><head><title>Rental</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateForm(){		                ");
	out.println("  if ((document.myForm.street_name.value.length == 0)){ "); 
	out.println("     alert(\"Street name is needed\" );   ");
	out.println("    return false;					    ");
	out.println("	}						    ");
	out.println("    return true ;					    ");
	out.println(" }				    ");
	out.println("  function firstFocus(){		                ");
	out.println(" document.myForm.street_num.focus();     ");
	out.println(" }				    ");
	out.println("  function validateDelete(){	                 ");
	out.println("   var x = false;                                   ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                      ");
	out.println("	}					         ");
	out.println("</script>			    ");
	//
    	if(!action.equals("")){
	    out.println(" </head><body>");
	    Helper.writeTopMenu(out, url);
	    out.println("<center><h2>Address</h2>");
	    if(!message.equals("")){
		if(success)
		    out.println("<h3><font color=green> "+message+
				"</font></h3>");
		else
		    out.println("<h3><font color=red>"+message+
				"</font></h3>");
	    }
	}
	else { // zoom, update, create
	    out.println(" </head><body onload=\"firstFocus()\" >");
	    Helper.writeTopMenu(out, url);
	    out.println("<center><h2>Address</h2>");
	    if(!success)
		out.println("<h3><font color=red>"+message+
			    "</font></h3>");
	    //
	}
	//
	out.println("<form name=\"myForm\" method=\"post\" >");
	out.println("<input type=\"hidden\" name=\"rid\" value=\""+rid+"\" />");
	if(!id.equals(""))
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	out.println("<table width=80% border><tr><td>");
	out.println("<table width=100%>");
	out.println("<tr><td>Rental ID:"+rid);
	out.println("&nbsp;&nbsp;</td><td>");
		
	invalid_addr = addr.isInvalid()?"checked=\"checked\"":"";
	out.println("<input type=\"checkbox\" name=\"invalid_addr\" "+invalid_addr+
		    " value=\"Y\" >Invalid Address");
	out.println("</td></tr>");
	//
	// st num
	out.println("<tr><td>Street Num:</td><td>");
	out.println("<input name=street_num size=10 maxlength=10 value=\""+
		    Helper.replaceSpecialChars(addr.getStreet_num())+"\" >");
	out.println("</td></tr>");
	out.println("<tr><td>Street Dir:</td><td>");
	out.println("<select name=\"street_dir\">");
	out.println("<option selected>"+addr.getStreet_dir());
	out.println(Rental.allStreetDir+"</td></tr>");
	out.println("<tr><td>Street Name:</td><td>");
	out.println("<input type=\"text\" name=\"street_name\" maxlength=\"30\" value=\""+Helper.replaceSpecialChars(addr.getStreet_name())+"\" size=\"20\" />");
	out.println("</td></tr>");
	//
	// st type
	out.println("<tr><td>Street Type:</td><td>");
	out.println("<select name=\"street_type\">");
	if(addr.getStreet_type().equals("")){	    
	    out.println("<option selected value=\"\">\n");
	}
	else{
	    for(int i=0; i<Rental.streetKeys.length; i++){
		if(Rental.streetKeys[i].equals(addr.getStreet_type())){
		    out.println("<option selected value=\""+addr.getStreet_type()+
				"\">"+Rental.streetInfo[i]);
		    break;
		}
	    }
	}
	out.println(Rental.allStreetType+"</td></tr>");	    
	//
	// post dir
	out.println("<tr><td>Post Dir:</td><td>");
	out.println("<select name=\"post_dir\" >");
	out.println("<option selected>"+addr.getPost_dir());
	out.println(Rental.allStreetDir+"</td></tr>");
	//
	// sud type
	out.println("<tr><td>Sud Type:</td><td>");
	out.println("<select name=\"sud_type\">");
	for(int i=0; i<Rental.sudKeys.length; i++){
	    if(addr.getSud_type().equals(Rental.sudKeys[i]))
		out.println("<option selected value=\""+addr.getSud_type()+"\">"+
			    Rental.sudInfo[i]);
	}
	//
	out.println(Rental.allSudTypes+"</td></tr>");
	//
	// sud num
	out.println("<tr><td>Sud Num:</td><td>");
	out.println("<input type=\"text\" name=\"sud_num\" maxlength=\"4\" "+
		    "size=\"4\" value=\""+addr.getSud_num()+"\" />");
	out.println("</td></tr></table></td></tr>");

	//
	if(id.equals("")){
	    out.println("<tr><td align=\"right\">  "+
			"<input type=\"submit\" name=\"action\" "+
			"value=\"Save\" />&nbsp;&nbsp;&nbsp;"+
			"</td></tr>"); 
	}
	else {  // save, update, zoom
	    out.println("<tr><td><table width=100%><tr><td valign=top "+
			"align=right>If you've made any changes click on");
	    out.println("<input type=submit name=action "+
			"value=Update>&nbsp;&nbsp;");
	    out.println("<input type=submit name=action "+
			"value=New></td>");
	    out.println("</form>");
	    out.println("<form name=delForm method=post "+
			"onSubmit=\"return validateDelete()\">");
	    out.println("</td><td valign=top align=right>");
	    out.println("<input type=hidden name=id value="+id+">");
	    out.println("<input type=hidden name=rid value="+rid+">");
	    out.println("<input type=submit name=action "+
			"value=Delete>");
	    out.println("</td></tr></table></td></tr>");
	}
	out.println("</table></center>");
	out.println("</form>");
	//
	String [] titles = {"Address","Invalid"};
	if(!rid.equals("")){ 
	    AddressList al = new AddressList(debug, rid);
	    if(!id.equals("")){
		al.excludeId(id);
	    }
	    List<Address> addresses = null;			
	    String back = al.lookFor();
	    if(back.equals("")){
		addresses = al.getAddresses();
	    }
	    if(addresses != null && addresses.size() > 0){
		out.println("<center><table border>");
		out.println("<tr>");
		for(int i=0; i<titles.length; i++){
		    out.println("<td>"+titles[i]+"</td>");
		}
		out.println("</tr>");
		for(Address addrr:addresses){
		    out.println("<tr>");
		    String str = addrr.getId();
		    String str2 = addrr.getAddress();
		    String str3 = addrr.getInvalid_addr();
		    if(!str2.equals(""))
			out.println("<td><a href="+url+
				    "AddressEdit?rid="+rid +
				    "&action=zoom&id="+str+
				    ">"+str2+
				    "</a></td>");
		    else
			out.println("<td>&nbsp;</td>");
		    if(!str3.equals(""))
			out.println("<td>"+str3+"</td>");
		    else
			out.println("<td>&nbsp;</td>");
		    out.println("</tr>");
		}
		out.println("</table></center>");
	    }
	}
	//
	out.println("<center><li><a href="+url+"Rental?id="+rid+
		    "&dept_no="+dept_no+
		    "&action=Edit>Back to Related Permit</a><br /><br />");
	out.print("</center>");
	out.flush();
	// 
	// give a list of similar addresses 
	//
	if(addr.isInvalid() && !addr.getStreet_name().equals("")){
	    AddressList al = new AddressList(debug);
	    al.setAddress(addr);
	    String back = al.findSimilarAddr(checkAddrUrl);
	    if(back.equals("")){
		List<Address> adrs = al.getAddresses();
				
		if(adrs != null && adrs.size() > 0){
		    out.println("<center><table border cellspacing=4 "+
				"cellpadding=2><tr><td>"+
				"Valid Similar Addresses</td></tr>");
					
		    for(Address ad: adrs){
			String str = ad.getStreetAddress();
			if(ad.hasSubunits()){
			    List<Subunit> subs = ad.getSubunits();
			    for(int j=0;j<subs.size();j++){
				Subunit sub = subs.get(j);
				out.println("<tr><td>"+str+" "+sub.getAddress()+"</td></tr>");
			    }
			}
			else{
			    out.println("<tr><td>"+ad.getStreetAddress()+"</td></tr>");
			}
		    }
		    out.println("</table></center>");					
		}
	    }
	}
	out.print("</body></html>");
	out.close();
    }

}






















































