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
package de.hshannover.f4.trust.visitmeta.persistence.neo4j;

import static de.hshannover.f4.trust.visitmeta.persistence.neo4j.Neo4JPropertyConstants.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalIdentifier;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalIdentifierPair;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalLink;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalMetadata;

public class Neo4JLink extends InternalLink {

	private Neo4JRepository mRepo;
	private Node mMe;

	/**
	 * Create a link node in the database.
	 * 
	 * @param n
	 *            The node in memory.
	 * @param graph
	 *            The graph where to create this node.
	 */
	public Neo4JLink(Node n, Neo4JRepository graph) {
		super();
		Transaction tx = graph.beginTx();
		try {
			if (!n.hasLabel(Neo4JTypeLabels.LINK)) {
				String msg = "Trying to construct Link without LINK Label"
						+ ". We clearly disapprove and will die ungracefully now";
				throw new RuntimeException(msg);
			}
			mMe = n;
			mRepo = graph;
			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public InternalIdentifierPair getIdentifiers() {
		Transaction tx = mRepo.beginTx();
		InternalIdentifierPair result = null;
		try {
			Iterator<Relationship> it = mMe.getRelationships(LinkTypes.Link)
					.iterator();

			InternalIdentifier id1 = mRepo.getIdentifier(it.next()
					.getStartNode().getId());
			InternalIdentifier id2 = mRepo.getIdentifier(it.next()
					.getStartNode().getId());

			if (id1.hashCode() > id2.hashCode())
				result = new InternalIdentifierPair(id1, id2);
			else
				result = new InternalIdentifierPair(id2, id1);
			tx.success();
			return result;
		} finally {
			tx.finish();
		}
	}

	public Node getNode() {
		return mMe;
	}

	@Override
	public List<InternalMetadata> getMetadata() {
		Transaction tx = mRepo.beginTx();
		try {
			Iterator<Relationship> i = mMe.getRelationships(LinkTypes.Meta)
					.iterator();
			List<InternalMetadata> lm = new ArrayList<>();

			while (i.hasNext()) {
				Relationship r = i.next();
				InternalMetadata metadata = mRepo.getMetadata(r.getEndNode()
						.getId());
				lm.add(metadata);
			}
			tx.success();
			return lm;
		} finally {
			tx.finish();
		}
	}

	public String getHash() {
		return this.getProperty(KEY_HASH);
	}

	/**
	 * Adds a given Metadata to the Link
	 * 
	 * @param Metadata
	 *            to add
	 */
	@Override
	public void addMetadata(InternalMetadata m) {
		Neo4JMetadata metadata = (Neo4JMetadata) mRepo.insert(m);
		mRepo.connectMeta(this, metadata);
	}

	/**
	 * Removes a given Metadata from the Link
	 * 
	 * @param Metadata
	 *            to remove
	 */
	@Override
	public void removeMetadata(InternalMetadata meta) {
		Transaction tx = mRepo.beginTx();
		try {
			for (Relationship r : mMe.getRelationships(LinkTypes.Meta)) {
				Neo4JMetadata neo4jMetadata = (Neo4JMetadata) mRepo
						.getMetadata(r.getEndNode().getId());
				if (meta.equalsForLinks(neo4jMetadata)) {
					mRepo.remove(neo4jMetadata.getNode().getId());
					break;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
	}

	/**
	 * Updates the Metadata if it is a SingleValue Metadata and if (and only
	 * if!) an old node exists in the graph.
	 * 
	 * @param SingleValue
	 *            Metadata that should be updated
	 */
	@Override
	public void updateMetadata(InternalMetadata meta) {
		Transaction tx = mRepo.beginTx();
		try {
			for (Relationship r : mMe.getRelationships(LinkTypes.Meta)) {
				Neo4JMetadata n4jm = (Neo4JMetadata) mRepo.getMetadata(r
						.getEndNode().getId());
				if (n4jm.equalsForLinks(meta)) {
					Neo4JMetadata newM = (Neo4JMetadata) mRepo.updateMetadata(
							n4jm, meta);
					mRepo.connectMeta(this, newM);
					break;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
	}

	public String getProperty(String name) {
		Transaction tx = mRepo.beginTx();
		String result = null;
		try {
			result = (String) mMe.getProperty(name);
			tx.success();
		} finally {
			tx.finish();
		}
		return result;
	}

	/**
	 * Check if this link has the given metadata connected to it.
	 * 
	 * @param Metadata
	 *            to check for
	 */
	@Override
	public boolean hasMetadata(InternalMetadata meta) {
		Transaction tx = mRepo.beginTx();
		boolean result = false;
		try {
			for (Relationship r : mMe.getRelationships(LinkTypes.Meta)) {
				Neo4JMetadata neo4jMetadata = (Neo4JMetadata) mRepo
						.getMetadata(r.getEndNode().getId());
				if (meta.equalsForLinks(neo4jMetadata)) {
					result = true;
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}
		return result;
	}

	/**
	 * Removes every Metadata from the Link
	 */
	@Override
	public void clearMetadata() {
		Transaction tx = mRepo.beginTx();
		try {
			for (Relationship r : mMe.getRelationships(LinkTypes.Meta)) {
				mRepo.remove(r.getEndNode().getId());
			}
			tx.success();
		} finally {
			tx.finish();
		}
	}
}
