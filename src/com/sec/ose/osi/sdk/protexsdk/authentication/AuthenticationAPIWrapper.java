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
package com.sec.ose.osi.sdk.protexsdk.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sec.ose.osi.data.LoginSessionEnt;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * AuthenticationAPIWrapper
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class AuthenticationAPIWrapper {
	private static Log log = LogFactory.getLog(AuthenticationAPIWrapper.class);
	
	private static boolean bLogin = false;
	
	private AuthenticationAPIWrapper(){}
	
	public static boolean login(
		String userID,
		String password, 
		String protexServerIP, 
		UIResponseObserver observer) {
		
		
		String xURL = "http://"+protexServerIP;

        
        log.debug("Try log in \"" + userID + "\" to \"" + xURL +"\"");
        
        bLogin = ProtexSDKAPIManager.connectToService(userID, password, xURL, observer);
        log.debug("Get response from project server");
        
    	if(bLogin == true){
        	LoginSessionEnt.getInstance().setUserID(userID);
        	LoginSessionEnt.getInstance().setPassword(password);
        	LoginSessionEnt.getInstance().setProtexServerIP(protexServerIP);
        	LoginSessionEnt.getInstance().setProtexServerUrl(xURL);
        	
        	return true;
    	} 
    	
    	LoginSessionEnt.getInstance().setUserID("Invalid");
    	LoginSessionEnt.getInstance().setPassword("Invalid");
    	LoginSessionEnt.getInstance().setProtexServerIP("Invalid");

    	return false;
	}
	
	public static boolean isLogin() {
		return bLogin;
	}
	
	public static void logout() {
		bLogin = false;
		LoginSessionEnt.getInstance().init();
	}
}
