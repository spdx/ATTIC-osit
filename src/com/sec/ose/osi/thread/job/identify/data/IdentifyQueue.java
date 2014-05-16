/**
 * Copyright(C) 2013-2014 Samsung Electronics Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
*/
package com.sec.ose.osi.thread.job.identify.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.SDKInterfaceImpl;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.DateUtil;
import com.sec.ose.osi.util.tools.FileOperator;
import com.sec.ose.osi.util.tools.FormatUtil;

/**
 * IdentifyQueue
 * @author hankido.lee, suhyun47.kim
 * 
 */
public class IdentifyQueue {
	private static Log log = LogFactory.getLog(IdentifyQueue.class);
	
	private static final String DAT_FILE_DIRECTORY = Property.FOLDER_DAT;
	private static final String LOG_FILE_DIRECTORY = Property.FOLDER_LOGS;
	
	private String identifyQueueFilePath = null;
	private String transactionLogFilePath = null;
	private boolean transactionLogWrite = false;
	
	private List<IdentifyData> identifyDataQueue = new CopyOnWriteArrayList<IdentifyData>();
	
	private volatile static IdentifyQueue instance = null;
	
	public static IdentifyQueue getInstance() {
		if(instance == null) {
			synchronized(IdentifyQueue.class) {
				if(instance == null) {
					instance = new IdentifyQueue();
				}
			}
		}
		return instance;
	}
	
	private IdentifyQueue(){
		init();
	}
	
	private void init() {
		
		String userID = SDKInterfaceImpl.getInstance().getUserID();
		transactionLogFilePath = LOG_FILE_DIRECTORY+"/TRANSACTION_LOG_"+userID+"_"+DateUtil.getCurrentTime("%1$tY%1$tm%1$td")+".dat";
		identifyQueueFilePath = DAT_FILE_DIRECTORY+"/identified_"+userID+".dat";
		
		
		log.debug("open Identify Queue File: \""+identifyQueueFilePath+"\"");
		
		prepareWritingTransactionLogFolder();
		if("true".equals(Property.getInstance().getProperty(Property.LOGGING_IDENTIFY_TRANSACTIONS))) {
			transactionLogWrite = true;
		}
		
		File dir = new File(DAT_FILE_DIRECTORY);
		if(!dir.exists()) {
			if(dir.mkdirs() == false) {
				log.error("FATAL:" + DAT_FILE_DIRECTORY+" is not created!!");
			}
		}

		File file = new File(identifyQueueFilePath);
		FileInputStream fis = null;
		ObjectInputStream oisReader = null;
		try {
			if(!file.exists()) {
				file.createNewFile();
			} else {
				fis = new FileInputStream(identifyQueueFilePath);
				if(fis != null && fis.available()>0) {
					oisReader = new ObjectInputStream(fis);
		
					IdentifyData tmpIdentifiedData;

					while((tmpIdentifiedData = (IdentifyData)oisReader.readObject()) != null) {
						
						log.debug("read from File : \n" + tmpIdentifiedData + "\n - queueSize: "+size()) ;
						
						int type = tmpIdentifiedData.getCompositeType();
						if((type & IdentificationConstantValue.CODE_MATCH_TYPE) != 0) {
							
							// step1
							if(tmpIdentifiedData.getCurrentComponentName() == null || 
									tmpIdentifiedData.getCurrentComponentName().length() == 0)
								continue;
							
							// step2
							if(tmpIdentifiedData.getCurrentLicenseName() == null || 
									tmpIdentifiedData.getCurrentLicenseName().length() == 0)
								continue;
						}
						
						identifyDataQueue.add(tmpIdentifiedData);
						log.debug("add - queueSize: "+size()) ;

						
					}
				}
			}
		} catch (IOException e) {
			log.debug("The end of the stream/block data has been reached");
		} catch (Exception e) {
			log.warn(e);
		} finally {
			try {
				if(oisReader != null) { try{oisReader.close();} catch(Exception e){log.debug(e);} }
				if(fis != null) { try{fis.close();} catch(Exception e){log.debug(e);} }
			} catch (Exception e) {
				log.warn(e);
			}
		}
		
		updateIdentifyQueuFile();
	}

	private void prepareWritingTransactionLogFolder() {
		File log_dir = new File(LOG_FILE_DIRECTORY);
		if(!log_dir.exists()) {
			if(log_dir.mkdirs() == false) {
				log.error("FATAL:" + LOG_FILE_DIRECTORY+" is not created!!");
			}
		}
		
	}

