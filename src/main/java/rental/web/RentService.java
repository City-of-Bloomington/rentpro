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

@WebServlet(urlPatterns = {"/RentService"})
public class RentService extends TopServlet {

    final static long serialVersionUID = 850L;
    int maxlimit = 100; // limit on records
    static Logger logger = LogManager.getLogger(RentService.class);
    //
    // Global sharable arrays
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
	boolean success = true;
	//
	PrintWriter out = res.getWriter();
	String name, value;
	String action="", message="", type="", streetAddress="";

	String [] vals;

	List<Rent> rents = null;
	RentList rl = new RentList(debug);
	Enumeration<String> values = req.getParameterNames();
	boolean gotParam = false;
	while (values.hasMoreElements()){

       	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();
	   
	    if (name.equals("dateFrom")) {
		rl.setDate_from(value);
		gotParam = true;
	    }
	    else if (name.equals("dateTo")) {
		rl.setDate_to(value);
		gotParam = true;
	    }
	    else if (name.equals("streetAddress")) {
		rl.setStreetAddress(value);
		gotParam = true;
	    }
	    else if (name.equals("whichDate")) {
		rl.setWhichDate(value);
	    }	
	    else if (name.equals("zoning")) {
		rl.setZoning(value);
		gotParam = true;
	    }
	    else if (name.equals("units")){
		rl.setUnits(value);
		gotParam = true;
	    }
	    else if (name.equals("structures")){
		rl.setStructures(value);
		gotParam = true;
	    }
	    else if (name.equals("occ_load")){
		rl.setOcc_load(value);
		gotParam = true;
	    }
	    else if (name.equals("cdbg_funding")) {
		rl.setCdbg_funding(value);
		gotParam = true;
	    }
	    else if (name.equals("property_status")) {
		rl.setProperty_status(value);
		gotParam = true;
	    }
	    else if (name.equals("orderBy")) {
		rl.setOrderBy(value);
		gotParam = true;
	    }
	    else if (name.equals("nhood")) {
		rl.setNhood(value);
		gotParam = true;
	    }
	    else if (name.equals("name_num")) { // owner num
		rl.setName_num(value);
		gotParam = true;
	    }
	    else if (name.equals("pull_reason")) {
		rl.setPull_reason(value);
		gotParam = true;
	    }
	    else if (name.equals("street_num")) {
		rl.setStreet_num(value);
		gotParam = true;
	    }
	    else if (name.equals("street_number")) {
		rl.setStreet_num(value);
		gotParam = true;
	    }	
	    else if (name.equals("street_dir")) {
		rl.setStreet_dir(value);
		gotParam = true;
	    }
	    else if (name.equals("direction")) {
		rl.setStreet_dir(value);
		gotParam = true;
	    }		
	    else if (name.equals("street_name")) {
		rl.setStreet_name(value);
		gotParam = true;
	    }
	    else if (name.equals("street_type")) {
		rl.setStreet_type(value);
		gotParam = true;
	    }
	    else if (name.equals("post_dir")) {
		rl.setPost_dir(value);
		gotParam = true;
				
	    }
	    else if (name.equals("postDirection")) {
		rl.setPost_dir(value);
		gotParam = true;
	    }		
	    else if (name.equals("permit_length")) {
		rl.setPermit_length(value);
		gotParam = true;
	    }
	    else if (name.equals("sud_num")) {
		rl.setSud_num(value);
		gotParam = true;
	    }
	    else if (name.equals("subunitIdentifier")) {
		rl.setSud_num(value);
		gotParam = true;
	    }	
	    else if (name.equals("sud_type")) {
		rl.setSud_type(value);
		gotParam = true;
	    }
	    else if (name.equals("subunitType")) {
		rl.setSud_type(value);
		gotParam = true;
	    }	
	    else if (name.equals("invalid_addr")) {
		rl.setInvalid_addr(value);
		gotParam = true;
	    }
	    else if (name.equals("prop_type")) {
		rl.setProp_type(value);
		gotParam = true;
	    }
	    else if (name.equals("id")) {
		rl.setId(value);
		gotParam = true;
	    }
	    else if (name.equals("agent")) {
		rl.setAgentId(value);
		gotParam = true;
	    }
	    else if (name.equals("inactive")) {
		rl.setInactive(value);
		gotParam = true;
	    }
	    else if (name.equals("grandfathered")) {
		rl.setGrandfathered(value);
		gotParam = true;
	    }
	    else if (name.equals("city")) { // owner city
		rl.setCity(value);
		gotParam = true;
	    }
	    else if (name.equals("state")) { // owner state
		rl.setState(value);
		gotParam = true;
	    }
	    else if (name.equals("fullName")) { // owner fullname
		rl.setOwn_name(value);
		gotParam = true;
	    }
	    else if (name.equals("address")) { // owner address
		gotParam = true;
		rl.setOwn_addr(value);	
	    }
	    else if (name.equals("zip")) {  // owner zip
		rl.setZip(value);
		gotParam = true;
	    }
	    else if (name.equals("phone")) { // owner phone
		rl.setPhone(value);
		gotParam = true;
	    }
	    else if (name.equals("type")) {
		type = value.toLowerCase();					
	    }
	    else if (name.equals("format")) {
		type = value.toLowerCase();					
	    }		
			
	}
	if(gotParam){
	    String back = rl.lookFor();
	    if(back.equals("")){
		rents = rl.getRents();
	    }
	    else{
		success = false;
		message += back;
	    }
	}
	else{
	    String help = "";
	    success = false;
	    help = "<center>";
	    help += "<h2>Help: RentService Search Options</h2>";
	    help += "<p>You can use the following fields for search criterea\n";
	    help += "and specify type or format as html (default),xml or json</p>";
	    help += "<h2>Rented Property Address Related Fields</h2>";
	    help += "<table><tr><th>Field Name</th><th>Description</th>";
	    help += "<tr><td>street_num</td><td>Street Number</td></tr>";
	    help += "<tr><td>street_dir</td><td>Street Direction</t></tr>";
	    help += "<tr><td>street_name</td><td>Street Name</td></tr>";
	    help += "<tr><td>street_type</td><td>Street Type</td></tr>";
	    help += "<tr><td>post_dir</td><td>Post Direction</td></tr>";  
	    help += "<tr><td>sud_type</td><td>Subunit Type</td></tr>";
	    help += "<tr><td>sud_num</td><td>Subunit Identifier</td></tr>";
	    help += "<tr><td>streetAddress</td><td>Such as 401 N Morton St</td></tr>";
	    help += "</table>";
	    help += "<h2>Rental Permit Related Fields</h2>";
	    help += "<table><tr><th>Field Name</th><th>Description</th>";
	    help += "<tr><td>id</td><td>Rental (permit) ID</td></tr>";
	    help += "<tr><td>units</td><td>Number of Units in the Permit</td></tr>";
	    help += "<tr><td>structures</td><td>Number of Structures in the Permit</td></tr>";
	    help += "<tr><td>occ_load</td><td>Number of Occupant Load</td></tr>";
	    help += "<tr><td>prop_type</td><td>Property Type (H:House,C:Condo,A:Apartment,M:Mobile,R:Rooming House)</td></tr>";
	    help += "<tr><td>property_status</td><td>R:registered,V:vacant,O:Owner Occupied,C:Commercial,D:Drive by</td></tr>";
	    help += "<tr><td>permit_length</td><td>Permit Length (years)</td></tr>";
	    help += "<tr><td>whichDate</td><td>Select a date from: registered_date (defualt), pull_date, permit_issued, permit_expires</td></tr>";
	    help += "<tr><td>dateFrom</td><td>Start Date Range in (mm/dd/yyyy) format</td></tr>";
	    help += "<tr><td>dateTo</td><td>End Date Range in (mm/dd/yyyy) format</td></tr>";
	    help += "</table>";
	    help += "<h2>Rented Property Owner Related Fields</h2>";
	    help += "<table><tr><th>Field Name</th><th>Description</th>";
	    help += "<tr><td>fullName</td><td>Full Name or Business Name</td></tr>";
	    help += "<tr><td>address</td><td>Address </td></tr>";
	    help += "<tr><td>city</td><td>City</td></tr>";
	    help += "<tr><td>state</td><td>State</td></tr>";
	    help += "<tr><td>zip</td><td>Zipcode </td></tr>";
	    help += "<tr><td>phone</td><td>Phone</td></tr>";
	    help += "</table></center>";
	    help += "<br /><br />";
	    res.setContentType("text/html");
	    out.println("<html><head><title>Rental Info</title>");
	    out.println("</head>");
	    out.println("    <body>");
	    out.println(help);
	    out.println("    </body>");
	    out.println("</html>");
	    out.close();
	    return;
	}
	if(type.equals("xml")){
	    res.setContentType("text/xml");
	    if(!success){
		out.println("<message>\n");
		out.println(message+"\n");
		out.println("</message>\n");
		out.close();
		return;
	    }
	    writeXml(rents, out);
	}
	else if(type.equals("json")){
	    res.setContentType("application/json"); // application/json
	    if(!success){
		out.println("{ \"message\": ");
		out.println("    \""+message+"\"");
		out.println("}");			
		out.close();
		return;
	    }				
	    writeJson(rents, out);
	}
	else{
	    res.setContentType("text/html");
	    if(!success){
		out.println("<html><head><title>Rental Info</title></head>");
		out.println("    <body>");
		out.println("        <h2>Error Message</h2>");
		out.println("        <p>"+message+"</p>");
		out.println("    </body>");
		out.println("</html>");				
		return;
	    }
	    writeHtml(rents, out);
	}
    }
    /*
     * Writes output in HTML
     *
     * @param List 
     */
    void writeHtml(List<Rent> rents,
		   PrintWriter out)throws IOException 
    {
	out.println("<html><head><title>Rental Info</title></head>");
	out.println("<body>");
	if(rents == null || rents.size() == 0){
	    out.println("<p>No match found</p>");
	}
	else{
	    out.println("<p>Found "+rents.size()+" records </p>");
	    for(Rent rent:rents){
		out.println("<table>");			
		out.println("<tr><td>ID</td><td>"+rent.getId()+"</td></tr>");
		out.println("<tr><td>Structures/Units</td><td>"+rent.getStructures()+"/"+rent.getUnits()+"</td></tr>");
		out.println("<tr><td>Property Status</td><td>"+rent.getPropStatus().getName()+"</td></tr>");
		out.println("<tr><td>Property Type</td><td>"+rent.getProp_type_text()+"</td></tr>");
		out.println("<tr><td>Bedrooms </td><td>"+Helper.encodeForXML(rent.getBedrooms())+"</td></tr>");
		out.println("<tr><td>Occupant Load</td><td>"+Helper.encodeForXML(rent.getOcc_load())+"</td></tr>");
		out.println("<tr><td>Zone</td><td>"+rent.getZone().getName()+"</td></tr>");				
		out.println("<tr><td>Registered Date</td><td>"+rent.getRegistered_date()+"</td></tr>");
		out.println("<tr><td>Last Cycle Date</td><td>"+rent.getLast_cycle_date()+"</td></tr>");
		out.println("<tr><td>Pull Date</td><td>"+rent.getPull_date()+"</td></tr>");
		out.println("<tr><td>Pull Reason</td><td>"+rent.getPullReason().getName()+"</td></tr>");
		out.println("<tr><td>Permit Issue Date</td><td>"+rent.getPermit_issued()+"</td></tr>");
		out.println("<tr><td>Permit Expire Date</td><td>"+rent.getPermit_expires()+"</td></tr>");
		out.println("<tr><td>Grandfathered?</td><td>"+rent.getGrandfathered()+"</td></tr>");
		out.println("<tr><td>CDBG Funding</td><td>"+rent.getCdbg_funding()+"</td></tr>");
		List<Address> addrs = rent.getAddresses();
		if(addrs == null || addrs.size() == 0){
		    out.println("<tr><td>Address</td><td>No address</td></tr>");
		}
		else{
		    out.println("<tr><td colspan=2><table><caption>Address(es)</caption>");
		    out.println("<tr><th>ID</th><th>Address</th></tr>");
		    for(Address addr: addrs){
			out.println("<tr><td>"+addr.getId()+"</td><td>"+
				    addr.getAddress()+"</td></tr>");
		    }
		    out.println("</table></td></tr>");
		}
		Owner agent = rent.getAgent();
		if(agent != null){
		    out.println("<tr><td colspan=2><table><caption>Agent</caption>");
		    out.println("<tr><th>ID</th><th>Name</th><th>Address</th><th>Phone</th></tr>");
		    out.println("<tr><td>"+agent.getId()+"</td><td>"+
				Helper.encodeForXML(agent.getFullName())+
				"</td><td>"+
				Helper.encodeForXML(agent.getAddress())+"</td><td>"+
				Helper.encodeForXML(agent.getPhones())+"</td></tr>");
		    out.println("</table></td></tr>");
		}
		List<Owner> owners = rent.getOwners();
		if(owners == null || owners.size() == 0){
		    out.println("<tr><td>Owners</td><td>No owner</td></tr>");
		}
		else{
		    out.println("<tr><td colspan=2><table>");
		    if(owners.size() == 1)
			out.println("<caption>Owner</caption>");
		    else
			out.println("<caption>Owners</caption>");
		    out.println("<tr><th>ID</th>"+
				"<th>Name</th>"+
				"<th>Address</th>"+
				"<th>City, State Zip</th>"+
				"<th>Phone</th></tr>");					
		    for(Owner owner:owners){
			out.println("<tr><td>"+
				    owner.getId()+"</td><td>"+
				    Helper.encodeForXML(owner.getFullName())+
				    "</td><td>"+
				    Helper.encodeForXML(owner.getAddress())+
				    "</td><td>"+
				    owner.getCityStateZip()+"</td><td>"+
				    Helper.encodeForXML(owner.getPhones())+
				    "</td></tr>");
		    }
		    out.println("</table></td></tr>");					
		}
		out.println("</table>");			
	    }
	}
	out.println("</body></htmL>");
	out.close();
    }
    /*
     * Writes output in XML
     * @param List 
     */
    void writeXml(List<Rent> rents,
		  PrintWriter out)throws IOException {
	out.println("<?xml version='1.0' encoding='UTF-8'?>");
	out.println("<rentals>");
	if(rents == null || rents.size() == 0){
	    out.println("  <message>No match found</message>");
	}
	else{		
	    for(Rent rent:rents){
		out.println("  <rental id=\""+rent.getId()+"\">");
		out.println("    <Structures-Units>"+rent.getStructures()+"/"+rent.getUnits()+"</Structures-Units>");
		out.println("    <Property-Status>"+rent.getPropStatus().getName()+"</Property-Status>");
		out.println("    <Property-Type>"+rent.getProp_type_text()+"</Property-Type>");
		out.println("    <Bedrooms>"+Helper.encodeForXML(rent.getBedrooms())+"</Bedrooms>");
		out.println("    <Occupant-Load>"+Helper.encodeForXML(rent.getOcc_load())+"</Occupant-Load>");
		out.println("    <Zone>"+rent.getZone().getName()+"</Zone>");				
		out.println("    <Registered-Date>"+rent.getRegistered_date()+"</Registered-Date>");
		out.println("    <Last-Cycle-Date>"+rent.getLast_cycle_date()+"</Last-Cycle-Date>");
		out.println("    <Pull-Date>"+rent.getPull_date()+"</Pull-Date>");
		out.println("    <Pull-Reason>"+rent.getPullReason().getName()+"</Pull-Reason>");
		out.println("    <Issue-Date>"+rent.getPermit_issued()+"</Issue-Date>");
		out.println("    <Expire-Date>"+rent.getPermit_expires()+"</Expire-Date>");
		out.println("    <Grandfathered>"+rent.getGrandfathered()+"</Grandfathered>");
		out.println("    <CDBG-Funding>"+rent.getCdbg_funding()+"</CDBG-Funding>");
		List<Address> addrs = rent.getAddresses();
		if(addrs != null || addrs.size() > 0){
		    out.println("    <Addresses>");
		    for(Address addr: addrs){
			out.println("      <Address id=\""+addr.getId()+"\">"+
				    Helper.encodeForXML(addr.getAddress())+"</Address>");
		    }
		    out.println("    </Addresses>");
		}
		Owner agent = rent.getAgent();
		if(agent != null){
		    out.println("     <Agent id=\""+agent.getId()+"\">");
		    out.println("        <Name>"+Helper.encodeForXML(agent.getFullName())+"</Name>");
		    out.println("        <Address>"+Helper.encodeForXML(agent.getAddress())+"</Address>");
		    out.println("        <Phone>"+Helper.encodeForXML(agent.getPhones())+"</Phone>");
		    out.println("     </Agent>");
					
		}
		List<Owner> owners = rent.getOwners();
		if(owners != null || owners.size() > 0){
		    out.println("    <Owners>");

		    for(Owner owner:owners){
			out.println("      <Owner id=\""+owner.getId()+"\">");
			out.println("        <Name>"+Helper.encodeForXML(owner.getFullName())+"</Name>");
			out.println("        <Address>"+Helper.encodeForXML(owner.getAddress())+"</Address>");
			out.println("        <City-State-Zip>"+owner.getCityStateZip()+"</City-State-Zip>");
			out.println("        <Phone>"+Helper.encodeForXML(owner.getPhones())+"</Phone>");
			out.println("      </Owner>");
		    }
		    out.println("    </Owners>");					
		}
		out.println("  </rental>");			
	    }
	}
	out.println("</rentals>");
    }
    /*
     * Writes output in JSON
     * @param List 
     */	
    void writeJson(List<Rent> rents,
		   PrintWriter out)throws IOException {
	// out.println("<?xml version='1.0' encoding='UTF-8'?>");

	if(rents == null || rents.size() == 0){
	    out.println("{ \"rentals\": { ");			
	    out.println("  \"message\" : \"No match found\"");
	    out.println("    } ");
	    out.println("} ");						
	}
	else{
	    out.println("{ \"rentals\": { ");			
	    out.println("  \"rental\": [ ");
	    String addComma = ",";
	    int jj_rent = 1;
	    for(Rent rent:rents){
		out.println("    { "); 			
		out.println(" 	    \"id\": \""+rent.getId()+"\",");
		out.println("       \"Structures-Units\": \""+rent.getStructures()+"/"+rent.getUnits()+"\",");
		out.println("       \"Property-Status\": \""+rent.getPropStatus().getName()+"\",");
		out.println("       \"Property-Type\": \""+rent.getProp_type_text()+"\",");
		out.println("       \"Bedrooms\": \""+Helper.encodeForXML(rent.getBedrooms())+"\",");
		out.println("       \"Occupant-Load\": \""+Helper.encodeForXML(rent.getOcc_load())+"\",");
		out.println("       \"Zone\": \""+rent.getZone().getName()+"\",");				
		out.println("       \"Registered-Date\": \""+rent.getRegistered_date()+"\",");
		out.println("       \"Last-Cycle-Date\": \""+rent.getLast_cycle_date()+"\",");
		out.println("       \"Pull-Date\": \""+rent.getPull_date()+"\",");
		out.println("       \"Pull-Reason\": \""+rent.getPullReason().getName()+"\",");
		out.println("       \"Issue-Date\": \""+rent.getPermit_issued()+"\",");
		out.println("       \"Expire-Date\": \""+rent.getPermit_expires()+"\",");
		out.println("       \"Grandfathered\": \""+rent.getGrandfathered()+"\",");
		out.println("       \"CDBG-Funding\": \""+rent.getCdbg_funding()+"\",");
		List<Address> addrs = rent.getAddresses();
		if(addrs != null || addrs.size() > 0){
		    out.println("       \"Addresses\": {");
		    out.println("         \"Address\": [ ");
		    int jj = 1;
		    String str = ",";
		    for(Address addr: addrs){
			if(jj == addrs.size()) str = "";
			out.println("       { \"id\": \""+addr.getId()+"\",\"address\": \""+Helper.encodeForXML(addr.getAddress())+"\" }" + str);
			jj++;
		    }
					
		    out.println("      ] ");
		    out.println("    }, ");		
		}
		Owner agent = rent.getAgent();
		if(agent != null){
					
		    out.println("       \"Agent\": { ");
		    out.println("          \"id\": \""+agent.getId()+"\",");
		    out.println("          \"Name\": \""+Helper.encodeForXML(agent.getFullName())+"\",");
		    out.println("          \"Address\": \""+Helper.encodeForXML(agent.getAddress())+"\",");
		    out.println("          \"Phone\": \""+Helper.encodeForXML(agent.getPhones())+"\"");
		    out.println("       },");
					
		}
		List<Owner> owners = rent.getOwners();
		if(owners != null || owners.size() > 0){
		    out.println("       \"Owners\": {");
		    out.println("          \"Owner\": [ ");
		    int jj=1;
		    String str = ",";
		    for(Owner owner:owners){
			if(jj == owners.size()) str = "";
			out.print("        { \"id\": \""+owner.getId()+"\",");
			out.print(" \"Name\": \""+Helper.encodeForXML(owner.getFullName())+"\",");
			out.print(" \"Address\": \""+Helper.encodeForXML(owner.getAddress())+"\",");
			out.print(" \"City-State-Zip\": \""+owner.getCityStateZip()+"\",");
			out.print(" \"Phone\": \""+Helper.encodeForXML(owner.getPhones())+"\"");
			out.println(" }" + str);
			jj++;
		    }
		    out.println("      ]");
		    out.println("     }");				
		}
		if(jj_rent == rents.size()) addComma = "";
		out.println("   }"+addComma);
		jj_rent++;
	    }
	    out.println("  ] ");
	    out.println(" }");	
	}
	out.println("}");
    }

}






















































