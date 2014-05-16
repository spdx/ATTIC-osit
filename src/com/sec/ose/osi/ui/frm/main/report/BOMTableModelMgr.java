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
package com.sec.ose.osi.ui.frm.main.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;

import com.sec.ose.osi.sdk.protexsdk.bom.BOMEnt;
import com.sec.ose.osi.thread.ui_related.UserRequestHandler;
import com.sec.ose.osi.thread.ui_related.data.message.UIResponseObserver;

/**
 * BOMTableModelMgr
 * @author suhyun47.kim, hankido.lee
 * 
 */
public class BOMTableModelMgr  {

	protected static final String EMPTY_PROJECT_NAME = "<Select Project>"; 
	private HashMap<String, BOMTableModel> hash;
	
	public BOMTableModelMgr() {
		hash = new HashMap<String, BOMTableModel>();
		hash.put(EMPTY_PROJECT_NAME, getDeaultBOMTableModel());
	}

	public void clear() {
		hash.clear();
	}

	public BOMTableModel getBOMTableModel(String projectName, JCheckBox checkbox) {
	
		if(hash.containsKey(projectName)) {
			BOMTableModel model =hash.get(projectName);
			model.setCheckboxObserver(checkbox);
			
			return model;
		}
		
		// need to load bom model from server
		
		ArrayList<BOMTableRow> rows = getBOMListFromServer(projectName);
		if(rows == null)
			return getDeaultBOMTableModel();
		
		BOMTableModel model = new BOMTableModel(rows, checkbox);
		hash.put(projectName, model );
		
		return model;
	}

	@SuppressWarnings("unchecked")
	private ArrayList<BOMTableRow> getBOMListFromServer(String projectName) {

		UEProjectName ueProjectName = new UEProjectName(projectName);
		UIResponseObserver observer = 
			UserRequestHandler.getInstance().handle(UserRequestHandler.GET_BOM_LIST_FROM_SERVER, ueProjectName, true, false);
		
		ArrayList<BOMTableRow> rows = new ArrayList<BOMTableRow>();
		ArrayList<BOMEnt> bomEntList = (ArrayList<BOMEnt>) observer.getReturnValue();
		 
		if(bomEntList == null)
			return null;
		
		
		for(int i=0; i<bomEntList.size(); i++) {
			BOMEnt curEnt = bomEntList.get(i);
			BOMTableRow row = new BOMTableRow(curEnt);
			rows.add(row);
		}
		
		
		return rows;
	}
	
	private  BOMTableModel defaultModel;

	protected  BOMTableModel getDeaultBOMTableModel() {
		
		if(defaultModel == null) {
			ArrayList<BOMTableRow> rows = new ArrayList<BOMTableRow>();
			defaultModel = new BOMTableModel(rows, null);
		}
		
		return defaultModel;
	}

	protected BOMTableModel getBOMTableModel(String projectName) {

		if(hash.containsKey(projectName)) {
			BOMTableModel model =hash.get(projectName);
			
			return model;
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void loadBOMTableModel(ArrayList<String> projectNames) {

		ArrayList<String> loadTarget = new ArrayList<String>();
		for(int i=0; i<projectNames.size(); i++) {
			String projectName = projectNames.get(i);
			if(hash.containsKey(projectName) == true)
				continue;
			
			loadTarget.add(projectName);
		}
		if(loadTarget.size() == 0)
			return;
		
		
		
		UEProjectName ueProjectName = new UEProjectName(loadTarget);
		UIResponseObserver observer = 
				UserRequestHandler.getInstance().handle(UserRequestHandler.GET_BOM_LIST_MAP_FROM_SERVER, ueProjectName, true, false);
			
		HashMap<String, ArrayList<BOMEnt>> bomEntListMap = (HashMap<String, ArrayList<BOMEnt>>) observer.getReturnValue();
		
		if(bomEntListMap == null)
			return;
		
		Iterator<Map.Entry<String, ArrayList<BOMEnt>>> iter = bomEntListMap.entrySet().iterator(); 
		if(iter == null) {
			return;
		}
		
		while(iter.hasNext()) {
			String projectName = iter.next().getKey();
			ArrayList<BOMEnt> bomEntList = bomEntListMap.get(projectName);
			ArrayList<BOMTableRow> rows = new ArrayList<BOMTableRow>();
			
			for(int i=0; i<bomEntList.size(); i++) {
				BOMEnt curEnt = bomEntList.get(i);
				BOMTableRow row = new BOMTableRow(curEnt);
				rows.add(row);
			}
			BOMTableModel model = new BOMTableModel(rows, null);
			hash.put(projectName, model );
		}

	}
}
