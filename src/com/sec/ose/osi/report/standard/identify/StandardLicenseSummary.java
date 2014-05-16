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
package com.sec.ose.osi.report.standard.identify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * StandardLicenseSummary
 * @author suhyun47.kim
 * 
 */
public class StandardLicenseSummary  {
	
	TreeMap<String, Integer> summary = new TreeMap<String, Integer>();
	
	public Iterator<String> getLicenseList() {
		return summary.keySet().iterator();
	}
	
	
	public Iterator<String> getLicenseListByFileCntAscendingOrder() {
		ArrayList<String> licenseList = new ArrayList<String>();
		Object[] original = summary.keySet().toArray();
		
		for(int i=0; i<original.length; i++) {
			for(int j=i+1; j<original.length; j++) {
				if(summary.get(original[i]) < summary.get(original[j]) ) {
					Object temp = original[i];
					original[i] = original[j];
					original[j] = temp;
				}
			}
		}
		
		for(int i=0; i<original.length; i++) {
			licenseList.add(""+original[i]);
		}
		
		return licenseList.iterator();
	}
	
	public Integer getFileCntForLicense(String licenseName) {
		return summary.get(licenseName);
	}
	
	public void addFileCntForLicense(String licenseName, int count) {
		
		
		if(summary.containsKey(licenseName) == false) {
			summary.put(licenseName, 0);
		}
		Integer curFileCnt = summary.get(licenseName);
		summary.put(licenseName, curFileCnt+count);
	}

	public int getTotalFileCnt() {
		
		int sum=0;
		
		Iterator<Integer> cnt = summary.values().iterator();
		while(cnt.hasNext()) {
			sum += cnt.next();
		}
		
		return sum;
	}
}