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
 * This file is part of visitmeta-dataservice, version 0.6.0,
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
package de.hshannover.f4.trust.visitmeta.persistence;

import de.hshannover.f4.trust.visitmeta.interfaces.GraphType;

/**
 * Interface for deeper queries against the underlying data.
 * 
 * @author Johannes Busch
 * 
 */
public interface Executor {

	/**
	 * Returns a number of all current elements in the graph. Elements are
	 * specified by the given type.
	 * 
	 * @return
	 */
	public long count(GraphType type);

	/**
	 * Returns a number of all elements in the graph at the given timestamp.
	 * Elements are specified by the given type.
	 * 
	 * @param type
	 * @param timestamp
	 * @return
	 */
	public long count(GraphType type, long timestamp);

	/**
	 * Returns a number of all elements in the graph in the given delta.
	 * Elements are specified by the given type.
	 * 
	 * @param type
	 * @param from
	 * @param to
	 * @return
	 */
	public long count(GraphType type, long from, long to);

	/**
	 * Returns the mean of edges in the graph. Beware: It does not count actual
	 * links. It counts metadata on a given link. If one link has several
	 * metadata, the link will be counted multiple times!
	 * 
	 * @return
	 */
	public double meanOfEdges();

	/**
	 * Returns the mean of edges in the graph. Beware: It does not count actual
	 * links. It counts metadata on a given link. If one link has several
	 * metadata, the link will be counted multiple times!
	 * 
	 * @return
	 */
	public double meanOfEdges(long timestamp);
}
