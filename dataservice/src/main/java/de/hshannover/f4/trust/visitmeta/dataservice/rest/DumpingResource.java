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
 * This file is part of visitmeta dataservice, version 0.0.3,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2013 Trust@FHH
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



import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.visitmeta.ifmap.ConnectionManager;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.ActiveDumpingException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.ConnectionException;
import de.hshannover.f4.trust.visitmeta.ifmap.exception.NoActiveDumpingException;


@Path("{connectionName}/dump")
public class DumpingResource {

	private static final Logger log = Logger.getLogger(DumpingResource.class);

	/**
	 * Start the Dumping-Service.
	 * 
	 * !! Dumping is NOT IF-MAP 2.0 compliant an can only be used with irond. !!
	 * 
	 * Example-URL: <tt>http://example.com:8000/default/dump/start</tt>
	 */
	@PUT
	@Path("start")
	public Response startDump(@PathParam("connectionName") String name) {
		try {

			ConnectionManager.deleteSubscriptionsFromConnection(name);
			ConnectionManager.startDumpingServiceFromConnection(name);

		} catch (ActiveDumpingException e){
			log.error("error while startDump from " + name, e);
			return Response.ok().entity("INFO: Dumping allready started").build();
		} catch (ConnectionException e) {
			log.error("error while startDump from " + name, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		}

		return Response.ok().entity("INFO: dumping starts successfully").build();
	}

	/**
	 * Stop the Dumping-Service.
	 * 
	 * !! Dumping is NOT IF-MAP 2.0 compliant an can only be used with irond. !!
	 * 
	 * Example-URL: <tt>http://example.com:8000/default/dump/stop</tt>
	 */
	@PUT
	@Path("stop")
	public Response stopDump(@PathParam("connectionName") String name) {
		try{

			ConnectionManager.stopDumpingServiceFromConnection(name);

		} catch (NoActiveDumpingException e){
			log.error("error while stopDump from " + name, e);
			return Response.ok().entity("INFO: Dumping allready stoped").build();
		} catch (ConnectionException e) {
			log.error("error while stopDump from " + name, e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		};

		return Response.ok().entity("INFO: dumping stop successfully").build();
	}
}
