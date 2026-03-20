package com.vts.pfms.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;

@Component
public class PMSLogoUtil 
{
	@Value("${ApplicationFilesDrive}")
	private String LabLogoPath;
	SimpleDateFormat  inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public String getLabLogoAsBase64String(String LabCode) throws IOException
	{
		//String path = LabLogoPath+"/images/lablogos/"+LabCode.trim().toLowerCase()+".png";
		System.out.println(LabCode);
		String path = Paths.get(LabLogoPath, "images", "lablogos", LabCode.trim().toLowerCase()+".png").toString();
		System.out.println(path);
		String path2 = Paths.get(LabLogoPath, "images", "lablogos", "lablogo.png").toString();
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(path)));
		}catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path "+path);
			File lablogo = new File(path2);
			if(lablogo.exists()) {
				return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(lablogo));
			}
			else
			{
				return null;
			}
		}
	}
	
	public String getLabImageAsBase64String(String LabCode) throws IOException
	{
		//String path = LabLogoPath+"/images/lablogos/"+LabCode.trim().toLowerCase()+".jpg";
		String path = Paths.get(LabLogoPath, "images", "lablogos", LabCode.trim().toLowerCase()+".jpg").toString();
		String path2 = Paths.get(LabLogoPath, "images", "lablogos", "lablogo.jpg").toString();
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(path)));
		}catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path "+path);
			File lablogo = new File(path2);
			if(lablogo.exists()) {
				return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(lablogo));
			}
			else
			{
				return null;
			}
		}
	}
	
	public String getDRDOLogoAsBase64String() throws IOException
	{
		String path = Paths.get(LabLogoPath, "images", "lablogos", "drdo.png").toString();
		try {
			
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(path)));
		}catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path "+path);
			return null;
		}
	}
	public String formatD(String date) throws ParseException {
		String approvedDate=outputFormat.format(inputFormat.parse(date));
		String[] approvedDate2=approvedDate.toString().split(":");
		String approvedDateFormatted=approvedDate2[0]+" : "+approvedDate2[1];
		return approvedDateFormatted;
	}
	

	public String getClusterLabsAsBase64String() throws IOException
	{
		String path = Paths.get(LabLogoPath, "images", "lablogos", "clusterLabs.png").toString();
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(path)));
		}catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path "+path);
			return null;
		}
	}
	
	public String getThankYouImageAsBase64String() throws IOException
	{
		String path = Paths.get(LabLogoPath, "images", "lablogos", "thankyou.png").toString();
		try {
			return Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(new File(path)));
		}catch (FileNotFoundException e) {
			System.err.println("File Not Found at Path "+path);
			return null;
		}
	}
	
}
