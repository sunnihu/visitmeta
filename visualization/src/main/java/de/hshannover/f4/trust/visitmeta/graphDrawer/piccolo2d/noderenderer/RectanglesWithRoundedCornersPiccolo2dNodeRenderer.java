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

import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.interfaces.Metadata;

/**
 * {@link Piccolo2dNodeRenderer} that draws nodes as rectangles with rounded corners.
 *
 * @author Bastian Hellmann
 *
 */
public class RectanglesWithRoundedCornersPiccolo2dNodeRenderer implements Piccolo2dNodeRenderer {

	private static float mArcWidth = 20.0f;
	private static float mArcHeight = 20.0f;
	private static float mOffsetWidth = 10.0f;
	private static float mOffsetHeight = 10.0f;

	/**
	 * Creates a {@link PPath} object as a rectangle that has rounded corners.
	 *
	 * @param text
	 *            the {@link PText} object
	 * @return a {@link PPath} object
	 */
	public static PPath createNode(PText text) {
		return PPath.createRoundRectangle(-5
				- 0.5F
						* (float) text.getWidth(), // x
				-5
						- 0.5F
								* (float) text.getHeight(), // y
				(float) text.getWidth()
						+ mOffsetWidth, // width + offset
				(float) text.getHeight()
						+ mOffsetHeight, // height + offset
				mArcWidth, // arcWidth
				mArcHeight // arcHeight
		);
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
