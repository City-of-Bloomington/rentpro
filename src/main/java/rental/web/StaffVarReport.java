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


@WebServlet(urlPatterns = {"/StaffVarReport"})
public class StaffVarReport extends TopServlet{

    final static long serialVersionUID = 900L;
    String [] inspIdArr = null;
    String [] inspArr = null;
    String [] typeIdArr = null;
    String [] typeArr = null;
    //
    String boardLetterFile = "J:\\departments\\HAND\\common\\BHQA\\Board Findings Letters\\2005";

    PrintWriter os;
    String bgcolor = Rental.bgcolor;
    static Logger logger = LogManager.getLogger(StaffVarReport.class);

    //
    public void doGet(HttpServletRequest req, 
		      HttpServletResponse res) 
	throws ServletException, IOException{
	doPost(req,res);
    }
    /**
     * Generates the bill.
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException{
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	boolean success = true;
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String rid="", id="",title="Staff Report: Petition for Variance",
	    meet_date="",variance="",
	    pet_num="",
	    f_name="",l_name="", 
	    inspector="",
	    details="",
	    comments="", message="",
	    conditions="", recommend="",
	    deadline="",
	    attachments="";
	
	//
	String name, value;
	String action="",qq="",staff_report="", address="";
	String delItem[] = null;
	String delItem2[] = null;
	String items[] = {"","","",""}; 
	String bullets[] = {"","","",""}; 
	String i_dates[] = {"","","",""}; 

	int access=0;

	Enumeration<String> values = req.getParameterNames();

	out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");

	String [] vals;

	while (values.hasMoreElements()){
	    name = values.nextElement().trim();
	    vals = req.getParameterValues(name);
	    value = Helper.replaceQuote(vals[vals.length-1].trim());	
	    if (name.equals("id")){
		id = value;
	    }
	    else if (name.equals("rid")){
		rid = value;
	    }
	    else if (name.equals("access")){
		try{
		    access = Integer.parseInt(value);
		}catch(Exception ex){}
	    }
	    else if (name.equals("title")){
		title = value;
	    }
	    else if (name.equals("meet_date")) {
		meet_date = value;
	    }
	    else if (name.equals("variance")) {
		variance = value;
	    }
	    else if (name.equals("pet_num")) {
		pet_num = value;
	    }
	    else if (name.equals("f_name")) {
		f_name = value;
	    }
	    else if (name.equals("l_name")) {
		l_name = value;
	    }
	    else if (name.equals("inspector")) {
		inspector = value;
	    }
	    else if (name.equals("details")) {
		details = value;
	    }
	    else if (name.equals("comments")) {
		comments = value;
	    }
	    else if (name.equals("recommend")) {
		recommend = value;
	    }
	    else if (name.equals("conditions")) {
		conditions = value;
	    }
	    else if (name.equals("deadline")) {
		deadline = value;
	    }
	    else if (name.equals("attachments")) {
		attachments = value;
	    }
	    else if (name.equals("delItem")) {
		delItem = vals; // array
	    }
	    else if (name.equals("delItem2")) {
		delItem2 = vals; // array
	    }
	    else if (name.equals("item0")) {
		items[0] = value;
	    }
	    else if (name.equals("item1")) {
		items[1] = value;
	    }
	    else if (name.equals("item2")) {
		items[2] = value;
	    }
	    else if (name.equals("item3")) {
		items[3] = value;
	    }
	    else if (name.equals("bullet0")) {
		bullets[0] = value;
	    }
	    else if (name.equals("bullet1")) {
		bullets[1] = value;
	    }
	    else if (name.equals("bullet2")) {
		bullets[2] = value;
	    }
	    else if (name.equals("bullet3")) {
		bullets[3] = value;
	    }
	    else if (name.equals("i_date0")) {
		i_dates[0] = value;
	    }
	    else if (name.equals("i_date1")) {
		i_dates[1] = value;
	    }
	    else if (name.equals("i_date2")) {
		i_dates[2] = value;
	    }
	    else if (name.equals("i_date3")) {
		i_dates[3] = value;
	    }
	    else if (name.equals("action")){ 
		// bill, change pay status
		action = value;  
	    }
	}
	User user = null;
	HttpSession session = null;
	session = req.getSession(false);
	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login?";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login?";
	    res.sendRedirect(str);
	    return; 
	}
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
	}
	catch(Exception ex){
	    success = false;
	    message += " could not connect to database "+ex;
	    logger.error(message);
	}
	//
	// we need these for default values of dates
	Calendar current_cal = Calendar.getInstance();
	int mm = current_cal.get(Calendar.MONTH)+1;
	int dd = current_cal.get(Calendar.DATE);
	int yyyy = current_cal.get(Calendar.YEAR);
	//
	try{
	    String str="",str2="";
	    if(inspIdArr == null){
		qq = "select count(*) from inspectors where name is not null";
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    int jj, n = rs.getInt(1);
		    if(n > 0){
			n++; // for an empty choice
			inspIdArr = new String[n];
			inspArr = new String[n];
			for (int i=0; i<n; i++){
			    inspIdArr[i] = "";
			    inspArr[i]="\n";
			}
			qq = "select initials,initcap(name) from "+
			    "inspectors where name "+
			    "is not null order by 2";
			if(debug){
			    logger.debug(qq);
			}
			rs = stmt.executeQuery(qq);
			jj = 1;
			while(rs.next()){
			    if(jj > n) break; // should not happen
			    str = rs.getString(1);
			    str2 = rs.getString(2);
			    if(str != null && str2 != null){
				inspIdArr[jj] = str;
				inspArr[jj] = str2;
				jj++;
			    }
			}
		    }
		}
	    }
	    if(typeIdArr == null){
		qq = "select count(*) from inspection_types";
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    int jj, n = rs.getInt(1);
		    if(n > 0){
			n++; // for an empty choice
			typeIdArr = new String[n];
			typeArr = new String[n];
			for (int i=0; i<n; i++){
			    typeIdArr[i] = "";
			    typeArr[i]="\n";
			}
			qq = "select * from inspection_types ";
			if(debug){
			    logger.debug(qq);
			}
			rs = stmt.executeQuery(qq);
			jj = 1;
			while(rs.next()){
			    if(jj > n) break; // should not happen
			    str = rs.getString(1);
			    str2 = rs.getString(2);
			    if(str != null && str2 != null){
				typeIdArr[jj] = str;
				typeArr[jj] = str2;
				jj++;
			    }
			}
		    }
		}
	    }
	    //
	    // Get the staff_report info
	    // This the list of inspections that have been
	    // performed on this property
	    //
	    if(!rid.equals("")){
		qq = "select to_char(i_date,'Mon dd, yyyy'),"+
		    "item "+
		    " from r_text_items where rid="+rid+
		    " order by i_date";
		if(debug){
		    logger.debug(qq);
		}
		rs = stmt.executeQuery(qq);
		while(rs.next()){
		    str = rs.getString(1);
		    str2 = rs.getString(2);
		    if(str != null) staff_report += str+" ";
		    if(str2 != null) staff_report += str2+"<br>";
		}
	    }
	    qq = "select initcap(street_num||' '||street_dir"+
		"||' '||street_name"+
		"||' '||street_type||' '||post_dir||' '||sud_type||' '||"+
		"sud_num) "+
		"from address2 "+
		"where registr_id="+ id;
	    if(debug){
		logger.debug(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    str="";
	    while(rs.next()){
		str = rs.getString(1);
		if(str != null){
		    address += str+"<br>";
		}
	    }    
	}
	catch(Exception ex){
	    message += ""+ex;
	    logger.error(message+" : "+qq);
	}
	//
	if(action.equals("zoom") ||
	   action.startsWith("Print")){
	    //
	    String str="";
	    if(action.equals("zoom"))
		qq = "select id,title,"+
		    "to_char(meet_date,'mm/dd/yyyy'),"+
		    "variance,pet_num,f_name,l_name,inspector,"+
		    "details,comments,conditions,recommend,"+
		    "to_char(deadline,'mm/dd/yyyy'),"+
		    "attachments "+
		    "from r_staff_var_rep where rid="+rid;
	    else // printable
		qq = "select id,title,"+
		    "to_char(meet_date,'Mon dd, yyyy'),"+
		    "variance,pet_num,f_name,l_name,inspector,"+
		    "details,comments,conditions,recommend,"+
		    "to_char(deadline,'Month dd, yyyy'),"+
		    "attachments "+
		    "from r_staff_var_rep where rid="+rid;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    str = rs.getString(1);
		    if(str != null) id = str;
		    str = rs.getString(2);
		    if(str != null) title = str;    
		    str = rs.getString(3);
		    if(str != null) meet_date = str;   
		    str = rs.getString(4);
		    if(str != null) variance = str;   
		    str = rs.getString(5);
		    if(str != null) pet_num = str;   
		    str = rs.getString(6);
		    if(str != null) f_name = str;   
		    str = rs.getString(7);
		    if(str != null) l_name = str;   
		    str = rs.getString(8);
		    if(str != null) inspector = str;   
		    str = rs.getString(9);
		    if(str != null) details = str;   
		    str = rs.getString(10);
		    if(str != null) comments = str;   
		    str = rs.getString(11);
		    if(str != null) conditions = str;   
		    str = rs.getString(12);
		    if(str != null) recommend = str;   
		    str = rs.getString(13);
		    if(str != null) deadline = str;   
		    str = rs.getString(14);
		    if(str != null) attachments = str; 
		   
		}
		//
	    }
	    catch(Exception ex){
		message += " could not retreive data "+ex;
		logger.error(message+" : "+qq);
	    }
	}
	else if(action.equals("Save")){
	    //
	    qq = " select r_staff_var_id_seq.nextval  from dual";
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		if(rs.next()){
		    rid= rs.getString(1);
		}
		qq = " insert into r_staff_var_rep values("+rid+","+id+",";
		if(title.equals(""))
		    qq += " null,";
		else
		    qq += "'"+Helper.doubleApostrify(title)+"',";
		if(meet_date.equals(""))
		    qq += " null,";
		else
		    qq += " to_date('"+meet_date+"','mm/dd/yyyy'),";
		if(variance.equals(""))
		    qq += " null,";
		else
		    qq += "'"+Helper.doubleApostrify(variance)+"',";
		if(pet_num.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(pet_num)+"',";
		if(f_name.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(f_name)+"',";
		if(l_name.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(l_name)+"',";
		if(inspector.equals(""))
		    qq += " null,";
		else
		    qq += " '"+inspector+"',";
		if(details.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(details)+"',";
		if(comments.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(comments)+"',";
		if(recommend.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(recommend)+"',";
		if(conditions.equals(""))
		    qq += " null,";
		else
		    qq += " '"+Helper.doubleApostrify(conditions)+"',";
		if(deadline.equals(""))
		    qq += " null,";
		else
		    qq += " to_date('"+deadline+"','mm/dd/yyyy'),";
		if(attachments.equals(""))
		    qq += " null";
		else
		    qq += " '"+Helper.doubleApostrify(attachments)+"'";
		qq += ")";
		if(user.canEdit()){
		    if(debug){
			logger.debug(qq);
		    }
		    stmt.executeUpdate(qq);
		    //
		    // Adding  bullets (if any)
		    //
		    for(int i=0;i<bullets.length;i++){
			if(!bullets[i].equals("")){
			    qq = " insert into r_text_bullets values("+
				"r_bullet_id_seq.nextval,"+rid+",'"+
				Helper.doubleApostrify(bullets[i])+"')"; 
			    if(debug){
				logger.debug(qq);
			    }
			    stmt.executeUpdate(qq);
			}
		    }
		    // adding items (if any)
		    //
		    for(int i=0;i<items.length;i++){
			if(!items[i].equals("")){
			    qq = " insert into r_text_items values("+
				"r_bullet_id_seq.nextval,"+rid+",";
			    if(i_dates[i].equals(""))
				qq += "null,";
			    else
				qq += "to_date('"+i_dates[i]+"','mm/dd/yyyy'),'";
			    qq += Helper.doubleApostrify(items[i])+"')"; 
			    if(debug){
				logger.debug(qq);
			    }
			}
		    }
		}
		else{
		    message += "You could not save data ";
		    success = false;
		}
	    }
	    catch(Exception ex){
		message += " could not save data "+ex;
		logger.error(message+" : "+qq);
		out.println(ex);
	    }
	}
	else if(action.startsWith("Delete")){
	    String query2 = "delete from r_text_bullets where rid="+rid;
	    String query3 = "delete from r_text_items where rid="+rid;
	    String q = "delete from r_staff_var_rep where rid="+rid;
	    qq = query2;
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		if(user.canEdit()){
		    stmt.executeUpdate(qq);
		    qq = query3;
		    if(debug){
			logger.debug(qq);
		    }
		    stmt.executeUpdate(qq);
		    qq = q;
		    if(debug){
			logger.debug(qq);
		    }
		    stmt.executeUpdate(qq);
		}
		else{
		    success = false;
		    message += " could not save data ";
		}
	    }
	    catch(Exception ex){
		success = false;
		message += " could not delete this record "+ex;
		logger.error(message+" : "+qq);
	    }
	}
	else if(action.equals("Update")){
	    qq = " update r_staff_var_rep set ";
	    if(title.equals(""))
		qq += " title= null,";
	    else
		qq += " title='"+Helper.doubleApostrify(title)+"',";
	    if(meet_date.equals(""))
		qq += " meet_date=null,";
	    else
		qq += " meet_date=to_date('"+meet_date+"','mm/dd/yyyy'),";
	    if(variance.equals(""))
		qq += " variance= null,";
	    else
		qq += " variance='"+
		    Helper.doubleApostrify(variance)+"',";
	    if(pet_num.equals(""))
		qq += " pet_num= null,";
	    else
		qq += " pet_num='"+Helper.doubleApostrify(pet_num)+"',";
	    if(f_name.equals(""))
		qq += " f_name= null,";
	    else
		qq += " f_name='"+Helper.doubleApostrify(f_name)+"',";
	    if(l_name.equals(""))
		qq += " l_name= null,";
	    else
		qq += " l_name='"+Helper.doubleApostrify(l_name)+"',";
	    if(inspector.equals(""))
		qq += " inspector= null,";
	    else
		qq += " inspector='"+inspector+"',";
	    if(details.equals(""))
		qq += " details= null,";
	    else
		qq += " details='"+
		    Helper.doubleApostrify(details)+"',";
	    if(comments.equals(""))
		qq += " comments= null,";
	    else
		qq += " comments='"+
		    Helper.doubleApostrify(comments)+"',";
	    if(recommend.equals(""))
		qq += " recommend= null,";
	    else
		qq += " recommend='"+
		    Helper.doubleApostrify(recommend)+"',";
	    if(conditions.equals(""))
		qq += " conditions= null,";
	    else
		qq += " conditions='"+
		    Helper.doubleApostrify(conditions)+"',";
	    if(attachments.equals(""))
		qq += " attachments= null,";
	    else
		qq += " attachments='"+
		    Helper.doubleApostrify(attachments)+"',";
	    if(deadline.equals(""))
		qq += " deadline=null";
	    else
		qq += " deadline=to_date('"+deadline+"','mm/dd/yyyy')";
	    qq += " where rid="+rid;

	    try{
		if(user.canEdit()){
		    if(debug){
			logger.debug(qq);
		    }				
		    stmt.executeUpdate(qq);
		    //
		    // if we need to delete some of the bullets
		    //
		    if(delItem != null){
			for(int i=0;i<delItem.length;i++){
			    if(!delItem[i].equals("")){
				qq = "delete from r_text_items where sid= "+
				    delItem[i];
				if(debug){
				    logger.debug(qq);
				}
				stmt.executeUpdate(qq);
			    }
			}
		    }
		    // if we need to delete some of the bullets
		    //
		    if(delItem2 != null){
			for(int i=0;i<delItem2.length;i++){
			    if(!delItem2[i].equals("")){
				qq = "delete from r_text_bullets where sid= "+
				    delItem2[i];
				if(debug){
				    logger.debug(qq);
				}
				stmt.executeUpdate(qq);
			    }
			}
		    }
		    //
		    // is there any new bullet to be added
		    //
		    for(int i=0;i<bullets.length;i++){
			if(!bullets[i].equals("")){
			    qq = " insert into r_text_bullets values("+
				"r_bullet_id_seq.nextval,"+rid+",'"+
				Helper.doubleApostrify(bullets[i])+"')"; 
			    if(debug){
				logger.debug(qq);
			    }
			    stmt.executeUpdate(qq);
							
			}
		    }
		    //
		    for(int i=0;i<items.length;i++){
			if(!items[i].equals("")){
			    qq = " insert into r_text_items values("+
				"r_bullet_id_seq.nextval,"+rid+",";
			    if(i_dates[i].equals(""))
				qq += "null,";
			    else
				qq += "to_date('"+i_dates[i]+"','mm/dd/yyyy'),'";
			    qq += Helper.doubleApostrify(items[i])+"')"; 
			    if(debug){
				logger.debug(qq);
			    }
			    stmt.executeUpdate(qq);
			}
		    }
		}
		else{
		    success = false;
		    message += " You could not update data ";
		}
	    }
	    catch(Exception ex){
		success = false;
		message += " could not update data "+ex;
		logger.error(message+" : "+qq);
	    }
    	}
	//
	out.println("<!DOCTYPE html public \"-//W3C//DTD HTML 4//EN\">");
	out.println("<html><head><title> </title>");
	out.println("<script language=Javascript1.2>");
	out.println(" function moveToNext(item, nextItem){      ");
	out.println("  if(item.value.length > 1){               ");
	out.println("  eval(nextItem.focus());                  ");
	out.println("  }}                                       ");
	out.println("  function validateMonth(mm){	        ");
	out.println(" var len = mm.length;                      ");
	out.println(" if(len == 1){                             ");
	out.println("     if(isNaN(mm)){             ");
	out.println("        return false;			");
	out.println("      }                                    ");
	out.println("     if(mm == \"0\"){                      ");
	out.println("       return false;			");
	out.println("     }                                     ");
	out.println("  }else{                                   ");
	out.println("     if(isNaN(mm)){ ");
	out.println("        return false;		        ");
	out.println("      }                                    ");
	out.println("    if(mm == \"00\" || mm > 12 || mm < 1){ ");
	out.println("     return false;			        ");
	out.println("  }}                                          ");
	out.println("     return true;				   ");
	out.println("  }                                           ");
	out.println("  function validateYear(yy){	           ");
	out.println(" var len = yy.length;                         ");
	out.println(" if(!(len == 2 || len == 4)){                 ");
	out.println("     return false;}			   ");
	out.println("    if(isNaN(yy)){                 ");
	out.println("     return false;				   ");
	out.println("    }                                         ");
	out.println("     return true;				   ");
	out.println("  }                                           ");
	out.println("  function validateDay(dd){	           ");
	out.println(" var len = dd.length;                         ");
	out.println("    if(isNaN(dd))                  "); 
	out.println("     return false;			           ");
	out.println("    if(dd > 31 || dd < 1){ return false;}    ");
	out.println("     return true;				    ");
	out.println("  }                                            ");
	//
	out.println("  function validateForm(){		            ");
	out.println("   var xx = document.myForm.meet_date.value;   ");
	out.println("   if(!validateDate(xx)) return false;         ");
	out.println("   xx = document.myForm.deadline.value;        ");
	out.println("   if(!validateDate(xx)) return false;         ");
	out.println("   xx = document.myForm.i_date0.value;         ");
	out.println("   if(!validateDate(xx)) return false;         ");
	out.println("   xx = document.myForm.i_date1.value;         ");
	out.println("   if(!validateDate(xx)) return false;         ");
	out.println("   xx = document.myForm.i_date2.value;         ");
	out.println("   if(!validateDate(xx)) return false;         ");
	out.println("   xx = document.myForm.i_date3.value;         ");
	out.println("   if(!validateDate(xx)) return false;         ");
	out.println("     return true;				    ");
	out.println("	}	         			    ");
        out.println("  function validateDate(xx){               ");
	out.println("  var len = xx.length;                     ");
	out.println("  if(len == 0) return true;                ");
	out.println("  var n1 = xx.indexOf('/');                ");
	out.println("   var mon = xx.substr(0,n1);              ");
	out.println("   var rest = xx.substr(n1+1,len);         ");
	out.println("  var n2 = rest.indexOf('/');              ");
	out.println("  var len2 = rest.length;                  ");
	out.println("   var day = rest.substr(0,n2);            ");
	out.println("   var yyyy = rest.substr(n2+1,len2);      ");
	out.println("   if(!validateMonth(mon)){                ");
	out.println("   alert(\"invalid month \"+mon);          ");
	out.println("   return false;                           ");
	out.println("   }                                       ");
	out.println("   if(!validateDay(day)){                  ");
	out.println("   alert(\"invalid date \"+day);           ");
	out.println("   return false;                           ");
	out.println("   }                                       ");
	out.println("   if(!validateYear(yyyy)){                ");
	out.println("   alert(\"invalid year \"+yyyy);          ");
	out.println("   return false;                           ");
	out.println("   }                                       ");
	out.println("   return true;                            ");
	out.println("  }                                        ");
	out.println("    function validateTextarea(ss, len) {          ");
	out.println("      if (ss.value.length > len) {                 ");
	out.println("       alert(\"Maximum number of characters is \"+len); ");
	out.println("        ss.value = ss.value.substring(0,len);      ");
	out.println("                    }                              ");
	out.println("                }                                  ");
	out.println("  function validateDelete(){               ");
	out.println("   var x = false;                          ");
	out.println("   x = confirm(\"Are you sure you want to delete this record\");");
	out.println("     if(x){ document.myForm3.submit();            ");
	out.println("	}					       ");
	out.println("     return x;                                    ");
	out.println("	}					       ");
	out.println("  function checkNavigator(){		       ");
	out.println("  var appl = navigator.appName;                   ");
	// out.println("	      alert(appl);                     ");
	out.println("   if(appl.substr(0,1) != \"M\") { // Microsoft   ");
	out.println("	      alert(\"MS Internet Explorer is the only compatbile browser for writing or editing inspection reports\");    ");
	out.println("         return false; }			        ");
	out.println("         return true; }			        ");
	out.println(" </script>		                                ");
	if(!action.startsWith("Print")){
	    out.println("<script type=\"text/javascript\" src=\"/FCKeditor/fckeditor.js\"></script>");
	    out.println("<script type=\"text/javascript\">             ");
	    out.println(" function replaceTextareas(){                   ");
	    out.println(" var oFCKeditor = new FCKeditor('details'); ");
	    out.println(" oFCKeditor.BasePath = \"/FCKeditor/\";       ");
	    out.println(" oFCKeditor.Width	= '520';               ");
	    out.println(" oFCKeditor.ReplaceTextarea() ;               ");
	    out.println("var oFCKeditor2 = new FCKeditor('comments');  ");
	    out.println(" oFCKeditor2.BasePath = \"/FCKeditor/\";      ");
	    out.println(" oFCKeditor2.Width	= '520';               ");
	    out.println(" oFCKeditor2.ReplaceTextarea() ;              ");
	    out.println("var oFCKeditor3 = new FCKeditor('recommend');  ");
	    out.println(" oFCKeditor3.BasePath = \"/FCKeditor/\";      ");
	    out.println(" oFCKeditor3.Width	= '520';               ");
	    out.println(" oFCKeditor3.ReplaceTextarea() ;              ");
	    out.println("var oFCKeditor4 = new FCKeditor('conditions');  ");
	    out.println(" oFCKeditor4.BasePath = \"/FCKeditor/\";      ");
	    out.println(" oFCKeditor4.Width	= '520';               ");
	    out.println(" oFCKeditor4.ReplaceTextarea() ;              ");
	    out.println("var oFCKeditor5 = new FCKeditor('attachments');  ");
	    out.println(" oFCKeditor5.BasePath = \"/FCKeditor/\";      ");
	    out.println(" oFCKeditor5.Width	= '520';               ");
	    out.println(" oFCKeditor5.ReplaceTextarea() ;              ");
	    out.println("  }                                           ");
	    out.println(" </script>		                       ");
	    out.println("  </head><body onload=\"replaceTextareas()\"> ");
	}
	else {
	    out.println("  </head><body> ");
	}
	//
		
	if(!message.equals("")){
	    if(!success){
		out.println("<h3><font color=red>"+message+"</font></h3>");
	    }
	}
	if(!action.startsWith("Print")){
	    out.println("<h2><center>Board of Housing Quality Appeals "+
			"</center></h2>");
	    //
	    out.println("<center><table align=center width=90% border>");
	    out.println("<tr><td bgcolor="+bgcolor+">");
	    //
	    // Add/Edit record
	    //
	    out.println("<table width=100%>");
	    out.println("<tr><td>");
	    //
	    //the real table
	    out.println("<form name=myForm method=post "+
			"onSubmit=\"return validateForm()\">");
	    //
	    out.println("<input type=hidden name=id " +
			" value=\""+id+"\">");
	    if(!rid.equals(""))
		out.println("<input type=hidden name=rid " +
			    " value=\""+rid+"\">");
	    //
	    // Permit id
	    out.println("<tr><td>Rental Permit: </td><td>");
	    out.println("<a href="+url+"Rental?id="+id+
			"&action=zoom>"+id+"</a></td></tr>");
	    //
	    // Report title
	    out.println("<tr><td>Report Title: </td>");
	    out.println("<td><input type=text name=title maxlength=80 "+
			" value=\""+title+
			"\" size=60></td></tr>");
	    //
	    // meeting date
	    out.println("<tr><td>Meeting Date:</td>");
	    out.println("<td><input type=text name=meet_date maxlength=10 "+
			" value=\""+meet_date+
			"\" size=10></td></tr>");
	    // 
	    // Variance
	    out.println("<tr><td>Variance Request:</td>");
	    out.println("<td><select name=variance>");
	    out.println("<option selected>"+variance);
	    for(int i=0;i<Rental.varianceReqArr.length;i++){
		out.println("<option>"+Rental.varianceReqArr[i]);
	    }	
	    out.println("</select></td></tr>");
	    //
	    // Petition #
	    out.println("<td>Petition Number:</td>");
	    out.println("<td><input type=text name=pet_num maxlength=10 "+
			" value=\""+pet_num+
			"\" size=10></td></tr>");
	    // 
	    // Address
	    out.println("<tr><td>Address:</td>");
	    out.println("<td>"+address+"</td></tr>");
	    //
	    // Petitioner
	    out.println("<tr><td colspan=2><table><tr><td valign=top><b>"+
			"Petitioner</b></td><td>First Name</td>"+
			"<td> Last Name</td></tr>");
	    out.println("<tr><td></td><td>");
	    out.println("<input type=text name=f_name maxlength=30 "+
			"value=\""+f_name+"\" "+
			"size=20></td><td>");
	    out.println("<input type=text name=l_name maxlength=30 "+
			"value=\""+l_name+"\" "+
			"size=20></td></tr></table></td></tr>");
	    // 
	    // Inspector
	    out.println("<tr><td>Inspector: </td>");
	    out.println("<td><select name=inspector>");
	    if(inspIdArr != null){
		for(int i=0;i<inspIdArr.length; i++){
		    if(inspector.equals(inspIdArr[i]))
			out.println("<option selected value=\""+
				    inspIdArr[i]+"\">"+
				    inspArr[i]);
		    else
			out.println("<option value=\""+inspIdArr[i]+"\">"+
				    inspArr[i]);
		}
	    }
	    out.println("</select></td></tr>");
	    //
	    // Staff Report
	    out.println("<tr><td colspan=2>Staff Report:</td></tr>");
	    out.println("<tr><td colspan=2 align=center><table border>");
	    out.println("<tr><td>&nbsp;</td>"+
			"<td><b>Date</b></td>"+
			"<td><b>Action</b></td>"+
			"<td>&nbsp;</td>"+
			"</tr>");

	    if(!rid.equals("")){
		//
		// Table of bullets
		//
		// Check if there are items belonging to this permit
		// if so, list them first.
		//
		qq = " select sid,to_char(i_date,'mm/dd/yyyy'),item "+
		    " from r_text_items where rid="+rid+" order by sid ";
		if(debug){
		    logger.debug(qq);
		}
		try{
		    rs = stmt.executeQuery(qq);
		    String str="",str2="",str3="";

		    while(rs.next()){
			str = rs.getString(1);
			str2 = rs.getString(2);
			str3 = rs.getString(3);
			if(str2 != null && !str2.equals("")){
			    out.println("<tr><td>");
			    out.println("<input type=checkbox name=delItem "+
					"value="+str+"></td>");
			    out.println("<td><span id="+str+"0>");
			    out.println(str2);
			    out.println("</span></td>");
			    out.println("<td><span id="+str+"1>");
			    out.println(str3);
			    out.println("</span></td>");
			    out.println("<td>");
			    if(access > 1){
				out.println("<input type=button "+
					    "name=button"+str+
					    " value=\"Edit Text\" "+
					    "onClick=\"window.open('"+url+
					    "EditBullet?"+
					    "&action=Edit&sid="+
					    str+"','Text',"+
					    "'toolbar=0,location=0,"+
					    "directories=0,status=0,menubar=1,"+
					    "scrollbars=1,top=100,left=100,"+
					    "resizable=1,width=450,height=300');\">");
			    }
			    out.println("</td></tr>");
			}
		    }
		    //   out.println("</table></td></tr>");
		}
		catch(Exception ex){
		    out.println(ex);
		    logger.error(ex+":"+qq);
		    success = false;
		}

	    }
	    out.println("<tr><td colspan=4 align=center><font size=-1 "+
			"color=green>You"+
			" may add four text items at a time."+
			"</td></tr>");
	    //
	    for(int i=0;i<4;i++){
		//
		// bullet text items
		//
		out.println("<tr><td>"+(i+1)+" - </td>"+
			    "<td><input name=i_date"+i+" size=10 "+
			    " maxlength=10></td><td colsapn=2>");
		out.println("<input name=item"+i+" size=50 "+
			    " maxlength=80></td><td>&nbsp;</td></tr>");

	    }
	    out.println("</table></td></tr>");
	    //
	    // Details
	    out.println("<tr><td colspan=2>Details:"+
			"<font color=green size=-1>Format your text using the"+
			" toolbar below. <br>"+
			"Max of 1000 characters</font>"+
			"</td></tr>");
	    out.println("<div>");
	    out.println("<tr><td colspan=2>"+
			"<textarea name=details rows=5 cols=60 wrap "+
			"onChange=\"validateTextarea(this,1000)\">");
	    out.println(details);
	    out.println("</textarea></td></tr>");
	    out.println("</div>");
	    //
	    // Comments
	    out.println("<tr><td colspan=2>Comments:"+
			"<font color=green size=-1>Format your text using the"+
			" toolbar below. <br>"+
			"Max of 1000 characters</font>"+
			"</td></tr>");

	    out.println("<tr><td colspan=2>");
	    out.println("<div>");
	    out.println("<textarea name=comments rows=5 cols=60 wrap "+
			"onChange=\"validateTextarea(this,1000)\">");

	    out.println(comments);
	    out.println("</textarea></td></tr>");
	    out.println("</div>");
	    //
	    // Bullets
	    //
	    /////********///
	    out.println("<tr><td colspan=2 align=center><table border>");
	    out.println("<tr><td>&nbsp; </td>"+
			"<td><b>Item</b></td>"+
			"<td>&nbsp;</td></td>"+
			"</tr>");
	    if(!rid.equals("")){
		//
		// Table of bullets
		//
		// Check if there are bullets belonging to this permit
		// if so, list them first.
		//
		qq = " select sid,bullet "+
		    " from r_text_bullets where rid="+rid+
		    " order by sid ";
		if(debug){
		    logger.debug(qq);
		}
		try{
		    rs = stmt.executeQuery(qq);
		    String str="",str2="";
		    while(rs.next()){
			str = rs.getString(1);
			str2 = rs.getString(2);
			if(str2 != null && !str2.equals("")){
			    out.println("<tr><td>");
			    out.println("<input type=checkbox name=delItem2 "+
					"value="+str+"></td>");
			    out.println("<td><span id="+str+"0>");
			    out.println(str2);
			    out.println("</span></td>");
			    out.println("<td>");
			    if(access > 1){
				out.println("<input type=button "+
					    "name=button"+str+
					    " value=\"Edit Text\" "+
					    "onClick=\"window.open('"+url+
					    "EditBullet2?"+
					    "&action=Edit&sid="+
					    str+"','Text',"+
					    "'toolbar=0,location=0,"+
					    "directories=0,status=0,menubar=1,"+
					    "scrollbars=1,top=100,left=100,"+
					    "resizable=1,width=450,height=300');\">");
			    }
			    out.println("</td></tr>");
			}
		    }
		    //    out.println("</table></td></tr>");
		}
		catch(Exception ex){
		    out.println(ex);
		    logger.error(ex+":"+qq);
		    success = false;
		}

	    }
	    out.println("</table></td></tr>");
	    //
	    // Recommandation
	    //
	    out.println("<tr><td colspan=2>Staff Recommendations: "+
			"<font color=green size=-1>Format your text using the"+
			" toolbar below. <br>"+
			"Max of 500 characters</font>"+
			"</td></tr>");
	    out.println("<div>");
	    out.println("<tr><td colspan=2>"+
			"<textarea name=recommend rows=5 cols=60 wrap "+
			"onChange=\"validateTextarea(this,500)\">");
	    out.println(recommend);
	    out.println("</textarea></td></tr>");
	    out.println("</div>");
	    //
	    // Conditions
	    out.println("<tr><td colspan=2>Conditions:"+
			"<font color=green size=-1>Max of 1500 characters</font>"+
			"</td></tr>");
	    out.println("<div>");
	    out.println("<tr><td colspan=2>");
	    out.println("<textarea name=conditions rows=5 cols=60 "+
			"onChange=\"validateTextarea(this,1500)\">");
		       
