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
package de.hshannover.f4.trust.visitmeta.dataservice.graphservice.testcases;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import de.hshannover.f4.trust.visitmeta.interfaces.Delta;
import de.hshannover.f4.trust.visitmeta.interfaces.IdentifierGraph;

public class MultiValueMetadataSimpleTest extends AbstractTestCase {

	private final String FILENAME = TESTCASES_DIRECTORY + File.separator + "MultiValueMetadataSimple.yml";

	@Override
	public String getTestcaseFilename() {
		return FILENAME;
	}

	@Override
	public void getInitialGraph() throws JSONException {
		List<IdentifierGraph> initialGraph = mService.getInitialGraph();

		// ### check IdentifierGraph size ###
		testGraphListSize(initialGraph, 1);

		// ### check Identifiers size ###
		IdentifierGraph graph = initialGraph.get(0);
		testIdentifierCount(graph, 1);

		// ### check JSON-String ###
		JSONArray actual = toJson(initialGraph);
		JSONArray expected = new JSONArray();
		expected.put(createJSON(1L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData1")));

		assertTrue(jsonsEqual(actual, expected));
	}

	@Override
	public void getGraphAt() throws JSONException {
		getGraphAt0();
		getGraphAt1();
		getGraphAt2();
		getGraphAt3();

	}

	private void getGraphAt0() throws JSONException {
		long timestamp = 0;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);

		// ### check IdentifierGraph size ###
		testGraphListSize(graphAt, 0);

		// ### check Identifiers size ###
		// nothing to check, size must be 0

		// ### check JSON-String ###
		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();

		assertTrue(jsonsEqual(actual, expected));
	}

	private void getGraphAt1() throws JSONException {
		long timestamp = 1;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);

		// ### check IdentifierGraph size ###
		testGraphListSize(graphAt, 1);

		// ### check Identifiers size ###
		IdentifierGraph graph = graphAt.get(0);
		testIdentifierCount(graph, 1);

		// ### check JSON-String ###

		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();
		expected.put(createJSON(1L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData1")));

		assertTrue(jsonsEqual(actual, expected));
	}

	private void getGraphAt2() throws JSONException {
		long timestamp = 2;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);

		// ### check IdentifierGraph size ###
		testGraphListSize(graphAt, 1);

		// ### check Identifiers size ###
		IdentifierGraph graph = graphAt.get(0);
		testIdentifierCount(graph, 1);

		// ### check JSON-String ###

		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();
		expected.put(createJSON(2L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData2")));

		assertTrue(jsonsEqual(actual, expected));
	}

	private void getGraphAt3() throws JSONException {
		long timestamp = 3;
		List<IdentifierGraph> graphAt = mService.getGraphAt(timestamp);

		// ### check IdentifierGraph size ###
		testGraphListSize(graphAt, 0);

		// ### check Identifiers size ###
		// nothing to check, size must be 0

		// ### check JSON-String ###
		JSONArray actual = toJson(graphAt);
		JSONArray expected = new JSONArray();

		assertTrue(jsonsEqual(actual, expected));
	}

	@Override
	public void getCurrentGraph() throws JSONException {
		List<IdentifierGraph> currentGraph = mService.getCurrentGraph();

		// ### check IdentifierGraph size ###
		testGraphListSize(currentGraph, 0);

		// ### check Identifiers size ###
		// nothing to check, size must be 0

		// ### check JSON-String ###
		JSONArray actual = toJson(currentGraph);
		JSONArray expected = new JSONArray();

		assertTrue(jsonsEqual(actual, expected));
	}

	@Override
	public void getDelta() throws JSONException {
		getDeltaFrom0To1();
		getDeltaFrom0To2();
		getDeltaFrom0To3();

		getDeltaFrom1To2();
		getDeltaFrom1To3();

		getDeltaFrom2To3();
	}

	private void getDeltaFrom0To1() throws JSONException {
		long t1 = 0;
		long t2 = 1;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> deletes = delta.getDeletes();
		List<IdentifierGraph> updates = delta.getUpdates();

		// ### check Delta sizes for deletes and updates ###
		testDeltaSize(delta, 1, 0);

		// ### check Identifiers size ###
		// check deletes
		// nothing to check, size must be 0

		// check updates
		IdentifierGraph updategraph = updates.get(0);
		testIdentifierCount(updategraph, 1);

		// ### check JSON-String ###
		// check deletes
		JSONArray actualDeletes = toJson(deletes);

		JSONArray expectedDeletes = new JSONArray();
		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));

		// check updates
		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();
		expectedUpdates.put(createJSON(1L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData1")));

		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));
	}

	private void getDeltaFrom0To2() throws JSONException {
		long t1 = 0;
		long t2 = 2;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> deletes = delta.getDeletes();
		List<IdentifierGraph> updates = delta.getUpdates();

		// ### check Delta sizes for deletes and updates ###
		testDeltaSize(delta, 1, 0);

		// ### check Identifiers size ###
		// check deletes
		// nothing to check, size must be 0

		// check updates
		IdentifierGraph updategraph = updates.get(0);
		testIdentifierCount(updategraph, 1);

		// ### check JSON-String ###
		// check deletes
		JSONArray actualDeletes = toJson(deletes);

		JSONArray expectedDeletes = new JSONArray();
		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));

