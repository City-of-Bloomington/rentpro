package rental.model;
import java.util.*;
import java.util.Vector;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.utils.*;


public class Owner implements java.io.Serializable{

    String name_num="",fullName="",address="",city="",state="",zip="",type="",
	phone_home="",phone_work="",email="", notes="",
	unconfirmed="";
    final static long serialVersionUID = 620L;
    boolean debug = false;
    String errors = "";
    static Logger logger = LogManager.getLogger(Owner.class);

    List<Phone> phones = null;
    List<Rent> rents = null;
    // for rentals that belong to
    String startDate="", endDate="";
    boolean soonToExpire = false;
    public Owner(){
		
    }
	
    public Owner(boolean deb, String val){
	setId(val);
	debug = deb;
		
    }
    public Owner(boolean deb,
		 String val,
		 String val2
		 ){
	debug = deb;		
	setId(val);
	setFullName(val2);
    }
    public Owner(boolean deb,
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
		 String val11
		 ){
	debug = deb;		
	setId(val);
	setFullName(val2);
	setAddress(val3);
	setCity(val4);
	setState(val5);
	setZip(val6);
	setPhone_home(val7);
	setPhone_work(val8);
	setNotes(val9);
	setEmail(val10);
	setUnconfirmed(val11);
    }	
    public Owner(boolean deb){
	debug = deb;
		
    }
    public void setId(String val){
	if(val != null)
	    name_num = val;
    }	
    //
    // getters
    //
    public String getName_num(){
	return name_num;
    }
    public String getId(){
	return name_num;
    }	
    public String getFullName(){
	return fullName;
    }
    public String getEmail(){
	return email.toLowerCase();
    }
    public String getUnconfirmed(){
	return unconfirmed;
    }
    public boolean isUnconfirmed(){
	return !unconfirmed.equals("");
    }
    public List<String> getEmails(){
	List<String> emails = new ArrayList<String>();
	if(email.indexOf(",") > -1){
	    String[] tmp = email.split(",");
	    for(String str: tmp){
		str = str.trim();
		if(Helper.isValidEmail(str)){
		    emails.add(str.toLowerCase());
		}
	    }
	}
	else{
	    if(Helper.isValidEmail(email)){
		emails.add(email.toLowerCase());
	    }
	}
	if(emails.size() == 0) return null;
	return emails;
    }
    public boolean hasEmail(){
	return !email.equals("") && email.indexOf("@") > 0;
    }
    public boolean hasValidEmail(){
	if(!hasEmail()) return false;
	if(email.indexOf(",") > -1){
	    String[] tmp = email.split(",");
	    for(String str: tmp){
		if(!Helper.isValidEmail(str.trim())){
		    return false;
		}
	    }
	    return true;
	}
	else{
	    return Helper.isValidEmail(email); 
	}
    }
    /**
     * to exclude certain entries such as ",","." etc
     */ 
    public boolean isLegit(){
	return fullName.length() > 3 ;
    }
    public String getAddress(){
	return address;
    }
    public String getState(){
	return state;
    }
    public String getCity(){
	return city;
    }
    public String getZip(){
	return zip;
    }
    public String getNotes(){
	return notes;
    }
    public String getCityStateZip(){
	String ret = "";
	if(!city.equals(""))
	    ret = city;
	if(!state.equals("")){
	    if(!ret.equals("")) ret += ", ";
	    ret += state;
	}
	if(!zip.equals("")){
	    if(!ret.equals("")) ret += " ";
	    ret += zip;
	}
	return ret;
    }
    public String getPhones(){
	String ret = "";
	if(phones == null){
	    getPhoneList();
	}
	if(phones != null){
	    for(Phone ph: phones){
		if(!ret.equals("")) ret += ", ";
		ret += ph.getType().substring(0,1)+":"+ph; // will convert to &#58;
	    }
	}
	else{
	    if(!phone_work.equals(""))
		ret = phone_work;
	    if(!phone_home.equals("")){
		if(!ret.equals("")) ret += ", ";
		ret += phone_home;	
	    }
	}
	return ret;
    }
    public String getWorkPhone(){
		
	String ret = "";
	if(phones == null){
	    getPhoneList();
	}
	if(phones != null){
	    for(Phone ph: phones){
		if(ph.getType().equals("Work")){
		    ret = ""+ph; // will convert to &#58;
		}
	    }
	}
	else{
	    if(!phone_work.equals(""))
		ret = phone_work;
	}
	return ret;

    }
    public List<Phone> getPhoneList(){
	String back = "";
	if(phones == null && !name_num.equals("")){
	    PhoneList pl = new PhoneList(debug, name_num);
	    back = pl.find();
	    if(back.equals("")){
		phones = pl.getPhones();
	    }
	}
	return phones;
    }
    public String[] getNames(){

	String[] str = null;
	Vector<String> vec = new Vector<String>(2);
	if(!fullName.equals("")){
	    vec.add(fullName);
	    String str2 = "";
	    str = fullName.split("\\s");
	    if(str != null){
		for(int i=0;i<str.length;i++){
		    str2 = Helper.strClean(str[i]);
		    if(!str2.equals("")){
			vec.add(str2);
		    }
		}
	    }
	}
	Object[] obj = vec.toArray();
	String[] strArr = null;
	if(obj != null && obj.length > 0){
	    strArr = new String[obj.length];
	    for(int i=0;i<obj.length;i++){
		strArr[i] = (String)obj[i];
	    }
	}
	return strArr;
		
    }
    public List<Rent> getRents(){
	if(rents == null && !soonToExpire){
	    errors += findRents();
	}
	return rents;
    }
    //
    // setters
    //
    public void setFullName (String val){
	if(val != null)
	    fullName = val;
    }
    public void setAddress (String val){
	if(val != null)		
	    address = val;
    }
    public void setCity (String val){
	if(val != null)
	    city = val;
		
    }
    public void setUnconfirmed(String val){
	if(val != null)
	    unconfirmed = val;
		
    }		
		
