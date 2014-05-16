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
package com.sec.ose.osi.util.tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tools
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class Tools {
	private static Log log = LogFactory.getLog(Tools.class);
	
	public static int transStringToInteger(String pNumStr) {
		if(pNumStr==null || pNumStr.length()==0) return 0;
		int tmpNum = 0;
		try {
			tmpNum = Integer.parseInt(pNumStr);
		} catch (Exception e) {
			try {
				tmpNum = Integer.parseInt(pNumStr.replaceAll(",", ""));
			} catch (Exception e2) {
				log.warn(e2.getMessage());
				tmpNum = 0;
			}
		}
		return tmpNum;
	}

	public static String getPrintStackTraceInfoString(Exception e) {
		String returnStackTraceString = e.toString();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(out);
		e.printStackTrace( printStream );
		returnStackTraceString = out.toString();
		return returnStackTraceString;
	 }

}