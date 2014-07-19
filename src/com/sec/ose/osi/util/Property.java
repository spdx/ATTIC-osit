/**
 * Copyright(C) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.Version;

/**
 * Property
 * @author suhyun47.kim, sjh.yoo, ytaek.kim, hankido.lee
 * 
 */
public class Property {
	private static Log log = LogFactory.getLog(Property.class);

	public static final String PROPERTY_FILE = "config.ini";

	public static final String RDF_ENCODING = "Rdf_Encoding";
	
	public static final String DEFALT_REPORT_LOCATION = "Default_Report_Location";
								// Identification Report, License Notice
								// ex. Default_Report_Location=D\:\\
	
	public static final String RECIPROCAL_LICENSE = "Reciprocal_License";
								// Licenses to be displayed RED in Identification Report
								// Reciprocal_License=GPL,LGPL,MPL,EPL
	
	public static final String MAJOR_LICENSE = "Major_License";
								// Licenses to be displayed ORANGE in Identification Report

	public static final String ANALYSIS_EXCLUDE_FILE_EXTENTION = "Analysis_Exclude_File_Extention";
								// file extensions which will be 
	public static final String ANALYSIS_EXCLUDE_FOLDER_NAME = "Analysis_Exclude_Folder_Name";

	public static final String PROXY_SERVER_IP = "Proxy_Server_IP";
	public static final String PROXY_SERVER_PORT = "Proxy_Server_Port";
	public static final String PROXY_USED_BY_SERVER = "Proxy_UsedBy_Server";
	
	public static final String LOGGING_IDENTIFY_TRANSACTIONS = "Logging_Identify_Transactions";  // "true" will work
	
	
	public static final String MINIMIZED_CODE_MATCH_PENDING_FILE_LIST_ENABLED = "Minimized_Code_Match_Pending_File_List_Enabled";  // "true" will work
							// "true" will improve performance
	
	/**
	 *  - Prevent loading discovery data when user select project in identify tab 
	 *    even local db already contains the discovery information for particular project.
	 *    
	 *  "true" will work, default=false
	 *  
	 *  "false" will improve performance - reducing project loading time.
	 *  
	 *  ex. Discovery_Data_Always_Reload_When_Select_Project=false
	 */
	public static final String DISCOVERY_DATA_ALWAYS_RELOAD_WHEN_SELECT_PRROJECT = "Discovery_Data_Always_Reload_When_Select_Project";
	
	/**
	 * 
	 * Request Identification to Server with Synchronous or Asynchronous
	 * 
	 * "false" is default and false will improve identification speed.
	 * if "true" option is setted, the request is finished after receiving complete signal from server
	 * if "false" option is setted, the request is finished without receiving complete signal from server
	 * 
	 * ex. Identification_With_Synchronous_Bom_Refresh=false
	 */
	public static final String IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH = "Identification_With_Synchronous_Bom_Refresh";

	public static final String MAX_NUM_OF_FILES_UPPER_LIMIT  = "Max_Num_Of_Files_Upper_Limit";
	public static final String MIN_NUM_OF_FILES_UPPER_LIMIT  = "Min_Num_Of_Files_Upper_Limit";
	public static final String DEFAULT_NUM_OF_FILES_UPPER_LIMIT  = "Default_Num_Of_Files_Upper_Limit";

	public static final String MAX_NUM_OF_REPORT_ENTITY = "Max_Num_Of_Report_Entity";
	
	public static final String  ENABLE_SERVER_SPECIFIC_SETTINGS = "Enable_Server_Specific_Settings";
	
	// Local File Map
	public static final String FOLDER_DAT = "DAT";
	public static final String FOLDER_TEMPLATE = "TEMPLATES";
	public static final String FOLDER_LOGS = "LOGS";
	
	public static final String FILE_IDENTIFICATION_DB = "ldbIdentification.db";
	public static final String FILE_COMPONENT_INFO_CACHE = "kdbComponentCache.dat";
	
	// Project Split
	public static final String LOCAL_OPTION_POSTFIX = "__local__";

	public static final int DEFAULT_MAX_NUM_OF_FILES_UPPER_LIMIT  = 100000;
	public static final int DEFAULT_MIN_NUM_OF_FILES_UPPER_LIMIT  = 1000;
	public static final int DEFAULT_OSI_NUM_OF_FILES_UPPER_LIMIT  = 50000;
	
	// StringFormatPolicy
	public static HashMap<String, String> HTML_RESERVED_CHAR_REPLACE_MAP = new HashMap<String, String>();
	// key: web keyword in Protex Server
	// value: string to be replaced.
	static {
		HTML_RESERVED_CHAR_REPLACE_MAP.put("&#10;", "\n");
		HTML_RESERVED_CHAR_REPLACE_MAP.put("&lt;", "<");
		HTML_RESERVED_CHAR_REPLACE_MAP.put("&gt;", ">");
		HTML_RESERVED_CHAR_REPLACE_MAP.put("&quot;", "\"");
		HTML_RESERVED_CHAR_REPLACE_MAP.put("&apos;", "'");
		HTML_RESERVED_CHAR_REPLACE_MAP.put("&amp;", "&");
	}
	
