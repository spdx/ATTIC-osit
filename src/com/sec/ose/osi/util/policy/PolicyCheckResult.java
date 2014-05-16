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
package com.sec.ose.osi.util.policy;

/**
 * PolicyCheckResult
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class PolicyCheckResult {

	public static final int UNKNOWN = 0;
	
	public static final int PROJECT_NAME_OK = 1;
	public static final int PROJECT_NAME_FAIL_WITH_SPECIAL_CHARACTER = 2;
	public static final int PROJECT_NAME_FAIL_WITH_YEAR = 3;
	public static final int PROJECT_NAME_FAIL_WITH_PREFIX = 4;

	private int result;
	private String resultMsg;

	public PolicyCheckResult() {
		super();
		
		this.result = UNKNOWN;
		this.resultMsg = "UNKNOWN";
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getResult() {
		return result;
	}

}
