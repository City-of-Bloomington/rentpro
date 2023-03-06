package rental.web;

import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/EditStructure"})
public class EditStructure extends TopServlet{

    final static long serialVersionUID = 270L;
    static Logger logger = LogManager.getLogger(EditStructure.class);
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
	String action="";
	String id="", rid="";

	boolean success = true;
	String message="";
	String [] vals;
	Structure structure = new Structure(debug);
	// 
	// class to handle multipart request (for example text + image)
	// the image file or any upload file will be saved to the 
	// specified directory
	// 
	Enumeration<String> values = req.getParameterNames();

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		structure.setId(value);
	    }
	    else if (name.equals("identifier")){
		structure.setIdentifier(value);
	    }
	    else if(name.equals("action")){
		if(value.equals("New")) action = "";
		else action = value;  
	    }
	}
	//

	if(action.equals("zoom") || action.equals("Edit")){
	    //	    
	    String back = structure.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
    	else if(action.equals("Update")){
	    String back = structure.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		message += " Updated Successfully";
	    }

	}
	out.println("<html><head><title>Edit Structure</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type='text/javascript'>");
	out.println("  function validateForm(){		                ");
	out.println("   with(document.myForm){                      ");
	out.println("    var b = identifier.value;         ");
	out.println("    if(b == ''){                     ");
	out.println("     alert('Invalid identifier ');  ");
	out.println("     return false;                   ");
	out.println("    }                               ");	
	out.println("    var seqId = id.value;           ");
	out.println("    opener.document.getElementById(seqId).firstChild.nodeValue = b; ");
	out.println("  }                                            ");
	out.println("   return true;                                ");
	out.println(" }                                             ");		
	out.println(" </script>		                                ");
    	out.println(" </head><body>                                 ");
	//
	out.println("<center><h2>Edit Structure </h2>");
	Helper.writeTopMenu(out, url);	
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");
	}	
	out.println("<table border width=80%>");
	out.println("<tr><td>");
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	//
	out.println("<input type=hidden name=id value="+structure.getId()+">");
	out.println("<table width=100%>");
	//
	// Identifier
	out.println("<tr><td><b>Identifier:</b>");
	out.println("<input size=15 maxlength=30 name=identifier value=\""+
		    structure.getIdentifier()+"\"></td></tr>");
	//
	// Update
	out.println("<tr><td align=right><input type=submit name=action "+
		    "value=Update>&nbsp;</td>");
	out.println("</td></tr>");
	//
	out.println("</table></td></tr>");
	out.println("</form>");
	out.println("</table><br>");
	out.println("<li><a href=javascript:window.close();>"+
		    "Close This Window</a></li>");
	out.print("</body></html>");
	out.close();
    }

}






















































