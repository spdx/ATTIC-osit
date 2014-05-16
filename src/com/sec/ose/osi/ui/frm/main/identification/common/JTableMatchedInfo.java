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

import javax.swing.JTable;

import com.sec.ose.osi.data.match.ISummaryInfo;

/**
 * JTableMatchedInfo
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public abstract class JTableMatchedInfo extends JTable {

	private static final long serialVersionUID = 1L;
	public static final int COL_SEARCH_SM = 0;
	public static final int COL_COMPONENT_SM = 1;
	public static final int COL_VERSION_SM = 2;
	public static final int COL_LICENSE_SM = 3;
	
	public static final int COL_COMPONENT_CM = 0;
	public static final int COL_VERSION_CM = 1;
	public static final int COL_LICENSE_CM = 2;
	
	public JTableMatchedInfo() {}
	
	public abstract void refresh(ISummaryInfo summaryInfo);
	
}