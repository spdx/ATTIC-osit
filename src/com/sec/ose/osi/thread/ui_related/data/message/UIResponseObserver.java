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
package com.sec.ose.osi.thread.ui_related.data.message;

/**
 * UIResponseObserver
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public interface UIResponseObserver {
	public int getResult();
	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_FAIL = 0;

	public void setReturnValue(Object value);
	
	public Object getReturnValue();
	
	public void setResult(int pStatus);
	
	public void pushMessage(String pMessage);
	
	public void pushMessageWithHeader(String pMessage);
	
	public String popMessage();
	
	public boolean hasMoreMessage();
	
	public String getMessageHeader();
	
	public void setMessageHeader(String header);
	
	public void setFailMessage(String pMessage);
	
	public String getFailMessage();
	
	public void setSuccessMessage(String pMessage);
	
	public String getSuccessMessage();
	
}
