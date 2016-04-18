/*
 * Copyright 2015 - 2016 Xyanid
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package de.saxsys.bindablefx.mocks;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author xyanid on 31.03.2016.
 */
public class D {

    /**
     * Determines the e
     */
    private final ObjectProperty<E> e = new SimpleObjectProperty<>();

    /**
     * Determines the x
     */
    private final ObjectProperty<Long> x = new SimpleObjectProperty<>();

    /**
     * Gets the property {@link #e}.
     *
     * @return the {@link #e} property.
     */
    public final ObjectProperty<E> eProperty() {
        return e;
    }

    /**
     * Gets the property {@link #x}.
     *
     * @return the {@link #x} property.
     */
    public final ObjectProperty<Long> xProperty() {
        return x;
    }
}
