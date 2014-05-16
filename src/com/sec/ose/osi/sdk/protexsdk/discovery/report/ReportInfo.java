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

import org.jdom.Namespace;

import com.blackducksoftware.sdk.protex.report.ReportSectionType;

/**
 * ReportInfo
 * @author sjh.yoo
 * 
 */
public class ReportInfo {
	public static final Namespace schemaNamespace = Namespace.getNamespace("ss", "urn:schemas-microsoft-com:office:spreadsheet");
	public interface ReportType {
		String getType();
		ReportSectionType getSectionType();
		ReportEntityListCreator getCreator();
	}
	
	static public class SUMMARY implements ReportType {
		public SUMMARY() {}
		public String getType() {return "Summary";}
		public ReportSectionType getSectionType() {return ReportSectionType.SUMMARY;}
		public ReportEntityListCreator getCreator() {return new SummaryCreator();}
		
		public static final String NAME = "Name:";
		public static final String PROJECT_CREATOR = "Project Creator:";
		public static final String LICENSE = "License:";
		public static final String DESCRIPTION = "Description:";
		public static final String NUMBER_OF_FILES = "Number of Files:";
		public static final String FILES_PENDING_IDENTIFICATION = "Files Pending Identification:";  // current pending count
		public static final String FILES_WITH_VIOLATIONS = "Files with Violations:";
		public static final String SERVER = "Server:";
	}

	static public class ANALYSIS_SUMMARY implements ReportType {
		public ANALYSIS_SUMMARY() {}
		public String getType() {return "Analysis Summary";}
		public ReportEntityListCreator getCreator() {return new SummaryCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.ANALYSIS_SUMMARY;}

		public static final String LAST_UPDATED = "Last Updated:";
		public static final String SCAN_STARTED = "Scan Started:";
		public static final String SCAN_FINISHED = "Scan Finished:";
		public static final String FILES_ANALYZED = "Files analyzed:";
		public static final String BYTES_ANALYZED = "Bytes analyzed:";
		public static final String FILES_SKIPPED = "Files skipped:";
		public static final String BYTES_SKIPPED = "Bytes skipped:";
		public static final String ANALYZED_RELEASE_DESCRIPTION = "Analysis Release Description:";
		public static final String ANALYZED_FROM_HOST = "Analyzed From Host:";
		public static final String ANALYZED_BY = "Analyzed By:";
		public static final String ANALYZED_WITH_OS = "Analyzed With OS:";
		public static final String ANALYZED_WITH_LOCALE= "Analyzed With Locale:";
	}

	static public class BILL_OF_MATERIALS implements ReportType {
		public BILL_OF_MATERIALS() {}
		public String getType() {return "Bill of Materials";}
		public ReportEntityListCreator getCreator() {return new DefaultEntityListCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.BILL_OF_MATERIALS;}
		
		public static final String APPROVAL_STATUS = "Approval Status";
		public static final String LICENSE_CONFLICT = "License Conflict";
		public static final String COMPONENT = "Component";
		public static final String VERSION = "Version";
		public static final String HOMEPAGE = "Home Page";
		public static final String COMMENT = "Component Comment";
		public static final String LICENSE = "License";
		public static final String SHIP_STATUS = "Ship Status";
		public static final String NUM_OF_CODE_MATCH = "# Manual Code Match";
		public static final String NUM_OF_DEPENDS = "# Manual Depends";
		public static final String NUM_OF_SERACH = "# Manual Search";
		public static final String USED_BY = "Used By";
	}

	static public class IDENTIFIED_FILES implements ReportType {
		public IDENTIFIED_FILES() {}
		public String getType() {return "Identified Files";}
		public ReportEntityListCreator getCreator() {return new DefaultEntityListCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.IDENTIFIED_FILES;}
		
