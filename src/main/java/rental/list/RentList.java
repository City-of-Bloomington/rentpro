package rental.list;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class RentList extends ArrayList<Rent>{

    boolean debug = false;
    Logger logger = LogManager.getLogger(RentList.class);
    final static long serialVersionUID = 840L;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");	
    String id=""; // registr_id from rental
    String which_date="registered_date",
	agent="";
    String dept="", nhood = "",inactive="",property_status="",
	bedrooms="",bath_count="",occ_load="",zoning="", permit_length="",
	units="",structures="",cdbg_funding="",pull_reason="",prop_type="",
	grandfathered="", streetAddress="", active="";
	
    // owner related
    String name_num="", city="", state="", own_addr="",own_name="",
	zip="", phone="", email="";
    // 
    // address related
    String street_num="", street_dir="",street_name="",street_type="",
	post_dir="", sud_num="", sud_type="", invalid_addr="";

    String unitsGr="", structuresGr="",
	date_from="", date_to="", notes="", owner_or_agent="",
	receipt_no="", bid="", variance="", building_type="",hasVariance="",
	affordable="", sortBy="pd.id",
	order_by="", addr_str="", accessory_dwelling="";
    //
    // basic constructor
    public RentList(boolean deb){

	debug = deb;
	//
	// initialize
	//
    }
    public RentList(boolean deb, String val){

	debug = deb;
	//
	// initialize
	//
	this.id = val;
    }
    public void setName_num (String val){
	if(val != null)
	    name_num = val;
    }
    public void setCity (String val){
	if(val != null)
	    city = val.toUpperCase();
    }
    public void setState (String val){
	if(val != null)
	    state = val.toUpperCase();
    }
    public void setZip (String val){
	if(val != null)
	    zip = val;
    }
    public void setEmail (String val){
	if(val != null)		
	    email = val;
    }
    public void setPhone (String val){
	if(val != null)
	    phone = val;
    }
    public void setWhichDate (String val){
	if(val != null)		
	    which_date = val;
    }
    public void setDept(String val){
	if(val != null)
	    dept = val;
    }
    public void setNhood(String val){
	if(val != null)
	    nhood = val;
    }
    public void setInactive(String val){
	if(val != null)
	    inactive = val;
    }
    public void setActive(String val){
	if(val != null)
	    active = val;
    }
    public void setActiveStatus(String val){
	if(val != null){
	    if(val.equals("y")) active="y";
	    else if(val.equals("n")) inactive="y";
	}
    }	
    public void setBedrooms(String val){
	if(val != null)
	    bedrooms = val;
    }
    public void setUnits(String val){
	if(val != null)
	    units = val;
    }
    public void setUnitsGr(String val){
	if(val != null)
	    unitsGr = val;
    }	
    public void setStructures(String val){
	if(val != null)
	    structures = val;
    }
    public void setStructuresGr(String val){
	if(val != null)
	    structuresGr = val;
    }	
    public void setBath_count(String val){
	if(val != null)
	    bath_count = val;
    }
    public void setOcc_load(String val){
	if(val != null)
	    occ_load = val;
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
	    agent = val;
    }
    public void setProperty_status(String val){
	if(val != null)
	    property_status = val;
    }	
    // address related
    public void setStreet_num (String val){
	if(val != null)
	    street_num = val;
    }
    public void setStreet_dir (String val){
	if(val != null)
	    street_dir = val.toUpperCase();
    }
    public void setStreet_name (String val){
	if(val != null)
	    street_name = val.toUpperCase();
    }
    public void setStreet_type (String val){
	if(val != null)
	    street_type = val.toUpperCase();
    }
    public void setPost_dir (String val){
	if(val != null)
	    post_dir = val.toUpperCase();
    }
    public void setSud_type (String val){
	if(val != null)
	    sud_type = val.toUpperCase();
    }
    public void setSud_num (String val){
	if(val != null)
	    sud_num = val;
    }
    public void setOwn_addr(String val){
	if(val != null)
	    own_addr = val.toUpperCase();
    }

    public void setOwn_name(String val){
	if(val != null)
	    own_name = val.toUpperCase();
    }
    public void setDate_from(String val){
	if(val != null)
	    date_from = val;
    }
    public void setDate_to(String val){
	if(val != null)
	    date_to = val;
    }
    public void setNotes(String val){
	if(val != null)
	    notes = val.toUpperCase();
    }
    public void setOwner_or_agent(String val){
	if(val != null)
	    owner_or_agent = val;
    }
    public void setReceipt_no(String val){
	if(val != null)
	    receipt_no = val;
    }
    public void setBid(String val){
	if(val != null)
	    bid = val;
    }
    public void setVariance(String val){
	if(val != null)
	    variance = val;
    }
    public void setAffordable(String val){
	if(val != null)
	    affordable = val;
    }	
    public void setHasVariance(){
	hasVariance = "y";
    }	
    public void setBuilding_type(String val){
	if(val != null)
	    building_type = val;
    }	
    public void setInvalid_addr (String val){
	if(val != null)
	    invalid_addr = val;
    }
    public void setAccessory_dwelling(String val){
	if(val != null)
	    accessory_dwelling = val;
    }		
    public void setSortBy (String val){
	if(val != null)
	    sortBy = val;
    }
    public void setOrderBy (String val){
	if(val != null)
	    sortBy = val;
    }		
    public void setStreetAddress (String val){
	if(val != null)
	    streetAddress = val.toUpperCase();
    }
	
    //
    // setters
    //
    public void setId(String val){
	if(val != null && !val.trim().equals(""))
	    id = val;
    }
    //
    // getters
    //
    public List<Rent> getRents(){
	return this;
    }
    public void buildAddrStr(){
	if(!street_num.equals("")){
	    addr_str = street_num;
	}
	if(!street_dir.equals("")){
	    if(!addr_str.equals("")) addr_str += " ";
	    addr_str += street_dir;
	}
	if(!street_name.equals("")){
	    if(!addr_str.equals("")) addr_str += " ";
	    addr_str += street_name;
	}
	if(!street_type.equals("")){
	    if(!addr_str.equals("")) addr_str += " ";
	    addr_str += street_type;
	}		
    }
    //
    // search for rental owners
    //
    public String lookFor(){
	//
	buildAddrStr();		
	Connection con = null;
	PreparedStatement pstmt = null;	
	ResultSet rs = null;
	String qq = "", qo="";
	String q = "select pd.id, "+
	    "pd.property_status,"+
	    "pd.agent,"+  
	    "pd.permit_length,"+
	    "pd.pull_reason,"+
			
	    "pd.grandfathered,"+
	    "pd.units,"+
	    "pd.structures,"+
	    "pd.bedrooms,"+  // 9
	    "pd.occ_load,"+
			
	    "to_char(pd.registered_date,'mm/dd/yyyy'), "+
	    "to_char(pd.last_cycle_date,'mm/dd/yyyy'), "+
	    "to_char(pd.permit_issued,'mm/dd/yyyy'), "+
	    "to_char(pd.permit_expires,'mm/dd/yyyy'), "+
	    "to_char(pd.pull_date,'mm/dd/yyyy'), "+
			
	    "to_char(pd.date_billed,'mm/dd/yyyy'), "+
	    "to_char(pd.date_rec,'mm/dd/yyyy'), "+
	    " pd.notes,"+
	    " pd.cdbg_funding, "+ 
	    " pd.zoning2, "+ // 20
	    // old ps
	    " pd.prop_type,"+
	    " pd.bath_count,"+
	    " pd.nhood, "+
	    " to_char(pd.built_date,'mm/dd/yyyy'), "+
	    " pd.inactive,"+ // 25
			
	    " pd.affordable,"+
	    " pd.building_type, "+ // 27
	    " pd.accessory_dwelling ";;
	String qf = " from registr pd "+
	    "left outer join name ag on pd.agent=ag.name_num "+						
	    "left outer join regid_name rn on pd.id=rn.id "+
	    "left outer join name od on rn.name_num=od.name_num ";							
	// "left outer join address2 ad on pd.id = ad.registr_id ";
				
	String qw = "";
	boolean addrTbl = false,
	    ownTbl = false,
	    agntTbl = false,
	    phoneTbl = false,
	    varTbl = false,
	    statusTbl = false,
	    noteTbl = false ;
	String back = "";
	if(!id.equals("")){
	    qw += " pd.id=?";
	    qq = q + qf + "where "+qw;
	}
	else if(!bid.equals("")){
	    qf += ", reg_bills bd";
	    if(!qw.equals("")){
		qw += " and ";
	    }
	    qw += " bd.bid = ? ";
	    qw += " and bd.id=pd.id ";
	    qq = q + qf + " where "+qw;
	}
	else if(!receipt_no.equals("")){
	    qf += ", reg_bills bd";
	    qf += ", reg_paid rd";
	    if(!qw.equals("")){
		qw += " and ";
	    }
	    qw += " rd.receipt_no = ?";
	    qw += " and bd.id=pd.id ";
	    qw += " and rd.bid=bd.bid ";
	    qq = q + qf + " where "+qw;
	}
	else{
	    if(!name_num.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " od.name_num = ?";
		// ownTbl = true;
	    }			
	    if(!own_addr.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " (upper(ag.address) like ? or upper(od.address) like ?)";
		ownTbl = true;
	    }
	    if(!city.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}								
		qw += " (ag.city like ? or od.city like ?)";
		ownTbl = true;
	    }
	    if(!state.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " (ag.state like ? or od.state like ?)";
		ownTbl = true;
	    }
	    if(!zip.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " (ag.zip like ? or od.zip like ?)";
		ownTbl = true;
	    }
	    if(!phone.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " op.phone_num = ?";
		phoneTbl = true;
	    }
	    if(!email.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " (ag.email like ? or od.email like ?)";
		ownTbl = true;
	    }
	    if(!street_num.equals("") || !street_dir.equals("") || !street_name.equals("") || !street_type.equals("") || !post_dir.equals("") || !sud_num.equals("") || !sud_type.equals("")){
		addrTbl = true;
		String qww = "";
		if(!street_num.equals("")){
		    qww += " ad.street_num = ?";
		}
		if(!street_dir.equals("")){
		    if(!qww.equals("")) qww += " and ";
		    qww += " ad.street_dir = ?";
		}
		if(!street_name.equals("")){
		    if(!qww.equals("")) qww += " and ";					
		    qww += " upper(ad.street_name) like ?";
		}			
		if(!street_type.equals("")){
		    if(!qww.equals("")) qww += " and ";
		    qww += " ad.street_type like ?";
		}
		if(!post_dir.equals("")){
		    if(!qww.equals("")) qww += " and ";
		    qww += " ad.post_dir like ?";
		}
		if(!sud_num.equals("")){
		    if(!qww.equals("")) qww += " and ";
		    qww += " ad.sud_num = ?";
		}
		if(!sud_type.equals("")){
		    if(!qww.equals("")) qww += " and ";
		    qww += " ad.sud_type like ?";
		}
		if(!qww.equals("")){
		    if(!qw.equals("")) qw += " and ";										
		    qw += " (("+qww+")";
		    if(!addr_str.equals("")){
			qw +=" or (upper(ad.streetAddress) like '"+Helper.doubleApostrify(addr_str)+"%')";
		    }
		    qw += ")";
		}
	    }
	    if(!streetAddress.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " (ad.street_num||' '||ad.street_dir||' '||upper(ad.street_name)||' '||ad.street_type like ? or upper(streetAddress) like ?) ";
		addrTbl = true;
	    }
	    // rental related
	    if(!dept.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.dept = ?";
	    }
	    if(!agent.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.agent = ?";
	    }	
	    if(!nhood.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.nhood = ?";
	    }
						
	    if(!property_status.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.property_status = ?";
	    }
	    if(!bedrooms.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.bedrooms = ?";
	    }
	    if(!bath_count.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.bath_count = ?"; // to pd.
	    }
	    if(!occ_load.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.occ_load = ?";
	    }
	    if(!zoning.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.zoning2 = ?";
	    }
	    if(!permit_length.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.permit_length = ?";
	    }
	    if(!units.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.units = ?";
	    }
	    if(!structures.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.structures = ?";
	    }
	    if(!pull_reason.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.pull_reason = ?";
	    }
	    if(!prop_type.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.prop_type = ?"; // to pd.
	    }
	    if(!which_date.equals("")){
		if(!date_from.equals("")){
		    if(!qw.equals("")){
			qw += " and ";
		    }
		    qw += " pd."+which_date+" >= ? ";
		}
		if(!date_to.equals("")){
		    if(!qw.equals("")){
			qw += " and ";
		    }
		    qw += " pd."+which_date+" <= ? ";
		}			
	    }	
	    //
	    if(!inactive.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.inactive is not null "; // to pd.
	    }
	    if(!cdbg_funding.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.cdbg_funding = 'Y' ";
	    }
	    if(!grandfathered.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.grandfathered = 'Y' ";
	    }
	    if(!accessory_dwelling.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.accessory_dwelling is not null ";
	    }						
	    if(!active.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.inactive is null "; // to pd.
	    }
	    if(!variance.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw +=  " vr.variance like ?";
		varTbl = true;
	    }
	    if(!hasVariance.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " vr.variance is not null ";
		varTbl = true;
	    }
	    if(!own_name.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " (upper(od.name) like ? or "+
		    " upper(ag.name) like ? )";				
		ownTbl = true;
	    }
	    if(!building_type.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw +=  " pd.building_type = ? ";  // to pd.
		varTbl = true;
	    }
	    if(!affordable.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.affordable is not null "; // to pd.
	    }
	    if(!notes.equals("")){
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw +=  " rnt.notes like ? ";  // to pd.
		noteTbl = true;
	    }
	    if(sortBy.equals("")){
		qo = " order by pd.id desc ";
	    }
	    else {
		if(sortBy.equals("pull_d"))
		    qo = " order by pd.pull_date ";
		else if(sortBy.equals("reg_d"))
		    qo = " order by pd.registered_date ";
		else if(sortBy.equals("cycle_d")){
		    qo = " order by pd.last_cycle_date ";
		}
		else if(sortBy.equals("issue_d")){
		    qo = " order by pd.permit_issued ";
		}
		else if(sortBy.equals("expire_d")){
		    qo = " order by pd.permit_expires ";
		}
		else if(sortBy.equals("bill_d")){
		    qo = " order by pd.date_billed ";
		}
		else if(sortBy.equals("rec_d")){
		    qo = " order by pd.date_rec ";
		}
		else if(sortBy.equals("agent")){
		    qo = " order by ag.name ";
		}
		else if(sortBy.equals("owner")){
		    qo = " order by od.name ";
		    ownTbl = true;
		}
		else if(sortBy.equals("addr")){
		    qo = " order by ad.street_name,ad.street_dir,ad.street_type,ad.street_num ";
		    addrTbl=true;
		}
		else{
		    qo =" order by "+sortBy;
		}
	    }
	    if(ownTbl){
		/*
		  qf += ", name od ";
		  if(!qw.equals("")){
		  qw += " and ";
		  }
		  qw += " od.name_num = rn.name_num ";
		*/
	    }
	    if(addrTbl){
		qf += "left outer join address2 ad on pd.id = ad.registr_id ";
	    }
	    if(varTbl){
		qf += ", variances vr ";
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " vr.id = pd.id ";
	    }
	    if(phoneTbl){

		qf += ", owner_phones op ";
		if(!qw.equals("")){
		    qw += " and ";
		}								
		qw += " op.name_num=od.name_num ";
	    }
	    if(noteTbl){
		qf += ", rental_notes rnt ";
		if(!qw.equals("")){
		    qw += " and ";
		}
		qw += " pd.id = rnt.rental_id ";								
	    }
	    if(!qw.equals("")){
		qq = q + qf + " where "+qw + qo;
	    }
	    else{
		qq = q + qf + qo;
	    }
	}
	if(debug){
	    logger.debug(qq);
	}
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    System.err.println("Error: "+back);
	    return back;
	}		
	try{
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(!id.equals("")){
		pstmt.setString(jj++, id);
	    }
	    else if(!bid.equals("")){
		pstmt.setString(jj++, bid);
	    }
	    else if(!receipt_no.equals("")){
		pstmt.setString(jj++, receipt_no);
	    }
	    else {
		if(!name_num.equals("")){
		    pstmt.setString(jj++, name_num);
		}	
		if(!own_addr.equals("")){
		    pstmt.setString(jj++, "%"+own_addr+"%");
		    pstmt.setString(jj++, "%"+own_addr+"%");
		}
		if(!city.equals("")){
		    pstmt.setString(jj++, city);
		    pstmt.setString(jj++, city);
		}
		if(!state.equals("")){
		    pstmt.setString(jj++, state);
		    pstmt.setString(jj++, state);
		}
		if(!zip.equals("")){
		    pstmt.setString(jj++, zip);
		    pstmt.setString(jj++, zip);
		}
		if(!phone.equals("")){ 
		    pstmt.setString(jj++, phone);
		}
		if(!email.equals("")){
		    pstmt.setString(jj++, email);
		}
		if(!street_num.equals("")){
		    pstmt.setString(jj++, street_num);
		}
		if(!street_dir.equals("")){
		    pstmt.setString(jj++, street_dir);
		}
		if(!street_name.equals("")){
		    street_name = "%"+street_name+"%";
		    pstmt.setString(jj++, street_name);
		}
		if(!street_type.equals("")){
		    pstmt.setString(jj++, street_type);
		}
		if(!post_dir.equals("")){
		    pstmt.setString(jj++, post_dir);
		}
		if(!sud_num.equals("")){
		    pstmt.setString(jj++, sud_num);
		}
		if(!sud_type.equals("")){
		    pstmt.setString(jj++, sud_type);
		}
		if(!dept.equals("")){
		    pstmt.setString(jj++, dept);
		}
		if(!agent.equals("")){
		    pstmt.setString(jj++, agent);
		}				
		if(!nhood.equals("")){
		    pstmt.setString(jj++, nhood);
		}
		if(!property_status.equals("")){
		    pstmt.setString(jj++, property_status);
		}				
		if(!bedrooms.equals("")){
		    pstmt.setString(jj++, bedrooms);
		}
		if(!bath_count.equals("")){
		    pstmt.setString(jj++, bath_count);
		}
		if(!occ_load.equals("")){
		    pstmt.setString(jj++, occ_load);
		}
		if(!zoning.equals("")){
		    pstmt.setString(jj++, zoning);
		}
		if(!permit_length.equals("")){
		    pstmt.setString(jj++, permit_length);
		}
		if(!units.equals("")){
		    pstmt.setString(jj++, units);
		}
		if(!structures.equals("")){
		    pstmt.setString(jj++, structures);
		}
		if(!pull_reason.equals("")){
		    pstmt.setString(jj++, pull_reason);
		}
		if(!prop_type.equals("")){
		    pstmt.setString(jj++, prop_type);
		}
		if(!which_date.equals("")){
		    if(!date_from.equals("")){
			pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(date_from).getTime()));
		    }
		    if(!date_to.equals("")){
			pstmt.setDate(jj++, new java.sql.Date(dateFormat.parse(date_to).getTime()));
		    }	
		}
		if(!streetAddress.equals("")){
		    pstmt.setString(jj++, streetAddress+"%");
		    pstmt.setString(jj++, streetAddress+"%");					
		}
		if(!variance.equals("")){
		    pstmt.setString(jj++, "%"+variance+"%");
		}
		if(!own_name.equals("")){
		    own_name="%"+own_name+"%";
		    pstmt.setString(jj++, own_name);
		    pstmt.setString(jj++, own_name);
		}	
		if(!building_type.equals("")){
		    pstmt.setString(jj++, building_type);
		}
		if(!notes.equals("")){
		    pstmt.setString(jj++, "%"+notes+"%");
		}								
	    }
	    rs = pstmt.executeQuery();
	    jj =1;
	    while(rs.next()){
		String strs[] = new String[28];
				
		for(int i=0;i<strs.length;i++) strs[i]="";
		for(int i=1;i<strs.length+1;i++){
		    strs[i-1] = rs.getString(i);
		}
								
		Rent rent = new Rent(debug, strs);
		if(!this.contains(rent))
		    add(rent);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+": "+qq);
	    back += ex+": "+qq;
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }
	
}























































