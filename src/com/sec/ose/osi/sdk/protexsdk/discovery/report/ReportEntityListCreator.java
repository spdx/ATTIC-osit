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
package com.sec.ose.osi.sdk.protexsdk.discovery.report;

import java.io.BufferedReader;

/**
 * ReportEntityListCreator
 * @author sjh.yoo, hankido.lee
 * 
 */
interface ReportEntityListCreator {
	
	static final String DATA_START_TAG = "Data ss:Type=\"String\">";
	static final String DATA_END_TAG = "</Data>";
	static final String DATA_END_TAG_WITH_NS = "</ss:Data>";
	static final String ROW_END_TAG = "</Row>";
	static final String TABLE_END_TAG = "</Table>";
	static final int DATA_START_TAG_LEN = DATA_START_TAG.length();
	
	static final String HTML_DATA_TABLE_START_TAG = "<table border='0' cellspacing='0' cellpadding='0' class='reportTable'";
	static final String HTML_DATA_TABLE_END_TAG = "</table>";
	static final String HTML_DATA_ROW_START_TAG = "<tr ";
	
	ReportEntityList createReportEntityList(BufferedReader XmlReportReader);
}

