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
 * This file is part of visitmeta-common, version 0.2.0,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2014 Trust@HsH
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
package de.hshannover.f4.trust.visitmeta.interfaces.ifmap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONObject;

import de.hshannover.f4.trust.ifmapj.messages.SubscribeRequest;
import de.hshannover.f4.trust.visitmeta.exceptions.ifmap.ConnectionEstablishedException;
import de.hshannover.f4.trust.visitmeta.exceptions.ifmap.ConnectionException;
import de.hshannover.f4.trust.visitmeta.exceptions.ifmap.NotConnectedException;
import de.hshannover.f4.trust.visitmeta.interfaces.GraphService;

/**
 * An interface for {@link ConnectionManager} classes, that handle all
 * {@link Connection} instances.
 *
 * @author Bastian Hellmann
 * @author Marcel Reichenbach
 */
public interface ConnectionManager {

	/**
	 * Adds a connection to the connection pool.
	 *
	 * @param connection
	 *            the {@link Connection} to add
	 * @throws ConnectionException
	 */
	public void addConnection(Connection c) throws ConnectionException;

	/**
	 * Connect to a MAP-Server.
	 *
	 * @param connectionName
	 *            the connection name.
	 * @throws ConnectionException
	 */
	public void connect(String connectionName)
			throws ConnectionEstablishedException, ConnectionException;

	/**
	 * Constructs a new {@link Connection}.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @param url
	 *            URL of the {@link Connection}
	 * @param userName
	 *            username of the user for the new {@link Connection}
	 * @param userPassword
	 *            password of the user for the new {@link Connection}
	 * @return
	 * @throws ConnectionException
	 */
	public Connection createConnection(String connectionName, String url,
			String userName, String userPassword) throws ConnectionException;

	/**
	 * Delete a subscription.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @param subscriptionName
	 *            name of the subscription.
	 * @throws ConnectionException
	 */
	public void deleteSubscription(String connectionName,
			String subscriptionName) throws ConnectionException;

	/**
	 * Deletes all active subscriptions.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @throws ConnectionException
	 */
	public void deleteAllSubscriptions(String connectionName)
			throws ConnectionException;

	/**
	 * Close a connection to the MAP-Server.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @throws ConnectionException
	 */
	public void disconnect(String connectionName) throws NotConnectedException,
	ConnectionException;

	/**
	 * Checks if the given {@link Connection} exists in the list of saved
	 * connections.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @return
	 */
	public boolean doesConnectionExist(String connectionName);

	/**
	 * Returns all active subscriptions for a given {@link Connection}.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @return a Set<String> with the active subscriptions.
	 * @throws ConnectionException
	 */
	public Set<String> getActiveSubscriptions(String connectionName)
			throws ConnectionException;

	/**
	 * Returns all saved connections as a String {@link Set}.
	 *
	 * @return a Set<String> with all saved subscriptions.
	 */
	public Map<String, Connection> getSavedConnections();

	/**
	 * Returns the {@link GraphService} for a given {@link Connection}.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @return the {@link GraphService} for a given {@link Connection}
	 * @throws ConnectionException
	 */
	public GraphService getGraphService(String connectionName)
			throws ConnectionException;

	/**
	 * Stores a {@link Connection} in the connection pool and in the
	 * connections-configuration file.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @param subscription
	 *            a {@link JSONObject} representing the subscription
	 * @throws IOException
	 */
	public void storeSubscription(String connectionName, JSONObject subscription)
			throws IOException;

	/**
	 * Delete a saved {@link Connection}.
	 *
	 * @param c
	 *            a {@link Connection} instance
	 * @throws ConnectionException
	 */
	public void removeConnection(Connection c) throws ConnectionException;

	/**
	 * Tries to connect all saved {@link Connection}s to the MAP server that
	 * have the flag for <i>connect at startup</i> set.
	 *
	 * @throws ConnectionException
	 */
	public void executeStartupConnections() throws ConnectionException;

	/**
	 * Send a ifmapj {@link SubscribeRequest} to the MAP server.
	 *
	 * @param connectionName
	 *            name of the {@link Connection}
	 * @param request
	 *            the ifmapj {@link SubscribeRequest}.
	 * @throws ConnectionException
	 */
	public void subscribe(String connectionName, SubscribeRequest request)
			throws ConnectionException;

}
