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
 * This file is part of visitmeta-dataservice, version 0.5.2,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2012 - 2015 Trust@HsH
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

package de.hshannover.f4.trust.visitmeta.dataservice.persistence.inmemory;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalIdentifier;
import de.hshannover.f4.trust.visitmeta.persistence.inmemory.InMemoryIdentifier;
import de.hshannover.f4.trust.visitmeta.persistence.inmemory.InMemoryIdentifierGraph;

public class InMemoryIdentifierGraphTest {
	private static final String TYPE_ROOT = "ROOT";
	private static final String TYPE_ONE = "ONE";
	private static final String TYPE_TWO = "TWO";
	private static final String TYPE_THREE = "THREE";
	InMemoryIdentifierGraph graph;
	InternalIdentifier root;
	InternalIdentifier id1;
	InternalIdentifier id2;
	InternalIdentifier id3;
	InternalIdentifier id4;
	long IM_TIMESTAMP = 666;
	int NUM_IDENTIFIERS = 4;



	@Before
	public void setup() {
		graph = new InMemoryIdentifierGraph(IM_TIMESTAMP);

		root = graph.insert(new InMemoryIdentifier(TYPE_ROOT));
		id1  = new InMemoryIdentifier(TYPE_ONE);
		id2  = new InMemoryIdentifier(TYPE_TWO);
		id3  = new InMemoryIdentifier(TYPE_THREE);

	}


	@Test
	public void testRoot() {
		graph.insert(root);
		assertEquals(TYPE_ROOT, root.getTypeName());
	}

	@Test
	public void testGetIdentifiers() {
		graph.insert(root);
		graph.insert(id1);
		graph.insert(id2);
		assertEquals(NUM_IDENTIFIERS-1, graph.getIdentifiers().size());
		graph.insert(id3);
		assertEquals(NUM_IDENTIFIERS, graph.getIdentifiers().size());
	}

}
