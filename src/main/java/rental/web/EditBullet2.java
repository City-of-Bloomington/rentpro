package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.naming.directory.*;
import javax.naming.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;
import rental.list.*;


public class EditBullet2 extends TopServlet{

    final static long serialVersionUID = 260L;
    static Logger logger = LogManager.getLogger(Address.class);
    /**
     * Generates the main form with the view of
     * previously entered information.
     *
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
     * @link #doGet
     * @see #doGet
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String action="";
	String sid="";

	boolean success = true;
	String username = "", bullet="", message="";
	int access=0;
	String [] vals;
	// 
	// class to handle multipart request (for example text + image)
	// the image file or any upload file will be saved to the 
	// specified directory
	// 
	//MultipartRequest mreq = new MultipartRequest(req, saveDirectory);
	Enumeration<String> values = req.getParameterNames();

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("sid")){
		sid = value;
	    }
	    else if (name.equals("bullet")) {
		bullet =value;
	    }
	    else if(name.equals("action")){
		if(value.equals("New")) action = "";
		else action = value;  
	    }
	}
	//
	if(url.equals("")){
	    url  = getServletContext().getInitParameter("url");
	    String debug2 = getServletContext().getInitParameter("debug");
	    if(debug2.equals("true")) debug = true;
	}
	// String today = Opportunity.getToday();
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
	    System.err.println(ex);
	    success = false;
	    message += " could not connect to database "+ex;
	    logger.error(message);
	}
	//        
	if(action.equals("zoom") || action.equals("Edit")){
	    //	    
	    String qq = "select sid,bullet "+
		" from r_text_bullets "+
		" where sid="+sid;
	    String str="";
	    if(debug){
		System.err.println(qq);
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(2);
		    if(str != null && !str.equals("")) bullet = str;
		}
	    }
	    catch(Exception ex){
		success = false;
		message += " could not retreive data "+ex;
		System.err.println(ex);
		logger.error(message+" : "+qq);
	    }
	}
    	else if(action.equals("Update")){

	    String qq = "";
	    qq += " update r_text_bullets set ";
	    qq += "bullet='"+ Helper.doubleApostrify(bullet)+"'";
	    qq += " where sid="+sid;
	    //
	    if(debug){
		System.err.println(qq);
		logger.debug(qq);
	    }
	    try{
		stmt.executeUpdate(qq);
		message += " Updated successfully";
	    }
	    catch(Exception ex){
		success = false;
		message += " could not update record "+ex;			
		System.err.println(ex);
		logger.error(message+": "+qq);
	    }
	}
	//
	out.println("<html><head><title>Item Review</title>");
	out.println("<script language=Javascript>");
	out.println("  function validateForm(){		                ");
	out.println(" var u = document.myForm.bullet.value;              ");
	//
	// update the text on the main page
	//
	out.println(" var seqId = document.myForm.sid.value;           ");
        out.println(" opener.document.getElementById(seqId+0).firstChild.nodeValue = u; "); 
	out.println("   return true;}                                   ");
	out.println(" </script>		                                ");
    	out.println(" </head><body>                                     ");
	//
    	if(action.equals("") || 
	   action.equals("Delete")){
	    out.println("<center><h2>View Item </h2>");
	}
	else { // zoom, update, save
	    if(action.equals("zoom"))
		out.println("<center><h2>View Text Items</h2>");
	    else 
		out.println("<center><h2>Update Text Items</h2>");
	    //
	}
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");
	}		
	//
	if(action.equals("zoom") || action.equals("Edit")){
	    //
	    out.println("<table border width=80%>");
	    out.println("<tr><td>");
	    out.println("<form name=myForm method=post "+
			"onSubmit=\"return validateForm()\">");
	    //
	    out.println("<input type=hidden name=sid value="+sid+">");
	    //
	    // 1st block
	    out.println("<table width=100%>");
	    //
	    // Bullet
	    out.println("<tr><td><b>Text Item:</b>");
	    out.println("<input size=60 maxlength=80 name=bullet value=\""+
			bullet+"\"></td></tr>");
	    //
	    out.println("<tr><td align=right><input type=submit name=action "+
			"value=Update>&nbsp;</td>");
	    out.println("</td></tr>");
	    //
	    out.println("</table></td></tr>");
	    out.println("</form>");
	    out.println("</table><br>");
	}
	out.println("<li><a href=javascript:window.close();>"+
		    "Click to Close This Window</a>");
	out.print("</body></html>");
	out.close();
    	Helper.databaseDisconnect(con,stmt,rs);		
    }

}






















































