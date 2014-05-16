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

/**
 * DateUtil
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class DateUtil {

	public static String getCurrentTime(String format) {
		return String.format(java.util.Locale.ENGLISH, format, System.currentTimeMillis());
	}

	public static String getFormatingTime(String format, long time) {
		return String.format(format,time);
	}

	public static String translateTimeFormatToColon(long sourceTime) {
		final int[] TIME_UNIT = {24*60*60*1000, 60*60*1000, 60*1000, 1000};
		final String[] TIME_UNIT_NAME = {"day ", ": ", ": ", ""};
		
		long targetTime = sourceTime;
		StringBuffer result = new StringBuffer();
		
		long tmpTime = 0;
		for(int i=0; i<TIME_UNIT.length;i++){
			tmpTime = targetTime/TIME_UNIT[i];
			if(i==0 && tmpTime==0) {
				continue;
			}
			String time = "";
			if(i>0 && tmpTime >= 0 && tmpTime <= 9) {
				time = "0" + String.valueOf(tmpTime);
			} else {
				time = String.valueOf(tmpTime);
			}
			result.append(time).append(TIME_UNIT_NAME[i]);

			targetTime %= TIME_UNIT[i];
		}
		return result.toString();
	}
}


