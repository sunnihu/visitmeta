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
 * This file is part of visitmeta-visualization, version 0.5.2,
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
package de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d;

import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.visitmeta.graphDrawer.Piccolo2DPanel;
import de.hshannover.f4.trust.visitmeta.interfaces.Propable;

/**
 * EventHandler for clicks in the panel.
 */
public class ClickEventHandler extends MouseAdapter {
	private static final Logger LOGGER = Logger.getLogger(ClickEventHandler.class);
	Piccolo2DPanel mPanel = null;

	public ClickEventHandler(Piccolo2DPanel pPanel) {
		mPanel = pPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		LOGGER.trace("Method mouseClicked("
				+ e + ") called.");
		if (e.getButton() == MouseEvent.BUTTON3) {
			if (e.getClickCount() == 2) {
				mPanel.setFocusToCenter();
			} else {
				Propable mouseOverNode = mPanel.getMouseOverNode();
				if (mouseOverNode != null) {
					mPanel.getConnection().showContextMenu(mouseOverNode, MouseInfo.getPointerInfo().getLocation());
				} else {
					mPanel.getConnection().hideContextMenu();
				}
			}
		} else {
			mPanel.getConnection().hideContextMenu();
		}
	}
}
