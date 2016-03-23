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
package de.hshannover.f4.trust.visitmeta.ifmap;

import java.util.List;

import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalIdentifier;
import de.hshannover.f4.trust.visitmeta.dataservice.internalDatatypes.InternalMetadata;

/**
 * Represents one ResultItem as specified by the IF-MAP protocol.
 */
public class ResultItem {

	private final InternalIdentifier mId1;
	private final InternalIdentifier mId2;

	private final List<InternalMetadata> mMetadata;

	private final boolean mIsLinkUpdate;
	private final ResultItemTypeEnum mType;

	public ResultItem(InternalIdentifier id1, InternalIdentifier id2, List<InternalMetadata> metadata,
			ResultItemTypeEnum type) {
		super();
		mId1 = id1;
		mId2 = id2;
		mMetadata = metadata;
		mType = type;

		if (id1 == null) {
			throw new IllegalArgumentException("id1 cannot be null");
		}

		if (id1 != null && id2 != null) {
			mIsLinkUpdate = true;
		} else {
			mIsLinkUpdate = false;
		}
	}

	public InternalIdentifier getId1() {
		return mId1;
	}

	public InternalIdentifier getId2() {
		return mId2;
	}

	public List<InternalMetadata> getMetadata() {
		return mMetadata;
	}

	public ResultItemTypeEnum getType() {
		return mType;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof ResultItem)) {
			return false;
		}

		ResultItem otherItem = (ResultItem) other;

		if (mIsLinkUpdate != otherItem.mIsLinkUpdate) {
			return false;
		}

		if (mType != otherItem.mType) {
			return false;
		}

		if (getId1() == null) {
			if (otherItem.getId1() != null) {
				return false;
			}
		} else if (!getId1().equals(otherItem.getId1())) {
			return false;
		}

		if (getId2() == null) {
			if (otherItem.getId2() != null) {
				return false;
			}
		} else if (!getId2().equals(otherItem.getId2())) {
			return false;
		}

		if (!getMetadata().equals(otherItem.getMetadata())) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("ResultItem(");
		s.append(mId1);
		s.append(" -- ");
		for (InternalMetadata m : mMetadata) {
			s.append(m);
			s.append(" ");
		}
		s.append("-- ");
		s.append(mId2);
		s.append("[");
		s.append(mIsLinkUpdate);
		s.append("])");

		return s.toString();
	}

}
