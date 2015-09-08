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
package de.hshannover.f4.trust.visitmeta.persistence;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.visitmeta.dataservice.graphservice.NamespaceHolder;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalIdentifier;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalLink;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalMetadata;
import de.hshannover.f4.trust.visitmeta.ifmap.PollResult;
import de.hshannover.f4.trust.visitmeta.ifmap.ResultItem;

public abstract class AbstractWriter implements Writer {

	protected Repository mRepo;
	protected Long mLastTimestamp;
	protected String mConnectionName;

	private static final Logger log = Logger.getLogger(AbstractWriter.class);

	public abstract void finishTransaction();

	public abstract void beginTransaction();

	protected abstract void submitUpdate(List<InternalMetadata> meta);

	protected abstract void submitDelete(int n);

	protected void savePrexif(InternalMetadata m) {
		String prefix = null, value = null;
		for (String key : m.getProperties()) {
			if (key.contains("@xmlns")) {
				prefix = key.substring(key.indexOf("@xmlns") + 7,
						key.length() - 1);
				value = m.valueFor(key);
			}
		}
		if (prefix != null && value != null) {
			NamespaceHolder.addPrefix(mConnectionName, prefix, value);
		}
	}

	@Override
	public void submitPollResult(PollResult pr) {
		beginTransaction();
		for (ResultItem resultItem : pr.getResults()) {
			switch (resultItem.getType()) {
			case UPDATE:
			case SEARCH:
			case NOTIFY:
				if (resultItem.getId1() == null) {
					submitUpdate(resultItem.getId2(), resultItem.getMetadata());
				} else if (resultItem.getId2() == null) {
					submitUpdate(resultItem.getId1(), resultItem.getMetadata());
				} else {
					submitUpdate(resultItem.getId1(), resultItem.getId2(),
							resultItem.getMetadata());
				}
				break;
			case DELETE:
				if (resultItem.getId1() == null) {
					submitDelete(resultItem.getId2(), resultItem.getMetadata());
				} else if (resultItem.getId2() == null) {
					submitDelete(resultItem.getId1(), resultItem.getMetadata());
				} else {
					submitDelete(resultItem.getId1(), resultItem.getId2(),
							resultItem.getMetadata());
				}
				break;
			default:
				break;
			}
		}

		finishTransaction();
	}

	protected void submitUpdate(InternalIdentifier id,
			List<InternalMetadata> meta) {
		log.debug("Got update for a single identifier");
		InternalIdentifier in = mRepo.findIdentifier(id);
		List<InternalMetadata> unique = new ArrayList<InternalMetadata>();
		SubmitStatus status = new SubmitStatus(meta.size());

		if (in == null) {
			in = mRepo.insert(id);
			for (InternalMetadata m : meta) {
				in.addMetadata(m);
				unique.add(m);
				status.added();
			}
		} else {
			for (InternalMetadata m : meta) {
				if (!in.hasMetadata(m)) {
					in.addMetadata(m);
					unique.add(m);
					status.added();
				} else {
					if (m.isSingleValue()) {
						if (!in.equalsSingleValue(m)) {
							in.updateMetadata(m);
							unique.add(m);
							status.updated();
						}
					}
				}
			}
		}
		this.submitUpdate(unique);
		log.debug(status);
		log.trace("Added update for a single identifier");
	}

