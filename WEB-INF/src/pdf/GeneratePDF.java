package pdf;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Bryan
 * Date: 4/15/13
 * Time: 7:58 PM
 */
public class GeneratePDF {
	String logUserId = "0";
	public String path = "c:/PDF";

	public void genrateCmd(String reqURL, String folderName, String subFolderName, String id) {
		try {
			File destFoldereGP = new File("c:/eGP");
			if (destFoldereGP.exists() == false) {
				destFoldereGP.mkdirs();
			}

			File destFolderPDF = new File("c:/PDF/");
			if (destFolderPDF.exists() == false) {
				destFolderPDF.mkdirs();
			}

			File destFolder = new File("c:/PDF/" + folderName);
			if (destFolder.exists() == false) {
				destFolder.mkdirs();
			}

			File destSubFolder = new File("c:/PDF/" + folderName + "/" + subFolderName);
			if (destSubFolder.exists() == false) {
				destSubFolder.mkdirs();
			}

//For Image change 'wkhtmltopdf.exe' to 'wkhtmltoimage.exe' and '.pdf' to '.jpeg'
			System.out.println("Attempting to create PDF...");
			System.out.println("C:/wkhtmltopdf/wkhtmltopdf.exe " + reqURL + " c:/PDF/" + folderName + "/" + subFolderName + "/" + id + ".pdf");
			Process p = Runtime.getRuntime().exec("C:/wkhtmltopdf/wkhtmltopdf.exe " + reqURL + " c:/PDF/" + folderName + "/" + subFolderName + "/" + id + ".pdf");

		} catch (IOException e1) {
			System.out.println("Exception::" + e1);
			e1.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception::" + e);
			e.printStackTrace();
		}
	}
}
