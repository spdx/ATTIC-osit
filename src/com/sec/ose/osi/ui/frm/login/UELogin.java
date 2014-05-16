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
package com.sec.ose.osi.ui.frm.login;

import com.sec.ose.osi.ui.cache.UIEntity;

/**
 * UELogin
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class UELogin implements UIEntity {
	private static final long serialVersionUID = 1L;
	
	private boolean rememberID = false;
	
	private String mUserID = "";
	private transient String mPassword = "";
	private String mProtexServerIP = "";
	
	protected UELogin( 
			String userUD, 
			String password, 
			String protexServerIP,
			boolean rememberID) {
		
		super();
		this.rememberID = rememberID;
		this.mUserID = userUD;
		this.mPassword = password;
		this.mProtexServerIP = protexServerIP;
	}
	
	public String getUserID() {
		return mUserID;
	}

	public String getPassword() {
		return mPassword;
	}
	
	public boolean isRememberIDOnly() {
		return rememberID;
	}
	
	public String getProtexServerIP() {
		return mProtexServerIP;
	}
	
}
