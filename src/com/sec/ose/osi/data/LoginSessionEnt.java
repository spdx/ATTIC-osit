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
package com.sec.ose.osi.data;

/**
 * LoginSessionEnt
 * @author sjh.yoo, suhyun47.kim, hankido.lee
 * 
 */
public class LoginSessionEnt{
	
	private volatile static LoginSessionEnt instance = null;
	
	private LoginSessionEnt(){}

	public static LoginSessionEnt getInstance() {
		if(instance == null) {
			synchronized(LoginSessionEnt.class) {
				if(instance == null) {
					instance = new LoginSessionEnt();
				}
			}
		}
		return instance;
	}
	
	private String userID = null;
	private String protexServerIP = null;
	private String protexServerUrl = null;
	private String password = null;
	private String protexServerIPOnly = null;
	private String organization = null;

	public void setProtexServerUrl(String protexServerUrl) {
		this.protexServerUrl = protexServerUrl;
	}
	
	public String getProtexServerUrl() {
		return protexServerUrl;
	}

	public String getProtexServerIP() {
		return protexServerIP;
	}

	public String getProtexServerIPOnly() {
		return protexServerIPOnly;
	}
	
	public void setProtexServerIP(String protexServerIP) {
		this.protexServerIP = protexServerIP;
		if(protexServerIP.indexOf(":") >= 0) {
			this.protexServerIPOnly = protexServerIP.substring(0,protexServerIP.indexOf(":"));
		} else {
			this.protexServerIPOnly = protexServerIP;
		}
		
	}
	public String getUserID() {
		return userID;
	}
	public  void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public void init() {
		userID = null;
		protexServerIP = null;
		password = null;
	}
		
}