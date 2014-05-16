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

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ProxyUtil
 * 
 * @author hankido.lee
 * 
 */
public class ProxyUtil {
	
	private static Log log = LogFactory.getLog(ProxyUtil.class);

	private static ProxyUtil instance = null;

	public Proxy getProxy() {
		
		if(isValidProxyInfo() == false)
			return null;
		
		Proxy proxy = new Proxy(
				Proxy.Type.HTTP, 
				new InetSocketAddress(
						this.getProxyServerIP(), 
						this.getProxyServerPort()));
		
		log.debug("Current Proxy Server Info: <"+this.getProxyServerIP()+":"+this.getProxyServerPort()+">");
		
		return proxy;
	}

	protected boolean isValidProxyInfo() {
		
		String proxyServerIP = Property.getInstance().getProperty(Property.PROXY_SERVER_IP);
		
		if(proxyServerIP == null || 
				proxyServerIP.trim().length() == 0) {
		
			log.info("Proxy Server Info is not existed: no proxy server IP");
			return false;
		}
		
		
		String strProxyServerPort = Property.getInstance().getProperty(Property.PROXY_SERVER_PORT);
		if(strProxyServerPort == null || 
				strProxyServerPort.trim().length() == 0) {
			log.info("Proxy Server Info is not existed: no proxy server port");
			return false;
		}
		
		try {
			Integer.parseInt(strProxyServerPort);
		} catch(NumberFormatException e) {
			log.error("Malformed Proxy Server Info: <"+strProxyServerPort+">");
			log.debug(e.getMessage());
			return false;
		}
			
		return true;
	}

	private int getProxyServerPort() {
		
		int proxyServerPort=-1;
		
		String strProxyServerPort = Property.getInstance().getProperty(Property.PROXY_SERVER_PORT);
		if(strProxyServerPort == null)
			return -1;
		
		try {
			proxyServerPort = Integer.parseInt(strProxyServerPort);
		} catch(NumberFormatException e) {
			return -1;
		}
			
		return proxyServerPort;
		
	}

	private String getProxyServerIP() {
		
		String proxyServerIP = Property.getInstance().getProperty(Property.PROXY_SERVER_IP);
		
		if(proxyServerIP == null || 
				proxyServerIP.trim().length() == 0)
			return "";
		
		return proxyServerIP;
		
	}

	public static ProxyUtil getInstance() {
		if(instance == null) {
			synchronized(Property.class) {
				if(instance == null) {
					instance = new ProxyUtil();
				}
			}
		}
		return instance;
	}

	public void setEnabled(boolean enabled) {
		
		if(isValidProxyInfo() == false) {
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
			log.debug("Proxy disabled..");
			return;
		}
		
		if(enabled) {
			System.setProperty("http.proxyHost", Property.getInstance().getProperty(Property.PROXY_SERVER_IP));
			System.setProperty("http.proxyPort", Property.getInstance().getProperty(Property.PROXY_SERVER_PORT));
			log.debug("Proxy enabled..");
		} else {
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
			log.debug("Proxy disabled..");
		}
	}
	
}
