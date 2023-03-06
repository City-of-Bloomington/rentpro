package rental.web;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rental.list.*;
import rental.model.*;
import rental.utils.*;

@WebServlet(urlPatterns = {"/RentalFileServ"})
public class RentalFileServ extends TopServlet{

    static final long serialVersionUID = 24L;
    static Logger logger = LogManager.getLogger(RentalFileServ.class);
    // int maxImageSize = 10000000, maxDocSize=10000000;
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    /**
     * Generates the main upload or view image form.
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
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
    // @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest req, 
		       HttpServletResponse res) 
	throws ServletException, IOException {
    
	res.setContentType("text/html");
	PrintWriter out = res.getWriter();
	String name, value;
	boolean success = true,
	    sizeLimitExceeded = false;
	String saveDirectory ="",file_path="";
	String newFile = "";
	String action="", date="", load_file="";
	String id="", rental_id = "", notes="", action2="";
	
	String message = "";
	int maxMemorySize = 10000000; // 10 MB , int of bytes
	int maxRequestSize = 10000000; // 10 MB
	String [] vals;
	User user = null;
	HttpSession session = null;
	long sizeInBytes = 0;
	// 
	// class to handle multipart request (for example text + image)
	// the image file or any upload file will be saved to the 
	// specified directory
	// 
	// we need this path for save purpose
	session = req.getSession(false);

	if(session != null){
	    user = (User)session.getAttribute("user");
	    if(user == null){
		String str = url+"Login";
		res.sendRedirect(str);
		return; 
	    }
	}
	else{
	    String str = url+"Login";
	    res.sendRedirect(str);
	    return; 
	}
	// 
	// we have to make sure that this directory exits
	// if not we create it
	//
	File myDir = new File(attachment_path);
	if(!myDir.isDirectory()){
	    myDir.mkdirs();
	}
	// newFile = "spon"+month+day+seq; // no extension 
	// boolean isMultipart = ServletFileUpload.isMultipartContent(req);
	// System.err.println(" Multi "+isMultipart);
	//
	// Create a factory for disk-based file items
	DiskFileItemFactory factory = new DiskFileItemFactory();
	//
	// Set factory constraints
	factory.setSizeThreshold(maxMemorySize);
	//
	// if not set will use system temp directory
	// factory.setRepository(fileDirectory); 
	//
	// Create a new file upload handler
	ServletFileUpload upload = new ServletFileUpload(factory);
	// ServletFileUpload upload = new ServletFileUpload();
	//
	// Set overall request size constraint
	upload.setSizeMax(maxRequestSize);
	//
	String ext = "", old_name="";
	RentalFile rentalFile = new RentalFile(debug);

	List<FileItem> items = null;
	String content_type = req.getContentType();
	try{
	    if(content_type != null && content_type.startsWith("multipart")){
		// to suppress warning 
		// items = upload.parseRequest(req);
		List<?> tmp_items = (List<?>)upload.parseRequest(req);
		if (tmp_items != null) {
		    items = new ArrayList<>();
		    for (Object o : tmp_items) {
			FileItem q = (FileItem) o;
			items.add(q);
		    }
		}
		Iterator<FileItem> iter = items.iterator();
		while (iter.hasNext()) {
		    FileItem item = iter.next();
		    if (item.isFormField()) {
			//
			// process form fields
			//
			name = item.getFieldName();
			value = item.getString();
			if (name.equals("id")){  
			    id = value;
			    rentalFile.setId(value);
			}
			else if (name.equals("notes")) {
			    notes=value;
			    rentalFile.setNotes(value);
			}
			else if (name.equals("rental_id")){ 
			    rental_id = value;
			    rentalFile.setRental_id(value);
			}
			else if (name.equals("load_file")) {
			    load_file =value.replace('+',' ');
			}
			else if(name.equals("action")){
			    action = value;
			}
		    }
		    else {
			// String mimType = Magic.getMagicMatch(item.get(), false).getMimeType();
			// System.err.println(" type "+mimType);
			//
			// process uploaded item/items
			//
			// String fieldName = item.getFieldName();
												
			String contentType = item.getContentType();
			System.err.println(" context type "+contentType);
			if(Helper.mimeTypes.containsKey(contentType)){
			    ext = Helper.mimeTypes.get(contentType);
			}
			System.err.println(" ext from type "+ext);
			sizeInBytes = item.getSize();
			String oldName = item.getName();
			String filename = "";
			// 
			logger.debug("file "+oldName);
			if (oldName != null && !oldName.equals("")) {
			    filename = FilenameUtils.getName(oldName);
			    old_name = filename;
			    if(ext.equals("")){
				ext = Helper.getFileExtensionFromName(filename);
				System.err.println(" ext from name "+ext);
			    }
			    //
			    // create the file on the hard drive and save it
			    //
			    if(sizeInBytes > maxDocSize) 
				sizeLimitExceeded = true;
			    if(sizeLimitExceeded){
				message = " File Uploaded exceeds size limits "+
				    sizeInBytes;
				success = false;
			    }
			    else if(success){
				//
				// get a new name
				//
				rentalFile.setOldName(old_name);
				rentalFile.composeName(ext);
				newFile = rentalFile.getName();
				if(!newFile.equals("")){
				    saveDirectory = rentalFile.getFullPath(attachment_path, ext);
				    File file = new File(saveDirectory, newFile);
				    item.write(file);
				}
				else{
				    message = "Error: no file name assigned ";
				    success = false;
				}
			    }
			}
		    }
		}
	    }
	    else{
		Enumeration<String> values = req.getParameterNames();
		while (values.hasMoreElements()){
		    name = values.nextElement().trim();
		    vals = req.getParameterValues(name);
		    if(vals == null) continue;
		    value = vals[vals.length-1].trim();	
		    if (name.equals("id")){
			id = value;
			rentalFile.setId(value);
		    }
		    else if(name.equals("rental_id")){
			rental_id = value;
			rentalFile.setRental_id(value);
		    }
		    else if(name.equals("notes")){
			rentalFile.setNotes(value);
		    }
		    else if(name.equals("action")){
			action = value;
		    }	
		}
	    }
	}
	catch(Exception ex){
	    logger.error(ex);
	    success = false;
	    message += ex;
	}
	//
	if(action.equals("Save") && !sizeLimitExceeded){
	    date = Helper.getToday();
	    rentalFile.setAddedBy(user);
	    String back = rentalFile.doSave();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		id = rentalFile.getId();
	    }
	}
	if(action.equals("Update")){
	    String back = rentalFile.doUpdate();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	}
	else if(action.equals("Delete")){
	    String back = rentalFile.doDelete();
	    if(!back.equals("")){
		message += back;
		success = false;
	    }
	    else{
		id="";
	    }
	}	
	else if(action.equals("download")){
	    String back = rentalFile.doSelect();
	    String filename = rentalFile.getName();
	    String filePath = rentalFile.getPath(attachment_path);
	    filePath += filename;
	    doDownload(req, res, filePath, rentalFile);
	    return;
	}
	else if(action.equals("") && !id.equals("")){
	    String back = rentalFile.doSelect();
	    rental_id = rentalFile.getRental_id();
	}
	List<RentalFile> files = null;
	if(!rental_id.equals("")){
	    RentalFileList rfl = new RentalFileList(debug, rental_id);
	    String back = rfl.find();
	    if(!back.equals("")){
		message += back;
	    }
	    else{
		List<RentalFile> ones = rfl.getFiles();
		if(ones != null && ones.size() > 0){
		    files = ones;
		}
	    }
	}
	out.println("<html><head><title>Rental Attachments</title>");
	Helper.writeWebCss(out, url);
	out.println("<script type=\"text/javascript\">");
	out.println(" </script>		                         ");
	if(success){
	    if(!message.equals(""))
		out.println("<h3>"+message+"</h3>");
	}
	else{
	    if(!message.equals(""))
		out.println("<h3><font color=\"red\">"+message+"</font></h3>");
	}
	Helper.writeTopMenu(out, url);
	out.println("<form name=\"myForm\" method=\"post\" "+
		    "ENCTYPE=\"multipart/form-data\" >");
	if(!id.equals("")){
	    out.println("<h3>File Upload "+id+"</h3>");
	    out.println("<input type=\"hidden\" name=\"id\" value=\""+id+"\" />");
	}
	if(!rental_id.equals("")){
	    out.println("<input type=\"hidden\" name=\"rental_id\" value=\""+rental_id+"\" />");
	}	
	//
	out.println("<fieldset><legend>Upload Files</legend>");
	out.println("<table border=\"1\" width=\"75%\">");
	out.println("<tr><td>");
	//
	out.println("<table width=\"100%\">");
	out.println("<tr><td><label>Related Rental :</label><a href=\""+url+"Rental?id="+rental_id+"&action=zoom\">"+rental_id+"</a></td></tr>");						
	if(id.equals("")){
	    out.println("<tr><td>"+
			"To upload a new document "+
			"<ul>"+
			"<li>Scan the document if still on paper</li>"+
			"<li>Download it to your computer </li>"+
			"<li>Click on the Browse button to locate this file"+
			" on your computer.</li> "+
			"<li> Click on Save.</li>"+
			"<li> A new link to the uploaded "+
			" file will be shown below.</li>"+
			"<li> Supported documents are images, MS Documents, PDF;s, web pages, spread sheets, etc </li>"+
			"</ul>");
	    out.println("</td></tr>");
	    out.println("<tr><td><label>File </label> "); 
	    out.println("<input type=\"file\" name=\"load_file\" "+
			" size=\"30\"></td></tr>");
	    out.println("<tr><td class=\"left\"><label>Notes </label></td></tr>");
	    out.println("<tr><td class=\"left\">"); 
	    out.println("<textarea name=\"notes\" cols=\"70\" rows=\"5\" wrap=\"wrap\">");
	    out.println("</textarea></td></tr>");
	    out.println("</table></td></tr>");												
	    out.println("<tr><td align=\"right\">  "+
			"<input type=\"submit\" name=\"action\" "+
			"value=\"Save\">"+
			"</td></tr>");

	}
	else{
	    out.println("<tr><td><label>Date: </label>"+rentalFile.getDate()+"</td></tr>");
	    out.println("<tr><td><label>Added By: </label>"+rentalFile.getAddedBy()+"</td></tr>");						
	    out.println("<tr><td><label>File: </label> <a href=\""+url+"RentalFileServ?id="+id+"&action=download\"> "+rentalFile.getOldName()+"</a> </td></tr>");
	    if(rentalFile.hasNotes()){
		out.println("<tr><td><label>Notes: </label>"+rentalFile.getNotes()+"</td></tr>");
	    }
	    out.println("<tr>");
	    out.println("<td align=\"right\">  "+
			"<input type=\"submit\" name=\"action\" "+
			"onclick=\"validateDelete();\" "+
			"value=\"Delete\">"+
			"</td>");
	    out.println("</tr>");				
	}

	out.println("</table></td></tr>");
	out.println("</table>");				
	out.println("</fieldset>");
	out.println("</form>");
	if(files != null && files.size() > 0){
	    Helper.printFiles(out, url, files);
	}
	//
	// send what we have so far
	//
	out.print("</body></html>");
	out.close();

    }

    void doDownload(HttpServletRequest request,
		    HttpServletResponse response,
		    String inFile,
		    RentalFile rentalFile){
		
	BufferedInputStream input = null;
	BufferedOutputStream output = null;
	try{
	    //
	    // Decode the file name (might contain spaces and so on) and prepare file object.
	    // File file = new File(filePath, URLDecoder.decode(inspFile, "UTF-8"));
	    File file = new File(inFile);
	    //
	    // Check if file actually exists in filesystem.
	    //
	    if (!file.exists()) {
		// Do your thing if the file appears to be non-existing.
		// Throw an exception, or send 404, or show default/warning page, or just ignore it.
		response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
		return;
	    }
	    //
	    // Get content type by filename.
	    String contentType = context.getMimeType(file.getName());
	    //
	    // To add new content types, add new mime-mapping entry in web.xml.
	    if (contentType == null) {
		contentType = "application/octet-stream";
	    }
	    //			
	    // Init servlet response.
	    response.reset();
	    response.setBufferSize(DEFAULT_BUFFER_SIZE);
	    response.setContentType(contentType);
	    response.setHeader("Content-Length", String.valueOf(file.length()));
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + (rentalFile.getOldName().equals("")?rentalFile.getName():rentalFile.getOldName()) + "\"");
	    //
	    // Prepare streams.
	    //
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
	}
	catch(Exception ex){
	    logger.error(ex);
        } finally {
	    close(output);
            close(input);
        }
    }	
    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}






















