		// check updates
		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();
		expectedUpdates.put(createJSON(2L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData2")));

		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));
	}

	private void getDeltaFrom0To3() throws JSONException {
		long t1 = 0;
		long t2 = 3;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> deletes = delta.getDeletes();
		List<IdentifierGraph> updates = delta.getUpdates();

		// ### check Delta sizes for deletes and updates ###
		testDeltaSize(delta, 0, 0);

		// ### check Identifiers size ###
		// check deletes
		// nothing to check, size must be 0

		// check updates
		// nothing to check, size must be 0

		// ### check JSON-String ###
		// check deletes
		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();

		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));

		// check updates
		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();

		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));
	}

	private void getDeltaFrom1To2() throws JSONException {
		long t1 = 1;
		long t2 = 2;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> deletes = delta.getDeletes();
		List<IdentifierGraph> updates = delta.getUpdates();

		// ### check Delta sizes for deletes and updates ###
		testDeltaSize(delta, 1, 1);

		// ### check Identifiers size ###
		// check deletes
		IdentifierGraph deletegraph = deletes.get(0);
		testIdentifierCount(deletegraph, 1);

		// check updates
		IdentifierGraph updategraph = updates.get(0);
		testIdentifierCount(updategraph, 1);

		// ### check JSON-String ###
		// check deletes
		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();
		expectedDeletes.put(createJSON(2L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData1")));

		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));

		// check updates
		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();
		expectedUpdates.put(createJSON(2L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData2")));

		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));
	}

	private void getDeltaFrom1To3() throws JSONException {
		long t1 = 1;
		long t2 = 3;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> deletes = delta.getDeletes();
		List<IdentifierGraph> updates = delta.getUpdates();

		// ### check Delta sizes for deletes and updates ###
		testDeltaSize(delta, 0, 1);

		// ### check Identifiers size ###
		// check deletes
		IdentifierGraph deletegraph = deletes.get(0);
		testIdentifierCount(deletegraph, 1);

		// check updates
		// nothing to check, size must be 0

		// ### check JSON-String ###
		// check deletes
		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();
		expectedDeletes.put(createJSON(3L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData1")));

		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));

		// check updates
		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();

		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));
	}

	private void getDeltaFrom2To3() throws JSONException {
		long t1 = 2;
		long t2 = 3;
		Delta delta = mService.getDelta(t1, t2);
		List<IdentifierGraph> deletes = delta.getDeletes();
		List<IdentifierGraph> updates = delta.getUpdates();

		// ### check Delta sizes for deletes and updates ###
		testDeltaSize(delta, 0, 1);

		// ### check Identifiers size ###
		// check deletes
		IdentifierGraph deletegraph = deletes.get(0);
		testIdentifierCount(deletegraph, 1);

		// check updates
		// nothing to check, size must be 0

		// ### check JSON-String ###
		// check deletes
		JSONArray actualDeletes = toJson(deletes);
		JSONArray expectedDeletes = new JSONArray();
		expectedDeletes.put(createJSON(3L,
				createJSONIdentifierMetadataConnection("access-request_rawData1", "capability_rawData2")));

		assertTrue(jsonsEqual(actualDeletes, expectedDeletes));

		// check updates
		JSONArray actualUpdates = toJson(updates);
		JSONArray expectedUpdates = new JSONArray();

		assertTrue(jsonsEqual(actualUpdates, expectedUpdates));
	}

	@Override
	public void getChangesMap() throws JSONException {
		SortedMap<Long,Long> changesMap = mService.getChangesMap();

		SortedMap<Long,Long> expectedChanges = new TreeMap<Long,Long>();
		expectedChanges.put(1L, 1L);	// t1
		expectedChanges.put(2L, 2L);	// t2
		expectedChanges.put(3L, 1L);	// t3

		testChangesMap(expectedChanges, changesMap);
		testChangesMapJSON(expectedChanges, changesMap);
	}

}
