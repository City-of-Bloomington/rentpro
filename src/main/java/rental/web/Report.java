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

@WebServlet(urlPatterns = {"/Report"})
public class Report extends TopServlet {

    final static long serialVersionUID = 870L;
    static Logger logger = LogManager.getLogger(Report.class);
    //
    /**
     * Generates the form for the search engine.
     * @param req
     * @param res
     */
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException {
	doPost(req, res);
    }
    /**
     * Processes the search request and arranges the output in a table.
     * 
     * Some requests of cetain reports are produced here as well.
     * @param req
     * @param res
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	res.setContentType("text/html");
	//
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean success = true;
	//
	String titles[] =
	    {"Permit ID",         // 1
	     "Owner ID",        // 2
	     "Owners",            // 
	     "Agent ID",        // 
	     "Agent",             // 5

	     "Address",           // 6
	     "Registered Date",   // 7
	     "Last Cycle Date",   // 8
	     "Permit Issue Date", // 9  
	     "Expire Date",       // 10

	     "Length (years)",    // 11
	     "Pull Date",         // 12
	     "Pull Reason",       // 13
	     "Date Billed",       // 14
	     "Date Received",     // 15

	     "Property Status",   // 
	     "Units/ Buildings",   // 
	     "Bedrooms/ Baths",   
	     "Occupant Load",     // 
	     //   "Zoning",       // 20

	     "CDBG Funding",      //  
	     "Notes",             //  
	     "N-Hood",
	     "Type"
	    };  
	//
	// fields to be shown
	//
	boolean show[] = 
	    { true,false,true,false,true, // 5
	      true,false,false,false,false, // 10
	      true,true,true,false,false, // 15
	      false,true,false,true,false,  // 20
	      false,true,false
	    };

	PrintWriter out = res.getWriter();			  
	boolean showAll = false;

	String name, value,username="";
	String action="", message="";
	String hours_spent="",street_opt="",units="",structures="",
	    occ_load="", inactive="";
	String street_num="", id="", street_dir="", street_name="";
	String street_type="", post_dir="",sud_type="",sud_num="",
	    valid_address="",fname_opt="",property_status="",zoning="",
	    phone="",permit_length="",cdbg_funding="",nhood="";
	String address_opt="",address="",pull_reason="",prop_type="",
	    grandfathered="",owner_or_agent="",state="",order_by="";
	String agent="",name_num="",notes="",notes_opt="",own_name="",
	    name_opt="",own_addr_opt="",own_addr="",city="",zip="",
	    u_multi="",u_single="", inspection_type="", affordable="";
	String violations="";
	String registr_d="",issue_d="",cycle_d="",expire_d="",bill_d="",
	    rec_d="",pull_d="",insp_d="",req_d="";
	String outputType="summary", showOut="Basics",report="";
	String date_from="",date_to="", who="", units_from="";
	String stDir = "", stName="",stNumLow="",stNumHigh="",stType="";
	String building_type = "";
	boolean ownTbl=false, agntTbl=false,
	    addrTbl=false, 
	    supTbl=false, 
	    inspTbl=false, phoneTbl = false;
	String type="rental";  //"address","inspection" for browse or report
	String sortby="";
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	Hashtable<String, String> inspMap = new Hashtable<String, String>();
		
	while (values.hasMoreElements()){

       	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	   
	    if (name.equals("date_from")) {
		date_from=value;
	    }
	    else if (name.equals("date_to")) {
		date_to=value;
	    }
	    else if (name.equals("who")){ // from reports
		who = value;
	    }
	    else if (name.equals("inspection_type")){
		inspection_type = value;
	    }
	    else if (name.equals("building_type")){
		building_type = value;
	    }						
	    else if (name.equals("req_d")) {
		req_d=value;
		if (req_d.equals("registr_d")) {
		    registr_d=value;
		}
		else if (req_d.equals("issue_d")) {
		    issue_d=value;
		}
		else if (req_d.equals("cycle_d")) {
		    cycle_d=value;
		}
		else if (req_d.equals("expire_d")) {
		    expire_d=value;
		}
		else if (req_d.equals("bill_d")) {
		    bill_d=value;
		}
		else if (req_d.equals("pull_d")) {
		    pull_d=value;
		}
		else if (req_d.equals("insp_d")) {
		    insp_d=value;
		}	    
	    }
	    else if (name.equals("type")) {
		type = value;
	    }
	    else if (name.equals("order_by")) {
		order_by =value;
	    }
	    else if (name.equals("report")) {
		report =value;
	    }
	    else if (name.equals("violations")) {
		violations = value;
	    }						
	    else if (name.equals("pull_reason")) {
		pull_reason =value;
	    }
	    else if (name.equals("showOut")) {
		showOut =value;
	    }
	    else if (name.equals("sortby")) {
		sortby = value.trim();
	    }
	    else if (name.equals("street_opt")) {
		street_opt = value;
	    }
	    else if (name.equals("name_opt")) {
		name_opt = value;
	    }
	    else if (name.equals("prop_type")) {
		prop_type = value;
	    }
	    else if (name.equals("owner_or_agent")) { // "", owner, agent
		owner_or_agent = value;
	    }
	    else if (name.equals("agent")) {
		agent = value;
	    }

	}
	List<Item> inspectTypes = null;
	if(true){
	    if(inspectTypes == null){
		InspectTypeList itl = new InspectTypeList(debug);
		String back = itl.find();
		if(back.equals("")){
		    inspectTypes = itl.getInspectTypes();
		    if(inspectTypes != null){
			for(Item item: inspectTypes){
			    inspMap.put(item.getId(),item.getName());
			}
		    }
		}
	    }
	}
	//
	// we need these for default values of dates
	//
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	//
	// to find overdue bill date
	//
	current_cal.set(Calendar.MONTH, mm-2); // one month before

	int dd2 = current_cal.get(Calendar.DATE);
	int yyyy2 = current_cal.get(Calendar.YEAR);
	int mm2 = current_cal.get(Calendar.MONTH)+1;

	Vector<String> wherecases = new Vector<String>();
	//
	// if the submit came from ReportMenu class
	//
	if(!report.equals("")){
	    if(report.equals("addr")){
		type = "address";
	    }
	    else if(report.equals("inaddr")){
		type = "address";
		// list invalid addresses				
		wherecases.addElement("(ad.invalid_addr is not null or ad.location_id is null)");
	    }
	    else if(report.equals("pull")){
		// pull dates
		show[0] = true;
		show[5] = true;  // address
		show[11] = true;  // pull date
		show[12] = true;  // pull reason
		if(order_by.equals(""))order_by="pull_d";
		if(!date_from.equals(""))pull_d="y";
		if(!date_to.equals(""))pull_d="y";
								
	    }
	    else if(report.equals("pullReas")){
		// pull dates and reason
		show[0] = true;
		show[5] = true;  // address
		show[11] = true;  // pull date
		show[12] = true;  // pull reason								
		if(!date_from.equals("")) pull_d="y";
		if(!date_to.equals("")) pull_d="y";
		if(order_by.equals(""))order_by="pull_r";
		if(!pull_reason.equals("")){
		    wherecases.addElement("pd.pull_reason ='"+pull_reason+"'");
		}								
	    }
	    else if(report.equals("noPullDate")){
		// pull dates and reason
		if(order_by.equals(""))order_by="pull_r";
		wherecases.addElement("pd.pull_date is null");
	    }
	    else if(report.equals("withUnits")){
		//
		type= "withUnits";
	    }
	    else if(report.equals("agents")){
		// list of agents
		type = "owner";    // for agents and owners
		wherecases.addElement("pd.agent > 0 ");
	    }
	    else if(report.equals("owners")){
		// list of owners
		type = "owner";
	    }
	    else if(report.startsWith("prop")){
		// list of properties by owner/agent
		type = "prop";
	    }
	    else if(report.equals("cycle")){
		// cycle date
		// what is in mind the issue date for all new permits
		// since issue date is changed eveytime the permit
		// is renewed, we can use the inspection date to 
		// exclude the ones that are inspected previous to this
		// date period !!
		//
		issue_d="y";
		// 
	    }
	    else if(report.startsWith("inspect")){
		// list of inspections
		type = "inspect";
		if(!inspection_type.equals("")){
		    wherecases.addElement("insp.inspection_type='"+inspection_type+"'");
		}
	    }
	    else if(report.equals("inspType")){
		// list of inspections and their types
		type = "inspect";
	    }
	    else if(report.equals("ownNprop")){ 
		// list of properties and their owners
		type = "ownNprop";
	    }
	    else if(report.equals("noOwnAgent")){ 
		// list of properties with no owner nor agent
		type = "noOwnAgent";
	    }
	    else if(report.startsWith("email")){ 
		// list of properties with no owner nor agent
		type = "email";
	    }
	    else if(report.startsWith("oldUnit")){ 
		// list of properties with old unit format
		type = "oldUnit";
		// since last 4 years
		date_from = Helper.getDateYearsFromNow(-4);
		expire_d="y";
		date_to = ""; // no limit
		// wherecases.addElement("pd.permit_expires >= to_date('"+date+"','mm/dd/yyyy')");
								
	    }						
	    else if(report.startsWith("variance")){ 
		if(!date_from.equals("")){
		    wherecases.addElement("pd.permit_expires >= to_date('"+date_from+"','mm/dd/yyyy')");
		}
		if(!date_to.equals("")){
		    wherecases.addElement("pd.permit_expires <= to_date('"+date_to+"','mm/dd/yyyy')");
		}
		type="variance";
	    }
	    else if(report.equals("overdue")){
		//
		show[0] = true;
		show[2] = true;  // owner
		show[13] = true; // billed
		show[14] = true;  // received 
		wherecases.addElement("pd.date_billed < "+
				      " to_date('"+mm2+
				      "/"+dd2+"/"+yyyy2+"','mm/dd/yyyy')");
		wherecases.addElement("pd.date_rec is null");
		bill_d="y";
	    }
	}
	if(showOut.equals("Basics")){
	    //
	    // List of the items in Basics show
	    // use the default for now
	    //
	}
	else{
	    showAll = true;
	    outputType = "summary";
	}
	//	
	if(true){
	    //
	    if(!date_to.equals("")){
		if(!registr_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.registered_date");
		else if(!issue_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.permit_issued");
		else if(!expire_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.permit_expires");
		else if(!bill_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.date_billed");
		else if(!rec_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.date_rec");
		else if(!cycle_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.last_cycle_date");
		else if(!pull_d.equals(""))
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= pd.pull_date");
		else if(!insp_d.equals("")){
		    wherecases.addElement("to_date('"+date_to+
					  "','mm/dd/yyyy') >= insp.inspection_date");
		    inspTbl = true;
		}
	    }
	    if(!date_from.equals("")){
		if(!registr_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.registered_date");
		else if(!issue_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.permit_issued");
		else if(!expire_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.permit_expires");
		else if(!bill_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.date_billed");
		else if(!rec_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.date_rec");
		else if(!cycle_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.last_cycle_date");
		else if(!pull_d.equals(""))
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= pd.pull_date");
		else if(!insp_d.equals("")){
		    wherecases.addElement("to_date('"+date_from+
					  "','mm/dd/yyyy') <= insp.inspection_date");
		    inspTbl = true;	 
		}
	    }
	    // 
	    // to list all the new permits for this year
	    // This takes very long time to finish
	    if(report.equals("cycle") && !date_from.equals("")){
		wherecases.addElement("pd.id not in ( select i.id from "+
				      " inspections i where "+
				      " to_date('"+date_from+
				      "','mm/dd/yyyy') > "+
				      "i.inspection_date)"); 
	    }
	    if(!stName.equals("")){
		wherecases.addElement("ad.street_name like '"+stName+"'");
	    }
	    if(!stDir.equals("")){
		wherecases.addElement("ad.street_dir = '"+stDir+"'");
	    }
	    if(!stType.equals("")){
		wherecases.addElement("ad.street_type = '"+stType+"'");
	    }
	    if(!stNumLow.equals("")){
		wherecases.addElement("ad.street_num >= '"+stNumLow+"'");
	    }
	    if(!stNumHigh.equals("")){
		wherecases.addElement("ad.street_num <= '"+stNumHigh+"'");
	    }						
	}
	out.println("<html><head><title>Rental Reports</title>");
	Helper.writeWebCss(out, url);
	out.println("<script>");
	out.println("  function sendSortSub(order_by) { ");
	out.println("  document.myForm.order_by.value=order_by; ");
	out.println("  document.myForm.submit();         ");
	out.println(" }                             ");
	out.println("</script>");				
	out.println("</head><body>");
	Helper.writeTopMenu(out, url);	
	out.println("<center>");
	if(!report.equals("")){
	    if(report.equals("addr")){
		out.println("<h2>Addresses</h2>");
	    }
	    else if(report.equals("inaddr")){
		out.println("<h2>Invalid Addresses</h2>");
	    }
	    else if(report.equals("withUnits")){
		out.println("<h2>Permits with Units</h2>");
	    }			
	    else if(report.equals("pull")){
		out.println("<h2>Pulled List</h2>");
	    }
	    else if(report.equals("pullReas")){
		out.println("<h2>Pulled Dates and Types</h2>");
	    }
	    else if(report.equals("noPullDate")){
		out.println("<h2>Permits without Pull Date</h2>");
	    }			
	    else if(report.equals("agents")){
		out.println("<h2>Agents</h2>");
	    }
	    else if(report.equals("owners")){
		out.println("<h2>Owners</h2>");
	    }
	    else if(report.equals("cycle")){
		out.println("<h2>New Cycle</h2>");
				
	    }
	    else if(report.equals("expire")){
		out.println("<h2>Expiring Permits</h2>");
				
	    }
	    else if(report.startsWith("oldUnit")){
		out.println("<h2>Permits with old structure/unit format</h2>");
				
	    }									
	    else if(report.equals("inspectViolation")){
		out.println("<h2>Inspections with Violations</h2>");

	    }
	    else if(report.equals("inspect")){
		//
		// list of inspections
		out.println("<h2>Inspections Report ");
		String str = "";
		if(!inspection_type.equals("")){
		    str = inspMap.get(inspection_type);
		    if(str != null)
			out.println(" of Type "+str);
		}
		out.println("</h2>");
	    }
	    else if(report.equals("inspType")){
		out.println("<h2>Inspection Dates & Types</h2>");
	    }
	    else if(report.equals("overdue")){
		out.println("<h2>Overdue Bills</h2>");
	    }
	    else if(report.equals("propAgent")){
		out.println("<h2>Properties Managed by Agent</h2>");
	    }
	    else if(report.equals("propOwn")){
		out.println("<h2>Properties Managed by Owner</h2>");
	    }
	    else if(report.equals("ownNprop")){
		out.println("<h2>Owners and Their Properties</h2>");
	    }
	    else if(report.startsWith("noOwn")){
		out.println("<h2>Permits With No Owners Nor Agents</h2>");
	    }
	    else if(report.startsWith("variance")){
		out.println("<h2>Permits with Variances</h2>");
	    }
	    else if(report.startsWith("emailList")){
		if(who.equals("owner"))
		    out.println("<h2>List of Owners' Emails</h2>");
		else
		    out.println("<h2>List of Agents' Emails</h2>");
	    }
	    out.println("</center>");
	}
	//
	String qq = "", qs="";
	try{
	    con = Helper.getConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		success = false;
		message += " could not connect to database";
		logger.error(message);
		out.println("<h2>Error: Could not connect to Database</h2>");
		out.println("<h3>"+message+"</h3>");
		out.println("<h2>Check with ITS</h2>");
		out.println("</body></html>");
		return;
	    }
	    //
	    if(true){
		//
		if(report.startsWith("pullR")){
		    wherecases.add(" pd.inactive is null ");
		}
		else if(report.equals("pull")){
		    // 
		    if(order_by.equals("") || order_by.equals("pull_d")){
			qs = " order by pd.pull_date, ";
			qs += " pr.pull_text,ad.street_name,ad.street_dir,"+
			    "lpad(ad.street_num,6,'0') "; // ASC
			if(!sortby.isEmpty()){
			    qs += ", "+sortby;
			}
		    }
		    else if(order_by.equals("address")){
			qs = " order by ad.street_name,ad.street_dir,"+
			    "lpad(ad.street_num,6,'0')";
		    }
		    else if(order_by.equals("id")){
			qs = " order by pd.id ";
		    }
		    else if(order_by.equals("pull_r")){
			qs = " order by pr.pull_text,ad.street_name,ad."+
			    "street_dir,lpad(ad.street_num,6,'0')"; // ASC
			if(!sortby.isEmpty()){
			    qs += ", "+sortby;
			}
		    }
		}
		else if(order_by.equals("pull_r")){
		    qs = " order by pr.pull_text,ad.street_name,ad."+
			"street_dir,lpad(ad.street_num,6,'0') "; // ASC
		    if(!sortby.isEmpty()){
			qs += ", "+sortby;
		    }
		}
		else if(order_by.equals("pull_d")){
		    qs = " order by pd.pull_date, ";
		    qs += " pr.pull_text,ad.street_name,ad."+
			"street_dir,lpad(ad.street_num,6,'0') "; // ASC
		    if(!sortby.isEmpty()){
			qs += ", "+sortby;
		    }
		}
		else if(order_by.equals("owner")){
		    qs = " order by od.name ";
		}
		else if(order_by.equals("id")){
		    qs = " order by pd.id ";
		}
		else if(order_by.equals("agent")){
		    qs = " order by od2.name ";
		}
		else if(order_by.equals("address")){
		    qs = " order by ad.street_name,ad.street_dir,"+
			"lpad(ad.street_num,6,'0') ";
		}
		else if(order_by.equals("reg_d")){
		    qs = " order by pd.registered_date ";
		}
		else if(order_by.equals("cycle_d")){
		    qs = " order by pd.last_cycle_date ";
		}
		else if(order_by.equals("issue_d")){
		    qs = " order by pd.permit_issued ";
		}
		else if(order_by.equals("expire_d")){
		    qs = " order by pd.permit_expires ";
		}
		else if(order_by.equals("bill_d")){
		    qs = " order by pd.date_billed ";
		}
		else if(order_by.equals("rec_d")){
		    qs = " order by pd.date_rec ";
		}
		else if(order_by.equals("prop")){
		    qs = " order by pps.status_text ";
		}
	    }
	    if(type.startsWith("owner")){
		//
		// reports related to owners/agents info
		//
		String ownTitles[] = {
		    "ID",
		    "Name",
		    "Address",
		    "City",
		    "State", // 5

		    "Zip",
		    "Phone",
		    "Notes"  // 8
		};
		//
		String qc = "select count(*) ";
		String qy = "select od.name_num,od.name,initcap(od.address),"+
		    "od.city,"+
		    "od.state,od.zip,od.phone_work||' '||od.phone_home,od.notes";
		String qf = " from name od ";
		String qw =" where od.name_num > 0 ";
		if(owner_or_agent.startsWith("ag")){ // agents
		    qw += " and od.name_num in (select rn.name_num from registr pd,"+
			" regid_name rn "+
			" where rd.agent > 0 and rn.id=pd.id ";
		    //
		    // all these should go in the paran
		    if(wherecases.size()>0){
			for (int c = 0; c < wherecases.size(); c++){
			    qw += " and ";
			    qw += wherecases.elementAt(c);
			}
		    }
		    qw += ")";
		}
		else{
		    // owners 
		    if(wherecases.size()>0){
			qw += " and od.name_num in (select rn.name_num from "+
			    "registr pd, "+
			    " regid_name rn where rn.id=pd.id ";
			for (int c = 0; c < wherecases.size(); c++){
			    qw += " and ";
			    qw += wherecases.elementAt(c);
			}
		    }
		    qw += ")";
		}
		qs = " order by lpad(od.name_num,5,'0') "; // ASC
		if(!sortby.isEmpty()){
		    qs += ", "+sortby;
		}
		String str="";
		int ncnt = 0;
		qq = qc+qf+qw;

		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		qq = qy+qf+qw+qs;
		int row=0;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    if(outputType.equals("summary")){
			out.println("<table border>");
		    }
		    //
		    String that = "";
		    while(rs.next()){
			if(row%20 ==0){
			    out.println("<tr>");
			    for (int c = 0; c < ownTitles.length; c++){ 
				out.println("<th>"+ownTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=#e0e0e0 >");  
			else
			    out.println("<tr>");  
			row++;
			for (int c = 0; c < ownTitles.length; c++){ 
			    that = rs.getString(c+1); 
			    if(that == null || 
			       that.trim().equals("")) that ="&nbsp;";
			    if(c == 0){
				if(that.trim().equals("0")) that ="&nbsp;";
				else {
				    that = "<a href="+url+
					"OwnerServ?"+
					"&type="+owner_or_agent+
					"&name_num="+that+"&action=zoom>"+
					that+
					"</a>";
				}
			    }
			    out.println("<td>"+that+"</td>");       
														
			}
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}

	    }
	    else if(type.startsWith("ownNprop")){
		//
		// reports related to owners/agents info
		//
		String ownTitles[] = {
		    "ID",
		    "Name",
		    "Address",
		    "City, State, Zip",
		    "Property Address" // 6
		};
		//
		String qc = "select count(*) ";
		String qy = "select od.name_num,od.name,initcap(od.address),"+
		    "od.city||' '||"+
		    "od.state||' '||od.zip,"+
		    "initcap(ad.street_num"+
		    "||' '||"+
		    "ad.street_dir||' '||ad.street_name||' '||"+
		    "ad.street_type||' '||ad.sud_type||' '||ad.sud_num)";
		String qf = " from name od,address2 ad, "+
		    "registr pd, "+
		    "regid_name rn ";
		String qw =" where od.name_num > 0 and rn.id=pd.id "+
		    " and rn.name_num=od.name_num and ad.registr_id=pd.id and "+
		    " not od.name_num=6010"; // 6010 = Unknown
		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c);
		    }
		}
		qs = " order by od.name"; // ASC 

		String str="";
		int ncnt = 0;
		qq = qc+qf+qw;
        

		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		if(debug){
		    logger.debug("Total Matching "+ ncnt);
		}
		qq = qy+qf+qw+qs;
		int row=0;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    if(outputType.equals("summary")){
			out.println("<table border>");
		    }
		    //
		    String that = "";
		    while(rs.next()){
			if(row%20 ==0){
			    out.println("<tr>");
			    for (int c = 0; c < ownTitles.length; c++){ 
				out.println("<th>"+ownTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=#e0e0e0 >");  
			else
			    out.println("<tr>");  
			row++;
			for (int c = 0; c < ownTitles.length; c++){ 
			    that = rs.getString(c+1); 
			    if(that == null || 
			       that.trim().equals("")) that ="&nbsp;";
			    if(c == 0){
				if(that.trim().equals("0")) that ="&nbsp;";
				else {
				    that = "<a href="+url+
					"OwnerServ?"+
					"&type=own"+
					"&name_num="+that+"&action=zoom>"+
					that+
					"</a>";
				}
			    }
			    out.println("<td>"+that+"</td>");       
			}
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}
	    }
	    else if(type.startsWith("addr")){
		//
		// reports related to addresses info
		//
		String addrTitles[] = {
		    "Address",
		    "Invalid Address",
		    "Permit ID",
		    "Registered Date",
		    "Expire Date"
		};
		boolean invalid = false;
		if(report.startsWith("inadd")){
		    invalid = true;
		}
		boolean addrShow[] = { true,true,true};
		String qc = "select count(*) ";
		String qy = "select initcap(street_num||' '||street_dir||' '"+
		    "||street_name"+
		    "||' '||street_type||' '||sud_type||' '||"+
		    "sud_num),invalid_addr,ad.id,ad.registr_id,to_char(pd.registered_date,'mm/dd/yyyy'),to_char(pd.permit_expires,'mm/dd/yyyy')  ";
		String qf = " from address2 ad,registr pd ";
		String qw =" where ad.registr_id=pd.id ";								
		if(inspTbl){
		    qf += ", inspections insp ";
		    qw += " and insp.id=pd.id ";
		}
		if(sortby.equals("") || sortby.indexOf("street") > -1){
		    qs = " order by ad.street_name,ad.street_dir,"+
			"lpad(street_num,6,'0'),ad.street_name "; // ASC
		}
		else if(sortby.indexOf("expire") > -1){
		    qs = " order by pd.permit_expires ";
		}
		else if(sortby.indexOf("date") > -1){
		    qs = " order by pd.registered_date ";
		}

		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c);
		    }
		}
		int ncnt = 0;
		qq = qc+qf+qw;
		int row=0;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		qq = qy+qf+qw+qs;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    out.println("<table border>");
		    //
		    String that = "", str="", str2="", str3="", str4="", str5="", str6="";
		    while(rs.next()){
			if(row%20==0){
			    out.println("<tr>");
			    for (int c = 0; c < addrTitles.length; c++){ 
				out.println("<th>"+addrTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			str = rs.getString(1);
			str2 = rs.getString(2);
			str3 = rs.getString(3); // address id
			str4 = rs.getString(4);
			str5 = rs.getString(5);	 // register date
			str6 = rs.getString(6); // expire
			if(str5 == null) str5 = "&nbsp;";
			if(str6 == null) str6 = "&nbsp;";
			if(str3 == null || str4 == null) continue;
			if(row%3==2)
			    out.println("<tr bgcolor=\"#e0e0e0\">");  
			else
			    out.println("<tr>");
			row++;
			str = str.trim();
												
			that = "<a href="+url+
			    "AddressEdit?"+
			    "&id="+str3+
			    "&action=zoom>"+
			    str+
			    "</a>";
												
			out.println("<td>"+that+"</td>");
			that = "&nbsp;";
			if(str2 != null || invalid){
			    that = "invalid";
			}
			out.println("<td>"+that+"</td>");
			that = "<a href="+url+
			    "Rental?"+
			    "&id="+str4+
			    "&action=zoom>"+
			    str4+
			    "</a>";
			out.println("<td>"+that+"</td>");
			out.println("<td>"+str5+"</td>");
			out.println("<td>"+str6+"</td>");
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}

	    }
	    else if(type.startsWith("insp")){
		//
		// reports related to inspection info
		//
		String inspTitles[] = {
		    "Inspection",
		    "Rental ID",
		    "Address",
		    "Building type",
		    "Inspection Date",
		    "Inspection Type",
		    "Compliance Date",
		    "Violations",
		    "Smoke Detectors",
		    "Life Safety",
		    "Insp. Time Duration",
		    "Inspected by"
		};
		String qc = "select count(*) ";
		String qy = "select insp.insp_id,insp.id,"+
		    " initcap(ad.street_num||' '||ad.street_dir||' '"+
		    "||ad.street_name"+
		    "||' '||ad.street_type||' '||ad.sud_type||' '||"+
		    "ad.sud_num),"+
		    " pd.building_type,"+ 
		    "to_char(insp.inspection_date,'mm/dd/yyyy'),"+
		    "it.insp_desc,to_char(insp.compliance_date,'mm/dd/yyyy'),"+
		    "insp.violations,insp.smook_detectors,insp.life_safety,"+ // 7
		    "insp.time_spent,"+
		    "initcap(ds.name)";
		String qf = " from inspections insp,registr pd,inspection_types it,"+
		    " inspectors ds,address2 ad ";
		String qw =" where insp.id=pd.id and pd.id=ad.registr_id and "+
		    "it.insp_type=insp.inspection_type and ds.initials=insp.inspected_by";
		if(!violations.equals("")){
		    qw += " and insp.violations > "+violations;
		}
		if(!building_type.equals("")){
		    qw += " and pd.building_type like '"+building_type+"'";
		}
		qs = " order by insp.id,insp.inspection_date ";
								

		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c);
		    }
		}
		//
		String str="";
		int ncnt = 0;
		int vttl=0,sttl=0,lttl=0;
			
		qq = qc+qf+qw;
		//        
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		qq = qy+qf+qw+qs;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    out.println("<table border>");
		    //
		    String that = "", insp_type="";;
		    int row = 0;
		    while(rs.next()){
			if(row%20 == 0){
			    out.println("<tr>");
			    for (int c = 0; c < inspTitles.length; c++){ 
				out.println("<th>"+inspTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=#e0e0e0>");
			else
			    out.println("<tr>");
			row++;
			insp_type = rs.getString(6);
			//
			// we add only cycle violations
			//
			if(insp_type != null && insp_type.equals("Cycle")){
			    vttl += rs.getInt(8);
			    sttl += rs.getInt(9);
			    lttl += rs.getInt(10);
			}
			for (int c = 0; c < inspTitles.length; c++){ 
			    that = rs.getString(c+1); 
			    if(that != null) that = that.trim();
			    if(that == null || 
			       that.equals("")){
				if(c == 7 || c == 8 || c == 9){
				    that = "0";
				}
				else
				    that ="&nbsp;";
			    }
			    if(c == 0){
				if(that.equals("")) that ="&nbsp;";
				else {
				    that = "<a href="+url+
					"InspectFileServ?"+
					"&insp_id="+that+
					">"+
					that+
					"</a>";
				}
			    }
			    else if(c == 1){
				if(that.equals("")) that ="&nbsp;";
				else {
				    that = "<a href="+url+
					"Rental?"+
					"&id="+that+
					">"+
					that+
					"</a>";
				}
			    }	
			    out.println("<td>"+that+"</td>");       
			}
			out.println("</tr>");  
		    }
		    if(insp_type != null && insp_type.equals("Cycle")){
			out.println("<tr><td colspan=7>Total</td><td>"+vttl+"</td><td>"+sttl+"</td><td>"+lttl+"</td><td>&nbsp;</td></tr>");
		    }
		    out.println("</table>");
						
		}

	    }
	    else if(type.equals("prop")){
		//
		// properties managed by an Agent/Owner
		//
		boolean propShow[] = {
		    true,true,true,true,true
		};
		String propTitles[] = {
		    "Permit ID",
		    "Owner Name",
		    "Agent",
		    "Units/ Buildings"
		};
		if(report.endsWith("Agent")){ 
		    // do not show these
		    propShow[2] = false;
		}
		else {
		    propShow[1] = false;
		}
		ArrayList<String> idArr = new ArrayList<String>();
		String qc = "select count(*) ";
		String qy = "select pd.id,od.name,od2.name,pd.units||'/'||"+
		    "pd.structures ";
		String qf = " from registr pd,name od,name od2,regid_name rn ";
		String qw =" where rn.id=pd.id and rn.name_num=od.name_num ";
		qw += " and od2.name_num = pd.agent ";
		if(report.endsWith("Agent")){
		    if(!agent.equals(""))
			qw += " and pd.agent = "+agent;
		    else
			qw += " and pd.agent > 0 ";												
		}
		else{
		    if(!name_num.equals(""))										
			qw += " and od.name_num = "+name_num;
		    else
			qw += " and od.name_num > 0 ";												
		}
		qs = " order by pd.id ";
		//
		String str="";
		int ncnt = 0;
		qq = qc+qf+qw;
        

		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);

		qq = qy+qf+qw+qs;
		if(ncnt == 0){
		    out.println("<center><h3>No match found</h3>");
		}
		else {
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    String that = "";
		    boolean putTitle = false;
		    while(rs.next()){
			if(!putTitle){
			    putTitle = true;
			    if(report.endsWith("Agent")){
				that = rs.getString(3); 
			    }
			    else{
				that = rs.getString(2); 
			    }
			    if(that != null) that = that.trim();
			    else that = "&nbsp;";
			    out.println("<center><h3>"+that+"</h3>");
			    out.println("<h4>Total "+ ncnt +" </h4>");
			    out.println("<table border>");
			    out.println("<tr>");		
			    for (int c = 0; c < propTitles.length; c++){ 
				if(propShow[c])
				    out.println("<th>"+propTitles[c]+
						"</th>");
			    }	   
			    out.println("</tr>");
			}
			// 
			out.println("<tr>");  
			for (int c = 0; c < propTitles.length; c++){ 
			    
			    if(propShow[c]){
				that = rs.getString(c+1); 
				if(that != null) that = that.trim();
				if(that == null || 
				   that.equals("")) that ="&nbsp;";
				if(c == 0){
				    if(that.equals("")) that ="&nbsp;";
				    else {
					idArr.add(that);
					that = "<a href="+url+
					    "Rental?"+
					    "&id="+that+
					    "&action=zoom>"+
					    that+
					    "</a>";
				    }
				}
				out.println("<td>"+that+"</td>");       
			    }
			}
			out.println("</tr>");  
		    }
		    out.println("</table>");
		    out.flush();
		}
		if(idArr.size() > 0){
		    out.println("<br><table border>");
		    out.println("<tr><th>Permit ID"+
				"</th><th>Address</th></tr>");  
		    for(int i=0; i<idArr.size(); i++){
			qy = "select initcap(street_num||' '||"+
			    "street_dir||' '||"+
			    "street_name||' '||street_type||' '||post_dir"+
			    "||' '||sud_type||' '||sud_num)";
			qf = " from address2 ";
			qw =" where registr_id="+idArr.get(i);
			qq = qy+qf+qw;
			if(debug){
			    logger.debug(qq);
			}
			rs = stmt.executeQuery(qq);
			//
			String that = "";
			while(rs.next()){
			    that = rs.getString(1); 
			    if(that != null) that = that.trim();
			    if(that == null || 
			       that.equals("")) that ="&nbsp;"; 
			    out.println("<tr><td>"+idArr.get(i)+
					"</td><td>"+that+"</td></tr>");    
			}
		    }
		    out.println("</table>");
		}
	
	    }
	    else if(type.startsWith("noOwn")){
		//
		String qc = "select count(*) from ";
		String qy = "select pd.id ";

		String qf = " from registr pd ";
		String qw =" where pd.agent=0 ";

		String qy2 = "select pd.id  "; 

		String qf2 = " from registr pd, "+
		    " regid_name rn ";
		String qw2 = " where pd.id=rn.id and "+
		    " rn.name_num > 0 ";
		qc += "("+qy+qf+qw+" minus "+
		    qy2+qf2+qw2+")";
		if(debug){
		    logger.debug(qc);
		}
		rs = stmt.executeQuery(qc);
		rs.next();
		int ncnt = rs.getInt(1);
		if(ncnt == 0){
		    out.println("<center><h3>No match </h3>");
		}
		else {
		    out.println("<center><h3>Total Matching:"+ncnt+" </h3>");
		    out.println("<table border width=80%><tr><th colspan=10>"+
				"Permit ID</th></tr>");
		    qq = qy+ qf+qw+" minus "+
			qy2+qf2+qw2+"";
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    String that="";
		    int jj = 0;
		    out.println("<tr>");
		    while(rs.next()){
			that = rs.getString(1); 
			if(that != null) that = that.trim();
			if(that == null || 
			   that.equals("")) that ="&nbsp;";
			if(that.equals("")) that ="&nbsp;";
			else {
			    that = "<a href="+url+
				"Rental?"+
				"&id="+that+
				"&action=zoom>"+
				that+
				"</a>";
			}
			if(jj % 11 == 0){
			    out.println("</tr>");
			    out.println("<tr>");
			}
			else 
			    out.println("<td>"+that+"</td>");       
			jj++;
		    }
		    if(jj-1 % 11 > 0)
			out.println("</tr>");
		    out.println("</table>");
		    out.flush();
		}
	    }
	    else if(report.startsWith("email")){
		// reports related to owners/agents info
		//
		OwnerList owners = new OwnerList(debug);
		owners.setWithEmailOnly();
		owners.setActiveRent();
		if(who.equals("agent")){
		    owners.setAgentsOnly();
		}
		else if(who.equals("owner")){
		    owners.setOwnersOnly();
		}
		String back = owners.lookFor();
		if(!back.equals("")){
		    message += back;
		}
		out.println("<h4>Total Matching Records "+ owners.size() +" </h4>");
								
		String ownTitles[] = {
		    "ID",
		    "Name",
		    "Address",
		    "City, State Zip",
		    "Phones",
		    "Emails"
		};
		int row = 0;
		if(owners.size() > 0){
		    out.println("<table border>");										
		    for(Owner one:owners){
			if(row%20 ==0){
			    out.println("<tr>");
			    for (int c = 0; c < ownTitles.length; c++){ 
				out.println("<th>"+ownTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=\"#e0e0e0\" >");  
			else
			    out.println("<tr>");  
			row++;
			String str = "<a href="+url+
			    "OwnerServ?"+
			    "&name_num="+one.getId()+"&action=zoom>"+
			    one.getId()+"</a>";
			out.println("<td>"+str+"</td>");
			out.println("<td>"+one.getFullName()+"</td>");
			out.println("<td>"+one.getAddress()+"</td>");
			out.println("<td>"+one.getCityStateZip()+"</td>");
			out.println("<td>"+one.getPhones()+"</td>");
			out.println("<td>"+one.getEmail()+"</td>");												
			out.println("</tr>");
		    }
		    out.println("</table>");
		}
	    }
	    else if(report.startsWith("noEmail")){
		// reports related to owners/agents without emails
		//
		OwnerList owners = new OwnerList(debug);
		owners.setNoEmail();
		owners.setActiveRent();
		if(req_d.equals("expire_d")){
		    owners.setSoonToExpire();
		    owners.setStartDate(date_from);
		    owners.setEndDate(date_to);
		}
		String back = owners.lookFor();
		if(!back.equals("")){
		    message += back;
		}
		out.println("<h4>Total Matching Records "+ owners.size() +" </h4>");
								
		String ownTitles[] = {
		    "ID",
		    "Name",
		    "Address",
		    "City, State Zip",
		    "Phones"
		};
		int row = 0;
		if(owners.size() > 0){
		    out.println("<table border>");										
		    for(Owner one:owners){
			if(row%20 ==0){
			    out.println("<tr>");
			    for (int c = 0; c < ownTitles.length; c++){ 
				out.println("<th>"+ownTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=\"#e0e0e0\" >");  
			else
			    out.println("<tr>");  
			row++;
			String str = "<a href="+url+
			    "OwnerServ?"+
			    "&name_num="+one.getId()+"&action=zoom>"+
			    one.getId()+"</a>";
			out.println("<td>"+str+"</td>");
			out.println("<td>"+one.getFullName()+"</td>");
			out.println("<td>"+one.getAddress()+"</td>");
			out.println("<td>"+one.getCityStateZip()+"</td>");
			out.println("<td>"+one.getPhones()+"</td>");
			out.println("</tr>");
		    }
		    out.println("</table>");
		}
	    }
	    else if(report.startsWith("variance")){
		//
		// permits with variance
		//
		//
		// reports related to owners/agents info
		//
		String varTitles[] = {
		    "Permit ID",
		    "Property Address",
		    "Expire Date",				
		    "Owner Name",
		    "Owner: Address",
		    "Owner: City, State, Zip",
		    "Agent",
		    "Agent: Address",
		    "Agent: City State, Zip",
		    "Variance"
		};
		String qc = "select count(*) ";
		String qy = "select pd.id,"+
		    "initcap(ad.street_num||' '||"+
		    "ad.street_dir||' '||ad.street_name||' '||"+
		    "ad.street_type||' '||ad.sud_type||' '||ad.sud_num),"+
		    "to_char(pd.permit_expires,'yyyy-mm-dd'),"+
		    "initcap(od.name),initcap(od.address),"+
		    "initcap(od.city)||' '||"+
		    "od.state||', '||od.zip,"+
		    "od2.name,initcap(od2.address),"+
		    "initcap(od2.city)||' '||"+
		    "od2.state||', '||od2.zip,"+		
		    "vr.variance ";

		String qf = " from name od,address2 ad, "+
		    "registr pd, "+
		    " regid_name rn, variances vr, "+
		    "name od2 ";
		String qw =" where od.name_num > 0 and rn.id=pd.id and "+
		    " rn.name_num=od.name_num and ad.registr_id=pd.id and "+
		    " not od.name_num=6010 "+// 6010 = Unknown
		    " and pd.agent=od2.name_num "+ 
		    " and vr.id=pd.id and vr.variance is not null";
		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c).toString();
		    }
		}
		qs = " order by pd.permit_expires"; // ASC 

		String str="";
		int ncnt = 0;
		qq = qc+qf+qw;
        

		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		if(debug){
		    logger.debug("Total Matching "+ ncnt);
		}
		qq = qy+qf+qw+qs;
		int row=0;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    out.println("<table border>");
		    //
		    String that = "";
		    while(rs.next()){
			if(row%20 ==0){
			    out.println("<tr>");
			    for (int c = 0; c < varTitles.length; c++){ 
				out.println("<th>"+varTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=#e0e0e0 >");  
			else
			    out.println("<tr>");  
			row++;
			for (int c = 0; c < varTitles.length; c++){ 
			    that = rs.getString(c+1); 
			    if(that == null || 
			       that.trim().equals("")) that ="&nbsp;";
			    if(c == 0){
				if(that.trim().equals("0")) that ="&nbsp;";
				else {
				    that = "<a href="+url+
					"Rental?id="+that+
					"&action=zoom>"+
					that+
					"</a>";
				}
			    }
			    out.println("<td>"+that+"</td>");       
			}
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}
	    }
	    else if(type.equals("oldUnit")){
		// reports related to owners/agents info
		//
		Set<String> set = new HashSet<String>();
		String oldTitles[] = {
		    "Permit ID",
		    "Property Address",
		    "Expire Date",
		    "Structures/Units",									
		    "Owner Name",
		    "Owner: Address",
		    "Owner: City, State, Zip",
		    "Agent",
		    "Agent: Address",
		    "Agent: City State, Zip"
		};
		String qc = "select count(*) ";
		qc += " from registr pd, name od, regid_name rn ";
		qc +=" where od.name_num > 0 and rn.id=pd.id and "+
		    " rn.name_num=od.name_num "+
		    " and not od.name_num=6010 ";
		for (int c = 0; c < wherecases.size(); c++){
		    qc += " and ";
		    qc += wherecases.elementAt(c).toString();
		}
		qc += " and pd.id not in "+
		    " (select rid from rental_structures) ";
		String qy = "select pd.id,"+
		    "(case when ad.streetAddress is null "+
		    "then initcap(ad.street_num||' '||"+
		    "ad.street_dir||' '||ad.street_name||' '||"+
		    "ad.street_type||' '||ad.sud_type||' '||ad.sud_num) "+
		    "else initcap(ad.streetAddress) "+
		    " end) as address,"+
		    "to_char(pd.permit_expires,'mm/dd/yyyy'),"+
		    "pd.structures||'/'||pd.units,"+
		    "initcap(od.name),initcap(od.address),"+
		    "initcap(od.city)||' '||"+
		    "od.state||', '||od.zip,"+
		    "od2.name,initcap(od2.address),"+
		    "initcap(od2.city)||' '||"+
		    "od2.state||', '||od2.zip ";	

		String qf = " from name od, address2 ad, "+
		    "registr pd, "+
		    " regid_name rn, "+
		    "name od2 ";
		String qw =" where od.name_num > 0 and rn.id=pd.id and "+
		    " rn.name_num=od.name_num and ad.registr_id=pd.id "+
		    " and not od.name_num=6010 "+// 6010 = Unknown
		    " and pd.agent=od2.name_num ";
		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c).toString();
		    }
		}
		qw += " and pd.id not in "+
		    " (select rid from rental_structures)";								
		qs = " order by pd.permit_expires"; // ASC 

		String str="";
		int ncnt = 0;
		qq = qc;
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		if(debug){
		    logger.debug("Total Matching "+ ncnt);
		}
		qq = qy+qf+qw+qs;
		int row=0;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    out.println("<table border>");
		    //
		    String that = "";
		    while(rs.next()){
			if(row%20 ==0){
			    out.println("<tr>");
			    for (int c = 0; c < oldTitles.length; c++){ 
				out.println("<th>"+oldTitles[c]+"</th>");
			    }	   
			    out.println("</tr>");
			}
			if(row%3 == 2)
			    out.println("<tr bgcolor=#e0e0e0 >");  
			else
			    out.println("<tr>");  
			row++;
			that = rs.getString(1);
			if(set.contains(that)) continue;
			set.add(that);
			for (int c = 0; c < oldTitles.length; c++){ 
			    that = rs.getString(c+1); 
			    if(that == null || 
			       that.trim().equals("")) that ="&nbsp;";
			    if(c == 0){
				if(that.trim().equals("0")) that ="&nbsp;";
				else {
				    that = "<a href="+url+
					"Rental?id="+that+
					"&action=zoom>"+
					that+
					"</a>";
				}
			    }
			    out.println("<td>"+that+"</td>");       
			}
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}
	    }
	    else if(report.startsWith("pull") ||
		    report.startsWith("cycle") ||
		    report.startsWith("overdue")){
		out.println("<form name=myForm method=post>");
		out.println("<input type=hidden name=order_by value=\""+
			    "\">");
		if(!type.equals("")){
		    out.println("<input type=hidden name=type value=\""+
				type+"\">");
		}
		if(!prop_type.equals("")){
		    out.println("<input type=hidden name=prop_type value=\""+
				prop_type+"\">");
		}
		if(!report.equals("")){
		    out.println("<input type=hidden name=report value=\""+
				report+"\">");
		}
		if(!inactive.equals("")){
		    out.println("<input type=hidden name=inactive value=\"y\">");
		}
		// since we can't have more than one of  these
		if(!req_d.equals("")){
		    out.println("<input type=hidden name=req_d value=\""+
				req_d+"\">");
		}
		if(!owner_or_agent.equals("")){
		    out.println("<input type=hidden name=owner_or_agent value=\""+
				owner_or_agent+"\">");
		}
		if(!property_status.equals("")){
		    out.println("<input type=hidden name=property_status value=\""+
				property_status+"\">");
		}
		if(!pull_reason.equals("")){
		    out.println("<input type=hidden name=pull_reason value=\""+
				pull_reason+"\">");
		}
		if(!valid_address.equals("")){
		    out.println("<input type=hidden name=valid_address value=\""+
				valid_address+"\">");
		}
		if(!sortby.equals("")){
		    out.println("<input type=hidden name=sortby value=\""+
				sortby+"\">");
		}
		if(!street_opt.equals("")){
		    out.println("<input type=hidden name=street_opt value=\""+
				street_opt+"\">");
		}
		if(!date_from.equals("")){
		    out.println("<input type=hidden name=date_from value=\""+
				date_from+"\">");
		}
		if(!date_to.equals("")){
		    out.println("<input type=hidden name=date_to value=\""+
				date_to+"\">");
		}
		out.println("</form>");				
		out.println("<br><b>Sort by:</b>");
		if(order_by.equals("id") || order_by.equals("")){
		    out.println(" ID,");
		}
		else{
		    // default
		    out.println("<a href=javascript:sendSortSub(\"id\")>ID</a>,"); 
		}
		if(show[2] || showAll){
		    if(order_by.equals("owner")){
			out.println(" Owner Name,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"owner\")>"+
				    "Owner Name</a>,");
		    }
		}
		if(show[4] || showAll){
		    if(order_by.equals("agent")){
			out.println(" Agent,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"agent\")>"+
				    "Agent</a>,");
		    }
		}
		if(show[5] || showAll){
		    if(order_by.equals("address")){
			out.println(" Address,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"address\")>"+
				    "Address</a>,");
		    }
		}
		if(show[6] || showAll){
		    if(order_by.equals("reg_d")){
			out.println(" Registered Date,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"reg_d\")>"+
				    "Registered Date</a>,");
		    }
		}
		if(show[7] || showAll){
		    if(order_by.equals("cycle_d")){
			out.println(" Last Cycle Date,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"cycle_d\")>"+
				    "Last Cycle Date</a>,");
		    }
		}
		if(show[8] || showAll){
		    if(order_by.equals("issue_d")){
			out.println(" Permit Issue Date,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"issue_d\")>"+
				    "Permit Issue Date</a>,");
		    }
		}
		if(show[9] || showAll){
		    if(order_by.equals("expire_d")){
			out.println(" Expire Date,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"expire_d\")>"
				    +"Expire Date</a>,");
		    }
		}
		if(show[11] || showAll){
		    if(order_by.equals("pull_d")){
			out.println(" Pull Date,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"pull_d\")>"+
				    "Pull Date</a>,");
		    }
		}
		if(show[12] || showAll){
		    if(order_by.equals("pull_r")){
			out.println(" Pull reason,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"pull_r\")>"+
				    "Pull reason</a>,");
		    }
		}
		if(show[13] || showAll){
		    if(order_by.equals("bill_d")){
			out.println(" Date billed,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"bill_d\")>"+
				    "Date billed</a>,");
		    }
		}
		if(show[14] || showAll){
		    if(order_by.equals("rec_d")){
			out.println(" Date received,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"rec_d\")>"
				    +"Date received</a>,");
		    }
		}
		if(show[15] || showAll){
		    if(order_by.equals("prop")){
			out.println(" Property status,");
		    }	
		    else{
			out.println("<a href=javascript:sendSortSub(\"prop\")>"+
				    "Property status</a>");
		    }
		}								
		String qc = "select count(*) ";
		String qy = "select pd.id,od.name_num,od.name,"+
		    "pd.agent,od2.name," // 4
		    +"initcap(ad.street_num||' '||ad.street_dir||' '||"
		    +"ad.street_name||' '||"
		    +"ad.street_type||' '||ad.post_dir||' '||ad.sud_type||' '||"
		    +"ad.sud_num),"
		    +"to_char(pd.registered_date,'Mon dd/yyyy'), " 
		    +"to_char(pd.last_cycle_date,'Mon dd/yyyy'), "  
		    +"to_char(pd.permit_issued,'Mon dd/yyyy'), " 
		    +"to_char(pd.permit_expires,'Mon dd/yyyy'), "  // 8
		    +"pd.permit_length,"
		    +"to_char(pd.pull_date,'Mon dd/yyyy'), " 
		    +"initcap(pr.pull_text),"                              // 11
		    +"to_char(pd.date_billed,'Mon dd/yyyy'), " 
		    +"to_char(pd.date_rec,'Mon dd/yyyy'), " 
		    +"pps.status_text,"
		    +"pd.units||'/'||pd.structures,"
		    +"pd.bedrooms||'/'||pd.bath_count,"  // 16
		    +"pd.occ_load,"
		    // +"zd.zone_text,"
		    +"pd.cdbg_funding,"
		    +"pd.notes,"+
		    "pd.nhood,pd.prop_type ";             // 20
		String qf = " from registr pd, "+
		    "pull_reas pr, "+
		    "prop_status pps,name od,name od2, regid_name rn, "+
		    "address2 ad";
		if(!zoning.equals(""))
		    qf += ",zoning_2007 zd ";
		if(inspTbl)
		    qf += ", inspections id";
		if(phoneTbl)
		    qf += ", owner_phones op";			
		//
		String qw =" where "+
		    "pd.pull_reason=pr.p_reason "+
		    "and pd.property_status=pps.status and rn.id=pd.id "+
		    "and ad.registr_id=pd.id "+
		    "and pd.agent=od2.name_num "+  
		    "and rn.name_num=od.name_num ";
		if(!zoning.equals(""))
		    qw += " and pd.zoning2=zd.zoned ";
		if(owner_or_agent.startsWith("ag")){ // agent
		    qw += " and pd.agent > 0 ";
		}
		if(inspTbl)
		    qw += " and insp.id=pd.id ";
	    
		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c);
		    }
		}
		qs ="";
		if(report.startsWith("pullR")){
		    qw += " and pd.inactive is null ";
		    if(order_by.equals("pull_r")){
			qs = " order by pr.pull_text,ad.street_name,ad."+
			    "street_dir,lpad(ad.street_num,6,'0') ";
			if(!sortby.isEmpty()){
			    qs +=", "+sortby; // ASC
			}
		    }
		    else if(order_by.equals("id")){
			qs = " order by pd.id ";

		    }
		    else if(order_by.equals("address")){
			qs = " order by ad.street_name,ad.street_dir,"+
			    "lpad(ad.street_num,6,'0') ";
		    }
		    else {
			qs = " order by pd.pull_date, ";
			qs += " pr.pull_text,ad.street_name,ad."+
			    "street_dir,lpad(ad.street_num,6,'0')";
			if(!sortby.isEmpty()){
			    qs += ", "+sortby;
			}
													 
		    }
		}
		else if(report.equals("pull")){
		    //
		    if(order_by.equals("pull_d")){
			qs = " order by pd.pull_date, ";
			qs += " pr.pull_text,ad.street_name,ad.street_dir,"+
			    "lpad(ad.street_num,6,'0') "; // ASC
			if(!sortby.isEmpty()){
			    qs += ", "+sortby;
			}
		    }
		    else if(order_by.equals("address")){
			qs = " order by ad.street_name,ad.street_dir,"+
			    "lpad(ad.street_num,6,'0')";
		    }
		    else if(order_by.equals("id")){
			qs = " order by pd.id ";
		    }
		    else if(order_by.equals("pull_r")){
			qs = " order by pr.pull_text,ad.street_name,ad."+
			    "street_dir,lpad(ad.street_num,6,'0')"; // ASC
			if(!sortby.isEmpty()){
			    qs += ", "+sortby;
			}
		    }
		}
		else if(order_by.equals("owner")){
		    qs = " order by od.name ";
		}
		else if(order_by.equals("id")){
		    qs = " order by pd.id ";
		}
		else if(order_by.equals("agent")){
		    qs = " order by od2.name ";
		}
		else if(order_by.equals("cycle_d")){
		    qs = " order by pd.last_cycle_date ";
		}
		else if(order_by.equals("issue_d")){
		    qs = " order by pd.permit_issued ";
		}
		else if(order_by.equals("expire_d")){
		    qs = " order by pd.permit_expires ";
		}
		else if(order_by.equals("bill_d")){
		    qs = " order by pd.date_billed ";
		}
		else if(order_by.equals("rec_d")){
		    qs = " order by pd.date_rec ";
		}
		else if(order_by.equals("prop")){
		    qs = " order by pps.status_text ";
		}
		String str="";
		int ncnt = 0;
		qq = qc+qf+qw;
		int row = 0;
			
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		rs.next();
		ncnt = rs.getInt(1);
		out.println("<h4>Total Matching Records "+ ncnt +" </h4>");
		qq = qy+qf+qw+qs;
		if(ncnt > 0){
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    if(!report.equals(""))
			out.println("<table>");
		    else
			out.println("<table border>");
		    //
		    String that = "";
		    // 
		    out.println("<tr>");
		    for (int c = 0; c < titles.length; c++){ 
			if(show[c] || showAll)
			    out.println("<th>"+titles[c]+"</th>");
		    }	   
		    out.println("</tr>");
		    //
		    while(rs.next()){
			if(row%3 == 2)
			    out.println("<tr bgcolor=\"#e0e0e0\">");
			else
			    out.println("<tr>");  
			row++;
			for (int c = 0; c < titles.length; c++){ 
			    if(show[c] || showAll){
				that = rs.getString(c+1); 
				if(that == null || 
				   that.trim().equals("")) that ="&nbsp;";
				if(c == 0){
				    if(that.trim().equals("0")) 
					that ="&nbsp;";
				    else {
					that = "<a href="+url+
					    "Rental?"+
					    "&id="+that+"&action=zoom>"+
					    that+"</a>";
				    }
				}
				else if(c == 1){
				    if(that.trim().equals("0")) 
					that ="&nbsp;";
				    else {
					that = "<a href="+url+
					    "OwnerServ?"+
					    "&name_num="+that+
					    "&action=zoom>"+
					    "Owner: "+that+"</a>";
																				
				    }
				}
				else if(c == 2){
				    if(that.trim().equals("NO AGENT")) 
					that ="&nbsp;";
				}
				else if(c == 3){
				    if(that.trim().equals("0")) 
					that ="&nbsp;";
				    else {
					that = "<a href="+url+
					    "OwnerServ?"+
					    "&name_num="+that+
					    "&action=zoom&type=agent>"+
					    "Agent: "+that+"</a>";
				    }
				}
				else if(c == 16){ // units/structures
				    if(that.trim().equals("/")) 
					that ="&nbsp;";
				}
				out.println("<td>"+that+"</td>");       
			    }
			}
			out.println("</tr>");  
		    }
		    out.println("</table>");
		}
	    }
	}catch(Exception ex){
	    out.println(ex);
	    message += ex;
	    success = false;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	if(!success){
	    if(!message.equals("")){
		out.println("<p> Error; "+message+"</p>");
	    }
	}
	out.println("<br><center>");
	out.println("</body>");
	out.println("</html>");
	out.close();
		
    }

}






















































