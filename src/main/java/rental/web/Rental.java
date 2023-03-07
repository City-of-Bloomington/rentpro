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

@WebServlet(urlPatterns = {"/Rental"})
public class Rental extends TopServlet{

    final static long serialVersionUID = 860L;
    String mapUrl = "http://map.bloomington.in.gov:8080/servlet/GenaService";

    final static String bgcolor = "silver";// #bfbfbf gray
    static Logger logger = LogManager.getLogger(Rental.class);

    final static String[] varianceReqArr ={
	"",
	"An extension of time to complete repairs",
	"A modification or exception to the Housing Property Maintenance Code",
	"Relief from an administrative decision",
	"Rescind a variance"
    };
    final static String allOpts = "<option>"+
	"<option>is"+
	"<option>contains"+
	"<option>starts with"+
	"<option>ends with</select>";
    //
    static String[] allmonths = {"\n","JAN","FEB","MAR",
	"APR","MAY","JUN",
	"JUL","AUG","SEP",
	"OCT","NOV","DEC"};
    //
    static final String MONTH_SELECT = 
	"<option value=0>\n" + 
	"<option value=1>JAN\n" + 
	"<option value=2>FEB\n" + 
	"<option value=3>MAR\n" + 
	"<option value=4>APR\n" + 
	"<option value=5>MAY\n" + 
	"<option value=6>JUN\n" + 
	"<option value=7>JUL\n" + 
	"<option value=8>AUG\n" + 
	"<option value=9>SEP\n" + 
	"<option value=10>OCT\n" + 
	"<option value=11>NOV\n" + 
	"<option value=12>DEC\n" + 
	"</select>";
    //
    final static String allStreetType = 
	"<option value=\"\">\n "+
	"<option value=AVE>Avenue"+
	"<option value=BND>Bend"+
	"<option value=BLVD>Boulevard"+
	"<option value=BOW>Bow"+
	// "<option value=BYP>Bypass"+
	"<option value=CTR>Center"+
	"<option value=CIR>Circle"+
	"<option value=CT>Court"+
	"<option value=CRST>Crest"+
	"<option value=DR>Drive "+
	"<option value=EXPY>Expressway "+
	"<option value=KNL>Knoll"+
	"<option value=LN>Lane "+
	"<option value=PIKE>Pike " +
	"<option value=PKWY>Parkway " +
	"<option value=PL>Place " +
	"<option value=RD>Road "+
	"<option value=RDG>Ridge "+
	"<option value=RUN>Run "+
	"<option value=SQ>Square "+
	"<option value=ST>Street "+
	"<option value=TER>Terrace "+
	"<option value=TRL>Trail "+
	"<option value=TPKE>Turnpike "+
	"<option value=TURN>Turn "+
	"<option value=VLY>Valley "+
	"<option value=WAY>Way "+
	"</select>";

    final static String intArr[] ={"","1","2","3","4","5"};
    
    final static String allIntArr5 = 
	"<option>\n<option>1<option>2<option>3<option>4<option>5</select>";
    final static String allIntArr = 
	"<option>\n<option>1<option>2<option>3<option>4<option>5"+
	"<option>6<option>7<option>8<option>9<option>10<option>11"+
	"<option>12<option>13<option>14<option>15<option>16<option>17"+
	"<option>18<option>19<option>20<option>21<option>22<option>23"+
	"<option>24</select>";

    final static String sudKeys[] = { 
	"APT", "BSMT", "BLDG", "FL", 
	"LOT", "LOWR","RM", "SPC", "STE",
	"TRLR","UNIT","UPPR"};
    final static String sudInfo[] = {
		
	"Apartment",
	"Basement",
	"Building",
	"Floor",
	"Lot",
	"Lower",
	"Room",
	"Space",
	"Suite",
	"Trailer",
	"Unit",
	"Upper"
    };
    final static String allSudTypes =
	"<option value=\"\">\n"+
	"<option value=\"APT\">Apartment\n"+
	"<option value=\"BSMT\">Basement\n"+
	"<option value=\"BLDG\">Building\n"+
	"<option value=\"FL\">Floor\n"+		
	"<option value=\"LOT\">Lot\n"+
	"<option value=\"LOWR\">Lower\n"+		
	"<option value=\"RM\">Room\n"+
	"<option value=\"SPC\">Space\n"+
	"<option value=\"STE\">Suite\n"+
	"<option value=\"TRLR\">Trailer\n"+
	"<option value=\"UNIT\">Unit\n"+
	"<option value=\"UPPR\">Upper\n"+	
	"</select>";

