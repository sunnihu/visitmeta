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
 * This file is part of visitmeta-visualization, version 0.4.1,
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
package de.hshannover.f4.trust.visitmeta.gui.nodeinformation;

/**
 * Factory class for {@link NodeInformationStrategy}.
 * 
 * @author Bastian Hellmann
 *
 */
public class NodeInformationStrategyFactory {

	/**
	 * Returns a {@link NodeInformationStrategy} instance
	 * based on the given {@link NodeInformationStrategyType}.
	 * 
	 * @param type {@link NodeInformationStrategyType}
	 * @return {@link NodeInformationStrategy} instance for the given {@link NodeInformationStrategyType}
	 */
	public static NodeInformationStrategy create(NodeInformationStrategyType type) {
		switch (type) {
		case PLAIN_XML:
			return new NodeInformationPlainXML();
		case XML_BREAKDOWN:
			return new NodeInformationXMLBreakdown();
		default:
			throw new IllegalArgumentException("No strategy for given type '" + type + "'");
		}
	}

}
