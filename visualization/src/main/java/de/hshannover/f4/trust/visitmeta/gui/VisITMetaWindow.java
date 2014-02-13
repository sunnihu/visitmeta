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
 * This file is part of visitmeta visualization, version 0.0.3,
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.visitmeta.datawrapper.PropertiesManager;

public class VisITMetaWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(VisITMetaWindow.class);

	private JMenuBar mMenuBar = null;
	private JSplitPane mMainSplitPane = null;
	private JPanel mLeftMainPanel = null;
	private JPanel mRightMainPanel = null;
	private JTabbedPane mTabbedConnectionPane = null;
	private DefaultTableModel mTableModel = null;
	private JTable mConnectionTable = null;
	private HashSet<ConnectionTab> mConnectionTabs = null;
	private JScrollPane mConnectionScrollPane = null;
	private GuiController mGuiController = null;

	/**
	 * 
	 * @param guiController
	 */
	public VisITMetaWindow(GuiController guiController) {
		super("VisITmeta");
		this.mGuiController = guiController;
		mMenuBar = new MenuBar(mGuiController);
		this.setJMenuBar(mMenuBar);
		this.mConnectionTabs = new HashSet<ConnectionTab>();

		init();
	}

	/**
	 * 
	 * @param name
	 * @param graphPanel
	 */
	public void addConnection(String name, JComponent graphPanel) {
		Object[] tmpTab = new Object[1];
		tmpTab[0] = new ConnectionTab(name, graphPanel);
		this.mConnectionTabs.add((ConnectionTab) tmpTab[0]);
		this.mTableModel.addRow(tmpTab);
	}

	/**
	 * Removes a connection
	 * 
	 * @param connection
	 *            the should be removed
	 */
	public void removeConnection(ConnectionTab connection) {
		for (int i = 0; i < mTableModel.getRowCount(); i++) {
			if (mTableModel.getValueAt(i, 0).equals(connection)) {
				mTableModel.removeRow(i);
				mConnectionTabs.remove(connection);
				break;
			}
		}
	}

	/**
	 * Removes a given connection
	 * 
	 * @param name
	 *            of the connection that should be removed
	 */
	public void removeConnection(String name) {
		this.removeConnection(new ConnectionTab(name, null));
	}

	/**
	 * Initializes the main panel
	 */
	private void init() {
		this.setLookAndFeel();
		this.setMinimumSize(new Dimension(800, 600));

		initLeftHandSide();
		initRightHandSide();

		mMainSplitPane = new JSplitPane();
		mMainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		mMainSplitPane.setRightComponent(mRightMainPanel);
		mMainSplitPane.setLeftComponent(mLeftMainPanel);

		this.getContentPane().add(mMainSplitPane);

		loadProperties();
		setCloseOperation();
	}

	/**
	 * Initializes the left hand side of the main panel
	 */
	private void initLeftHandSide() {
		mTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		mTableModel.addColumn("Connections");
		mConnectionTable = new JTable(mTableModel);
		mConnectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		DefaultTableCellRenderer iconCellRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setValue(Object value) {
				if (value instanceof ConnectionTab) {
					ConnectionTab tmpTab = ((ConnectionTab) value);
					setText(tmpTab.toString());
					setIcon(tmpTab.getStatusImage());
				} else {
					super.setValue(value);
				}
			}
		};

		iconCellRenderer.setHorizontalAlignment(JLabel.LEFT);

		mConnectionTable.setDefaultRenderer(Object.class, iconCellRenderer);

		mConnectionTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				ConnectionTab tab = (ConnectionTab) mConnectionTable.getValueAt(mConnectionTable.getSelectedRow(), 0);
				boolean alreadyOpen = false;
				for (Component t : mTabbedConnectionPane.getComponents()) {
					if (t.equals(tab)) {
						alreadyOpen = true;
					}
				}
				if (!alreadyOpen) {
					mTabbedConnectionPane.add(tab.getConnName(), tab);
				} else {
					mTabbedConnectionPane.setSelectedComponent(tab);
				}
			}
		});

		mConnectionTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					int r = mConnectionTable.rowAtPoint(e.getPoint());
					if (r >= 0 && r < mConnectionTable.getRowCount()) {
						mConnectionTable.setRowSelectionInterval(r, r);
					} else {
						mConnectionTable.clearSelection();
					}
					if (e.getButton() == MouseEvent.BUTTON3) {
						new ConnectionTabListMenu((ConnectionTab) mConnectionTable.getValueAt(r, 0)).show(
								mConnectionTable, e.getX(), e.getY());
					}
				} catch (ArrayIndexOutOfBoundsException ae) {
					System.out.print("");
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		mConnectionScrollPane = new JScrollPane(mConnectionTable);
		mConnectionScrollPane.setPreferredSize(new Dimension(120, 0));
		mTableModel.fireTableDataChanged();

		mLeftMainPanel = new JPanel();
		mLeftMainPanel.setLayout(new GridLayout());
		mLeftMainPanel.add(mConnectionScrollPane);
	}

	/**
	 * Initializes the right hand side of the main panel
	 */
	private void initRightHandSide() {
		mTabbedConnectionPane = new JTabbedPane();

		mRightMainPanel = new JPanel();
		mRightMainPanel.setLayout(new GridLayout());
		mRightMainPanel.add(mTabbedConnectionPane);
	}

	/**
	 * Loads Properties
	 */
	private void loadProperties() {
		setTitle(PropertiesManager.getProperty("window", "title", "VisITMeta"));
		setLocation(Integer.parseInt(PropertiesManager.getProperty("window", "position.x", "0")),
				Integer.parseInt(PropertiesManager.getProperty("window", "position.y", "0")));
		setPreferredSize(new Dimension(Integer.parseInt(PropertiesManager.getProperty("window", "width", "700")),
				Integer.parseInt(PropertiesManager.getProperty("window", "height", "700"))));
	}

	/**
	 * Adds a listener to the window in order to save the windows' position and
	 * size and sets the close operation
	 */
	private void setCloseOperation() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				PropertiesManager.storeProperty("window", "position.x",
						String.valueOf((int) getLocationOnScreen().getX()));
				PropertiesManager.storeProperty("window", "position.y",
						String.valueOf((int) getLocationOnScreen().getY()));
				PropertiesManager.storeProperty("window", "width", String.valueOf(getWidth()));
				PropertiesManager.storeProperty("window", "height", String.valueOf(getHeight()));
				System.exit(0);
			}
		});
	}

	/**
	 * Set the look and feel of java depending on the operating system.
	 */
	private void setLookAndFeel() {
		LOGGER.trace("Method setLookAndFeel() called.");
		try {
			switch (System.getProperty("os.name").toLowerCase()) {
			case "win": // Windows
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				break;
			case "linux": // Linux
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				break;
			// case "nix": // Unix
			// break;
			// case "mac": // Mac
			// break;
			// case "sunos": // Solaris
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// break;
			default:
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				break;
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}