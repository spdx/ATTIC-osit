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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.sdk.SDKInterfaceImpl;
import com.sec.ose.osi.util.Property;

/**
 * IdentifyErrorQueue
 * @author hankido.lee, suhyun47.kim
 * 
 */
public class IdentifyErrorQueue {
	private static Log log = LogFactory.getLog(IdentifyErrorQueue.class);
	
	private String userID;
	private static final String DAT_FILE_DIRECTORY = Property.FOLDER_DAT;
	private String identifyQueueErrorFileName = null;
	
	private List<IdentifyData> identifyDataQueueError = new CopyOnWriteArrayList<IdentifyData>();
	
	
	private volatile static IdentifyErrorQueue instance = null;

	public static IdentifyErrorQueue getInstance() {
		if(instance == null) {
			synchronized(IdentifyErrorQueue.class) {
				if(instance == null) {
					instance = new IdentifyErrorQueue();
				}
			}
		}
		return instance;
	}

	private IdentifyErrorQueue(){
		
		userID = SDKInterfaceImpl.getInstance().getUserID();
		init(userID);
	}
	
	private void init(String userID) {	
		identifyQueueErrorFileName = DAT_FILE_DIRECTORY+"/error_"+userID+".dat";
		
		File dir = new File(DAT_FILE_DIRECTORY);
		if(!dir.exists()) {
			if(dir.mkdirs() == false) {
				System.err.println("Can not create folder");
			}
		}
		File file = new File(identifyQueueErrorFileName);
		FileInputStream fis = null;
		ObjectInputStream oisReader = null;
		try {
			if(!file.exists()) {
				file.createNewFile();
			} else {
				fis = new FileInputStream(identifyQueueErrorFileName);
				if(fis != null && fis.available()>0) {
					oisReader = new ObjectInputStream(fis);
		
					IdentifyData tmpIdentifiedData;

					while((tmpIdentifiedData = (IdentifyData)oisReader.readObject()) != null) {
						
						log.debug("read from File : \n" + tmpIdentifiedData + "\n - queueSize: "+size()) ;
						identifyDataQueueError.add(tmpIdentifiedData);
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
		
		updateDatFileByIdentifyErrorQueue();
	}

	public void enqueue(IdentifyData pIdentifiedData) {
		log.debug("enqueue");
		identifyDataQueueError.add(pIdentifiedData);
		updateDatFileByIdentifyErrorQueue();
	}
	
	public int size() {
		return identifyDataQueueError.size();
	}
	
	private boolean updateDatFileByIdentifyErrorQueue() {
		
		log.debug("updateIdentifyErrorQueueFile-start - num of item:"+size());
		
		FileOutputStream fos = null;
    	try {
    		// Check Folder
    		File dir = new File(DAT_FILE_DIRECTORY);
    		if(!dir.exists()) {
    			if(dir.mkdirs() == false) {
    				System.err.println("Can not create folder.");
    			}
    		}

    		// Check File
    		File file = new File(identifyQueueErrorFileName);
			if(file.exists() == false) {
				file.createNewFile();
			}
        	fos = new FileOutputStream(file);	// Overwrite
        	if(identifyDataQueueError.size() > 0) {
	        	ObjectOutputStream oosWriter = new ObjectOutputStream(fos);
	        	for(IdentifyData tmpIdentifiedData:identifyDataQueueError) {
	        		oosWriter.writeObject(tmpIdentifiedData);
	        	}
	        	oosWriter.flush();
	        	oosWriter.close();
        	}
        	fos.close();
        	
        	log.debug("updateIdentifyErrorQueueFile-end");
        	
    		// write text file
    		writeTextFile(toHTMLForm());
    		
        	return true;
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
    	return false;
	}
	
	private String identifyQueueErrorTextFileName = "";
	private BufferedWriter bw = null;
	public void writeTextFile(String str) {
		
		if(size() <= 0) return;
		
		String userID = SDKInterfaceImpl.getInstance().getUserID();
		identifyQueueErrorTextFileName = DAT_FILE_DIRECTORY+"/error_"+userID+".txt";
		// check "logs" directory exists.
		if (!new File(DAT_FILE_DIRECTORY).exists()) {
			File f = new File(DAT_FILE_DIRECTORY);
			f.mkdirs();
		}
		
		try {
			bw = new BufferedWriter(new FileWriter(identifyQueueErrorTextFileName));
			bw.write(str);
			bw.write("\r\n");
			bw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			try { if (bw != null) bw.close(); } catch (Exception e2) {}
		}
	}

	private StringBuffer outputString = null; 
	public String toHTMLForm() {
		
		
		return toHTMLForm(identifyDataQueueError.size());
	}

	public String toHTMLForm(int numOfItemsToBeshow) {
		if(outputString == null) {
			outputString = new StringBuffer("");
		}
		outputString.setLength(0);
		
		int count = 1;
		outputString.append("<HTML>");
		for(IdentifyData id : identifyDataQueueError) {
			
			outputString.append("\r\n\r\n<HR>");
			outputString.append("#").append(count++);
			outputString.append(" (created at ").append(id.getTimeStamp()).append(")");
			outputString.append("<HR>");
			outputString.append(id.toHTMLContents());
			outputString.append("<BR><BR>");
			
			if(count > numOfItemsToBeshow) break;
		}
		outputString.append("\r\n</HTML>");
		
		return outputString.toString();
	}
}
