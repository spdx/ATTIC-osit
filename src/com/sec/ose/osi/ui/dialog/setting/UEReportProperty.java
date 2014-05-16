/**
 * Copyright (c) 2010-2014 Samsung Electronics Co., Ltd. All rights reserved.
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
package com.sec.ose.osi.ui.dialog.setting;

import com.sec.ose.osi.ui.cache.UIEntity;
import com.sec.ose.osi.util.Property;

/**
 * UEReportProperty
 * @author suhyun47.kim, hankido.lee
 *
 */
public class UEReportProperty implements UIEntity{
 
	private static final long serialVersionUID = 1L;
	private String mDefaultReportLocation;
	private String mReciprocalLicense;
	private String mMajorLicense;

	protected UEReportProperty() {
		
		Property prop = Property.getInstance();
		this.mDefaultReportLocation = prop.getProperty(Property.DEFALT_REPORT_LOCATION);
		this.mReciprocalLicense = prop.getProperty(Property.RECIPROCAL_LICENSE);
		this.mMajorLicense = prop.getProperty(Property.MAJOR_LICENSE);
	}
	
	public String getDefaultReportLocation() {
		return mDefaultReportLocation;
	}
	public void setDefaultReportLocation(String defaultReportLocation) {
		mDefaultReportLocation = defaultReportLocation;
	}
	
	public String getMajorLicense() {
		return mMajorLicense;
	}

	public void setMajorLicense(String majorLicense) {
		this.mMajorLicense = majorLicense;
	}

	public String getReciprocalLicense() {
		return mReciprocalLicense;
	}

	public void setReciprocalLicense(String reciprocalLicense) {
		this.mReciprocalLicense = reciprocalLicense;
	}
}
