package screens;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import sqlrow.Companies;
import sqlrow.Company;
import sqlrow.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 4/22/13
 * Time: 7:42 PM
 */
public class LogoFileUpload extends HttpServlet {
	private static final String TMP_DIR_PATH = "\\temp";
	private File tmpDir;
	public static String DESTINATION_DIR_PATH ="C:/images";
	private File destinationDir;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tmpDir = new File(TMP_DIR_PATH);
		if(!tmpDir.isDirectory()) {
			throw new ServletException(TMP_DIR_PATH + " is not a directory");
		}
		//String realPath = getServletContext().getRealPath(DESTINATION_DIR_PATH);
		destinationDir = new File(DESTINATION_DIR_PATH);
		if(!destinationDir.isDirectory()) {
			throw new ServletException(DESTINATION_DIR_PATH+" is not a directory");
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(1024*1024*10); //10 MB
		fileItemFactory.setRepository(tmpDir);

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			List items = uploadHandler.parseRequest(request);
			String companyIDString = null;
			FileItem fileItem = null;

			for (Object object : items) {
				FileItem item = (FileItem) object;
				if (item.getFieldName().equals("companyid")) {
					companyIDString = item.getString();
				}
				else if(!item.isFormField()) {
					fileItem = item;
				}
			}

			if(fileItem != null && Utils.notEmpty(fileItem.getName()) && Utils.notEmpty(companyIDString)) {
				String newPath = DESTINATION_DIR_PATH + "/" + companyIDString;
				File directory = new File(newPath);//getClass().getResource("../../../images/").getPath().replaceAll("%20", " ") + "/" + companyIDString);

				if(!directory.isDirectory()) {
					directory.mkdirs();
				}

				File file = new File(directory, fileItem.getName());
				fileItem.write(file);

				Company company = Companies.getCompany(Utils.parseInt(companyIDString, -1));
				company.setLogo(fileItem.getName());
				company.update();
			}
		}catch(FileUploadException ex) {
			System.out.println("Error encountered while parsing the request");
			ex.printStackTrace();
		} catch(Exception ex) {
			System.out.println("Error encountered while uploading file");
			ex.printStackTrace();
		}

		response.sendRedirect("control-panel/settings");
	}
}