    public void setState (String val){
	if(val != null)
	    state = val;
    }
    public void setZip (String val){
	if(val != null)
	    zip = val;
    }
    public void setType (String val){
	if(val != null)
	    type = val;
    }
    public void setEmail (String val){
	if(val != null)
	    email = val;
    }
    public void setNotes (String val){
	if(val != null)
	    notes = val;
    }
    public void setPhone_home (String val){
	if(val != null)
	    phone_home = val;
    }
    public void setPhone_work (String val){
	if(val != null)
	    phone_work = val;
    }
    public void setSoonToExpire(String val, String val2){
	if(val != null)
	    startDate = val;
	if(val2 != null)
	    endDate = val2;
	soonToExpire = true;
    }
	
    //
    public String doSelect(Statement stmt, ResultSet rs){
	return doSelect();		
    }
    public String doSelect(){
	String back = "";
	String qq = "select initcap(name),initcap(address),initcap(city),"+
	    "state,zip,phone_home,phone_work,notes,email,unconfirmed "+
	    "from name where name_num=?";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1, name_num);
	    rs = pstmt.executeQuery();
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) fullName = str;
		str = rs.getString(2);
		if(str != null) address = str;
		str = rs.getString(3);
		if(str != null) city = str;
		str = rs.getString(4);
		if(str != null) state = str;
		str = rs.getString(5);
		if(str != null) zip = str;
		str = rs.getString(6);
		if(str != null) phone_home = str;
		str = rs.getString(7);
		if(str != null) phone_work = str;
		str = rs.getString(8);
		if(str != null) notes = str;
		str = rs.getString(9);
		if(str != null) email = str;
		str = rs.getString(10);
		if(str != null) unconfirmed = str;
								
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;		
    }
    public String doUpdate(){
	String back = "";
	String qq = " update name set name=?,address=?,city=?,zip=?,"+
	    "email=?,state=?,phone_home=?,phone_work=?,"+
	    "notes=?,unconfirmed=? where name_num=?";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	try{
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(fullName.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, fullName);
	    }
	    if(address.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, address);
	    }
	    if(city.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, city);
	    }
	    if(zip.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, zip);
	    }
	    if(email.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, email);
	    }
	    if(state.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, state);
	    }
	    if(phone_home.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, phone_home);
	    }
	    if(phone_work.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, phone_work);
	    }
	    if(notes.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else {
		pstmt.setString(jj++, notes);
	    }
	    if(unconfirmed.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else {
		pstmt.setString(jj++, "y");
	    }						
	    pstmt.setString(jj++, name_num);
						
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;	
    }
    public String doSave(){
	String back = "";
		
	String qq = "select name_num_seq.nextval from dual";		
	Connection con = null;
	PreparedStatement pstmt = null, pstmt2=null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	try{

	    pstmt2 = con.prepareStatement(qq);
	    rs = pstmt2.executeQuery();
	    if(rs.next()){
		name_num = rs.getString(1);
	    }
	    qq = "insert into name values ("+name_num+",?,?,?,?,?,?,?,?,?,'y')";
	    if(debug){
		logger.debug(qq);
	    }
	    pstmt = con.prepareStatement(qq);
	    int jj = 1;
	    if(fullName.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, fullName);
	    }
	    if(address.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, address);
	    }
	    if(city.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, city);
	    }
	    if(state.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, state);
	    }
	    if(zip.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, zip);
	    }
	    if(phone_home.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, phone_home);
	    }
	    if(phone_work.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, phone_work);
	    }
	    if(notes.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else {
		pstmt.setString(jj++, notes);
	    }
	    if(email.equals("")){
		pstmt.setString(jj++, null);
	    }
	    else{
		pstmt.setString(jj++, email);
	    }
	    unconfirmed="y"; // default
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.databaseDisconnect(con, rs, pstmt, pstmt2);
	}
	return back;	
    }
    public String removeFromRental(String rentId){
	String back = "";
		
	String qq = "delete from regid_name "+
	    "where name_num=? and id=?";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,name_num);
	    pstmt.setString(2,rentId);
	    pstmt.executeUpdate();
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;

    }
    public String doDelete(){
	String back = "";
		
	String qq = "select count(*) from regid_name where "+
	    "name_num=?";
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	con = Helper.getConnection();
	if(con == null){
	    back = "Could not connect to Database ";
	    return back;
	}		
	if(debug){
	    logger.debug(qq);
	}
	String str="";
	int count = 0;
	try{
	    pstmt = con.prepareStatement(qq);
	    pstmt.setString(1,name_num);
	    rs = pstmt.executeQuery();
	    rs.next();
	    count = rs.getInt(1);
	    if(count == 0){ // first delete the related phones
		qq = "delete from owner_phones "+
		    "where name_num=?";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,name_num);
		pstmt.executeUpdate();		
		qq = "delete from name "+
		    "where name_num=?";
		if(debug){
		    logger.debug(qq);
		}
		pstmt = con.prepareStatement(qq);
		pstmt.setString(1,name_num);
		pstmt.executeUpdate();
		name_num = "";
	    }
	    else{
		back = "Can not delete, still some rental associated with this owner ";
	    }
	}
	catch(Exception ex){
	    back += ex;
	    logger.error(back+" : "+qq);
	}
	finally{
	    Helper.doClean(con, pstmt, rs);
	}
	return back;

    }
    public String getOwnerInfo(){
	String ret = "";
	ret += " FullName: "+fullName;
	ret += "\n Address:"+address;
	ret += "\n City, State Zip: "+city+", "+state+" "+zip;
	return ret;
		
    }
    public String toString(){
	return fullName;
    }
    //
    @Override
    public int hashCode(){
	int n = 0;
	if(!name_num.equals("")){
	    try{
		n = Integer.parseInt(name_num);
	    }
	    catch(Exception ex){};
	}
	return  n;
    }
    //
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Owner)) {
            return false;
        }
        return ((Owner)obj).getId().equals(this.getId());
    }	
    public String findRents(){
		
	String back = "";
	RentList rl = new RentList(debug);
	rl.setName_num(name_num);
	rl.setActive("Y");
	back = rl.lookFor();
	if(back.equals("")){
	    rents = rl.getRents();
	}
	else{
	    return back;
	}
	rl = new RentList(debug);
	rl.setAgentId(name_num);
	rl.setActive("Y");
	back = rl.lookFor();
	if(back.equals("")){
	    if(rents == null)
		rents = rl.getRents();
	    else{
		List<Rent> list = rl.getRents();
		for(Rent rent: list){
		    rents.add(rent);
		}
	    }
	}
	else{
	    return back;
	}
	return back;
    }

    public String findSoonToExpireRents(boolean ownerOnly){
		
	String back = "";
	RentList rl = new RentList(debug);
	if(ownerOnly)
	    rl.setName_num(name_num);
	else
	    rl.setAgentId(name_num);						
	rl.setActive("Y");
	rl.setWhichDate("permit_expires");
	rl.setDate_from(startDate);
	rl.setDate_to(endDate);
	rl.setProperty_status("R");
	back = rl.lookFor();
	if(back.equals("")){
	    rents = rl.getRents();
	}
	else{
	    return back;
	}
	return back;
    }	
		

}