	    out.println(conditions);
	    out.println("</textarea></td></tr>");
	    out.println("</div>");
	    //
	    // Compliance deadline
	    out.println("<tr><td colspan=2>Compliance Deadline:");
	    out.println("<input type=text name=deadline maxlength=10 "+
			"value=\""+deadline+"\" size=10></td></tr>");
	    // 
	    // Attachements
	    out.println("<tr><td colspan=2>Attachments:"+
			"<font color=green size=-1>Max of 500 characters</font>"+
			"</td></tr>");
	    out.println("<div>");
	    out.println("<tr><td colspan=2>");
	    out.println("<textarea name=attachments rows=4 cols=60 "+
			"onChange=\"validateTextarea(this,500)\">");
	    out.println(attachments);
	    out.println("</textarea></td></tr>");
	    out.println("</div>");
	    //
	    out.println("</table></td></tr>");
	    //
	    // Submit
	    if(!rid.equals("") && !action.startsWith("Delete")){
		out.println("<tr><td colspan=2 align=right><table width=100%>"+
			    "<tr><td align=right "+
			    "valign=top><input type=submit name=action "+
			    "value=\"Update\">");
		out.println("</td><td align=right "+
			    "valign=top><input type=submit name=action "+
			    "value=\"Printable\">");
		out.println("</td><td align=right>");
		out.println("<input type=button name=action onClick=\"");
		out.println("javascript:if(checkNavigator())");
		out.println("window.open('"+url+"Inspection?"+
			    "&id="+id+
			    "','Inspection',"+
			    "'toolbar=0,location=0,"+
			    "directories=0,status=0,menubar=1,"+
			    "scrollbars=1,top=100,left=100,"+
			    "resizable=1,width=650,height=600');\""+
			    " value=\"Inspection\">");
		out.println("</form></td><td align=right>");
		out.println("<form name=myForm4 method=post "+
			    " action=\""+url+"BoardAction?rid="+rid+
			    "\">");
		out.println("<input type=submit "+
			    "value=\"Board Action\"></form>");
		out.println("</td><td align=right>");
		out.println("<form name=myForm3>");
		out.println("<input type=hidden name=rid " +
			    " value=\""+rid+"\">");
		out.println("<input type=hidden name=id " +
			    " value=\""+id+"\">");
		out.println("<input type=submit name=action "+
			    " onClick=\"validateDelete()\" "+
			    "value=\"Delete\">");
		out.println("</form></td></tr></table>");
	    }
	    else{
		out.println("<tr><td><table width=100%>"+
			    "<tr><td align=right "+
			    "valign=top><input type=submit name=action "+
			    "value=\"Save\"></form></td></tr></table>");
	    }
	    out.println("</td></tr></table>");
	    //
	    // Check if there are reports issued before
	    //
	    if(!id.equals("")){
		String titles[] = { "Report ID",
		    "Meeting Date",
		    "Title"
		};
		qq = "select count(*) from r_staff_var_rep where id="+id;
		try{
		    if(debug){
			logger.debug(qq);
		    }
		    rs = stmt.executeQuery(qq);
		    rs.next();
		    int ncnt = rs.getInt(1);
		    if(ncnt > 0){
			out.println("<br><center><table border><tr>");
			for(int i=0; i<titles.length;i++){
			    out.println("<th>"+titles[i]+"</th>");
			}
			out.println("</tr>");
			out.println("<caption>Staff Variance Reports for "+
				    "Permit "+id);
			out.println("</caption>");
			qq = "select rid,to_char(meet_date,'mm/dd/yyyy'),"+
			    "title "+
			    " from r_staff_var_rep where id="+id+
			    " order by rid DESC";
			if(debug){
			    logger.debug(qq);
			}
			rs = stmt.executeQuery(qq);
			while(rs.next()){
			    String str = rs.getString(1);
			    if(str == null) str = "";
			    if(!str.equals("")){
				out.println("<td><a href="+url+
					    "StaffVarReport?"+
					    "&action=zoom&rid="+str+
					    "&id="+id+"&access="+access+
					    ">"+str+
					    "</a></td>");
			    }
			    else
				out.println("<td>&nbsp;</td>");
			    for(int i=1 ;i < titles.length;i++){
				str = rs.getString(i+1);
				if(str == null) str = "&nbsp;";
				out.println("<td>"+str+"</td>");
			    }
			    out.println("</tr>");
			}
			out.println("</table>"); 
		    }
		}
		catch(Exception ex){
		    logger.error(ex+":"+qq);
		}
	    }
	}
	else{  // Printable
	
	    out.println("<br><br><br><br>");
	    //  out.println("City Of Bloomington<br>");
	    // out.println("HAND<br><br>");
	    //
	    out.println("<br><br>");
	    out.println("<h3><center>Board of Housing Quality Appeals <br>");
	    out.println(title+"</h3></center>");
	    out.println("<center><table width=100%>");
	    out.println("<tr><td width=30%>Meeting Date:</td><td>");
	    out.println(meet_date+"</td></tr>");
	    out.println("<tr><td>Variance Request:</td><td>");
	    out.println(variance+"</td></tr>");
	    out.println("<tr><td>Petition Number:</td><td>");
	    out.println(pet_num+"</td></tr>");
	    out.println("<tr><td>Address:</td><td>");
	    out.println(address+"</td></tr>");
	    out.println("<tr><td>Petitioner:</td><td>");
	    out.println(f_name+" "+l_name+"</td></tr>");
	    out.println("<tr><td>Inspector:</td><td>");
	    if(inspIdArr != null){
		for(int i=0;i<inspIdArr.length; i++){
		    if(inspector.equals(inspIdArr[i])){
			out.println(inspArr[i]);
		    }
		}
	    }
	    out.println("</td></tr>");
	    out.println("<tr><td valign=top>Staff Report:</td><td>");
	    out.println(staff_report+"</td></tr>");
	    out.println("<tr><td colspan=2><p>");
	    out.println(details+"</p></td></tr>");
	    if(!comments.equals("")){
		out.println("<tr><td colspan=2><p>");
		out.println(comments+"</p></td></tr>");
	    }
	    //
	    // Check if there are bullets belonging to this permit
	    // if so, list them first.
	    //
	    qq = " select sid,bullet "+
		" from r_text_bullets where rid="+rid+
		" order by sid ";
	    String all="";
	    if(debug){
		logger.debug(qq);
	    }
	    try{
		rs = stmt.executeQuery(qq);
		String str="";
		while(rs.next()){
		    str = rs.getString(2);
		    if(str != null && !str.equals("")){
			all += "<li>"+str;
		    }
		}
	    }
	    catch(Exception ex){
		out.println(ex);
		logger.error(ex+":"+qq);
	    }
	    if(!all.equals(""))
		out.println("<tr><td colspan=2>"+all+"</td></tr>");
	    out.println("<tr><td valign=top>Staff Recommendations:</td><td>");
	    out.println(recommend+"</td></tr>");
	    out.println("<tr><td valign=top>Conditions:</td><td>");
	    out.println(conditions+"</td></tr>");
	    out.println("<td>Compliance Deadline: </td><td>");
	    out.println(deadline+"</td></tr>");
	    out.println("<tr><td valign=top>Attachments:</td><td>");
	    out.println(attachments+"</td></tr>");
	}
    	out.println("<br>");
	out.println("<br>");    
	out.print("</body></html>");
	out.close();
	Helper.databaseDisconnect(con,stmt,rs);
    }

}























































