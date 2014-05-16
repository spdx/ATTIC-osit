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
package com.sec.ose.osi.ui.frm.main.identification.common;

import java.util.HashMap;

/**
 * IdentificationConstantValue
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class IdentificationConstantValue {
	
	public static final int STRING_MATCH_TYPE = 0x01;
	public static final int CODE_MATCH_TYPE = 0x02;
	public static final int PATTERN_MATCH_TYPE = 0x04;
	public static final int STANDARD_COMPONENT_TYPE = 0x08;
	public static final int FOLDER_TYPE = 0x10;
	
	public static final int RESET_TYPE = 0x20;
	public static final int IDENTIFY_TYPE = 0x21;
	
	public static HashMap<Integer, String> DBTABLE_NAME = new HashMap<Integer, String>();
	static {
		DBTABLE_NAME.put(STRING_MATCH_TYPE, "stringsearch");
		DBTABLE_NAME.put(CODE_MATCH_TYPE, "codematch");
		DBTABLE_NAME.put(PATTERN_MATCH_TYPE, "patternmatch");
	}

	public static final int BINDING_TYPE_STATIC = 0;
	public static final int BINDING_TYPE_DYNAMIC = 1;
	public static final int BINDING_TYPE_OBJECT = 2;
	public static final int BINDING_TYPE_EXECUTABLE = 3;
	public static final int BINDING_TYPE_TEXT = 4;
	public static final int BINDING_TYPE_COMPRESSION = 5;
	public static final int MOUSE_ONE_CLICK = 1;
	public static final int MOUSE_DOUBLE_CLICK = 2;
	public static final int PATTERN_MATCH_STATUS_NONE = 9999;
	public static final int INVALID = -999;
}
