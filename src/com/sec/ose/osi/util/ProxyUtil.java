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
	
	public String getProxyHost() {
		String proxyHost = Property.getInstance().getProperty(Property.PROXY_HOST);

		if(proxyHost == null || proxyHost.trim().length() == 0) return "";
		return proxyHost;
	}

	public String getProxyPort() {
		String proxyPort = Property.getInstance().getProperty(Property.PROXY_PORT);

		if(proxyPort == null || proxyPort.trim().length() == 0) return "";
		return proxyPort;
	}

	public String getProxyBypass() {
		String proxyBypass = Property.getInstance().getProperty(Property.PROXY_BYPASS);

		if(proxyBypass == null || proxyBypass.trim().length() == 0) return "";
		return proxyBypass;
	}
	
	public void setProxyInfo(String mProxyHost, String mProxyPort, String mProxyBypass) {
		if(!getProxyHost().equals(mProxyHost)) Property.getInstance().setProperty(Property.PROXY_HOST, mProxyHost);
		if(!getProxyPort().equals(mProxyPort)) Property.getInstance().setProperty(Property.PROXY_PORT, mProxyPort);
		if(!getProxyBypass().equals(mProxyBypass)) Property.getInstance().setProperty(Property.PROXY_BYPASS, mProxyBypass);
		
		if(!isValidProxyInfo(mProxyHost, mProxyPort, mProxyBypass)) {
			System.setProperty("proxySet", "false");
			System.clearProperty("http.proxyHost");
			System.clearProperty("http.proxyPort");
			log.debug("Proxy disabled..");
		} else {
			System.setProperty("proxySet", "true");
			System.setProperty("http.proxyHost", mProxyHost);
			System.setProperty("http.proxyPort", mProxyPort);
			System.setProperty("http.nonProxyHosts", mProxyBypass);
			log.debug("Proxy enabled..[" + mProxyHost + ":"+mProxyPort+"]");
			if(mProxyBypass.length() > 0) log.debug("nonProxyHosts : [" + mProxyBypass +"]");
		}
		
	}

	private boolean isValidProxyInfo(String mProxyHost, String mProxyPort, String mProxyBypass) {
		
		if(mProxyHost == null || mProxyHost.trim().length() == 0) {
			log.info("Proxy Host is not existed: no proxy host");
			return false;
		}

		if(mProxyPort == null || mProxyPort.trim().length() == 0) {
			log.info("Proxy Server Info is not existed: no proxy port");
			return false;
		}
		
		try {
			Integer.parseInt(mProxyPort);
		} catch(NumberFormatException e) {
			log.error("Malformed Proxy Server Info: <"+mProxyPort+">");
			log.debug(e.getMessage());
			return false;
		}
			
		return true;
	}

}
