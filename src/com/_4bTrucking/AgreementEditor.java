package com._4bTrucking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.Item;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

@Path("/PdfService")
public class AgreementEditor {
	private static PDDocument _pdfDocument;
	private static int count;

	@GET
	@Path("/editForm")
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveAgreement(@DefaultValue("") @QueryParam("data") String data) {
		// data = "{\"City\":\"Prithvi\", \"agreementType\":
		// \"ContractCarrierAgreement\"}";
		System.out.println(data);
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> fieldNameAndValue = gson.fromJson(data, type);
		String agreementType = fieldNameAndValue.get("agreementType");
		System.out.println(fieldNameAndValue);
		/*String src = "G:/Softwares/webserver/apache-tomcat-8.0.27/apache-tomcat-8.0.27/webapps/4bTruckingAgreementSave/"
				+ agreementType + "Src.pdf";
		String test = "G:/Softwares/webserver/apache-tomcat-8.0.27/apache-tomcat-8.0.27/webapps/4bTruckingAgreementSave/"
				+ agreementType + ".pdf";*/
		/*String src = "/data/apache-tomcat-7.0.35/webapps/4bTruckingAgreementSave/"
				+ agreementType + "Src.pdf";
		String test = "/data/apache-tomcat-7.0.35/webapps/4bTruckingAgreementSave/"
				+ agreementType + ".pdf";*/
		String src = "/data/tomcat8/webapps/4bTruckingAgreementSave/"
		+ agreementType + "Src.pdf";
String test = "/data/tomcat8/webapps/4bTruckingAgreementSave/"
		+ agreementType + ".pdf";
		try {
			File target = new File(test);
			File pdf = new File(src);
			_pdfDocument = PDDocument.load(pdf);
			System.out.println(_pdfDocument.getNumberOfPages());
			PDDocumentCatalog docCatalog = _pdfDocument.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();
			List<PDField> fields = acroForm.getFields();
			PDField legalName = acroForm.getField("Legal Name");
			//legalName.setValue(fieldNameAndValue.get("Legal Name"));
			for (PDField pdField : fields) {
				System.out.println(pdField.getFullyQualifiedName() + ":"
						+ (pdField instanceof PDTextField));
				if ((pdField != null && pdField instanceof PDTextField) && pdField!=legalName ) {
					System.out.println(pdField.getFullyQualifiedName());
					pdField.setValue(fieldNameAndValue.get(pdField.getFullyQualifiedName()) == null ? ""
							: fieldNameAndValue.get(pdField.getFullyQualifiedName()));
					pdField.setReadOnly(true);
				}
			}
			//acroForm.flatten();
			_pdfDocument.save(target);
			_pdfDocument.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(200).entity("ok").build();
	}
	@GET
	@Path("/editFormItext")
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveAgreementItext(@DefaultValue("") @QueryParam("data") String data) throws Exception {
		System.out.println(data);
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> fieldNameAndValue = gson.fromJson(data, type);
		String agreementType = fieldNameAndValue.get("agreementType");
		System.out.println(fieldNameAndValue);
		/*String src = "G:/Softwares/webserver/apache-tomcat-8.0.27/apache-tomcat-8.0.27/webapps/4bTruckingAgreementSave/"
				+ agreementType + "Src.pdf";
		String test = "G:/Softwares/webserver/apache-tomcat-8.0.27/apache-tomcat-8.0.27/webapps/4bTruckingAgreementSave/"
				+ agreementType + ".pdf";*/
		String src = "/data/tomcat8/webapps/4bTruckingAgreementSave/"
				+ agreementType + "Src.pdf";
		String test = "/data/tomcat8/webapps/4bTruckingAgreementSave/"
				+ agreementType + ".pdf";
		PdfStamper stamper = null;
		PdfReader reader = null;
		try {
			File target = new File(test);
			target.delete();
	        reader = new PdfReader(src);
	        stamper = new PdfStamper(reader, new FileOutputStream(test));
	        AcroFields form = stamper.getAcroFields();
	        System.out.println("before for each map");
	        for (Map.Entry<String, Item> entry : form.getFields().entrySet())
	        {
	        	String key = entry.getKey();
	        	String field=form.getField(key);
	        	System.out.println(key +" "+entry.getValue()+" "+form.getFieldType(key));
	        	if (key!=null && entry.getValue()!=null && form.getFieldType(key)!=6) {
	        		form.setFieldProperty(key, "textsize", new Float(14), null);
	        		form.setField(key, fieldNameAndValue.get(key));
				}
	        }
	        form.setFieldProperty("Legal Name", "textsize", new Float(8), null);
	        form.setField("Legal Name", fieldNameAndValue.get("Legal Name"));
	        stamper.setFormFlattening(true);
	        stamper.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (stamper!=null) {
				stamper.close();
			}
			if (reader!=null) {
				reader.close();
			}
		}
		return Response.status(200).entity("ok").build();
	}
}
