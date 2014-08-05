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
package de.hshannover.f4.trust.visitmeta.graphDrawer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.visitmeta.datawrapper.ExpandedLink;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeIdentifier;
import de.hshannover.f4.trust.visitmeta.datawrapper.NodeMetadata;
import de.hshannover.f4.trust.visitmeta.datawrapper.Position;
import de.hshannover.f4.trust.visitmeta.datawrapper.PropertiesManager;
import de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d.ClickEventHandler;
import de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d.NodeEventHandler;
import de.hshannover.f4.trust.visitmeta.graphDrawer.piccolo2d.ZoomEventHandler;
import de.hshannover.f4.trust.visitmeta.gui.GraphConnection;
import de.hshannover.f4.trust.visitmeta.interfaces.Identifier;
import de.hshannover.f4.trust.visitmeta.util.IdentifierHelper;
import de.hshannover.f4.trust.visitmeta.util.IdentifierWrapper;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;
import edu.umd.cs.piccolox.nodes.PComposite;

public class Piccolo2DPanel implements GraphPanel {
	private static final Logger LOGGER = Logger.getLogger(Piccolo2DPanel.class);
	private PLayer mLayerNode = null;
	private PLayer mLayerEdge = null;
	private PLayer mLayerGlow = null;
	private PCanvas mPanel = new PCanvas();
	private HashMap<Object, PComposite> mMapNode = new HashMap<>();
	private HashMap<NodeMetadata, ArrayList<PPath>> mMapEdge = new HashMap<>();
	private int mNodeTranslationDuration = 0;

	private double mAreaOffsetX = 0.0;
	private double mAreaOffsetY = 0.0;
	private double mAreaWidth = 650.0;
	private double mAreaHeight = 650.0;
	public double mGlowWidth = 40.0;
	public double mGlowHeight = 40.0;

	private Color mColorBackground = null;
	private Color mColorEdge = null;
	private Color mColorNewNode = null;
	private Color mColorDeleteNode = null;
	//	private Color mTransparency          = new Color(0.0f, 0.0f, 0.0f, 1.0f);

	private List<String> mPublisher = new ArrayList<>();

	public Piccolo2DPanel(GraphConnection connection) {
		mNodeTranslationDuration = connection.getSettingManager().getNodeTranslationDuration();

		mPanel.setDefaultRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		mPanel.setInteractingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		mPanel.setAnimatingRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		mLayerNode = mPanel.getLayer();

		mLayerEdge = new PLayer();
		mPanel.getRoot().addChild(0, mLayerEdge);
		mPanel.getCamera().addLayer(0, mLayerEdge);

		mLayerGlow = new PLayer();
		mPanel.getRoot().addChild(0, mLayerGlow);
		mPanel.getCamera().addLayer(0, mLayerGlow);

		mPanel.addMouseListener(new ClickEventHandler(this));
		mPanel.setZoomEventHandler(new ZoomEventHandler(this));
		mLayerNode.addInputEventListener(new NodeEventHandler(connection, this));

		String vColorBackground = PropertiesManager.getProperty("color", "color.background", "0xFFFFFF");
		String vColorEdge = PropertiesManager.getProperty("color", "color.edge", "0x000000");
		String vColorNewNode = PropertiesManager.getProperty("color", "color.node.new", "0xC5D931");
		String vColorDeleteNode = PropertiesManager.getProperty("color", "color.node.delete", "0x82150F");
		mColorNewNode = Color.decode(vColorNewNode);
		mColorBackground = Color.decode(vColorBackground);
		mColorEdge = Color.decode(vColorEdge);
		mColorDeleteNode = Color.decode(vColorDeleteNode);
		mPanel.setBackground(mColorBackground);
	}

	/**
	 * Create a gradient color depending on the node size.
	 * 
	 * @param pNode
	 *            the node.
	 * @param pColorInside
	 *            the color inside of the gradient.
	 * @param pColorOutside
	 *            the color outside of the gradient.
	 * @return the gradient color.
	 */
	private Paint createGradientColor(PPath pNode, Color pColorInside, Color pColorOutside) {
		LOGGER.trace("Method createGradientColor(" + pNode + ", " + pColorInside + ", " + pColorOutside + ") called.");
		Color[] colors = { pColorInside, pColorOutside };
		float[] dist = { 0.0f, 0.5f };
		float radius = (float) pNode.getWidth();
		Point2D zero = new Point2D.Double(0.0, 0.0);
		Point2D center = pNode.getBounds().getCenter2D();
		AffineTransform vTransformation = AffineTransform.getScaleInstance(1.0, // x
				// scaling
				pNode.getHeight() / pNode.getWidth() // y scaling
				);
		vTransformation.translate(center.getX(), // x center
				center.getY() * (pNode.getWidth() / pNode.getHeight()) // y
				// center
				// *
				// invert
				// scaling
				);
		return new RadialGradientPaint(zero, radius, zero, dist, colors, RadialGradientPaint.CycleMethod.NO_CYCLE,
				RadialGradientPaint.ColorSpaceType.SRGB, vTransformation);
	}