    //
    final static String streetKeys[] = {
	"AVE", "BND", "BLVD", "BOW",
	"CTR","CIR", "CT", "CRST",
	"DR",  "EXPY", "KNL","LN", "PIKE",
	"PKWY", "PL",  "RD", "RDG","RUN",   "ST", "TER", 
	"TRL","TPKE", "TURN","VLY","WAY"};
    //
    final static String streetInfo[] = {
	"Avenue","Bend", "Boulevard","Bow",
	"Center","Circle", "Court","Crest",
	"Drive", "Expressway", "Knoll","Lane", "Pike" ,
	"Parkway" ,"Place" ,"Road" ,"Ridge","Run","Street", "Terrace",
	"Trail","Turnpike","Turn","Valley","Way"};    
    //
    // N-Hood represents zones for inspectors
    final static String nhoodArr[] = {"","1","2","3","4","5","6","7",
	"8","9","10","11","12","13","14",
	"15"};
    //
    final static String allStreetDir = 
	"<option>\n"+
	"<option>N<option>S<option>E<option>W</select>";
    //
    final static String [] propTypes = {"\n",
	"House",
	"Apartment",
	"Condo",
	"Mobile",
	"Rooming House"}; 
    //
    // Global temporary shared objects
    //
    List<Zone> zones = null;
    List<Item> pulls = null;
    List<Item> pstats = null;
    /**
     * Generates the Rental Permit form and processes view, add, 
     * update and delete operations.
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
	String message = "";
	String name, value;
	boolean success = true;
	String invalid_addr="", inactive="", agent="", id="";

	String tag = ""; // for gis related links introduced July 2005
	String delagent="";
	// 
	String mapAddrStr = "global openDataset=ivy.dataset;geov.enqraw -image 700 700 \'\' findaddressptAT.qry \'background.layer handnbrzone.layer roads.layer railroads.layer building_fp.layer roadnametext.layer addresstext.layer barAndNorth.layer\' -attribute ";
	//
	boolean addrAval = false;
	//
	// Session info
	String role="";
	User user = null;
	HttpSession session = null;

	String action="", action2="";
	boolean	oldUnitFormat = false;
	Enumeration<String> values = req.getParameterNames();
	String [] vals;
	String [] ownerDelArr = null; // owners list to delete
	String [] addrDelArr = null; // addresses list to delete
	Rent rent = null;
	Address addr = null;
	PropStatus propStatus = null;
	PullReason pullReason = null;
	Zone zone = null;
	RentalNote rentalNote = null;
	PullHistory pullHistory = null;
	try{
	    rent = new Rent(debug);
	    addr = new Address(debug);
	    rentalNote = new RentalNote(debug);
	    pullHistory = new PullHistory(debug);
	}catch(Exception ex){
	    ex.printStackTrace();
	}

	while (values.hasMoreElements()){

	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = vals[vals.length-1].trim();	
	    if (name.equals("id")){
		id = value;
		rent.setId(id);
		rentalNote.setRental_id(id);
		pullHistory.setRental_id(id);
	    }
	    else if (name.equals("nhood")){
		rent.setNhood(value);
	    }
	    else if (name.equals("inactive")){
		rent.setInactive(value);
	    }
	    else if (name.equals("affordable")){
		rent.setAffordable(value);
	    }		
	    else if (name.equals("built_date")){
		rent.setBuilt_date(value);				
	    }
	    else if (name.equals("registered_date")){
		rent.setRegistered_date(value);
	    }
	    else if (name.equals("property_status")){
		rent.setProperty_status(value);
	    }
	    else if (name.equals("last_cycle_date")){
		rent.setLast_cycle_date(value);
	    }
	    else if (name.equals("bedrooms")){
		rent.setBedrooms(value);
	    }
	    else if (name.equals("bath_count")){
		rent.setBath_count(value);
	    }
	    else if (name.equals("occ_load")){
		rent.setOcc_load(value);
	    }
	    else if (name.equals("pull_date")){
		rent.setPull_date(value);
		pullHistory.setPull_date(value);
	    }
	    else if (name.equals("date_rec")){
		rent.setDate_rec(value);
	    }
	    else if (name.equals("date_billed")){
		rent.setDate_billed(value);
	    }
	    else if (name.equals("permit_issued")){
		rent.setPermit_issued(value);
	    }
	    else if (name.equals("permit_expires")){
		rent.setPermit_expires(value);
	    }
	    else if (name.equals("zoning")){
		rent.setZoning(value);
	    }
	    else if (name.equals("permit_length")){
		rent.setPermit_length(value);
	    }
	    else if (name.equals("units")){
		rent.setUnits(value);
	    }
	    else if (name.equals("structures")){
		rent.setStructures(value);
	    }
	    else if (name.equals("cdbg_funding")){
		rent.setCdbg_funding(value);
	    }
	    else if (name.equals("pull_reason")){
		rent.setPull_reason(value);
		pullHistory.setPull_reason(value);
	    }
	    else if (name.equals("prop_type")){
		rent.setProp_type(value);
	    }
	    else if (name.equals("building_type")){
		rent.setBuilding_type(value);
	    }	
	    else if (name.equals("grandfathered")){
		rent.setGrandfathered(value);
	    }
	    else if (name.equals("accessory_dwelling")){
		rent.setAccessory_dwelling(value);
	    }						
	    else if (name.equals("notes")){
		rent.setNotes(value);
		rentalNote.setNotes(value);
	    }
	    else if (name.equals("street_num")){
		addr.setStreet_num(value);
	    }
	    else if (name.equals("street_dir")){
		addr.setStreet_dir(value);
	    }
	    else if (name.equals("street_name")){
		addr.setStreet_name(value);
	    }
	    else if (name.equals("street_type")){
		addr.setStreet_type(value);
	    }
	    else if (name.equals("post_dir")){
		addr.setPost_dir(value);
	    }
	    else if (name.equals("sud_num")){
		addr.setSud_num(value);  
	    }
	    else if (name.equals("sud_type")){
		addr.setSud_type(value); 
	    }
	    else if (name.equals("own_del")){
		ownerDelArr = vals;
	    }
	    else if (name.equals("addr_del")){
		addrDelArr = vals;
	    }
	    else if (name.equals("agent")){
		rent.setAgentId(value);
	    }
	    else if (name.equals("tag")){
		tag = value.trim();
		addr.setTag(value.trim());
	    }
	    else if (name.equals("delagent")){
		delagent = value;
	    }
	    else if (name.equals("invalid_addr")){
		addr.setInvalid_addr(value);
	    }
	    else if (name.equals("action")){ 
		// Create, zoom, edit, delete, New
		action = value;  
	    }
	    else if (name.equals("action2")){ 
		// delete only
		action2 = value;
		if(!value.equals("")) // only in case of valid value
		    action = value;
	    }
	}
	if(!action2.equals("")) action = action2;
	//
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?source=Rental&action=zoom&id="+id;	
		res.sendRedirect(str);
		return;
	    }
	}
	else if(tag.equals("")){
	    String str = url+"Login?source=Rental&action=zoom&id="+id;			
	    res.sendRedirect(str);
	    return; 
	}
	//
	// in case we want to delete the agent
	//
	if(!delagent.equals("") && action.equals("Update")){
	    agent = "";
	    rent.deleteAgent(); // when run update will update DB
	}
	//
	String qq="";
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
	// if tag has a value, this means we were called from
	// GIS and we need to find the record id for the rental
	// permit 
	if(!tag.equals("")){
	    action = "zoom";
	    String back = addr.findRentalByTagNumber();
	    if(back.equals("")){
		id = addr.getRid();
		rent.setId(id);
	    }
	    else{
		message += back;
		success = false;
	    }
	    if(id.equals("")){
		message += "No rental found for this address";
	    }
	}
	//
	if(action.equals("Create")){
	    //
	    if(user.canEdit()){
		String str = rent.doSave();
		if(str.equals("")){
		    message = "Data Saved Successfully";
		    id = rent.getId();
		    rentalNote.setRental_id(id);
		    if(rentalNote.isValid()){
			rentalNote.setUserid(user.getUsername());
			str = rentalNote.doSave();
			if(!str.equals("")){
			    message += " Could not save notes "+str;
			}
		    }
		    pullHistory.setRental_id(id);
		    if(pullHistory.isValid()){
			pullHistory.setUserid(user.getUsername());
			str = pullHistory.doSave();
			if(!str.equals("")){
			    message += " Could not save pull history "+str;
			}
												
		    }
		}
		else{
		    message += str;
		    success = false;
		}
	    }
	}
	else if(action.equals("Update")){
	    //
	    if(user.canEdit() || user.isInspector()){
		String back = rent.doUpdate();
		if(back.equals("")){
		    message += "Updated Successfully";
		}
		else{
		    success = false;
		    message += "Could not update "+back;
		}
		if(rentalNote.isValid()){
		    rentalNote.setUserid(user.getUsername());
		    back = rentalNote.doSave();
		    if(!back.equals("")){
			message += " Could not save notes "+back;
		    }
		}
		if(pullHistory.isNew() && !pullHistory.hasErrors()){
		    pullHistory.setUserid(user.getUsername());
		    back = pullHistory.doSave();
		    if(!back.equals("")){
			message += " Could not save pull history "+back;
		    }										
		}
		//
		// deleting checked for delete owners
		// 
		if(ownerDelArr != null && ownerDelArr.length > 0){
		    //
		    if(user.canEdit()){
			back = rent.deleteOwners(ownerDelArr);
			if(!back.equals("")){
			    success = false;
			    message += "Could not delete owners "+back;
			}	
		    }
		}
		//
		// check if certain addresses are checked for deletion
		//				
		if(addrDelArr != null && addrDelArr.length > 0){
		    for(int i=0; i<addrDelArr.length; i++){
			Address adr = new Address(debug, addrDelArr[i]);
			back = adr.doDelete();
			if(!back.equals("")){
			    message += back;
			    success = false;
			}
		    }
		}				
	    }
	}
	else if(action.equals("Delete")){
	    //	    
	    // delete all other records related to this one
	    // in certain tables.
	    //
	    if(user.isAdmin()){
		String back = rent.doDelete();
		if(!back.equals("")){
		    message += back;
		    success = false;
		}
		else{
		    message += "Deleted Successfully";
		    id="";
		    rent = new Rent(debug);
		}
	    }
	    else{
		message += " You could not delete ";
	    }
	}
	else if(!id.equals("")){
	    String back = rent.doSelect();
	    if(!back.equals("")){
		message = "No permit found for ID = "+id+" "+back;
		success = false;
		id="";
		action="";
	    }
	    back = rent.findStats();
	}
	else if(action.startsWith("New")){	
	    //
	    id="";
	}
	if(action.equals("Create") || action.equals("Update")){
	    if(addr.isNew() && !id.equals("")){
		//
		// Check the address valid or not
		//
		addr.setRid(id);
		if(!addr.hasMasterAddressInfo(checkAddrUrl)){
		    invalid_addr = "checked";
		    addr.setInvalid_addr("Y");
		    addrAval = true;
		}
		else{
		    invalid_addr = "";
		    addr.setInvalid_addr("");
		}
		String back = addr.doSave();
		if(!back.equals("")){
		    message += " could not save address "+back;
		    logger.error(message);
		    success = false;
		}
	    }
	}
	List<Address> addresses = null;
	if(!id.equals("")){
	    AddressList al = new AddressList(debug, id);
	    String back = al.lookFor();
	    if(back.equals("")){
		addresses = al.getAddresses();
	    }
	    else{
		message += " "+back;
		logger.error(message);
		success = false;
	    }
	    if(addresses != null && addresses.size() > 0){
		Address adr = addresses.get(0);
		if(adr != null){
		    //
		    // We need this info for the map button and interface
		    //
		    if(!adr.getStreet_name().equals("")){
			addrAval = true;
			boolean in = false;
			if(!adr.getStreet_num().equals("")){
			    mapAddrStr += "NUMBER is \'"+adr.getStreet_num()+"\' ";
			    in = true;
			}
			if(!adr.getStreet_dir().equals("")){
			    if(in) mapAddrStr += " AND ";
			    mapAddrStr += "DIR starts \'"+adr.getStreet_dir()+"\' ";
			    in = true;
			}
			if(!adr.getStreet_name().equals("")){
			    if(in) mapAddrStr += " AND ";
			    mapAddrStr += "NAME contains \'"+adr.getStreet_name()+"\' ";
			    in = true;
			}
			if(!adr.getStreet_type().equals("")){
			    if(in) mapAddrStr += " AND ";
			    mapAddrStr += "SUFFIX contains \'"+adr.getStreet_type()+"\' ";
			    in = true;
			}
		    }
		}
	    }
	}
	if(rent.isInactive()){
	    inactive = "checked";
	}
	// leave the edit mode
	if(success && (action.equals("Create") || action.equals("Update"))){
	    action = "zoom";
	}
	//
	// This condition is relaxed for the request that will come
	// through GIS using the flag 'tag' as our indicator 
	//
	out.println("<html><head><title>Rental</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type='text/javascript'>");
	out.println("/*<![CDATA[*/");		
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
	out.println("  function validateForm(){		                   ");
	if(!action.equals("zoom")){
	    out.println("  if ((document.myForm.notes.value.length > 1000)){ "); 
	    out.println("     alert(\"You have entered \" + document.myForm.notes.value.length + \" characters in the notes field. Maximum characters allowed are 1000\");		");
	    out.println("  	document.myForm.notes.value = document.myForm.notes.value.substring(0,1000);         ");
	    out.println("    return false;			    ");
	    out.println("	}				            ");
	    out.println(" if(!checkDate(document.myForm.registered_date.value)){");
	    out.println("	      alert(\"Invalid Date \");             ");
	    out.println("         document.myForm.registered_date.focus();  ");
	    out.println("         return false;			            ");
	    out.println("  	  }					                    ");
	    //
	    out.println("   if (!checkDate(document.myForm.last_cycle_date.value)){");
	    out.println("	      alert(\"Invalid Date \");             ");
	    out.println("         document.myForm.last_cycle_date.focus();  ");
	    out.println("         return false;			            ");
	    out.println("  	  }					    ");
	    out.println("  if(!checkDate(document.myForm.permit_issued.value)){");
	    out.println("	      alert(\"Invalid Date \");             ");
	    out.println("         document.myForm.permit_issued.focus();    ");
	    out.println("         return false;			            ");
	    out.println("  	  }					                    ");
	    out.println("  if(!checkDate(document.myForm.permit_expires.value)){");
	    out.println("	      alert(\"Invalid Date \");             ");
	    out.println("         document.myForm.permit_expires.focus();   ");
	    out.println("         return false;			            ");
	    out.println("  	  }					    ");
	    out.println("  if(!checkDate(document.myForm.pull_date.value)){ ");
	    out.println("	      alert(\"Invalid Date \");             ");
	    out.println("         document.myForm.pull_date.focus();        ");
	    out.println("         return false;			            ");
	    out.println("  	  }					    ");
	    out.println("  if(!checkDate(document.myForm.date_billed.value)){");
	    out.println("	      alert(\"Invalid Date \");             ");
	    out.println("         document.myForm.date_billed.focus();      ");
	    out.println("         return false;			            ");
	    out.println("  	  }					    ");
	    out.println("  if(!checkDate(document.myForm.date_rec.value)){  ");
	    out.println("	      alert(\"Invalid Date \");           ");
	    out.println("         document.myForm.date_rec.focus();       ");
	    out.println("         return false;			          ");
	    out.println("  	  }					  ");
	    out.println("if(!checkDate(document.myForm.built_date.value)){ ");
	    out.println("	      alert(\"Invalid Date \");           ");
	    out.println("         document.myForm.built_date.focus();     ");
	    out.println("         return false;			          ");
	    out.println("  	  }					  ");
	    out.println(" var pid = document.myForm.prop_type.selectedIndex;");
	    out.println(" var prop = document.myForm.prop_type.options[pid].text; ");

	    out.println(" if(prop && prop.indexOf('Room') > -1){         ");
	    out.println(" var baths = document.myForm.bath_count.value;  ");
	    out.println("  if(baths.length == 0){                        ");
	    out.println("	      alert('Rooming House Bathrooms count is required ');           ");
	    out.println("         return false;			          ");
	    out.println("  	  }}					  ");
	}
	out.println("     return true;					  ");
       	out.println("	}	         				      ");
	out.println("  function validateDelete(){	                  ");
	out.println("   var x = false;                                    ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     if(x){                                          ");
	out.println("  	  document.myForm.action2.value=\"Delete\";       ");
	out.println("     document.myForm.submit();                       ");
	out.println("	}					          ");
	out.println("     return x;                                       ");
	out.println("	}					          ");
	out.println("  function checkNavigator(){		          ");
	out.println("  var appl = navigator.appName;                      ");
	out.println("	      alert(appl);           ");
	out.println("   if(appl.substr(0,1) != \"M\") { // Microsoft      ");
	out.println("	      alert(\"MS Internet Explorer is the only compatbile browser for writing or editing inspection reports\");           ");
	out.println("         return false; }			          ");
	out.println("         return true; }			          ");
	if(id.equals("")){
	    out.println("  function firstFocus(){                           ");
	    out.println("     if(document.myForm.street_num){               ");
	    out.println("     document.myForm.street_num.focus();           ");
	    out.println("	}}			       		              ");
	}
	else{
	    out.println("  function firstFocus(){                        ");
	    out.println("     if(document.myForm.notes){                 ");
	    out.println("     document.myForm.notes.focus();             ");
	    out.println("	}}			       		              ");
	}
	out.println("/*]]>*/\n");	
	out.println(" </script>				                  ");
	out.println(" </head><body onload=\"firstFocus()\" >  ");
	out.println(" <center>");
	Helper.writeTopMenu(out, url);
	if(!message.equals("")){
	    if(success)
		out.println("<h3>"+message+"</h3>");
	    else
		out.println("<p><font color='red'>"+message+"</font></p>");
	}
	// 
	if(id.equals("")){
	    out.println("<h2>New Rental Registration</h2>");
	}
	else{
	    if(action.equals("zoom"))
		out.println("<h2>View Rental "+id+"</h2>");
	    else{
		out.println("<h2>Edit Rental "+id+"</h2>");
		out.println("Note: To remove an owner from a property, mark the chackbox infront of the owner and click on 'Update'.<br />");

	    }
	}
	if(!invalid_addr.equals(""))
	    out.println("<h4><font color=\"red\">Invalid Address, "+
			"please verify location entries.</font></h4>");
	if(!id.equals("") && !rent.hasUpdatedUnits()){
	    out.println("<p><font color=\"red\">The units, structures, bedrooms in this record need to be updated according to the new setting </font></p>");
	    oldUnitFormat = true;
	}
	//
	boolean avgReported = false;
	//
	if(action.equals("zoom")){
	    out.println("<form name=\"myForm\" method=\"post\">");
	}
	else{
	    out.println("<form name=\"myForm\" method=\"post\" "+
			"onSubmit=\"return validateForm()\">");
	}
	if(!id.equals("")){
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	}
	out.println("<table border=\"1\">");
	if(action.equals("zoom")){
	    //
	    // Address Block
	    out.println("<tr><td><table><tr><td><b>Address:</b> "+
			"</td></tr>");
	    if(addresses != null && addresses.size() > 0){
		for(Address adr: addresses){
		    out.println("<tr><td>");					
		    if(adr.isInvalid()){
			out.println("<font color=\"red\">");
		    }
		    out.println(adr.getAddress());
		    if(adr.isInvalid()){
			out.println("</font>");
		    }
		    out.println("&nbsp;&nbsp;");							
		    if(!adr.isInvalid()){
			adr.getMasterAddrInfo(checkAddrUrl);
			String lat = adr.getLat();
			String lng = adr.getLng();
			if(!lat.equals("")){
			    out.println("<a href=\""+url+"ShowMap?"+
					"rid="+id+"&id="+adr.getId()+
					"&lat="+lat+"&lng="+lng);
			    out.println("\" target=\"_blank\">Show on Google Map</a>");	
			}
		    }
		    out.println("</td></tr>");    
		}
		addrAval = true;
	    }
	    out.println("</table></td></tr>"); // end address table
	    //
	    // Owners block
	    //
	    // Get the list of owners if this is 
	    // not a new record 
	    if(!id.equals("")){
		out.println("<tr><td><table width=\"100%\">");//two colormns
		out.println("<tr><td valign=\"top\"><table>");//owners table
		out.println("<tr><td><b>Owner(s) </b></td></tr>");				
		List<Owner> owners = rent.getOwners();
		if(owners != null && owners.size() > 0){
		    for(Owner ownr:owners){
			out.println("<tr><td>"+
				    "<a href=\""+url+"OwnerServ?"+
				    "&name_num="+ownr.getId()+"&action=zoom"+
				    "&tag="+tag+"&id="+id+"\">"+
				    ownr.getFullName()+"</a></td></tr>");
		    }
		}
		out.println("</td></tr></table>");
		out.println("</td><td valign=\"top\">");
		out.println("<table><tr><td>"); // agent table
		//
		out.println("<b>Agent:</b></td></tr><tr><td valign=top>");
		//				
		// Agent
		if(rent.hasAgent()){
		    Owner agnt = rent.getAgent();
		    out.println("<a href=\""+url+							
				"OwnerServ?name_num="+agnt.getId()+
				"&id="+id+"&tag="+tag+
				"&action=zoom&type=agent\">"+
				agnt.getFullName()+"</a>");
		}
		else{
		    out.println("No Agent");
		}
		out.println("</td></tr></table></td></tr>"); // end agent tbl
		out.println("</table></td></tr>"); // end owners table
	    }
	    // 
	    // 3rd block
	    // row 1 
	    out.println("<tr><td align=\"center\">");
	    out.println("<table border=\"1\" width=\"100%\">");
	    out.println("<tr><td align=\"right\"><b>Registration&nbsp;Date:"+
			"</b></td><td>");
	    out.println(rent.getRegistered_date()+"</td>"+
			"<td align=\"right\">");
	    out.println("<b>Property&nbsp;Status:</b></td><td>");
	    propStatus = rent.getPropStatus();
	    if(propStatus != null){
		out.println(propStatus.getName());
	    }
	    out.println("</td></tr>");
	    // 
	    // row 2
	    out.println("<tr><td align=\"right\"><b>Last&nbsp;"+
			"Cycle&nbsp;Date:</b>");
	    out.println("</td><td><span id=\"l_cycle\">&nbsp;"+
			rent.getLast_cycle_date()+"</span></td>");
	    out.println("<td align=\"right\"><b>Structures/Units:</b></td><td>");
	    out.println(rent.getStructures()+"/"+rent.getUnits());
	    out.println("</td></tr>");
	    //
	    // row 3
	    out.println("<tr><td align=\"right\"><b>Permit Issued:</b></td><td>");
	    out.println(rent.getPermit_issued()+
			"&nbsp;</td><td align=\"right\"><b>Units/Beds/O. Load:</b>");
	    out.println("</td><td>");
	    if(oldUnitFormat){		
		out.println(rent.getOldOcc_load()+"&nbsp;</td></tr>");
	    }
	    else{
		out.println(rent.getOcc_load()+"&nbsp;</td></tr>");
	    }
	    //
	    // row 4
	    out.println("<tr><td align=\"right\" valign=\"top\"><b>"+
			"Permit Expires:</b>");
	    out.println("</td><td valign=\"top\">"
			+rent.getPermit_expires());
	    out.println("&nbsp;</td>");
	    if(oldUnitFormat){
		out.println("<td align=\"right\"><b>Bedrooms:</b></td><td>");
		out.println(rent.getOldBedrooms());
	    }
	    else{
		avgReported = true;
		out.println("<td align=\"right\"><b>Average Inspection time:</b></td><td>"+rent.getAverageTime());
	    }
	    out.println("&nbsp;</td></tr>");
	    //
	    // row 5
	    out.println("<tr><td align=\"right\"><b>Permit Length:</b></td><td>");
	    out.println(rent.getPermit_length());
	    out.println("&nbsp;</td><td align=\"right\">");
			
	    out.println("<b>Bathrooms:</b></td><td>");
	    out.println(rent.getBath_count()+"&nbsp;</td></tr>");
	    // 
	    // row 6
	    out.println("<tr><td align=\"right\"><b>Pull Date:</b></td><td>");
	    out.println(rent.getPull_date()+
			"&nbsp;</td><td align=\"right\"><b>Pull Reason:</b>"+
			"</td><td>&nbsp;");
	    pullReason = rent.getPullReason();
	    out.println(pullReason.getName());
	    out.println("</td></tr>");
	    //
	    out.println("<tr><td align=\"right\"><b>Received Date:</b></td><td>");
	    out.println(rent.getDate_rec()+"&nbsp;</td>");
	    //
	    out.println("<td align=\"right\"><b>Zoning:</b></td><td>");
	    zone = rent.getZone();
	    out.println(zone.getName());
	    out.println("&nbsp;</td></tr>");
	    //
	    out.println("<tr><td align=\"right\"><b>Date Billed:</b></td>");
	    out.println("<td>"+rent.getDate_billed()+"&nbsp;</td>");
	    if(!rent.getCdbg_funding().equals("")){
		out.println("<td><b>CDBG Funding? </b>Yes</td><td>&nbsp;</td>");
	    }
	    else{
		if(!avgReported)
		    out.println("<td><b>Average Inspection Time:</b></td><td>"+rent.getAverageTime()+"</td>");
		else
		    out.println("<td>&nbsp;</td><td>&nbsp;</td>");
	    }
	    out.println("</tr>");
	    out.println("<tr><td align=\"right\">");
	    out.println("<b>Property Type:</b></td><td>");
	    out.println(rent.getProp_type());
	    out.println("&nbsp;</td><td>");
	    if(!rent.getGrandfathered().isEmpty() && rent.getGrandfathered().equals("Y")){
		out.println(" <b>Grandfathered?</b>Yes");
	    }
	    if(!rent.getAccessory_dwelling().equals("")){
		out.println(" <b>Accessory Dwelling?</b>Yes");
	    }						
	    out.println("&nbsp;</td><td> <b>&nbsp;&nbsp;N-Hood:&nbsp;</b>");
	    out.println(rent.getNhood());
	    out.println("</td></tr>");
	    out.println("<td align=\"right\"><b>Building Type:</b></td><td>");
	    out.println(rent.getBuilding_type());
	    out.println("&nbsp;</td><td> <b>Affordable Housing</b></td><td>");
	    out.println((rent.getAffordable().equals(""))?"":"Yes");
	    out.println("&nbsp;</td></tr>");
	    //
	    out.println("<tr><td align=\"right\">");
	    out.println("<b>Construction Date:</b></td><td>");
	    out.println(rent.getBuilt_date()+"&nbsp;</td>");
	    if(!inactive.equals("")){
		out.println("<td colspan=\"2\"><b>This Permit is Inactive </b>");
		out.println("<input type=\"hidden\" name=\"inactive\" value=\"y\" />");
		out.println("</td>");
	    }
	    else{
		out.println("<td colspan=\"2\">&nbsp;</td></tr>");
	    }
	    out.println("</table></td></tr>");
	    //
	    // 4th block
	    // buttons
	    //
	    /**
	    if(tag.equals("") && (user.canEdit() ||
				  user.isInspector())){
		out.println("<tr><td valign=\"top\" align=\"right\">");
		out.println("<input type=\"submit\" name=\"action\" "+
			    "value=\" Edit \" />&nbsp;&nbsp;");
		out.println("</td></tr>");
	    }
	    */
	}
	else{
	    //
	    // Address Block
	    out.println("<tr><td><table width=\"100%\">");
	    out.println("<tr><td valign=\"bottom\"><b>Address </b><br />"+
			"Street Num</td>"+
			"<td valign=\"bottom\">Dir</td><td valign=\"bottom\">"+
			"Street Name");
	    out.println("</td><td valign=\"bottom\">St. Type</td>"+
			"<td valign=\"bottom\">Post Dir</td>"+
			"<td valign=\"bottom\">"+
			"SUD type</td><td valign=\"bottom\">SUD<br /> number"+
			"</td></tr>");
	    // 
	    out.println("<tr><td><input type=\"text\" name=\"street_num\" "+
			"maxlength=\"8\" size=\"8\" /></td><td>");
	    out.println("<select name=\"street_dir\">");
	    out.println(allStreetDir+"</td><td>");
	    out.println("<input type=\"text\" name=\"street_name\" maxlength=\"30\" "+
			"size=\"15\" />");
	    out.println("</td><td>");
	    out.println("<select name=\"street_type\">");
	    out.println(allStreetType+"</td><td>");	    
	    out.println("<select name=\"post_dir\">");
	    out.println(allStreetDir+"</td><td>");
	    out.println("<select name=\"sud_type\">");
	    out.println(allSudTypes+"</td><td>");
	    out.println("<input type=\"text\" name=\"sud_num\" maxlength=\"4\" size=\"4\" />");
	    out.println("</td></tr>");
	    // 
	    // get the list of other addresses if this is 
	    // not a new record units
	    boolean in = false;
	    if(!id.equals("")){
		if(addresses != null && addresses.size() > 0){
		    addrAval = true;					
		    for(Address adr: addresses){
			String str = adr.getId();
			String str2 = adr.getAddress();
			if(str != null){
			    out.println("<tr><td align=\"right\">"+
					"<input type=\"checkbox\" "+
					"name=\"addr_del\" "+ 
					" value=\""+str+"\" />"+
					"<font color=\"green\">*</font>"+
					"</td><td colspan=\"6\">");
			    if(adr.isInvalid()){
				out.println("<font color=\"red\">");
			    }
			    out.println(str2);
			    out.println("</font>");
			    out.println("&nbsp;&nbsp;");
			    if(adr.isInvalid()){
				out.print("<a href=\""+url+
					  "AddressEdit?id="+str+
					  "&action=zoom&registr_id="+id);
				out.println("\">Edit Address</a>");
			    }
			    else{
				adr.getMasterAddrInfo(checkAddrUrl);
				String lat = adr.getLat();
				String lng = adr.getLng();
				if(!lat.equals("")){
				    out.print("<a href=\""+url+"ShowMap?"+
					      "rid="+id+
					      "&id="+adr.getId()+
					      "&lat="+lat+
					      "&lng="+lng);
				    out.println("\" target=\"_blank\">Show on Google Map</a>");	
				}
			    }
			    out.println("</td></tr>");    
			}
		    }
		}
	    }
	    out.println("</table></td></tr>");
	    //
	    // owner block
	    //
	    // owners
	    // get the list of owners if this is 
	    // an old record
	    if(!id.equals("")){
		out.println("<tr><td><table width=\"100%\">");//two colormns
		out.println("<tr><td valign=\"top\"><table>");  //  owners table
		out.println("<tr><td><b>Owner(s) </b></td></tr>");
		List<Owner> owners = rent.getOwners();
		if(owners != null && owners.size() > 0){
		    for(Owner ownr:owners){
			out.println("<tr><td align=\"right\" valign=\"top\">"+
				    "<input type=\"checkbox\" "+
				    "name=\"own_del\""+ 
				    " value=\""+ownr.getId()+"\" />"+
				    "<font color=\"green\">*</font></td><td>"+
				    "<a href=\""+url+"OwnerServ?"+
				    "&tag="+tag+
				    "&name_num="+ownr.getId()+"&action=zoom"+
				    "&id="+id+"\">"+
				    ownr.getFullName()+"</a></td></tr>");
			in=true;
		    }
		}
		out.println("</td></tr></table>");
		out.println("</td><td valign=\"top\">");
		out.println("<table><tr><td>"); // agent table
		//
		// agent
		Owner agnt = rent.getAgent();
		if(agnt != null){
		    out.println("<b>Agent:</b></td></tr><tr><td valign=\"top\">");
		    out.println("<input type=\"checkbox\" name=\"delagent\" "+
				"value=\"y\">"+
				"<font color=\"green\">*</font>"+
				"<a href=\""+url+
				"OwnerServ?name_num="+agnt.getId()+
				"&id="+id+
				"&tag="+tag+
				"&action=zoom&type=agent\">"+
				agnt.getFullName()+"</a>");
		    out.println("<input type=\"hidden\" name=\"agent\" value=\""+
				agnt.getId()+"\" />");
		}
		out.println("</td></tr></table></td></tr>");
		out.println("</table></td></tr>");
		//
	    }
	    // 
	    // 3rd block
	    // row 1 
	    out.println("<tr><td>");
	    out.println("<table width=\"100%\" border=\"1\">");
	    out.println("<tr><td align=\"right\"><b>Registration&nbsp;Date:</b>"+
			"</td><td>");
	    out.println("<input name=\"registered_date\" maxlength=\"10\" "+
			"id=\"registered_date\" class=\"date\" "+
			"size=\"10\" value=\""+rent.getRegistered_date()+"\" />");
	    out.println("</td><td>");		
	    out.println("<b>Property&nbsp;Status:</b>");
	    propStatus = rent.getPropStatus();
	    out.println("<select name=\"property_status\">");
	    out.println("<option value=\"\"></option>");
	    if(pstats != null){
		for(Item pstat:pstats){
		    String selected = pstat.getId().equals(propStatus.getId())?"selected=\"selected\"":"";
		    out.println("<option "+selected+" value=\""+pstat.getId()+"\">"+
				pstat.getName()+"</option>");
		}
	    }
	    out.println("</select>");
	    out.println("</td></tr>");
	    // 
	    // row 2
	    out.println("<tr><td align=\"right\"><b>Last&nbsp;Cycle&nbsp;"+
			"Date:</b>");
	    out.println("</td><td><input name=\"last_cycle_date\" size=\"10\""+
			" id=\"last_cycle_date\" class=\"date\" "+
			" maxlength=\"10\" value=\""+rent.getLast_cycle_date()+
			"\" /></td>");
	    //
	    out.println("<td>");
	    out.println("<b>Structures:</b>");
	    if(id.equals(""))
		out.println("<input name=structures size=\"4\" value=\""+rent.getStructures()+"\" maxlength=\"4\" />");
	    else
		out.println(rent.getStructures());
			
	    out.println("&nbsp;<b>Units:</b>");
	    out.println(rent.getUnits());
	    out.println("&nbsp;</td></tr>");
	    //			
	    // row 3
	    out.println("<tr><td align=\"right\"><b>Permit Issued:</b></td>");
	    out.println("<td>");
	    out.println("<input name=\"permit_issued\" size=\"10\" "+
			"id=\"permit_issued\" "+
			"maxlength=\"10\" value=\""+rent.getPermit_issued()+"\" />");
	    out.println("</td><td align=\"left\"><b>Units/Bedrooms/Occ Load:</b>");
	    if(oldUnitFormat){
		out.println(rent.getOldOcc_load()+"&nbsp;</td></tr>");
	    }
	    else{
		out.println(rent.getOcc_load()+"&nbsp;</td></tr>");
	    }
	    //
	    // row 
	    out.println("<tr><td align=\"right\" valign=\"top\"><b>"+
			"Permit Expires:</b>");
	    out.println("</td><td valign=\"top\">"+
			"<input name=\"permit_expires\" size=\"10\" "+
			"id=\"permit_expires\" "+
			"maxlength=\"10\" value=\""+rent.getPermit_expires()+
			"\" />");
	    if(oldUnitFormat){
		out.println("<td><b>Bedrooms:</b>");
		out.println(rent.getOldBedrooms()+"&nbsp;");
	    }
	    else{
		out.println("</td><td>&nbsp;</td>");
	    }
	    out.println("</td></tr>");
	    //
	    // row 
	    out.println("<tr><td align=\"right\"><b>Permit Length:</b></td><td>");
	    out.println("<select name=\"permit_length\">");
	    out.println("<option selected=\"selected\">"+rent.getPermit_length());
	    out.println(allIntArr5);
	    out.println("</td><td>");
	    out.println("<b>Bathrooms:</b>");
	    out.println("<input type=\"text\" name=\"bath_count\" maxlength=\"4\" "+
			"size=\"4\" value=\""+rent.getBath_count()+"\" />");
	    out.println("</td></tr>");
	    //
	    // row 6
	    out.println("<tr><td align=\"right\"><b>Pull Date:</b></td><td>");
	    out.println("<input name=\"pull_date\" size=\"10\" "+
			" maxlength=\"10\" id=\"pull_date\" class=\"date\" "+
			"value=\""+rent.getPull_date()+"\" />");
	    out.println("</td><td colspan=\"2\">"+
			"<b>Pull Reason:</b>");
	    out.println("<select name=\"pull_reason\">");
	    out.println("<option>\n</option>");
	    if(pulls != null){
		for(Item pull:pulls){
		    if(rent.getPull_reason().equals(pull.getId()))
			out.println("<option selected=\"selected\" value=\""+pull.getId()+
				    "\">"+pull.getName()+"</option>");
		    else
			out.println("<option value=\""+pull.getId()+
				    "\">"+pull.getName()+"</option>"); 
		}
	    }
	    out.println("</select>");
	    out.println("</td></tr>");
			
	    out.println("<tr><td align=\"right\"><b>Received Date:</b></td><td>");
	    out.println("<input name=\"date_rec\" size=\"10\" "+
			"id=\"date_rec\" class=\"date\" "+
			"maxlength=\"10\" value=\""+rent.getDate_rec()+"\" />");
	    out.println("</td>");
	    out.println("<td colspan=\"2\">");
	    out.println("<b>Zoning:</b>");
	    out.println("<select name=\"zoning\">");
	    out.println("<option></option>");
	    for(Zone zz:zones){
		if(rent.getZoning().equals(zz.getId()))
		    out.println("<option selected=\"selected\" value=\""+zz.getId()+"\">"+zz.getName()+"</option>");
		else
		    out.println("<option value=\""+zz.getId()+"\">"+zz.getName()+"</option>");
	    }
	    out.println("</td></tr>");
	    //
	    // row 7
	    out.println("<tr><td align=\"right\"><b>Date Billed:</b></td>");
	    out.println("<td>");
	    out.println("<input name=\"date_billed\" size=\"10\" "+
			"id=\"date_billed\" class=\"date\" "+
			"maxlength=\"10\" value=\""+rent.getDate_billed()+"\" />");
	    out.println("</td><td>");
	    out.println("<input type=\"checkbox\" name=\"cdbg_funding\" "+
			(rent.getCdbg_funding().isEmpty()?"":"checked=\"checked\"")+
			" value=\"Y\"><b>CDBG Funding </b>");
	    String checked = rent.getGrandfathered().equals("Y")?"checked=\"checked\"":"";
	    out.println("<input type=\"checkbox\" name=\"grandfathered\" "+checked+
			" value=\"Y\" /><b>Grand Fathered </b> ");
	    out.println("</td></tr>");
	    //
	    out.println("<tr><td align=\"right\">");
	    out.println("<b>Property Type:</b></td><td>");
	    out.println("<select name=\"prop_type\">");
	    for(int i=0;i<propTypes.length;i++){
		if(rent.getProp_type().equals(propTypes[i]))
		    out.println("<option selected>"+propTypes[i]);
		else
		    out.println("<option>"+propTypes[i]);
	    }
	    out.println("</select></td><td>");
	    out.println("<input type=\"checkbox\" name=\"accessory_dwelling\" "+(rent.getAccessory_dwelling().equals("")? "":"checked=\"checked\"")+
			" value=\"y\" /><b>Accessory Dwelling </b>,&nbsp;&nbsp;");						
	    out.println("<b> N-Hood:</b>");
	    out.println("<select name=\"nhood\">");
	    for(int i=0;i<nhoodArr.length;i++){
		if(rent.getNhood().equals(nhoodArr[i]))
		    out.println("<option selected=\"selected\">"+nhoodArr[i]+"</option>");
		else
		    out.println("<option>"+nhoodArr[i]+"</option>");
	    }
	    out.println("</select></td></tr>");
	    //
	    out.println("<tr><td align=\"right\">");
	    out.println("<b>Building Type:</b></td><td>");
	    out.println("<select name=\"building_type\">");
	    for(String btype:Helper.buildTypes){
		String selected="";
		if(rent.getBuilding_type().equals(btype)) selected="selected=\"selected\"";
		out.println("<option "+selected+">"+btype+"</option>");
	    }
	    out.println("</select></td><td colspan=\"2\">");
	    out.println("<input type=\"checkbox\" name=\"affordable\" "+
			(rent.isAffordable()?"checked=\"checked\"":"")+
			" value=\"Y\"><b>Affordable Housing </b>");		
	    out.println("</td></tr>");
	    //
	    out.println("<tr><td align=\"right\">");
	    out.println("<b>Construction Date:</b></td><td>");
	    out.println("<input name=\"built_date\" size=\"10\" class=\"date\" "+
			"maxlength=\"10\" value=\""+rent.getBuilt_date()+
			"\" /></td>");
	    if(tag.equals("") && user.canEdit()){
		if(!id.equals("")){
		    out.println("<td><b>Mark This Record as: </b>");
		    out.println("<input type=\"checkbox\" name=\"inactive\" value=\"y\" "+inactive+" />");
		    out.println("<b>Inactive </b></td>");
		}
	    }
	    else{
		if(!inactive.equals("")){
		    out.println("<td><b>This Permit is Inactive </b>");
		    out.println("<input type=\"hidden\" name=\"inactive\" value=\"y\" />");
		    out.println("</td>");
		}
	    }
	    out.println("</tr></table></td></tr>");
	    out.println("<tr><td><table width=\"100%\">");
	    out.println("<tr><td colspan=\"2\" align=\"left\"><b>Add New Notes: (max 1000 characters)</b><br />");
	    out.print("<textarea name=\"notes\" rows=\"3\" cols=\"70\" wrap>");
	    out.println("</textarea></td></tr>");
	    out.println("</td></tr></table></td></tr>");
	    //
	}
	//
	// 4th block
	// Buttons
	//
	if(!id.equals("") && tag.equals("")){
	    //
	    out.println("<tr><td><table width=\"100%\">");
	    if((user.canEdit() || user.isInspector()) &&
	       !action.equals("zoom")){
		//
		// Update 
		out.println("<tr>");
		/**
		out.println("<td valign=\"top\" "+
			    "align=\"center\"><font color=\"green\">"+
			    "If you've made any changes click on &gt;&gt;"+
			    "</font>");
		out.println("<input type=\"submit\" name=\"action\" "+
			    "value=\"Update\">&nbsp;&nbsp;");
		//
		if(tag.equals("") && user.isAdmin()){
		    out.println("<input type=\"hidden\" name=\"action2\" value=\"\" />");
		    out.println("<input type=\"button\" name=\"action\" "+
				"onclick=\"validateDelete();\" value=\"Delete\" />");
		}
		out.println("</td>");
		*/
		out.println("</tr>");
		out.println("</table></td></tr>");
	    }
	    // Second row
	    out.println("<tr><td><table width=\"100%\">");
	    out.println("<tr>");
	    if(user.canEdit() || user.isInspector()){
		//
		// Add Owner (old from list or new)
		/**
		out.println("<td valign=\"top\">");
		out.println("<input type=\"button\" name=\"action\" "+
			    "onClick=\"document.location='"+url+
			    "InsertOwner?"+
			    "id="+id+"'\" "+
			    "value=\"Add Owner/Agent\" />&nbsp;&nbsp;");
		out.println("</td>");

		out.println("<td valign=\"top\">");
		out.println("<input type=\"button\" name=\"action\" "+
			    "onClick=\"document.location='"+url+
			    "StructServ?rid="+id+"'\" "+
			    "value=\"Structures/Units\" />&nbsp;&nbsp;");
		out.println("</td>");
		out.println("<td valign=\"top\">");
		out.println("<input type=\"button\" name=\"action\" "+
			    "onclick=\"document.location='"+url+
			    "RentalFileServ?rental_id="+id+"'\" "+
			    "value=\"Add Attachment\" />&nbsp;&nbsp;");
		out.println("</td>");
		*/
	    }

	    // 
	    // Inspection
	    if(user.isInspector() ||
	       user.canEdit()){
		/**
		out.println("<td valign=\"top\" align=\"right\">");
		if(oldUnitFormat){
		    out.println("<input type=\"button\" name=\"action\" onclick=\"");
		    out.println("alert('Please Update Units, Occupant Load according to the new Format before starting Inspection report');\" "+
				" value=\"Inspection\" />&nbsp;&nbsp;</td>");
		}
		else{
		    out.print("<input type=\"button\" name=\"action\" "+
			      "onclick=\"document.location='"+url+"InspectionServ?rental_id="+id+"'\" "+
			      " value=\"Inspections\">&nbsp;&nbsp;</td>");
		}
		out.println("</td>");
		*/
		out.println("<td valign=top>");
		out.println("<input type=\"button\" name=\"action\" "+
			    "onclick=\"document.location='"+url+
			    "StartLegal?rental_id="+id+"'\" "+
			    "value=\"Start Legal\" />");
		out.println("</td>");		
		/**
		out.println("<td valign=\"top\" align=\"right\">");
		out.println("<input type=\"button\" name=\"action\" onclick=\""+
			    "window.open('"+url+"NoteServ?"+
			    "id="+id+"','Notes',"+
			    "'toolbar=0,location=0,"+
			    "directories=0,status=0,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=650,height=500');\""+
			    " value=\"Add Notes\">&nbsp;</td>");
		*/
	    }
	    if(user.canEdit()){
		out.println("<td valign=top>");
		//
		// bill
		/**
		out.println("<input type=\"button\" name=\"action\" onclick=\""+
			    "window.open('"+url+"BillServ?"+
			    "id="+id+"','Bill',"+
			    "'toolbar=0,location=0,"+
			    "directories=0,status=0,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=700,height=600');\""+
			    " value=\"Bill\">&nbsp;");
		out.println("</td>");
		*/
		//
		/**
		out.println("<td valign=\"top\">");
		
		out.println("<input type=\"button\" name=\"action\" onclick=\""+
			    "window.open('"+url+"Permit?"+
			    "id="+id+"','Permit'"+
			    ");\""+
			    " value=\"Permit\">&nbsp;");
		out.println("</td>");
		*/
	    }
	    out.println("</tr></table></td></tr>");
	    out.println("<tr><td align=\"center\"><table width=\"80%\" border=\"0\"><tr>");
	    out.println("</form>");
	    //
	    // Complaint
	    // replace with new uReport service
	    //
	    if(user.canEdit()){
		/**
		out.println("<td valign=\"top\" align=\"right\">");
		//
		out.println("<input type=\"button\" name=\"action\" "+
			    "onclick=\"document.location='"+url+
			    "VarianceServ?id="+id+"'\" "+
			    "value=\"Variance\" />&nbsp;&nbsp;");
		out.println("</td>");
		*/
	    }
	    /**
	    if(addresses != null && addresses.size() > 0){				
		out.println("<td valign=\"top\">");
		out.println("<form name=\"form2\" action=\""+url+
			    "ComplainBrowse?"+
			    "\" method=\"post\">");
		out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
		out.println("<input type=\"submit\" value=\"Complaints\" />&nbsp;");
		out.println("</td></form>");
	    }
	    */
	    //
	    // image
	    /**
	    out.println("<td valign=\"top\">");
	    out.println("<input type=\"button\" "+
			"onclick=\"document.location='"+url+
			"ImageProc?rid="+id+"'\" "+
			"value=\"Images\" />&nbsp;&nbsp;");
	    out.println("</td>");
	    */
	    //
	    // Map				
	    if(addrAval){
		/**
		out.println("<td valign=\"top\">");
		out.println("<form name=\"form3\" action=\""+mapUrl+
			    "\" method=\"post\" target=_blank>");
		if(debug){
		    logger.debug("Map Str: "+mapAddrStr);
		}
		out.println("<input type=\"hidden\" name=\"command\" value=\""+
			    mapAddrStr+"\" />");
		out.println("<input type=\"hidden\" name=\"rid\" value=\""+id+"\" />");
		out.println("<input type=\"hidden\" name=\"genauser\" "+
			    "value=\"me\" />");
		out.println("<input type=\"hidden\" name=\"service\" "+
			    "value=\"RAWTEXTPROVIDER\" />"); 
		out.println("<input type=\"hidden\" name=\"save\" value=\"off\" />");
		out.println("<input type=\"hidden\" name=\"browser\" "+
			    "value=\"true\" />");
		out.println("<input type=\"hidden\" name=\"application\" "+
			    "value=\"ivy\" />");
		out.println("<input type=\"hidden\" name=\"span\" "+
			    "value=\"1080 feet_us\" />");
		out.println("<input type=\"hidden\" name=\"uspan\" "+
			    "value=\"1080\" />");
		out.println("<input type=\"submit\" value=\"Location Map\">");
		out.println("</td></form>");
		*/
	    }
	    if(user.canEdit()){

	    }
	    out.println("</tr>");				
	}
	else{
	    //
	    // Create
	    /**
	    out.println("<tr><td align=\"right\" colspan=\"2\">");
	    if(tag.equals("") && user.canEdit()){
		out.println("<input type=\"submit\" "+
			    "name=\"action\" value=\"Create\" />"+
			    "&nbsp;&nbsp;&nbsp;"+
			    "</td></tr>"); 
	    }
	    */
	    out.println("</form>");	
	}
	// 
	out.println("</td></tr></table></td></tr>");
	out.println("</table></td></tr></table>");
	if(tag.equals("") && !(id.equals("") || action.equals("zoom"))){
	    out.println("<font color=\"green\">*Checking this box will delete "+
			"the corresponding item after clicking on Update "+
			"button</font><br /><br />");
	}
	out.println("</center>");
	if(!id.equals("")){
	    out.println("<div id=\"accordion\">");						
	    if(rent.hasRentalNotes()){
		List<RentalNote> rentalNotes = rent.getRentalNotes();
		out.println("<h3>Previous Notes </h3>");
		out.println("<div>");
		Helper.writeRentalNotes(out, rentalNotes);
		out.println("</div>");
	    }
	    StructureList structs = new StructureList(debug, id);
	    String back = structs.find();
	    if(back.equals("") && structs.size() > 0){
		out.println("<h3>Buildings & Units</h3>");
		out.println("<div>");
		Helper.writeStructUnits(out, structs);
		out.println("</div>");
	    }
	    if(tag.equals("")){			
		String all="";
		if(rent.hasPullHistory()){
		    all += "<h3>Pull History</h3>";
		    all += "<div>";
		    all += "<table border width=\"100%\">"+
			"   <tr><th>Date</th><th>Reason</th><th>By</th></tr>";
		    List<PullHistory> phl = rent.getPullHistorys();
		    for(PullHistory one: phl){
			all += "<tr>"+
			    "<td>"+one.getPull_date()+"</td>"+
			    "<td>"+one.getPull_text()+"</td>";
			if(one.hasUser())
			    all += "<td>"+one.getUser()+"&nbsp;</td>";
			else
			    all += "<td>&nbsp;</td>";
			all += "</tr>";
		    }
		    all += "</table>";
		    all += "</div>";
		    out.println(all);										
		}
		InspectionList inspects = new InspectionList(debug, id);
		inspects.find();
		if(inspects != null && inspects.size() > 0){
		    out.println("<h3>Inspection History</h3>");
		    out.println("<div>");
		    Helper.writeInspections(out, url, inspects);
			out.println("</div>");
		}
		if(rent.hasRentalFiles()){
		    List<RentalFile> files = rent.getRentalFiles();
		    out.println("<h3>Rental Attachments</h3>");
		    out.println("<div>");
		    Helper.printFiles(out, url, files);
		    out.println("</div>");
		}
		VarianceList variances = new VarianceList(debug, id);
		variances.find();
		if(variances != null && variances.size() > 0){
		    out.println("<h3>Variances</h3>");
		    out.println("<div>");					
		    Helper.writeVariances(out, url, variances);
		    out.println("</div>");

		}
		BillList bills = new BillList(debug, id);
		back = bills.find();
		if(back.equals("") && bills.size() > 0){
		    out.println("<h3>Bills</h3>");
		    out.println("<div>");					
		    Helper.writeBills(out, url, bills);
		    out.println("</div>");
		}
	    }
	    out.println("</div>");
	}
	Helper.writeWebFooter(out, url);
	//
	out.println("<script>");
	out.println("  $( \"#accordion\" ).accordion({collapsible:true, autoHeight: false,animated : false, icons:icons}); ");						out.println("</script>");	
	out.println("</body></html>");
	out.close();

    }

}






















































