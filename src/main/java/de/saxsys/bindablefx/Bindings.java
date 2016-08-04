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

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Utility class to allow for the binding mechanisms in the lib to be used and is also the only point of entrance.
 *
 * @author Xyanid on 06.05.2016.
 */
public final class Bindings {

    // region Constructor

    /**
     * Prevents others from creating an instance of this class.
     */
    private Bindings() {}

    // endregion

    // region Methods

    /**
     * Creates a new {@link RootBinding} that listens to changes made to the given {@link ObservableValue} and then invokes its own binding mechanism.
     *
     * @param observedValue the {@link ObservableValue} to listen to.
     * @param <TValue>      the type of the value of the {@link ObservableValue}
     *
     * @return a new {@link RootBinding}.
     */
    public static <TValue> IFluentBinding<TValue> observe(@NotNull final ObservableValue<TValue> observedValue) {
        final RootBinding<TValue> result = new RootBinding<>();
        result.setObservedValue(observedValue);
        result.invalidate();
        return result;
    }

    /**
     * Creates
     *
     * @param observableValue
     * @param converter
     * @param <TValue>
     * @param <TConvertedValue>
     *
     * @return
     */
    public static <TValue, TConvertedValue> IFluentBinding<TConvertedValue> convert(@NotNull final ObservableValue<TValue> observableValue,
                                                                                    @NotNull final Function<TValue, TConvertedValue> converter) {
        return new ConverterBinding<>(observableValue, converter);
    }

    /**
     * Binds the given property1 bidirectional against the property2, the values will be converted using the given {@link IConverter}.
     *
     * @param property1         the first {@link Property} to be bind.
     * @param property2         the second {@link Property} to be bind.
     * @param converter         the {@link IConverter} to use.
     * @param <TValue>          the type of the first {@link Property}.
     * @param <TConvertedValue> the type of the second {@link Property}.
     */
    public static <TValue, TConvertedValue> BidirectionalBinding<Object> bindBidirectional(@NotNull final Property<TValue> property1,
                                                                                           @NotNull final Property<TConvertedValue> property2,
                                                                                           @NotNull final IConverter<TValue, TConvertedValue> converter) {
        return BidirectionalBinding.bind(property1, property2, converter);
    }

    /**
     * Unbinds the given property1 bidirectional from property2.
     * <p>
     * NOTE: this method is incompatible with the {@link com.sun.javafx.binding.BidirectionalBinding}, so calling this unbind will not destroy any
     * {@link com.sun.javafx.binding.BidirectionalBinding} but only the {@link BidirectionalBinding}.
     *
     * @param property1         the first {@link Property} to be unbound.
     * @param property2         the second {@link Property} to be unbound.
     * @param <TValue>          the type of the first {@link Property}.
     * @param <TConvertedValue> the type of the second {@link Property}.
     */
    public static <TValue, TConvertedValue> void unbindBidirectional(@NotNull final Property<TValue> property1, @NotNull final Property<TConvertedValue> property2) {
        BidirectionalBinding.unbind(property1, property2);
    }

    // endregion
}