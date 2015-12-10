()package com.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;

public class FilesDemo {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		String textXML = IOUtils.toString(FilesDemo.class.getClassLoader().getResourceAsStream("fileinfo.xml"),"UTF-8");
		FileInfo fileInfo=parseXMLToECTemplates(textXML);
		List<FileData> fileDataList=fileInfo.getFiledata();
		ExecutorService executor=Executors.newFixedThreadPool(fileDataList.size());
		List<Future<Integer>> resultList=new ArrayList<Future<Integer>>();
		for (FileData fileData : fileDataList) {
			FileCopier fc=new FileCopier(fileData);
			resultList.add(executor.submit(fc));
		}
		
		Iterator<Future<Integer>> itr=resultList.iterator();
		while(itr.hasNext()){
			Integer result=itr.next().get();
			
			if(result == 0){
				System.out.println("result is 0");
				itr.remove();
			}
		}
		
		if(resultList.size() ==0){
			executor.shutdown();
		}
		
		
		
	}

	private static FileInfo parseXMLToECTemplates(String textXML) {
		FileInfo fileInfo = null;
		try {

			JAXBContext context = JAXBContext.newInstance(FileInfo.class);
			javax.xml.bind.Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			fileInfo = (FileInfo) jaxbUnmarshaller.unmarshal(new StringReader(
					textXML));

		} catch (JAXBException ex) {
			ex.printStackTrace();
		}
		return fileInfo;
	}

}

@XmlRootElement(name = "files")
class FileInfo {
	private List<FileData> filedata;

	public List<FileData> getFiledata() {
		return filedata;
	}

	public void setFiledata(List<FileData> filedata) {
		this.filedata = filedata;
	}

	

}

@XmlRootElement(name = "filedata")
@XmlAccessorType(XmlAccessType.FIELD)
class FileData {
	private String srcdir;
	private String srcfile;
	private String destdir;
	private String destfile;

	public String getSrcdir() {
		return srcdir;
	}

	public void setSrcdir(String srcdir) {
		this.srcdir = srcdir;
	}

	public String getSrcfile() {
		return srcfile;
	}

	public void setSrcfile(String srcfile) {
		this.srcfile = srcfile;
	}

	public String getDestdir() {
		return destdir;
	}

	public void setDestdir(String destdir) {
		this.destdir = destdir;
	}

	public String getDestfile() {
		return destfile;
	}

	public void setDestfile(String destfile) {
		this.destfile = destfile;
	}

}

