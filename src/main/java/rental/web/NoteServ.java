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
/**
 * 
 *
 */
@WebServlet(urlPatterns = {"/NoteServ"})
public class NoteServ extends TopServlet{

    final static long serialVersionUID = 1044L;
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
	String action="", message="";
	//
	String id="", notes=""; // rental id
	boolean success = true;

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	RentalNote note = new RentalNote(debug);
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")) {
		id = value;
		note.setRental_id(value);
	    }
	    else if (name.equals("action")) {
		action = value;
	    }
	    else if (name.equals("notes")) {
		note.setNotes(value);
	    }
	}
	User user = null;
	HttpSession session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=NoteServ&id="+id;
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=NoteServ&id="+id;
	    res.sendRedirect(str);
	    return; 
	}
	if(action.equals("Save")){
	    if(user.canEdit() || user.isInspector()){
		note.setUserid(user.getUsername());
		String back = note.doSave();
		if(!back.equals("")){
		    message += " Notes error "+back;
		    success = false;
		}
		else{
		    message += "Saved Successfully";
		}
	    }
	}
	//
	out.println("<html><head><title>Rental Notes</title>");
	out.println("<script type=\"text/javascript\">");
	out.println("  function validateForm(){		                   ");
	out.println("  if ((document.myForm.notes.value.length > 1000)){ "); 
	out.println("     alert(\"You have entered \" + document.myForm.notes.value.length + \" characters in the notes field. Maximum characters allowed are 1000\");		");
	out.println("  	document.myForm.notes.value = document.myForm.notes.value.substring(0,1000);         ");
	out.println("    return false;			    ");
	out.println("	     }				            ");
	out.println(" }                         ");
	out.println("  function refreshOpener(){		               ");
	out.println("  var url=\""+url+"Rental?id="+id+"&action=zoom\";");
	out.println("  window.opener.document.location.href=url;");
	out.println("  window.close();            ");				
	out.println("	}				                    ");		
	out.println(" </script>		                ");
	if(!action.equals("") && success){
	    out.println("</head><body onload=\"refreshOpener();\">");
	}
	else{
	    out.println("</head><body>");
	}
	out.println("<center><h2>Rental Notes</h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<p><font color=red>"+message+"</font></p>");
	}
	//
	out.println("<form name=\"myForm\" method=\"post\" onsubmit=\"return validateForm()\">");
	out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	out.println("<table width=\"80%\" border=\"1\">");
	out.println("<tr><td>");
	out.println("<table width=\"100%\">");
	out.println("<tr><td><b>Rental: </b>");
	out.println(id);			
	out.println("</td></tr>");
	out.println("<tr><td><b> Motes: (1000 characters max) </b></td></tr>");
	out.println("<tr><td><textarea name=\"notes\" rows=\"10\" cols=\"50\" wrap>");
	out.println(Helper.replaceSpecialChars(note.getNotes()));
	out.println("</textarea>");
	out.println("</td></tr>");
	out.println("</table></td></tr>");
	out.println("<tr><td align=\"right\">");
	out.println("<input type=\"submit\" name=\"action\" "+
		    "value=\"Save\" /></td></tr>");
				
	out.println("</table>");
	out.println("<a href=\"javascript:window.close();\">Close this window.</a>");				
	out.println("</form></center>");
	out.println("</body></html>");
	out.close();
    }

}






















































