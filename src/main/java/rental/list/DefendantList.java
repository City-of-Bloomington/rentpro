package rental.list;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.model.*;
import rental.utils.*;


public class DefendantList extends ArrayList<Defendant>{

    String id="", date_from="", date_to=""; // case id
	
    boolean debug = false;
    String errors = "";
    final static long serialVersionUID = 240L;
    static Logger logger = LogManager.getLogger(DefendantList.class);	

	
    Defendant defendant = null;
	
    public DefendantList(boolean val){
	debug = val;
    }
	
    public DefendantList(String val, boolean deb){
	id = val;
	debug = deb;
    }
    public DefendantList(Defendant def, boolean deb){
	defendant = def;
	debug = deb;
    }	
    public void setDefendant(Defendant def){
	defendant = def; // used for search purpose
    }
    public void setId(String val){
	id = val;
    }
    public void setDateFrom(String val){
	if(val != null)
	    date_from = val;
    }
    public void setDateTo(String val){
	if(val != null)
	    date_to = val;
    }		
    //
    // getters
    //
    public List<Defendant> getDefendants(){
	return this;
    }

    public String find(){
		
	String back = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;		
	String qq = "select did "+
	    "from legal_def_case where id=" + id;
	if(id.equals("")){
	    back = " Need to set case Id ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	List<String> list = new ArrayList<String>();
	try{
	    con = Helper.getLegalConnection();
	    if(con != null){
		stmt = con.createStatement();
	    }
	    else{
		back = " Could not connect to DB ";
		logger.error(back);
		return back;
	    }					
	    rs = stmt.executeQuery(qq);
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null) list.add(str);
	    }
	    if(list != null && list.size() > 0){
		for(int i=0;i<list.size();i++){
		    str = list.get(i);
		    if(str != null){
			Defendant def = new Defendant(str, debug);
			def.doSelect();
			if(def != null) add(def);
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
    public String findMatchingDefendants(List<Owner> owners){
	String back = "", qq = "";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;	
	if(owners != null){
	    con = Helper.getLegalConnection();
	    if(con == null){			
		back = " Could not connect to DB ";
		logger.error(back);
		return back;
	    }		
	    try{
		stmt = con.createStatement();
		for(Owner one: owners){
		    String[] names = one.getNames();
		    String set = "";
		    for(int j=0;j<names.length;j++){
			if(!set.equals("")) set += ", ";
			set += "'"+Helper.escapeIt(names[j])+"'";
		    }
		    set = "("+set+")";
		    qq = " select did from legal_defendents d,legal_def_addresses da where d.did=da.defId and upper(d.l_name) in "+set;
		    if(!one.getCity().equals("")){
			qq += " and upper(da.city)='"+one.getCity()+"'";
		    }
		    if(!one.getState().equals("")){
			qq += " and upper(da.state)='"+one.getState()+"'";
		    }			
		    if(!one.getZip().equals("")){
			qq += " and da.zip='"+one.getZip()+"'";
		    }					
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    while(rs.next()){
			String did = rs.getString(1);
			Defendant def = new Defendant(did, debug);
			def.doSelect();
			add(def);
		    }
		}
	    }
	    catch(Exception ex){
		back = ex+":"+qq;
		logger.error(back);
	    }
	    finally{
		Helper.databaseDisconnect(con, stmt, rs);	
	    }			
	}
	return back;
    }
}
