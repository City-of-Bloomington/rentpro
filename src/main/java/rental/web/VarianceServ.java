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

@WebServlet(urlPatterns = {"/VarianceServ"})
public class VarianceServ extends TopServlet{

    final static long serialVersionUID = 1040L;
    final static String bgcolor = "silver";// #bfbfbf gray
				
    static Logger logger = LogManager.getLogger(VarianceServ.class);
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
	String action="", vid="", message="";
	//
	String id="", role="";
	boolean success = true;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Variance variance = new Variance(debug);
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		id = value;
		variance.setRegistrId(value);
	    }
	    else if (name.equals("action")) {
		action = value;
		if(action.equals("New")) action = "";
	    }
	    else if (name.equals("variance")) {
		variance.setText(value);
	    }
	    else if (name.equals("vid")) {
		vid = value;
		variance.setId(value);
	    }
	    else if (name.equals("variance_date")) {
		variance.setDate(value);
	    }
	}
	
	
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=VarianceServ&id="+id;
		res.sendRedirect(str);
	    }
	}
	else{
	    String str = url+"Login?source=VarianceServ&id="+id;
	    res.sendRedirect(str);
	}

	if(action.equals("zoom") || action.equals("Edit")){
	    String back = variance.doSelect();
	    if(!back.equals("")){
		message += " Could not retreive data "+back;
		success = false;
	    }
	}
    	else if(action.equals("Save") && user.canEdit()){
	    String back = variance.doSave();
	    if(!back.equals("")){
		message += " Could not save data "+back;
		success = false;
	    }
	    else{
		vid = variance.getId();
		message += "Saved Successfully";
	    }
	}
	else if(action.equals("Update")){
	    String back = variance.doUpdate();
	    if(!back.equals("")){
		message += " Could not update data "+back;
		success = false;
	    }
	    else{
		message += "Updated Successfully";
	    }
	}
	else if(action.equals("Delete")){
	    String back = variance.doDelete();
	    if(!back.equals("")){
		message += " Could not delete data "+back;
		success = false;
	    }
	    else{
		variance = new Variance(debug);
		vid = "";
		message += "Deleted Successfully";				
	    }
	}
	else{ // new and ""
	    variance = new Variance(debug);
	}
	//
	out.println("<html><head><title>Variance</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateDelete(){	                     ");
	out.println("   var x = false;                                   ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     return x;                                       ");
	out.println("	}						                          ");
	out.println("  function doCount(item, size){	              ");
	out.println("   var len = item.value.length;                   ");
	out.println("     if(len > size){                 ");
	out.println("     alert(\"The text entered exceeds \"+size+\" limit\"); ");
	out.println("     }                                               ");
	out.println("   document.getElementById(\"remain\").innerHTML=(size-len)+\" characters remain\"; ");	
	out.println("	}						                          ");	
	out.println("  function validateForm(){	                          ");
	out.println("     return true;                                    ");
	out.println("	}						                          ");
	out.println(" </script>		                                      ");
	out.println("</head><body>");
	Helper.writeTopMenu(out, url);
	out.println("<center><h2>Rental Variance</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<p><font color=red>"+message+"</font></p>");
	}
	//
	if(action.equals("zoom")){
	    if(user.canEdit()){
		out.println("<form name=myForm method=post >");
	    }
	    out.println("<table width=70% border><tr><td>");
	    out.println("<table width=100%>");
	    out.println("<tr><td><b>Rental: </b>");
	    out.println("<a href=\""+url+"Rental?action=zoom&id="+id+"\">"+id+"</a>");			
	    out.println("</td></tr>");
	    out.println("<tr><td><b> Date: </b>");
	    out.println(variance.getDate());
	    out.println("</td></tr>");
	    out.println("<tr><td><b> Variance Details:  </b>");
	    out.println("</td></tr>");
	    out.println("<tr><td>");
	    out.println(Helper.replaceSpecialChars(variance.getText()));
	    out.println("</td></tr></table></td></tr>");
	    if(user != null && user.canEdit()){
		out.println("<tr><td align=right>");
		out.println("<input type=hidden name=id value="+variance.getRegistrId()+">");
		out.println("<input type=hidden name=vid value="+variance.getId()+">");
		out.println("<input type=submit name=action value=Edit>");
		out.println("</td></tr>");
	    }
	    out.println("</form>");			
	    out.println("</table>");
	}
	else if(user != null && user.canEdit()){
	    out.println("<form name=myForm method=post "+
			"onSubmit=\"return validateForm()\">");
	    out.println("<table width=80%>");
	    out.println("<tr><td><b>Rental: </b>");
	    out.println("<a href=\""+url+"Rental?action=zoom&id="+id+"\">"+id+"</a>");
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Date:</b>");
	    out.println("<input id=\"variance_date\" class=\"date\" name=\"variance_date\" size=\"10\" "+
			"maxlength=\"10\" value=\""+variance.getDate()+"\" />");
	    out.println("</td></tr>");
	    out.println("<tr><td><b>Variance Details: </b><font color=green><spna id=\"remain\">Up to 10000 "+
			"characters</span></font>");
	    out.println("</td></tr>");
	    out.println("<tr><td><textarea name=variance rows=20 "+
			"cols=70 wrap onkeydown=\"doCount(this, 10000)\">");
	    out.println(Helper.replaceSpecialChars(variance.getText()));
	    out.println("</textarea></td></tr>");
	    //
	    out.println("<input type=hidden name=id value="+id+">");
	    if(!vid.equals(""))
		out.println("<input type=hidden name=vid value="+vid+">");

	    //
	    if(action.equals("") || action.startsWith("New")||
	       action.equals("Delete")){
		if(user.canEdit()){
		    out.println("<tr><td align=right>  "+
				"<input type=submit "+
				"name=action "+
				"value=Save>&nbsp;&nbsp;&nbsp;"+
				"</td></tr>");
		}
	    }
	    else {  // save, update, zoom
		out.println("<tr><td><table width=100%><tr>");
		if(user.canEdit()){
		    out.println("<td valign=top "+
				"align=right>If you've made any changes click on");
		    out.println("<input type=submit name=action "+
				"value=Update></td>");
		}
		out.println("<td><input type=submit name=action "+
			    "value=New></td>");
		out.println("</form>");
		if(user.canEdit()){
		    out.println("<td><form name=delForm method=post "+
				"onSubmit=\"return validateDelete()\">");
		    out.println("</td><td valign=top align=right>");
		    out.println("<input type=hidden name=id value="+variance.getRegistrId()+">");
		    out.println("<input type=hidden name=vid value="+variance.getId()+">");
		    out.println("<input type=hidden name=variance_date "+
				" value=\""+variance.getDate()+"\">");
		    out.println("<input type=submit name=action "+
				"value=Delete></td>");
		    out.println("</form>");
		}
		out.println("</tr></table></td></tr>");
	    }
	    out.println("</table>");
			
	}
	VarianceList variances = new VarianceList(debug, id);
	String back = variances.find();
	if(variances != null && variances.size() > 0){
	    Helper.writeVariances(out, url, variances);
	}
	Helper.writeWebFooter(out, url);
	out.println("<br />");
	out.print("</body></html>");
	out.close();
    }

}






















































