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

@WebServlet(urlPatterns = {"/EditBullet"})
public class EditBullet extends TopServlet{

    final static long serialVersionUID = 250L;
    static Logger logger = LogManager.getLogger(EditBullet.class);
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
	String sid="", message="";

	boolean success = true;
	String username = "", item="",i_date="";
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
	    else if (name.equals("item")) {
		item =value;
	    }
	    else if (name.equals("i_date")) {
		i_date =value;
	    }
	    else if(name.equals("action")){
		if(value.equals("New")) action = "";
		else action = value;  
	    }
	}
	//
	// String today = Opportunity.getToday();
	try{
	    con = Helper.getConnection();
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
	    String qq = "select sid,to_char(i_date,'mm/dd/yyyy'),item "+
		" from r_text_items "+
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
		    if(str != null && !str.equals("")) i_date = str;
		    str = rs.getString(3);
		    if(str != null && !str.equals("")) item = str;
		}
	    }
	    catch(Exception ex){
		System.err.println(ex);
		success = false;
		message += ex;
		logger.error(message+":"+qq);
	    }
	}
    	else if(action.equals("Update")){

	    String qq = "";
	    qq += " update r_text_items set ";
	    qq += "i_date=to_date('"+i_date+"','mm/dd/yyyy'),";
	    qq += "item='"+ Helper.doubleApostrify(item)+"'";
	    qq += " where sid="+sid;
	    //
	    if(debug){
		System.err.println(qq);
		logger.debug(qq);
	    }
	    try{
		stmt.executeUpdate(qq);
		message +=" Updated successfully ";
	    }
	    catch(Exception ex){
		success = false;
		message += ex;				
		System.err.println(ex);
		logger.error(message+":"+qq);
	    }
	}
	//
	out.println("<html><head><title>Item Review</title>");
	out.println("<script language=Javascript>");
	out.println("  function validateMonth(mm){	        ");
	out.println(" var len = mm.length;                      ");
	out.println(" if(len == 1){                             ");
	out.println("     if(isNaN(mm)){             ");
	out.println("        return false;			");
	out.println("      }                                    ");
	out.println("     if(mm == \"0\"){                      ");
	out.println("       return false;			");
	out.println("     }                                     ");
	out.println("  }else{                                   ");
	out.println("     if(isNaN(mm)){ ");
	out.println("        return false;		        ");
	out.println("      }                                    ");
	out.println("    if(mm == \"00\" || mm > 12 || mm < 1){           ");
	out.println("     return false;			        ");
	out.println("  }}                                          ");
	out.println("     return true;				   ");
	out.println("  }                                           ");
	out.println("  function validateYear(yy){	           ");
	out.println(" var len = yy.length;                         ");
	out.println(" if(!(len == 2 || len == 4)){                 ");
	out.println("     return false;}			   ");
	out.println("    if(isNaN(yy)){                 ");
	out.println("     return false;				   ");
	out.println("    }                                         ");
	out.println("     return true;				   ");
	out.println("  }                                           ");
	out.println("  function validateDay(dd){	           ");
	out.println(" var len = dd.length;                         ");
	out.println("    if(isNaN(dd))                  "); 
	out.println("     return false;			           ");
	out.println("    if(dd > 31 || dd < 1){ return false;}    ");
	out.println("     return true;				    ");
	out.println("  }                                            ");
	//
        out.println("  function validateDate(xx){               ");
	out.println("  var len = xx.length;                     ");
	out.println("  if(len == 0) return true;                ");
	out.println("  var n1 = xx.indexOf('/');                ");
	out.println("   var mon = xx.substr(0,n1);              ");
	out.println("   var rest = xx.substr(n1+1,len);         ");
	out.println("  var n2 = rest.indexOf('/');              ");
	out.println("  var len2 = rest.length;                  ");
	out.println("   var day = rest.substr(0,n2);            ");
	out.println("   var yyyy = rest.substr(n2+1,len2);      ");
	out.println("   if(!validateMonth(mon)){                ");
	out.println("   alert(\"invalid month \"+mon);          ");
	out.println("   return false;                           ");
	out.println("   }                                       ");
	out.println("   if(!validateDay(day)){                  ");
	out.println("   alert(\"invalid date \"+day);           ");
	out.println("   return false;                           ");
	out.println("   }                                       ");
	out.println("   if(!validateYear(yyyy)){                ");
	out.println("   alert(\"invalid year \"+yyyy);          ");
	out.println("   return false;                           ");
	out.println("   }                                       ");
	out.println("   return true;                            ");
	out.println("  }                                        ");
	out.println("  function validateForm(){		            ");
	out.println(" var d = document.myForm.i_date.value;              ");
	out.println(" var u = document.myForm.item.value;              ");
	out.println("   if(!validateDate(d)) return false;         ");
	//
	// update the text on the main page
	//
	out.println(" var seqId = document.myForm.sid.value;           ");
        out.println(" opener.document.getElementById(seqId+0).firstChild.nodeValue = d; "); 
        out.println(" opener.document.getElementById(seqId+1).firstChild.nodeValue = u; "); 
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
	    // Date
	    out.println("<tr><td><b>Action Date:</b>");
	    out.println("<input size=10 maxlength=10 name=i_date value=\""+
			i_date+"\"></td></tr>");
	    //
	    // Bullet
	    out.println("<tr><td><b>Action:</b>");
	    out.println("<input size=40 maxlength=80 name=item value=\""+
			item+"\"></td></tr>");
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






















































