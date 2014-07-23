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
package com.sec.ose.osi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.thread.job.BackgroundJobManager;
import com.sec.ose.osi.ui.UISharedData;
import com.sec.ose.osi.ui.frm.login.JFrmLogin;
import com.sec.ose.osi.ui.frm.tray.JTrayIconApp;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.ProxyUtil;

/**
 * UIMain
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class UIMain {
	private static Log log = LogFactory.getLog(UIMain.class);
	
	private UIMain() {}
	
	public static void main(String[] args) {
		log.debug("Start GUI Application : " + Version.getApplicationVersionInfo());

		// check java version
		final double MINIMUM_VERSION = 1.6;			// SwingWorker is since 1.6
		
		log.debug("Checking Java version.");
		
		double cur_version = getJavaVersion();
		Properties prop = System.getProperties();
		String msg = "You need the latest JVM version to execute " + Version.getApplicationVersionInfo() + 
		"\nYou can download JVM(JRE or JDK) at http://java.sun.com" +
     	"\n -Minimun JVM to execute: Java " + MINIMUM_VERSION +
		"\n -Current version: Java " + prop.getProperty("java.version")+
		"\n -Local java home directory: " + prop.getProperty("java.home");

		if(cur_version < MINIMUM_VERSION) {
			JOptionPane.showMessageDialog(
					null, 
					msg,
					"Incompatible JVM",
					JOptionPane.ERROR_MESSAGE
			);
			
			log.debug(msg);
			System.exit(0);
		}
		
		// App Initialize
	    BackgroundJobManager.getInstance().startBeforeLoginTaskThread();
		
	    log.debug("Loading \"Login Frame\"");

		// login
		JFrame frmLogin = new JFrmLogin();
		UISharedData.getInstance().setCurrentFrame(frmLogin);
		frmLogin.setVisible(true);
		
		// check for only one OSI
		if(isRunning()) {
			JOptionPane.showMessageDialog(null, "OSI already has started. The program will be closed.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		
		// tray icon create
		new JTrayIconApp("OSIT", frmLogin);
	}

	private static double getJavaVersion() {
		
		Properties prop = System.getProperties();
		String java_specification_version = prop.getProperty("java.specification.version");
		
		StringTokenizer st = new StringTokenizer(java_specification_version, ".");
		
		String major = st.nextToken();
		String minor = st.nextToken();
		
		try {
			
			double version = Double.parseDouble(major+"."+minor);
			return version;
			
		} catch(NumberFormatException e) {
			log.warn(e);
			return 0;
		}
	}

	private static final String OSIT_JAR_FILE_NAME = "OSIT.jar";
	private static boolean isRunning() {
		log.debug("Running Test...");
		String execCMD = "";
		String protexStr = "";
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.indexOf("win") >= 0) {
			// windows
			execCMD = "TASKLIST /V /FO CSV /FI \"IMAGENAME eq javaw.exe\" /NH";
			protexStr = "osit";
		} else if(osName.indexOf("nix") >=0 || osName.indexOf("nux") >=0) {
			execCMD = "ps -ef";
			protexStr = "java -jar ./lib/"+OSIT_JAR_FILE_NAME;
		} else {
			JOptionPane.showMessageDialog(null, osName + " is not supported. The program will be closed.", "Error", JOptionPane.ERROR_MESSAGE);
//			System.exit(-1);
		}
		
		InputStreamReader isr = null;
		try {
			Process p = Runtime.getRuntime().exec(execCMD);
			isr = new InputStreamReader(p.getInputStream(),"UTF-8");
			BufferedReader reader = new BufferedReader(isr);
			
			String str = null;
			int count = 0;
			while((str = reader.readLine())!=null) {
				log.debug("### ONLY ONE OSI PROCESS CHECK : " + str.toLowerCase());
				if (str.toLowerCase().contains(protexStr.toLowerCase())) {
					if(++count > 1) {
						reader.close();
						return true;
					}
				}
			}
		} catch (IOException e1) {
			log.warn(e1.getMessage());
        } finally {
        	try {
				if(isr != null) { isr.close(); }
			} catch (Exception e) {
				log.debug(e);
			}
        }
		return false;
	}

}