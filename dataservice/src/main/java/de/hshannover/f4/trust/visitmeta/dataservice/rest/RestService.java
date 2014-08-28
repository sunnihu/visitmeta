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
 * This file is part of visitmeta dataservice, version 0.1.2,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2013 Trust@HsH
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
package de.hshannover.f4.trust.visitmeta.dataservice.rest;



import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import de.hshannover.f4.trust.visitmeta.dataservice.Application;
import de.hshannover.f4.trust.visitmeta.dataservice.ApplicationFacade;
import de.hshannover.f4.trust.visitmeta.dataservice.util.ConfigParameter;


public class RestService implements Runnable {


	private static final Logger log = Logger.getLogger(RestService.class);

	private final String url  = Application.getDSConfig().getProperty(ConfigParameter.DS_REST_URL);

	private final Map<String, String> params = new HashMap<String, String>();


	@Override
	public void run() {
		log.debug("run() ...");

		params.put("com.sun.jersey.config.property.packages", "de.hshannover.f4.trust.visitmeta.dataservice.rest");

		log.info("starting REST service on "+url+"...");

		try {
//			ResourceConfig rc = new ResourceConfig().packages(
//					"com.sun.jersey.config.property.packages", "de.hshannover.f4.trust.visitmeta.dataservice.rest");
			HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(url), new ApplicationFacade( /* dependecies ... */ ));

			log.debug("REST service running.");
			// TODO shutdown server properly

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try {

			synchronized (this){
				wait(); // FIXME
			}

		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

		log.debug("... run()");
	}
}
