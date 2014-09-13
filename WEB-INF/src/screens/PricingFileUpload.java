package screens;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sqlrow.Categories;
import sqlrow.Category;
import sqlrow.Task;
import sqlrow.Utils;


public class PricingFileUpload extends HttpServlet {
	private static final String TMP_DIR_PATH = "\\temp";
	private File tmpDir;
	private static final String DESTINATION_DIR_PATH ="/pricing";
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
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(1024*1024*1024); //1 GB
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
				File file = new File(destinationDir, "pricing_" + companyIDString + ".xml");
				fileItem.write(file);

				int companyID = Utils.parseInt(companyIDString, 0);
				Categories.deleteAll(companyID);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				document.getDocumentElement().normalize();
				NodeList categories = document.getElementsByTagName("category");

				for(int i = 0; i < categories.getLength(); i++) {
					Node catNode = categories.item(i);

					if(catNode.getNodeType() == Node.ELEMENT_NODE) {
						Element catElement = (Element) catNode;
						Category category = new Category();
						category.setName(catElement.getElementsByTagName("name").item(0).getTextContent());
						category.setCompanyID(companyID);
						category.insert();

						NodeList subCategories = catElement.getElementsByTagName("subcategory");

						for(int j = 0; j < subCategories.getLength(); j++) {
							Node subCatNode = subCategories.item(j);

							if(subCatNode.getNodeType() == Node.ELEMENT_NODE) {
								Element subCatElement = (Element) subCatNode;

								Category subCategory = new Category();
								subCategory.setName(subCatElement.getElementsByTagName("name").item(0).getTextContent());
								subCategory.setParentID(category.getID());
								subCategory.setCompanyID(companyID);
								subCategory.insert();

								NodeList tasks = subCatElement.getElementsByTagName("task");

								for(int k = 0; k < tasks.getLength(); k++) {
									Node taskNode = tasks.item(k);

									if(taskNode.getNodeType() == Node.ELEMENT_NODE) {
										Element taskElement = (Element) taskNode;

										Task task = new Task();
										task.setName(taskElement.getElementsByTagName("name").item(0).getTextContent());
										task.setNumber(taskElement.getElementsByTagName("number").item(0).getTextContent());
										task.setDescription(taskElement.getElementsByTagName("description").item(0).getTextContent());
										task.setTime(Utils.parseInt(taskElement.getElementsByTagName("time").item(0).getTextContent(), 0));
										task.setParts(Utils.parseDouble(taskElement.getElementsByTagName("parts").item(0).getTextContent(), 0.0));

										Element col3 = (Element) taskElement.getElementsByTagName("col3").item(0);
										task.setStandard(Utils.parseDouble(col3.getElementsByTagName("primary").item(0).getTextContent(), 0.0));
										task.setStandardAddOn(Utils.parseDouble(col3.getElementsByTagName("addon").item(0).getTextContent(), 0.0));

										Element col2 = (Element) taskElement.getElementsByTagName("col2").item(0);
										task.setMember(Utils.parseDouble(col2.getElementsByTagName("primary").item(0).getTextContent(), 0.0));
										task.setMemberAddOn(Utils.parseDouble(col2.getElementsByTagName("addon").item(0).getTextContent(), 0.0));

										task.setCategoryID(subCategory.getID());
										task.insert();
									}
								}
							}
						}
					}
				}
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