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

import java.util.ArrayList;
import java.util.List;

import de.hshannover.f4.trust.visitmeta.interfaces.Subscription;
import de.hshannover.f4.trust.visitmeta.interfaces.data.Data;
import de.hshannover.f4.trust.visitmeta.interfaces.data.MapServerData;
import de.hshannover.f4.trust.visitmeta.interfaces.data.SubscriptionData;

public class MapServerDataImpl extends DataImpl implements MapServerData {

	private String mUrl;

	private String mUserName;

	private String mUserPass;

	private String mTruststorePath;

	private String mTruststorePassword;

	private List<SubscriptionData> mSubscriptionDataList;

	private int mMaxPollResultSize;

	private boolean mConnected;

	private boolean mStartupConnect;

	private boolean mAuthenticationBasic;

	private MapServerDataImpl() {
		mSubscriptionDataList = new ArrayList<SubscriptionData>();
	}

	public MapServerDataImpl(String name) {
		this();
		setName(name);
	}

	public MapServerDataImpl(String name, String url, String userName, String userPassword) {
		this();
		setName(name);
		setUrl(url);
		setUserName(userName);
		setUserPassword(userPassword);
	}

	@Override
	public MapServerData copy() {
		MapServerData data = new MapServerDataImpl(getName(), getUrl(), getUserName(), getUserPassword());
		data.setTruststorePath(getTruststorePath());
		data.setTruststorePassword(getTruststorePassword());
		data.setSubscriptionData(getSubscriptions());
		data.setMaxPollResultSize(getMaxPollResultSize());
		data.setStartupConnect(doesConnectOnStartup());
		data.setAuthenticationBasic(isAuthenticationBasic());
		return data;
	}

	@Override
	public MapServerData clone() {
		return (MapServerData) super.clone();
	}

	@Override
	public void changeData(MapServerData newData) {
		setName(newData.getName());
		setUrl(newData.getUrl());
		setUserName(newData.getUserName());
		setUserPassword(newData.getUserPassword());
		setTruststorePath(newData.getTruststorePath());
		setTruststorePassword(newData.getTruststorePassword());
		setMaxPollResultSize(newData.getMaxPollResultSize());
		setStartupConnect(newData.doesConnectOnStartup());
		setAuthenticationBasic(newData.isAuthenticationBasic());
	}

	@Override
	public String getConnectionName() {
		return super.getName();
	}

	@Override
	public void setConnectionName(String connectionName) {
		super.setName(connectionName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Data> getSubData() {
		return (List<Data>) (List<?>) getSubscriptions();
	}

	@Override
	public String getUrl() {
		return mUrl;
	}

	@Override
	public void setUrl(String url) {
		mUrl = url;
	}

	@Override
	public String getUserName() {
		return mUserName;
	}

	@Override
	public void setUserName(String userName) {
		mUserName = userName;
	}

	@Override
	public String getUserPassword() {
		return mUserPass;
	}

	@Override
	public void setUserPassword(String userPassword) {
		mUserPass = userPassword;
	}

	@Override
	public String getTruststorePath() {
		return mTruststorePath;
	}

	@Override
	public void setTruststorePath(String truststorePath) {
		mTruststorePath = truststorePath;
	}

	@Override
	public String getTruststorePassword() {
		return mTruststorePassword;
	}

	@Override
	public void setTruststorePassword(String truststorePass) {
		mTruststorePassword = truststorePass;
	}

	@Override
	public void setSubscriptionData(List<SubscriptionData> connection) {
		mSubscriptionDataList = connection;
	}

	@Override
	public int getMaxPollResultSize() {
		return mMaxPollResultSize;
	}

	@Override
	public void setMaxPollResultSize(int maxPollResultSize) {
		mMaxPollResultSize = maxPollResultSize;
	}

	@Override
	public void setConnected(boolean connected) {
		mConnected = connected;
	}

	@Override
	public boolean isConnected() {
		return mConnected;
	}

	@Override
	public void setStartupConnect(boolean startupConnect) {
		mStartupConnect = startupConnect;
	}

	@Override
	public boolean doesConnectOnStartup() {
		return mStartupConnect;
	}

	@Override
	public void setAuthenticationBasic(boolean authenticationBasic) {
		mAuthenticationBasic = authenticationBasic;
	}

	@Override
	public boolean isAuthenticationBasic() {
		return mAuthenticationBasic;
	}

	@Override
	public void addSubscription(SubscriptionData subscriptionData) {
		mSubscriptionDataList.add(subscriptionData);
	}

	@Override
	public void updateSubscription(SubscriptionData newData) {
		for (Data subData : getSubscriptions()) {
			if (subData.getName().equals(newData.getName())) {
				((SubscriptionData) subData).changeData(newData);
				break;
			}
		}
	}

	@Override
	public void deleteSubscription(String subscriptionName) {
		for (Data subData : getSubscriptions()) {
			if (subData.getName().equals(subscriptionName)) {
				mSubscriptionDataList.remove(subData);
				break;
			}
		}
	}

	@Override
	public void deleteAllSubscriptions() {
		mSubscriptionDataList.clear();
	}

	@Override
	public List<SubscriptionData> getActiveSubscriptions() {
		List<SubscriptionData> newActiveList = new ArrayList<SubscriptionData>();
		for (SubscriptionData subData : getSubscriptions()) {
			if (((Subscription) subData).isActive()) {
				newActiveList.add(subData);
			}
		}

		return newActiveList;
	}

	@Override
	public List<SubscriptionData> getSubscriptions() {
		return new ArrayList<SubscriptionData>(mSubscriptionDataList);
	}

	@Override
	public Class<?> getDataTypeClass() {
		return MapServerData.class;
	}

}
