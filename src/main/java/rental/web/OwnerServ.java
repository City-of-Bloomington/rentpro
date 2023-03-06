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

@WebServlet(urlPatterns = {"/OwnerServ"})
public class OwnerServ extends TopServlet{

    final static long serialVersionUID = 650L;
    static Logger logger = LogManager.getLogger(OwnerServ.class);
    /**
     * Generates the request form.
     * It also handles the view, add and update operations (except deletion).
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     * @see Rental
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
	boolean success = true;
	String action="", message="";
	String name_num="",oName="",address="",city="BLOOMINGTON",
	    state="IN",zip="", type="", tag="",
	    phone_work="",phone_home="", phone_cell="", phone_emergency="",
	    notes="", email="";
	String id="";
	int access = 0;
	Enumeration<String> values = req.getParameterNames();
	String [] delPhone = null;
	String [] vals;

	Owner owner = new Owner(debug);
	Phone phone = new Phone(debug);
	Phone phone2 = new Phone(debug);	
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("name_num")){
		owner.setId(value);
		name_num = value;
		phone.setOwnerId(value);
		phone2.setOwnerId(value);	
	    }
	    else if (name.equals("oName")){
		owner.setFullName(value);
	    }
	    else if (name.equals("address")){
		owner.setAddress(value);
	    }
	    else if (name.equals("city")){
		owner.setCity(value);
	    }
	    else if (name.equals("state")){
		owner.setState(value);
	    }
	    else if (name.equals("zip")){
		owner.setZip(value);
	    }
	    else if (name.equals("unconfirmed")){
		owner.setUnconfirmed(value);
	    }						
	    else if (name.equals("tag")){
		tag =value;
	    }
	    else if (name.equals("type")){ // agent or owner
		type =value;
	    }
	    else if (name.equals("delPhone")){
		delPhone = vals; // array
	    }
	    else if (name.equals("phoneType")){
		phone.setType(value);
	    }
	    else if (name.equals("phoneType2")){
		phone2.setType(value);
	    }
	    else if (name.equals("phone")){
		phone.setNumber(value);
	    }
	    else if (name.equals("phone2")){
		phone2.setNumber(value);
	    }
	    else if (name.equals("email")){
		owner.setEmail(value);
	    }
	    else if (name.equals("notes")){
		owner.setNotes(value);
	    }
	    else if (name.equals("id")) {
		id = value;
	    }
	    else if (name.equals("action")){ 
		// add, zoom, edit, delete, startNew
		action = value;  
	    }
	}

	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){ // go to rental instead of owner
		String str = url+"Login?source=Rental&action=zoom&id="+id;
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=Rental&action=zoom&id="+id;
	    res.sendRedirect(str);
	    return; 
	}
	//
	// we need this parameter to check for 
	// deleting an owner from the list of owners completely
	// on the condition that the owner has no links to any
	// property
	//
	int ncnt = 0;
	if(action.equals("Save")){
	    if(user.canEdit()){
		String back = owner.doSave();
		if(back.equals("")){
		    message += "Saved successfully";
		    name_num = owner.getId();
		}
		else{
		    message += back;
		}
	    }
	    else{
		success = false;
		message += "You could not save  ";
	    }
	}
	else if(action.equals("Update")){
	    if(user.canEdit()){
		String back = owner.doUpdate();
		if(back.equals("")){
		    message += "Updated successfully";
		    if(delPhone != null && delPhone.length > 0){
			for(String str: delPhone){
			    Phone ph = new Phone(debug, str);
			    ph.doDelete();
			}
		    }
		}
		else{
		    message += back;
		}
	    }
	    else{
		success = false;
		message += " You can not update ";
	    }
	}
	else if(action.equals("zoom") || action.equals("Edit")){	
	    //
	    // Note:
	    // we bring the records that match the id and empid
	    // we do not let anybody modify other people record
	    //
	    String back = owner.doSelect();
	    if(!back.equals("")){
		message += " Could not retreive data "+back;
		success = false;
	    }
	}
	else if(action.startsWith("Remove")){
	    //
	    // delete the owner from the list of owners of this
	    // property but not from the overall list of owners
	    //
	    if(user.canDelete()){
		String back = owner.removeFromRental(id);
		if(back.equals("")){
		    message += " Removed successfully";
		}
		else{
		    success = false;
		    message += "Could not delete "+back;
		}
	    }
	    else{
		success = false;
		message += "You can not delete ";
	    }
	}
	else if(action.startsWith("Delete")){
	    //
	    // delete the owner from the list of owners
	    // make sure he/she does not have any link to properties first
	    //
	    if(user.canDelete()){
		String back = owner.doDelete();
		if(back.equals("")){
		    message += "Deleted Successfully";
		    name_num = "";
		    id = "";
		}
		else{
		    success = false;
		    message += back;
		}
	    }
	    else{
		success = false;
		message += "You can not delete ";
	    }
	}
	if(action.equals("Save") ||
	   action.equals("zoom") ||
	   action.equals("Update")){
	    if(owner.hasEmail() && !owner.hasValidEmail()){

		message += " The email "+owner.getEmail()+" is not valid <br />";

	    }
	    if(phone.hasNumber()){
		phone.setOwnerId(name_num);				
		phone.doSave();
	    }
	    if(phone2.hasNumber()){
		phone2.setOwnerId(name_num);				
		phone2.doSave();
	    }	
	}
	//
	out.println("<html><head><title>Owner</title>");
	Helper.writeWebCss(out, url);
	out.println("<script language=Javascript>");
	out.println("  function validateForm(){		                ");
	out.println("  if ((document.myForm.notes.value.length > 250)){ "); 
	out.println("     alert(\"You have entered \" + document.myForm.notes.value.length + \" characters in the notes field. Maximum characters allowed are 250\");		");
	out.println("  	document.myForm.notes.value = document.myForm.notes.value.substring(0,250);         ");
	out.println("    return false;				         ");
	out.println("	}					         ");
	out.println("  if ((document.myForm.oName.value.length == 0)){   "); 
	out.println("     alert(\"Need to enter the Owner/Agent name \");");
	out.println("     document.myForm.oName.focus(); "); 
	out.println("     return false;}		        	 ");
	out.println("  if ((document.myForm.email.value.length > 0)){   ");
	out.println("     var str = document.myForm.email.value;        ");
	out.println("     if(str.indexOf('@') == -1){                   ");
	out.println("     alert(\"Invalid email address \");         ");
	out.println("     document.myForm.email.focus();             "); 
	out.println("     return false;}		        	         ");		
	out.println("     if( str.indexOf(',') > -1 ){                  ");
	out.println("      var data = str.split(',');                    ");
	out.println("      for(var j=0;j<data.length;j++){              ");
	out.println("       if(!isEmail(data[j])){                       ");
	out.println("        alert('Invalid email address '+data[j]);      ");
	out.println("        return false;		        	         ");
	out.println("        }}                                       ");
	out.println("     }                                             ");
	out.println("     else{                                      ");
	out.println("       if(!isEmail(str)){                       ");
	out.println("        alert('Invalid email address '+str);         ");
	out.println("        return false;		        	         ");	
	out.println("      }                                             ");	
	out.println("	  }	         				                 ");
	out.println("	 }	         				                 ");		
	out.println("     return true;					             ");
	out.println("  }	         				                 ");
	out.println("  function isEmail(eml){                           ");
	// out.println("   return (eml.search('^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$') != -1); ");
	// out.println("   return (eml.search('@') != -1); ");
	out.println("  return true;                                  ");
	out.println("  }	         				                 ");		
	out.println("  function validateDelete(){	                 ");
	out.println("   var x = false;                               ");
	out.println("   x = confirm(\"Are you sure you want to delete this record from This Permit\");");
	out.println("     return x;                                  ");
	out.println("	}						                     ");
	out.println("  function validateDelete2(){	                 ");
	out.println("   var x = false;                               ");
	out.println("   x = confirm(\"Are you sure you want to delete this record Completely from the System.\");");
	out.println("     return x;                                  ");
	out.println("	}						                     ");
	out.println(" </script>		                                 ");
	if(action.equals("zoom")){
	    out.println(" </head><body>");
	}
	else
	    out.println(" </head><body "+
			"onLoad=\"document.myForm.oName.focus();\">");

	Helper.writeTopMenu(out, url);	

	//
	// delete 
	if(type.equals("")){
	    out.println("<h2><center> Owner Info </h2>");
	}
	else{
	    out.println("<h2><center> Agent Info </h2>");
	}
	if(action.equals("")){

	}
	else if(success){
	    //
	    if(!message.equals("")){
		out.println("<center><p><font color=green>");				
		out.println(message);
		out.println("</font></p></center>");				
	    }
	}
	else{
	    if(!message.equals("")){
		out.println("<center><p><font color=red>");				
		out.println(message);
		out.println("</font></p></center>");
	    }
	}
	//
	out.println("<table align=center width=80% border>");
	//
	if(action.equals("zoom")){
	    out.println("<form name=myForm method=post>");
	}
	else{
	    out.println("<form name=myForm method=post "+
			"onSubmit=\"return validateForm()\">");
	}
	//
	out.println("<input type=hidden name=id value=\""+id+"\">");
	if(!owner.getId().equals("")){
	    out.println("<input type=hidden name=name_num value=\""+
			owner.getId()+"\">");
	}
	if(type.equals("agent")){
	    out.println("<input type=hidden name=type value=agent>");
	}
	//
	out.println("<tr><td><table width=100%>");
	if(action.equals("zoom")){
	    //
	    // 1st block
	    out.println("<tr><td><b>Name: </b>"+owner.getFullName());
	    if(owner.isUnconfirmed()){
		out.println(" (Uncofirmed)");
	    }
	    out.println(" &nbsp;&nbsp;</td><td><b>ID: </b>"+owner.getId()+
			"</td></tr>");
	    out.println("<tr><td><b>Address: </b>");
	    out.println(owner.getAddress()+" </td></tr></table></td></tr>");
	    //
	    // city,state,zip,phones
	    out.println("<tr><td><table width=100%><tr><td><b>City</b>"+
			"</td><td><b>State</b></td><td><b>Zip Code</b></td></tr>");
	    out.println("<tr><td>");
	    out.println(owner.getCity()+" </td><td>"+owner.getState()+
			"</td><td>"+owner.getZip()+"</td></tr>");
	    out.println("</table></td></tr>");
	    List<Phone> phones = owner.getPhoneList();
	    if(phones != null && phones.size() > 0){
		out.println("<tr><td><table width=100%>");			
		out.println("<tr><td><b> Phone Type</b></td>"+
			    "<td><b>Phone Number</b></td></tr>");
		for(Phone ph: phones){
		    out.println("<tr><td>"+
				ph.getType()+"</td><td>"+
				ph.getNumber()+"</td></tr>");
		}
		out.println("</table></td></tr>");
	    }
	    out.println("<tr><td><b>Email: </b>");
	    out.println(owner.getEmail()+" </td></tr>");
	    //
	    out.println("<tr><td><b>Notes: </b>");
	    out.println(owner.getNotes());
	    out.println("</td></tr>");
	    out.println("<tr>");
	    /**
	    if(user.canEdit()){
		out.println("<td align=right>");
		out.println("<input type=submit "+
			    "name=action value=Edit></td>");
	    }
	    */
	    out.println("</form>");
	    out.println("</tr>");
	}
	else if(user.hasRole("Edit")){
	    //
	    /**
	    // 1st block
	    out.println("<tr><td><b>Name:</b>");
	    out.println("<input name=oName value=\""+owner.getFullName()+"\""+
			" size=50 maxlength=50>&nbsp;&nbsp;ID:"+owner.getId()+
			"</td></tr>");
	    out.println("<tr><td><b>Uncofirmed?</b>");
	    String checked = owner.isUnconfirmed()?"checked=\"checked\"":"";
	    out.println("<input type=checkbox name=\"unconfirmed\" value=\"y\""+
			checked+" /> "+(owner.isUnconfirmed()?"Uncheck to cofirm":"Check to unconfirm"));
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Address:</b>");
	    out.println("<input name=address value=\""+owner.getAddress()+"\""+
			" size=50 maxlength=50></td></tr></table></td></tr>");
	    //
	    // city,state,zip,phones
	    out.println("<tr><td><table><tr><td><b>City</b></td><td><b>State</b></td>");
	    out.println("<td><b>Zip</b></td></tr>");
	    out.println("<tr><td>");
	    out.println("<input name=city value=\""+owner.getCity()+"\""+
			" size=20 maxlength=20></td><td>");
	    out.println("<input name=state value=\""+
			owner.getState()+"\""+
			" size=2 maxlength=2></td><td>");
	    out.println("<input name=zip value=\""+
			owner.getZip()+"\""+
			" size=10 maxlength=10></td></tr>");
						
	    out.println("</table></td></tr>");
	    List<Phone> phones = owner.getPhoneList();
	    if(phones != null && phones.size() > 0){
		out.println("<tr><td><table width=60%><caption>Current Phones</caption>");	
		out.println("<tr><td></td><td><b>Phone Type</b></td>"+
			    "<td><b>Phone Number</b></td></tr>");
		for(Phone ph: phones){
		    out.println("<tr><td>");
		    out.println("<input type=checkbox value=\""+
				ph.getId()+"\""+
				" name=\"delPhone\">*</td><td>");
		    out.println(ph.getType());
		    out.println("</td><td>");
		    out.println(ph.getNumber());
		    out.println("</td></tr>");
		}
		out.println("</table></td></tr>");	
	    }
	    out.println("<tr><td><table width=60%><caption>Add New Phones</caption>");	
	    out.println("<tr><td><b>Phone Type</b></td>"+
			"<td><b>Phone Number</b></td></tr>");
	    out.println("<tr><td>");
	    out.println("<select name=phoneType>");
	    for(String str: Helper.phoneTypes){
		out.println("<option>"+str+"</option>");
	    }
	    out.println("</select></td><td>");
	    out.println("<input name=phone value=\"\""+
			" size=20 maxlength=20></td></tr>");
	    out.println("<tr><td>");
	    out.println("<select name=phoneType2>");
	    for(String str: Helper.phoneTypes){
		out.println("<option>"+str+"</option>");
	    }
	    out.println("</select></td><td>");
	    out.println("<input name=phone2 value=\"\""+
			" size=20 maxlength=20></td></tr>");
	    out.println("<tr><td colspan=2>You can add two phones at any time</td></tr>");
	    out.println("</table></td></tr>");
	    out.println("<tr><td><b>Email:</b>**");
	    out.println("<input name=email value=\""+owner.getEmail()+"\""+
			" size=50 maxlength=70>***Check note below</td></tr>");
	    //
	    out.println("<tr><td><b>Notes </b><font color=green size=-1>"+
			"Up to 250 characters<br></font>");
	    out.println("<textarea name=notes cols=70 rows=4 wrap>");
	    out.println(owner.getNotes());
	    out.println("</textarea>");
	    out.println("</td></tr>");
	    out.println("<tr>");
	    //
	    if(action.equals("") ||
	       (action.startsWith("Delete") && success) ||
	       (action.equals("Save") && !success)){
		out.println("<td align=right>");
		if(user.canEdit()){
		    out.println("<input type=submit "+
				"name=action value=Save>");
		}
		out.println("</form>");
	    }
	    else{ // submit update zoom
		//
		out.println("<td align=right>");
		out.println("<table cellpadding=0 cellspacing=0>"+
			    "<tr><td> "+
			    " </td><td valign=top>");
		if(user.canEdit()){
		    out.println("<input type=submit name=action "+
				"value=Update>");
		}
		out.println("</form></td><td valign=top>");
		if(user.canDelete() && !id.equals("")){
		    out.println("<form name=delForm "+
				"onSubmit=\"return "+
				"validateDelete();\">");
		    out.println("<input type=hidden name=id value="+id+">");
		    out.println("<input type=hidden name=name_num value="+
				owner.getId()+">");
		    out.println("<input type=submit name=action "+
				"value=\"Remove From This Property\">");
		    out.println("</form>");
		}
		out.println("</td><td valign=top>");
		if(user.canDelete()){
		    out.println("<form name=delAllForm "+
				"onSubmit=\"return "+
				"validateDelete2();\">");
		    out.println("<input type=hidden name=id value="+id+">");
		    out.println("<input type=hidden name=name_num value="+
				owner.getId()+">");
		    out.println("<input type=submit name=action "+
				"value=\"Delete From System\">");
		    out.println("</form>");
		}
		out.println("</td><td>&nbsp;</td></tr></table>");
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td>* Check the checkbox to delete the phone on Update</td></tr>");
	    out.println("<tr><td>** Please do not use any special characters in the email address</td></tr>");
	    out.println("<tr><td>*** Use comma to separate multiple emails</td></tr>");
	    */
			
	}
	out.println("</table>");	
       	out.flush();
	//
	if(!name_num.equals("")){
	    //
	    String titles[] = { "ID","Address","Pull Date","Expire Date","Pull Reason"};
	    //
	    // make a list of all properties that belong to this 
	    // owner
	    String str="";
	    List<Rent> rents = owner.getRents();
	    if(rents != null && rents.size() > 0){
		//
		out.println("<center><table border>");
		out.println("<caption>Properties</caption>");
		out.println("<tr>");
		for(int i=0;i<titles.length;i++){
		    out.println("<th>"+titles[i]+"</th>");
		}
		for(Rent rent: rents){
		    str = rent.getId();
		    List<Address> addrs = rent.getAddresses();
		    if(addrs != null && addrs.size() > 0){
			for(Address addr:addrs){
			    out.println("<tr><td>");	
			    out.println("<a href="+url+"Rental?id="+str+
					"&tag="+tag+"&action=zoom>"+str+"</a>");
			    out.println("</td><td>");
			    out.println(addr.getAddress());
			    out.println("</td><td>&nbsp;");
			    out.println(rent.getPull_date());
			    out.println("</td><td>&nbsp;");
			    out.println(rent.getPermit_expires());
			    out.println("</td><td>&nbsp;");
			    out.println(rent.getPullReason());
			    out.println("</td>");
			    out.println("</tr>");
			}
		    }
		    else{
			out.println("<tr><td>");	
			out.println("<a href="+url+"Rental?id="+str+
				    "&tag="+tag+"&action=zoom>"+str+"</a>");
			out.println("</td><td>");
			out.println("&nbsp;"); // no address yet
			out.println("</td><td>&nbsp;");
			out.println(rent.getPull_date());
			out.println("</td><td>&nbsp;");
			out.println(rent.getPermit_expires());
			out.println("</td></tr>");
		    }
		}
		out.println("</table>");
	    }
	    if(!id.equals("") && tag.equals(""))
		out.println("<a href="+url+"Rental?id="+id+
			    "&action=zoom>Back to Related Permit</a>");
	}
	out.print("</body></html>");
	out.close();
    }

}






















