	protected void submitUpdate(InternalIdentifier id1, InternalIdentifier id2,
			List<InternalMetadata> meta) {
		log.debug("Persistance got update for two identifiers");
		InternalIdentifier idGraph1 = mRepo.findIdentifier(id1);
		InternalIdentifier idGraph2 = mRepo.findIdentifier(id2);
		InternalLink l;
		List<InternalMetadata> unique = new ArrayList<InternalMetadata>();
		SubmitStatus status = new SubmitStatus(meta.size());

		if (idGraph1 != null && idGraph2 != null) {
			// both identifiers are in the graph, this means we either have to update the metadata
			// on the link between them or establish a new link between them
			log.trace("Found both identifier in the graph");
			l = mRepo.findCommonLink(idGraph1, idGraph2);
			if (l == null) {
				l = mRepo.connect(idGraph1, idGraph2);
			}
			for (InternalMetadata m : meta) {
				if (!l.hasMetadata(m)) {
					l.addMetadata(m);
					unique.add(m);
					status.added();
				} else {
					if (m.isSingleValue()) {
						if (!l.equalsSingleValue(m)) {
							l.updateMetadata(m);
							unique.add(m);
							status.updated();
						}
					}
				}
			}
		} else if (idGraph1 != null && idGraph2 == null) {
			// add link to idGraph1 with metadata
			log.trace("Found one identifier in the graph");
			idGraph2 = mRepo.insert(id2);
			l = mRepo.connect(idGraph1, idGraph2);
			for (InternalMetadata m : meta) {
				l.addMetadata(m);
				unique.add(m);
				status.added();
			}
		} else if (idGraph1 == null && idGraph2 != null) {
			// add link to idGraph2 with metadata
			log.trace("Found one identifier in the graph");
			idGraph1 = mRepo.insert(id1);
			l = mRepo.connect(idGraph1, idGraph2);
			for (InternalMetadata m : meta) {
				l.addMetadata(m);
				unique.add(m);
				status.added();
			}
		} else {
			// both links are not in the graph
			idGraph1 = mRepo.insert(id1);
			idGraph2 = mRepo.insert(id2);
			l = mRepo.connect(idGraph1, idGraph2);
			for (InternalMetadata m : meta) {
				l.addMetadata(m);
				unique.add(m);
				status.added();
			}

		}
		this.submitUpdate(unique);
		log.debug(status);
		log.trace("Added update for two identifiers");
	}

	protected void submitDelete(InternalIdentifier id1, InternalIdentifier id2,
			List<InternalMetadata> meta) {
		log.debug("Persistance got delete for two identifiers");
		InternalIdentifier idGraph1 = mRepo.findIdentifier(id1);
		InternalIdentifier idGraph2 = null;
		InternalLink linkToEdit = null;
		SubmitStatus status = new SubmitStatus(meta.size());
		int n = 0;
		if (idGraph1 == null) {
			throw new RuntimeException(
					"Someone is trying to delete from an non initiated identifier.");
		}
		for (InternalLink l : idGraph1.getLinks()) {
			if (l.getIdentifiers().getFirst().equals(id2)
					|| l.getIdentifiers().getSecond().equals(id2)) {
				linkToEdit = l;
				break;
			}
		}

		if (linkToEdit != null) {
			for (InternalMetadata m : meta) {
				if (linkToEdit.hasMetadata(m)) {
					linkToEdit.removeMetadata(m);
					n++;
					status.removed();
				}
			}
			if (linkToEdit.getMetadata().size() == 0) {
				log.trace("Deleting link " + linkToEdit);
				idGraph1 = linkToEdit.getIdentifiers().getFirst();
				idGraph2 = linkToEdit.getIdentifiers().getSecond();
				mRepo.disconnect(idGraph1, idGraph2);
			}
		} else {
			throw new RuntimeException(
					"Someone is trying to delete from a link that is not in the graph or not connected to the given identifiers. Bad Boy!");
		}
		this.submitDelete(n);
		log.debug(status);
		log.trace("Performed delete for two identifiers");
	}

	protected void submitDelete(InternalIdentifier id,
			List<InternalMetadata> meta) {
		log.debug("Got delete for single identifier");
		InternalIdentifier idGraph = mRepo.findIdentifier(id);
		SubmitStatus status = new SubmitStatus(meta.size());
		int n = 0;
		if (idGraph != null) {
			for (InternalMetadata m : meta) {
				if (idGraph.hasMetadata(m)) {
					idGraph.removeMetadata(m);
					n++;
					status.removed();
				}
			}
		} else {
			throw new RuntimeException(
					"Someone is trying to delete from an identifier that is not in the graph. Bad Boy!");
		}
		this.submitDelete(n);
		log.debug(status);
		log.trace("Performed delete for a single identifier");
	}
}
