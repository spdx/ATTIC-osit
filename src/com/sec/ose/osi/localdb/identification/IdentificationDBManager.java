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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blackducksoftware.sdk.protex.component.version.ComponentVersion;
import com.sec.ose.osi.data.match.AbstractMatchInfo;
import com.sec.ose.osi.data.match.CodeMatchInfoForFolder;
import com.sec.ose.osi.data.match.FileSummary;
import com.sec.ose.osi.data.match.FolderSummary;
import com.sec.ose.osi.data.match.MultipleFileSummary;
import com.sec.ose.osi.data.match.StringMatchInfoForFolder;
import com.sec.ose.osi.sdk.protexsdk.component.ComponentAPIWrapper;
import com.sec.ose.osi.sdk.protexsdk.discovery.AbstractDiscoveryController;
import com.sec.ose.osi.sdk.protexsdk.project.ProjectAPIWrapper;
import com.sec.ose.osi.ui.frm.main.identification.common.IdentificationConstantValue;
import com.sec.ose.osi.util.Property;
import com.sec.ose.osi.util.tools.QueryUtil;
import com.sec.ose.osi.util.tools.Time;
import com.sec.ose.osi.util.tools.Tools;

/**
 * IdentificationDBManager - have to refactoring
 * @author sjh.yoo, suhyun47.kim, hankido.lee
 * 
 */
public class IdentificationDBManager {
	private static Log log = LogFactory.getLog(IdentificationDBManager.class);
	private static ArrayList<String> projectNameList = new ArrayList<String>();
	private static Connection conn = null;
	
