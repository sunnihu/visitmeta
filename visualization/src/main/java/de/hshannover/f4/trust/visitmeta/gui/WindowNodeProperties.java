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
 * This file is part of visitmeta visualization, version 0.1.0,
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
package de.hshannover.f4.trust.visitmeta.gui;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JWindow;
import javax.swing.Timer;

import org.apache.log4j.Logger;

/**
 * A Window that shows the properties of a node.
 */
public class WindowNodeProperties extends JWindow {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(WindowNodeProperties.class);
	PanelXmlTree mPanel = null;

	/**
	 * 
	 * @param owner
	 * @param controller
	 */
	public WindowNodeProperties(Frame owner) {
		super(owner);
		mPanel = new PanelXmlTree();
		mPanel.setPreferredSize(new Dimension(250, 200));
		add(mPanel);
		pack();
	}

	/**
	 * Fill the window with a tree generated of XML data.
	 * 
	 * @param pXml
	 *            the XML data to show in a tree.
	 * @param pTimer
	 *            the timer to hide the window.
	 */
	public void fill(String pXml, Timer pTimer) {
		LOGGER.trace("Method fill(" + pXml + ") called.");
		mPanel.fill(pXml, pTimer);
	}
}
