package rental.model;

import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;

public class Inspection{

    boolean debug = false;
    static Logger logger = LogManager.getLogger(Inspection.class);
    final static long serialVersionUID = 450L;
    public final static String table_name="inspections";
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    String inspection_type="", inspection_type_name="",
	id="", //insp_id
	registr_id="", inspection_date="",
	compliance_date="", violations="0",
	insp_file="", 
	comments="",inspected_by="", heat_src="",
	story_cnt="",
	foundation="",attic="",accessory="",
	smook_detectors="0",
	life_safety="0",
	time_status="In Progress";
    String has_affidavit="";//Yes, No, N/A
    double time_spent = 0; // inspection duration 
    Inspector inspector = null;
    public Inspection(boolean val){
	debug = val;
    }
    public Inspection(boolean deb,
		      String val,
		      String val2,
		      String val3,
		      String val4,
		      String val5,
		      String val6,
		      String val7,
		      String val8,
		      String val9,
		      String val10,
		      String val11,
		      String val12,
		      String val13,
		      String val14,
		      String val15,
		      String val16,
		      String val17,
		      String val18,
		      String val19
		      ){
	debug = deb;
	setId(val);
	setInspectionDate(val2);
	setInspectionType(val3);
	setComplianceDate(val4);
	setViolations(val5);
	setInspectedBy(val6);
	setInspFile(val7);
	setComments(val8);
	setFoundation(val9);
	setAttic(val10);
	setAccessory(val11);
	setStoryCnt(val12);
	setHeatSrc(val13);
	setSmookDetectors(val14);
	setLifeSafety(val15);
	setRegistrId(val16);
	setTimeSpent(val17);
	setTimeStatus(val18);
	setHasAffidavit(val19);
    }	
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setRegistrId(String val){
	if(val != null)
	    registr_id = val;
    }
    public void setRental_id(String val){
	if(val != null)
	    registr_id = val;
    }		
    public void setInspectionType(String val){
	if(val != null)
	    inspection_type = val;
    }
    public void setInspectedBy(String val){
	if(val != null)
	    inspected_by = val;
    }
    public void setInspectionDate(String val){
	if(val != null)
	    inspection_date = val;
    }
    public void setComplianceDate(String val){
	if(val != null)
	    compliance_date = val;
    }
    public void setViolations(String val){
	if(val != null)
	    violations = val;
    }
    public void setComments(String val){
	if(val != null)
	    comments = val;
    }
    public void setInspFile(String val){
	if(val != null)
	    insp_file = val;
    }
    public void setHeatSrc(String val){
	if(val != null)
	    heat_src = val;
    }
    public void setFoundation(String val){
	if(val != null)
	    foundation = val;
    }
    public void setAttic(String val){
	if(val != null)
	    attic = val;
    }
    public void setHasAffidavit(String val){
	if(val != null)
	    has_affidavit = val;
    }		
    public void setAccessory(String val){
	if(val != null)
	    accessory = val;
    }
    public void setStoryCnt(String val){
	if(val != null)
	    story_cnt = val;
    }
    public void setSmookDetectors(String val){
	if(val != null)
	    smook_detectors = val;
    }
    public void setLifeSafety(String val){
	if(val != null)
	    life_safety = val;
    }
    public void setTimeSpent(String val){
	if(val != null && !val.equals("")){
	    try{
		time_spent = Double.parseDouble(val);
	    }catch(Exception ex){}
	}
    }
    public void setTimeStatus(String val){
	if(val != null)
	    time_status = val;
    }
    //
    // getters
    //
    public String getId(){
	return id ;
    }
    public String getRegistrId(){
	return registr_id ;
    }
    public String getRental_id(){
	return registr_id ;
    }		
    public String getInspectionType(){
	return inspection_type ;
    }
    public String getInspectionTypeName(){
	if(inspection_type_name.equals("") && !inspection_type.equals("")){
	    InspectTypeList il = new InspectTypeList(debug, inspection_type);
	    String back = il.find();
	    if(back.equals("") && il.size() > 0){
		Item one = il.get(0);
		inspection_type_name = one.getName();
	    }
	}
	return inspection_type_name ;
    }
    public String getInspectedBy(){
	return inspected_by ;
    }
    public String getInspectionDate(){
	return inspection_date ;
    }
    public String getComplianceDate(){
	return compliance_date ;
    }
    public String getViolations(){
	return violations ;
    }
    public String getComments(){
	return comments ;
    }
    public String getInspFile(){
	return insp_file ;
    }
    public String getHeatSrc(){
	return heat_src ;
    }
    public String getFoundation(){
	return foundation ;
    }
    public String getAttic(){
	return attic;
    }
    public String getHasAffidavit(){
	return has_affidavit;
    }		
    public String getStoryCnt(){
	return story_cnt;
    }
    public String getAccessory(){
	return accessory;
    }
    public String getSmookDetectors(){
	return smook_detectors;
    }
    public String getLifeSafety(){
	return life_safety;
    }
    public String getTimeStatus(){
	return time_status;
    }
    public double getTimeSpent(){
	return time_spent;
    }
    public Inspector getInspector(){
	if(inspector == null){
	    findInspector();
	}
	return inspector;
    }
    public String getInspFileName(){
	String ret = "";
	if(!insp_file.equals("")){
	    int n = insp_file.lastIndexOf("\\");
	    /*
	      if(n == -1){
	      n = insp_file.lastIndexOf("/");
	      }
	    */
	    if(n > -1){
		ret = insp_file.substring(n+1);
	    }
	    else{
		ret = insp_file;
	    }
	}
	return ret ;
    }	
    public String doSave(){

	String today = Helper.getToday();
	String qq = "";
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	String back="";
	if(!inspection_date.equals("")){
	    String dmm = inspection_date.substring(0,2);  // nonth
	    String dyy = inspection_date.substring(6,10); // year
	    insp_file =  dyy+"\\"+dmm+"\\"+insp_file;
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = " Could not connect to DB ";
		return back;
	    }
	    qq = "select inspection_seq.nextval from dual ";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt2 = con.prepareStatement(qq);
	    rs = pstmt2.executeQuery();
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = "insert into "+table_name+"(ID,INSPECTION_DATE,INSPECTION_TYPE,COMPLIANCE_DATE,VIOLATIONS,INSPECTED_BY,INSP_FILE,TIME_SPENT,TIME_STATUS,COMMENTS,FOUNDATION,ATTIC,ACCESSORY,STORY_CNT,HEAT_SRC,INSP_ID,SMOOK_DETECTORS,LIFE_SAFETY,HAS_AFFIDAVIT) values ("+
		"?,?,?,?,?,"+
		"?,?,?,?,?,"+
		"?,?,?,?,?, ?,?,?,?)";
	    if(debug){
		logger.debug(qq);
	    }	
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,registr_id);
				
