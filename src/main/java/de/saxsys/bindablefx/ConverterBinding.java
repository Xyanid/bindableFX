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

import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Function;


/**
 * This binding can be used to convert the value of given {@link ObservableValue} into another {@link ObservableValue} of a different type.
 *
 * @param <TValue>          the type of the {@link ObservableValue} this binidng is based on.
 * @param <TConvertedValue> the desired type to convert to.
 */
class ConverterBinding<TValue, TConvertedValue> extends RootBinding<TConvertedValue> {

    // region Fields

    /**
     * The converter to be used when the value of the {@link #parent} is propertyChanged and shall be converted into the desired type.
     */
    @NotNull
    private final Function<TValue, TConvertedValue> converter;

    /**
     * The {@link ObservableValue} that provides the base value.
     */
    @NotNull
    private final WeakReference<ObservableValue<TValue>> parent;

    // endregion

    // region Constructor

    ConverterBinding(@NotNull final ObservableValue<TValue> parent, @NotNull final Function<TValue, TConvertedValue> converter) {
        this.parent = new WeakReference<>(parent);
        this.converter = converter;
        bind(parent);
    }

    // endregion

    // region Override RootBinding

    /**
     * Returns the current converted value of the {@link #parent} or the {@link #fallbackValue} if either the {@link #parent} is not longer available.
     *
     * @return {@link Optional#empty()} if the {@link #observedValue} is null or an {@link Optional} of the current value of the {@link #observedValue}.
     */
    @Override
    protected TConvertedValue computeValue() {
        final ObservableValue<TValue> observedValue = parent.get();

        if (observedValue != null) {
            return converter.apply(observedValue.getValue());
        } else {
            return super.computeValue();
        }
    }

    /**
     * Stops listening to the {@link #parent}.
     */
    @Override
    public void dispose() {
        super.dispose();
        final ObservableValue<TValue> observableValue = parent.get();
        if (observableValue != null) {
            unbind(observableValue);
            parent.clear();
        }
    }

    // endregion
}