package rental.list;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import java.sql.*;
import javax.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class CaseList implements java.io.Serializable{

    String f_name = "", l_name="",  invld_addr="",
	street_num="",street_dir="", id="", citeId="",source="",
	street_name="",street_type="",sud_type="",sud_num="",
	rent_street_num="",rent_street_dir="",
	rent_street_name="",rent_street_type="",rent_sud_type="",
	rent_sud_num="", pro_supp_time="", did="",
	dob_from="",dob_to="",dob_on="",ssn="",cause_num="",
	case_type="",status="", pro_supp="",
	which_date="", date_from="", date_to="",
	which_fee="", fee_from="",fee_to="",
	closed_comments="", comments="", city="",state="",zip="",
	judg_amount_from="", 
	fine_from="",court_cost_from="", citation_num="",
	judg_amount_to="", sortby="", dln="",
	fine_to="",court_cost_to="", addrType="";	;
	
    boolean debug = false;
    String errors = "";
    final static long serialVersionUID = 160L;
    static Logger logger = LogManager.getLogger(CaseList.class);	

    Case lcase = null;
    List<Case> cases = new ArrayList<Case>();
	
    public CaseList(boolean val){
	debug = val;
    }
    // this is useful for list of cases that belong to certain defendant
    public CaseList(String val, boolean deb){
	did = val;
	debug = deb;
    }
    public CaseList(Case val, boolean deb){
	lcase = val;
	debug = deb;
    }	
    public void setCase(Case val){
	lcase = val; // used for search purpose
    }
    public void setSortby(String val){
	if(!val.equals("")){
	    sortby = val;
	}
    }
    public void setCause_num(String val){
	if(!val.equals("")){
	    cause_num = val.toUpperCase();
	}
    }
    public void setStatus(String val){
	if(!val.equals("")){
	    status = val;
	}
    }
    public void setPro_supp(String val){
	if(!val.equals("")){
	    pro_supp = val;
	}
    }
    public void setF_name(String val){
	if(!val.equals("")){
	    f_name = val;
	}
    }
    public void setL_name(String val){
	if(!val.equals("")){
	    l_name = val;
	}
    }
    public void setDln(String val){
	if(!val.equals("")){
	    dln = val;
	}
    }	
    public void setAddrType(String val){
	if(!val.equals("")){
	    addrType = val;
	}
    }
    public void setStreet_num(String val){
	if(!val.equals("")){
	    street_num = val;
	}
    }
    public void setStreet_dir(String val){
	if(!val.equals("")){
	    street_dir = val;
	}
    }
    public void setStreet_name(String val){
	if(!val.equals("")){
	    street_name = val.toUpperCase();
	}
    }
    public void setStreet_type(String val){
	if(!val.equals("")){
	    street_type = val;
	}
    }
    public void setSud_type(String val){
	if(!val.equals("")){
	    sud_type = val;
	}
    }
    public void setSud_num(String val){
	if(!val.equals("")){
	    sud_num = val;
	}
    }
    public void setCity(String val){
	if(!val.equals("")){
	    city = val.toUpperCase();
	}
    }
    public void setState(String val){
	if(!val.equals("")){
	    state = val.toUpperCase();
	}
    }
    public void setZip(String val){
	if(!val.equals("")){
	    zip = val;
	}
    }
    public void setCitation_num(String val){
	if(!val.equals("")){
	    citation_num = val;
	}
    }	
    public void setInvld_addr(String val){
	if(!val.equals("")){
	    invld_addr = val;
	}
    }
    public void setDate_from(String val){
	if(!val.equals("")){
	    date_from = val;
	}
    }
    public void setDate_to(String val){
	if(!val.equals("")){
	    date_to = val;
	}
    }
    public void setWhich_date(String val){
	if(!val.equals("")){
	    which_date = val;
	}
    }
    public void setCase_type(String val){
	if(!val.equals("")){
	    case_type = val;
	}
    }
    public void setFee_from(String val){
	if(!val.equals("")){
	    fee_from = val;
	}
    }
    public void setFee_to(String val){
	if(!val.equals("")){
	    fee_to = val;
	}
    }
    public void setWhich_fee(String val){
	if(!val.equals("")){
	    which_fee = val;
	}
    }
    public void setComments(String val){
	if(!val.equals("")){
	    comments = val.toUpperCase();
	}
    }
    //
    // getters
    //
    public List<Case> getCases(){
	return cases;
    }
    // setters
    //
    public String find(){
		
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	String back = "";
	String qq = "select id "+
	    "from legal_def_case where did=" + did;
		
	if(did.equals("")){
	    back = " Need to set defendant Id ";
	    return back;
	}
	con = Helper.getLegalConnection();
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}	
	if(debug){
	    logger.debug(qq);
	}		
	String str="";
	List<String> list = new ArrayList<String>();
	try{
	    stmt = con.createStatement();
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null) list.add(str);
	    }
	    Helper.databaseDisconnect(con, stmt, rs);
	    // 
	    if(list != null && list.size() > 0){
		for(int i=0;i<list.size();i++){
		    str = list.get(i);
		    if(str != null){
			Case ccase = new Case(str, debug, true);
			str = ccase.doSelect();
			if(str.equals("")){
			    cases.add(ccase);
			}
			else{
			    back += " "+str;
			}
		    }
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;		
    }
    //
    // Look for defendant with similar names and info
    //
    public String lookFor(){
		
	String back = "";
	Connection con = Helper.getLegalConnection();
	Statement stmt = null;
	ResultSet rs = null;
	if(con == null){
	    back = "Could not connect to DB ";
	    logger.error(back);
	    return back;
	}		
	String str="";
	boolean defTbl = false, addrTbl = false;
		
	Vector<String> wherecases = new Vector<String>();
	if(!ssn.equals("")){
	    wherecases.addElement("d.ssn='"+ssn+"'");
	    defTbl = true;
	}
	if(!dln.equals("")){
	    wherecases.addElement("d.dln='"+ssn+"'");
	    defTbl = true;
	}	
	else if(!cause_num.equals("")){
	    wherecases.addElement("l.cause_num='"+cause_num+"'");
	    defTbl = true;
	}
	else {
	    if(!status.equals(""))
		wherecases.addElement("c.status='"+status+"'");
	    if(!pro_supp.equals(""))
		wherecases.addElement("c.pro_supp='y'");
	    if(!f_name.equals("")){
		wherecases.addElement("d.f_name like '%"+
				      Helper.replaceQuote(f_name)+"%'");
	    }
	    if(!l_name.equals("")){
		wherecases.addElement("d.l_name like '%"+
				      Helper.replaceQuote(l_name)+"%'");
	    }
	    String suff="";
	    if(addrType.startsWith("def")){
		suff = "da";
	    }
	    else{
		suff = "a";
	    }
			
	    if(!street_num.equals("")){
		wherecases.addElement(suff+".street_num ='"+street_num+"'");
	    }
	    if(!street_dir.equals("")){
		wherecases.addElement(suff+".street_dir ='"+street_dir+"'");
				
	    }
	    if(!street_type.equals("")){
		wherecases.addElement(suff+".street_type ='"+street_type+"'");
				
	    }
	    if(!street_name.equals("")){
		wherecases.addElement(suff+".street_name like '%"+
				      Helper.replaceQuote(street_name)+"%'");
	    }
	    if(!sud_type.equals("")){
		wherecases.addElement(suff+".sud_type ='"+sud_type+"'");
	    }
	    if(!sud_num.equals("")){
		wherecases.addElement(suff+".sud_num ='"+sud_num+"'");
	    }
	    if(!city.equals("")){
		wherecases.addElement("d.city like '"+city+"%'");
	    }
	    if(!state.equals("")){
		wherecases.addElement("d.state like '"+state+"'");
	    }
	    if(invld_addr.equals("y")){
		if(addrType.startsWith("def"))
		    wherecases.addElement(suff+".invld_addr ='y'");
		else
		    wherecases.addElement(suff+".invalid_addr ='Y'");
	    }
	    if(invld_addr.equals("n")){
		if(addrType.startsWith("def"))
		    wherecases.addElement(suff+".invld_addr is null");
		else
		    wherecases.addElement(suff+".invalid_addr is null");	
	    }
	    if(!zip.equals("")){
		wherecases.addElement("d.zip like '"+zip+"%'");
	    }
	    if(!case_type.equals(""))
		wherecases.addElement("c.case_type like '"+case_type+"'");
	    if(!citation_num.equals(""))
		wherecases.addElement("c.citation_num = '"+citation_num+"'");
	    if(!date_from.equals("")){
		if(which_date.equals("dob")){
		    wherecases.addElement("d.dob >=str_to_date('"+date_from+
					  "','%m/%d/%Y')");
		}
		else if(which_date.equals("addr_req_date")){
		    wherecases.addElement("d.addr_req_date >=str_to_date('"+date_from+
					  "','%m/%d/%Y')");
		}
		else if(!which_date.equals("")){
		    wherecases.addElement("c."+which_date+" >=str_to_date('"+date_from+
					  "','%m/%d/%Y')");		
		}
	    }
	    if(!date_to.equals("")){
		if(which_date.equals("dob")){
		    wherecases.addElement("d.dob <=str_to_date('"+date_to+
					  "','%m/%d/%Y')");
		}
		else if(which_date.equals("addr_req_date")){
		    wherecases.addElement("d.addr_req_date <=str_to_date('"+date_to+
					  "','%m/%d/%Y')");
		}
		else if(!which_date.equals("")){				
		    wherecases.addElement("c."+which_date+" <=str_to_date('"+date_to+
					  "','%m/%d/%Y')");		
		}
	    }
	    if(!which_fee.equals("")){
		if(!fee_from.equals(""))
		    wherecases.addElement("c."+which_fee+" >= "+fee_from);
		if(!fee_to.equals(""))
		    wherecases.addElement("c."+which_fee+" <= "+fee_to);
	    }
	    if(!comments.equals(""))
		wherecases.addElement("upper(c.comments) like '%"+
				      Helper.replaceQuote(comments)+"%'");
	    if(!closed_comments.equals(""))
		wherecases.addElement("c.closed_comments like '"+
				      closed_comments+"'");
	}
	String qf="", qw="", qo="";
	String qq = "select c.id,"+
	    "c.case_type,"+
	    "c.status,"+
	    "c.ini_hear_time,"+
	    "c.contest_hear_time,"+
	    "c.misc_hear_time,"+
	    "c.judgment_amount,"+
	    "c.fine,"+
	    "c.court_cost,"+
	    "date_format(c.ini_hear_date,'%m/%d/%Y'),"+ // 10
			
	    "date_format(c.contest_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.misc_hear_date,'%m/%d/%Y'),"+
	    "date_format(c.pro_supp_date,'%m/%d/%Y'),"+
	    "date_format(c.received,'%m/%d/%Y'),"+
	    "date_format(c.filed,'%m/%d/%Y'),"+
	    "date_format(c.compliance_date,'%m/%d/%Y'),"+
	    "date_format(c.judgment_date,'%m/%d/%Y'),"+
	    "date_format(c.sent_date,'%m/%d/%Y'),"+
	    "date_format(c.last_paid_date,'%m/%d/%Y'),"+
	    "date_format(c.closed_date,'%m/%d/%Y'),"+ // 20
			
	    "c.closed_comments,"+
	    "c.comments,"+
	    "c.pro_supp_time,"+
	    "c.per_day, "+
	    "c.mcc_flag, "+
	    "c.pro_supp, "+
	    "c.lawyerid, "+
	    "date_format(c.rule_date,'%m/%d/%Y'),"+
	    "c.rule_time, "+
	    "date_format(c.e41_date,'%m/%d/%Y'), "+ // 30
			
	    "date_format(c.trans_collect_date,'%m/%d/%Y'), "+
	    "date_format(c.citation_date,'%m/%d/%Y'), "+			
	    "c.citation_num, "+		//33
	    "t.typeDesc,s.statusDesc ";
	if(!sortby.equals("")){
	    qo = " order by "+sortby;
	}
	else
	    qo = " order by d.l_name,d.f_name ";
	qf = " from legal_cases c join legal_case_types t on c.case_type=t.typeId"+
	    " join legal_case_status s on c.status=s.statusId ";
	qf += " left join legal_def_case l on l.id=c.id "+
	    " left join legal_defendents d on l.did=d.did "+
	    " left join legal_def_addresses da on d.did=da.defId ";
	qf += " left join legal_addresses a on c.id=a.caseId ";
		
	if(wherecases.size()>0){
	    qw = " where ";
	    for (int c = 0; c < wherecases.size(); c++){
		if(c > 0) qw += " and ";
		qw += wherecases.elementAt(c);
	    }
	}
	qq += qf+qw;
	qq += qo;
	if(debug){
	    logger.debug(qq);
	}
	try{
	    stmt = con.createStatement();			
	    rs = stmt.executeQuery(qq);
	    Set<String> set = new HashSet<String>();
	    while(rs.next()){
		if(cases == null)
		    cases = new ArrayList<Case>();				
		str = rs.getString(1); 
		if(str != null && !set.contains(str)){
		    set.add(str);
		    String arr[] = new String[33];
		    String str2="";
		    for(int i=1;i<34;i++)
			arr[i-1] = rs.getString(i);
		    Case ccase = new Case(debug, arr);
		    cases.add(ccase);
		}
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(ex+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, stmt, rs);
	}
	return back;		
    }

}
