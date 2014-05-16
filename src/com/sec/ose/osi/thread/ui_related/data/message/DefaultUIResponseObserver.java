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

import java.util.Iterator;
import java.util.TreeMap;

/**
 * DefaultUIResponseObserver
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class DefaultUIResponseObserver implements UIResponseObserver {

	private String mSuccessMessage = "";
	private String mFailMessage = "";
	private int    mResult;
	private String mMessageHeader = "";
	
	private Object returnValue;
	
	private TreeMap<Long, String>  	mTreeMap = new TreeMap<Long, String>();
	private long 					mFirstIdx;
	private long					mLastIdx;
	
	public DefaultUIResponseObserver() {
		clear();
	}
	
	public void setReturnValue(Object value) {
		this.returnValue = value;
	}
	
	public Object getReturnValue() {
		return this.returnValue;
	}
	
	private void clear() {
		mTreeMap.clear();
		mFirstIdx = 0;
		mLastIdx = 0;
		
		this.pushMessage("Executing...");
	}

	public String popMessage() {
		
		String message = mTreeMap.remove(this.mFirstIdx);
		this.mFirstIdx++;
		
		return message;
	}

	public int getSize() {
		return this.mTreeMap.size();
	}

	public boolean isEmpty() {

		if(this.mTreeMap.size() > 0)
			return false;
		
		return true;
	}

	public void pushMessage(String pMessage) {
		
		mTreeMap.put(this.mLastIdx, pMessage);
		this.mLastIdx++;
	}

	public void pushMessageWithHeader(String pMessage) {
		if(mTreeMap == null) return;
		
		mTreeMap.put(this.mLastIdx, mMessageHeader + pMessage);
		this.mLastIdx++;
	}
	
	public String toString() {
		Iterator<String> itr = this.mTreeMap.values().iterator();
		
		StringBuffer msg = new StringBuffer("");
		
		msg.append("result: " + this.mResult+"\n");
		msg.append("success: " + this.mSuccessMessage+"\n");
		msg.append("fail: " + this.mFailMessage + "\n");
		msg.append("message: ");
		
		if(itr == null) {
			msg.append("<NONE>");
			return msg.toString();
		}
		
		int cnt=0;
		while(itr.hasNext()) {
			msg.append((itr.next()+";"));
			cnt++;
		}

		if(msg.length() > 2)
			return msg.substring(0, msg.length()-1);
		else
			return "";
	}

	public Iterator<String> iterator() {
		
		return this.mTreeMap.values().iterator();
	}
	
	public String getFailMessage() {
		return mFailMessage;
	}

	public String getSuccessMessage() {
		return mSuccessMessage;
	}

	public void setFailMessage(String pMessage) {
		this.mFailMessage = pMessage;
		this.mResult = RESULT_FAIL;
	}

	public void setSuccessMessage(String pMessage) {
		this.mSuccessMessage = pMessage;
		this.mResult = RESULT_SUCCESS;
	}

	public int getResult() {
		return this.mResult;
		
	}

	public void setResult(int pResult) {
		if(pResult == RESULT_SUCCESS)
			this.mResult = pResult;
		else
			this.mResult = RESULT_FAIL;
	}

	public boolean hasMoreMessage() {
		
		if(this.mTreeMap.size() == 0)
			return false;
		else
			return true;
	}

	public String getMessageHeader() {
		if(this.mMessageHeader == null)
			return "";
		
		return this.mMessageHeader;
	}

	public void setMessageHeader(String header) {
		this.mMessageHeader = header;
		
	}
}