	public  void enqueue(IdentifyData pIdentifiedData) {
		log.debug("enqueue");
		identifyDataQueue.add(pIdentifiedData);
		if(transactionLogWrite == true) {
			appendTransactionFile(pIdentifiedData);
		}
		updateIdentifyQueuFile();
	}
	
	public  int size() {
		return identifyDataQueue.size();
	}
	
	public IdentifyData dequeue() {
		log.debug("dequeue");
		IdentifyData removedData = identifyDataQueue.get(0);
		identifyDataQueue.remove(0);
		updateIdentifyQueuFile();
		
		return removedData;
	}

	private  boolean updateIdentifyQueuFile() {
		
		log.debug("updateIdentifyQueFile-start - num of item:"+size());
		
		FileOutputStream fos = null;
    	try {
        	File file = new File(identifyQueueFilePath);
        	fos = new FileOutputStream(file);
        	if(identifyDataQueue.size() > 0) {
	        	ObjectOutputStream oosWriter = new ObjectOutputStream(fos);
	        	for(IdentifyData tmpIdentifiedData:identifyDataQueue) {
	        		oosWriter.writeObject(tmpIdentifiedData);
	        	}
	        	oosWriter.flush();
	        	oosWriter.close();
        	}
        	
    	} catch (IOException e) {
    		log.warn(e);
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
        	log.debug("updateIdentifyQueFile-end");

		}
    	return true;
	}

	private  void appendTransactionFile(IdentifyData pIdentifiedData) {
		
		log.debug("append transaction: "+pIdentifiedData.exportCSVForm());
		
		FileOutputStream fos = null;
		ObjectOutputStream oosWriter = null;
    	try {
        	File file = new File(transactionLogFilePath);
        	if(file.exists()==true) {
        		fos = new FileOutputStream(file, true);	
        		oosWriter = new ObjectOutputStream(fos);
        	} else {
        		fos = new FileOutputStream(file, true);	
        		
        		oosWriter = new AppendAllowedObjectOutputStream(fos);
        	}
        	
       		oosWriter.writeObject(pIdentifiedData);
        	oosWriter.flush();
        	oosWriter.close();
        	
    	} catch (IOException e) {
    		log.warn(e);
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	public  IdentifyData firstElement() {
		return (identifyDataQueue.size() > 0)? identifyDataQueue.get(0) : null;
	}

	private StringBuffer mHTMLBuf = new StringBuffer(); 

	public String toHTMLForm() {
		
		return toHTMLForm(identifyDataQueue.size());
	}

	public boolean isIdentifyCompleted(String projectName) {
		for(IdentifyData idData : identifyDataQueue) {
			if(projectName.equals(idData.getProjectName())) {
				return false;
			}
		}
		return true;
	}

	public synchronized String  toHTMLForm(int numOfItemsToBeshow) {
		
		
		mHTMLBuf.setLength(0);
		
		int count = 1;
		mHTMLBuf.append("<HTML>");
		for(IdentifyData id : identifyDataQueue) {
			
			mHTMLBuf.append("\r\n\r\n<HR>");
			mHTMLBuf.append("#").append(count++);
			mHTMLBuf.append(" (created at ").append(id.getTimeStamp()).append(")");
			mHTMLBuf.append("<HR>");
			mHTMLBuf.append(id.toHTMLContents());
			mHTMLBuf.append("<BR><BR>");
			
			if(count > numOfItemsToBeshow) break;
		}
		mHTMLBuf.append("\r\n</HTML>");
		
		return mHTMLBuf.toString();
	}

	public boolean makeBackup() {
		
		if(this.size() <=0 )
			return true;
		
		String originalFilePath = identifyQueueFilePath;
		String backupFilePath = originalFilePath+".backup."+FormatUtil.getTimeForLicenseNotice();
		
		try {
			FileOperator.copyFile(
					new File(originalFilePath), 
					new File(backupFilePath));
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
}

class AppendAllowedObjectOutputStream extends ObjectOutputStream {
	public AppendAllowedObjectOutputStream(FileOutputStream out) throws IOException	 {
	  super(out);
	 }
	 
	 @Override
	 protected void writeStreamHeader() throws IOException {
	 }
}