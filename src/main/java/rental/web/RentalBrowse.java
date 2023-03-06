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

@WebServlet(urlPatterns = {"/RentalBrowse"})
public class RentalBrowse extends TopServlet {

    final static long serialVersionUID = 870L;
    static Logger logger = LogManager.getLogger(RentalBrowse.class);
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
	String violations="", accessory_dwelling="";
	String registr_d="",issue_d="",cycle_d="",expire_d="",bill_d="",
	    rec_d="",pull_d="",insp_d="",req_d="";
	String outputType="summary", showOut="Basics",report="";
	String date_from="",date_to="", who="", units_from="";
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
	   
	    if (name.equals("u_multi")) {
		u_multi=value;
	    }
	    else if (name.equals("u_single")) {
		u_single=value;
	    }
	    else if (name.equals("date_from")) {
		date_from=value;
	    }
	    else if (name.equals("date_to")) {
		date_to=value;
	    }
	    else if (name.equals("zoning")) {
		zoning=value;
	    }
	    else if (name.equals("units")){
		units = value;
	    }
	    else if (name.equals("units_from")){
		units_from = value;
	    }			
	    else if (name.equals("who")){ // from reports
		who = value;
	    }
	    else if (name.equals("inspection_type")){
		inspection_type = value;
	    }
	    else if (name.equals("structures")){
		structures = value;
	    }
	    else if (name.equals("occ_load")){
		occ_load = value;
	    }
	    else if (name.equals("cdbg_funding")) {
		cdbg_funding=value;
	    }
	    else if (name.equals("affordable")) {
		affordable=value;
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
	    else if (name.equals("property_status")) {
		property_status =value;
	    }
	    else if (name.equals("type")) {
		type = value;
	    }
	    else if (name.equals("order_by")) {
		order_by =value;
	    }
	    else if (name.equals("nhood")) {
		nhood =value;
	    }
	    else if (name.equals("name_num")) {
		name_num =value;
	    }
	    else if (name.equals("report")) {
		report =value;
	    }
	    else if (name.equals("pull_reason")) {
		pull_reason =value;
	    }
	    else if (name.equals("showOut")) {
		showOut =value;
	    }
	    else if (name.equals("street_num")) {
		street_num =value;
	    }
	    else if (name.equals("street_dir")) {
		street_dir =value;
	    }
	    else if (name.equals("street_name")) {
		if(!value.equals(""))
		    street_name = value.toUpperCase();
	    }
	    else if (name.equals("street_type")) {
		street_type =value;
	    }
	    else if (name.equals("post_dir")) {
		post_dir =value;
	    }
	    else if (name.equals("permit_length")) {
		permit_length =value;
	    }
	    else if (name.equals("sud_num")) {
		sud_num =value;
	    }
	    else if (name.equals("sud_type")) {
		sud_type =value;
	    }
	    else if (name.equals("valid_address")) {
		valid_address =value;
	    }
	    else if (name.equals("outputType")) {
		outputType =value;
	    }
	    else if (name.equals("sortby")) {
		sortby =value;
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
	    else if (name.equals("id")) {
		id = value;
	    }
	    else if (name.equals("agent")) {
		agent = value;
	    }
	    else if (name.equals("inactive")) {
		inactive = value;
	    }
	    else if (name.equals("phone")) {
		phone = value;
	    }
	    else if (name.equals("grandfathered")) {
		grandfathered = value;
	    }
	    else if (name.equals("accessory_dwelling")) {
		accessory_dwelling = value;
	    }						
	    else if (name.equals("city")) {
		city = value.toUpperCase();
	    }
	    else if (name.equals("state")) {
		state = value.toUpperCase();
	    }
	    else if (name.equals("own_name")) {
		own_name = value.toUpperCase();
	    }
	    else if (name.equals("own_addr")) {
		own_addr = value.toUpperCase();
	    }
	    else if (name.equals("zip")) {
		zip = value.toUpperCase();
	    }
	    else if (name.equals("address_opt")) {
		address_opt = value;
	    }
	    else if (name.equals("notes_opt")) {
		notes_opt = value;
	    }
	    else if (name.equals("violations")) {
		violations = value;
	    }	
	    else if (name.equals("notes")) {
		if(!value.equals(""))
		    notes = value.toUpperCase();
	    }
	    else if (name.equals("cc_all")){
		showAll = true;
	    }
	}
	if(!id.equals("")){
	    Rent rent = new Rent(id, debug);
	    String str = rent.doSelect();
	    if(str.equals("")){
		str = url+"Rental?action=zoom&id="+id;
		res.sendRedirect(str);
		return;
	    }
	    else{
		success = false;
		message = str;
		showError(out, message);
		return;
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
	    for(int i=0; i<show.length;i++) 
		show[i] = false;
	    if(report.equals("addr")){
		type = "address";
	    }
	    else if(report.equals("inaddr")){
		type = "address";
		// list invalid addresses				
		wherecases.addElement("(ad.invalid_addr='Y' or ad.location_id is null)");
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
	    }
	    else if(report.equals("noPullDate")){
		// pull dates and reason
		show[0] = true;
		show[5] = true;  // address
		show[11] = false;  // pull date
		show[12] = true;  // pull reason
		if(order_by.equals(""))order_by="pull_r";
		wherecases.addElement("pd.pull_date is null");
	    }
	    else if(report.equals("withUnits")){
		//
		type= "withUnits";
		show[0] = true;
		show[5] = true;  // address
		// wherecases.addElement("pd.pull_date is null");
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
		show[0] = true;
		show[7] = true;  // last cycle date 
		show[8] = true;
		issue_d="y";
		// 
	    }
	    else if(report.startsWith("inspect")){
		// list of inspections
		type = "inspect";
		if(!inspection_type.equals("")){
		    wherecases.addElement("id.inspection_type='"+inspection_type+"'");
		}
	    }
	    else if(report.equals("inspType")){
		// list of inspections and their types
		type = "inspect";
	    }
	    else if(report.equals("ownNprop")){ // owners and property lists
		// list of properties and their owners
		type = "ownNprop";
	    }
	    else if(report.equals("noOwnAgent")){ // No owner nor agent
		// list of properties with no owner nor agent
		type = "noOwnAgent";
	    }
	    else if(report.startsWith("email")){ // No owner nor agent
		// list of properties with no owner nor agent
		type = "email";
	    }
	    else if(report.startsWith("variance")){ // No owner nor agent
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
		// overdue bills
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
	if(!id.equals("")){
	    //
	    // we are looking certain record
	    // so we ignore all other parameters
	    //
	    wherecases.addElement("pd.id ="+id);
	}
	else{
	    String str="";
	    if(!name_num.equals("")){
		if(owner_or_agent.equals("")){
		    wherecases.addElement("(od.name_num="+name_num +" or "+
					  "pd.agent="+name_num+")");
		}
		else if(owner_or_agent.startsWith("o")){
		    wherecases.addElement("od.name_num ="+name_num);
		    ownTbl = true;
		}
		else{
		    wherecases.addElement("pd.agent ="+name_num);
		}
	    }
	    if(!property_status.equals("")){
		wherecases.addElement("pd.property_status ='"+
				      property_status+"'");
	    }
	    if(!grandfathered.equals("")){
		wherecases.addElement("pd.grandfathered ='Y'");
	    }
	    if(!accessory_dwelling.equals("")){
		wherecases.addElement("pd.accessory_dwelling is not null");
	    }						
	    if(!u_multi.equals("")){
		wherecases.addElement("pd.units > 1");
		wherecases.addElement("(not pd.prop_type like  '%House')");
	    }
	    else if(!u_single.equals("")){
		wherecases.addElement("pd.units = 1");
	    }
	    if(!cdbg_funding.equals("")){
		wherecases.addElement("pd.cdbg_funding ='Y'");
	    }
	    if(inactive.equals("")){
		wherecases.addElement("pd.inactive is null"); // change to pd later
	    }
	    else{
		wherecases.addElement("pd.inactive is not null "); // inactive records
	    }
	    if(!affordable.equals("")){
		wherecases.addElement("pd.affordable is not null"); 
	    }	
	    if(!pull_reason.equals("")){
		wherecases.addElement("pd.pull_reason ='"+pull_reason+"'");
	    }
	    if(!units.equals("")){
		wherecases.addElement("pd.units ="+
				      units);
	    }
	    if(!structures.equals("")){
		wherecases.addElement("pd.structures ="+
				      structures);
	    }
	    if(!occ_load.equals("")){
		wherecases.addElement("pd.occ_load ='"+
				      occ_load+"'");
	    }
	    if(!zoning.equals("")){
		wherecases.addElement("pd.zoning2 ='"+zoning+"'");
	    }
	    if(!permit_length.equals("")){
		wherecases.addElement("pd.permit_length ='"+permit_length+"'");
	    }
	    if(!street_num.equals("")){
		wherecases.addElement("ad.street_num ='"+street_num+"'");
		addrTbl = true;
	    }
	    if(!street_dir.equals("")){
		wherecases.addElement("ad.street_dir ='"+street_dir+"'");
		addrTbl = true;
	    }
	    if(!street_name.equals("")){
		addrTbl = true;
		str = Helper.doubleApostrify(street_name);
		if(street_opt.equals("is") || street_opt.equals(""))
		    wherecases.addElement("ad.street_name = '"+str+"'");
		else if(street_opt.startsWith("cont"))
		    wherecases.addElement("ad.street_name like '%"+str+"%'");
		else if(street_opt.startsWith("st"))
		    wherecases.addElement("ad.street_name like '"+str+"%'");
		else if(street_opt.startsWith("end"))
		    wherecases.addElement("ad.street_name like '%"+str+"'");
	    }
	    if(!street_type.equals("")){
		wherecases.addElement("ad.street_type ='"+street_type+"'");
		addrTbl = true;
	    }
	    if(!post_dir.equals("")){
		wherecases.addElement("ad.post_dir ='"+post_dir+"'");
		addrTbl = true;
	    }
	    if(!sud_num.equals("")){
		wherecases.addElement("ad.sud_num ='"+sud_num+"'");
		addrTbl = true;
	    }
	    if(!sud_type.equals("")){
		wherecases.addElement("ad.sud_type ='"+sud_type+"'");
		addrTbl = true;
	    }
	    if(!valid_address.equals("")){
		if(valid_address.equals("Y"))
		    wherecases.addElement("ad.invalid_addr is null");
		else 
		    wherecases.addElement("ad.invalid_addr ='Y'");
		addrTbl = true;
	    }
	    if(!own_name.equals("")){
		str = Helper.doubleApostrify(own_name);
		if(owner_or_agent.equals("") || 
		   owner_or_agent.startsWith("o")){
		    if(name_opt.equals("is") || name_opt.equals(""))
			wherecases.addElement("od.name ='"+str+"'");
		    else if(name_opt.startsWith("cont"))
			wherecases.addElement("od.name like '%"+str+"%'");
		    else if(name_opt.startsWith("st"))
			wherecases.addElement("od.name like '"+str+"%'");
		    else if(name_opt.startsWith("end"))
			wherecases.addElement("od.name like '%"+str+"'");
		    ownTbl = true;
		}
		else{ // agent
		    if(name_opt.equals("is") || name_opt.equals(""))
			wherecases.addElement("od2.name ='"+str+"'");
		    else if(name_opt.startsWith("cont"))
			wherecases.addElement("od2.name like '%"+str+"%'");
		    else if(name_opt.startsWith("st"))
			wherecases.addElement("od2.name like '"+str+"%'");
		    else if(name_opt.startsWith("end"))
			wherecases.addElement("od2.name like '%"+str+"'");
		    agntTbl = true;
		}
	    }
	    if(!own_addr.equals("")){
		str = Helper.doubleApostrify(own_addr);
		if(owner_or_agent.equals("") || 
		   owner_or_agent.startsWith("o")){
		    if(own_addr_opt.equals("is") || own_addr_opt.equals(""))
			wherecases.addElement("od.address ='"+str+"'");
		    else if(own_addr_opt.startsWith("cont"))
			wherecases.addElement("od.address like '%"+str+"%'");
		    else if(own_addr_opt.startsWith("st"))
			wherecases.addElement("od.address like '"+str+"%'");
		    else if(own_addr_opt.startsWith("end"))
			wherecases.addElement("od.address like '%"+str+"'");
		    ownTbl = true;
		}
		else {
		    if(own_addr_opt.equals("is") || own_addr_opt.equals(""))
			wherecases.addElement("od2.address ='"+str+"'");
		    else if(own_addr_opt.startsWith("cont"))
			wherecases.addElement("od2.address like '%"+str+"%'");
		    else if(own_addr_opt.startsWith("st"))
			wherecases.addElement("od2.address like '"+str+"%'");
		    else if(own_addr_opt.startsWith("end"))
			wherecases.addElement("od2.address like '%"+str+"'");
		    agntTbl = true;
		}
	    }
	    if(!(prop_type.equals(""))){
		wherecases.addElement("pd.prop_type ='"+prop_type+"'");
		supTbl = true;
	    }
	    if(!(nhood.equals(""))){
		wherecases.addElement("pd.nhood ='"+nhood+"'");
		supTbl = true;
	    }
	    if(!phone.equals("")){
		phoneTbl = true;
		if(owner_or_agent.equals("") || 
		   owner_or_agent.startsWith("o")){
		    wherecases.addElement("op.phone_num ='"+phone+"' and "+
					  "op.name_num = od.name_num ");
		    ownTbl = true;
		}
		else{
		    wherecases.addElement("(op.phone_num ='"+phone+"' and "+
					  "od2.name_num = op.name_num ");
		    agntTbl = true;
		}
	    }
	    if(!city.equals("")){
		if(owner_or_agent.startsWith("a")){
		    wherecases.addElement("od2.city ='"+city+"'");
		    agntTbl = true;
		}
		else{
		    wherecases.addElement("od.city ='"+city+"'");
		    ownTbl = true;
		}
	    }
	    if(!zip.equals("")){
		if(owner_or_agent.startsWith("a")){
		    wherecases.addElement("od2.zip ='"+zip+"'");
		    agntTbl = true;
		}
		else {
		    wherecases.addElement("od.zip ='"+zip+"'");
		    ownTbl = true;
		}
	    }
	    if(!state.equals("")){
		if(owner_or_agent.startsWith("a")){
		    wherecases.addElement("od2.state ='"+state+"'");
		    agntTbl = true;
		}
		else {
		    wherecases.addElement("od.state ='"+state+"'");
		    ownTbl = true;
		}
	    }
	    if(report.equals("noPullDate")){


	    }
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
					  "','mm/dd/yyyy') >= id.inspection_date");
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
					  "','mm/dd/yyyy') <= id.inspection_date");
		    inspTbl = true;	 
		}
	    }
	    if(!notes.equals("")){
		str = Helper.doubleApostrify(notes);
		if(notes_opt.equals("is") || notes_opt.equals(""))
		    wherecases.addElement("upper(pd.notes) ='"+str+"'");
		else if(notes_opt.startsWith("cont"))
		    wherecases.addElement("upper(pd.notes) like '%"+str+"%'");
		else if(notes_opt.startsWith("st"))
		    wherecases.addElement("upper(pd.notes) like '"+str+"%'");
		else if(notes_opt.startsWith("end"))
		    wherecases.addElement("upper(pd.notess) like '%"+str+"'");
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
	}
	//
	if(!report.equals("")){
	    out.println("<html><head><title>Rental </title>");
	    Helper.writeWebCss(out, url);
	    out.println("</head><body>");
	    Helper.writeTopMenu(out, url);	  
	    out.println("<center>");
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
	    else if(report.equals("inspectViolation")){
		out.println("<h2>Inspections with Violations</h2>");

	    }
	    else if(report.equals("inspect")){
		//
		// list of inspections
		out.println("<h2>Dates of Inspection ");
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
	String qq = "";
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
	    if(!success){
		out.println("<h2>Error: Could not connect to Database</h2>");
		out.println("<h3>"+message+"</h3>");
		out.println("<h2>Check with ITS</h2>");
		out.println("</body></html>");
		return;
	    }
	    //
	    if(type.equals("rental")){
		// 
		out.println("<html><head><title>Rental </title>");
		Helper.writeWebCss(out, url);
		out.println("<script type='text/javascript'>");
		out.println("  function sendSortSub(order_by) { ");
		out.println("  document.myForm.order_by.value=order_by; ");
		out.println("  document.myForm.submit();         ");
		out.println(" }                             ");   

		//
		out.println("</script>");  
		out.println("</head><body>");
		Helper.writeTopMenu(out, url);	    		

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
		if(!nhood.equals("")){
		    out.println("<input type=hidden name=nhood value=\""+
				nhood+"\">");
		}
		if(!phone.equals("")){
		    out.println("<input type=hidden name=phone value=\""+
				phone+"\">");
		}
		if(!city.equals("")){
		    out.println("<input type=hidden name=city value=\""+
				city+"\">");
		}
		if(!state.equals("")){
		    out.println("<input type=hidden name=state value=\""+
				state+"\">");
		}
		if(!zip.equals("")){
		    out.println("<input type=hidden name=zip value=\""+
				zip+"\">");
		}
		if(!units.equals("")){
		    out.println("<input type=hidden name=units value=\""+
				units+"\">");
		}
		if(!structures.equals("")){
		    out.println("<input type=hidden name=structures value=\""+
				structures+"\">");
		}
		if(!occ_load.equals("")){
		    out.println("<input type=hidden name=occ_load value=\""+
				occ_load+"\">");
		}
		if(!inactive.equals("")){
		    out.println("<input type=hidden name=inactive value=\"y\">");
		}
		// since we can't have more than one of  these
		if(!req_d.equals("")){
		    out.println("<input type=hidden name=req_d value=\""+
				req_d+"\">");
		}
		if(!id.equals("")){
		    out.println("<input type=hidden name=id value=\""+id+"\">");
		}
		if(!showOut.equals("")){
		    out.println("<input type=hidden name=showOut value=\""+
				showOut+"\">");
		}
		if(!notes.equals("")){
		    out.println("<input type=hidden name=notes value=\""+
				notes+"\">");
		}
		if(!notes_opt.equals("")){
		    out.println("<input type=hidden name=notes_opt value=\""+
				notes_opt+"\">");
		}
		if(!name_num.equals("")){
		    out.println("<input type=hidden name=name_num value=\""+
				name_num+"\">");
		}
		if(!owner_or_agent.equals("")){
		    out.println("<input type=hidden name=owner_or_agent value=\""+
				owner_or_agent+"\">");
		}
		if(!property_status.equals("")){
		    out.println("<input type=hidden name=property_status value=\""+
				property_status+"\">");
		}
		if(!grandfathered.equals("")){
		    out.println("<input type=hidden name=grandfathered value=\""+
				grandfathered+"\">");
		}
		if(!cdbg_funding.equals("")){
		    out.println("<input type=hidden name=cdbg_funding value=\""+
				cdbg_funding+"\">");
		}
		if(!pull_reason.equals("")){
		    out.println("<input type=hidden name=pull_reason value=\""+
				pull_reason+"\">");
		}
		if(!zoning.equals("")){
		    out.println("<input type=hidden name=zoning value=\""+
				zoning+"\">");
		}
		if(!permit_length.equals("")){
		    out.println("<input type=hidden name=permit_length value=\""+
				permit_length+"\">");
		}
		if(!street_num.equals("")){
		    out.println("<input type=hidden name=street_num value=\""+
				street_num+"\">");
		}
		if(!street_dir.equals("")){
		    out.println("<input type=hidden name=street_dir value=\""+
				street_dir+"\">");
		}
		if(!street_name.equals("")){
		    out.println("<input type=hidden name=street_name value=\""+
				street_name+"\">");
		}
		if(!street_type.equals("")){
		    out.println("<input type=hidden name=street_type value=\""+
				street_type+"\">");
		}
		if(!post_dir.equals("")){
		    out.println("<input type=hidden name=post_dir value=\""+
				post_dir+"\">");
		}
		if(!sud_num.equals("")){
		    out.println("<input type=hidden name=sud_num value=\""+
				sud_num+"\">");
		}
		if(!sud_type.equals("")){
		    out.println("<input type=hidden name=sud_type value=\""+
				sud_type+"\">");
		}
		if(!valid_address.equals("")){
		    out.println("<input type=hidden name=valid_address value=\""+
				valid_address+"\">");
		}
		if(!own_name.equals("")){
		    out.println("<input type=hidden name=own_name value=\""+
				own_name+"\">");
		}
		if(!name_opt.equals("")){
		    out.println("<input type=hidden name=name_opt value=\""+
				name_opt+"\">");
		}
		if(!own_addr.equals("")){
		    out.println("<input type=hidden name=own_addr value=\""+
				own_addr+"\">");
		}
		if(!own_addr_opt.equals("")){
		    out.println("<input type=hidden name=own_addr_opt value=\""+
				own_addr_opt+"\">");
		}
		if(!outputType.equals("")){
		    out.println("<input type=hidden name=outputType value=\""+
				outputType+"\">");
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
		//
		// this is mostly related to search engine and
		// some reports
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
		    "pd.nhood,pd.prop_type,pd.accessory_dwelling ";             // 21
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
		    qw += " and id.id=pd.id ";
	    
		if(wherecases.size()>0){
		    for (int c = 0; c < wherecases.size(); c++){
			qw += " and ";
			qw += wherecases.elementAt(c);
		    }
		}
		String qs ="";
		if(report.startsWith("pullR")){
		    qw += " and pd.inactive is null ";
		    if(order_by.equals("") || order_by.equals("pull_r")){
			qs = " order by pr.pull_text,ad.street_name,ad."+
			    "street_dir,lpad(ad.street_num,6,'0') "+sortby; // ASC 
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
			    "street_dir,lpad(ad.street_num,6,'0') "+
			    sortby; // ASC 
		    }
		}
		else if(report.equals("pull")){
		    // 
		    if(order_by.equals("") || order_by.equals("pull_d")){
			qs = " order by pd.pull_date, ";
			qs += " pr.pull_text,ad.street_name,ad.street_dir,"+
			    "lpad(ad.street_num,6,'0') "+sortby; // ASC 
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
			    "street_dir,lpad(ad.street_num,6,'0') "+sortby; // ASC 
		    }
		}
		else if(order_by.equals("pull_r")){
		    qs = " order by pr.pull_text,ad.street_name,ad."+
			"street_dir,lpad(ad.street_num,6,'0') "+sortby; // ASC 
		}
		else if(order_by.equals("pull_d")){
		    qs = " order by pd.pull_date, ";
		    qs += " pr.pull_text,ad.street_name,ad."+
			"street_dir,lpad(ad.street_num,6,'0') "+sortby; // ASC 
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
		    if(showOut.equals("Basics")){
			if(!report.equals(""))
			    out.println("<table cellpadding=5 cellspacing=2>");
			else
			    out.println("<table border cellpadding=5 "+
					"cellspacing=2>");
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
				out.println("<tr bgcolor=#e0e0e0>");
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
		    else{ // detailed
			out.println("<table>");
			String that = "";
			while(rs.next()){
			    that = rs.getString(3);  // owner
			    if(that != null)
				out.println("<tr><td colspan=2 align=center>"+
					    "<font size=+1>"+
					    that+"</td></tr>");
			    for (int c = 0; c < titles.length; c++){ 
				that = rs.getString(c+1); 
				if(that == null) continue; 
				that = that.trim();
				if(c == 0){
				    if(that.equals("0")) that ="";
				    else {
					that = "<a href="+url+
					    "Rental?"+
					    "&id="+that+"&action=zoom>"+that+
					    "</a>";
				    }
				}
				else if(c == 1){
				    if(that.trim().equals("0")) that ="";
				    else {
					that = "<a href="+url+
					    "OwnerServ?"+
					    "&name_num="+that+"&action=zoom>"+
					    that+"</a>";
				    }
				}
				else if(c == 2){
				    if(that.trim().startsWith("NO ")) that ="";
				}
				else if(c == 3){
				    if(that.trim().equals("0")) that ="";
				    else {
					that = "<a href="+url+
					    "OwnerServ?"+
					    "&name_num="+that+
					    "&action=zoom&type=agent>"+
					    that+"</a>";
				    }
				}
				else if(c == 16){ // units/structures
				    if(that.trim().equals("/")) 
					that ="";
				}
				if(!that.equals(""))
				    printItem(out, titles[c], that);
			    }				
			    out.println("</table>");
			}
		    }
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
    void showError(PrintWriter out, String msg){
	out.println("<html><head><title>Rental </title></head><body>");			
	out.println("<center><br />");		
	if(!msg.equals("")){
	    out.println("<p> Error; "+msg+"</p>");
	}
	out.println("</center></body>");
	out.println("</html>");
	out.close();
    }
    //
    // make 2d arrays from the subcats
    //
    /**
     * Outputs a pair of title, item arranged in a table.
     * @param out output stream
     * @param title
     * @param that the item content
     */
    void  printItem(PrintWriter out,String title, String that){
	out.println("<tr><td align=right valign=top><b>"+title+" :</b> ");
	out.println("</td><td>"+that+"</td></tr>");
    }

}






















































