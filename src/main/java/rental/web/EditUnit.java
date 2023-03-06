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

@WebServlet(urlPatterns = {"/EditUnit"})
public class EditUnit extends TopServlet{

    final static long serialVersionUID = 280L;
    static Logger logger = LogManager.getLogger(EditUnit.class);
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
	String id="", sid="", rid="", occLoad="",units="",beds="";

	boolean success = true;
	String username = "", message="";
	String [] vals;
	Unit unit = new Unit(debug);
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
		unit.setId(value);
	    }
	    else if (name.equals("sid")){
		unit.setSid(value);
	    }
	    else if (name.equals("units")) {
		unit.setUnits(value);
	    }
	    else if (name.equals("bedrooms")) {
		unit.setBedrooms(value);
	    }
	    else if (name.equals("occLoad")) {
		unit.setOccLoad(value);
	    }
	    else if (name.equals("sleepRoom")) {
		unit.setSleepRoom(value);
	    }
	    else if (name.equals("uninspected")) {
		unit.setUninspected(value);
	    }			
	    else if(name.equals("action")){
		if(value.equals("New")) action = "";
		else action = value;  
	    }
	}
	//
	if(action.equals("zoom") || action.equals("Edit")){
	    //	    
	    String back = unit.doSelect();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
    	else if(action.equals("Update")){
	    String back = unit.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		message += " Updated Successfully";
	    }

	}
    	else if(action.equals("Delete")){
	    // not done here
	    String back = unit.doDelete();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	StructureList structs = null;
	if(true){
	    structs = new StructureList(debug, rid);
	    String back = structs.find();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}		
    	//
	out.println("<html><head><title>Edit Unit Group</title>");
	out.println("<script type='text/javascript'>");
	out.println("  function validateForm(){		                ");
	out.println("   with(document.myForm){                      ");
	out.println(" var u = units.value;              ");		
	out.println(" var b = bedrooms.selectedIndex;         ");
	out.println(" var l = occLoad.value;           ");
	out.println(" var sr = sleepRoom.checked;           ");
	out.println(" var uninsp = uninspected.checked;           ");		
	out.println(" if(b.length > 0){                                 ");
	out.println(" if(isNaN(b)){                                     ");
	out.println(" alert(\"bedrooms not a valid number \");        ");
	out.println("   return false; }}                                ");
	out.println(" if(u.length > 0){                                 ");
	out.println(" if(isNaN(u)){                                     ");
	out.println(" alert(\"Units not a valid number \");             ");
	out.println("   return false; }}                                ");
	out.println(" if(l.length > 0){                                 ");
	out.println(" if(isNaN(l)){                                     ");
	out.println(" alert(\"Occupation Load not a valid number \");  ");
	out.println("   return false; }}                                ");
	//
	// update the text on the main page
	//
	out.println("  var seqId = id.value;           ");
	//
	// get the old values
	//
        out.println("  var oldu = opener.document.getElementById(seqId+1).firstChild.nodeValue; "); 
        out.println("  var oldb = opener.document.getElementById(seqId+2).firstChild.nodeValue; "); 
        out.println("  var oldl = opener.document.getElementById(seqId+5).firstChild.nodeValue ");
	out.println("  if(isNaN(oldb)) oldb = 0;               ");
	out.println("  var oldTtlUnts = opener.document.getElementById('iUnits').value; ");
	out.println("  var oldTtlBeds = opener.document.getElementById('iBeds').value; ");
	out.println("  var oldTtlLds = opener.document.getElementById('iLoad').value; ");				
	//
	// set the new values
        out.println("  opener.document.getElementById(seqId+1).firstChild.nodeValue = u; ");
	out.println("  if(b == 0){ ");
        out.println("      opener.document.getElementById(seqId+2).firstChild.nodeValue = ''; ");
	out.println("      opener.document.getElementById(seqId+3).firstChild.nodeValue = 1; ");
	out.println("  } else {   ");
        out.println("      opener.document.getElementById(seqId+2).firstChild.nodeValue = b; ");
	out.println("      opener.document.getElementById(seqId+3).firstChild.nodeValue = ''; ");
	out.println("  }  ");
        out.println("  opener.document.getElementById(seqId+5).firstChild.nodeValue = l; ");
	out.println("  if(sr){ ");
	out.println("      opener.document.getElementById(seqId+0).firstChild.nodeValue = 'RH'; ");
	out.println("      opener.document.getElementById(seqId+'00').firstChild.nodeValue = 'Yes'; ");	
	out.println("      opener.document.getElementById(seqId+4).firstChild.nodeValue = 'SR'; ");
		
	out.println("  } else { ");
	out.println("      opener.document.getElementById(seqId+0).firstChild.nodeValue = ''; ");
	out.println("      opener.document.getElementById(seqId+'00').firstChild.nodeValue = ''; ");		
	out.println("      opener.document.getElementById(seqId+4).firstChild.nodeValue = ''; ");		
	out.println("  } ");
	out.println("  if(uninsp){ ");
	out.println("      opener.document.getElementById(seqId+6).firstChild.nodeValue = 'Yes'; ");
	out.println("  } else { ");
	out.println("      opener.document.getElementById(seqId+6).firstChild.nodeValue = ''; ");
	out.println("  } ");		
	// set the totals
	out.println("  opener.document.getElementById('iUnits').value = (oldTtlUnts - oldu*1+u*1); ");
	out.println("  opener.document.getElementById('iBeds').value = (oldTtlBeds*1 - oldu*oldb + u*b); ");
	out.println("  opener.document.getElementById('iLoad').value = (oldTtlLds*1 - oldu*oldl + u*l); ");
	out.println("  opener.updateTotals(); ");
	out.println("   }                                ");
	out.println("   return true;                                  ");
	out.println(" }                               ");		
	out.println(" </script>		                                ");
    	out.println(" </head><body>                                     ");
	//
	out.println("<center><h2>Edit Unit </h2>");
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<h3><font color=red>"+message+"</font></h3>");
	}	
	//
	out.println("<table border width=80%>");
	out.println("<tr><td>");
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	//
	out.println("<input type=hidden name=id value=\""+unit.getId()+"\" />");
	out.println("<table width=100%>");
	//
	// Structure
	out.println("<tr><td><b>Structure (Building):</b></td><td>");
	Structure struct = unit.getStructure();
	if(struct != null){
	    out.println(struct.getIdentifier());
	}
	out.println("</td></tr>");		
	//
	// Units
	out.println("<tr><td><b>Units:</b></td><td>");
	out.println("<input size=3 maxlength=3 name=units value=\""+
		    (unit.getUnits())+"\"></td></tr>");
	//
	out.println("<tr><td><b>Rooming House/Sleeping Rooms?</b></td><td>");
	out.println("<input type=\"checkbox\" name=\"sleepRoom\" value=\"y\" ");
	out.println(unit.isSleepRoom()? "checked=\"checked\"":"");
	out.println(" />yes</td></tr>");
		
	// Beds
	out.println("<tr><td><b>Bedrooms:</b></td><td>");
	out.println("<select name=bedrooms id=bedrooms>");
	int bedCnt = unit.getBedrooms();
	for(int j=0;j<Helper.bedsArr.length;j++){
	    String selected = "";
	    if(j == bedCnt) selected = "selected=\"selected\"";
	    out.println("<option value=\""+j+"\" "+selected+">"+
			Helper.bedsArr[j]+"</option>");
	}
	out.println("</select>per unit</td></tr>");
	//
		
	// occupation load
	out.println("<tr><td><b>Occupation Load:</b></td><td>");
	out.println("<input size=3 maxlength=3 name=occLoad value=\""+
		    (unit.getOccLoad())+"\">per unit</td></tr>");
	//
	// bathrooms
	out.println("<tr><td><b>Bathrooms:</b></td><td>");
	out.println("<input size=3 maxlength=3 name=baths value=\""+
		    (unit.getBaths())+"\">per unit</td></tr>");
	out.println("<tr><td><b>Uninspected?</b></td><td>");
	out.println("<input type=\"checkbox\" name=\"uninspected\" value=\"y\" ");
	out.println(unit.isUninspected()? "checked=\"checked\"":"");
	out.println(" />yes</td></tr>");
	//
	// Update
	out.println("<tr><td colspan=2 align=right><input type=submit name=action "+
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






















































