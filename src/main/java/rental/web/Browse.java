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

@WebServlet(urlPatterns = {"/Browse","/Search"})
public class Browse extends TopServlet {

    boolean debug = false;
    final static long serialVersionUID = 140L;
    int maxlimit = 100; // limit on records
    String allQueryOptions ="<option>\n<option>is<option>contains"+
	"<option>starts with<option>ends with</select>";
    String bgcolor = Rental.bgcolor;
    static Logger logger = LogManager.getLogger(Browse.class);
    //
    // Global sharable arrays
    //
    List<Zone> zones = null;
    List<Item> pulls = null;
    List<Item> pstats = null;
	
    String yesNoOpts ="<option selected value=\"\">\n"+
	"<option value=Y>Yes"+
	"<option value=N>No"+
	"</select>";
    /**
     * Generates the form for the search engine.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	boolean success = true;
	String name, value;
	String date_from = "", date_to = "", req_d="";

	String byLocation="", byOwner="", message="";
	String byProp="",byAll="",byPermit="";
	String registr_d="", issue_d="",cycle_d="",expire_d="",
	    bill_d="",rec_d="",pull_d="",insp_d="",receipt_no="",bid="";

	String username = "";
	//
	// Global temporary sharable arrays
	//

	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("byLocation")){
		byLocation = value;
	    }
	    else if (name.equals("byOwner")){
		byOwner = value;
	    }
	    else if (name.equals("message")){
		message = value;
	    }						
	    else if (name.equals("byPermit")){
		byPermit = value;
	    }
	    else if (name.equals("byProp")){
		byProp = value;
	    }
	    else if (name.equals("byAll")){
		byAll = value;
	    }
	    else if (name.equals("registr_d")){
		registr_d = value;
	    }
	    else if (name.equals("issue_d")){
		issue_d = value;
	    }
	    else if (name.equals("cycle_d")){
		cycle_d = value;
	    }
	    else if (name.equals("expire_d")){
		expire_d = value;
	    }
	    else if (name.equals("bill_d")){
		bill_d = value;
	    }
	    else if (name.equals("rec_d")){
		rec_d = value;
	    }
	    else if (name.equals("insp_d")){
		insp_d = value;
	    }
	    else if (name.equals("req_d")){
		req_d = value;
	    }
	    else{
		// System.err.println("Unknown "+name+" "+value);
	    }
	}
	// 
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=Browse";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?source=Browse";
	    res.sendRedirect(str);
	    return; 
	}
	if(true){
	    String str ="", str2="";
	    if(zones == null){
		ZoneList zl = new ZoneList(debug);
		str = zl.find();
		if(str.equals("")){
		    zones = zl;
		}
	    }
	    if(pulls == null){
		PullList zl = new PullList(debug);
		str = zl.find();
		if(str.equals("")){
		    pulls = zl.getPulls();
		}
	    }
	    if(pstats == null){
		PropStatList psl = new PropStatList(debug);
		String back = psl.find();
		if(back.equals("")){
		    pstats = psl.getPropStatuses();
		}
	    }			
	}
	//
	out.println("<html><head><title>Rental</title>");
	Helper.writeWebCss(out, url);		
	out.println("<script>");
	out.println("                            ");
	out.println(" function moveToNext(item, size, nextItem, e){ ");
	out.println("  var keyCode = \" \";  ");
	out.println(" keyCode = (window.Event) ? e.which: e.keyCode;  ");
	//out.println("  alert(\" keycode = \"+keyCode);  ");
	out.println("  if(keyCode > 47 && keyCode < 58){  "); // only numbers
	out.println("  if(item.value.length > size - 1){         ");
	out.println("  eval(nextItem.focus());      ");
	out.println("  }}}      ");
	out.println("  function checkDate(dd){		                   ");
	out.println("   if(dd.length == 0) return true;                "); 
	out.println("   else if(dd.length != 10){                      "); 
	out.println("      return false; }                             ");
	out.println("   else {                                         "); 
	out.println("   var m = dd.substring(0,2);                     "); 
	out.println("   var d = dd.substring(3,5);                     "); 
	out.println("   var y = dd.substring(6,10);                    "); 
	out.println(" if(!(dd.charAt(2) == \"/\" && dd.charAt(5)== \"/\")){ ");
	out.println("      return false; }                             ");
	out.println("   if(isNaN(m) || isNaN(d) || isNaN(y)){ ");
	out.println("      return false; }                             ");
	out.println("   if( !((m > 0 && m < 13) && (d > 0 && d <32) && ");
	out.println("    (y > 1900 && y < 2099))){                     "); 
	out.println("      return false; }                             ");
	out.println("       }                                          ");
	out.println("    return true;                                  ");
	out.println("    }                                             ");		
	//
	out.println("  function validateForm(){		            ");
	out.println("  	 if ((document.myForm.date_from.value.length > 0)){ ");
	out.println("  if(!checkDate(document.myForm.date_from.value)){ ");  
	out.println("   document.myForm.date_from.focus();    ");
	out.println("     alert(\"Invalid date \");	    ");
	out.println("     return false;		       	    ");
	out.println("	}}               ");
	out.println("  	 if ((document.myForm.date_to.value.length > 0)){ ");
	out.println("  if(!checkDate(document.myForm.date_to.value)){ ");   
	out.println("   document.myForm.date_to.focus();      ");
	out.println("     alert(\"Invalid date \");	    ");
	out.println("     return false;	      		    ");
	out.println("	}}               ");
	//
	out.println("     return true;			    ");
	out.println("	}                                   ");
	out.println(" </script>				    ");
	out.println("</head><body onLoad=\"document.myForm.id.focus()\"> ");
	//
	Helper.writeTopMenu(out, url);	
	out.println("<center><h2>Rental Search</h2>");
	if(!message.equals("")){
	    out.println("<p>"+message+"</p>");
	}
	out.println("<table align=center border width=80%>");
	out.println("<tr><td bgcolor="+bgcolor+">"); // #e0e0e0 light gray
	out.println("<table width=100%>");
	out.println("<form name=myForm method=post "+
		    "onSubmit=\"return validateForm()\">");
	out.println("<tr><td><font size=+1><center>Refine Search</font>"
		    +"</td></tr>");
	out.println("<tr><td><font color=green><center>A Simplified "+
		    "Registration "+
		    "Search </font></td></tr>");
	out.println("<tr><td><font color=green><center>Intended for Simple "+
		    "Search Operations, such as </font></td></tr>");
	out.println("<tr><td><font color=green><center>Search by Permit ID "+
		    "or Dates </font></td></tr>");
	//
	// Permit
	out.println("<tr><td><b>Permit</b></td></tr>");
	out.println("<tr><td>ID");
	out.println("<input type=text name=id maxlength=8 size=8"+
		    " > ");
	out.println("&nbsp;&nbsp;Show ");
	out.println("<input type=\"radio\" name=\"active_status\" value=\"\" />All");		
	out.println("<input type=\"radio\" name=\"active_status\" value=\"y\" checked=\"checked\" />Active only");
	out.println("<input type=\"radio\" name=\"active_status\" value=\"n\" />Inactive only");		
	out.println("</td></tr>");	
	//
	out.println("<tr><td>Length:");
	out.println("<select name=permit_length>");
	out.println("<option selected>\n");
	out.println(Rental.allIntArr5);
	out.println("</select> &nbsp;");
	out.println("<input type=checkbox name=cdbg_funding "+
		    " value=checked>CDBG Funding, ");
	out.println(" &nbsp;&nbsp;N-Hood <select name=nhood>");
	out.println("<option selected>\n");
	for(int i=0;i<Rental.nhoodArr.length;i++){
	    out.println("<option>"+Rental.nhoodArr[i]);
	}
	out.println("</select></td></tr>");
	//
	out.println("<tr><td>Number of, buildings:");
	out.println("<input type=text name=structures maxlength=2 size=2"+
		    " >, units:");
	out.println("<input type=text name=units maxlength=2 size=2"+
		    " >, occupants:");
	out.println("<input type=text name=occ_load maxlength=4 size=2"+
		    " ></td></tr>");
	out.println("<tr><td>Number of, buildings more than: ");
	out.println("<input type=text name=structuresGr maxlength=2 size=2"+
		    " >, units more than:");
	out.println("<input type=text name=unitsGr maxlength=2 size=2"+
		    " >");
	out.println("</td></tr>");
	//
	// variance
	out.println("<tr><td><input type=checkbox name=hasVariance value=y>");	
	out.println("Permit has variance. The variance text contains:");
	out.println("<input type=text name=variance maxlength=40 size=20"+
		    " ></td></tr>");
	out.println("<tr><td><input type=checkbox name=affordable value=y>");	
	out.println("Affordable Housing</td></tr>");
	out.println("<tr><td><input type=checkbox name=accessory_dwelling value=y>");	
	out.println("Accessory Dwelling</td></tr>");				
	//
	// Notes
	out.println("<tr><td>Notes");
	out.println("<input type=text name=notes maxlength=40 size=20"+
		    " ></td></tr>");
	// out.println("<tr><td>&nbsp;</td></tr>");
	out.println("<tr><td align=right><input type=submit "+
		    "name=browse "+
		    "value=Browse></td></tr>");
	//
	// Location Address
	out.println("<tr><td><b>Location</b></td></tr>");
	out.println("<tr><td>Street Num");
	out.println("<input type=text name=street_num "+
		    "maxlength=8 size=8" +
		    " >&nbsp;&nbsp;Dir.");
	out.println("<select name=street_dir><option selected>\n");
	out.println(Rental.allStreetDir);
	out.println("</td></tr><tr><td>");
	out.println("Street Name");
	out.println("<input type=text name=street_name maxlength=30 "+
		    "size=15 >&nbsp;&nbsp;");
	out.println(" Type");
	out.println("<select name=street_type>");
	out.println("<option selected value=\"\">\n");
	out.println(Rental.allStreetType);
	out.println("</td></tr><tr><td>");
	out.println("Post Dir");
	out.println("<select name=post_dir>");
	out.println(Rental.allStreetDir);
	out.println("SUD");
	out.println("<select name=sud_type>");
	out.println(Rental.allSudTypes+"&nbsp;Sud Num");
	out.println("<input type=text name=sud_num maxlength=4 size=4 "+
		    ">");
	out.println("</td></tr>");
	out.println("<tr><td align=right><input type=submit "+
		    "name=browse "+
		    "value=Browse></td></tr>");
	// out.println("<tr><td><b>&nbsp;</td></tr>");
	//
	// Owner/Agent	
	out.println("<tr><td><b>Owner/Agent</b></td></tr>");
	out.println("<tr><td>Owner/Agent?");
	out.println("<select name=owner_or_agent>");
	out.println("<option selected value=\"\">All");
	out.println("<option value=owner>Owners only");
	out.println("<option value=agent>Agents only");
	out.println("</select>&nbsp;ID");
	out.println("<input type=text name=name_num maxlength=8 size=8"+
		    " >");
	out.println("</td></tr>");
	//
	out.println("<tr><td>Name");
	out.println("<input type=text name=own_name maxlength=20 size=20 />");
	out.println("</td></tr>");
	out.println("<tr><td>Address");
	out.println("<input type=text name=own_addr maxlength=20 size=20 />"+
		    "</td></tr>");
	out.println("<tr><td>City");
	out.println("<input type=text name=city maxlength=20 size=20"+
		    " >State");
	out.println("<input type=text name=state maxlength=2 size=2"+
		    " >Zip");
	out.println("<input type=text name=zip maxlength=10 size=10"+
		    " >");
	out.println("</td></tr><tr><td>");
	// phone
	out.println("Phone:");
	out.println("<input type=text name=phone maxlength=12 "+
		    "size=12></td></tr>");
	out.println("<tr><td align=right><input type=submit "+
		    "name=browse "+
		    "value=Browse></td></tr>");
	//
	// Property
	out.println("<tr><td><b>Property</b></td></tr>");
	out.println("<tr><td>Status");
	out.println("<select name=property_status>");
	out.println("<option selected>\n");
	if(pstats != null){
	    for(Item pstat:pstats){
		out.println("<option value="+pstat.getId()+">"+
			    pstat.getName()+"</option>");
	    }
	}
	out.println("</select>&nbsp;");
	//
	out.println("Type");
	out.println("<select name=prop_type>");
	out.println("<option selected>\n");
	for(int i=0;i<Rental.propTypes.length;i++){
	    out.println("<option>"+Rental.propTypes[i]);
	}
	out.println("</select>&nbsp;&nbsp;");
	out.println("<input type=checkbox name=grandfathered "+
		    " value=Y>Grand Fathered </td></tr>");
	out.println("<tr><td>Building Type:");
	out.println("<select name=building_type>");
	for(String btype:Helper.buildTypes){
	    out.println("<option>"+btype+"</option>");
	}
	out.println("</select></td></tr>");
	out.println("<tr><td><b>&nbsp;</td></tr>");
	//
	out.println("<tr><td><b>Bills </b></td></tr>");
	out.println("<tr><td>Look for Permits with bill no.:");
	out.println("<input type=text name=bid value=\""+bid+"\" size=5 "+
		    "maxlength=8>");
	out.println(", Receipt no.:");
	out.println("<input type=text name=receipt_no value=\""+
		    receipt_no+"\" size=5 "+
		    "maxlength=8></td></tr>");
	out.println("<tr><td><b>&nbsp;</td></tr>");
	//
	// Dates
	out.println("<tr><td><b>Dates</td></tr>");
	out.println("<tr><td align=center>");
	if(req_d.equals("")) req_d = "registered_date";
	String checked = req_d.equals("registered_date")?"checked=\"checked\"":"";
	out.println("<input type=radio name=req_d value=registered_date "+
		    checked+">Registered, ");
	checked = req_d.endsWith("issued")?"checked=\"checked\"":"";
	out.println("<input type=radio name=req_d value=permit_issued "+
		    checked+" value=checked>Issued, ");
	checked = req_d.startsWith("last_cycle")?"checked=\"checked\"":"";
	out.println("<input type=radio name=req_d value=last_cycle_date "+
		    checked+" value=checked>Last Cycle, ");
	checked = req_d.endsWith("expires")?"checked=\"checked\"":"";		
	out.println("<input type=radio name=req_d value=permit_expires "+
		    checked+" value=checked>Expire, ");
	checked = req_d.endsWith("billed")?"checked=\"checked\"":"";
	out.println("<input type=radio name=req_d value=date_billed "+
		    checked+" value=checked>Billed, ");
	checked = req_d.endsWith("rec")?"checked=\"checked\"":"";		
	out.println("<input type=radio name=req_d value=date_rec "+
		    checked+" value=checked>Received, ");
	checked = req_d.startsWith("pull")?"checked=\"checked\"":"";		
	out.println("<input type=radio name=req_d value=pull_date "+
		    checked+" value=checked>Pulled ");
	out.println("</td></tr>");
	out.println("<tr><td><font color=green "+
		    "size=-1><center>"+
		    "In the following date fields, if 'mm' is "+
		    "only entered 'dd' will be set to 1 and 'yy' to "+
		    "current year.<br> If yy is only entered 'dd' and "+
		    "'mm' will be set to 1</font></td></tr>");
	//
	// date 
	out.println("<tr><td><center><table border>");
	out.println("<tr><td rowspan=2><center>Date</td>"+
		    "<td align=center>From</td>");
	out.println("<td align=center>To</td></tr>");
	out.println("<tr><td>mm/dd/yyyy</td>");
	out.println("<td>mm/dd/yyyy</td></tr>");
	out.println("<tr><td align=right>Date</td>");
	out.println("<td><input type=text name=date_from value=\""+
		    date_from+"\" size=10 maxlength=10 "+
		    "class=\"date\" />/</td> ");
	//
	out.println("<td><input type=text name=date_to value=\""+
		    date_to +"\" size=10 maxlength=10 class=\"date\" "+
		    " />");
	out.println("</td></tr>");
	//
	out.println("</table>"); // end of date table
	out.println("</td></tr>"); 
	out.println("<tr><td>&nbsp;</td></tr>"); 
	//
	out.println("<tr><td>Output Type:");
	out.println("<input type=checkbox name=outputType value=csv />CSV File ");
	out.println(" (To Use with Excel, May take long time) </td></tr>");
	out.println("<tr><td>Sort by ");
	out.println("<select name=sortBy><option value=\"pd.id desc\" "+
		    "selected>New Requests First</option>");
	out.println("<option value=\"pd.id\">Old Requests First</option>");
	out.println("<option value=\"address\">Address</option>");
	out.println("<option value=\"owner\">Owner Name</option>");				
	out.println("</select></td></tr>");
	//
	out.println("<tr><td><hr></td></tr>");
	out.println("<tr><td align=right><input type=submit "+
		    "name=browse "+
		    "value=Browse></td></tr>");
	out.println("</table></td></tr>");
	//
	out.println("</td></tr></table></td></tr>");
	out.println("</form></table>");
	out.println("<br />");
	Helper.writeWebFooter(out, url);
	out.print("</body></html>");
	out.close();


    }
    /**
     * Processes the search request and arranges the output in a table.
     *
     * @param req
     * @param res
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");
	//
	boolean success = true;
	PrintWriter out = null;
	//
	String titles[] =
	    {"ID",         // 1
	     "Address",          // 
	     "Owner",            //
	     "Owner Contact",
	     "Agent",             // 5
			 
	     "Registered Date",   // 6
	     "Last Cycle Date",   // 7
	     "Expire Date",       // 8
	     "Pull Date",         // 9
	     "Building, Units",    // 10
			 
	     "Units,Bedrooms,Max Occupant Load" // 11
	    };  
	//
	// fields to be shown
	//
	boolean show[] = 
	    { true, true, true, true, true, // 5
	      true, true, true, true, true, // 10
	      true // 11
	    };

	boolean showAll = false, addrTbl=false,
	    ownTbl=false, agentTbl=false,
	    billTbl = false, receiptTbl = false,
	    varTbl=false, statTbl = false,
	    phoneTbl = false;
	String name, value;
	String action="";
	String hours_spent="",street_opt="",units="",structures="",occ_load="";
	String street_num="", id="", street_dir="", street_name="", inactive="";
	String street_type="", post_dir="",sud_type="",sud_num="",
	    valid_address="",fname_opt="",property_status="",zoning="",
	    phone="",permit_length="",cdbg_funding="";
	String address_opt="",address="",pull_reason="",prop_type="",
	    grandfathered="",owner_or_agent="",state="",nhood="";
	String agent="",name_num="",notes="",notes_opt="",own_name="",
	    name_opt="",own_addr_opt="",own_addr="",city="",zip="",
	    affordable="", building_type="";
	String registr_d="",issue_d="",cycle_d="",expire_d="",bill_d="",
	    rec_d="",pull_d="",insp_d="",req_d="", accessory_dwelling="";
	String bid="",receipt_no="", active_status="";
	String outputType="", showOut="Basics",report="";
	String separator = ",";// "</td><td>"; // for csv files
	String date_from="", date_to="";
	String sortby="", orderBy="", message="";
	String hasVariance="", variance="";
	String structuresGr="",unitsGr=""; // for > than
	Enumeration<String> values = req.getParameterNames();
	RentList rents = new RentList(debug);
	String [] vals;
	while (values.hasMoreElements()){
       	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	   
	    if (name.equals("nhood")){
		nhood = value;
		rents.setNhood(value);
	    }
	    else if (name.equals("units")){
		units = value;
		rents.setUnits(value);
	    }
	    else if (name.equals("structures")){
		structures = value;
		rents.setStructures(value);
	    }
	    else if (name.equals("unitsGr")){
		unitsGr = value;
		rents.setUnitsGr(value);
	    }
	    else if (name.equals("structuresGr")){
		structuresGr = value;
		rents.setStructuresGr(value);
	    }
	    else if (name.equals("occ_load")){
		occ_load = value;
		rents.setOcc_load(value);
	    }
	    else if (name.equals("affordable")){
		affordable = value;
		rents.setAffordable(value);
	    }
	    else if (name.equals("accessory_dwelling")){
		accessory_dwelling = value;
		rents.setAccessory_dwelling(value);
	    }						
	    else if (name.equals("street_num")) {
		street_num =value;
		rents.setStreet_num(value);
	    }
	    else if (name.equals("street_dir")) {
		street_dir =value;
		rents.setStreet_dir(value);
	    }
	    else if (name.equals("street_name")) {
		if(!value.equals("")){
		    street_name = value.toUpperCase();
		    rents.setStreet_name(value);
		}
	    }
	    else if (name.equals("street_type")) {
		street_type =value;
		rents.setStreet_type(value);				
	    }
	    else if (name.equals("post_dir")) {
		post_dir =value;
		rents.setPost_dir(value);
	    }
	    else if (name.equals("sud_num")) {
		sud_num =value;
		rents.setSud_num(value);				
	    }
	    else if (name.equals("sud_type")) {
		sud_type =value;
		rents.setSud_type(value);
	    }
	    else if (name.equals("date_from")) {
		date_from=value;
		rents.setDate_from(value);
	    }
	    else if (name.equals("date_to")) {
		date_to=value;
		rents.setDate_to(value);
	    }
	    else if (name.equals("inactive")) {
		rents.setInactive(value);
		inactive=value;
	    }
	    else if (name.equals("active_status")) {
		active_status = value;
		rents.setActiveStatus(value);
	    }			
	    else if (name.equals("hasVariance")) {
		rents.setHasVariance();
		hasVariance = value;
	    }
	    else if (name.equals("outputType")) {
		outputType = value;
	    }
	    else if (name.equals("building_type")) {
		rents.setBuilding_type(value);
		building_type =value;
	    }			
	    else if (name.equals("variance")) {
		rents.setVariance(value);
		variance = value;
	    }
	    else if (name.equals("cdbg_funding")) {
		rents.setCdbg_funding(value);
		cdbg_funding=value;
	    }
	    else if (name.equals("req_d")) {
		req_d=value;
		rents.setWhichDate(value);
	    }
	    else if (name.equals("property_status")) {
		property_status =value;
		rents.setProperty_status(value);
	    }
	    else if (name.equals("showOut")) {
		showOut =value;
	    }
	    else if (name.equals("sortBy")) {
		sortby =value;
		rents.setSortBy(sortby);
	    }
	    else if (name.equals("prop_type")) {
		prop_type = value;
		rents.setProp_type(value);				
	    }
	    else if (name.equals("id")) {
		id = value;
		rents.setId(value);
	    }
	    else if (name.equals("bid")) {
		bid = value;
		rents.setBid(value);
	    }
	    else if (name.equals("receipt_no")) {
		receipt_no = value;
		rents.setReceipt_no(value);
	    }
	    else if (name.equals("agent")) {
		agent = value;
		rents.setAgentId(value);
	    }
	    else if (name.equals("owner_or_agent")) {
		owner_or_agent = value;
		rents.setOwner_or_agent(value);
	    }
	    else if (name.equals("grandfathered")) {
		grandfathered = value;
		rents.setGrandfathered(value);
	    }
	    else if (name.equals("notes_opt")) {
		notes_opt = value;
	    }
	    else if (name.equals("notes")) {
		if(!value.equals("")){
		    notes = value;
		    rents.setNotes(value);
		}
	    }
	    else if (name.equals("cc_all")){
		showAll = true;
	    }
	    else if (name.equals("date_from")){
		date_from = value;
		rents.setDate_from(value);
	    }
	    else if (name.equals("date_to")){
		date_to = value;
		rents.setDate_to(value);
	    }
	    else if (name.equals("orderBy")){
		orderBy = value;
		rents.setOrderBy(value);
	    }
	    else if (name.equals("name_num")) {
		name_num = value;
		rents.setName_num(value);
	    }
	    else if (name.equals("own_name")) {
		own_name = value;
		rents.setOwn_name(value);
	    }
	    else if (name.equals("own_addr")) {
		own_addr = value;
		rents.setOwn_addr(value);
	    }
	    else if (name.equals("city")) {
		city = value;
		rents.setCity(value);
	    }
	    else if (name.equals("state")) {
		rents.setState(value);
		state = value;
	    }
	    else if (name.equals("zip")) {
		rents.setZip(value);
		zip = value;
	    }
	    else if (name.equals("phone")) {
		rents.setPhone(value);
		phone = value;
	    }
	}
	String str="";
	str = rents.lookFor();
	if(!str.equals("") || rents.size() == 0){
	    if(rents.size() == 0){
		message = " No match found ";
		if(!id.equals("")) message += " ID = "+id;
	    }
	    else{
		message = str;
	    }
	    message = java.net.URLEncoder.encode(message,"UTF-8");
	    str = url+"Browse?message="+message;
	    res.sendRedirect(str);
	    return; 								
	}
	//
	// Sorting header
	//
	if(outputType.equals("")){
	    out = res.getWriter();	
	    out.println("<html><head><title>Rental Browser</title>");
	    out.println("<script type=\"text/javascript\">");
	    out.println("  function sendSortSub(order_by) { ");
	    out.println("  document.myForm.orderBy.value=order_by; ");
	    out.println("  document.myForm.submit();         ");
	    out.println(" }                             ");   
	    //
	    out.println("</script>");  
	    out.println("</head><body>");
	    out.println("<form name=\"myForm\" method=\"post\" action=\""+url+"Browse\">");
	    out.println("<input type=\"hidden\" name=\"orderBy\" value=\""+
			"\" />");
	    if(!prop_type.equals("")){
		out.println("<input type=\"hidden\" name=\"prop_type\" value=\""+
			    prop_type+"\">");
	    }
	    if(!active_status.equals("")){
		out.println("<input type=\"hidden\" name=\"active_status\" value=\""+
			    active_status+"\">");
	    }						
	    if(!nhood.equals("")){
		out.println("<input type=\"hidden\" name=\"nhood\" value=\""+
			    nhood+"\">");
	    }
	    if(!phone.equals("")){
		out.println("<input type=\"hidden\" name=\"phone\" value=\""+
			    phone+"\">");
	    }
	    if(!city.equals("")){
		out.println("<input type=\"hidden\" name=\"city\" value=\""+
			    city+"\">");
	    }
	    if(!units.equals("")){
		out.println("<input type=\"hidden\" name=\"units\" value=\""+
			    units+"\">");
	    }
	    if(!structures.equals("")){
		out.println("<input type=\"hidden\" name=\"structures\" value=\""+
			    structures+"\">");
	    }
	    if(!occ_load.equals("")){
		out.println("<input type=\"hidden\" name=\"occ_load\" value=\""+
			    occ_load+"\">");
	    }
	    if(!state.equals("")){
		out.println("<input type=\"hidden\" name=\"state\" value=\""+
			    state+"\">");
	    }
	    if(!zip.equals("")){
		out.println("<input type=\"hidden\" name=\"zip\" value=\""+
			    zip+"\">");
	    }
	    if(!phone.equals("")){
		out.println("<input type=\"hidden\" name=\"phone\" value=\""+
			    phone+"\">");
	    }
	    if(!city.equals("")){
		out.println("<input type=\"hidden\" name=\"city\" value=\""+
			    city+"\">");
	    }
	    if(!state.equals("")){
		out.println("<input type=\"hidden\" name=\"state\" value=\""+
			    state+"\">");
	    }
	    if(!zip.equals("")){
		out.println("<input type=\"hidden\" name=\"zip\" value=\""+
			    zip+"\">");
	    }
	    if(!name_num.equals("")){
		out.println("<input type=\"hidden\" name=\"name_num\" value=\""+
			    name_num+"\">");
	    }
	    if(!owner_or_agent.equals("")){
		out.println("<input type=\"hidden\" name=\"owner_or_agent\" value=\""+
			    owner_or_agent+"\">");
	    }
	    // since we can't have more than one of  these
	    if(!req_d.equals("")){
		out.println("<input type=\"hidden\" name=\"req_d\" value=\""+
			    req_d+"\">");
	    }

	    if(!showOut.equals("")){
		out.println("<input type=\"hidden\" name=\"showOut\" value=\""+
			    showOut+"\">");
	    }
	    if(!notes.equals("")){
		out.println("<input type=\"hidden\" name=\"notes\" value=\""+
			    notes+"\">");
	    }
	    if(!notes_opt.equals("")){
		out.println("<input type=\"hidden\" name=\"notes_opt\" value=\""+
			    notes_opt+"\">");
	    }
	    if(!property_status.equals("")){
		out.println("<input type=\"hidden\" name=\"property_status\" value=\""+
			    property_status+"\">");
	    }
	    if(!grandfathered.equals("")){
		out.println("<input type=\"hidden\" name=\"grandfathered\" value=\""+
			    grandfathered+"\">");
	    }
	    if(!accessory_dwelling.equals("")){
		out.println("<input type=\"hidden\" name=\"accessory_dwelling\" value=\"y\">");
	    }						
	    if(!cdbg_funding.equals("")){
		out.println("<input type=\"hidden\" name=\"cdbg_funding\" value=\""+
			    cdbg_funding+"\">");
	    }
	    if(!permit_length.equals("")){
		out.println("<input type=\"hidden\" name=\"permit_length\" value=\""+
			    permit_length+"\">");
	    }
	    if(!date_from.equals("")){
		out.println("<input type=\"hidden\" name=\"date_from\" value=\""+
			    date_from+"\">");
	    }
	    if(!date_to.equals("")){
		out.println("<input type=\"hidden\" name=\"date_to\" value=\""+
			    date_to+"\">");
	    }
	    if(!street_num.equals("")){
		out.println("<input type=\"hidden\" name=\"street_num\" value=\""+
			    street_num+"\">");
	    }
	    if(!street_name.equals("")){
		out.println("<input type=\"hidden\" name=\"street_name\" value=\""+
			    street_name+"\">");
	    }
	    if(!street_type.equals("")){
		out.println("<input type=\"hidden\" name=\"street_type\" value=\""+
			    street_type+"\">");
	    }
	    if(!street_dir.equals("")){
		out.println("<input type=\"hidden\" name=\"street_dir\" value=\""+
			    street_dir+"\">");
	    }
	    if(!sud_num.equals("")){
		out.println("<input type=\"hidden\" name=\"sud_num\" value=\""+
			    sud_num+"\">");
	    }
	    if(!sud_type.equals("")){
		out.println("<input type=\"hidden\" name=\"sud_type\" value=\""+
			    sud_type+"\">");
	    }
	    if(!post_dir.equals("")){
		out.println("<input type=\"hidden\" name=\"post_dir\" value=\""+
			    post_dir+"\">");
	    }
	    if(!name_num.equals("")){
		out.println("<input type=\"hidden\" name=\"name_num\" value=\""+
			    name_num+"\">");
	    }
	    if(!owner_or_agent.equals("")){
		out.println("<input type=\"hidden\" name=\"owner_or_agent\" value=\""+
			    owner_or_agent+"\">");
	    }
	    if(!own_name.equals("")){
		out.println("<input type=\"hidden\" name=\"own_name\" value=\""+
			    own_name+"\">");
	    }
	    if(!name_opt.equals("")){
		out.println("<input type=\"hidden\" name=\"name_opt\" value=\""+
			    name_opt+"\">");
	    }
	    if(!own_addr.equals("")){
		out.println("<input type=\"hidden\" name=\"own_addr\" value=\""+
			    own_addr+"\">");
	    }
	    if(!own_addr_opt.equals("")){
		out.println("<input type=\"hidden\" name=\"own_addr_opt\" value=\""+
			    own_addr_opt+"\">");
	    }
	    out.println("</form>");
	    out.println("<br><b>Sort by:</b>");
	    if(orderBy.equals("id") || orderBy.equals("")){
		out.println(" ID,");
	    }
	    else{
		// default
		out.println("<a href=javascript:sendSortSub(\"\")>ID</a>,"); 
	    }
	    if(show[2] || showAll){
		if(orderBy.equals("agent")){
		    out.println(" Agent Name,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"agent\")>"+
				"Agent Name</a>,");
		}
	    }
	    if(show[3] || showAll){
		if(orderBy.equals("addr")){
		    out.println(" Address,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"addr\")>"+
				"Address</a>,");
		}
	    }
	    if(show[5] || showAll){
		if(orderBy.equals("owner")){
		    out.println(" Owner,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"owner\")>"+
				"Owner</a>,");
		}
	    }
	    if(show[6] || showAll){
		if(orderBy.equals("reg_d")){
		    out.println(" Registered Date,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"reg_d\")>"+
				"Registered Date</a>,");
		}
	    }
	    if(show[7] || showAll){
		if(orderBy.equals("cycle_d")){
		    out.println(" Last Cycle Date,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"cycle_d\")>"+
				"Last Cycle Date</a>,");
		}
	    }
	    if(show[9] || showAll){
		if(orderBy.equals("expire_d")){
		    out.println(" Expire Date,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"expire_d\")>"+
				"Expire Date</a>,");
		}
	    }
	    if(show[10] || showAll){
		if(orderBy.equals("pull_d")){
		    out.println(" Pull Date,");
		}	
		else{
		    out.println("<a href=javascript:sendSortSub(\"pull_d\")>"+
				"Pull Date</a>,");
		}
	    }
	}
	//
	if(success){
	    if(outputType.equals("")){
		if(rents.size() == 1){
		    Rent rent = rents.get(0);
		    str = url+"Rental?id="+rent.getId()+"&action=zoom";
		    res.sendRedirect(str);
		    return; 
		}
	    }
	    if(rents.size() > 0){
		String that = "";
		int row = 0;
		HashSet<String> set = new HashSet<String>(1000);
		if(outputType.equals("")){
		    out.println("<h4>Total Matching Records "+ rents.size() +" </h4>");					
		    out.println("<table border>");
		    for(Rent rent:rents){
			if(row%20 == 0){ 
			    out.println("<tr>");
			    for (int c = 0; c < titles.length; c++){ 
				if(show[c] || showAll)
				    out.println("<th>"+titles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=#e0e0e0>");  
			else 
			    out.println("<tr>");
			str = rent.getId();
			if(set.contains(str)) continue;
			set.add(str);
			row++;
			that = "<a href="+url+
			    "Rental?id="+str+"&action=zoom>"+
			    str+ "</a>";
			out.println("<tr>");
			out.println("<td>"+that+"</td>");
			//
			// Address
			List<Address> addrs = rent.getAddresses();
			that = "";
			if(addrs != null && addrs.size() > 0){
			    for(Address addr: addrs){
				if(that.equals("")) that += "<br />";
				that += addr.getAddress();
			    }
			}
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			//
			// Owner 
			that = "";
			String contacts = "";
			List<Owner> owners = rent.getOwners();
			if(owners != null && owners.size() > 0){
			    for(Owner own: owners){
				if(!that.equals("")) that += "<br />";
				str = own.getId();
				that += "<a href="+url+
				    "OwnerServ?"+
				    "&name_num="+str+
				    "&action=zoom>"+
				    own.getFullName()+"</a>";
				that += own.getAddress()+"<br />";
				that += own.getCityStateZip();
				str = own.getPhones();
				if(!str.equals("")){
				    if(!contacts.equals(""))
					contacts += "<br />"; 
				    contacts += str;
				}
			    }
			}
			if(that.equals("")) that = "&nbsp;";
			if(contacts.equals("")) contacts = "&nbsp;";
			out.println("<td>"+that+"</td>");
			out.println("<td>"+contacts+"</td>");
			//
			// Agent
			//
			Owner agency = rent.getAgent();
			that = "";
			if(agency != null){
			    str = agency.getId();
			    that += "<a href="+url+
				"OwnerServ?"+
				"&name_num="+str+
				"&action=zoom>"+
				agency.getFullName()+"</a>";
			    that += agency.getAddress()+"<br />";
			    that += agency.getCityStateZip()+"<br />";
			    that += agency.getPhones();
			}
			if(that.equals("")) that += "No Agent";
			out.println("<td>"+that+"</td>");
			//
			// Registered_date
			that = rent.getRegistered_date();
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			//	
			// Registered_date
			that = rent.getLast_cycle_date();
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			//	
			// Expire date
			that = rent.getPermit_expires();
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			//					
			// Pull Date
			that = rent.getPull_date();
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			str = rent.getUnits();
			that = rent.getStructures();
			if(!that.equals("") && !str.equals("")){
			    that += "/"+str;
			}
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			that = rent.getOcc_load();
			if(that.equals("")) that = "&nbsp;";
			out.println("<td>"+that+"</td>");
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}
		else{ // csv format
		    String startPage = "", endPage= "", startLine = "",
			endLine = "\n", quote = "\"", all=""; 
		    if(separator.equals("</td><td>")){
			startPage = "<html><head></head><body><table>\n";
			endPage = "</table></body></html>\n";
			startLine = "<tr><td>";
			endLine = "</td></tr>\n";
			quote = "";
			all = startPage;
			all += startLine;
		    }
		    for (int c = 0; c < titles.length; c++){
			if( c > 0) all += separator; // separator 
			all += quote + titles[c] + quote;
		    }	   		
		    all += endLine;
		    int jj = 0;
		    for(Rent rent:rents){
			String line = "", line2="";
			// jj++;
						
			str = rent.getId();
			if(set.contains(str)) continue;
			set.add(str);
			line2 += startLine;
			line2 += quote + str + quote;  // 1
			line2 += separator;
			//
			// get owner info
			//
			String ownContacts = "", ownNames="";
			List<Owner> owners = rent.getOwners();
			if(owners != null && owners.size() > 0){
			    for(Owner own: owners){
				if(!ownNames.equals("")) ownNames += ", ";
				ownNames += own.getFullName()+", ";
				ownNames += own.getAddress()+", ";
				ownNames += own.getCityStateZip();
				str = own.getWorkPhone();
				if(!str.equals("")){
				    if(!ownContacts.equals(""))
					ownContacts += ", "; 
				    ownContacts += str;
				}
			    }
			}
			Owner agency = rent.getAgent();
			String agentInfo = "";
			if(agency != null){
			    str = agency.getFullName();
			    if(!str.equals("")) agentInfo = str;
			    str = agency.getAddress();
			    if(!str.equals("")){
				agentInfo += ", "+str;
			    }
			    str = agency.getWorkPhone();
			    if(!str.equals("")){
				agentInfo += ", "+str;
			    }	
			}					
			if(agentInfo.equals("")) agentInfo += "No Agent";
			//						
			// Address
			List<Address> addrs = rent.getAddresses();
			that = "";
			if(addrs != null && addrs.size() > 0){
			    for(Address addr: addrs){
				that = addr.getAddress();
				line = line2;
				line += quote+that+quote; // 2
				line += separator;
				//
				// Owner 
				that = "";
				line += quote + ownNames + quote; // 3
				line += separator;
				line += quote + ownContacts + quote; // 4
				line += separator;						
				//
				// Agent
				//
				line += quote + agentInfo + quote; // 5
				line += separator;	
				//
				// Registered_date
				that = rent.getRegistered_date();
				line += quote + that + quote; // 6
				line += separator;	
				//	
				// Registered_date
				that = rent.getLast_cycle_date();
				line += quote + that + quote; // 7
				line += separator;	
				//	
				// Expire date
				that = rent.getPermit_expires();
				line += quote + that + quote; // 8
				line += separator;	
				//					
				// Pull Date
				that = rent.getPull_date();
				line += quote + that + quote; // 9
				line += separator;
				//
				str = rent.getUnits();
				that = rent.getStructures();
				if(!that.equals("") && !str.equals("")){
				    that += ", "+str;
				}
				line += quote + that + quote; // 10
				line += separator;							
				that = rent.getOcc_load();
				if(that.contains("/")){
				    that = that.replaceAll("/",",");
				}
				line += quote + that + quote; // 11
				line +=  endLine;
				all += line;
			    }
			}
		    }
		    if(outputType.equals("")){
			all += endPage;
		    }
		    //
		    // print to file
		    //
		    res.setHeader("Expires", "0");
		    res.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		    res.setHeader("Pragma", "public");
		    res.setHeader("Content-Disposition","inline; filename=\"rental_data.csv\"");
		    byte [] buf = all.getBytes();
		    // setting the content type
		    res.setContentType("application/csv");
		    // the contentlength is needed for MSIE!!!
		    res.setContentLength(buf.length);
		    // write ByteArrayOutputStream to the ServletOutputStream
		    ServletOutputStream out2 = res.getOutputStream();
		    out2.write(buf);
		    out2.close();
		}
	    }
	}
	if(!success){
	    if(out == null)
		out = res.getWriter();			  
	    out.println("<html><head><title>Rental Browser"+
			"</title></head><body>");
	    out.println("<h2>"+message+"</h2>");
	    out.println("</body></html>");
	}
	else if(outputType.equals("")){
	    out.println("<br><center>");
	    out.println("</body>");
	    out.println("</html>");
	    out.flush();
	    out.close();			
	}

    }

}






















