		public static final String RESOLUTION_TYPE = "Resolution Type";
		public static final String DISCOVERY_TYPE = "Discovery Type";
		public static final String FILE_FOLDER_NAME = "File/Folder";
		public static final String SIZE = "Size";
		public static final String FILE_LINE = "File Line";
		public static final String TOTAL_LINES = "Total Lines";
		public static final String COMPONENT = "Component";
		public static final String VERSION = "Version";
		public static final String LICENSE = "License";
		public static final String USAGE = "Usage";
		public static final String PERCENTAGE = "%";
		public static final String MATCHED_FILE = "Matched File";
		public static final String MATCHED_FILE_LINE = "Matched File Line";
		public static final String COMMENT = "File/Folder Comment";
		public static final String SEARCH = "Search";
	}

	static public class COMPARE_CODE_MATCHES implements ReportType {
		public COMPARE_CODE_MATCHES() {}
		public String getType() {return "Compare Code Matches - Preci...";}
		public ReportEntityListCreator getCreator() {return new CompareCodeMatchesCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.COMPARE_CODE_MATCHES_PRECISION;}

		public static final String FULL_PATH = "Full Path";
		public static final String NAME = "Name";
		public static final String CODE_MATCH_COUNT = "Code Match Count";
		public static final String COMPARE_CODE_MATCHES_LINK = "Compare Code Matches Link";
	}

	static public class CODE_MATCHES_PRECISION implements ReportType {
		public CODE_MATCHES_PRECISION() {}
		public String getType() {return "Code Matches - Precision";}
		public ReportEntityListCreator getCreator() {return new DefaultEntityListCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.CODE_MATCHES_PRECISION;}

		public static final String FILE = "File";
		public static final String SIZE = "Size";
		public static final String FILE_LINE = "File Line";
		public static final String TOTAL_LINES = "Total Lines";
		public static final String COMPONENT = "Component";
		public static final String VERSION = "Version";
		public static final String LICENSE = "License";
		public static final String USAGE = "Usage";
		public static final String STATUS = "Status";
		public static final String PRECISION = "%";
		public static final String MATCHED_FILE = "Matched File";		
		public static final String MATCHED_FILE_LINE = "Matched File Line";
		public static final String FILE_COMMENT = "File Comment";
		public static final String COMPONENT_COMMENT = "Component Comment";	
	}

	static public class STRING_SEARCHES implements ReportType {
		public STRING_SEARCHES() {}
		public String getType() {return "Searches";}
		public ReportEntityListCreator getCreator() {return new DefaultEntityListCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.STRING_SEARCHES;}

		public static final String SEARCH = "Search";
		public static final String FILE = "File";
		public static final String SIZE = "Size";
		public static final String LINE = "Line";
		public static final String MATCH = "Match";		
	}
	
	static public class CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION implements ReportType {
		public CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION() {}
		public String getType() {return "Code Matches Pending Identif...";}
		public ReportEntityListCreator getCreator() {return new DefaultEntityListCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.CODE_MATCHES_PENDING_IDENTIFICATION_PRECISION;}

		public static final String FILE = "File";
		public static final String SIZE = "Size";
		public static final String FILE_LINE = "File Line";
		public static final String TOTAL_LINES = "Total Lines";
		public static final String COMPONENT = "Component";
		public static final String VERSION = "Version";
		public static final String LICENSE = "License";
		public static final String USAGE = "Usage";
		public static final String PRECISION = "%";
		public static final String MATCHED_FILE = "Matched File";		
		public static final String MATCHED_FILE_LINE = "Matched File Line";
		public static final String FILE_COMMENT = "File Comment";
		public static final String COMPONENT_COMMENT = "Component Comment";	
	}

	// Protex Report : File Name Patterns Flagged as Pending Id
	static public class PATTERN_MATCHES_PENDING_FILES implements ReportType {
		public PATTERN_MATCHES_PENDING_FILES() {}
		public String getType() {return "File Name Patterns Flagged a...";}
		public ReportEntityListCreator getCreator() {return new DefaultEntityListCreator();}
		public ReportSectionType getSectionType() {return ReportSectionType.FILE_DISCOVERY_PATTERN_MATCHES_PENDING_IDENTIFICATION;}

		public static final String FULL_PATH = "Full Path";
		public static final String TYPE = "Type";
		public static final String COMMENT = "Comment";
	}
}
