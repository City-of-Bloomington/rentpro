package rental.model;
import java.util.List;
import java.sql.*;
import java.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class Rent implements java.io.Serializable{

    boolean debug = false;
    final static String [] propTypes = {"\n",
	"House",
	"Apartment",
	"Condo",
	"Mobile",
	"Rooming House"}; 
    static Logger logger = LogManager.getLogger(Rent.class);
    final static long serialVersionUID = 830L;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    NumberFormat dblForma = new DecimalFormat("#0.00");    
    String errors = "";
    String id="", nhood="",inactive="",built_date="",
	registered_date="",property_status="",last_cycle_date="",bedrooms="",
	bath_count="",occ_load="",pull_date="",date_rec="",date_billed="",
	permit_issued="",permit_expires="",zoning="",permit_length="",units="",
	structures="",cdbg_funding="",pull_reason="",prop_type="",
	grandfathered="",notes="",agentId = "", building_type="",accessory_dweliing="";
    String affordable = "";
    int inspection_count = 0;
    double average_time = 0;
    List<Address> addresses = null;
    OwnerList owners = null;
    VarianceList variances = null;
    Owner agent = null;
    PropStatus propStatus = null;
    Zone zone = null;
    PullReason pullReason = null;
    StructureList structs = null;
    List<RentalNote> rentalNotes = null;
    List<PullHistory> pullHistorys = null;
    List<RentalFile> rentalFiles = null;
    /*
     * Create new instance of the class
     *
     * @param boolean debug flag
     */	
    public Rent(boolean deb){
	debug = deb;
    }
    /*
     * Create new instance of the class
     *
     * @param String record id
     * @parma boolean debug flag
     */
	
    public Rent(String val, boolean deb){
	setId(val);
	debug = deb;
		
    }
    public Rent(String val, boolean doView, boolean deb){
	id = val;
	debug = deb;
	errors += doSelect();
    }
    public Rent(boolean deb, String[] strs){
	debug = deb;
	setId(strs[0]);
	setProperty_status(strs[1]);
	setAgentId(strs[2]);
	setPermit_length(strs[3]);
	setPull_reason(strs[4]);
	setGrandfathered(strs[5]);
	setUnits(strs[6]); // old format				
	setStructures(strs[7]); // old format
	setBedrooms(strs[8]);
	setOcc_load(strs[9]);
		
	setRegistered_date(strs[10]);
	setLast_cycle_date(strs[11]);
	setPermit_issued(strs[12]);
	setPermit_expires(strs[13]);
	setPull_date(strs[14]);
	setDate_billed(strs[15]);
	setDate_rec(strs[16]);
	setNotes(strs[17]);
	setCdbg_funding(strs[18]);
	setZoning(strs[19]);
		
	setProp_type(strs[20]);
	setBath_count(strs[21]);
	setNhood(strs[22]);
	setBuilt_date(strs[23]);
	setInactive(strs[24]);
	setAffordable(strs[25]);
	setBuilding_type(strs[26]);
	setAccessory_dwelling(strs[27]);
    }	
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getNhood(){
	return nhood;
    }
    public String getInactive(){
	return inactive;
    }
    public String getBuilt_date(){
	return built_date;
    }
    public String getRegistered_date(){
	return registered_date;
    }
    public String getProperty_status(){
	return property_status;
    }
    public String getLast_cycle_date(){
	return last_cycle_date;
    }
    public String getBuilding_type(){
	return building_type;
    }	
    public StructureList getStructs(){
	if(structs == null){
	    errors += findStructures();
	}
	return structs;
    }
    public String getStructureCount(){
	if(structs == null){
	    errors += findStructures();
	}
	if(structs != null){
	    return ""+structs.size();
	}
	return structures;
    }	
    /**
     * to check if this rental has updated unit structure
     * new format set up
     */
    public boolean hasUpdatedUnits(){
	if(structs == null){
	    errors += findStructures();
	}
	if(structs == null || structs.size() == 0){
	    return false;
	}
	return true;
    }
    public String getBedrooms(){
	if(structs == null){
	    errors += findStructures();
	}
	if(structs != null){
	    String beds = structs.getBedDescrp();
	    if(!beds.equals(""))
		return beds;
	}
	return bedrooms;
    }
    public String getUnits(){
	if(structs == null){
	    errors += findStructures();
	}
	if(structs != null){
	    int _units = structs.getTotalUnits();
	    if( _units > 0)
		return ""+_units;
	}
	return units;
    }
    public String getStructures(){
	return structures;
    }	
    public String getBath_count(){
	return bath_count;
    }
    public String getOcc_load(){
	if(structs == null){
	    errors += findStructures();
	}
	if(structs != null){
	    String occl = structs.getOccLoadDescrp();
	    if(!occl.equals(""))
		return occl;
	}
	return occ_load; // the old
    }
    public String getOldOcc_load(){
	return occ_load;
    }
    public String getOldUnits(){
	return units;
    }
    public String getOldBedrooms(){
	return bedrooms;
    }	
    public String getPull_date(){
	return pull_date;
    }
    public String getDate_rec(){
	return date_rec;
    }
    public String getDate_billed(){
	return date_billed;
    }
    public String getPermit_issued(){
	return permit_issued;
    }
    public String getPermit_expires(){
	return permit_expires;
    }
    public String getZoning(){
	return zoning;
    }
	
    public String getPermit_length(){
	return permit_length;
    }
    public String getCdbg_funding(){
	return cdbg_funding;
    }
    public String getPull_reason(){
	return pull_reason;
    }
    public String getProp_type(){
	return prop_type;
    }
    public String getProp_type_text(){
	if(!prop_type.equals("")){
	    try{
		int jj = Integer.parseInt(prop_type);
		return propTypes[jj];
	    }catch(Exception ex){}
	}
	return "";
    }
    public String getGrandfathered(){
	return grandfathered;
    }
    public String getAccessory_dwelling(){
	return accessory_dweliing;
    }
    public void setAccessory_dwelling(String val){
	if(val != null)
	    accessory_dweliing = val;
    }
    public String getAgentId(){
	return agentId;
    }
	
    public String getNotes(){
	return notes;
    }
    public String getAffordable(){
	return affordable;
    }
    public String getInspectionCount(){
	if(inspection_count >0)
	    return ""+inspection_count;
	else
	    return "";
    }
    public String getAverageTime(){
	if(average_time > 0)
	    return dblForma.format(average_time)+" hrs";
	else
	    return "";
    }		
    public Owner getAgent(){
	if(agent == null && hasAgent()){
	    Owner owner = new Owner(debug, agentId);
	    String str = owner.doSelect();
	    if(str.equals("")){
		agent = owner;
	    }
	}
	return agent;
    }
    public boolean hasAgent(){
	if(agentId.equals("") ||
	   agentId.equals("0") ||
	   agentId.equals("6010")) return false;
	return true;
    }
    //
    // check if this record is inactive any more
    //
    public boolean isInactive(){
	return !inactive.equals("");
    }
    /*
     * check if the permit is active
     */
    public boolean isActive(){
	return !isInactive();
    }
    //
    public boolean isAffordable(){
	return !affordable.equals("");
    }	
    //
    public boolean hasErrors(){
	return !errors.equals("");
    }
    //
    public OwnerList getOwners(){
	if(owners == null){
	    OwnerList ol = new OwnerList(debug, id);
	    String str = ol.lookFor();
	    if(str.equals("")){
		owners = ol;
	    }
	    else{
		if(debug)
		    logger.error(str);
	    }			
	}
	return owners;
    }
    public List<Address> getAddresses(){
	if(addresses == null){
	    findAddresses();
	}
	return addresses;
    }
    public PropStatus getPropStatus(){
	if(propStatus == null){
	    if(!property_status.equals("")){
		propStatus = new PropStatus(debug, property_status);
		propStatus.doSelect();
	    }
	    else{
		propStatus = new PropStatus(debug);
	    }
	}
	return propStatus;
    }
    public Zone getZone(){
	if(zone == null){
	    if(!zoning.equals("")){
		zone = new Zone(debug, zoning);
		zone.doSelect();
	    }
	    else{
		zone = new Zone(debug);
	    }
	}
	return zone;
    }
    //
    public PullReason getPullReason(){
	if(pullReason == null){
	    if(!pull_reason.equals("")){
		pullReason = new PullReason(debug, pull_reason);
		pullReason.doSelect();
	    }
	    else{
		pullReason = new PullReason(debug);
	    }
	}
	return pullReason;
    }
    public VarianceList getVariances(){
	if(variances == null){
	    VarianceList vl = new VarianceList(debug, id);
	    String str = vl.find();
	    if(str.equals("") && vl.size() > 0){
		variances = vl;
	    }
	}
	return variances;
    }
    public String findStructures(){
	if(!id.equals("")){
	    structs = new StructureList(debug, id);
	    errors += structs.find();
	}
	return "";
    }
    public boolean hasRentalNotes(){
	getRentalNotes();
	return rentalNotes != null && rentalNotes.size() > 0;
    }
    public List<RentalNote> getRentalNotes(){
	if(rentalNotes == null && !id.equals("")){
	    RentalNoteList rnl = new RentalNoteList(debug, id);
	    String back = rnl.find();
	    if(back.equals("") && rnl.size() > 0){
		rentalNotes = rnl;
	    }
	}
	return rentalNotes;
    }
    public boolean hasRentalFiles(){
	getRentalFiles();
	return rentalFiles != null && rentalFiles.size() > 0;
    }
    public List<RentalFile> getRentalFiles(){
	if(rentalFiles == null && !id.equals("")){
	    RentalFileList rfl = new RentalFileList(debug, id);
	    String back = rfl.find();
	    if(back.equals("")){						
		List<RentalFile> ones = rfl.getFiles();
		if(ones != null && ones.size() > 0)
		    rentalFiles = ones;
	    }
	}
	return rentalFiles;
    }		
    public boolean hasPullHistory(){
	getPullHistorys();
	return pullHistorys != null && pullHistorys.size() > 0;
    }		
    public List<PullHistory> getPullHistorys(){
	if(pullHistorys == null && !id.equals("")){
	    PullHistoryList rnl = new PullHistoryList(debug, id);
	    String back = rnl.find();
	    if(back.equals("") && rnl.size() > 0){
		pullHistorys = rnl;
	    }
	}
	return pullHistorys;
    }		
    //
    // setters
    //
    public void setId (String val){
	if(val != null)
	    id = val;
    }
    public void setNhood(String val){
	if(val != null)
	    nhood = val;
    }
    public void setInactive(String val){
	if(val != null)
	    inactive = val;
    }
    public void setBuilt_date(String val){
	if(val != null)
	    built_date = val;
    }
    public void setRegistered_date(String val){
	if(val != null)
	    registered_date = val;
    }
    public void setProperty_status(String val){
	if(val != null)
	    property_status = val.trim();
    }
    public void setLast_cycle_date(String val){
	if(val != null)
	    last_cycle_date = val;
    }
    public void setBedrooms(String val){
	if(val != null)
	    bedrooms = val;
    }
    public void setUnits(String val){
	if(val != null)
	    units = val;
    }
    public void setStructures(String val){
	if(val != null)
	    structures = val;
    }	
    public void setBath_count(String val){
	if(val != null)
	    bath_count = val;
    }
    public void setOcc_load(String val){
	if(val != null)
	    occ_load = val;
    }
    public void setPull_date(String val){
	if(val != null)
	    pull_date = val;
    }
    public void setDate_rec(String val){
	if(val != null)
	    date_rec = val;
    }
    public void setDate_billed(String val){
	if(val != null)
	    date_billed = val;
    }
    public void setPermit_issued(String val){
	if(val != null)
	    permit_issued = val;
    }
    public void setPermit_expires(String val){
	if(val != null)
	    permit_expires = val;
    }
    public void setZoning(String val){
	if(val != null)
	    zoning = val;
    }
    public void setPermit_length(String val){
	if(val != null)
	    permit_length = val;
    }
    public void setCdbg_funding(String val){
	if(val != null)
	    cdbg_funding = val;
    }
    public void setPull_reason(String val){
	if(val != null)
	    pull_reason = val;
    }
    public void setProp_type(String val){
	if(val != null)
	    prop_type = val;
    }
    public void setGrandfathered(String val){
	if(val != null)
	    grandfathered = val;
    }

    public void setAgentId(String val){
	if(val != null)
	    agentId = val;
    }
    public void setNotes (String val){
	if(val != null)	
	    notes = val;
    }
    public void setAffordable (String val){
	if(val != null)
	    if(!val.equals(""))
		affordable = "y";
    }	
    public void deleteAgent (){
	agentId = "";
	agent = null;
    }
    public void setBuilding_type(String val){
	if(val != null)
	    building_type = val;
    }
		
    //
    public String doSave(){
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;

	String back = "", qq = "";
	qq = "select registr_id_seq.nextval from dual";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt2 = con.prepareStatement(qq);			
	    rs = pstmt2.executeQuery();
						
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = "insert into registr values (" +id+","+
		"?,?,?,?,?,"+
		"?,?,?,?,?,"+
		"?,?,?,?,?,"+
		"?,?,?,?,?,"+				
		"?,?"+
		",?,?,?,?,?,?,?,?"+
		")";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    if(property_status.equals("")){
		property_status = "R";
	    }
	    pstmt.setString(1,property_status); // 2

	    if(agentId.equals("")) // 3
		pstmt.setString(2, "0");
	    else
		pstmt.setString(2, agentId);
	    if(registered_date.equals("")) //4
		pstmt.setDate(3, null);
	    else
		pstmt.setDate(3,new java.sql.Date(dateFormat.parse(registered_date).getTime()));				
	    if(last_cycle_date.equals(""))
		pstmt.setDate(4,null);
	    else
		pstmt.setDate(4,new java.sql.Date(dateFormat.parse(last_cycle_date).getTime()));								
	    if(permit_issued.equals("")) // 6
		pstmt.setDate(5,null);
	    else
		pstmt.setDate(5,new java.sql.Date(dateFormat.parse(permit_issued).getTime()));		
	    if(permit_expires.equals(""))
		pstmt.setDate(6,null);
	    else
		pstmt.setDate(6,new java.sql.Date(dateFormat.parse(permit_expires).getTime()));	
	    if(permit_length.equals("")) // 8
		pstmt.setString(7,"0");
	    else
		pstmt.setString(7,permit_length);
	    if(pull_date.equals(""))   // 9
		pstmt.setDate(8,null);
	    else
		pstmt.setDate(8,new java.sql.Date(dateFormat.parse(pull_date).getTime()));	
				
	    if(pull_reason.equals(""))
		pstmt.setString(9,null);
	    else
		pstmt.setString(9,pull_reason);
	    pstmt.setString(10, "N");   // new rental not used any more
	    pstmt.setString(11, null); // old zoning
	    if(grandfathered.equals("")) // 12
		pstmt.setString(12, "N");
	    else
		pstmt.setString(12, "Y");
	    pstmt.setString(13, "N");   // annexed not used any more
	    if(units.equals(""))  // 14
		pstmt.setString(14, null);
	    else
		pstmt.setString(14, units);
	    if(structures.equals(""))
		pstmt.setString(15,null);
	    else
		pstmt.setString(15,structures);
	    if(bedrooms.equals(""))  // 16
		pstmt.setString(16,null);
	    else
		pstmt.setString(16,bedrooms);
	    if(occ_load.equals("")) //  17
		pstmt.setString(17,null);	
	    else
		pstmt.setString(17, occ_load);
	    if(date_billed.equals("")) // 18
		pstmt.setString(18,null);				
	    else
		pstmt.setDate(18,new java.sql.Date(dateFormat.parse(date_billed).getTime()));		
	    if(date_rec.equals("")) // 19
		pstmt.setString(19,null);
	    else
		pstmt.setDate(19,new java.sql.Date(dateFormat.parse(date_rec).getTime()));						
	    if(notes.equals(""))  // 20
		pstmt.setString(20,null);				
	    else
		pstmt.setString(20,notes); // doubleApostrify

	    if(cdbg_funding.equals("")) // 21
		pstmt.setString(21,null);				
	    else
		pstmt.setString(21,"Y");
	    if(zoning.equals("")) // 22
		pstmt.setString(22,null);				
	    else
		pstmt.setString(22,zoning);
	    //
	    if(prop_type.equals(""))
		pstmt.setString(23,null);					
	    else
		pstmt.setString(23,prop_type);
	    if(bath_count.equals(""))
		pstmt.setString(24,null);					
	    else
		pstmt.setString(24,bath_count);
	    if(nhood.equals(""))
		pstmt.setString(25,null);					
	    else
		pstmt.setString(25,nhood);
	    if(built_date.equals(""))
		pstmt.setString(26,null);	
	    else
		pstmt.setDate(26,new java.sql.Date(dateFormat.parse(built_date).getTime()));						
	    pstmt.setString(27,null);
	    if(affordable.equals("")) 
		pstmt.setString(28,null);				
	    else
		pstmt.setString(28,affordable);		
	    if(building_type.equals("")) 
		pstmt.setString(29,null);				
	    else
		pstmt.setString(29,building_type);
	    if(accessory_dweliing.equals("")) 
		pstmt.setString(30,null);				
	    else
		pstmt.setString(30,"y");		
	    pstmt.executeUpdate();
	    //
	    qq = " insert into regid_name values(6010,?)"; // unknown owner yet
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	    if(!pull_date.equals("") && !pull_reason.equals("")){
		qq = " insert into rental_pull_hist values(?,?,?)";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, id);
		pstmt.setDate(2, new java.sql.Date(dateFormat.parse(pull_date).getTime()));				
		pstmt.setString(3, pull_reason);				

		pstmt.executeUpdate();
	    }			
	}
	catch(Exception ex){
	    back += " Could not save "+ex+":"+qq;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}
	return back;		
    }
    /*
     * Update data of certain record in the database given the id
     *
     * @return String if any eror or exception happens
     */	
    public String doUpdate(){
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "", qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}				
	try{
	    qq = "update registr set "+
		"property_status=?,agent=?,registered_date=?,last_cycle_date=?,permit_issued=?,"+
		"permit_expires=?,permit_length=?,pull_date=?,pull_reason=?,grandfathered=?,"+
		"date_billed=?,"+
		"date_rec=?,notes=?,cdbg_funding=?,zoning2=?"+
		",prop_type=?,bath_count=?,nhood=?,built_date=?,inactive=?,affordable=?,building_type=?,accessory_dwelling=? "+
		" where id=?";
	    pstmt = con.prepareStatement(qq);
	    //
	    pstmt.setString(1,property_status); 
	    if(agentId.equals("")) // 3
		pstmt.setString(2, "0");
	    else
		pstmt.setString(2, agentId);
	    if(registered_date.equals("")) //4
		pstmt.setDate(3, null);
	    else
		pstmt.setDate(3,new java.sql.Date(dateFormat.parse(registered_date).getTime()));				
	    if(last_cycle_date.equals(""))
		pstmt.setDate(4,null);
	    else
		pstmt.setDate(4,new java.sql.Date(dateFormat.parse(last_cycle_date).getTime()));								
	    if(permit_issued.equals("")) // 6
		pstmt.setDate(5,null);
	    else
		pstmt.setDate(5,new java.sql.Date(dateFormat.parse(permit_issued).getTime()));		
	    if(permit_expires.equals(""))
		pstmt.setDate(6,null);
	    else
		pstmt.setDate(6,new java.sql.Date(dateFormat.parse(permit_expires).getTime()));	
	    if(permit_length.equals("")) // 8
		pstmt.setString(7,"0");
	    else
		pstmt.setString(7,permit_length);
	    if(pull_date.equals(""))   // 9
		pstmt.setDate(8,null);
	    else
		pstmt.setDate(8,new java.sql.Date(dateFormat.parse(pull_date).getTime()));	
	    if(pull_reason.equals(""))
		pstmt.setString(9,null);
	    else
		pstmt.setString(9,pull_reason);
	    // skip 2 here use any more
	    if(grandfathered.equals("")) // 
		pstmt.setString(10, "N");
	    else
		pstmt.setString(10, "Y");
	    // skip 1 more here
	    if(date_billed.equals("")) // 
		pstmt.setString(11,null);				
	    else
		pstmt.setDate(11,new java.sql.Date(dateFormat.parse(date_billed).getTime()));		
	    if(date_rec.equals("")) // 
		pstmt.setString(12,null);
	    else
		pstmt.setDate(12,new java.sql.Date(dateFormat.parse(date_rec).getTime()));						
	    if(notes.equals(""))  // 
		pstmt.setString(13,null);				
	    else
		pstmt.setString(13,notes);

	    if(cdbg_funding.equals(""))
		pstmt.setString(14,null);				
	    else
		pstmt.setString(14,"Y");
	    if(zoning.equals("")) // 
		pstmt.setString(15,null);				
	    else
		pstmt.setString(15,zoning);
	    //

	    if(prop_type.equals(""))
		pstmt.setString(16,null);					
	    else
		pstmt.setString(16,prop_type);
	    if(bath_count.equals(""))
		pstmt.setString(17,null);					
	    else
		pstmt.setString(17,bath_count);
	    if(nhood.equals(""))
		pstmt.setString(18,null);					
	    else
		pstmt.setString(18,nhood);
	    if(built_date.equals(""))
		pstmt.setString(19,null);	
	    else
		pstmt.setDate(19,new java.sql.Date(dateFormat.parse(built_date).getTime()));
	    if(inactive.equals(""))
		pstmt.setString(20,null);
	    else
		pstmt.setString(20,"Y");
	    if(affordable.equals(""))
		pstmt.setString(21,null);
	    else
		pstmt.setString(21,"Y");
	    if(building_type.equals(""))
		pstmt.setString(22,null);
	    else
		pstmt.setString(22,building_type);
	    if(accessory_dweliing.equals("")) 
		pstmt.setString(23,null);				
	    else
		pstmt.setString(23,"y");		
	    pstmt.setString(24,id);
	    pstmt.executeUpdate();
	    //
	    // the supplement table
	    //
	    if(!pull_date.equals("") && !pull_reason.equals("")){
		qq = " select count(*) from rental_pull_hist "+
		    " where id="+id+" and pull_date=to_date('"+
		    pull_date+"','mm/dd/yyyy') and pull_reason='"+
		    pull_reason+"'";
		if(debug){
		    logger.debug(qq);
		}				
		pstmt = con.prepareStatement(qq);
		rs = pstmt.executeQuery();
		int nct = 0;
		if(rs.next()){
		    nct = rs.getInt(1);
		}
		if(nct == 0){
		    qq = " insert into rental_pull_hist values(?,?,?)";
		    if(debug){
			logger.debug(qq);
		    }
		    pstmt = con.prepareStatement(qq);
		    pstmt.setString(1,id);
		    pstmt.setDate(2,new java.sql.Date(dateFormat.parse(pull_date).getTime()));
		    pstmt.setString(3,pull_reason);
		    pstmt.executeUpdate();
		}
	    }
	}
	catch(Exception ex){
	    back += " Could not save "+ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;		
    }
    public String updateNotes(){
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "", qq = "";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}				
	try{
	    qq = "update registr set "+
		"notes=? where id=?";
	    pstmt = con.prepareStatement(qq);
	    if(notes.equals(""))  // 
		pstmt.setString(1,null);				
	    else
		pstmt.setString(1,notes);
	    pstmt.setString(2,id);
	    pstmt.executeUpdate();
	    //
	    // the supplement table
	    //
	}
	catch(Exception ex){
	    back += " Could not update "+ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;		
    }	
    /*
     * Get data from the database
     *
     * @return String if any eror or exception happens
     */
    public String doSelect(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back="", str="";
	String	qq = "select property_status,"+
	    "agent,"+  
	    "permit_length,"+
	    "pull_reason,"+
	    "grandfathered,"+
	    "units,"+
	    "structures,"+
	    "bedrooms,"+  
	    "occ_load,"+ 
	    "to_char(registered_date,'mm/dd/yyyy'), "+ // 10
			
	    "to_char(last_cycle_date,'mm/dd/yyyy'), "+
	    "to_char(permit_issued,'mm/dd/yyyy'), "+
	    "to_char(permit_expires,'mm/dd/yyyy'), "+
	    "to_char(pull_date,'mm/dd/yyyy'), "+
	    "to_char(date_billed,'mm/dd/yyyy'), "+
	    "to_char(date_rec,'mm/dd/yyyy'), "+
	    " notes,"+
	    " cdbg_funding, "+ 
	    " zoning2 "+  // 19
	    ",prop_type,bath_count,nhood, "+
	    "to_char(built_date,'mm/dd/yyyy'), "+
	    " inactive,affordable,building_type, "+
	    " accessory_dwelling "+
	    " from registr where id=?";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}			
	if(debug){
	    logger.debug(qq);
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) property_status = str;
		str = rs.getString(2);
		if(str != null) agentId = str;
		str = rs.getString(3);
		if(str != null) permit_length = str;
		str = rs.getString(4);
		if(str != null) pull_reason = str;
		str = rs.getString(5);
		if(str != null && str.equals("Y")) 
		    grandfathered = "Y";
		str = rs.getString(6);
		if(str != null) units = str;
		str = rs.getString(7);  
		if(str != null) structures = str; 
		str = rs.getString(8);
		if(str != null) bedrooms = str;
		str = rs.getString(9);
		if(str != null) occ_load = str;
		str = rs.getString(10);
		if(str != null) registered_date = str;
		str = rs.getString(11);
		if(str != null) last_cycle_date = str;
		str = rs.getString(12);
		if(str != null) permit_issued = str;
		str = rs.getString(13);
		if(str != null) permit_expires = str;
		str = rs.getString(14);
		if(str != null) pull_date = str;
		str = rs.getString(15);
		if(str != null) date_billed = str;
		str = rs.getString(16);
		if(str != null) date_rec = str;
		str = rs.getString(17);
		if(str != null) notes = str;
		str = rs.getString(18);
		if(str != null && str.equals("Y")) 
		    cdbg_funding = "Y";
		str = rs.getString(19);
		if(str != null) zoning = str;
		//
		str = rs.getString(20);
		if(str != null) prop_type = str;
		str = rs.getString(21);
		if(str != null) bath_count = str;
		str = rs.getString(22);
		if(str != null) nhood = str;
		str = rs.getString(23);
		if(str != null) built_date = str;
		str = rs.getString(24);
		if(str != null) inactive = str;
		str = rs.getString(25);
		if(str != null) affordable = str;
		str = rs.getString(26);
		if(str != null) building_type = str;
		str = rs.getString(27);
		if(str != null) accessory_dweliing = str;								
	    }
	    else{
		back = "No record found ";
	    }
	}
	catch(Exception ex){
	    back += " Could not retreive data "+ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;		
    }
    /**
     * Delete some owners from this permit
     *
     * @param List of owner id's
     */
    public String deleteOwners(String[] arr){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "";
	String qq = " delete from regid_name "+
	    "where id = ? and name_num = ? ";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);	
	    for(String str:arr){
		pstmt.setString(1,id);
		pstmt.setString(2,str);
		pstmt.executeUpdate();
	    }
	    qq = " select count(*) from regid_name where id=?";
	    int ncnt = 0;
	    if(debug){
		logger.debug(qq);	
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		ncnt = rs.getInt(1);
	    }
	    //
	    // if no more owner records add the dummy one
	    //
	    if(ncnt == 0){
		qq = "insert into regid_name values(6010,?)";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,id);
		pstmt.executeUpdate();
	    }
	}
	catch(Exception ex){
	    back += " Could not save "+ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;			

    }
    public String doDelete(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back="", str="";
	String	qq = "";
	//
	// all related inpection info
	//		
	String q2 = "delete from variances where id=?";
	String q3 = "delete from inspections where id=?";
	String q4 = "delete from regid_name where id=?";
	String q5 = "delete from address2 where registr_id= ?";
	String q6 = "delete from rental_updates where reg_id=?";
		
	String q7 = "delete from registr where id=?";
	// String q8 = "delete from r_units where id=?";
	String q9 = "delete from rental_pull_hist where id=?";
	//
	qq = q2;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}					
	if(debug){
	    logger.debug(qq);
	}
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	    qq = q3;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	    qq = q4;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	    qq = q5;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();			
	    qq = q6;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();			
	    qq = q9;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();			
	    qq = q7;
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	    /*
	      qq = q8;
	      if(debug){
	      logger.debug(qq);
	      }
	      pstmt = con.prepareStatement(qq);
	      pstmt.setString(1,id);
	      pstmt.executeUpdate();
	    */
	}
	catch(Exception ex){
	    back += " Could not delete "+ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }

    public String findAddresses(){
	AddressList al = new AddressList(debug, id);
	String str = al.lookFor();
	if(str.equals("")){
	    addresses = al.getAddresses();
	}
	else{
	    logger.error(str);
	}
	return str;
    }

    public String toString(){
	String ret = "";
	ret += " ID: "+id;
	return ret;
    }
    @Override
    public boolean equals(Object obj) { 
	if(this == obj) 
	    return true; 
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
        Rent one = (Rent) obj; 
        return one.getId().equals(this.id); 
    } 
      
    @Override
    public int hashCode() { 
	int ret = 37;
	if(!id.equals("")){
	    try{
		ret += Integer.parseInt(id)*29;
	    }
	    catch(Exception ex){
								
	    }
	}
	return ret;
    } 
      
 		
    /**
     * Add owners to this permit
     *
     * @param List of owner id's
     */
    public String addOwners(String[] arr){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "";
	String qq = "insert into regid_name values(?,?)";
	if(id.equals("")){
	    back = "Rental id not set ";
	    logger.error(back);
	    return back;
	}
	if(arr == null || arr.length < 1){
	    back = "Empty list of owners to add ";
	    logger.error(back);
	    return back;
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    for(String str:arr){
		pstmt.setString(1,str);
		pstmt.setString(2,id);
		pstmt.executeUpdate();
	    }
	    qq = " delete from regid_name where id="+id+ " and "+
		"(name_num=0 or name_num=6010) ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += " Could not delete "+ex+":"+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
    /**
     * Add owners to this permit
     *
     * @param List of owner id's
     */
    public String updateAgent(String[] arr){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "";
	if(id.equals("")){
	    back = "Rental id not set ";
	    logger.error(back);
	    return back;
	}
	if(arr == null || arr.length < 1){
	    back = "Empty list of owners to add ";
	    logger.error(back);
	    return back;
	}
	String qq = "update registr set agent=? where id=? ";
	String str = arr[0];
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	if(str != null){ 
	    try{
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1, str);
		pstmt.setString(2, id);
		pstmt.executeUpdate();
	    }
	    catch(Exception ex){
		back += " Could not delete "+ex+":"+qq;
	    }
	    finally{
		Helper.doClean(con, pstmt, rs);
	    }
	}
	return back;
    }
    public String findStats(){
	//
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String back = "", qq = "";
	try{
	    qq = "select count(*),avg(time_spent)"+
		" from "+Inspection.table_name+" where id = ? ";
	    qq += " and time_spent > 0 ";
	    if(debug){
		logger.debug(qq);
	    }	
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }		
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, id);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		inspection_count  = rs.getInt(1);
		average_time = rs.getDouble(2);
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
		
}
