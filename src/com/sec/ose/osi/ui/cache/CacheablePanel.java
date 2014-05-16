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

import javax.swing.JPanel;

/**
 * CacheablePanel
 * @author suhyun47.kim, sjh.yoo
 * 
 */
public abstract class CacheablePanel extends JPanel implements Cacheable, UIEntityExportable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected UICache mCache = null;
	
	public CacheablePanel() {
	
	//	CacheableMgr.getInstance().addCacheable(this);
		this.mCache = CacheableMgr.getInstance().getCache();
		
	}
	
	public void addCacheable() {
		CacheableMgr.getInstance().addCacheable(this);
	}
	
}
