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
package org.primeframework.transformer;

import org.primeframework.transformer.domain.Document;
import org.primeframework.transformer.domain.TagNode;
import org.primeframework.transformer.service.BBCodeParser;
import org.primeframework.transformer.service.BBCodeToHTMLTransformer;
import org.testng.annotations.Test;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Daniel DeGroff
 */
public class DocumentTest {
  @Test
  public void walk() throws Exception {
    // TODO Do we need this test anymore?  Looks like we can still set the boolean to false, but it does not affect
    // TODO the output since we now take a transform predicate instead.

    // TODO These two walk methods do in fact demonstrate the document model was changed, so perhaps it is ok.
    // TODO we probably just need need the last step where we validate the transformation.

    Document document = new BBCodeParser().buildDocument("Hello [size=\"14\"][b]World!![/b][/size] Yo.");
    document.walk(TagNode.class, node -> node.transform = false);

    // assert all tag nodes are set to not transform
    document.walk(TagNode.class, node -> assertFalse(node.transform));

    String transformedResult = new BBCodeToHTMLTransformer().transform(document, (node) -> false, null, null);
    assertEquals("Hello [size=\"14\"][b]World!![/b][/size] Yo.", transformedResult);
  }

  @Test
  public void walkWithNoType() throws Exception {

    // TODO Do we need this test anymore?  Looks like we can still set the boolean to false, but it does not affect
    // TODO the output since we now take a transform predicate instead.

    // TODO These two walk methods do in fact demonstrate the document model was changed, so perhaps it is ok.
    // TODO we probably just need need the last step where we validate the transformation.

    Document document = new BBCodeParser().buildDocument("Hello [size=\"14\"][b]World!![/b][/size] Yo.");
    document.walk(node -> {
      if (node instanceof TagNode) {
        ((TagNode) node).transform = false;
      }
    });

    // assert all tag nodes are set to not transform
    document.walk(node -> {
      if (node instanceof TagNode) {
        assertFalse(((TagNode) node).transform);
      }
    });

    String transformedResult = new BBCodeToHTMLTransformer().transform(document, (node) -> false, null, null);
    assertEquals("Hello [size=\"14\"][b]World!![/b][/size] Yo.", transformedResult);
  }
}
