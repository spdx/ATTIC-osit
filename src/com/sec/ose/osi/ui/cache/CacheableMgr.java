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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * CacheableMgr
 * @author suhyun47.kim, sjh.yoo, hankido.lee
 * 
 */
public class CacheableMgr {

	private volatile static CacheableMgr instance = null;

	public static CacheableMgr getInstance() {
		if(instance == null) {
			synchronized(CacheableMgr.class) {
				if(instance == null) {
					instance = new CacheableMgr();
				}
			}
		}
		return instance;
	}
	
	private ArrayList<Cacheable> mCacheable;
	private UICache mCache;
	
	private CacheableMgr() {
		mCacheable = new ArrayList<Cacheable>();
		mCache = new UICache();
	}
	
	public void loadFromCache() {
		
		Iterator<Cacheable> itr = mCacheable.iterator();
		if(itr == null)
			return;
		
		while(itr.hasNext()) {
			Cacheable panel = itr.next();
			panel.loadFromCache();
		}
	}
	
	public void addCacheable(Cacheable cacheable) {
		this.mCacheable.add(cacheable);
	}
	
	public UICache getCache() {
		return this.mCache;
	}

	protected void removeCacheable(Cacheable cacheable) {
		this.mCacheable.remove(cacheable);
	}

	public void saveToCache() {
		
		Iterator<Cacheable> itr = mCacheable.iterator();
		if(itr == null)
			return;
		
		while(itr.hasNext()) {
			Cacheable cacheable = itr.next();
			cacheable.saveToCache();
		}
	}
	
	public void removeAll() {
		mCacheable.clear();
	}
}