	private volatile static Property instance = null;

	public static Property getInstance() {
		if(instance == null) {
			synchronized(Property.class) {
				if(instance == null) {
					instance = new Property();
				}
			}
		}
		return instance;
	}

	private Properties mProperty;
	
	private Property() {

		boolean result = load();
		if(result == false) {
			JOptionPane.showMessageDialog(
					null, 
					"\""+PROPERTY_FILE+"\" is not existed or invalid.\nCannot run "+Version.getApplicationNameNice()+"without this file.", 
					"Exit",
					JOptionPane.ERROR_MESSAGE
					);
			log.debug("\""+PROPERTY_FILE+"\" is not existed.\nCannot run OSI without this file.");
			System.exit(0);
		}
		
		initSpecialPropertyValues();

	}

	public long getMaxNumOfReportEntity() {
		if("true".equalsIgnoreCase(getProperty(Property.MINIMIZED_CODE_MATCH_PENDING_FILE_LIST_ENABLED))) {
			return Long.parseLong(getProperty(Property.MAX_NUM_OF_REPORT_ENTITY)); 
		}
		return Long.MAX_VALUE;
	}

	public int getMinNumOfFilesUpperLimit() {
		try {
			return Integer.parseInt(Property.getInstance().getProperty(Property.MIN_NUM_OF_FILES_UPPER_LIMIT));
		} catch(Exception e) {}
		return DEFAULT_MIN_NUM_OF_FILES_UPPER_LIMIT;
	}

	public int getMaxNumOfFilesUpperLimit() {
		try {
			return Integer.parseInt(Property.getInstance().getProperty(Property.MAX_NUM_OF_FILES_UPPER_LIMIT));
		} catch(Exception e) {}
		return DEFAULT_MAX_NUM_OF_FILES_UPPER_LIMIT;
	}

	public int getDefaultNumOfFilesUpperLimit() {
		try {
			return Integer.parseInt(Property.getInstance().getProperty(Property.DEFAULT_NUM_OF_FILES_UPPER_LIMIT));
		} catch(Exception e) {}
		return DEFAULT_OSI_NUM_OF_FILES_UPPER_LIMIT;
	}

	private void initSpecialPropertyValues() {
		setProperty(IDENTIFICATION_WITH_SYNCHRONOUS_BOM_REFRESH, "true");
		setProperty(LOGGING_IDENTIFY_TRANSACTIONS, "false");
	}

	public void displayList() {
		this.mProperty.list(System.out);
	}

	public Iterator<String> getKeys() {
		ArrayList<String> list = new ArrayList<String>();
		
		Iterator<Object> itr = this.mProperty.keySet().iterator();
		if(itr == null) 
			return null;
		
		while(itr.hasNext()) {
			list.add(itr.next().toString());
		}
		
		return list.iterator();
	}

	public String getProperty(String pKey) {
		
		String value = this.mProperty.getProperty(pKey);
		if(value == null) {
			value = "";
		}
		
		return value;
	}

	private boolean load() {
		
		
		if(mProperty == null) {
			mProperty = new Properties();
			FileReader fr = null;
			try {
				
				File f = new File(PROPERTY_FILE);
				if(f.exists() == false) {
					return false;
				} 
				
				fr = new FileReader(PROPERTY_FILE);
				mProperty.load(fr);

			} catch (FileNotFoundException e) {
			
				log.warn(e);
				return false;

			} catch (IOException e) {
			
				log.warn(e);
				return false;
				
			} catch (Exception e) {
				log.warn(e);
				return false;
			}
			finally {
				
				try {
					if(fr != null) {
						fr.close();
					}
				} catch (IOException e) {
				
					log.warn(e);
				}
				fr = null;
			}
		}
		
		return true;
	}

	public void remove(String pKey) {
		
		this.mProperty.remove(pKey);
		save();
	}

	private void save() {
		
		FileWriter fw = null;
		
		try {
			fw = new FileWriter(PROPERTY_FILE);
			this.mProperty.store(
					fw, 
					"This file stores the property information for Open source Inspect Tool (OSIT) \r\n#"+
					"Your modification on this file will not be properly applied to OSIT Application.\r\n"+
					"The values must be changed by OSIT Application or restart OSIT to apply the modifications \r\n");
			fw.flush();
			
		} catch (IOException e) {
			log.warn(e);

		} finally {
			
			if(fw != null) {
				try {

					fw.close();
				} catch (IOException e) {
					log.warn(e);
				}
				
				fw = null;
			}
		}
	}
	
	/**
	 * 
	 * Set property for given "key", and "value"
	 * Property file is always updated when this method is called.
	 */
	public void setProperty(String pKey, String pValue) {

		this.mProperty.setProperty(pKey, pValue);
		save();
		
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		Enumeration<?> xKeyNames = this.mProperty.propertyNames();
		buf.append("Property file [").append(PROPERTY_FILE).append("]contains: \n");
		while(xKeyNames.hasMoreElements()) {
			String xKey = (String) xKeyNames.nextElement();
			buf.append(xKey).append("=").append(this.getProperty(xKey)).append("\n");
		}
		
		return buf.toString();
	}

}
