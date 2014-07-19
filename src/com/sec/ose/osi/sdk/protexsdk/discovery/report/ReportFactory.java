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
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.fault.ErrorCode;
import com.blackducksoftware.sdk.fault.SdkFault;
import com.blackducksoftware.sdk.protex.report.Report;
import com.blackducksoftware.sdk.protex.report.ReportFormat;
import com.blackducksoftware.sdk.protex.report.ReportSection;
import com.blackducksoftware.sdk.protex.report.ReportSectionType;
import com.blackducksoftware.sdk.protex.report.ReportTemplateRequest;
import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.data.match.CodeMatchInfo;
import com.sec.ose.osi.localdb.identification.IdentificationDBManager;
import com.sec.ose.osi.sdk.protexsdk.ProtexSDKAPIManager;
import com.sec.ose.osi.sdk.protexsdk.discovery.ReportAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.report.ReportInfo.ReportType;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;
import com.sec.ose.osi.util.tools.Tools;

/**
 * ReportFactory
 * @author sjh.yoo, hankido.lee
 * 
 */
public class ReportFactory {
	private static Log log = LogFactory.getLog(ReportFactory.class);
	
	public static ReportEntityList getReportEntityList(
			String pProjectName, 
			ReportInfo.ReportType mReport,
			UIResponseObserver observer) {
		
		log.debug("getReportEntityList: "+pProjectName);

		String ReportType = mReport.getType();
		ReportTemplateRequest reportTemplateRequest = createReportTemplateRequest(ReportType, mReport.getSectionType());
		BufferedReader reportBufferedReader = generateReportFromHTML(pProjectName, reportTemplateRequest, observer);
		
		if(reportBufferedReader == null) {
			log.debug("Fail to generate "+pProjectName + " "+ReportType+ " Report");
			observer.setFailMessage("Fail to generate "+pProjectName + " "+ReportType+ " Report");
			return null;
		}
		
		String msgHead  = " > Creating Report : "+pProjectName+"\n";
		String pMessage = " >> Generating ["+mReport.getType()+"] information from server.";
		pMessage = " >> Parsing ["+mReport.getType()+"] HTML file.";
		log.debug(pMessage);
		if(observer != null) {
			observer.pushMessageWithHeader(
					msgHead
					+pMessage);
		}
		
		ReportEntityList tmpReportEntityList = mReport.getCreator().createReportEntityList(reportBufferedReader);
		
		try {
			if(reportBufferedReader != null)
				reportBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		reportBufferedReader = null;
		System.gc();
		
		return tmpReportEntityList;
	}
	
	public static BufferedReader generateReportFromHTML(String pProjectName, ReportTemplateRequest request, UIResponseObserver observer) {
		Report report = null;
		try {
			log.debug("Project Name : "+pProjectName);
			String projectID = ProjectAPIWrapper.getProjectID(pProjectName);
			
			String msgHead  = " > Creating Report : "+pProjectName+"\n";
			String pMessage = " >> Generating ["+request.getTitle()+"] information from server.";
			log.debug(pMessage);
			if(observer != null) {
				observer.pushMessageWithHeader(
						msgHead
						+pMessage);
			}
			
			report = ProtexSDKAPIManager.getReportAPI().generateAdHocProjectReport(projectID, request, ReportFormat.HTML);
			log.debug(report.getFileName());
		} catch (SdkFault e) {
			ErrorCode errorCode = e.getFaultInfo().getErrorCode();
			log.error("generateReportFromHTML failed (error code) : " + errorCode);
			log.error(request.getTitle()+" failed : " + Tools.getPrintStackTraceInfoString(e));
			if(observer != null) {
				observer.setFailMessage(request.getTitle()+" failed.. ");
			}
			return null;
		} catch (Exception e) {
			log.error(request.getTitle()+" failed : " + Tools.getPrintStackTraceInfoString(e));
			if(observer != null) {
				observer.setFailMessage(request.getTitle()+" failed.. ");
			}
			String addMessage = "";
			if(request.getTitle().contains("Code Matches")) {
				addMessage = "\n\"Code Match\" information can't be trusted.";
			}
			JOptionPane.showMessageDialog(
					null, 
					"Can't get \""+request.getTitle()+"\" from Protex Server." +
					addMessage,
					"ERROR",
					JOptionPane.ERROR_MESSAGE
					); 
			return null;
		}
		
		BufferedReader htmlReportReader = null;
		if(report != null) {
			if(report.getFileContent() != null) {
				try {
					htmlReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(),"UTF-8"));
				} catch(IOException e) {
					log.warn(e.getMessage());
					if(observer != null) {
						observer.setFailMessage("FileIO failed");
					}
				}
			}
		}
		report = null;
		return htmlReportReader;
	}

	/*
	public static BufferedReader generateReportFromExcel(String pProjectName, ReportTemplateRequest request, UIResponseObserver observer) {
		Report report = null;
		try {
			log.debug("generateReportFromTemplate: "+pProjectName);

			String projectID = ProjectAPIWrapper.getProjectID(pProjectName);
			report = ProtexSDKAPIManager.getReportAPI().generateAdHocProjectReport(projectID, request, ReportFormat.XLS);
			log.debug(report.getFileName());
		} catch (SdkFault e) {
			ErrorCode errorCode = e.getFaultInfo().getErrorCode();
			log.error("generateReportFromTemplate failed (error code) : " + errorCode);
			log.error(request.getTitle()+" failed : " + Tools.getPrintStackTraceInfoString(e));
			if(observer != null) {
				observer.setFailMessage(request.getTitle()+" failed.. ");
			}
			return null;
		} catch (Exception e) {
			log.error(request.getTitle()+" failed : " + Tools.getPrintStackTraceInfoString(e));
			if(observer != null) {
				observer.setFailMessage(request.getTitle()+" failed.. ");
			}
			String addMessage = "";
			if(request.getTitle().contains("Code Matches")) {
				addMessage = "\n\"Code Match\" information can't be trusted.";
			}
			JOptionPane.showMessageDialog(
					null, 
					"Can't get \""+request.getTitle()+"\" from Protex Server." +
					addMessage,
					"ERROR",
					JOptionPane.ERROR_MESSAGE
					);
			return null;
		}
		
		BufferedReader XmlReportReader = null;
		if(report != null) {
			if(report.getFileContent() != null) {
				try {
					log.debug("set up xmlReportReader");
					XmlReportReader = new BufferedReader(new InputStreamReader(report.getFileContent().getInputStream(),"UTF-8"));
	
					// DO NOT REMOVE - move to data point
					while (!XmlReportReader.readLine().startsWith("<Row ss:Index=\"2\""));	// remove Top Disclaimer 
					while (!XmlReportReader.readLine().startsWith("<Row ss:Index=\"2\""));	// move to data point
				} catch(IOException e) {
					log.warn("[ERROR] XML fileIO failed: " + e.getMessage());
					if(observer != null) {
						observer.setFailMessage("XML fileIO failed");
					}
				}
			}
		}
		report = null;
		return XmlReportReader;
	}
	*/
	
	public static ReportTemplateRequest createReportTemplateRequest(String label,ReportSectionType rsType) {
		ReportTemplateRequest reportTemplate = new ReportTemplateRequest();

		reportTemplate.setTitle(label);
		reportTemplate.setForced(Boolean.TRUE);
		
		ReportSection section = new ReportSection();
		section.setLabel(label);
		section.setSectionType(rsType);
		reportTemplate.getSections().add(section);
		return reportTemplate;
	}

	public static void compositeCodematchTable(String projectName, UIResponseObserver observer) {
		ReportType reportType = new ReportInfo.CODE_MATCHES_PRECISION();
		ReportTemplateRequest reportTemplateRequest = createReportTemplateRequest(reportType.getType(), reportType.getSectionType());
		BufferedReader reportBufferedReader = generateReportFromHTML(projectName, reportTemplateRequest, observer);
		parser(projectName, reportBufferedReader, observer, reportType);
	}

	public static void parser(String projectName, BufferedReader htmlReportReader, UIResponseObserver observer, ReportType reportType) {
			String tmpLine = null;
			ArrayList<CodeMatchesPrecision> list = new ArrayList<CodeMatchesPrecision>();
			ArrayList<String> data = new ArrayList<String>();
			int insertedCnt=0;
			
			if(htmlReportReader == null) {
				log.debug("Fail to generate "+projectName + " "+reportType+ " Report");
				observer.setFailMessage("Fail to generate "+projectName + " "+reportType+ " Report");
				return;
			}
			
			String msgHead  = " > Creating Report : "+projectName+"\n";
			String pMessage = " >> Parsing ["+reportType.getType()+"] HTML file.";
			log.debug(pMessage);
			
			try {
				StringBuffer tmpValue = new StringBuffer("");
				while((tmpLine = htmlReportReader.readLine()) != null) {
					tmpLine = tmpLine.trim();
					if(tmpLine.startsWith("<table border='0' cellspacing='0' cellpadding='0' class='reportTable'")) {
						while((tmpLine = htmlReportReader.readLine()) != null) {
							tmpLine = tmpLine.trim();
							if(tmpLine.startsWith("</thead>")) {
								break;
							}
						}
						break;
					}
				}
				
				while((tmpLine = htmlReportReader.readLine()) != null) {
					tmpLine = tmpLine.trim();
					if(tmpLine.startsWith("<tr ")) {
						int index = 0;
						while((tmpLine = htmlReportReader.readLine()) != null) {
							tmpLine = tmpLine.trim();
							if(tmpLine.startsWith("<td ")) {
								while((tmpLine = htmlReportReader.readLine()) != null) {
									tmpLine = tmpLine.trim();
									if(tmpLine.startsWith("</td>")) {
										String removedTagValue = removeHtmlTag(tmpValue);
										data.add(removedTagValue);
										tmpValue.setLength(0);
										++index;
										break;
									}
									tmpValue.append(tmpLine);
								}
							}
							if(tmpLine.startsWith("</tr>")) {
								if(hasNoData(index)) {
									break;
								}
								CodeMatchesPrecision codeMatchesPrecision = new CodeMatchesPrecision();
								codeMatchesPrecision.setFile(data.get(0));
								codeMatchesPrecision.setSize(Tools.transStringToInteger(data.get(1)));
								codeMatchesPrecision.setFileLine(Tools.transStringToInteger(data.get(2)));
								codeMatchesPrecision.setTotalLines(Tools.transStringToInteger(data.get(3)));
								codeMatchesPrecision.setComponent(data.get(4));
								codeMatchesPrecision.setVersion(data.get(5));
								codeMatchesPrecision.setLicense(data.get(6));
								codeMatchesPrecision.setUsage(data.get(7));
								codeMatchesPrecision.setStatus(data.get(8));
								codeMatchesPrecision.setPercentage(data.get(9));
								codeMatchesPrecision.setMatchedFile(data.get(10));
								codeMatchesPrecision.setMatchedFileLine(Tools.transStringToInteger(data.get(11)));
								codeMatchesPrecision.setFileComment(data.get(12));
								codeMatchesPrecision.setComponentComment(data.get(13));
								list.add(codeMatchesPrecision);
								data.clear();
								
								insertedCnt++;
								if(insertedCnt % 10000 == 0) {
									log.debug("codeMatchesPrecision insertedCnt: "+insertedCnt);
									if(observer != null) {
										observer.pushMessageWithHeader(
												msgHead
												+pMessage
												+"\n >>> Inserted data count : "+insertedCnt);
									}
									insertCodematchTable(projectName, list);
									list.clear();
								}
								break;
							}
						}
					}
					if(tmpLine.startsWith("</table>")) {
						log.debug("codeMatchesPrecision insertedCnt: "+insertedCnt);
						insertCodematchTable(projectName, list);
						list.clear();
						break;
					}
				}
			} catch (IOException e) {
				ReportAPIWrapper.log.warn(e);
				String[] buttonOK = {"OK"};
				JOptionPane.showOptionDialog(null, "Out Of Memory Error", "Java heap space", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, buttonOK, "OK");
			}
			ReportAPIWrapper.log.debug("codeMatchesPrecision insertedCnt finally : "+insertedCnt);
		}


	public static String removeHtmlTag(StringBuffer tmpValue) {
		return tmpValue.toString().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	private static boolean hasNoData(int index) {
		final int CODEMATCH_PRECISION_KEYLIST_SIZE = 14;
		if(CODEMATCH_PRECISION_KEYLIST_SIZE <= index) {
			return false;
		}
		return true;
	}

	private static void insertCodematchTable(String projectName, ArrayList<CodeMatchesPrecision> list) {
		PreparedStatement prepCodematchTable = IdentificationDBManager.getCodeMatchPreparedStatement(projectName);
		String fileName = null;
		String component = null;
		String version = null;
		String license = null;
		int usage = 0;
		String status = null;
		String percentage = null;
		int percentageNumber = 0;
		String matchedFile = null;
		String fileComment = null;
		
		if(list == null){
	    	System.err.println("Not Founded CodeMatches PendingIdentification Precision.");
	    } else {
	        for(CodeMatchesPrecision tmpPendingFile:list) {
	        	fileName = tmpPendingFile.getFile();
	        	component = tmpPendingFile.getComponent();
	            version = tmpPendingFile.getVersion();
				if(version == null) version = "";
	            license = tmpPendingFile.getLicense();
	            usage = (tmpPendingFile.getUsage().equals("File"))?CodeMatchInfo.USAGE_FILE:CodeMatchInfo.USAGE_SNIPPET;
	            status = tmpPendingFile.getStatus();
	            percentage = tmpPendingFile.getPercentage();
	            percentageNumber = Tools.transStringToInteger(percentage.substring(0,percentage.length()-1));
	            matchedFile = tmpPendingFile.getMatchedFile();
	            fileComment = tmpPendingFile.getFileComment();
	            fileComment = fileComment.replace("<br />", "\n");
	            
	            try {
					prepCodematchTable.setString(1, fileName);
	                prepCodematchTable.setString(2, component);
	                prepCodematchTable.setString(3, version);
	                prepCodematchTable.setString(4, license);
	                prepCodematchTable.setString(5, String.valueOf(usage));
	                if(status.startsWith("Precision Match")) {
	                	prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_PENDING));
	                } else if(status.startsWith("Identified")) {
	            		prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_IDENTIFIED));
	                } else if(status.startsWith("Identified by Generic Version")) {
	                	prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_REJECT));
	                } else if(status.startsWith("Rejected")) {
	            		prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_REJECT));
	                } else if(status.startsWith("Declared")) {
	            		prepCodematchTable.setString(6, String.valueOf(AbstractMatchInfo.STATUS_DECLARED));
	                } 
	                prepCodematchTable.setInt(7, percentageNumber);
	                prepCodematchTable.setString(8, matchedFile);
	                prepCodematchTable.setString(9, fileComment);
	                prepCodematchTable.addBatch();
				} catch (SQLException e) {
					ReportAPIWrapper.log.warn(e);
				}
	        }
			IdentificationDBManager.execute(prepCodematchTable);
			if(prepCodematchTable != null) {
				try {
					prepCodematchTable.close();
				} catch (SQLException e) {
					ReportAPIWrapper.log.warn(e);
				}
			}
	    }
	}
}
