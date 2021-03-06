/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * $Id: DOMUtil.java 51109 2006-09-28 15:28:12Z lschwenk $
 */

package com.sun.ts.tests.jaxws.wsa.common;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import jakarta.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class DOMUtil {

  private static DocumentBuilder db;

  /**
   * Creates a new DOM document.
   */
  public static Document createDom() {
    synchronized (DOMUtil.class) {
      if (db == null) {
        try {
          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          dbf.setNamespaceAware(true);
          db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
          throw new FactoryConfigurationError(e);
        }
      }
      return db.newDocument();
    }
  }

  public static Node createDOMNode(InputStream inputStream) {

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(false);
    try {
      DocumentBuilder builder = dbf.newDocumentBuilder();
      try {
        return builder.parse(inputStream);
      } catch (SAXException e) {
        e.printStackTrace(); // To change body of catch statement use File |
                             // Settings | File Templates.
      } catch (IOException e) {
        e.printStackTrace(); // To change body of catch statement use File |
                             // Settings | File Templates.
      }
    } catch (ParserConfigurationException pce) {
      IllegalArgumentException iae = new IllegalArgumentException(
          pce.getMessage());
      iae.initCause(pce);
      throw iae;
    }
    return null;
  }

  /**
   * Traverses a DOM node and writes out on a streaming writer.
   *
   * @param node
   * @param writer
   */
  public static void serializeNode(Element node, XMLStreamWriter writer)
      throws XMLStreamException {
    String nodePrefix = fixNull(node.getPrefix());
    String nodeNS = fixNull(node.getNamespaceURI());

    // See if nodePrefix:nodeNS is declared in writer's NamespaceContext before
    // writing start element
    // Writing start element puts nodeNS in NamespaceContext even though
    // namespace declaration not written
    boolean prefixDecl = isPrefixDeclared(writer, nodeNS, nodePrefix);

    writer.writeStartElement(nodePrefix, node.getLocalName(), nodeNS);

    if (node.hasAttributes()) {
      NamedNodeMap attrs = node.getAttributes();
      int numOfAttributes = attrs.getLength();
      // write namespace declarations first.
      // if we interleave this with attribue writing,
      // Zephyr will try to fix it and we end up getting inconsistent namespace
      // bindings.
      for (int i = 0; i < numOfAttributes; i++) {
        Node attr = attrs.item(i);
        String nsUri = fixNull(attr.getNamespaceURI());
        if (nsUri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
          // handle default ns declarations
          String local = attr.getLocalName()
              .equals(XMLConstants.XMLNS_ATTRIBUTE) ? "" : attr.getLocalName();
          if (local.equals(nodePrefix) && attr.getNodeValue().equals(nodeNS)) {
            prefixDecl = true;
          }
          // this is a namespace declaration, not an attribute
          writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
          writer.writeNamespace(attr.getLocalName(), attr.getNodeValue());
        }
      }
    }
    // node's namespace is not declared as attribute, but declared on ancestor
    if (!prefixDecl) {
      writer.writeNamespace(nodePrefix, nodeNS);
    }

    // Write all other attributes which are not namespace decl.
    if (node.hasAttributes()) {
      NamedNodeMap attrs = node.getAttributes();
      int numOfAttributes = attrs.getLength();

      for (int i = 0; i < numOfAttributes; i++) {
        Node attr = attrs.item(i);
        String attrPrefix = fixNull(attr.getPrefix());
        String attrNS = fixNull(attr.getNamespaceURI());
        if (!attrNS.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
          String localName = attr.getLocalName();
          if (localName == null) {
            // TODO: this is really a bug in the caller for not creating proper
            // DOM tree.
            // will remove this workaround after plugfest
            localName = attr.getNodeName();
          }
          boolean attrPrefixDecl = isPrefixDeclared(writer, attrNS, attrPrefix);
          if (!attrPrefix.equals("") && !attrPrefixDecl) {
            // attr has namespace but namespace decl is there in ancestor node
            // So write the namespace decl before writing the attr
            writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
            writer.writeNamespace(attrPrefix, attrNS);
          }
          writer.writeAttribute(attrPrefix, attrNS, localName,
              attr.getNodeValue());
        }
      }
    }

    if (node.hasChildNodes()) {
      NodeList children = node.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        switch (child.getNodeType()) {
        case Node.PROCESSING_INSTRUCTION_NODE:
          writer.writeProcessingInstruction(child.getNodeValue());
          break;
        case Node.DOCUMENT_TYPE_NODE:
          break;
        case Node.CDATA_SECTION_NODE:
          writer.writeCData(child.getNodeValue());
          break;
        case Node.COMMENT_NODE:
          writer.writeComment(child.getNodeValue());
          break;
        case Node.TEXT_NODE:
          writer.writeCharacters(child.getNodeValue());
          break;
        case Node.ELEMENT_NODE:
          serializeNode((Element) child, writer);
          break;
        }
      }
    }
    writer.writeEndElement();
  }

  private static boolean isPrefixDeclared(XMLStreamWriter writer, String nsUri,
      String prefix) {
    boolean prefixDecl = false;
    NamespaceContext nscontext = writer.getNamespaceContext();
    Iterator prefixItr = nscontext.getPrefixes(nsUri);
    while (prefixItr.hasNext()) {
      if (prefix.equals(prefixItr.next())) {
        prefixDecl = true;
        break;
      }
    }
    return prefixDecl;
  }

  /**
   * Gets the first child of the given name, or null.
   */
  public static Element getFirstChild(Element e, String nsUri, String local) {
    for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element c = (Element) n;
        if (c.getLocalName().equals(local) && c.getNamespaceURI().equals(nsUri))
          return c;
      }
    }
    return null;
  }

  private static String fixNull(String s) {
    if (s == null)
      return "";
    else
      return s;
  }

  /**
   * Gets the first element child.
   */
  public static Element getFirstElementChild(Node parent) {
    for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        return (Element) n;
      }
    }
    return null;
  }
}
