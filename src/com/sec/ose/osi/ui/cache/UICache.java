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
package com.sec.ose.osi.ui.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * UICache
 * @author suhyun47.kim, hankido.lee
 * 
 */

public class UICache implements Serializable {
	private static Log log = LogFactory.getLog(UICache.class);

	private static final long serialVersionUID = 1L;

	private static final String CACHE_FILE_NAME = ".cache";
		public UICache() {
		load();
	}
	
	private HashMap <Integer, UIEntity> mMap = new HashMap<Integer, UIEntity>();
	public static final int ENT_LOGIN = 1;


	public static final int UE_LOGIN 					= 1;
	public static final int UE_PROJECT_EXPLORER 		= 2;
	public static final int UE_AUTO_DECLARE 			= 3;

	public static final int UE_BOM_TABLE				= 4;
	
	public static final int UE_SELECTED_PROJECT_INFO_IN_ANALYSIS = 10;
	
	public static final int UE_REPORT_TEMPLATE				= 15;
	public static final int UE_MONITOR_INTERVAL				= 16;

	public UIEntity getUIEntity(int pKey) {
		return this.mMap.get(pKey);
	}

	public void setUIEntity(int pKey, UIEntity pUIEntity) {
		this.mMap.put(pKey, pUIEntity);
		save();
	}
	
	private void save() {
	
		log.debug("save cache");		
		ObjectOutputStream dos = null;
		
		try {
			File file = new File(CACHE_FILE_NAME);
			if(file.exists() == false)
				file.createNewFile();			// IOException

			dos = new ObjectOutputStream(new FileOutputStream(file));	// FileNotFoundException
			dos.writeObject(mMap);
			

		} catch (FileNotFoundException e) {
			log.warn(e);
		} catch (IOException e) {
			log.warn(e);
		} finally {
			if(dos != null) {
				try {
					dos.flush();
					
				} catch (IOException e) {
					log.warn(e);
				}
				try {
					dos.close();
				} catch (IOException e) {
					log.warn(e);
				}

				dos = null;
			}
		}
		log.debug("save done");
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		File file = new File(CACHE_FILE_NAME);
		if(file.exists() == false) {
			return;
		}

		log.debug("load cache from cache file");

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));	// IO, FileNotFound
		
			this.mMap = (HashMap<Integer, UIEntity>) ois.readObject();							// ClassNotFound
		
		} catch (FileNotFoundException e) {
			log.warn(e);
		} catch (IOException e) {
			log.warn(e);
			file.delete();
		} catch (ClassNotFoundException e) {
			log.warn(e);
			file.delete();
		} catch (Exception e) {
			log.warn(e);
			file.delete();
		} finally {
			
			if(ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					log.warn(e);
				}
			}
			ois = null;
		}
	}
	
	public void setClear() {
		this.mMap.clear();
	}
}
