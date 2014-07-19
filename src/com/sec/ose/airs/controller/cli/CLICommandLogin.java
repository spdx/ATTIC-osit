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

import com.beust.jcommander.Parameter;

/**
 * 
 * 
 * @author ytaek.kim
 */
public class CLICommandLogin {
	@Parameter(names = "-u", required = true, description="protex user id")
	String user;
	@Parameter(names = "-p", required = true, description="protex user password")
	String password;
	@Parameter(names = "-h", required = true, description="protex user host name (ip or url)")
	String hostname;
	
	@Parameter(names = "--proxy-host", description="proxy host")
	String proxyHost;
	@Parameter(names = "--proxy-port", description="proxy port")
	String proxyPort;

	// Getter and Setter	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getProxyHost() {
		return proxyHost;
	}
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	public String getProxyPort() {
		return proxyPort;
	}
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
}
