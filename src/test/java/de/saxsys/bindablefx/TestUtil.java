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

package de.saxsys.bindablefx;

import javafx.beans.property.ObjectProperty;
import org.mockito.internal.util.reflection.Whitebox;

import java.lang.ref.WeakReference;

/**
 * @author Xyanid on 15.05.2016.
 */
public final class TestUtil {

    //region Constructor

    private TestUtil() {}

    //endregion

    // region Methods

    /**
     * Returns the {@link CascadedRelayBinding#binding} of the given {@link CascadedRelayBinding}.
     *
     * @param binding the {@link CascadedRelayBinding} to use.
     *
     * @return the {@link CascadedRelayBinding#binding} of the given {@link CascadedRelayBinding}.
     */
    public static BaseBinding getBinding(final CascadedRelayBinding binding) {
        return (BaseBinding) Whitebox.getInternalState(binding, "binding");
    }

    /**
     * Returns the {@link BaseBinding#observedProperty} of the given {@link BaseBinding}.
     *
     * @param binding the {@link BaseBinding} to use.
     *
     * @return the {@link BaseBinding#observedProperty} of the given {@link BaseBinding}.
     */
    public static WeakReference<ObjectProperty> getObservedProperty(final BaseBinding binding) {
        return (WeakReference<ObjectProperty>) Whitebox.getInternalState(binding, "observedProperty");
    }

    // endregion
}
