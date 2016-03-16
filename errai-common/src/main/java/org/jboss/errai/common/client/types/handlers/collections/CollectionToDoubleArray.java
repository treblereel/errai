/*
 * Copyright (C) 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.client.types.handlers.collections;

import org.jboss.errai.common.client.types.TypeHandler;

import java.util.Collection;

public class CollectionToDoubleArray implements TypeHandler<Collection, Double[]> {
  public Double[] getConverted(Collection in) {
    if (in == null) return null;
    final Double[] newArray = new Double[in.size()];

    int i = 0;
    for (final Object o : in) {
      newArray[i++] = ((Number) o).doubleValue();
    }

    return newArray;
  }
}