	    if(inspection_date.equals(""))
		pstmt.setString(2, null);
	    else
		pstmt.setDate(2, new java.sql.Date(dateFormat.parse(inspection_date).getTime()));	
	    if(inspection_type.equals(""))
		pstmt.setString(3, null);
	    else
		pstmt.setString(3, inspection_type);
	    if(compliance_date.equals(""))
		pstmt.setString(4, null);
	    else
		pstmt.setDate(4, new java.sql.Date(dateFormat.parse(compliance_date).getTime()));	   
	    if(violations.equals("")) violations="0";
	    pstmt.setString(5, violations);
	    if(inspected_by.equals(""))
		pstmt.setString(6, null);
	    else
		pstmt.setString(6, inspected_by);
	    if(insp_file.equals(""))
		pstmt.setString(7, null);
	    else
		pstmt.setString(7, insp_file);
	    pstmt.setDouble(8, time_spent);
	    if(inspection_type.equals("CYCL")){
		if(time_spent > 0)
		    time_status="Completed";
	    }
	    else{
		time_status="Completed";
	    }
	    pstmt.setString(9, time_status);						
	    if(comments.equals(""))
		pstmt.setString(10, null);				
	    else
		pstmt.setString(10, comments);
	    if(foundation.equals(""))
		pstmt.setString(11, null);	
	    else
		pstmt.setString(11, foundation);
	    if(attic.equals(""))
		pstmt.setString(12, null);	
	    else
		pstmt.setString(12,	attic);
	    if(accessory.equals(""))
		pstmt.setString(13, null);	
	    else
		pstmt.setString(13,	accessory);
	    if(story_cnt.equals(""))
		pstmt.setString(14, null);					
	    else
		pstmt.setString(14, story_cnt);
	    if(heat_src.equals(""))
		pstmt.setString(15, null);				
	    else
		pstmt.setString(15, heat_src);
						
