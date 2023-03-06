package rental.model;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Case {

    final static long serialVersionUID = 150L;
    boolean debug = false, basic = false;
    static Logger logger = LogManager.getLogger(Case.class);
    String 
	id="", citeId="",source="", citation_num="",
	per_day="",
	dob="",ssn="",case_type="",status="PD",
	ini_hear_date="",contest_hear_date="", post_dir="",
	misc_hear_date="", received="", filed="", pro_supp="",
	ini_hear_time="08:30",contest_hear_time="", e41_date="",
	misc_hear_time="", pro_supp_time="", mcc_flag="", rule_date="",rule_time="",
	judgment_date="",pro_supp_date="",compliance_date="",
	judgment_amount="", sent_date="", invld_addr="", addr_req_date="",
	fine="",court_cost="",last_paid_date="",closed_date="",
	closed_comments="", comments="", trans_collect_date="",
	lawyerid="", citation_date="";
	
    List<Defendant> defendants = null;
    List<LegalAddress> addresses = null;
    List<String> causeNums = null;
    public Case(boolean val){
	debug = val;
    }
	
    public Case(String val, boolean deb){
	id = val;
	debug = deb;
    }

    public Case(String val, boolean deb, boolean base){
	id = val;
	debug = deb;
	basic = base; // when not all features are needed;
    }
    public Case(boolean val, String[] arr){
	debug = val;
	if(arr != null && arr.length > 0){
	    setId(arr[0]);
	    setCase_type(arr[1]);
	    setStatus(arr[2]);
	    setIni_hear_time(arr[3]);
	    setContest_hear_time(arr[4]);
	    setMisc_hear_time(arr[5]);
	    setJudgment_amount(arr[6]);
	    setFine(arr[7]);
	    setCourt_cost(arr[8]);
	    setIni_hear_date(arr[9]);
			
	    setContest_hear_date(arr[10]);
	    setMisc_hear_date(arr[11]);
	    setPro_supp_date(arr[12]);
	    setReceived(arr[13]);
	    setFiled(arr[14]);
	    setCompliance_date(arr[15]);
	    setJudgment_date(arr[16]);
	    setSent_date(arr[17]);
	    setLast_paid_date(arr[18]);
	    setClosed_date(arr[19]);
			
	    setClosed_comments(arr[20]);
	    setComments(arr[21]);
	    setPro_supp_time(arr[22]);
	    setPer_day(arr[23]);
	    setMcc_flag(arr[24]);
	    setPro_supp(arr[25]);
	    setLawyerid(arr[26]);
	    setRule_date(arr[27]);
	    setRule_time(arr[28]);
	    setE41_date(arr[29]);
			
	    setTrans_collect_date(arr[30]);
	    setCitation_date(arr[31]);
	    setCitation_num(arr[32]);
	}
    }	
    //
    //setters
    //
	
    public void setId(String val){
	if(val != null)
	    id = val;
    }
    public void setReceived(String val){
	if(val != null)
	    received = val;
    }
    public void setSource(String val){
	if(val != null)
	    source = val;
    }
    public void setCitation_num(String val){
	if(val != null)
	    citation_num = val;
    }	
    public void setCase_type(String val){
	if(val != null)
	    case_type = val;
    }
    public void setStatus(String val){
	if(val != null)
	    status = val;
    }
    public void setTrans_collect_date(String val){
	if(val != null)
	    trans_collect_date = val;
    }	
    public void setSent_date(String val){
	if(val != null)
	    sent_date = val;
    }
    public void setCitation_date(String val){
	if(val != null)
	    citation_date = val;
    }	
    public void setIni_hear_date(String val){
	if(val != null)
	    ini_hear_date = val;
    }
    public void setIni_hear_time(String val){
	if(val != null)
	    ini_hear_time = val;
    }
    public void setContest_hear_date(String val){
	if(val != null)
	    contest_hear_date = val;
    }
    public void setContest_hear_time(String val){
	if(val != null)
	    contest_hear_time = val;
    }
    public void setMisc_hear_date(String val){
	if(val != null)
	    misc_hear_date = val;
    }
    public void setMisc_hear_time(String val){
	if(val != null)
	    misc_hear_time = val;
    }
    public void setFiled(String val){
	if(val != null)
	    filed = val;
    }
    public void setJudgment_date(String val){
	if(val != null)
	    judgment_date = val;
    }
    public void setCompliance_date(String val){
	if(val != null)
	    compliance_date = val;
    }
    public void setPro_supp_date(String val){
	if(val != null)
	    pro_supp_date = val;
    }
    public void setJudgment_amount(String val){
	if(val != null)
	    judgment_amount = val;
    }
    public void setFine(String val){
	if(val != null)
	    fine = val;
    }
    public void setCourt_cost(String val){
	if(val != null)
	    court_cost = val;
    }
    public void setLast_paid_date(String val){
	if(val != null)
	    last_paid_date = val;
    }
    public void setClosed_date(String val){
	if(val != null)
	    closed_date = val;
    }
    public void setClosed_comments(String val){
	if(val != null)
	    closed_comments = val;
    }
    public void setComments(String val){
	if(val != null)
	    comments = val;
    }
    public void setPro_supp_time(String val){
	if(val != null)
	    pro_supp_time = val;
    }
    public void setPer_day(String val){
	if(val != null)
	    per_day = val;
    }
    public void setMcc_flag(String val){
	if(val != null)
	    mcc_flag = val;
    }
    public void setPro_supp(String val){
	if(val != null)
	    pro_supp = val;
    }
    public void setLawyerid(String val){
	if(val != null)
	    lawyerid = val;
    }
    public void setRule_date(String val){
	if(val != null)
	    rule_date = val;
    }
    public void setRule_time(String val){
	if(val != null)
	    rule_time = val;
    }
    public void setE41_date(String val){
	if(val != null)
	    e41_date = val;
    }
    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getCase_type(){
	return case_type ;
    }
    public String getStatus(){
	return status ;
    }
    public String getCitation_num(){
	return citation_num ;
    }	
    public String getSent_date(){
	return sent_date ;
    }
    public String getTrans_collect_date(){
	return trans_collect_date ;
    }	
    public String getIni_hear_date(){
	return ini_hear_date ;
    }
    public String getIni_hear_time(){
	return ini_hear_time ;
    }
    public String getContest_hear_date(){
	return contest_hear_date ;
    }
    public String getContest_hear_time(){
	return contest_hear_time ;
    }
    public String getMisc_hear_date(){
	return misc_hear_date ;
    }
    public String getMisc_hear_time(){
	return misc_hear_time ;
    }
    public String getFiled(){
	return filed ;
    }
    public String getJudgment_date(){
	return judgment_date ;
    }
    public String getCompliance_date(){
	return compliance_date ;
    }
    public String getPro_supp_date(){
	return pro_supp_date ;
    }
    public String getJudgment_amount(){
	return judgment_amount ;
    }
    public String getFine(){
	return fine ;
    }
    public String getCourt_cost(){
	return court_cost ;
    }
    public String getLast_paid_date(){
	return last_paid_date ;
    }
    public String getClosed_date(){
	return closed_date ;
    }
    public String getClosed_comments(){
	return closed_comments ;
    }
    public String getComments(){
	return comments ;
    }
    public String getPro_supp_time(){
	return pro_supp_time ;
    }
    public String getPer_day(){
	return per_day ;
    }
    public String getMcc_flag(){
	return mcc_flag ;
    }
    public String getPro_supp(){
	return pro_supp ;
    }
    public String getLawyerid(){
	return lawyerid ;
    }
    public String getRule_date(){
	return rule_date ;
    }
    public String getRule_time(){
	return rule_time ;
    }
    public String getE41_date(){
	return e41_date ;
    }
    public String getCitation_date(){
	return citation_date ;
    }	
    public String getReceived(){
	return received ;
    }
    public List<Defendant> getDefendants(){
	if(defendants == null){
	    findDefendants();
	}
	return defendants;
		
    }
    public List<LegalAddress> getAddresses(){
	if(addresses == null){
	    findAddresses();
	}
	return addresses;
    }
    public String findLawyerFromCaseType(){
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	if(case_type.equals("")){
	    return "No case type specified";
	}
	String qq = "select lawyerid from legal_lawyer_types where typeId='"+
	    case_type+"'";		
	String back = "";
	try{
	    con = Helper.getLegalConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		logger.error(back);
		return back;
	    }
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		lawyerid = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    back = ex+":"+qq;
	    logger.error(back);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;

    }
    //
    // one of these dates must be entered when the data are
    // entered the first time
    //
    private boolean validateRequiredDates(){
	return true;
    }
    /*
     * find out if this case has letter texts associated with
     * the violation type
     */
    public boolean hasLetterText(){

	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
		
	String qq = "select count(*) from doc_texts where type='"+
	    case_type+"'", back = "";
	boolean ret = false;
	if(case_type.equals("")) return false;
	try{
	    con = Helper.getLegalConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		logger.error(back);
		return false;
	    }
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		if(rs.getInt(1) > 0) ret = true;
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return ret;
    }
    public String doSave(){
	//
	String back = "";
	Connection con = null;
	Statement stmt = null, stmt2=null;
	ResultSet rs = null;
		
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
		
	String qq = "";			
	try{
	    qq = "insert into legal_cases values (0,"; // auto_incr
	    if(received.equals("")){
		Calendar current_cal = Calendar.getInstance();
		received = Helper.getToday();
	    }
	    //
	    //  qq += "sysdate,";  // system date 
	    //  we replace sysdate with entry_date to ignore
	    //  the time portion of the date
	    //
	    qq += "str_to_date('"+received+"','%m/%d/%Y'),";
	    if(case_type.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+case_type+"',";
	    }
	    if(status.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+status+"',";
	    }
	    if(sent_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+sent_date+"','%m/%d/%Y'),"; 
	    if(ini_hear_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+ini_hear_date+"','%m/%d/%Y'),"; 
	    if(ini_hear_time.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+ini_hear_time+"',";
	    }
	    if(contest_hear_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+contest_hear_date+"','%m/%d/%Y'),"; 
	    if(contest_hear_time.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+contest_hear_time+"',";
	    }
	    if(misc_hear_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+misc_hear_date+"','%m/%d/%Y'),"; 
	    if(misc_hear_time.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+misc_hear_time+"',";
	    }
	    if(filed.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+filed+"','%m/%d/%Y'),"; 
	    if(judgment_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+judgment_date+"','%m/%d/%Y'),"; 
	    if(compliance_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+compliance_date+"','%m/%d/%Y'),"; 
	    if(pro_supp_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+pro_supp_date+"','%m/%d/%Y'),"; 
	    if(judgment_amount.equals(""))
		qq += "0,";
	    else
		qq += judgment_amount+",";
	    if(fine.equals(""))
		qq += "0,";
	    else
		qq += fine+",";
	    if(court_cost.equals(""))
		qq += "0,";
	    else
		qq += court_cost+",";
	    if(last_paid_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+last_paid_date+"','%m/%d/%Y'),"; 
	    if(closed_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+closed_date+"','%m/%d/%Y'),"; 
	    if(closed_comments.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+closed_comments+"',";
	    }
	    if(comments.equals("")){
		qq += "null,";
	    }
	    else{
		qq += "'"+Helper.escapeIt(comments)+"',";
	    }
	    if(pro_supp_time.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+pro_supp_time+"',";
	    }
	    if(per_day.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+per_day+"',";
	    }
	    if(mcc_flag.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'y',";
	    }
	    if(pro_supp.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'y',";
	    }
	    if(lawyerid.equals("")){
		qq += "null,";
	    }
	    else {
		qq += "'"+lawyerid+"',";
	    }
	    if(rule_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+rule_date+"','%m/%d/%Y'),"; 
	    if(rule_time.equals(""))
		qq += "null,"; 
	    else
		qq += "'"+rule_time+"',";
	    if(e41_date.equals(""))
		qq += "null"; 
	    else
		qq += "str_to_date('"+e41_date+"','%m/%d/%Y')";
	    qq +=",";
	    if(citation_num.equals(""))
		qq += "null,"; 
	    else
		qq += "'"+citation_num+"',";
	    if(trans_collect_date.equals(""))
		qq += "null,"; 
	    else
		qq += "str_to_date('"+trans_collect_date+"','%m/%d/%Y'),";
	    if(citation_date.equals(""))
		qq += "null"; 
	    else
		qq += "str_to_date('"+citation_date+"','%m/%d/%Y')";			
	    qq += ")";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
			
	    qq = "select LAST_INSERT_ID() ";
	    if(debug){
		logger.debug(qq);
	    }
	    stmt2 = con.createStatement();
	    rs = stmt2.executeQuery(qq);
	    if(rs.next()){
		id = rs.getString(1);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save date ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, rs, stmt, stmt2);
	}
	return back;
    }
    //
    public String doUpdate(){
	//
	String str = "", back = "", oldStatus = "";
	String qq = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
		
	qq = "update legal_cases set ";
	if(case_type.equals(""))
	    qq += "case_type=null,";
	else
	    qq += "case_type='"+case_type+"',";
	if(status.equals(""))
	    qq += "status=null,";
	else
	    qq += "status='"+status+"',";
	if(lawyerid.equals(""))
	    qq += "lawyerid=null,";
	else
	    qq += "lawyerid='"+lawyerid+"',";
	if(sent_date.equals(""))
	    qq += "sent_date=null,";
	else
	    qq += "sent_date=str_to_date('"+sent_date+"','%m/%d/%Y'),";
	if(ini_hear_date.equals(""))
	    qq += "ini_hear_date=null,";
	else
	    qq += "ini_hear_date=str_to_date('"+ini_hear_date+
		"','%m/%d/%Y'),";
	if(ini_hear_time.equals(""))
	    qq += "ini_hear_time=null,";
	else
	    qq += "ini_hear_time='"+ini_hear_time+"',";
	if(contest_hear_date.equals(""))
	    qq += "contest_hear_date=null,";
	else
	    qq += "contest_hear_date=str_to_date('"+contest_hear_date+
		"','%m/%d/%Y'),";
	if(contest_hear_time.equals(""))
	    qq += "contest_hear_time=null,";
	else
	    qq += "contest_hear_time='"+contest_hear_time+"',";
	if(misc_hear_date.equals(""))
	    qq += "misc_hear_date=null,";
	else
	    qq += "misc_hear_date=str_to_date('"+misc_hear_date+
		"','%m/%d/%Y'),";
	if(misc_hear_time.equals(""))
	    qq += "misc_hear_time=null,";
	else
	    qq += "misc_hear_time='"+misc_hear_time+"',";
	if(filed.equals(""))
	    qq += "filed=null,";
	else
	    qq += "filed=str_to_date('"+filed+
		"','%m/%d/%Y'),";
	if(rule_date.equals(""))
	    qq += "rule_date=null,";
	else
	    qq += "rule_date=str_to_date('"+rule_date+"','%m/%d/%Y'),";
		
	if(rule_time.equals(""))
	    qq += "rule_time=null,";
	else
	    qq += "rule_time='"+rule_time+"',";			
	if(judgment_date.equals(""))
	    qq += "judgment_date=null,";
	else
	    qq += "judgment_date=str_to_date('"+judgment_date+
		"','%m/%d/%Y'),";
	if(compliance_date.equals(""))
	    qq += "compliance_date=null,";
	else
	    qq += "compliance_date=str_to_date('"+compliance_date+
		"','%m/%d/%Y'),";
	if(pro_supp_date.equals(""))
	    qq += "pro_supp_date=null,";
	else
	    qq += "pro_supp_date=str_to_date('"+pro_supp_date+
		"','%m/%d/%Y'),";
	if(pro_supp_time.equals(""))
	    qq += "pro_supp_time=null,";
	else
	    qq += "pro_supp_time='"+pro_supp_time+
		"',";
	if(trans_collect_date.equals(""))
	    qq += "trans_collect_date=null,";
	else
	    qq += "trans_collect_date=str_to_date('"+trans_collect_date+"','%m/%d/%Y'),";		
	if(judgment_amount.equals(""))
	    qq += "judgment_amount=0,";
	else
	    qq += "judgment_amount="+judgment_amount+",";
	if(fine.equals(""))
	    qq += "fine=0,";
	else
	    qq += "fine="+fine+",";
	if(court_cost.equals(""))
	    qq += "court_cost=0,";
	else
	    qq += "court_cost="+court_cost+",";
	if(last_paid_date.equals(""))
	    qq += "last_paid_date=null,";
	else
	    qq += "last_paid_date=str_to_date('"+last_paid_date+
		"','%m/%d/%Y'),";
	if(closed_date.equals(""))
	    qq += "closed_date=null,";
	else
	    qq += "closed_date=str_to_date('"+closed_date+
		"','%m/%d/%Y'),";
	if(closed_comments.equals(""))
	    qq += "closed_comments=null,";
	else
	    qq += "closed_comments='"+
		Helper.escapeIt(closed_comments)+"',";
	if(per_day.equals(""))
	    qq += "per_day=null,";
	else
	    qq += "per_day='"+per_day+"',";
	if(mcc_flag.equals(""))
	    qq += "mcc_flag=null,";
	else
	    qq += "mcc_flag='y',";
	if(pro_supp.equals(""))
	    qq += "pro_supp=null,";
	else
	    qq += "pro_supp='y',";
	if(citation_num.equals(""))
	    qq += "citation_num=null,";
	else
	    qq += "citation_num='"+citation_num+"',";
	if(citation_date.equals(""))
	    qq += "citation_date=null,";
	else
	    qq += "citation_date=str_to_date('"+citation_date+"','%m/%d/%Y'),";
	if(comments.equals(""))
	    qq += "comments=null";
	else
	    qq += "comments='"+Helper.escapeIt(comments)+"'";
	qq += " where id="+id;
	//
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    if(!basic){
		findDefendants();
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not update data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String doDelete(){
	//
	String back = "";
	String qq = "delete from legal_payments where id=?";
	String qq2 = "delete from legal_def_case where id=?";
	String qq3 = "delete from legal_case_violations where id=?";
	String qq4 = "delete from legal_cases where id=? ";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    stmt.executeUpdate();
	    qq = qq2;
	    if(debug)			
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);			
	    stmt.executeUpdate();
	    qq = qq3;
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);				
	    stmt.executeUpdate();
	    qq = qq4;
	    if(debug)
		logger.debug(qq);
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);				
	    stmt.executeUpdate(qq);
	    per_day="";
	    case_type="";
	    status="PD"; id="";
	    ini_hear_date="";contest_hear_date="";
	    misc_hear_date=""; received=""; filed="";
	    mcc_flag="";
	    ini_hear_time="08:30";contest_hear_time="";
	    misc_hear_time="";pro_supp_time="";
	    lawyerid="";
	    judgment_date="";pro_supp_date="";compliance_date="";
	    judgment_amount=""; sent_date="";
	    fine="";court_cost="";last_paid_date="";closed_date="";
	    closed_comments=""; comments=""; rule_date="";
	    rule_time=""; e41_date=""; citation_num="";
	    addresses = null;
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not delete data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}					
	return back;
    }
    //
    public String doSelect(){	
	//
	String back = "";
	String qq = "select "+
	    "c.case_type,c.status,"+
	    "c.ini_hear_time,c.contest_hear_time,c.misc_hear_time,"+
	    "c.judgment_amount,c.fine,c.court_cost,"+
	    "date_format(c.ini_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.contest_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.misc_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.pro_supp_date,'%m/%d/%Y'),"+
	    "date_format(c.received,'%m/%d/%Y'),"+
	    "date_format(c.filed,'%m/%d/%Y'),"+
	    "date_format(c.compliance_date,'%m/%d/%Y'),"+
	    "date_format(c.judgment_date,'%m/%d/%Y'),"+
	    "date_format(c.sent_date,'%m/%d/%Y'),"+
	    "date_format(c.last_paid_date,'%m/%d/%Y'),"+
	    "date_format(c.closed_date,'%m/%d/%Y'),"+
	    "c.closed_comments,c.comments,c.pro_supp_time,c.per_day, "+
	    "c.mcc_flag, "+
	    "c.pro_supp, "+
	    "c.lawyerid, "+
	    "date_format(c.rule_date,'%m/%d/%Y'),"+
	    "c.rule_time, "+
	    "date_format(c.e41_date,'%m/%d/%Y'), "+
	    "date_format(c.trans_collect_date,'%m/%d/%Y'), "+
	    "date_format(c.citation_date,'%m/%d/%Y'), "+			
	    "c.citation_num, "+
	    "t.typeDesc,s.statusDesc,l.lawyerid,l.fullName,l.barNum,l.title,l.active "+	
	    " from legal_cases c join legal_case_types t on c.case_type=t.typeId"+
	    " join legal_case_status s on c.status=s.statusId "+
	    " left join legal_lawyers l on c.lawyerid=l.lawyerid "+
	    " where c.id=?";
	String str="";
	Connection con = null;
	PreparedStatement stmt = null;
	ResultSet rs = null;
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}					
	try{

	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.prepareStatement(qq);
	    stmt.setString(1, id);
	    rs = stmt.executeQuery();
	    if(rs.next()){
		//
		str = rs.getString(1);
		if(str != null) case_type = str;
		str = rs.getString(2);
		if(str != null) status = str;
		str = rs.getString(3);
		if(str != null) ini_hear_time = str;
		str = rs.getString(4);
		if(str != null) contest_hear_time = str;
		str = rs.getString(5);
		if(str != null) misc_hear_time = str;
		str = rs.getString(6);
		if(str != null && !str.equals("0")) judgment_amount = str;
		str = rs.getString(7);
		if(str != null && !str.equals("0")) fine = str;
		str = rs.getString(8);
		if(str != null && !str.equals("0")) court_cost = str;
		str = rs.getString(9);
		if(str != null) ini_hear_date = str;
		str = rs.getString(10);
		if(str != null) contest_hear_date = str;
		str = rs.getString(11);
		if(str != null) misc_hear_date = str;
		str = rs.getString(12);
		if(str != null) pro_supp_date = str;
		str = rs.getString(13);
		if(str != null) received = str;
		str = rs.getString(14);
		if(str != null) filed = str;
		str = rs.getString(15);
		if(str != null) compliance_date = str;
		str = rs.getString(16);
		if(str != null) judgment_date = str;
		str = rs.getString(17);
		if(str != null) sent_date = str;
		str = rs.getString(18);
		if(str != null) last_paid_date = str;
		str = rs.getString(19);
		if(str != null) closed_date = str;
		str = rs.getString(20);
		if(str != null) closed_comments = str;
		str = rs.getString(21);
		if(str != null) comments = str;
		str = rs.getString(22);
		if(str != null) pro_supp_time = str;
		str = rs.getString(23);
		if(str != null) per_day = str;
		str = rs.getString(24);
		if(str != null) mcc_flag = str;
		str = rs.getString(25);
		if(str != null) pro_supp = str;
		str = rs.getString(26);
		if(str != null) lawyerid = str;
		str = rs.getString(27);
		if(str != null) rule_date = str;
		str = rs.getString(28);
		if(str != null) rule_time = str;
		str = rs.getString(29);
		if(str != null) e41_date = str;
		str = rs.getString(30);
		if(str != null) trans_collect_date = str;
		str = rs.getString(31);
		if(str != null) citation_date = str;
		str = rs.getString(32);
		if(str != null) citation_num = str;
		String typeDesc = rs.getString(33);
		String statusDesc = rs.getString(34);
		String lid = rs.getString(35);
		String lfname = rs.getString(36);
		String lbar = rs.getString(37);
		String ltitle = rs.getString(38);
		String lactive = rs.getString(39);
		/*
		  if(typeDesc != null && !case_type.equals("")){
		  caseType = new CaseType(case_type, typeDesc, debug);
		  }
		  if(statusDesc != null && !status.equals("")){
		  cStatus = new Status(status, statusDesc, debug);
		  }
		  if(lid != null && lfname != null){
		  lawyer = new Lawyer(lid,lfname,lbar,ltitle,lactive, debug);
		  }
		*/
	    }
	    else{
		back = id+" not found ";
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not retreive data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
		
    }
    public String linkDefendantToCase(String[] defIds){
	String back = "";
	if(defIds != null){
	    for(String str:defIds){
		back += linkDefendantToCase(str);
	    }
	}
	return back;
    }
    public String linkDefendantToCase(String defId){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	if(id.equals("") || defId.equals("")){
	    back = "Case ID or defendant ID not set ";
	    return back;
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}
	String qq = " insert into legal_def_case values("+id+","+defId+",null)";				
	try{
	    if(debug){
		logger.debug(qq);
	    }
	    stmt = con.createStatement();
	    stmt.executeUpdate(qq);
	    back += findDefendants();
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;
    }
    public String unlinkDefendant(String[] defsId){
		
	String back = "", qq = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	if(id.equals("")){
	    back = "Case ID is not set ";
	    return back;
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}		
	try{
	    stmt = con.createStatement();
	    for(int i=0;i<defsId.length;i++){
		qq = " delete from legal_def_case where id="+id+
		    " and did="+defsId[i];
		if(debug){
		    logger.debug(qq);
		}
		stmt.executeUpdate(qq);
		back += findDefendants();
	    }
	}
	catch(Exception ex){
	    logger.error(ex+" : "+qq);
	    back += " Could not save data ";
	    back += ex;
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}				
	return back;
    }
    //
    //
    public String findAddresses(){

	String back = "";
	if(id.equals("")){
	    return "";
	}
	LegalAddressList al = new LegalAddressList(debug, id);
	back = al.lookFor();
	if(back.equals("")){
	    addresses = al.getAddresses();
	}
	return back;
    }
    String findDefendants(){

	String back = "";
	if(id.equals("")){
	    return back;
	}
	DefendantList dl = new DefendantList(id, debug);
	String str = dl.find();
	if(!str.equals(""))
	    back += str;
	else
	    defendants = dl.getDefendants();
	return back;
		
    }
    /**
     * check if this case has single defendant
     */
    public boolean hasSingleDef(){
	if(defendants == null){
	    findDefendants();
	}
	if(defendants != null && defendants.size() == 1){
	    return true;
	}
	return false;
    }
    /**
     * check if this case has multiple defendants
     */
    public boolean hasMultipleDef(){
	if(defendants == null){
	    findDefendants();
	}
	if(defendants != null && defendants.size() > 1){
	    return true;
	}
	return false;
    }
    /**
     * find the cause numbers
     */
    public String findCauseNums(){

	String back = "";
	if(id.equals("")){
	    return "";
	}
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	String qq = "select "+
	    "d.cause_num from "+							
	    "legal_def_case d where d.id = "+id;
	if(debug){
	    System.err.println(qq);
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}		
	try{

	    stmt = con.createStatement();
	    if(stmt == null){
		back = " Could not connect to DB ";
		logger.error(back);
		return back;
	    }
	    causeNums = new ArrayList<String>();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) causeNums.add(str);
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return back;
    }

    public String findCroosRefViolation(){
	String cid_t="", back="";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	if(case_type.equals("")) return cid_t;
	String qq = "select cid from legal_cross_ref "+
	    " where typeId = '"+case_type+"'";
	if(debug){
	    System.err.println(qq);
	}
	try{
	    con = Helper.getLegalConnection();
	    if(con == null){
		back = "Could not connect to DB ";
		logger.error(back);
		return cid_t;
	    }
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		String str = rs.getString(1);
		if(str != null) cid_t = str;
	    }
	}
	catch(Exception ex){
	    logger.error(ex+":"+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}		
	return cid_t;	
    }

}






















































