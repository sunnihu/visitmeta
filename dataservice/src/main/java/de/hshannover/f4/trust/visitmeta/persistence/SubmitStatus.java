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

public class SubmitStatus {

	private int mMetadataNumber;

	private int mAddedMetadata;

	private int mUpdatedMetadata;

	private int mRemovedMetadata;

	/**
	 * Metadata SubmitStatus
	 * 
	 * @param max metadata number
	 * @param metadataNumber
	 */
	public SubmitStatus(int metadataNumber) {
		mMetadataNumber = metadataNumber;
		mAddedMetadata = 0;
		mUpdatedMetadata = 0;
		mRemovedMetadata = 0;
	}

	/**
	 * Increased the ADDED-counter by one
	 * 
	 * @return long The actual counter value
	 */
	public int added() {
		mAddedMetadata++;
		return mAddedMetadata;
	}

	/**
	 * Increased the UPDATED-counter by one
	 * 
	 * @return long The actual counter value
	 */
	public int updated() {
		mUpdatedMetadata++;
		return mUpdatedMetadata;
	}

	/**
	 * Increased the REMOVED-counter by one
	 * 
	 * @return long The actual counter value
	 */
	public int removed() {
		mRemovedMetadata++;
		return mRemovedMetadata;
	}

	/**
	 * Resets the ADDED-counter UPDATED-counter and IGNORED-counter to 0.
	 */
	public void resetCounter() {
		mAddedMetadata = 0;
		mUpdatedMetadata = 0;
		mRemovedMetadata = 0;
	}

	/**
	 * Get the actual SubmitStatus.
	 * 
	 * @return String with ADDED-counter UPDATED-counter and IGNORED-counter
	 */
	public String getStatus() {
		return "\t ADDED(" + mAddedMetadata + ") UPDATED(" + mUpdatedMetadata + ") REMOVED(" + mRemovedMetadata
				+ ") IGNORED(" + (mMetadataNumber - mAddedMetadata - mUpdatedMetadata - mRemovedMetadata)
				+ ") Metadata";
	}

	/**
	 * Same as getStatus()
	 */
	@Override
	public String toString() {
		return getStatus();
	}
}
