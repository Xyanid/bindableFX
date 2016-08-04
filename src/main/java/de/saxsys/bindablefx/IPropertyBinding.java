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
import org.jetbrains.annotations.NotNull;

/**
 * This interface allows for a {@link Property} to be bound.
 *
 * @author Xyanid on 29.07.2016.
 */
public interface IPropertyBinding<TValue> extends Property<TValue>, IFluentBinding<TValue> {

    /**
     * Binds the given {@link Property} bidirectional against this {@link IPropertyBinding} using the given {@link IConverter} to convert back and forth.
     *
     * @param other         the other {@link Property} to bind to.
     * @param converter     the {@link IConverter} to use when converting back and forth.
     * @param <TOtherValue> the type of the value of the other {@link Property}.
     */
    <TOtherValue> void bindBidirectional(@NotNull final Property<TOtherValue> other, final @NotNull IConverter<TValue, TOtherValue> converter);

    /**
     * Unbinds the given {@link Property} bidirectional from this {@link IPropertyBinding}.
     *
     * @param other         the other {@link Property} to bind to.
     * @param <TOtherValue> the type of the value of the other {@link Property}.
     */
    <TOtherValue> void unbindBidirectionalConverted(@NotNull final Property<TOtherValue> other);

    /**
     * Unbinds this {@link IPropertyBinding} bidirectional from any property it might have been bound too.
     */
    void unbindBidirectional();

    /**
     * Determines if this {@link IPropertyBinding} is bidirectional bound against any other {@link Property}.
     *
     * @return true if this {@link IPropertyBinding} is bidirectional bound against any other {@link Property}, otherwise false.
     */
    boolean isBidirectionalBound();
}
