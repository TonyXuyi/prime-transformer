/*
 * Copyright (c) 2014, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package org.primeframework.transformer.domain;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>TagNode</code> holds index values to the document source to outline the start and end of the tag and the
 * body of the tag.
 * <p>
 * The following is an example in BBCode, but the same applies to other tags.
 * <pre>
 *     [url=http://foo.com] http://foo.com [/url]
 *     ^   ^                               ^    ^
 *     1   2               3 <--  body --> 4    5
 *
 *  Between index 1 and 2 is the opening tag.
 *  Between index 2 and 3 is the tag body.
 *  Between index 3 and 4 is the closing tag.
 *
 *  1. tagBegin
 *  2. attributeBegin
 *  3. bodyBegin
 *  4. bodyEnd
 *  5. tagEnd
 * </pre>
 * The body of the tag is contained in the children collection. A child may be either another <code>TagNode</code> or a
 * <code>TextNode</code>. The <code>TagNode</code> itself does not have a body, the body will be contained in a child
 * <code>TextNode</code>.
 * <p>
 * The two boolean hasBody and hasClosingTag states indicate what was found when the BBCode was parsed, not necessarily
 * what is required for a specific TagNode.<br>
 * <p>
 * Expected Variations of these two boolean states:
 * <p>
 * <ol>
 * <li>Has closing tag and a body: <pre>[b]foo[/b]</pre></li>
 * <li>Has closing tag and no body: <pre>[gameCard][/gameCard]</pre></li>
 * <li>Has no closing tag and a body: <pre>[*]foo</pre></li>
 * <li>Has no closing tag and no body: <pre>[:)]</pre> (emoticons)</li>
 * </ol>
 */
public class TagNode extends BaseTagNode {
  /**
   * Support for complex attributes. Example: [tag width="100" height="200" title="foo"]bar[/tag]
   */
  public final Map<String, String> attributes = new LinkedHashMap<>();

  public final List<Node> children = new ArrayList<>();

  /**
   * Support for a simple attribute. Example: [tag=foo]bar[/tag]
   */
  public String attribute;

  public int attributesBegin = -1;

  public int bodyBegin = -1;

  public int bodyEnd = -1;

  public boolean hasBody;

  public boolean hasClosingTag;

  public int nameEnd = -1;

  public boolean transform;

  public TagNode(Document document, int tagBegin) {
    this.document = document;
    this.tagBegin = tagBegin;
    this.transform = true;
  }

  public TagNode(Document document, int tagBegin, int nameEnd, int attributesBegin, int bodyBegin, int bodyEnd,
                 int tagEnd, String attribute, Map<String, String> attributes) {
    this.document = document;
    this.tagBegin = tagBegin;
    this.nameEnd = nameEnd;
    this.attributesBegin = attributesBegin;
    this.bodyBegin = bodyBegin;
    this.bodyEnd = bodyEnd;
    this.tagEnd = tagEnd;
    this.attribute = attribute;
    this.attributes.putAll(attributes);
    this.transform = true;
    this.hasBody = bodyBegin != -1 && bodyEnd != -1 && bodyBegin != bodyEnd;
    this.hasClosingTag = hasBody ? bodyEnd != tagEnd : nameEnd + 1 != tagEnd;
  }

  @Override
  public boolean addChild(Node node) {
    return children.add(node);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    TagNode tagNode = (TagNode) o;

    if (attributesBegin != tagNode.attributesBegin) {
      return false;
    }
    if (bodyBegin != tagNode.bodyBegin) {
      return false;
    }
    if (bodyEnd != tagNode.bodyEnd) {
      return false;
    }
    if (hasBody != tagNode.hasBody) {
      return false;
    }
    if (hasClosingTag != tagNode.hasClosingTag) {
      return false;
    }
    if (nameEnd != tagNode.nameEnd) {
      return false;
    }
    if (transform != tagNode.transform) {
      return false;
    }
    if (attribute != null ? !attribute.equals(tagNode.attribute) : tagNode.attribute != null) {
      return false;
    }
    if (!attributes.equals(tagNode.attributes)) {
      return false;
    }
    if (!children.equals(tagNode.children)) {
      return false;
    }

    return true;
  }

  @Override
  public List<Node> getChildren() {
    return children;
  }

  public String getName() {
    if (nameEnd > tagBegin + 1) {
      return document.getString(tagBegin + 1, nameEnd);
    }
    return null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (attribute != null ? attribute.hashCode() : 0);
    result = 31 * result + attributes.hashCode();
    result = 31 * result + attributesBegin;
    result = 31 * result + bodyBegin;
    result = 31 * result + bodyEnd;
    result = 31 * result + children.hashCode();
    result = 31 * result + (hasBody ? 1 : 0);
    result = 31 * result + (hasClosingTag ? 1 : 0);
    result = 31 * result + nameEnd;
    result = 31 * result + (transform ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TagNode{" +
        "tagBegin=" + tagBegin +
        ", tagEnd=" + tagBegin +
        ", attributes=" + attributes +
        ", children=" + children +
        ", attribute='" + attribute + '\'' +
        ", attributesBegin=" + attributesBegin +
        ", bodyBegin=" + bodyBegin +
        ", bodyEnd=" + bodyEnd +
        ", hasBody=" + hasBody +
        ", hasClosingTag=" + hasClosingTag +
        ", nameEnd=" + nameEnd +
        ", transform=" + transform +
        '}';
  }

  public TextNode toTextNode() {
    return new TextNode(document, tagBegin, tagEnd);
  }
}
