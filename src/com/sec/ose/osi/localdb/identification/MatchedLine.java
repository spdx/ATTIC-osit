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
package com.sec.ose.osi.localdb.identification;

import java.util.HashMap;
import java.util.Vector;

/**
 * MatchedLine
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class MatchedLine { 
	
	private HashMap<String,Vector<String>> MatchedLines = new HashMap<String,Vector<String>>(4);
		// key: string search rule
		// value: matched lines
	
	public MatchedLine() {
	}
	
	public Vector<String> getMatchedLinesBySMRule(String pSMRule) {
		return MatchedLines.get(pSMRule);
	}
	
	public void setMatchedLinesBySMRule(String key, Vector<String> value) {
		MatchedLines.put(key, value);
	}
	
	public HashMap<String,Vector<String>> getMatchedLines() {
		return MatchedLines;
	}
}
