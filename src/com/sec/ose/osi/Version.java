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

/**
 * Version
 * @author suhyun47.kim, hankido.lee, sjh.yoo
 * 
 */
public class Version {

	private static final String APPLICATION_NAME_NICE = "OSIT";
	private static final String APPLICATION_NAME = "Open Source Inspect Tool";
	private static final String APPLICATION_VERSION = "0.8.1";
	
	private static final String PROTEX_VERSION_NUM = "6.3";
	private static final String PROTEX_VERSION = "Protex SDK " + PROTEX_VERSION_NUM;
	
	public static String getApplicationVersionInfo() {
		return "OSIT "+APPLICATION_VERSION+" - "+PROTEX_VERSION;
	}
	
	public static String getReleaseFolderName() {
		return "OSIT_"+APPLICATION_VERSION;
	}

	public static String getApplicationName() {
		return APPLICATION_NAME;
	}

	public static String getVersion() {
		return APPLICATION_VERSION;
	}

	public static String getApplicationNameNice() {
		return APPLICATION_NAME_NICE;
	}

	public static String getProtexSDKVersion() {
		return PROTEX_VERSION;
	}
	
}