	/**
	 * Get the color for an identifier node.
	 * 
	 * @param pNode
	 *            the identifier node.
	 * @return the color.
	 */
	private Paint getColor(PPath vNode, NodeIdentifier pNode) {
		LOGGER.trace("Method getColor(" + vNode + ", " + pNode + ") called.");

		String vIdentifierInside = "0x9999FF";
		String vIdentifierOutside = "0x9999FF";

		Identifier identifier = pNode.getIdentifier();
		String typeName = identifier.getTypeName();

		if (IdentifierHelper.IDENTIFIER_TYPES.contains(typeName)) {
			vIdentifierInside = PropertiesManager.getProperty("color", "color.identifier." + typeName + ".inside", "0x9999FF");
			vIdentifierOutside = PropertiesManager.getProperty("color", "color.identifier." + typeName + ".outside", "0x9999FF");

			// Special case: extended identifier
			if (typeName.equals(IdentifierHelper.IDENTITY_EL_NAME)) {
				IdentifierWrapper wrapper = IdentifierHelper.identifier(identifier.getRawData());
				String type = wrapper.getValueForXpathExpression("@" + IdentifierHelper.IDENTITY_ATTR_TYPE);
				if (type != null && type.equals("other")) {
					vIdentifierInside = PropertiesManager.getProperty("color", "color.identifier.extended.inside", "0x9999FF");
					vIdentifierOutside = PropertiesManager.getProperty("color", "color.identifier.extended.outside", "0x9999FF");
				}
			}
		}

		Color vColorInside = Color.decode(vIdentifierInside);
		Color vColorOutside = Color.decode(vIdentifierOutside);
		return createGradientColor(vNode, vColorInside, vColorOutside);
	}

	/**
	 * Get the text color for a metadata node.
	 * 
	 * @param pPublisher
	 *            the publisher of the metadata.
	 * @return the text color.
	 */
	private Paint getColorText(String pPublisher) {
		LOGGER.trace("Method getColorText(" + pPublisher + ") called.");
		String vDefaultText = PropertiesManager.getProperty("color", "color.metadata.text", "0x000000");
		String vText = PropertiesManager.getProperty("color", "color." + pPublisher + ".text", vDefaultText);
		return Color.decode(vText);
	}

	/**
	 * Get the text color for an identifier node.
	 * 
	 * @return the text color.
	 */
	private Color getColorIdentifierText(NodeIdentifier pNode) {
		LOGGER.trace("Method getColorIdentifierStroke() called.");
		String vColor = "0x000000";

		Identifier identifier = pNode.getIdentifier();
		String typeName = identifier.getTypeName();

		if (IdentifierHelper.IDENTIFIER_TYPES.contains(typeName)) {
			vColor = PropertiesManager.getProperty("color", "color.identifier." + typeName + ".text", "0x000000");

			// Special case: extended identifier
			if (typeName.equals(IdentifierHelper.IDENTITY_EL_NAME)) {
				IdentifierWrapper wrapper = IdentifierHelper.identifier(identifier.getRawData());
				String type = wrapper.getValueForXpathExpression("@" + IdentifierHelper.IDENTITY_ATTR_TYPE);
				if (type != null && type.equals("other")) {
					vColor = PropertiesManager.getProperty("color", "color.identifier.extended.text", "0x000000");
				}
			}
		}

		return Color.decode(vColor);
	}

	/**
	 * Get the stroke color for an identifier node.
	 * 
	 * @return the stroke color.
	 */
	private Color getColorIdentifierStroke(NodeIdentifier pNode) {
		LOGGER.trace("Method getColorIdentifierStroke() called.");
		String vOutside = "0x000000";

		Identifier identifier = pNode.getIdentifier();
		String typeName = identifier.getTypeName();

		if (IdentifierHelper.IDENTIFIER_TYPES.contains(typeName)) {
			vOutside = PropertiesManager.getProperty("color", "color.identifier." + typeName + ".border", "0x000000");

			// Special case: extended identifier
			if (typeName.equals(IdentifierHelper.IDENTITY_EL_NAME)) {
				IdentifierWrapper wrapper = IdentifierHelper.identifier(identifier.getRawData());
				String type = wrapper.getValueForXpathExpression("@" + IdentifierHelper.IDENTITY_ATTR_TYPE);
				if (type != null && type.equals("other")) {
					vOutside = PropertiesManager.getProperty("color", "color.identifier.extended.border", "0x000000");
				}
			}
		}

		return Color.decode(vOutside);
	}

