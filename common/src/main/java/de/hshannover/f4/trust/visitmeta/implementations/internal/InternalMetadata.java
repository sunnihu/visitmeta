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
 * This file is part of visitmeta-common, version 0.6.0,
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
package de.hshannover.f4.trust.visitmeta.implementations.internal;





import java.util.Collections;
import java.util.List;

import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;
import de.hshannover.f4.trust.visitmeta.interfaces.Propable;

/**
 * Internal representation of one IF-MAP metadata.
 */
public abstract class InternalMetadata implements Propable{
	public static final long METADATA_NOT_DELETED_TIMESTAMP = -1;

	/**
	 * Returns the raw, unparsed XML data used to describe this Metadata. <b>Note: The xml version
	 * and encoding is included.</b>
	 */
	public abstract String getRawData();

	public abstract void addProperty(String name, String value);

	/**
	 * @deprecated use constructor parameter instead
	 * @param timestamp
	 */
	@Deprecated
	public abstract void setPublishTimestamp(long timestamp);
	/**
	 * Returns the timestamp when this metadata was received with a delete operation.
	 *
	 * @return the delete timestamp or
	 *          {@link Metadata.METADATA_NOT_DELETED_TIMESTAMP}
	 *          if this metadata is still valid
	 */
	public abstract long getDeleteTimestamp();

	@Override
	public String toString() {
		StringBuffer tmp = new StringBuffer();
		tmp.append(getTypeName() + "[" + hashCode() + "] Properties[");
		for (String p : getProperties()) {
			tmp.append("(" + p + ", " + valueFor(p) + ")");
		}
		tmp.append("]");
		return tmp.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (! (o instanceof InternalMetadata) ) {
			return false;
		}
		InternalMetadata other = (InternalMetadata) o;
		if (!this.getTypeName().equals(other.getTypeName())) {
			return false;
		}
		if(this.isSingleValue() != other.isSingleValue()) {
			return false;
		}
		if (getProperties().size() != other.getProperties().size()) {
			return false;
		}
		for (String property : getProperties()) {
			String value = valueFor(property);
			if (value == null) {
				if (!(other.valueFor(property) == null))
					return false;
			} else {
				if (!valueFor(property).equals(other.valueFor(property)))
					return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + getTypeName().hashCode();
		List<String> keys = getProperties();
		Collections.sort(keys);
		for (String key : keys) {
			result = prime * result + valueFor(key).hashCode();
		}
		return result;
	}

	/**
	 * @return
	 * True if corresponding IF-MAP metadata is single value, false otherwise.
	 */
	public abstract boolean isSingleValue();
	/**
	 * @return
	 * The (IF-MAP) publish timestamp
	 */
	public abstract long getPublishTimestamp();

	/**
	 * Checks if this metadata is valid at the given timestamp.
	 * Test is performed on the PublishTimestamp and DeleteTimestamp
	 * @param timestamp the timestamp to check
	 * @return the result wether it is valid or not
	 */
	public boolean isValidAt(long timestamp) {
		if(getPublishTimestamp() > timestamp) {
			return false;
		}
		return ((getDeleteTimestamp() == -1) || (timestamp < getDeleteTimestamp()));
	}
}