	synchronized public static void addIdentifiedFile(String projectName, String pPath, String type, String component, String version, String license, String matchedFile) {
		component = QueryUtil.makeValid(component);
		license = QueryUtil.makeValid(license);
		PreparedStatement ps = null;
		try {
			String identified_file_table = "identified_file_"+getProjectTablePostfix(projectName);
			removeIdentifiedFile(projectName, pPath, type, matchedFile);
			ps = conn.prepareStatement("insert into "+identified_file_table
														+" values ('"+pPath+"', '"+type+"', '"+component+"', '"+version+"', '"+license+"', '"+matchedFile+"');");
			ps.executeUpdate();
		} catch (SQLException e) {
			log.warn(e);
		} finally {
	    	if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * use multi select
	 */
	synchronized public static void addIdentifiedFile(String projectName, String pPath, String type, String component, String version, String license, String originalComponent, String originalVersion) {
		String codematch_table = "codematch_"+getProjectTablePostfix(projectName);
		String identified_file_table = "identified_file_"+getProjectTablePostfix(projectName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String matchedFile = "";
		ArrayList<String> matchFileList = new ArrayList<String>();
		component = QueryUtil.makeValid(component);
		license = QueryUtil.makeValid(license);
		originalComponent = QueryUtil.makeValid(originalComponent);
		try {
			rs = selectSQL("select matched_file from "+codematch_table+" where path='"+pPath+"' and component='"+originalComponent+"' and version='"+originalVersion+"';");
			while(rs.next()) {
				matchFileList.add(rs.getString(1));
			}
		} catch(Exception e) {
			log.debug(e);
		} finally {
	    	if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		
		try {
			for(int i=0; i<matchFileList.size(); i++) {
				matchedFile = matchFileList.get(i);
				removeIdentifiedFile(projectName, pPath, type, matchedFile);
				ps = conn.prepareStatement("insert into "+identified_file_table
						+" values ('"+pPath+"', '"+type+"', '"+component
						+"', '"+version+"', '"+license+"', '"+matchedFile+"');");
				ps.executeUpdate();
				if(ps != null) { ps.close(); }
			}
		} catch (Exception e) {
			log.debug(e);
		} finally {
			if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static void autoDeclare(String pProjectName, String pPath, String pComponent) {
		PreparedStatement ps = null;
		String postfix = "";
		String stringsearch_table = "";
		String codematch_table = "";
		String patternmatch_table = "";
		pComponent = QueryUtil.makeValid(pComponent);

		try {
			postfix = getProjectTablePostfix(pProjectName);
			stringsearch_table = "stringsearch_"+postfix;
			codematch_table = "codematch_"+postfix;
			patternmatch_table = "patternmatch_"+postfix;
		} catch(Exception e) {
			log.debug(e);
		}
		
		try {
			ps = conn.prepareStatement(
					"update "+stringsearch_table
					+" set status='"+ AbstractMatchInfo.STATUS_IDENTIFIED 
					+"',component='"+pComponent
					+"' where path='"+pProjectName+"';");
			ps.executeUpdate();
		} catch(Exception e) {
			log.debug(e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		
		try {
			ps = conn.prepareStatement(
					"update "+codematch_table
					+" set status='"+ AbstractMatchInfo.STATUS_IDENTIFIED 
					+"',component='"+pComponent
					+"' where path='"+pProjectName+"';");
			ps.executeUpdate();
		} catch(Exception e) {
			log.debug(e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		
		try {
			ps = conn.prepareStatement(
					"update "+patternmatch_table
					+" set status='"+ AbstractMatchInfo.STATUS_IDENTIFIED 
					+"',component='"+pComponent
					+"' where path='"+pProjectName+"';");
			ps.executeUpdate();
		} catch(Exception e) {
			log.debug(e);
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static boolean connectToDB(){
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+Property.FILE_IDENTIFICATION_DB);
		} catch(Exception e) {
			log.warn(e);
			return false;
		}
		return true;
	}

	synchronized private static void createTable(String projectName) {
		log.debug("Create Table for "+projectName+" - "+new java.util.Date());
		projectNameList.remove(projectName);
		projectNameList.add(projectName);
		Statement stat = null;
		try {
			String postfix = getProjectTablePostfix(projectName);
			String identify_file_table = "identify_file_"+postfix;
			String identified_file_table = "identified_file_"+postfix;
			String codematch_table = "codematch_"+postfix;
			String stringsearch_table = "stringsearch_"+postfix;
			String patternmatch_table = "patternmatch_"+postfix;

			conn = DriverManager.getConnection("jdbc:sqlite:"+Property.FILE_IDENTIFICATION_DB);
			stat = conn.createStatement();

			if(stat != null) {
				// identificaion file
				stat.executeUpdate("CREATE TABLE "+identify_file_table+" (" +
						"path varchar(20) CONSTRAINT firstkey PRIMARY KEY," +
						"url varchar(30)," +
						"stringsearchlicense varchar(20));");
	
				// identified files
				stat.executeUpdate("CREATE TABLE "+identified_file_table+" (" +
						"path varchar(20)," +
						"type char(1)," +
						"component varchar(20)," +
						"version varchar(5)," +
						"license varchar(30)," +
						"matched_file varchar(20));");
				stat.executeUpdate("CREATE INDEX "+identified_file_table+"_index on "+identified_file_table+"(path,matched_file);");
				
				// string search
				stat.executeUpdate("CREATE TABLE "+stringsearch_table+" (" +
						"path varchar(20)," +
						"stringsearch varchar(20)," +
						"component varchar(20) DEFAULT null," +
						"version varchar(5) DEFAULT null," +
						"license varchar(30) DEFAULT null," +
						"status char(1)," +
						"matchedLine varchar(2)," +
						"comment varchar(100));");
				stat.executeUpdate("CREATE INDEX "+stringsearch_table+"_index on "+stringsearch_table+"(path);");
				stat.executeUpdate("CREATE INDEX "+stringsearch_table+"_component_version on "+stringsearch_table+"(component, version);");
				
				// code match
				stat.executeUpdate("CREATE TABLE "+codematch_table+" (" +
						"path varchar(20)," +
						"component varchar(20)," +
						"version varchar(5)," +
						"license varchar(30)," +
						"usage char(1)," +
						"status char(1)," +
						"percentage int," +
						"matched_file varchar(20)," +
						"comment varchar(100));");
				stat.executeUpdate("CREATE INDEX "+codematch_table+"_index on "+codematch_table+"(path, status);");
				stat.executeUpdate("CREATE INDEX "+codematch_table+"_component_version on "+codematch_table+"(component, version);");
				
				// pattern match
				stat.executeUpdate("CREATE TABLE "+patternmatch_table+" (" +
						"path varchar(20)," +
						"component varchar(20) DEFAULT null," +
						"version varchar(5) DEFAULT null," +
						"license varchar(30) DEFAULT null," +
						"status char(1)," +
						"comment varchar(100));");
				stat.executeUpdate("CREATE INDEX "+patternmatch_table+"_index on "+patternmatch_table+"(path);");
				stat.executeUpdate("CREATE INDEX "+patternmatch_table+"_component_version on "+patternmatch_table+"(component, version);");
			}
		} catch (SQLException e) {
			log.warn(e);
		} finally {
			if(stat != null){
				try {
					stat.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized private static void deleteTablesForProject(String projectName) {
		log.debug("Delete Table for "+projectName+" - "+new java.util.Date());
		projectNameList.remove(projectName);
		projectNameList.add(projectName);
		Statement stat = null;
		try {
			String postfix = getProjectTablePostfix(projectName);
			String identify_file_table = "identify_file_"+postfix;
			String identified_file_table = "identified_file_"+postfix;
			String codematch_table = "codematch_"+postfix;
			String stringsearch_table = "stringsearch_"+postfix;
			String patternmatch_table = "patternmatch_"+postfix;

			conn = DriverManager.getConnection("jdbc:sqlite:"+Property.FILE_IDENTIFICATION_DB);
			stat = conn.createStatement();
			
			if(stat != null) {
				// identificaion file
				stat.executeUpdate("delete from "+identify_file_table+";");
				
				// identified files
				stat.executeUpdate("delete from "+identified_file_table+";");
				
				// string search
				stat.executeUpdate("delete from "+stringsearch_table+";");
				
				// code match
				stat.executeUpdate("delete from "+codematch_table+";");
				
				// pattern match
				stat.executeUpdate("delete from "+patternmatch_table+";");
			}
			
		} catch (SQLException e) {
			log.warn(e);
		} finally {
			if(stat != null){
				try {
					stat.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static void dropTable(String projectName) {
		log.debug("Drop Table for "+projectName+" - "+new java.util.Date());
		projectNameList.remove(projectName);
		projectNameList.add(projectName);
		Statement stat = null;
		try {
			String postfix = getProjectTablePostfix(projectName);
			String identify_file_table = "identify_file_"+postfix;
			String identified_file_table = "identified_file_"+postfix;
			String codematch_table = "codematch_"+postfix;
			String stringsearch_table = "stringsearch_"+postfix;
			String patternmatch_table = "patternmatch_"+postfix;

			if(conn != null) conn.close();
			conn = DriverManager.getConnection("jdbc:sqlite:"+Property.FILE_IDENTIFICATION_DB);
			stat = conn.createStatement();
			
			if(stat == null)
				return;
		
			// identificaion file
			stat.executeUpdate("DROP TABLE IF EXISTS "+identify_file_table+";");
			stat.executeUpdate("DROP INDEX IF EXISTS "+identify_file_table+"_index;");

			// identified files
			stat.executeUpdate("DROP TABLE IF EXISTS "+identified_file_table+";");
			stat.executeUpdate("DROP INDEX IF EXISTS "+identified_file_table+"_index;");
			
			// string search
			stat.executeUpdate("DROP TABLE IF EXISTS "+stringsearch_table+";");
			stat.executeUpdate("DROP INDEX IF EXISTS "+stringsearch_table+"_index;");
			stat.executeUpdate("DROP INDEX IF EXISTS "+stringsearch_table+"_component_version;");

			// code match
			stat.executeUpdate("DROP TABLE IF EXISTS "+codematch_table+";");
			stat.executeUpdate("DROP INDEX IF EXISTS "+codematch_table+"_index;");
			stat.executeUpdate("DROP INDEX IF EXISTS "+codematch_table+"_component_version;");
			
			// pattern match
			stat.executeUpdate("DROP TABLE IF EXISTS "+patternmatch_table+";");
			stat.executeUpdate("DROP INDEX IF EXISTS "+patternmatch_table+"_index;");
			stat.executeUpdate("DROP INDEX IF EXISTS "+patternmatch_table+"_component_version;");

		} catch (SQLException e) {
			log.warn(e);
		} finally {
			if(stat != null){
				try {
					stat.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static void execute(PreparedStatement tmpPreparedStatement) {
		
		if(tmpPreparedStatement == null) {
			return;
		}
		
		log.debug(tmpPreparedStatement.toString());
		
		try {
			conn.setAutoCommit(false);
			tmpPreparedStatement.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			tmpPreparedStatement.clearBatch();
		} catch (SQLException e) {
			log.warn(e);
		}
	}

	synchronized public static void executeUpdateStatement(PreparedStatement tmpStatement) {
		
		if(tmpStatement == null) {
			return;
		}
		
		try {
			tmpStatement.executeBatch();
			tmpStatement.clearBatch();
		} catch (SQLException e) {
			log.warn(e);
		}
	}
	
	synchronized public static ArrayList<String> getCodeMatchFileList(String projectName) {
		ArrayList<String> fileList = new ArrayList<String>();
		return fileList;
	}

	synchronized public static ArrayList<String> getCodeMatchIdentifyTargetFile(String projectName, String pPath,String pComponent, String pVersion) {
		String table = "codematch_"+getProjectTablePostfix(projectName);
		ResultSet rs = null;
		ArrayList<String> identifyList = new ArrayList<String>();
		try {
			if(pPath.equals("/")) {
				rs = selectSQL("select path from "+table+" where component='"+pComponent+"' and version='"+pVersion+"';");
			} else {
				rs = selectSQL("select path from "+table+" where path like '"+pPath+"%' and component='"+pComponent+"' and version='"+pVersion+"';");
			}
			while(rs.next()) {
				String target = rs.getString(1);
				identifyList.add(target);
			}
			rs.close();
			return identifyList;
		} catch (Exception e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static PreparedStatement getCodeMatchPreparedStatement(String projectName) {
		String codematch_table = "codematch_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("insert into "+codematch_table+" values (?, ?, ?, ?, ?, ?, ?, ?, ?);");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}

	synchronized public static PreparedStatement getCodeMatchUpdatePreparedStatement(String projectName) {
		String codematch_table = "codematch_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("update "+codematch_table+" set status=? where path=? and component=? and version=?;");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}

	synchronized public static String getComment(String projectName, int type, String path) {
		String condition = "";
		String retStr = "";
		if(type==IdentificationConstantValue.STRING_MATCH_TYPE) {
			condition = "stringsearch";
		} else if(type==IdentificationConstantValue.CODE_MATCH_TYPE) {
			condition = "codematch";
		} else if(type==IdentificationConstantValue.PATTERN_MATCH_TYPE) {
			condition = "patternmatch";
		}
		
		String sql = "select comment from "+condition+"_"+getProjectTablePostfix(projectName)
					+" where path='"+path+"';"; 
		ResultSet rs = selectSQL(sql);
		try {
			while(rs.next()) {
				retStr = rs.getString(1);
				break;
			}
		} catch (SQLException e) {
			log.debug(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		return retStr;
		
	}

	synchronized public static String getComponentNameForPatternMatchFile(String projectName, String tmpSelectedPath) {
		String postfix = IdentificationDBManager.getProjectTablePostfix(projectName);
		String table = "patternmatch_"+postfix;
		String componentName = "";
		ResultSet rs = null;
		String strSql = "select component from " + table + " where path='" + tmpSelectedPath + "';";
		try {
			rs = selectSQL(strSql);
			if(rs.next()) {
				componentName  = rs.getString(1);
			} else {
				componentName = "";
			}
			rs.close();
			log.debug("[IdentificationDBManager.getComponentNameForPatternMatchFile()] componentName : " + componentName);
		} catch (SQLException e) {
			log.debug(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		return componentName;
	}

	synchronized public static String getComponentNameForPatternMatchResetFolder(String projectName, String pPath) {
		String table = "patternmatch_"+getProjectTablePostfix(projectName);
		String targetComponentName = "";
		ResultSet rs = null;
		try {
			rs = selectSQL(
					"select component from "+table
					+" where path like '"+pPath+"%' and status='"+ AbstractMatchInfo.STATUS_IDENTIFIED +"';");
			if(rs.next()) {
				targetComponentName = rs.getString(1);
			} else {
				targetComponentName = "";
			}
//			if(rs!=null) rs.close();
			log.debug("folder reset targetComponentName : " + targetComponentName);
		} catch (SQLException e) {
			log.debug(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		return targetComponentName;
	}

	synchronized public static PreparedStatement getIdentificationFilePreparedStatement(String projectName) {
		String identify_file_table = "identify_file_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("insert into "+identify_file_table+" values (?, ?, ?);");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}

	synchronized public static PreparedStatement getPreparedStatementForSettingIdentifiedFiles(String projectName) {
		String identified_file_table = "identified_file_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("insert into "+identified_file_table+" values (?, ?, ?, ?, ?, ?);");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}

	synchronized public static ArrayList<String> getPendingFileList(String projctName, int type) {
		
		String tableName = IdentificationConstantValue.DBTABLE_NAME.get(type);
		String strSql = "select distinct(path) from "
			+tableName+"_"+getProjectTablePostfix(projctName)
			+ " where status="+ AbstractMatchInfo.STATUS_PENDING +";";
		log.debug( "getIdentifyFileList sql : "+strSql);
		ResultSet rs = null; 
		
		ArrayList<String> cacheIdentificationArrayList = new ArrayList<String>();
//		ResultSet rs = null;
		try {
			rs = selectSQL(strSql);
			while(rs.next()) {
				cacheIdentificationArrayList.add(rs.getString(1));
			}
			log.debug("## cacheIdentificationArrayList Size : " + cacheIdentificationArrayList.size());
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return cacheIdentificationArrayList;
	}

	synchronized public static ArrayList<String> getIdentifiedFileList(String projectName, int type) {
		
		String tableName = IdentificationConstantValue.DBTABLE_NAME.get(type);
		String strSql = "select distinct(path) from "
			+tableName+"_"+getProjectTablePostfix(projectName)
			+ " where status="+ AbstractMatchInfo.STATUS_IDENTIFIED +";";
		log.debug( "getIdentifiedFileList sql : "+strSql);
		ResultSet rs = null; 
		
		ArrayList<String> cacheIdentifiedFileArrayList = new ArrayList<String>();
		try {
			rs = selectSQL(strSql);
			while(rs.next()) {
				cacheIdentifiedFileArrayList.add(rs.getString(1));
			}
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return cacheIdentifiedFileArrayList;
	}
	
	synchronized public static String getLicenseForPatternMatch(String projectName, String tmpSelectedPath) {
		String postfix = IdentificationDBManager.getProjectTablePostfix(projectName);
		String table = "patternmatch_"+postfix;
		String licenseName = "";
		ResultSet rs = null;
		try {
			rs = selectSQL("select license from " + table + " where path='" + tmpSelectedPath + "';");
			if(rs.next()) {
				licenseName  = rs.getString(1);
			} else {
				licenseName = "";
			}
			
		} catch (SQLException e) {
			log.debug(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		return licenseName;
	}
	
	synchronized public static PreparedStatement getPatternMatchPreparedStatement(String projectName) {
		String patternmatch_table = "patternmatch_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("insert into "+patternmatch_table+" values (?, ?, ?, ?, ?, ?);");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}

	synchronized public static String getProjectTablePostfix(String projectName) {
		if(ProjectAPIWrapper.getProjectID(projectName) == null) return "_NOT_EXISTED_PROJECT_NAME_";
		return ProjectAPIWrapper.getProjectID(projectName).replace("-","_");
		//return projectName.replace(".", "_").replace(" ","_");
	}
	
	synchronized public static Statement getStatement() {
		try {
			return conn.createStatement();
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}
	
	synchronized public static int getStatusForPatternMatch(String projectName, String tmpSelectedPath) {
		String postfix = IdentificationDBManager.getProjectTablePostfix(projectName);
		String table = "patternmatch_"+postfix;
		
		ResultSet rs = selectSQL("select status from " + table + " where path='" + tmpSelectedPath + "';");
		int status = AbstractMatchInfo.STATUS_UNKNOWN;
		try {
			if(rs.next()) {
				status = Tools.transStringToInteger(rs.getString(1));
			}
		} catch (SQLException e) {
			log.debug(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		
		return status;
	}
	
	synchronized public static void updateStringSearchInfoForSingleFile(String projectName, String pPath, FileSummary pFileSummary) {
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		StringBuffer sqlString = new StringBuffer();
		
		sqlString.append("select stringsearch,component,version,license,count(CASE status WHEN '");
		sqlString.append(AbstractMatchInfo.STATUS_PENDING);
		sqlString.append("' THEN ");
		sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED); 
		sqlString.append(" END) as pending, count(CASE status WHEN '");
		sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED); 
		sqlString.append("' THEN ");
		sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED);
		sqlString.append(" END) as identified, status, comment from ");
		sqlString.append(table);
		sqlString.append(" where path='");
		sqlString.append(pPath);
		sqlString.append("' GROUP BY stringsearch,component,version");
		
		log.debug("updateStringSearchInfo : "+sqlString.toString());
		ResultSet rs = null;
		
		try {
			rs = selectSQL(sqlString.toString());
			while(rs.next()) {
				pFileSummary.addStringSearchInfoList(projectName, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8));
			}
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.debug(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static void updateStringSearchInfoForMultipleFile(
			String pProjectName, 
			ArrayList<String> pPath,
			MultipleFileSummary pMultipleFileSummary) {
		
		for(String path : pPath) {
			String table = "stringsearch_"+getProjectTablePostfix(pProjectName);
			StringBuffer sqlString = new StringBuffer();
			
			sqlString.append("select stringsearch,component,version,license,count(CASE status WHEN '");
			sqlString.append(AbstractMatchInfo.STATUS_PENDING);
			sqlString.append("' THEN ");
			sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED); 
			sqlString.append(" END) as pending, count(CASE status WHEN '");
			sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED); 
			sqlString.append("' THEN ");
			sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED);
			sqlString.append(" END) as identified, status, comment from ");
			sqlString.append(table);
			sqlString.append(" where path='");
			sqlString.append(path);
			sqlString.append("' GROUP BY stringsearch,component,version");
			
			log.debug("updateStringSearchInfo : "+sqlString.toString());
			ResultSet rs = null;
			
			try {
				rs = selectSQL(sqlString.toString());
				while(rs.next()) {
					pMultipleFileSummary.addStringSearchInfoList(pProjectName, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getString(8));
				}
	//			if(rs!=null) rs.close();
			} catch (Exception e) {
				log.debug(e);
			} finally {
				if(rs != null){
					try {
						rs.close();
					} catch (SQLException e) {
						log.debug(e);
					}
				}
			}
		}
	}

	synchronized public static void updateStringSearchForMultiFileFolderView(			
			String pProjectName, 
			ArrayList<String> pPaths,
			MultipleFileSummary pMultipleFileSummary) {
		
		String table = "stringsearch_"+getProjectTablePostfix(pProjectName);
		StringBuffer sqlString = new StringBuffer();
		HashMap<String, StringMatchInfoForFolder> stringMatchMap = new HashMap<String, StringMatchInfoForFolder>();
		for(String path : pPaths) {
			sqlString.setLength(0);
			sqlString.append("select path, stringsearch, component, version, license, status, matchedLine, comment, count(CASE status WHEN '");
			sqlString.append(AbstractMatchInfo.STATUS_PENDING);
			sqlString.append("' THEN ");
			sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED); 
			sqlString.append(" END) as pending, count(CASE status WHEN '");
			sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED); 
			sqlString.append("' THEN ");
			sqlString.append(AbstractMatchInfo.STATUS_IDENTIFIED);
			sqlString.append(" END) as identified from ");
			sqlString.append(table);
			sqlString.append(" where path='");
			sqlString.append(path);
			sqlString.append("' GROUP BY stringsearch,component,version");
			
			log.debug("updateStringSearchInfoForMultipleFileFolderView : "+sqlString.toString());
			ResultSet rs = null;
			try {
				rs = selectSQL(sqlString.toString());
				while(rs.next()) {
					StringMatchInfoForFolder stringMatchFolder = new StringMatchInfoForFolder(
							rs.getString(TableStringMatch.PATH),
							rs.getString(TableStringMatch.STRINGSEARCH), 
							rs.getString(TableStringMatch.COMPONENT), 
							rs.getString(TableStringMatch.VERSION), 
							rs.getString(TableStringMatch.LICENSE), 
							rs.getInt(TableStringMatch.PENDING_HITS), 
							rs.getInt(TableStringMatch.IDENTIFIED_HITS));
					
					String key = stringMatchFolder.getStringSearchName() +"_" + 
								 stringMatchFolder.getComponentName() +"_" + 
								 stringMatchFolder.getVersionName();
					
					if(stringMatchMap.containsKey(key)) {
						StringMatchInfoForFolder stringMatchFolderBase = stringMatchMap.get(key);
						stringMatchFolderBase.increaseFileCount();
						stringMatchFolderBase.plusPendingHits(stringMatchFolder.getPendingHits());
						stringMatchFolderBase.plusIdentifiedHits(stringMatchFolder.getIdentifiedHits());
					} else {
						stringMatchMap.put(key, stringMatchFolder);
					}
				}
	//			if(rs!=null) rs.close();
			} catch (Exception e) {
				log.debug(e);
			} finally {
				if(rs != null){
					try {
						rs.close();
					} catch (SQLException e) {
						log.debug(e);
					}
				}
			}
		}
		
		Iterator<Map.Entry<String, StringMatchInfoForFolder>> iter = stringMatchMap.entrySet().iterator(); 
		if(iter == null) {
			return;
		}
		
		while(iter.hasNext()) {
			String key = iter.next().getKey();			
			StringMatchInfoForFolder stringMatchFolder = stringMatchMap.get(key);
			pMultipleFileSummary.addStringMatchInforForMultipleFileSummary(
					stringMatchFolder.getStringSearchName(), 
					stringMatchFolder.getComponentName(), 
					stringMatchFolder.getVersionName(), 
					stringMatchFolder.getLicenseName(), 
					stringMatchFolder.getFileCount(), 
					stringMatchFolder.getPendingHits(), 
					stringMatchFolder.getIdentifiedHits());
		}
	}
	
	synchronized public static void updateStringSearchFolderSummary(String projectName, String pPath, FolderSummary folderSummary) {
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		
		String sql = "SELECT stringsearch,component,version,license,count(distinct(path)),count(CASE status WHEN '"+ AbstractMatchInfo.STATUS_PENDING 
					+"' THEN "+ AbstractMatchInfo.STATUS_IDENTIFIED +" END) as pending, count(CASE status WHEN '"+ AbstractMatchInfo.STATUS_IDENTIFIED 
					+"' THEN "+ AbstractMatchInfo.STATUS_IDENTIFIED +" END) as identified from "+table
					+" where path like '"+pPath+"%' GROUP BY stringsearch,component,version;";
		
		log.debug("StringSearchFolderView sql : " + sql);
		ResultSet rs1 = null;
		try {
			rs1 = selectSQL(sql);
			while(rs1.next()) {
				folderSummary.addStringMatchInforForFolderList(rs1.getString(1), rs1.getString(2), rs1.getString(3), rs1.getString(4), rs1.getInt(5), rs1.getInt(6), rs1.getInt(7));
			}
			rs1.close();
		} catch (Exception e) {
			log.debug(e);
		} finally {
			if(rs1 != null){
				try {
					rs1.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static HashSet<String> getStringSearchIdentifyTargetFile(String projectName, String pPath,String pStringSearch) {
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		String sql = "";
		HashSet<String> hs = new HashSet<String>();
		ResultSet rs = null;
		try {
			if(pPath.equals("/")) {
				sql = "select path from "+table+" where status='"+ AbstractMatchInfo.STATUS_PENDING +"';";
				log.debug("getStringSearchIdentifyTargetFile ~~~ " + sql);
				rs = selectSQL(sql);
			} else {
				sql = "select path from "+table+" where path like '"+pPath+"%' and status='"+ AbstractMatchInfo.STATUS_PENDING +"';";
				log.debug("getStringSearchIdentifyTargetFile ~~~ " + sql);
				rs = selectSQL(sql);
			}
			while(rs.next()) {
				hs.add(rs.getString(1));
			}
			return hs;
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static String getStringSearchLicense(String projectName, String pPath) {
		String identify_file_table = "identify_file_"+getProjectTablePostfix(projectName);
		ResultSet rs = null;
		try {
			rs = selectSQL("select stringsearchlicense from "+identify_file_table+" where path='"+pPath+"';");
			while(rs.next()) {
				return rs.getString(1);
			}
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		return "";
	}
	
	synchronized public static MatchedLine getStringSearchMatchedLines(String projectName, String filePath) {
		MatchedLine oMatchedLine = new MatchedLine();
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		String sql = "SELECT stringsearch,matchedLine FROM "+table+" where path='"+filePath+"';";
		log.debug("getStringSearchMatchedLines ~~ :: " + sql);
		ResultSet rs = selectSQL(sql);
		
		try {
			while(rs.next()) {
				
				String stringSearchName = rs.getString(1);
				String matchedLine = rs.getString(2);
				
				Vector<String> tmpMatchedLines = oMatchedLine.getMatchedLinesBySMRule(stringSearchName);
				
				if(tmpMatchedLines == null) {
					tmpMatchedLines = new Vector<String>();
					oMatchedLine.setMatchedLinesBySMRule(stringSearchName, tmpMatchedLines);
				}
				tmpMatchedLines.add(matchedLine);
			}
			return oMatchedLine;
		} catch (SQLException e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		
	}
	
	synchronized public static PreparedStatement getStringSearchPreparedStatement(String projectName) {
		String stringsearch_table = "stringsearch_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("insert into "+stringsearch_table+" values (?, ?, ?, ?, ?, ?, ?, ?);");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}
	
	synchronized public static HashSet<String> getStringSearchResetTargetFile(String projectName, String pPath,String pStringSearch) {
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		ResultSet rs = null;
		HashSet<String> hs = new HashSet<String>();
		try {
			if(pPath.equals("/")) {
				rs = selectSQL("select path from "+table+" where stringsearch='"+pStringSearch+"' and status='"+ AbstractMatchInfo.STATUS_IDENTIFIED +"';");
			} else {
				rs = selectSQL("select path from "+table+" where path like '"+pPath+"%' and stringsearch='"+pStringSearch+"' and status='"+ AbstractMatchInfo.STATUS_IDENTIFIED +"';");
			}
			
			while(rs.next()) {
				hs.add(rs.getString(1));
			}
			return hs;
		} catch (Exception e) {
			log.debug(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static String getURLForIdentifyFile(String projectName, String pPath) {
		String identify_file_table = "identify_file_"+getProjectTablePostfix(projectName);
		ResultSet rs = null;
		try {
			rs = selectSQL("select url from "+identify_file_table+" where path='"+pPath+"';");
			while(rs.next()) {
				return rs.getString(1);
			}
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
		return "";
	}
	
	synchronized public static void init() {
		for(String projectName:projectNameList) {
			log.debug(projectName);
			init(projectName);
		}
	}
	
	synchronized public static void init(String projectName) {
		log.debug("### Start Table Init");
		if(isExistedProject(projectName)) {
			deleteTablesForProject(projectName);
		} else {
			createTable(projectName);
		}
		log.debug("### End Table Init");
	}

	synchronized public static boolean isExistedProject(String projectName) {
		String postfix = getProjectTablePostfix(projectName);
		String identify_file_table_name = "identify_file_"+postfix;
		log.debug("### Check Table : "+identify_file_table_name);
		
		String showQuery = "SELECT name FROM sqlite_master " 
						+"WHERE type IN ('table', 'view') AND name NOT LIKE 'sqlite_%' AND name = '"+identify_file_table_name+"' " 
						+"UNION ALL SELECT name FROM sqlite_temp_master " 
						+"WHERE type IN ('table', 'view') ORDER BY 1;";
		
		log.debug(showQuery);
		
		ResultSet rs = null;
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(showQuery);
			rs = statement.executeQuery();
			while(rs.next()) {
				String name = rs.getString(1);
				log.debug("name: "+name+" / "+identify_file_table_name);
				if(identify_file_table_name.equals(name)) {
					return true;
				}
			}
		} catch (Exception e) {
			log.warn(e);
		} finally {
			if(statement != null){
				try {
					statement.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return false;
	}
	
	synchronized public static void removeIdentifiedFile(String projectName, String pPath, String type, String matchedFile) {
		Statement stat = null;
		try {
			String identified_file_table = "identified_file_"+getProjectTablePostfix(projectName);
			stat = conn.createStatement();
			
			if(stat != null) {
				stat.executeUpdate("delete from "+identified_file_table+" where path='"+pPath+"' and type='"+type+"' and matched_file='"+matchedFile+"';");
			}
			
		} catch (SQLException e) {
			log.warn(e);
		} finally {
			if(stat != null){
				try {
					stat.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized public static ResultSet selectSQL(String sql){
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			return rs;
		} catch(Exception e) {
			log.warn(e);
		}
		return null;
	}
	
	synchronized public static void setStringSearchLicense(String pProjectName, String pPath, String pLicense) {
		pLicense = QueryUtil.makeValid(pLicense);
		PreparedStatement ps = null;
		try {
			String table = "identify_file_"+getProjectTablePostfix(pProjectName);
			ps = conn.prepareStatement(
					"update "+table
					+" set stringsearchlicense='"+pLicense
					+"' where path='"+pPath+"';");
			ps.executeUpdate();
		} catch (SQLException e) {
			log.warn(e);
		} finally {
	    	if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	synchronized public static void update(String updateSQL) {
		
		log.debug("UPDATE SQL:"+updateSQL);
		
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			
			if(stmt != null) {
				stmt.executeUpdate(updateSQL);
			}
			
		} catch (SQLException e) {
			log.warn(e);
		} finally {
			if(stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static void updateCodeMatchInfoForSingleFile(String projectName, FileSummary pFileSummary) {
		String path = pFileSummary.getFullPath();
		HashMap<String, TableIdentifiedFile> identifiedFileMap = new HashMap<String, TableIdentifiedFile>();
			// key: path + matchedFile
		HashMap<String, CodeMatchInfoForFolder> commonSnippets = null;
			// key: componentName + version + licenseName + status
		HashMap<String, CodeMatchInfoForFolder> snippetsForPath = null;
			// key: componentName + version + licenseName + status
		try {
			String queryTableIdentifiedFile = TableIdentifiedFile.getQueryForIdentifiedFileListByFile(projectName, path);
			ResultSet rsForIdentifiedCodeMatchList = selectSQL(queryTableIdentifiedFile);
			while(rsForIdentifiedCodeMatchList.next()) {
				TableIdentifiedFile identifiedFile = new TableIdentifiedFile(
						rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.PATH), 
						rsForIdentifiedCodeMatchList.getInt(TableIdentifiedFile.TYPE),
						rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.COMPONENT),
						rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.VERSION),
						rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.LICENSE),
						rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.MATCHED_FILE));
				
				String key = identifiedFile.getPath()+"-"+identifiedFile.getMatchedFile();
				identifiedFileMap.put(key, identifiedFile);
			}			

			snippetsForPath = getSnippetsForPath(projectName, path);
			commonSnippets = getCommonSnippets(commonSnippets, snippetsForPath);
			
			if(commonSnippets == null) return;
			Iterator<Map.Entry<String, CodeMatchInfoForFolder>> iter = commonSnippets.entrySet().iterator(); 
			if(iter == null) {
				return;
			}
			
			while(iter.hasNext()) {
				String keyForTCM = iter.next().getKey();				
				CodeMatchInfoForFolder codeMatchFolder = commonSnippets.get(keyForTCM);
				String key = codeMatchFolder.getPath()+"-"+codeMatchFolder.getMatchedFile();
				if(identifiedFileMap.containsKey(key)) {
					
					TableIdentifiedFile identifiedFile = identifiedFileMap.get(key);
					
					pFileSummary.addCodeMatchInfoListWithIdentifiedInfo(
							projectName,
							codeMatchFolder.getComponentName(),
							codeMatchFolder.getVersionName(), 
							codeMatchFolder.getLicenseName(), 
							codeMatchFolder.getUsage(), 
							codeMatchFolder.getStatus(), 
							codeMatchFolder.getPercentage(), 
							codeMatchFolder.getMatchedFile(), 
							identifiedFile.getComponent(), 
							identifiedFile.getVersion(), 
							identifiedFile.getLicense() );

				}
				else {
					pFileSummary.addCodeMatchInfoList(
							codeMatchFolder.getComponentName(),
							codeMatchFolder.getVersionName(), 
							codeMatchFolder.getLicenseName(), 
							codeMatchFolder.getUsage(), 
							codeMatchFolder.getStatus(), 
							codeMatchFolder.getPercentage(), 
							codeMatchFolder.getMatchedFile() );
				}
			}
		}catch (Exception e) {
			log.warn("updateCodeMatchInfoForSingleFile error : ");
			log.warn(e);
		}		
	}

	synchronized public static void updateCodeMatchInfoForMultipleFile(
			String pProjectName, 
			MultipleFileSummary pMultipleFileSummary) {
		
		ArrayList<String> paths = pMultipleFileSummary.getPaths();
		HashMap<String, TableIdentifiedFile> identifiedFileMap = new HashMap<String, TableIdentifiedFile>();
			// key: path + matchedFile	,	value: TableIdentifiedFile
		HashMap<String, CodeMatchInfoForFolder> commonSnippets = null;
			// key: componentName + version + licenseName + status
		HashMap<String, CodeMatchInfoForFolder> snippetsForPath = null;
			// key: componentName + version + licenseName + status
		try {
			for(int i=0; i<paths.size(); i++){
				String queryTableIdentifiedFile = TableIdentifiedFile.getQueryForIdentifiedFileListByFile(pProjectName, paths.get(i));
				ResultSet rsForIdentifiedCodeMatchList = selectSQL(queryTableIdentifiedFile);
				while(rsForIdentifiedCodeMatchList.next()) {
					TableIdentifiedFile identifiedFile = new TableIdentifiedFile(
							rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.PATH), 
							rsForIdentifiedCodeMatchList.getInt(TableIdentifiedFile.TYPE),
							rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.COMPONENT),
							rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.VERSION),
							rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.LICENSE),
							rsForIdentifiedCodeMatchList.getString(TableIdentifiedFile.MATCHED_FILE));
					
					String key = identifiedFile.getPath()+"-"+identifiedFile.getMatchedFile();
					identifiedFileMap.put(key, identifiedFile);
				}			

				snippetsForPath = getSnippetsForPath(pProjectName, paths.get(i));
				commonSnippets = getCommonSnippets(commonSnippets, snippetsForPath);
			}
			
			if(commonSnippets == null) return;
			Iterator<Map.Entry<String, CodeMatchInfoForFolder>> iter = commonSnippets.entrySet().iterator(); 
			if(iter == null) {
				return;
			}
			
			while(iter.hasNext()) {
				String keyForTCM = iter.next().getKey();			
				CodeMatchInfoForFolder codeMatchFolder = commonSnippets.get(keyForTCM);
				String key = codeMatchFolder.getPath()+"-"+codeMatchFolder.getMatchedFile();
				if(identifiedFileMap.containsKey(key)) {
					
					TableIdentifiedFile identifiedFile = identifiedFileMap.get(key);
					
					pMultipleFileSummary.addCodeMatchInfoListWithIdentifiedInfo(
							pProjectName,
							codeMatchFolder.getComponentName(),
							codeMatchFolder.getVersionName(), 
							codeMatchFolder.getLicenseName(), 
							codeMatchFolder.getUsage(), 
							codeMatchFolder.getStatus(), 
							codeMatchFolder.getPercentage(), 
							codeMatchFolder.getMatchedFile(), 
							identifiedFile.getComponent(), 
							identifiedFile.getVersion(), 
							identifiedFile.getLicense() );

				}
				else {
					pMultipleFileSummary.addCodeMatchInfoList(
							codeMatchFolder.getComponentName(),
							codeMatchFolder.getVersionName(), 
							codeMatchFolder.getLicenseName(), 
							codeMatchFolder.getUsage(), 
							codeMatchFolder.getStatus(), 
							codeMatchFolder.getPercentage(), 
							codeMatchFolder.getMatchedFile() );
				}
			}
		}catch (Exception e) {
			log.warn("updateCodeMatchInfoForMultipleFile error : ");
			log.warn(e);
		}		
	}
	
	synchronized public static void updateCodeMatchForMultiFileFolderView(
			String pProjectName, 
			MultipleFileSummary pMultipleFileSummary) {
		
		HashMap<String, CodeMatchInfoForFolder> codeMatchSnippetsMap = new HashMap<String, CodeMatchInfoForFolder>();
				// key = component+"_"+version+"_"+license+"_"+status
		HashMap<String, TableIdentifiedFile> identifiedFileMap = new HashMap<String, TableIdentifiedFile>();
				// key = filepath+"-"+matchedFilePathInServer
		
		ArrayList<String> paths = pMultipleFileSummary.getPaths();
		
		try {
			for(String path : paths) {
				
				String sqlForIdentified = TableIdentifiedFile.getQueryForIdentifiedListByFolder(pProjectName, path);
				log.debug("updateCodeMatchInfoForMultipleFileFolderView sqlForIdentified : " + sqlForIdentified);
				
				ResultSet rsForIdentifiedSnippets = selectSQL(sqlForIdentified);
				while(rsForIdentifiedSnippets.next()) {
					TableIdentifiedFile identifiedFile = new TableIdentifiedFile(
							rsForIdentifiedSnippets.getString(TableIdentifiedFile.PATH), 
							rsForIdentifiedSnippets.getInt(TableIdentifiedFile.TYPE),
							rsForIdentifiedSnippets.getString(TableIdentifiedFile.COMPONENT),
							rsForIdentifiedSnippets.getString(TableIdentifiedFile.VERSION),
							rsForIdentifiedSnippets.getString(TableIdentifiedFile.LICENSE),
							rsForIdentifiedSnippets.getString(TableIdentifiedFile.MATCHED_FILE));
					String key = identifiedFile.getPath()+"-"+identifiedFile.getMatchedFile();
					identifiedFileMap.put(key, identifiedFile);
				}
				
				String sqlForCodeMatch = TableCodeMatch.getQueryForCodematchlistByFile(pProjectName, path);
				log.debug("updateCodeMatchInfoForMultipleFileFolderView sqlForCodeMatch : " + sqlForCodeMatch);
				
				ResultSet rsForCodeMatchSnippets = selectSQL(sqlForCodeMatch);
				while(rsForCodeMatchSnippets.next()) {
					CodeMatchInfoForFolder codeMatchFolder = new CodeMatchInfoForFolder(
							rsForCodeMatchSnippets.getString(TableCodeMatch.PATH), 
							rsForCodeMatchSnippets.getString(TableCodeMatch.COMPONENT), 
							rsForCodeMatchSnippets.getString(TableCodeMatch.VERSION), 
							rsForCodeMatchSnippets.getString(TableCodeMatch.LICENSE), 
							rsForCodeMatchSnippets.getInt(TableCodeMatch.USAGE), 
							rsForCodeMatchSnippets.getInt(TableCodeMatch.STATUS), 
							rsForCodeMatchSnippets.getInt(TableCodeMatch.PERCENTAGE), 
							rsForCodeMatchSnippets.getString(TableCodeMatch.MATCHED_FILE) );
					
					String key = codeMatchFolder.getComponentName()+"_"+
								 codeMatchFolder.getVersionName()+"_"+
								 codeMatchFolder.getLicenseName()+"_"+
								 codeMatchFolder.getStatus();
					
					if(codeMatchSnippetsMap.keySet().contains(key) == false) {
						codeMatchSnippetsMap.put(key, codeMatchFolder);
					} else {
						CodeMatchInfoForFolder exitedTableCodeMatch = codeMatchSnippetsMap.get(key);
						switch(exitedTableCodeMatch.getStatus()) {
							case TableCodeMatch.CODE_MATCH_TABLE_STATUS_PENDING:
								exitedTableCodeMatch.increasePendingSnippetCount();
								break;
								
							case TableCodeMatch.CODE_MATCH_TABLE_STATUS_IDENTIFIED:
								exitedTableCodeMatch.increaseIdentifiedSnippetCount();
								break;
								
							case TableCodeMatch.CODE_MATCH_TABLE_STATUS_DECLARED:
								exitedTableCodeMatch.increaseDeclaredSnippetCount();
								break;
						}
					}
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
		
		Iterator<Map.Entry<String, CodeMatchInfoForFolder>> iter = codeMatchSnippetsMap.entrySet().iterator(); 
		if(iter == null) {
			return;
		}
		
		while(iter.hasNext()) {
			String key = iter.next().getKey();
			CodeMatchInfoForFolder codeMatchFolder = codeMatchSnippetsMap.get(key);
			String keyIdentified = codeMatchFolder.getPath()+"-"+codeMatchFolder.getMatchedFile();
			String identifiedComponent = null;
			String identifiedVersion = null;
			String identifiedLicense = null;
			
			if(identifiedFileMap.containsKey(keyIdentified)) {
				
				TableIdentifiedFile tableIdentifiedFile = identifiedFileMap.get(keyIdentified);
				identifiedComponent = tableIdentifiedFile.getComponent();
				identifiedVersion = tableIdentifiedFile.getVersion();
				identifiedLicense = tableIdentifiedFile.getLicense();
			}
			
			pMultipleFileSummary.addCodeMatchInforForMultipleFileSummary(
					codeMatchFolder.getComponentName(), 
					codeMatchFolder.getVersionName(), 
					codeMatchFolder.getLicenseName(),
					codeMatchFolder.getPendingSnippetCount(), 
					codeMatchFolder.getIdentifiedSnippetCount(),
					codeMatchFolder.getDeclaredSnippetCount(),
					identifiedComponent, 
					identifiedVersion, 
					identifiedLicense );
			
		}
		
	}
	
	synchronized public static void updateCodeMatchFolderSummary(String projectName, String pPath, FolderSummary folderSummary) {
		long startTime = Time.startTime("updateCodeMatchFolderSummary");
		folderSummary.clear();
		
		ResultSet rs1 = null;
		try {
			String sqlForIdentified = TableIdentifiedFile.getQueryForIdentifiedListByFolder(projectName, pPath);
			log.debug("CodeMatchFolder ~~~ sqlForIdentified: " + sqlForIdentified);	
			rs1 = selectSQL(sqlForIdentified);
			HashMap<String, TableIdentifiedFile> identifiedFileMap = new HashMap<String, TableIdentifiedFile>();
			// key = filepath+"-"+matchedFilePathInServer

			while(rs1.next()) {
				TableIdentifiedFile identifiedFile = new TableIdentifiedFile(
						rs1.getString(TableIdentifiedFile.PATH), 
						rs1.getInt(TableIdentifiedFile.TYPE),
						rs1.getString(TableIdentifiedFile.COMPONENT),
						rs1.getString(TableIdentifiedFile.VERSION),
						rs1.getString(TableIdentifiedFile.LICENSE),
						rs1.getString(TableIdentifiedFile.MATCHED_FILE));
				String key = identifiedFile.getPath()+"-"+identifiedFile.getMatchedFile();
				identifiedFileMap.put(key, identifiedFile);
			}
			
			String sqlForCodeMatch = TableCodeMatch.getQueryForCodeMatchlistByFolder(projectName, pPath);

			log.debug("CodeMatchFolder ~~~ sqlForCodeMatch: " + sqlForCodeMatch);	
			rs1 = selectSQL(sqlForCodeMatch);

			HashMap<String, CodeMatchInfoForFolder> codeMatchInfoForFolderMap = new HashMap<String, CodeMatchInfoForFolder>();
			// key = component+version+license+identifiedComponent+identifiedVersion+identifiedLicense
			
			while(rs1.next()) {
				CodeMatchInfoForFolder codeMatchFolder = new CodeMatchInfoForFolder(
						rs1.getString(TableCodeMatch.PATH),
						rs1.getString(TableCodeMatch.COMPONENT), 
						rs1.getString(TableCodeMatch.VERSION), 
						rs1.getString(TableCodeMatch.LICENSE), 
						rs1.getInt(TableCodeMatch.USAGE), 
						rs1.getInt(TableCodeMatch.STATUS), 
						rs1.getInt(TableCodeMatch.PERCENTAGE), 
						rs1.getString(TableCodeMatch.MATCHED_FILE), 
						rs1.getString(TableCodeMatch.COMMENT));
				

				String identifiedComponent = null;
				String identifiedVersion = null;
				String identifiedLicense = null;
				String key = codeMatchFolder.getPath()+"-"+codeMatchFolder.getMatchedFile();
				if(identifiedFileMap.containsKey(key)) {
					identifiedComponent = identifiedFileMap.get(key).getComponent();
					identifiedVersion = identifiedFileMap.get(key).getVersion();
					identifiedLicense = identifiedFileMap.get(key).getLicense();
					
				}
				String key2 = codeMatchFolder.getComponentName()+codeMatchFolder.getVersionName()+codeMatchFolder.getLicenseName()+
							identifiedComponent+identifiedVersion+identifiedLicense;
				

				
				if(codeMatchInfoForFolderMap.containsKey(key2) == false) {
					CodeMatchInfoForFolder codeMatchInfoForFolder = new CodeMatchInfoForFolder(
							codeMatchFolder.getComponentName(), 
							codeMatchFolder.getVersionName(), 
							codeMatchFolder.getLicenseName(), 
							0, 
							0,
							0,
							identifiedComponent,
							identifiedVersion,
							identifiedLicense
							);
					codeMatchInfoForFolderMap.put(key2, codeMatchInfoForFolder);
				}
				CodeMatchInfoForFolder codeMatchInfoForFolder = codeMatchInfoForFolderMap.get(key2);
				switch(codeMatchFolder.getStatus()) {
				
					case TableCodeMatch.CODE_MATCH_TABLE_STATUS_PENDING:
						codeMatchInfoForFolder.increasePendingSnippetCount();
						break;
						
					case TableCodeMatch.CODE_MATCH_TABLE_STATUS_IDENTIFIED:
						codeMatchInfoForFolder.increaseIdentifiedSnippetCount();
						break;
						
					case TableCodeMatch.CODE_MATCH_TABLE_STATUS_DECLARED:
						codeMatchInfoForFolder.increaseDeclaredSnippetCount();
						break;
				}
			}
			
			for(CodeMatchInfoForFolder codeMatchInfoForFolder: codeMatchInfoForFolderMap.values()) {
				folderSummary.addCodeMatchInforForFolderList(codeMatchInfoForFolder);
			}
			rs1.close();
			
		} catch (Exception e) {
			log.debug(e);
		} finally {
			Time.endTime("updateCodeMatchFolderSummary", startTime);
			if(rs1 != null){
				try {
					rs1.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
	}
	
	synchronized private static HashMap<String, CodeMatchInfoForFolder> getCommonSnippets(
			HashMap<String, CodeMatchInfoForFolder> target1st, 
			HashMap<String, CodeMatchInfoForFolder> target2nd) {
		
		HashMap<String, CodeMatchInfoForFolder> commonSnippets = new HashMap<String, CodeMatchInfoForFolder>();
				// key: componentName + version + licenseName + status	,	value: TableCodeMatch
		if(target1st == null) {
			return target2nd;
		}
		
		Iterator<Map.Entry<String, CodeMatchInfoForFolder>> iter = target1st.entrySet().iterator(); 
		if(iter == null) {
			return commonSnippets;
		}
		
		while(iter.hasNext()) {
			String key = iter.next().getKey();		
			if(target2nd.containsKey(key)) {
				commonSnippets.put(key, target1st.get(key));
			}
		}
		return commonSnippets;
	}
	
	synchronized private static HashMap<String, CodeMatchInfoForFolder> getSnippetsForPath(String projectName, String path) {
		
		String table = "codematch_"+getProjectTablePostfix(projectName);
		String sql = "SELECT * FROM "+table+
					 " WHERE path='"+path+"';";
		HashMap<String, CodeMatchInfoForFolder> snippetsForPath = new HashMap<String, CodeMatchInfoForFolder>();
				// key: componentName + version + licenseName + status	,	value: TableCodeMatch
		log.debug("getTableCodeMatch : " + sql);
		ResultSet rs = selectSQL(sql);
		
		try {
			while(rs.next()) {
				String key = rs.getString(TableCodeMatch.COMPONENT)+"_"+
							 rs.getString(TableCodeMatch.VERSION)+"_"+
							 rs.getString(TableCodeMatch.LICENSE)+"_"+
							 rs.getInt(TableCodeMatch.STATUS);
				CodeMatchInfoForFolder codeMatchFolder = new CodeMatchInfoForFolder(
														rs.getString(TableCodeMatch.PATH),
														rs.getString(TableCodeMatch.COMPONENT), 
														rs.getString(TableCodeMatch.VERSION), 
														rs.getString(TableCodeMatch.LICENSE), 
														rs.getInt(TableCodeMatch.USAGE), 
														rs.getInt(TableCodeMatch.STATUS), 
														rs.getInt(TableCodeMatch.PERCENTAGE), 
														rs.getString(TableCodeMatch.MATCHED_FILE));
				snippetsForPath.put(key, codeMatchFolder);
			}
		} catch (SQLException e) {
			log.warn(e);
		}
		
		return snippetsForPath;
	}

	synchronized public static void updateIdentifyInfoForCommonCodeMatchFile(String projectName, String pFilePath) {
		String table = "codematch_"+getProjectTablePostfix(projectName);
		update("update "+table+" set status='"+ AbstractMatchInfo.STATUS_DECLARED +"' where path='"+pFilePath+"';"); 
		// status = 4 : declared status, status = 0 : pending status 
	}

	synchronized public static void updateIdentifyInfoForCommonPatternMatchFile(String projectName, String pFilePath) {
		String table = "patternmatch_"+getProjectTablePostfix(projectName);
		String updateSql = "update "+table
						+" set status='"+ AbstractMatchInfo.STATUS_IDENTIFIEDCOMMON 
						+"' where path='"+pFilePath
						+"' and status='"+ AbstractMatchInfo.STATUS_PENDING +"';";
		log.debug("updateIdentifyInfoForCommonPatternMatchFile SQL : "+updateSql);
		update(updateSql);
	}
	
	synchronized public static void updateIdentifyInfoForCommonStringSearchFile(
			String projectName, 
			String pFilePath, 
			String pComponent, 
			String pLicense) {
		
		pComponent = QueryUtil.makeValid(pComponent);
		pLicense = QueryUtil.makeValid(pLicense);
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		String updateSql = "update "+table
						+" set status='"+ AbstractMatchInfo.STATUS_DECLARED 
						+"', component='"+pComponent+"', license='"+pLicense
						+"' where path='"+pFilePath+"';";
		log.debug("updateIdentifyInfoForCommonStringSearchFile SQL : "+updateSql);
		update(updateSql); // status = 4 : declared status, status = 0 : pending status
	}
	
	synchronized public static void updateIdentifyInfoForPatternMatch(
			String projectName, 
			String pFilePath, 
			String pComponent, 
			String pVersion, 
			String pLicense) {
		
		String tablePostfix = getProjectTablePostfix(projectName);
		pComponent = QueryUtil.makeValid(pComponent);
		pLicense = QueryUtil.makeValid(pLicense);
		String updateSQL = "update patternmatch_"+tablePostfix+" set status='1',component='"+pComponent+"',version='"+pVersion+"',license='"+pLicense+"' where path='"+pFilePath+"' and status='0';";
		log.debug(updateSQL);
		IdentificationDBManager.update(updateSQL);
		IdentificationDBManager.addIdentifiedFile(projectName, pFilePath, AbstractDiscoveryController.TYPE_PATTERN_MATCH, pComponent, pVersion, pLicense, "");
		
		IdentificationDBManager.updateIdentifyInfoForCommonStringSearchFile(projectName, pFilePath, pComponent, pLicense);
		IdentificationDBManager.updateIdentifyInfoForCommonCodeMatchFile(projectName, pFilePath);
	}
	synchronized public static void updateResetInfoForCommonCodeMatchFile(String projectName, String pFilePath) {
		String table = "codematch_"+getProjectTablePostfix(projectName);
		update("update "+table
				+" set status='"+ AbstractMatchInfo.STATUS_PENDING 
				+"' where path='"+pFilePath
				+"' and status='"+ AbstractMatchInfo.STATUS_DECLARED +"';");
	}
	synchronized public static void updateResetInfoForCommonPatternMatchFile(String projectName, String pFilePath) {
		String table = "patternmatch_"+getProjectTablePostfix(projectName);
		update("update "+table
				+" set status='"+ AbstractMatchInfo.STATUS_PENDING 
				+"' where path='"+pFilePath
				+"' and status='"+ AbstractMatchInfo.STATUS_IDENTIFIEDCOMMON +"';");
	}
	
	synchronized public static void updateResetInfoForCommonStringSearchFile(String projectName, String pFilePath) {
		String table = "stringsearch_"+getProjectTablePostfix(projectName);
		update("update "+table
				+" set status='"+ AbstractMatchInfo.STATUS_PENDING 
				+"', component='', license='' where path='"+pFilePath
				+"' and status='"+ AbstractMatchInfo.STATUS_DECLARED +"';");
	}
	
	synchronized public static void updateResetInfoForPatternMatch(String projectName, String pFilePath) {
		String tablePostfix = getProjectTablePostfix(projectName);
		String updateSQL = "update patternmatch_"+tablePostfix+" set status='0',component=null,version=null,license=null where path='"+pFilePath+"' and status='1';";
		log.debug("resetPatternMatch : updateSQL : "+updateSQL);
		IdentificationDBManager.update(updateSQL);
		IdentificationDBManager.removeIdentifiedFile(projectName, pFilePath, AbstractDiscoveryController.TYPE_PATTERN_MATCH, "");
		IdentificationDBManager.updateResetInfoForCommonStringSearchFile(projectName, pFilePath);
		IdentificationDBManager.updateResetInfoForCommonCodeMatchFile(projectName, pFilePath);
	}
	
	synchronized public static void writeDBComment(
			String projectName, 
			int type, 
			String path,
			String comment) {
		
		comment = QueryUtil.makeValid(comment);
		
		try {
			String condition = "";
			if(type==IdentificationConstantValue.STRING_MATCH_TYPE) {
				condition = "stringsearch";
			} else if(type==IdentificationConstantValue.CODE_MATCH_TYPE) {
				condition = "codematch";
			} else if(type==IdentificationConstantValue.PATTERN_MATCH_TYPE) {
				condition = "patternmatch";
			}
			
			String sql = "update "+condition+"_"+getProjectTablePostfix(projectName)
						+" set comment ='"+comment+"' " 
						+"where path='"+path+"';";
			update(sql);
		} catch (Exception e) {
			log.warn(e);
		}
	}

	synchronized public static HashMap<Integer, ArrayList<String>> loadIdentifiedFilesInfoToMemory(String projectName) {
		HashMap<Integer, ArrayList<String>> hmIdentifiedFiles = new HashMap<Integer, ArrayList<String>>();
			//	key: match type, 	value: each match type's identified file list
		
		ArrayList<String> alSSIdentifiedFiles = new ArrayList<String>();
		ArrayList<String> alCMIdentifiedFiles = new ArrayList<String>();
		ArrayList<String> alPMIdentifiedFiles = new ArrayList<String>();
		String table = "";
		ResultSet rs = null;
		
		hmIdentifiedFiles.clear();
		
		try {
			if(projectName == null || ProjectAPIWrapper.getProjectID(projectName) == null) {
				return hmIdentifiedFiles;
			}
			log.debug("@@@ save identified files to memory... start...");
			table = "stringsearch_"+ProjectAPIWrapper.getProjectID(projectName).replace("-", "_");
			rs = IdentificationDBManager.selectSQL("select distinct(path) from "+table+" where path not in (select distinct(path) from "+table+" where status='0');");
			while(rs.next()) {
				alSSIdentifiedFiles.add(rs.getString(1));
			}
			hmIdentifiedFiles.put(IdentificationConstantValue.STRING_MATCH_TYPE, alSSIdentifiedFiles);
			rs.close();
			
			table = "codematch_"+ProjectAPIWrapper.getProjectID(projectName).replace("-", "_");
			rs = IdentificationDBManager.selectSQL("select distinct(path) from "+table+" where path not in (select distinct(path) from "+table+" where status='0');");
			while(rs.next()) {
				alCMIdentifiedFiles.add(rs.getString(1));
			}
			hmIdentifiedFiles.put(IdentificationConstantValue.CODE_MATCH_TYPE, alCMIdentifiedFiles);
			if(rs != null) {rs.close();}
			
			table = "patternmatch_"+ProjectAPIWrapper.getProjectID(projectName).replace("-", "_");
			rs = IdentificationDBManager.selectSQL("select distinct(path) from "+table+" where path not in (select distinct(path) from "+table+" where status='0');");
			while(rs.next()) {
				alPMIdentifiedFiles.add(rs.getString(1));
			}
			hmIdentifiedFiles.put(IdentificationConstantValue.PATTERN_MATCH_TYPE, alPMIdentifiedFiles);
			if(rs != null) {rs.close();}
			
			log.debug("@@@ save identified files to memory... end...");
			
			return hmIdentifiedFiles;
		} catch (SQLException e1) {
			log.warn(e1);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static ArrayList<String> getMatchedFileFromCodeMatch(
			String pFilePath, 
			String pComponent, 
			String pVersion, 
			String pMatchedFile, 
			String tablePostfix) {
		
		ArrayList<String> matchedFileList = new ArrayList<String>();
		ResultSet rs = null;
		pComponent = QueryUtil.makeValid(pComponent);
		String selectSQL = "select matched_file from codematch_"+tablePostfix+" where path='"+pFilePath+"' and component='"+pComponent+"' and version='"+pVersion+"';";
		try {
			rs = IdentificationDBManager.selectSQL(selectSQL);
			while(rs.next()) {
				matchedFileList.add(rs.getString(1));
			}
			return matchedFileList;
		} catch (SQLException e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static ComponentVersion getComponentInfoUsingMatchedFile(
			String pFilePath,
			String pMatchedFile, 
			String tablePostfix) {
		
		ResultSet rs = null;
		String selectSQL = "select component, version from codematch_"+tablePostfix+" where path='"+pFilePath+"' and matched_file='"+pMatchedFile+"';";
		try {
			rs = IdentificationDBManager.selectSQL(selectSQL);
			String componentName = null;
			String versionName = null;
			while(rs.next()) {
				componentName = rs.getString(1);
				versionName = rs.getString(2);
			}
			if(componentName == null) return null;
			return ComponentAPIWrapper.getComponentVersionByName(componentName, versionName);
		} catch (SQLException e) {
			log.warn(e);
			return null;
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.debug(e);
				}
			}
		}
	}

	synchronized public static PreparedStatement updateIdentifiedTablePreparedStatement(String projectName) {
		String identified_file_table = "identified_file_"+getProjectTablePostfix(projectName);
		try {
			return conn.prepareStatement("INSERT INTO "+identified_file_table+
										 " VALUES(?,?,?,?,?,?);");
		} catch (SQLException e) {
			log.warn(e);
		}
		return null;
	}
	
	private static StringBuffer smfBuf = new StringBuffer();

	synchronized public static ArrayList<String> getStringMatchFileList(
			String projectName,
			String path, 
			String stringSearch,
			int status) {
		
		String tableName = IdentificationConstantValue.DBTABLE_NAME.get(IdentificationConstantValue.STRING_MATCH_TYPE)
		+"_"+getProjectTablePostfix(projectName);
		
		smfBuf.setLength(0);
		smfBuf.append("SELECT DISTINCT(path) FROM ");
		smfBuf.append(tableName);
		smfBuf.append(" WHERE status=").append(status);
		smfBuf.append(" AND path like '").append(path).append("%'");
		smfBuf.append(";");

		String strSql = smfBuf.toString();
		log.debug( "getIdentifyFileList sql : "+strSql);

		ResultSet rs = null; 

		ArrayList<String> cacheIdentificationArrayList = new ArrayList<String>();
		//ResultSet rs = null;
		try {
			rs = selectSQL(strSql);
			int cnt=0;
			while(rs.next()) {
				String filePath = rs.getString(1);
				cacheIdentificationArrayList.add(filePath);
				log.debug("   >>> StringMatch selected file: ["+ ++cnt+"] "+filePath);				
			}
			//if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return cacheIdentificationArrayList;
	
	}
	
	private static StringBuffer cmfBuf = new StringBuffer();

	synchronized public static ArrayList<String> getCodeMatchFileList(
			String projectName,
			String folderPath,
			String componentName,
			String versionName,
			String licenseName,
			int status) {
		
		String tableName = IdentificationConstantValue.DBTABLE_NAME.get(IdentificationConstantValue.CODE_MATCH_TYPE)
							+"_"+getProjectTablePostfix(projectName);
		
		componentName = QueryUtil.makeValid(componentName);
		licenseName = QueryUtil.makeValid(licenseName);
		
		cmfBuf.setLength(0);
		cmfBuf.append("SELECT DISTINCT(path) FROM ");
		cmfBuf.append(tableName);
		cmfBuf.append(" WHERE status=").append(status);
		cmfBuf.append(" AND component like '").append(componentName).append("'");
		cmfBuf.append(" AND version like '").append(versionName).append("'");
		cmfBuf.append(" AND license like '").append(licenseName).append("'");
		cmfBuf.append(" AND path like '").append(folderPath).append("%'");
		cmfBuf.append(";");
		
		String strSql = cmfBuf.toString();
		log.debug( " getCodeMatchPendingFileList sql : "+strSql);
		ResultSet rs = null; 
		
		ArrayList<String> cacheIdentificationArrayList = new ArrayList<String>();
		try {
			rs = selectSQL(strSql);
			int cnt=0;
			while(rs.next()) {
				String filePath = rs.getString(1);
				cacheIdentificationArrayList.add(filePath);
				log.debug("   >>> CodeMatch selected file: ["+ ++cnt+"] "+filePath);				
			}
//			if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return cacheIdentificationArrayList;
	}
	
	private static StringBuffer pmfBuf = new StringBuffer();
	
	synchronized public static ArrayList<String> getPatternMatchFileList(
			String projectName,
			String path,
			int status) {
		
		pmfBuf.setLength(0);
		String tableName = IdentificationConstantValue.DBTABLE_NAME.get(IdentificationConstantValue.PATTERN_MATCH_TYPE)
				+"_"+getProjectTablePostfix(projectName);
		
		pmfBuf.setLength(0);
		pmfBuf.append("SELECT DISTINCT(path) FROM ");
		pmfBuf.append(tableName);
		pmfBuf.append(" WHERE status=").append(status);
		pmfBuf.append(" AND path like '").append(path).append("%'");
		pmfBuf.append(";");

		String strSql = pmfBuf.toString();
		log.debug( " getPatternMatchPendingFileList sql : "+strSql);
		ResultSet rs = null; 

		ArrayList<String> cacheIdentificationArrayList = new ArrayList<String>();
		//	ResultSet rs = null;
		try {
			rs = selectSQL(strSql);
			int cnt=0;
			while(rs.next()) {
				String filePath = rs.getString(1);
				cacheIdentificationArrayList.add(filePath);
				log.debug("   >>> PatternMatch selected file: ["+ ++cnt+"] "+filePath);				
			}
			//	if(rs!=null) rs.close();
		} catch (Exception e) {
			log.warn(e);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return cacheIdentificationArrayList;
	}

	synchronized public static HashMap<Integer ,Integer> setOkCancelButtonEnabledForPatternMatch(
			String projectName, 
			ArrayList<String> selectedPaths) {
		
		String postfix = IdentificationDBManager.getProjectTablePostfix(projectName);
		String table = "patternmatch_"+postfix;
		HashMap<Integer ,Integer> countPerStatus = new HashMap<Integer, Integer>();
			// key: status, value: count
		int pendingCount = 0;
		int identifiedCount = 0;
		int status = AbstractMatchInfo.STATUS_UNKNOWN;
		
		for(String path : selectedPaths) {
			ResultSet rs = selectSQL("select status from " + table + " where path='" + path + "';");
			try {
				if(rs.next()) {
					status = Tools.transStringToInteger(rs.getString(1));
				
					if(status==AbstractMatchInfo.STATUS_PENDING) {
						pendingCount++;
					} else if(status==AbstractMatchInfo.STATUS_IDENTIFIED) {
						identifiedCount++;
					}
				}
			} catch (SQLException e) {
				log.debug(e);
			} finally {
				if(rs != null){
					try {
						rs.close();
					} catch (SQLException e) {
						log.debug(e);
					}
				}
			}
		}
		
		log.debug("pattern match pendingCount : " + pendingCount);
		log.debug("pattern match identifiedCount : " + identifiedCount);
		
		countPerStatus.put(AbstractMatchInfo.STATUS_PENDING, pendingCount);
		countPerStatus.put(AbstractMatchInfo.STATUS_IDENTIFIED, identifiedCount);
		
		return countPerStatus;
	}


}