	    pstmt.setString(16,id);						
	    if(smook_detectors.equals(""))
		pstmt.setString(17, "0");				
	    else
		pstmt.setString(17, smook_detectors);
	    if(life_safety.equals(""))
		pstmt.setString(18, "0");				
	    else
		pstmt.setString(18, life_safety);			
	    if(has_affidavit.equals(""))
		pstmt.setString(19, null);				
	    else
		pstmt.setString(19, has_affidavit);	
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}
	return back;
    }
    //
    public String doUpdate(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	String back = "", qq = "";
	try{
	    qq = "update "+table_name+" set "+
		" compliance_date=?, "+
		" violations=?, "+
		" smook_detectors=?, "+
		" life_safety=?, "+
		" time_spent=?, "+
		" time_status=?, "+
		" has_affidavit=? "+
		" where insp_id=?";
	    if(debug){
		logger.debug(qq);
	    }	
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }		
	    pstmt = con.prepareStatement(qq);
	    if(compliance_date.equals(""))
		pstmt.setString(1, null);
	    else
		pstmt.setDate(1, new java.sql.Date(dateFormat.parse(compliance_date).getTime()));
	    if(violations.equals(""))
		pstmt.setString(2, "0");				
	    else
		pstmt.setString(2, violations);
	    if(smook_detectors.equals(""))
		pstmt.setString(3, "0");				
	    else
		pstmt.setString(3, smook_detectors);
	    if(life_safety.equals(""))
		pstmt.setString(4, "0");				
	    else
		pstmt.setString(4, life_safety);
	    pstmt.setDouble(5, time_spent);
	    if(time_spent > 0)
		time_status="Completed";
	    //
	    // other than Cycle type
	    //
	    if(!inspection_type.equals("CYCL")){
		time_status="Completed";
	    }
	    pstmt.setString(6, time_status);
	    if(has_affidavit.equals(""))
		pstmt.setString(7, null);				
	    else
		pstmt.setString(7, has_affidavit);	
	    pstmt.setString(8, id);			
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;
    }

    public String doSelect(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
		
	String back = "", qq = "", str="";
	try{
	    qq = "select i.id,"+
		"to_char(i.inspection_date,'mm/dd/yyyy'),"+
		"i.inspection_type,"+
		"to_char(i.compliance_date,'mm/dd/yyyy'),"+
		"i.violations,"+
		"i.inspected_by,"+
		"i.insp_file,"+
		"i.comments,"+
		"i.foundation,"+
		"i.attic,"+
		"i.accessory,"+
		"i.story_cnt,"+
		"i.heat_src,"+
		"i.smook_detectors,"+
		"i.life_safety,t.insp_desc, "+
		"i.time_spent,i.time_status, "+
		"i.has_affidavit "+
		" from "+table_name+" i, inspection_types t where i.insp_id=? "+
		" and t.insp_type=i.inspection_type ";
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
		str = rs.getString(1);
		if(str != null)
		    registr_id = str;
		str = rs.getString(2);
		if(str != null)
		    inspection_date = str;
		str = rs.getString(3);
		if(str != null)
		    inspection_type = str;
		str = rs.getString(4);
		if(str != null)
		    compliance_date = str;
		str = rs.getString(5);
		if(str != null && !str.equals("0"))
		    violations = str;
		str = rs.getString(6);
		if(str != null)
		    inspected_by = str;
		str = rs.getString(7);
		if(str != null){
		    insp_file = str;
		}
		str = rs.getString(8);
		if(str != null)
		    comments = str;				
		str = rs.getString(9);
		if(str != null)
		    foundation = str;
		str = rs.getString(10);
		if(str != null)
		    attic = str;
		str = rs.getString(11);
		if(str != null)
		    accessory = str;
		str = rs.getString(12);
		if(str != null)
		    story_cnt = str;
		str = rs.getString(13);
		if(str != null)
		    heat_src = str;
		str = rs.getString(14);
		if(str != null)
		    smook_detectors = str;
		str = rs.getString(15);
		if(str != null)
		    life_safety = str;
		str = rs.getString(16);
		if(str != null)
		    inspection_type_name = str;
		time_spent = rs.getDouble(17);
		str = rs.getString(18);
		if(str != null)
		    time_status = str;
		str = rs.getString(19);
		if(str != null)
		    has_affidavit = str;								
	    }
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;	
    }
    public String doSaveFile(){

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	String back = "", qq = "";
	if(insp_file.equals("")){
	    back = " No file found to be saved ";
	    return back;
	}
	try{
	    qq = "update "+table_name+" set "+
		"insp_file=? "+
		"where insp_id=?";
	    if(debug){
		logger.debug(qq);
	    }	
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }		
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, insp_file);
	    pstmt.setString(2, id);			
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex+":"+qq;
	    logger.error(back);
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
	String qq = "delete from "+table_name+" where insp_id=?";
	if(debug){
	    logger.debug(qq);
	}
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		return back;
	    }			
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,id);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += " Could not delete "+ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;	

    }
    void findInspector(){
	if(!inspected_by.equals("")){
	    inspector = new Inspector(inspected_by, debug);
	    inspector.doSelect();
	}
    }
    //
    public String createFileName(){
		
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

		
	// for a new file we need a symbol according to inspection type
	// and a sequence based on the previous inspection count
	// Symbols are of type cy, cc, ee, rv, tv, fr, hm
	// and sequence will be 001, 002,..
	Hashtable<String, String> map = new Hashtable<String, String>(8);
	map.put("CYCL","cy");
	map.put("REIN","rv");
	map.put("COMP","co");
	map.put("FIRE","fr");
	map.put("TV","tv");
	map.put("EE","ee");
	map.put("HOME","hm");
	map.put("PRMT","pt");
	map.put("SHTR","sh");				
		
	String str = "", str2="", name="", prefix="", back="";
		
	List<String> files = new ArrayList<String>();
	String qq = "select insp_file from "+table_name+" where id = ?"+
	    " and inspection_type like ?";
	try{
	    con = Helper.getConnection();
	    if(con == null){
		back = "Could not connect to Database ";
		logger.error(back);
		return "";
	    }
	    if(debug)
		logger.debug(qq);
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,registr_id);
	    pstmt.setString(2,inspection_type);
	    rs = pstmt.executeQuery();
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null) files.add(str.toLowerCase());
	    }
	}
	catch(Exception ex){
	    back += " Could not query "+ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	if(!inspection_type.equals("")){
	    prefix = map.get(inspection_type);
	}
	if(files.size() == 0){
	    name = registr_id+prefix+"_001.doc"; // we start with one
	    return name;
	}
	else{
	    int max_n = 0;
	    for(String fname:files){
		int n = -1;
		String str3 = "";
		str = fname;
		if(str.contains("_")){
		    n = str.indexOf("_");
		}
		else if(str.contains(".")){
		    n = str.indexOf("."); // some old files have 4324.cy004.doc
		}
		if(n > -1){
		    str = str.substring(n+1);
		}
		if(str.indexOf(".") > -1){
		    int m = str.indexOf(".");
		    if(m > 0){
			str = str.substring(0, m);
		    }
		}
		n = 0;
		try{
		    n = Integer.parseInt(str);
		    if(n > max_n) max_n = n;
		}
		catch(Exception ex){
		    logger.debug(ex+" : "+str);// ignore if no number found
		}
	    }
	    max_n++;
	    name = registr_id+prefix+"_";
	    if(max_n >= 100) name += ""+max_n;
	    else if(max_n >= 10) name += "0"+max_n;
	    else name += "00"+max_n;
	    name += ".doc";
	    return name;
	}
    }
	
}






















































