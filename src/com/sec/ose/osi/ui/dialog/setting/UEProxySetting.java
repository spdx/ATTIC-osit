/**
 * Copyright (c) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.dialog.setting;

import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.util.Property;

/**
 * UEProxySetting
 * 
 * @author hankido.lee
 *
 */
public class UEProxySetting implements UIEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String proxyServerIP;
	private String proxyServerPort;
	
	public UEProxySetting() {
		Property prop = Property.getInstance();
		this.proxyServerIP = prop.getProperty(Property.PROXY_SERVER_IP);
		this.proxyServerPort = prop.getProperty(Property.PROXY_SERVER_PORT);
	}
	
	public String getProxyServerIP() {
		return proxyServerIP;
	}
	
	public void setProxyServerIP(String proxyServerIP) {
		this.proxyServerIP = proxyServerIP;
	}
	
	public String getProxyServerPort() {
		return proxyServerPort;
	}
	
	public void setProxyServerPort(String proxyServerPort) {
		this.proxyServerPort = proxyServerPort;
	}
}
