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
 * Generates a printable permit.
 *
 */
@WebServlet(urlPatterns = {"/Permit"})
public class Permit extends TopServlet{

    String bgcolor = Rental.bgcolor;
    String url="";
    boolean debug = false;
    final static long serialVersionUID = 670L;
    boolean userFoundFlag = false;
    static Logger logger = LogManager.getLogger(Permit.class);	
 
    /**
     * Generates the printable permit for the currnet registration.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{

	res.setContentType("text/html");
	String message = "";
	boolean success = true;
	PrintWriter out = res.getWriter();

	String reinsp_date="",reinsp_cnt="0",noshow_cnt="0",
	    noshow_date="",status="",inspector="",insp_date="",
	    paid="",check_no="",invoice_num="",occ_load="",bedrooms="",
	    units_struct="",zoning="", last_cycle_date = "",
	    today="",permit_expires="",compliance_date="";
	//
	String name, value;
	String action="", id="";
	String agentName="",agentAddr="",
	    agentAddr2="";	    
	//
	String propAddr="";
	String [] vals;
	Enumeration<String> values = req.getParameterNames();
	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
	    }
	}
	Rent rent = new Rent(id, debug);
	String back = rent.doSelect();
	if(!back.equals("")){
	    message += back;
	    success = false;
	}
	List<Owner> owners = rent.getOwners();
	List<Address> addresses = rent.getAddresses();
	Owner agent = rent.getAgent();
	if(addresses != null){
	    for(Address addr:addresses){
		if(!propAddr.equals(""))propAddr += ", ";
		propAddr += addr.getAddress();
	    }
	}
	if(!rent.hasUpdatedUnits()){
	    message += "The units,structures,bedrooms in this permit need to be updated according to the new setting ";
	    success = false;
	}
	//
	// Doctype is needed for the feature colapse to work in IE
	//
	out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");		
	out.println("<html><head><title>Rental Permit</title>");
	out.println("<style type=\"text/css\">");
	out.println("#centered { ");
	out.println("           text-align:center; ");
	out.println("           color:black;       ");
	out.println(" }                            ");
	// # id
	out.println("table { border-collapse:collapse; border:0px; margin:1em 0 1em 0; } ");
	out.println("tr { } ");
	out.println("td { padding:2px; background-color:inherit; border:0px; vertical-align:top;test-align:left;} ");
	out.println("th { text-align:center; border:1px solid #999999; background-color:#e0e0e0; padding:2px; vertical-align: top; font-weight:bold; } ");
	out.println(".main table ");
	out.println(" { border-collapse:collapse; border:1px solid; margin:1em 0 1em 0; } ");
	out.println(".main td { padding:2px; background-color:inherit; border:1px solid; vertical-align:top; text-align:center; } ");
	out.println(".main th { text-align:center; border:1px solid; padding:2px; vertical-align: top; font-weight:bold; } ");
	out.println(".lefted { text-align:left;} ");
	out.println("</style>");
	out.println("</head><body>");
	//
	if(!success){
	    out.println("<center><h3>Error </h3>");
	    out.println("<p><font color=red>"+message+"</font></p>");
	    out.println("</center></body></htmml>");
	    out.close();
	    return;
	}
	// we need these for default values of dates
	//
	//
	// we are only interested in the last inspection has compliance date
	// that is why we order by compliance_date in Descending order
	//
	if(true){
	    InspectionList inspects = new InspectionList(debug, id);
	    inspects.setOrderBy("compliance_date desc");
	    inspects.setComplianceDateNotNull();
	    back = inspects.find();
	    if(back.equals("") && inspects.size() > 0){
		Inspection inspect = inspects.get(0); // we want the top one
		compliance_date = inspect.getComplianceDate();
		Inspector one = inspect.getInspector();
		if(one != null){
		    inspector = ""+one;
		}
	    }
	}
	// 
	out.println("<br><br><br><br><br><br><br>");
	out.println("<font size=-1>");
	out.println("<center>"+
		    "A COPY OF THIS PERMIT AND THE RENTAL FILE ARE AVAILABLE"+
		    " FOR THE PUBLIC TO VIEW DURING <br>"+
		    "REGULAR BUSINESS HOURS AT THE HOUSING AND NEIGHBORHOOD "+
		    "DEVELOPMENT OFFICE<br></font>");
	//
	out.println("<hr width=100% size=1 noshade>");
	out.println("<b>");
	out.println("RESIDENTIAL RENTAL OCCUPANCY PERMIT</b><br />");
	out.println("HOUSING AND NEIGHBORHOOD DEVELOPMENT DEPARTMENT");
	out.println("<br />");
	out.println("<i>City of Bloomington, Indiana</i><br />");
	out.println("</center>");
	out.println("<table style=\"width:100%\"><tr><td></td><td style=\"text-align:right;\">");
	//
	// date
	out.println("<font size=1>"+Helper.getToday()+"</td></tr>");
	//
	out.println("<tr><td>&nbsp;</td></tr>");
	//
	// location
	out.println("<tr><td><font size=1>"+
		    "<b>Location: </b>"+propAddr+
		    "</font></td><td align=right><font size=1>Zone:"+
		    rent.getZoning() + "</font></td></tr>");
	out.println("</table>");
	// 
	// left side table
	out.println("<left><table><tr><td>"+
		    "<font size=1><b>Owner: </b>"+
		    "</font></td><td><font size=1>");
	if(owners != null){
	    int jj = 1;
	    for(Owner owner:owners){
		if(jj > 1) out.println("<br />");
		out.println(owner.getFullName()+"&nbsp;&nbsp;");
		out.println(owner.getAddress()+"&nbsp;&nbsp;");
		out.println(owner.getCityStateZip()+"&nbsp;&nbsp;");
		jj++;
	    }
	}
	out.println("</font></td></tr>");
	//
	// out.println("<tr><td>&nbsp;</td></tr>");
	//
	// Agent
	if(agent != null){
	    out.println("<tr><td>"+
			"<font size=1><b>Agent: </b></font></td><td>");
	    out.println("<font size=1>");
	    out.println(agent.getFullName()+"&nbsp;&nbsp;");
	    out.println(agent.getAddress()+"&nbsp;&nbsp;");
	    out.println(agent.getCityStateZip());
	    out.println("</font></td></tr>");
	}
	out.println("</table>");
	//
	out.println("<table style=\"width:100%\"><tr><td><font size=1>"+
		    "<b>Structures/Units: </b>"+rent.getStructures()+"/"+rent.getUnits()+" </td><td><font size=1><b>Inspector: </b>"+inspector+"</td></tr>");
	out.println("</table>");
	out.println("<div class=\"main\">");
	StructureList structs = rent.getStructs();
	if(structs != null){
	    String cellh = "";
	    int totalRows = structs.getTotalItems();
	    if(structs.hasUninspections()){
		cellh = "<th><font size=1>Inspection</font></th>";
	    }			
	    if(totalRows < 9){
		out.println("<table style=\"width:60%\">");
		out.println("<tr><th><font size=1>Structure</font></th>"+
			    "<th><font size=1>Units</font></th>"+
			    "<th><font size=1>Bedrooms per Unit</font></th>"+
			    "<th><font size=1>Max Occupant Load per Unit</font></th>");
		out.println(cellh);
		out.println("</tr>");
		for(Structure strc: structs){
		    UnitList units = strc.getUnits();
		    for(Unit unit:units){
			out.println("<tr><td style=\"text-align:left\"><font size=1>"+strc.getIdentifier()+"</font></td>");
			out.println("<td><font size=1>"+unit.getUnits()+" ");
			out.println(unit.isSleepRoom()?"RH":"");
			out.println("</font></td>");
			out.print("<td><font size=1>");
			int beds = unit.getBedrooms();
			if(beds > 0){
			    out.print(beds);
			    out.print(((unit.isSleepRoom())?" SR":""));
			}
			else{
			    out.print("Eff");
			}
			out.println("</font></td>");
			out.println("<td><font size=1>"+unit.getOccLoad()+"</font></td>");
			if(structs.hasUninspections()){
			    String inspected = "";							
			    if(unit.isUninspected()){
				inspected = "(Uninspected)";
			    }
			    out.println("<td><font size=1>"+inspected+"</font></td>");
			}
			out.println("</tr>");
		    }
		}
		out.println("</table>");
	    }
	    else{
		if(structs.hasUninspections()){

		}
		out.println("<table style=\"width:100%;\">");
		out.println("<tr>");
		out.println("<th><font size=1>Structure</font></th>"+
			    "<th><font size=1>Units</font></th>"+
			    "<th><font size=1>Bedrooms per Unit</font></th>"+
			    "<th><font size=1>Max Occupant Load per Unit</font></th>");
		out.println(cellh);
		out.println("<th><font size=1>Structure</font></th>"+
			    "<th><font size=1>Units</font></th>"+
			    "<th><font size=1>Bedrooms per Unit</font></th>"+
			    "<th><font size=1>Max Occupant Load per Unit</font></th>");
		out.println(cellh);				
		out.println("</tr>");
		int row = 0;
		for(Structure strc: structs){
		    UnitList units = strc.getUnits();
		    for(Unit unit:units){
			if(row == 0)
			    out.println("<tr>");
			out.println("<td style=\"text-align:left\"><font size=1>"+strc.getIdentifier()+"</font></td>");
			out.println("<td><font size=1>"+unit.getUnits()+"</font></td>");
			out.print("<td><font size=1>");
			int beds = unit.getBedrooms();
			if(beds > 0){
			    out.print(beds);
			    out.print(((unit.isSleepRoom())?" SR":""));
			}
			else{
			    out.print("Eff");
			}
			out.println("</font></td>");					
			out.println("<td><font size=1>"+unit.getOccLoad()+"</font></td>");
			if(structs.hasUninspections()){
			    String inspected = "";							
			    if(unit.isUninspected()){
				inspected = "(Uninspected)";
			    }
			    out.println("<td><font size=1>"+inspected+"</font></td>");
			}
						
			if(row == 1)
			    out.println("</tr>");
			row++;
			if(row > 1) row = 0;
		    }
		}
		// for odd numbers
		if(row == 1)
		    out.println("<td>&nbsp;</td></tr>");
		out.println("</table>");
	    }
	}
	out.println("</div>");
	//
	out.println("<p><font size=-1>The permit certifies compliance with the provision of Title 16 of the Bloomington Municipal Code, \"Bloomington Residential Rental Unit and Lodging Establishment Inspection Program\", and does not represent compliance with any other Title of the Bloomington Municipal Code or other relevant statutes or ordinances, particularly in regards to laws which regulate the zoning of this property. No change of use shall be made in this location without the prior approval of the applicable departments.</font></p>");
	//
	insp_date = rent.getLast_cycle_date();
	if(insp_date == null){
	    insp_date = "&nbsp;";
	}
	out.println("<table style=\"width:100%\"><tr><td><font size=1>"+
		    "Date Inspected: "+insp_date+"</td><td><font size=1>"+
		    "Date Complied: "+compliance_date+
		    "</td><td><font size=1>"+
		    "PERMIT EXPIRES: "+rent.getPermit_expires()+
		    "</td></tr></table>");
	//
	VarianceList vars = rent.getVariances();
	if(vars != null && vars.size() > 0){
	    out.println("<table><tr><td colspan=2>");
	    out.println("<b>B.H.Q.A Variance Conditions of Approval:<b></td></tr>");
	    for(Variance var:vars){
		out.println("<tr><td valign=top><font size=1>");
		String str = var.getDate();
		if(str == null)str = "&nbsp;";
		out.println(str+"</td><td><font size=1>");
		str = var.getText();
		if(str == null)str="&nbsp;";
		out.println(str+"</td></tr>");
	    }
	    out.println("</table>");			
	}
	out.println("<table style=\"width:100%\"><tr><td align=\"right\"><br />");
	out.println("__________________________________");
	out.println("<tr><td align=right>");
	out.println("<font size=1>Housing Official</font</td></tr>");
	out.println("</table>");
	//
	out.println("<hr width=\"100%\" size=\"1\" noshade>");
	//
	out.println("<br>");
	out.println("<center><b>A copy of the permit must be displayed ");
	out.println("on the inside of the main entrance of the rental "+
		    "units</b><br />");
	out.println("<font size=\"1\">Reminder: Each residential rental unit shall be scheduled to receive a cycle inspection at least sixty days prior to the expiration of its permit. Don't forget to call HAND before this time. (16.03.040) </font><br />");
	out.println("</body>");
	out.println("</html>");
	out.close();
    }
    /**
     * Genetates the printalbe permit.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
	doGet(req,res);
    }

}























