	/**
	 * Get the stroke color for a metadata node.
	 * 
	 * @param pPublisher
	 *            the publisher of the metadata.
	 * @return the stroke color.
	 */
	private Color getColorMetadataStroke(String pPublisher) {
		LOGGER.trace("Method getColorMetadataStroke(" + pPublisher + ") called.");
		String vDefaultStroke = PropertiesManager.getProperty("color", "color.metadata.border", "0x000000");
		String vStroke = PropertiesManager.getProperty("color", "color." + pPublisher + ".border", vDefaultStroke);
		return Color.decode(vStroke);
	}

	/**
	 * Get the color for a metadata node.
	 * 
	 * @param pPublisher
	 *            the publisher of the metadata.
	 * @param pNode
	 *            the metadata node.
	 * @return the color.
	 */
	private Paint getColor(String pPublisher, PPath pNode) {
		LOGGER.trace("Method getColor(" + pPublisher + ", " + pNode + ") called.");

		String vDefaultInside = PropertiesManager.getProperty("color", "color.metadata.inside", "0xFF9966");
		String vDefaultOutside = PropertiesManager.getProperty("color", "color.metadata.outside", "0xFF9966");
		String vInside = PropertiesManager.getProperty("color", "color." + pPublisher + ".inside", vDefaultInside);
		String vOutside = PropertiesManager.getProperty("color", "color." + pPublisher + ".outside", vDefaultOutside);
		Color vColorInside = Color.decode(vInside);
		Color vColorOutside = Color.decode(vOutside);
		return createGradientColor(pNode, vColorInside, vColorOutside);
	}

	@Override
	public JComponent getPanel() {
		LOGGER.trace("Method getPanel() called.");
		return mPanel;
	}

