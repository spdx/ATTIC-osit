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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * FormatUtil
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class FormatUtil {
	public static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat vdCoverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	public static final SimpleDateFormat mReportIdentifyFormat = new SimpleDateFormat("MMddHHmmss");
	public static final SimpleDateFormat mElapsedTimeFormat = new SimpleDateFormat("HH:mm:ss");
	public static final DecimalFormat mDecimalFormat = new DecimalFormat("##"); 
	public static final SimpleDateFormat mLicenseNoticeFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	public static String getTimeIdentifier() {
		return mReportIdentifyFormat.format(System.currentTimeMillis());
	}
	
	public static String getTimeForLicenseNotice() {
		return mLicenseNoticeFormat.format(System.currentTimeMillis());
	}

	private final static long MILISEC_TO_HOUR = 1000 * 60 * 60;
	private final static long MILISEC_TO_MIN = 1000 * 60;
	private final static long MILISEC_TO_SEC = 1000;
	
	public static String getElapsedTime(long timeMilliSec) {

		long sec = (timeMilliSec / MILISEC_TO_SEC) % 60;
		long min = (timeMilliSec / MILISEC_TO_MIN) % 60;
		long hour = timeMilliSec / MILISEC_TO_HOUR;

		String strHour = ( (hour>9)? ""+hour: "0"+hour); 
		String strMin= ( (min>9)? ""+min: "0"+min);
		String strSec = ( (sec>9)? ""+sec: "0"+sec);
		
		return strHour+":"+strMin+":"+strSec;
	}
	
	public static String getTimeStamp() {
		Date date = Calendar.getInstance().getTime();
		return mDateFormat.format(date)+" "+mElapsedTimeFormat.format(date);
	}
	
	public static String getTimeStampFormat(Date date) {
		return mDateFormat.format(date)+" "+mElapsedTimeFormat.format(date);
	}
	
	
}
