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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Time
 * @author hankido.lee
 * 
 */
public class Time {
	
	private static Log log = LogFactory.getLog(Time.class);

	public static long startTime(String msg) {
		log.debug("## "+msg+" ## START time check");
		long startTime = System.currentTimeMillis();
		return startTime;
	}

	public static void endTime(String msg, long startTime) {
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		log.debug("## "+msg+" ## END time check : "+elapsedTime+"(ms)");
	}
	
}