	@Override
	public void addIdentifier(NodeIdentifier pNode) {
		LOGGER.trace("Method addIdentifier(" + pNode + ") called.");
		if (!mMapNode.containsKey(pNode)) {
			PText vText = createIdentifierText(pNode);
			vText.setHorizontalAlignment(Component.CENTER_ALIGNMENT);
			vText.setTextPaint(getColorIdentifierText(pNode));

			final PPath vNode = PPath.createRoundRectangle(-5, // x TODO Set
					// text in
					// center
					-5, // y TODO Set text in center
					(float) vText.getWidth() + 10, // width TODO Add offset
					(float) vText.getHeight() + 10, // height TODO Add offset
					20.0f, // arcWidth TODO Design variable
					20.0f // arcHeight TODO Design variable
					);
			vNode.setPaint(getColor(vNode, pNode));
			vNode.setStrokePaint(getColorIdentifierStroke(pNode));
			/* Composite */
			final PComposite vCom = new PComposite();
			vCom.addChild(vNode);
			vCom.addChild(vText);
			vCom.setOffset( // Set position
					mAreaOffsetX + pNode.getX() * mAreaWidth, // x
					mAreaOffsetY + pNode.getY() * mAreaHeight // y
					);
			vCom.addAttribute("type", "identifier");
			vCom.addAttribute("position", pNode);
			vCom.addAttribute("edges", new ArrayList<ArrayList<PPath>>()); // Add
			// edges
			// to
			// node
			mMapNode.put(pNode, vCom); // Add node to HashMap.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mLayerNode.addChild(vCom); // Add node to layer.
				}
			});
		}
	}

	/**
	 * Creates the text written into a Identifier-node,
	 * based on the information of the identifier itself.
	 *
	 * Examples:
	 * <ul>
	 * <li> access-request: ar1
	 * <li> device: switch
	 * <li> ip-address: 127.0.0.1 (IPv4)
	 * <li> mac-address: aa:bb:cc:dd:ee:ff
	 * <li> identity: John Doe (username)
	 * <li> extended-identifier
	 * </ul>
	 *
	 * @param pNode the {@link NodeIdentifier} for creating the text
	 * @return a {@link PText} containing the specific information for the given {@link NodeIdentifier}
	 */
	private PText createIdentifierText(NodeIdentifier pNode) {
		StringBuilder sb = new StringBuilder();

		Identifier identifier = pNode.getIdentifier();
		String typeName = identifier.getTypeName();
		IdentifierWrapper identifierWrapper = IdentifierHelper.identifier(identifier.getRawData());

		sb.append(typeName);

		String administrativeDomain = identifierWrapper.getValueForXpathExpression("@" + IdentifierHelper.IDENTIFIER_ATTR_ADMIN_DOMAIN);	// administrative-domain

		boolean multiLine = Boolean.parseBoolean(PropertiesManager.getProperty("visualizationConfig", "node.text.multiline", "true"));

		String identifierString = "";
		switch (typeName) {
		case IdentifierHelper.ACCESS_REQUEST_EL_NAME:
			identifierString = createIdentifierTextForAccessRequest(identifierWrapper, multiLine);
			break;
		case IdentifierHelper.DEVICE_EL_NAME:
			identifierString = createIdentifierTextForDevice(identifierWrapper, multiLine);
			break;
		case IdentifierHelper.IDENTITY_EL_NAME:
			identifierString = createIdentifierTextForIdentity(identifierWrapper, multiLine);
			break;
		case IdentifierHelper.MAC_ADDRESS_EL_NAME:
			identifierString = createIdentifierTextForMacAddress(identifierWrapper, multiLine);
			break;
		case IdentifierHelper.IP_ADDRESS_EL_NAME:
			identifierString = createIdentifierTextForIPAddress(identifierWrapper, multiLine);
			break;
		default:
			break;
		}

		sb.append(identifierString);

		if (administrativeDomain != null && !administrativeDomain.isEmpty()) {
			sb.append("\n");
			sb.append("<");
			sb.append(administrativeDomain);
			sb.append(">");
		}

		return new PText(sb.toString());
	}

	private String createIdentifierTextForAccessRequest(IdentifierWrapper wrapper, boolean multiLine) {
		StringBuilder sb = new StringBuilder();

		if (multiLine) {
			sb.append("\n");
			sb.append("[ name=");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.ACCESS_REQUEST_ATTR_NAME, "name"));	// name
			sb.append(" ]");
		} else {
			sb.append(": ");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.ACCESS_REQUEST_ATTR_NAME, "name"));	// name
		}

		return sb.toString();
	}

	private String createIdentifierTextForDevice(IdentifierWrapper wrapper, boolean multiLine) {
		StringBuilder sb = new StringBuilder();

		if (multiLine) {
			sb.append("\n");
			sb.append("[ name=");
			sb.append(wrapper.getValueForXpathExpressionOrElse(IdentifierHelper.DEVICE_NAME_EL_NAME, "name"));	// name
			sb.append(" ]");
		} else {
			sb.append(": ");
			sb.append(wrapper.getValueForXpathExpressionOrElse(IdentifierHelper.DEVICE_NAME_EL_NAME, "name"));	// name
		}

		return sb.toString();
	}

	private String createIdentifierTextForIdentity(IdentifierWrapper wrapper, boolean multiLine) {
		StringBuilder sb = new StringBuilder();
		String type = wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_TYPE, "type");

		if (multiLine) {
			if (type.equals("other")) {
				sb = new StringBuilder();
				sb.append("extended-identifier");
				sb.append("\n");
				sb.append("[ name=");
				sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_NAME, "name"));	// name
				sb.append(" ]");
				sb.append("\n");

				// TODO try to extract name from canonical XML
				sb.append("[ other-type-definition=");
				sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_OTHER_TYPE_DEF, "other-type-definition"));	// other-type-definition
				sb.append(" ]");
			} else {
				sb.append("\n");
				sb.append("[ name=");
				sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_NAME, "name"));	// name
				sb.append(" ]");
				sb.append("\n");
				sb.append("[ type=");
				sb.append(type);	// type
				sb.append(" ]");
			}
		} else {
			if (type.equals("other")) {
				sb = new StringBuilder();
				sb.append("extended-identifier: ");
				sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_NAME, "name"));	// name
				sb.append("\n");

				// TODO try to extract name from canonical XML
				sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_OTHER_TYPE_DEF, "other-type-definition"));	// other-type-definition
			} else {
				sb.append(": ");
				sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IDENTITY_ATTR_NAME, "name"));	// name
				sb.append(" (");
				sb.append(type);	// type
				sb.append(")");
			}

		}

		return sb.toString();
	}

	private String createIdentifierTextForIPAddress(IdentifierWrapper wrapper, boolean multiLine) {
		StringBuilder sb = new StringBuilder();

		if (multiLine) {
			sb.append("\n");
			sb.append("[ value=");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IP_ADDRESS_ATTR_VALUE, "value"));	// value
			sb.append(" ]");
			sb.append("\n");
			sb.append("[ type=");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IP_ADDRESS_ATTR_TYPE, "type"));	// type
			sb.append(" ]");
		} else {
			sb.append(": ");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IP_ADDRESS_ATTR_VALUE, "value"));	// value
			sb.append(" (");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.IP_ADDRESS_ATTR_TYPE, "type"));	// type
			sb.append(")");
		}

		return sb.toString();
	}

	private String createIdentifierTextForMacAddress(IdentifierWrapper wrapper, boolean multiLine) {
		StringBuilder sb = new StringBuilder();

		if (multiLine) {
			sb.append("\n");
			sb.append("[ value=");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.MAC_ADDRESS_ATTR_VALUE, "value"));	// value
			sb.append(" ]");
		} else {
			sb.append(": ");
			sb.append(wrapper.getValueForXpathExpressionOrElse("@" + IdentifierHelper.MAC_ADDRESS_ATTR_VALUE, "value"));	// value
		}

		return sb.toString();
	}

	private void addMetadata(NodeMetadata pNode) {
		LOGGER.trace("Method addMetadata(" + pNode + ") called.");
		if (!mMapNode.containsKey(pNode)) {
			String vType = pNode.getMetadata().getTypeName();
			final String vPublisher = pNode.getMetadata().valueFor("/meta:" + vType + "[@ifmap-publisher-id]");
			if (!mPublisher.contains(vPublisher)) {
				mPublisher.add(vPublisher);
			}
			/* Text */
			PText vText = new PText(pNode.getMetadata().getTypeName());
			vText.setTextPaint(getColorText(vPublisher));
			/* Rectangle */
			final PPath vNode = PPath.createRectangle(-5, // x TODO Set text in
					// center
					-5, // y TODO Set text in center
					(float) vText.getWidth() + 10, // width TODO Add offset
					(float) vText.getHeight() + 10 // height TODO Add offset
					);
			vNode.setPaint(getColor(vPublisher, vNode));
			vNode.setStrokePaint(getColorMetadataStroke(vPublisher));
			/* Composite */
			final PComposite vCom = new PComposite();
			vCom.addChild(vNode);
			vCom.addChild(vText);
			vCom.setOffset(mAreaOffsetX + pNode.getX() * mAreaWidth, mAreaOffsetY + pNode.getY() * mAreaHeight);
			vCom.addAttribute("type", "metadata");
			vCom.addAttribute("publisher", vPublisher);
			vCom.addAttribute("position", pNode);
			vCom.addAttribute("edges", new ArrayList<ArrayList<PPath>>()); // Add
			// edges
			// to
			// node
			mMapNode.put(pNode, vCom); // Add node to HashMap.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mLayerNode.addChild(vCom); // Add node to layer.
				}
			});
		}
	}

	@Override
	public void addMetadata(NodeIdentifier pIdentifier, NodeMetadata pMetadata) {
		LOGGER.trace("Method addMetadata(" + pIdentifier + ", " + pMetadata + ") called.");
		addMetadata(pMetadata);
		addEdge(pMetadata, pIdentifier, pMetadata);
	}

	@Override
	public void addMetadata(ExpandedLink pLink, NodeMetadata pMetadata) {
		LOGGER.trace("Method addMetadata(" + pLink + ", " + pMetadata + ") called.");
		addMetadata(pMetadata);
		/* Edges form Identifier to Metadata. */
		addEdge(pMetadata, pLink.getFirst(), pMetadata);
		/* Edges from Metadata to Identifier. */
		addEdge(pMetadata, pMetadata, pLink.getSecond());
	}

	/**
	 * Add an edge to the graph.
	 * 
	 * @param pKey
	 *            a key to group the edge.
	 * @param pNodeFirst
	 *            the node where the edge starts.
	 * @param pNodeSecond
	 *            the node where the edge ends.
	 */
	@SuppressWarnings("unchecked")
	private void addEdge(NodeMetadata pKey, Position pNodeFirst, Position pNodeSecond) {
		LOGGER.trace("Method addEdge(" + pKey + ", " + pNodeFirst + ", " + pNodeSecond + ") called.");
		PPath vEdge = new PPath();
		PComposite vNodeFirst = mMapNode.get(pNodeFirst);
		PComposite vNodeSecond = mMapNode.get(pNodeSecond);
		/* Add Edge to Node. */
		((ArrayList<PPath>)vNodeFirst.getAttribute("edges")).add(vEdge);
		((ArrayList<PPath>)vNodeSecond.getAttribute("edges")).add(vEdge);
		/* Add Node to Edge. */
		vEdge.addAttribute("nodes", new ArrayList<PComposite>());
		((ArrayList<PComposite>)vEdge.getAttribute("nodes")).add(vNodeFirst);
		((ArrayList<PComposite>)vEdge.getAttribute("nodes")).add(vNodeSecond);
		/* Add edge to layer. */
		mLayerEdge.addChild(vEdge);
		/* Add edge to HashMap. */
		ArrayList<PPath> vEdges = mMapEdge.get(pKey);
		if (vEdges == null) { // Is fist entry?
			vEdges = new ArrayList<>();
			mMapEdge.put(pKey, vEdges);
		}
		vEdges.add(vEdge);
		updateEdge(vEdge);
	}

	@SuppressWarnings("unchecked")
	private void deleteEdge(Object pKey, PPath pEdge) {
		LOGGER.trace("Method deleteEdge(" + pKey + ", " + pEdge + ") called.");
		/* Remove edge from layer */
		mLayerEdge.removeChild(pEdge);
		/* Remove edge from node */
		for (PComposite vNode : ((ArrayList<PComposite>) pEdge.getAttribute("nodes"))) {
			((ArrayList<PPath>) vNode.getAttribute("edges")).remove(pEdge);
		}
	}

	@Override
	public void updateIdentifier(NodeIdentifier pNode) {
		LOGGER.trace("Method updateIdentifier(" + pNode + ") called.");
		/* Update position */
		updatePosition(pNode);
	}

	@Override
	public void updateMetadata(NodeMetadata pNode) {
		LOGGER.trace("Method updateMetadata(" + pNode + ") called.");
		/* Update position */
		updatePosition(pNode);
	}

	/**
	 * The the position of a node and its edges.
	 * 
	 * @param pNode
	 *            the node in the graph.
	 */
	@SuppressWarnings("unchecked")
	private void updatePosition(final Position pNode) {
		LOGGER.trace("Method updatePosition(" + pNode + ") called.");
		if (!pNode.isInUse()) {
			final double vX = mAreaOffsetX + pNode.getX() * mAreaWidth;
			final double vY = mAreaOffsetY + pNode.getY() * mAreaHeight;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (mNodeTranslationDuration > 0) {
						final PComposite vNode = mMapNode.get(pNode);
						final PAffineTransform vDestTransform = vNode.getTransform();
						vDestTransform.setOffset(vX, vY);
						final PTransformActivity.Target vTarget = new PTransformActivity.Target() {
							@Override
							public void setTransform(final AffineTransform pTransform) {
								vNode.setTransform(pTransform);
								synchronized (vNode) {
									PPath vShadow = (PPath) vNode.getAttribute("glow");
									if (vShadow != null) {
										vShadow.setTransform(pTransform);
									}
								}
							}

							@Override
							public void getSourceMatrix(final double[] aSource) {
								vNode.getTransformReference(true).getMatrix(aSource);
							}
						};
						PActivity vNodeTranslation = new PTransformActivity(mNodeTranslationDuration,
								PUtil.DEFAULT_ACTIVITY_STEP_RATE, vTarget, vDestTransform) {
							@Override
							protected void activityStep(long time) {
								/* Set position of the node. */
								super.activityStep(time);
								/* Redraw edges. */
								ArrayList<PPath> vEdges = (ArrayList<PPath>) vNode.getAttribute("edges");
								for(PPath vEdge : vEdges) {
									updateEdge(vEdge);
								}
							}
						};
						mMapNode.get(pNode).addAttribute("activitie", vNodeTranslation);
						/* Add node activity */
						vNode.addActivity(vNodeTranslation);
					} else {
						final PComposite vNode = mMapNode.get(pNode);
						vNode.setOffset(vX, vY);
						ArrayList<PPath> vEdges = (ArrayList<PPath>) vNode.getAttribute("edges");
						for(PPath vEdge : vEdges) {
							updateEdge(vEdge);
						}
					}
				}
			});
		}
	}

	/**
	 * Redraw a edge with the new position on the panel.
	 * 
	 * @param pEdge
	 *            the edge to redraw.
	 */
	@SuppressWarnings("unchecked")
	public void updateEdge(PPath pEdge) {
		LOGGER.trace("Method updateEdge(" + pEdge + ") called.");
		synchronized (pEdge) {
			PNode       vNode1  = ((ArrayList<PComposite>)pEdge.getAttribute("nodes")).get(0);
			PNode       vNode2  = ((ArrayList<PComposite>)pEdge.getAttribute("nodes")).get(1);
			Point2D     vStart  = vNode1.getFullBoundsReference().getCenter2D();
			Point2D     vEnd    = vNode2.getFullBoundsReference().getCenter2D();
			pEdge.reset();
			/* Set edge color */
			pEdge.setStrokePaint(mColorEdge);
			pEdge.moveTo((float) vStart.getX(), (float) vStart.getY());
			pEdge.lineTo((float) vEnd.getX(), (float) vEnd.getY());
			pEdge.repaint();
		}
	}

	@Override
	public void deleteNode(Position pPosition) {
		LOGGER.trace("Method deleteNode(" + pPosition + ") called.");
		PComposite vNode = mMapNode.get(pPosition);
		if (vNode != null) {
			/* Delete node from layer */
			mLayerNode.removeChild(vNode);
			/* Remove Shadow */
			PPath vShadow = (PPath) vNode.getAttribute("glow");
			if (vShadow != null) {
				mLayerGlow.removeChild(vShadow);
			}
			/* Metadata */
			if (pPosition instanceof NodeMetadata) {
				/* Delete edges from layer */
				for (PPath vEdge : mMapEdge.get(pPosition)) {
					deleteEdge(pPosition, vEdge);
				}
				/* Delete edge from HashMap */
				mMapEdge.remove(pPosition);
			}
			/* Delete node from HashMap */
			mMapNode.remove(pPosition);
		}
	}

	@Override
	public void markAsNew(final Position pPosition) {
		LOGGER.trace("Method markAsNew(" + pPosition + ") called.");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PComposite vNode = mMapNode.get(pPosition);
				if (vNode != null) {
					addGlow(vNode, mColorNewNode);
				} else {
					LOGGER.debug("Coundn't find " + pPosition + " to mark as new.");
				}
			}
		});
	}

	@Override
	public void markAsDelete(final Position pPosition) {
		LOGGER.trace("Method markAsDelete(" + pPosition + ") called.");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PComposite vNode = mMapNode.get(pPosition);
				if (vNode != null) {
					addGlow(vNode, mColorDeleteNode);
				} else {
					LOGGER.debug("Coundn't find " + pPosition + " to mark as delete.");
				}
			}
		});
	}

	@Override
	public void clearHighlight(final Position pPosition) {
		LOGGER.trace("Method clearHighlight(" + pPosition + ") called.");
		/* Reset the Stroke of the node */
		final PComposite vNode = mMapNode.get(pPosition);
		if (vNode != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					synchronized (vNode) {
						/* Remove Shadow */
						final PPath vShadow = (PPath) vNode.getAttribute("glow");
						if (vShadow != null) {
							vNode.addAttribute("glow", null);
							PActivity vFading = vShadow.animateToTransparency(0.0f, 500); // TODO
							// Use
							// SettingManager
							PActivity vRemove = new PActivity(0) {
								@Override
								protected void activityStep(long time) {
									mLayerGlow.removeChild(vShadow);
								}
							};
							vRemove.startAfter(vFading);
							vShadow.addActivity(vRemove);
						}
					}
				}
			});
		} else {
			LOGGER.debug("Coundn't find " + pPosition + " to clear its highlights.");
		}
	}

	private void addGlow(PComposite pNode, Color pHighlight) {
		Point2D vPosition = pNode.getOffset();
		PBounds vBound = pNode.getFullBoundsReference();
		float vShadowWidth = (float) (vBound.getWidth() + mGlowWidth);
		float vShadowHeight = (float) (vBound.getHeight() + mGlowHeight);
		PPath vShadow = PPath.createEllipse((float) (-mGlowWidth / 2 - 5), // x
				(float) (-mGlowHeight / 2 - 5), // y
				vShadowWidth, // width
				vShadowHeight // height
				);
		vShadow.setOffset((float) (vPosition.getX()), // x
				(float) (vPosition.getY()) // y
				);
		vShadow.setStroke(null);
		vShadow.setPaint(createGradientColor(
				vShadow,
				pHighlight,
				mColorBackground // TODO mTransparency dosn't work.
				));
		vShadow.setTransparency(0.0f);
		synchronized (pNode) {
			mLayerGlow.addChild(vShadow);
			pNode.addAttribute("glow", vShadow);
		}
		vShadow.animateToTransparency(1.0f, 500); // TODO Use SettingManager
	}

	@Override
	public void clearGraph() {
		mLayerEdge.removeAllChildren();
		mLayerNode.removeAllChildren();
		mLayerGlow.removeAllChildren();
		mMapEdge.clear();
		mMapNode.clear();
	}

	/**
	 * Adjust the size of the panel depending on the number and size of labels.
	 */
	@Override
	public void adjustPanelSize() {
		LOGGER.trace("Method adjustPanelSize() called.");
		int vNumberOfNodes = mMapNode.size();
		int vNumberOfChars = 0;
		if (vNumberOfNodes > 0) {
			for (PComposite vNode : mMapNode.values()) {
				vNumberOfChars += ((PText) vNode.getChild(1)).getText().length();
			}
			double vAverage = vNumberOfChars / vNumberOfNodes;
			double vSize = (Math.sqrt(vNumberOfNodes) + vAverage) * 50;
			mAreaHeight = vSize;
			mAreaWidth = vSize;
		}
	}

	/**
	 * Move the camera to the center of the graph.
	 */
	public void setFocusToCenter() {
		LOGGER.trace("Method setFocusToCenter() called.");
		double xMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		if (mMapNode.values().size() > 0) {
			for (PComposite vNode : mMapNode.values()) {
				if (vNode.getOffset().getX() > xMax) {
					xMax = vNode.getOffset().getX();
				}
				if (vNode.getOffset().getX() < xMin) {
					xMin = vNode.getOffset().getX();
				}
				if (vNode.getOffset().getY() > yMax) {
					yMax = vNode.getOffset().getY();
				}
				if (vNode.getOffset().getY() < yMin) {
					yMin = vNode.getOffset().getY();
				}
			}
			int vOffset = 50;
			final PBounds vBounds = new PBounds(xMin - vOffset, yMin - vOffset, xMax - xMin + 2 * vOffset, yMax - yMin
					+ 2 * vOffset);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					mPanel.getCamera().setViewBounds(vBounds);
				}
			});
		}
	}

	@Override
	public void repaintNodes(String pType, String pPublisher) {
		LOGGER.trace("Method repaintNodes(" + pType + ", " + pPublisher + ") called.");
		for (Object key : mMapNode.keySet()) {
			PComposite vCom = mMapNode.get(key);
			PPath vNode = (PPath) vCom.getChild(0);
			PText vText = (PText) vCom.getChild(1);
			/* Check if node is highlighted */
			boolean isHighlighted = vNode.getStrokePaint().equals(mColorNewNode)
					|| vNode.getStrokePaint().equals(mColorDeleteNode);
			if (pType.equals("identifier")) {
				if (vCom.getAttribute("type").equals(pType)) {
					NodeIdentifier i = (NodeIdentifier) key;
					/* Repaint identifier nodes */
					vNode.setPaint(getColor(vNode, i));
					if (!isHighlighted) {
						vNode.setStrokePaint(getColorIdentifierStroke(i));
					}
					vText.setTextPaint(getColorIdentifierText(i));
				}
			} else if (pType.equals("metadata")) {
				if (vCom.getAttribute("type").equals(pType)) {
					/* Repaint metadata nodes */
					if (vCom.getAttribute("publisher").equals(pPublisher)) {
						/* Repaint the nodes of this publisher */
						vNode.setPaint(getColor(pPublisher, vNode));
						if (!isHighlighted) {
							vNode.setStrokePaint(getColorMetadataStroke(pPublisher));
						}
						vText.setTextPaint(getColorText(pPublisher));
					} else if (pPublisher.equals("")) {
						/*
						 * Default color was changed, repaint each node with its
						 * own color
						 */
						String vPublisher = (String) vCom.getAttribute("publisher");
						vNode.setPaint(getColor(vPublisher, vNode));
						if (!isHighlighted) {
							vNode.setStrokePaint(getColorMetadataStroke(vPublisher));
						}
						vText.setTextPaint(getColorText(vPublisher));
					}
				}
			}
		}
	}

	@Override
	public void repaint() {
		mPanel.repaint();
	}

	public double getAreaOffsetX() {
		LOGGER.trace("Method getAreaOffsetX called.");
		return mAreaOffsetX;
	}

	public double getAreaOffsetY() {
		LOGGER.trace("Method getAreaOffsetY called.");
		return mAreaOffsetY;
	}

	public double getAreaWidth() {
		LOGGER.trace("Method getAreaWidth called.");
		return mAreaWidth;
	}

	public double getAreaHeight() {
		LOGGER.trace("Method getAreaHeight called.");
		return mAreaHeight;
	}

	@Override
	public List<String> getPublisher() {
		LOGGER.trace("Method getPublisher called.");
		return mPublisher;
	}

	public void setAreaOffsetX(double pAreaOffsetX) {
		LOGGER.trace("Method setAreaOffsetX(" + pAreaOffsetX + ") called.");
		mAreaOffsetX = pAreaOffsetX;
	}

	public void setAreaOffsetY(double pAreaOffsetY) {
		LOGGER.trace("Method setAreaOffsetY(" + pAreaOffsetY + ") called.");
		mAreaOffsetY = pAreaOffsetY;
	}

	public void setAreaWidth(double pAreaWidth) {
		LOGGER.trace("Method setAreaWidth(" + pAreaWidth + ") called.");
		mAreaWidth = pAreaWidth;
	}

	public void setAreaHeight(double pAreaHeight) {
		LOGGER.trace("Method setAreaHeight(" + pAreaHeight + ") called.");
		mAreaHeight = pAreaHeight;
	}

	@Override
	public void setNodeTranslationDuration(int pNodeTranslationDuration) {
		mNodeTranslationDuration = pNodeTranslationDuration;
	}
}
