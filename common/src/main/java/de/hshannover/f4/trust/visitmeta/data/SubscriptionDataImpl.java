/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of visitmeta-common, version 0.6.0,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2016 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.visitmeta.data;

import java.util.List;

import de.hshannover.f4.trust.visitmeta.interfaces.data.Data;
import de.hshannover.f4.trust.visitmeta.interfaces.data.SubscriptionData;

public class SubscriptionDataImpl extends DataImpl implements SubscriptionData {

	public String mIdentifier;

	public String mIdentifierType;

	public String mFilterLinks;

	public String mFilterResult;

	public String mTerminalIdentifierTypes;

	public boolean mStartupSubscribe;

	public int mMaxDepth;

	public int mMaxSize;

	private boolean mActive;

	public SubscriptionDataImpl(String subscriptionName) {
		super.setName(subscriptionName);
		setMaxDepth(-1);
		setMaxSize(-1);
	}

	@Override
	public List<Data> getSubData() {
		// Subscriptions do not have subData
		return null;
	}

	@Override
	public SubscriptionData copy() {
		SubscriptionData tmpCopy = new SubscriptionDataImpl(super.getName());
		tmpCopy.setStartIdentifier(getStartIdentifier());
		tmpCopy.setIdentifierType(getIdentifierType());
		tmpCopy.setMatchLinksFilter(getMatchLinksFilter());
		tmpCopy.setResultFilter(getResultFilter());
		tmpCopy.setTerminalIdentifierTypes(getTerminalIdentifierTypes());
		tmpCopy.setStartupSubscribe(isStartupSubscribe());
		tmpCopy.setMaxDepth(getMaxDepth());
		tmpCopy.setMaxSize(getMaxSize());
		return tmpCopy;
	}

	@Override
	public SubscriptionData clone() {
		return (SubscriptionData) super.clone();
	}

	@Override
	public void changeData(SubscriptionData newData) {
		setName(newData.getName());
		setStartIdentifier(newData.getStartIdentifier());
		setIdentifierType(newData.getIdentifierType());
		setMatchLinksFilter(newData.getMatchLinksFilter());
		setResultFilter(newData.getResultFilter());
		setTerminalIdentifierTypes(newData.getTerminalIdentifierTypes());
		setStartupSubscribe(newData.isStartupSubscribe());
		setMaxDepth(newData.getMaxDepth());
		setMaxSize(newData.getMaxSize());
	}

	@Override
	public String getStartIdentifier() {
		return mIdentifier;
	}

	@Override
	public void setStartIdentifier(String identifier) {
		this.mIdentifier = identifier;
	}

	@Override
	public String getIdentifierType() {
		return mIdentifierType;
	}

	@Override
	public void setIdentifierType(String identifierType) {
		this.mIdentifierType = identifierType;
	}

	@Override
	public String getMatchLinksFilter() {
		return mFilterLinks;
	}

	@Override
	public void setMatchLinksFilter(String filterLinks) {
		this.mFilterLinks = filterLinks;
	}

	@Override
	public String getResultFilter() {
		return mFilterResult;
	}

	@Override
	public void setResultFilter(String filterResult) {
		this.mFilterResult = filterResult;
	}

	@Override
	public String getTerminalIdentifierTypes() {
		return mTerminalIdentifierTypes;
	}

	@Override
	public void setTerminalIdentifierTypes(String terminalIdentifierTypes) {
		this.mTerminalIdentifierTypes = terminalIdentifierTypes;
	}

	@Override
	public boolean isStartupSubscribe() {
		return mStartupSubscribe;
	}

	@Override
	public void setStartupSubscribe(boolean startupSubscribe) {
		this.mStartupSubscribe = startupSubscribe;
	}

	@Override
	public int getMaxDepth() {
		return mMaxDepth;
	}

	@Override
	public void setMaxDepth(int maxDepth) {
		this.mMaxDepth = maxDepth;
	}

	@Override
	public int getMaxSize() {
		return mMaxSize;
	}

	@Override
	public void setMaxSize(int maxSize) {
		this.mMaxSize = maxSize;
	}

	@Override
	public boolean isActive() {
		return mActive;
	}

	@Override
	public void setActive(boolean active) {
		this.mActive = active;
	}

	@Override
	public Class<?> getDataTypeClass() {
		return SubscriptionData.class;
	}

}
