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
 * This file is part of visitmeta-visualization, version 0.6.0,
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
package de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d.noderenderer;

import org.piccolo2d.nodes.PPath;
import org.piccolo2d.nodes.PText;
import org.piccolo2d.util.PBounds;

import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;

/**
 * {@link Piccolo2dNodeRenderer} that draws nodes as ellipses.
 *
 * @author Bastian Hellmann
 *
 */
public class EllipsePiccolo2dNodeRenderer implements Piccolo2dNodeRenderer {

	private static double mGlowWidth = 80.0;
	private static double mGlowHeight = 40.0;

	/**
	 * Creates a new {@link PPath} object as a ellipse.
	 *
	 * @param text
	 *            the {@link PText} object
	 * @return a {@link PPath} object
	 */
	public static PPath createNode(PText text) {
		PBounds bounds = text.getFullBoundsReference();
		float width = (float) (bounds.getWidth()
				+ mGlowWidth);
		float height = (float) (bounds.getHeight()
				+ mGlowHeight);

		PPath result = PPath.createEllipse(-0.5F
				* width, // x
				-0.5F
						* height, // y
				width, // width
				height // height
		);

		return result;
	}

	@Override
	public PPath createNode(Identifier identifier, PText text) {
		return createNode(text);
	}

	@Override
	public PPath createNode(Metadata metadata, PText text) {
		return createNode(text);
	}
}
