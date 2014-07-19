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
package com.sec.ose.airs.controller.cli;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beust.jcommander.JCommander;
import com.sec.ose.airs.Properties;
import com.sec.ose.airs.service.AIRSService;
import com.sec.ose.airs.service.protex.AIRSProtexService;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class CLIMain {
	private static Log log = LogFactory.getLog(CLIMain.class);
	
	private static void setProxy(CLICommandLogin cm) {
		if (StringUtils.isNotEmpty(cm.getProxyHost())) {
			System.setProperty("http.proxyHost", cm.getProxyHost());
			if (StringUtils.isNotEmpty(cm.getProxyPort())) { 
				System.setProperty("http.proxyPort", cm.getProxyPort());
			}
		}
	}
	
	private static void login(AIRSService svc, CLICommandLogin cm) {
		setProxy(cm);
		
		try {
			svc.init(cm.getHostname(), cm.getUser(), cm.getPassword());
		} catch (Exception e) {
			log.error("Error when login");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		log.debug("Start Application");
		
		String programName = Properties.PROGRAM_NAME + " " + Properties.VERSION;
		
		CLICommandExport expArgs = new CLICommandExport();
		CLICommandAutoIdentify aiArgs = new CLICommandAutoIdentify();
		JCommander jc = new JCommander();
		jc.setProgramName(programName);
		
		jc.addCommand("export", expArgs);
		jc.addCommand("ai", aiArgs);
		jc.parse(args);
		
		String cmd = jc.getParsedCommand();
		
		//jc.usage();		
		
		AIRSService svc = new AIRSProtexService();
		if ("export".equalsIgnoreCase(cmd)) {
			login(svc, expArgs.getLogin());
			
			if (expArgs.getProjectId() != null) {
				svc.export(expArgs.getProjectId(), expArgs.getFilePath(), expArgs.getPackageName(), expArgs.getPackageFileName(), expArgs.getOrganizationName(), expArgs.getCreatorName(), expArgs.getCreatorEmail(), System.out);
			} else if (expArgs.getProjectName() != null) {
				svc.exportByProjectName(expArgs.getProjectName(), expArgs.getFilePath(), expArgs.getPackageName(), expArgs.getPackageFileName(), expArgs.getOrganizationName(), expArgs.getCreatorName(), expArgs.getCreatorEmail(), System.out);
			} else {
				exitWithErrorMessage("Project name or Project id necessary");
			}
			
		} else if ("ai".equalsIgnoreCase(cmd)) {

			login(svc, aiArgs.getLogin());
			
			if (aiArgs.getProjectId() != null) {
				svc.autoIdentify(aiArgs.getFileList(), aiArgs.getProjectId(), System.out);
			} else if (aiArgs.getProjectName() != null) {
				svc.autoIdentifyByProjectName(aiArgs.getFileList(), aiArgs.getProjectName(), System.out);
			} else {
				exitWithErrorMessage("Project name or Project id necessary");
			}
		}
	}
	
	public static void exitWithErrorMessage(String msg) {
		System.err.println(msg);
		System.exit(0);
	}
}
