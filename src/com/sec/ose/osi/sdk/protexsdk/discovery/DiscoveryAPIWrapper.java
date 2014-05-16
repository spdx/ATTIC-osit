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
package com.sec.ose.osi.sdk.protexsdk.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.project.codetree.discovery.IdentificationStatus;

/**
 * DiscoveryAPIWrapper
 * @author sjh.yoo
 * 
 */
public class DiscoveryAPIWrapper {
	private static Log log = LogFactory.getLog(DiscoveryAPIWrapper.class);
	
	// 1. Discovery API Wrapper
	public static String STATE_PENDING = "0";
	public static String STATE_IDENTIFIED = "1";
	public static String STATE_REJECT = "2";
	public static String STATE_PROCESSING = "3";
	public static String STATE_NEED_DEFINE = "9";
	
	public static String translateIdentficationStatus(IdentificationStatus tmp) {
		if(tmp == IdentificationStatus.PENDING_IDENTIFICATION) {
			return STATE_PENDING;
		} else if(tmp == IdentificationStatus.CODE_MATCH_IDENTIFIED_FILE || tmp == IdentificationStatus.CODE_MATCH_IDENTIFIED_FOLDER || tmp == IdentificationStatus.DECLARED || tmp == IdentificationStatus.CODE_MATCH_IDENTIFIED_FILE_GENERIC_VERSION) {
			return STATE_IDENTIFIED;
		} else if(tmp == IdentificationStatus.IDENTIFIED_SIDE_EFFECT) {
			return STATE_REJECT;
		} else {
			log.debug("STATE_NEED_DEFINE : " + tmp);
			return STATE_NEED_DEFINE;
		}
	}
}
