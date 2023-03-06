package rental.model;
import java.util.Vector;
import java.sql.*;
import javax.naming.*;
import javax.naming.directory.*;
import rental.list.*;
import rental.utils.*;


public class Abatement implements java.io.Serializable{

    final static long serialVersionUID = 4L;
    String id="", registrId="", abateDate="",
	invoiceId="", fine ="", amountPaid="", paidDate="",
	status="", notes="";
	
    boolean debug = false;
    public Abatement(){
		
    }
	
    public Abatement(String val, boolean deb){
		
	id = val;
	debug = deb;
    }

    //
    // getters
    //
    public String getId(){
	return id;
    }
    public String getRegistrId(){
	return registrId;
    }
    public String getInvoiceId(){
	return invoiceId;
    }
    public String getAbateDate(){
	return abateDate;
    }
    public String getNotes(){
	return notes;
    }
    public String getFine(){
	return fine;
    }
    public String getStatus(){
	return status;
    }
    //
    // setters
    //
    public void setId (String val){
	id = val;
    }
    public void setFine (String val){
	if(val != null)
	    fine = val;
    }
    public void setNotes (String val){
	if(val != null)
	    notes = val;
    }
    public void setRegistrId (String val){
	if(val != null)
	    registrId = val;
    }
    public void setInvoiceId (String val){
	if(val != null)
	    invoiceId = val;
    }
    public void setAbateDate (String val){
	if(val != null)
	    abateDate = val;
    }
    public void setStatus (String val){
	if(val != null)
	    status = val;
    }
    public void setPaidDate (String val){
	if(val != null)
	    paidDate = val;
    }
    //
    public String doSelect(Statement stmt, ResultSet rs){
	//		
	String back = "";
	String qq = "select fine,registrId,invoiceId,"+
	    " status,notes,to_char(abateDate,'mm/dd/yyyy'),"+
	    " amountPaid, to_char(paidDate,'mm/dd/yyyy') "+
	    " from abatements where id=" + id;
	if(debug){
	    System.err.println(qq);
	}
	String str="";
	try{
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		str = rs.getString(1);
		if(str != null) fine = str;
		str = rs.getString(2);
		if(str != null) registrId = str;
		str = rs.getString(3);
		if(str != null) invoiceId = str;
		str = rs.getString(4);
		if(str != null) status = str;
		str = rs.getString(5);
		if(str != null) notes = str;
		str = rs.getString(6);
		if(str != null) abateDate = str;
		str = rs.getString(7);
		if(str != null) amountPaid = str;
		str = rs.getString(8);
		if(str != null) paidDate = str;
	    }
	}
	catch(Exception ex){
	    back += ex;
	    System.err.println(ex);
	}
	return back;		
    }

    public String doSave(Statement stmt, ResultSet rs){
		
	String back = "";
	String qq = "select abatement_seq.nextval from dual";
	try {
	    if(debug){
		System.err.println(qq);
	    }
	    rs = stmt.executeQuery(qq);
	    if(rs.next()){
		id = rs.getString(1);
	    }
	    qq = "insert into abatements values("+id+","+registrId+",";
	    if(invoiceId.equals(""))
		qq += "null,";
	    else
		qq += "'"+invoiceId+"',";
	    if(fine.equals(""))
		qq += "0,";
	    else
		qq += ""+fine+",";
	    if(abateDate.equals(""))
		qq += "null,";
	    else
		qq += "to_date('"+abateDate+"','mm/dd/yyyy'),";
	    //
	    qq +="0,null,'Unpaid',"; // amountPaid, paidDate, status
	    if(notes.equals("")){
		qq += "null";
	    }
	    else {
		qq += "'"+Helper.doubleApostrify(notes)+"'";
	    }
	    if(debug){
		System.err.println(qq);
	    }
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    back += ex;
	    System.err.println(ex);
	}
	return back;	
    }
    public String doUpdate(Statement stmt, ResultSet rs){
		
	String back = "";
	String qq = "update abatements set ";

	if(debug){
	    System.err.println(qq);
	}
	try{
	    if(invoiceId.equals(""))
		qq += "invoiceId=null,";
	    else
		qq += "invoiceId='"+invoiceId+"',";
	    if(fine.equals(""))
		qq += "fine=0,";
	    else
		qq += "fine="+fine+",";
	    if(abateDate.equals(""))
		qq += "abateDate = null,";
	    else
		qq += "abateDate=to_date('"+abateDate+"','mm/dd/yyyy'),";
	    //
	    if(status.equals(""))
		qq += "status='Unpaid',";
	    else
		qq += "status='"+status+"',";
	    if(notes.equals("")){
		qq += "notes=null";
	    }
	    else {
		qq += "notes='"+Helper.doubleApostrify(notes)+"'";
	    }
	    if(debug){
		System.err.println(qq);
	    }
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    back += ex;
	    System.err.println(ex);
	}
	return back;	
    }
    //
    public String doDelete(Statement stmt, ResultSet rs){
		
	String back = "";
	String qq = "delete from abatements where id="+id;
	if(debug){
	    System.err.println(qq);
	}
	try{
	    stmt.executeUpdate(qq);
	}
	catch(Exception ex){
	    back += ex;
	    System.err.println(ex);
	}
	return back;	
    }
    //
    public String toString(){
		
	String ret = ""+id;
	if(!ret.equals("")) ret += " ";
	ret += registrId;
	if(!ret.equals("")) ret += " ";
	ret += fine;
	return ret;
		
    }
	

}
