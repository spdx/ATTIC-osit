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
package com.sec.ose.osi.util.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FileOperator
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class FileOperator {
	private static Log log = LogFactory.getLog(FileOperator.class);
	
	private static final int BUF_SIZE = 2048;

	public static boolean saveFile(String filePath, String contents) {
		
		File writeFile = new File(filePath);

		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(new FileWriter(writeFile));
			writer.println(contents);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warn(e);
			return false;
		} finally {

			if(writer != null) {
				writer.flush();
				writer.close();
			}
			writer = null;
		}
		
		
		return true;
	}

	public static void copyFile(File pSource, File pDest) throws FileNotFoundException {
		
		if(pSource == null)
			throw new FileNotFoundException("Source file is not found");
		
		if(pSource.exists() == false ) 
			throw new FileNotFoundException(pSource.getPath());
		
		if(pDest == null)
			throw new FileNotFoundException("Report file is not found");
		
		BufferedInputStream bi = null;
		BufferedOutputStream bo = null;
		
		try {
			bi = new BufferedInputStream(new FileInputStream(pSource));		// FileNotFoundExeption
			bo = new BufferedOutputStream(new FileOutputStream(pDest));

			byte buffer[] = new byte[BUF_SIZE];
			
			int readByte = 0;
			
			while( (readByte = bi.read(buffer)) != -1) {				// IOException
				bo.write(buffer,0, readByte);
				
			}

		} catch (FileNotFoundException e) {
			log.warn(e);
			throw e;
		}
		catch (IOException e) {
			log.warn(e);
		} finally {
			
			if(bo!=null) {
				try {
					bo.flush();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.warn(e);
				}
				try {
					bo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.warn(e);
				}
				
				bo = null;
			}
			if(bi != null) {
				try {
					bi.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.warn(e);
				}
				bi = null;
			}
		}


	}

	public static String getExtention(String filePath) {
		if(filePath == null) return null;
		
		int lastIdxSlash = filePath.lastIndexOf("/");
		int lastIdxDot = filePath.lastIndexOf(".");
		
		if(lastIdxDot < 0 || lastIdxDot < lastIdxSlash)
			return null;

		if(lastIdxSlash+1 == lastIdxDot) return "";
		return filePath.substring(lastIdxDot+1, filePath.length());
	}

	public static String readFile(String filePath) {
		
		File file = new File(filePath);

		BufferedReader br = null;
		
		StringBuffer buf = new StringBuffer();
		
		try {
			br = new BufferedReader((new FileReader(file)));
			String line = null;
			
			while( (line = br.readLine()) != null  ) {
				buf.append(line).append("\n");	
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.warn(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warn(e);
		} finally {
			if(br!= null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.warn(e);
				}
			}
		}

		return buf.toString();
	}

}
