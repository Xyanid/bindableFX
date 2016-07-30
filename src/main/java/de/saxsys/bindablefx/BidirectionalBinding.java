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

import javafx.beans.WeakListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

/**
 * This class allows for bidirectional binding of two {@link Property}s of different type using a converter to convert from one type to the other and vice versa.
 * <p>
 * The current implementation is kind of a copy of the already existing java implementation but since the {@link com.sun.javafx.binding.BidirectionalBinding} is not accessible, the two classes can
 * not be used in
 * conjunction.
 *
 * @author Xyanid on 29.07.2016.
 */
//TODO change this to a single class extending from BidirectionalBinding if the accessibility ever changes... I pray to god it does otherwise having two different ways sucks hard
public abstract class BidirectionalBinding<TValue> implements ChangeListener<TValue>, WeakListener {

    // region Fields

    private final int cachedHashCode;

    // endregion

    //region Constructor

    private BidirectionalBinding(Object property1, Object property2) {
        cachedHashCode = property1.hashCode() * property2.hashCode();
    }

    //endregion

    // region Static

    private static void checkParametersOrFail(Object property1, Object property2) {
        if ((property1 == null) || (property2 == null)) {
            throw new NullPointerException("Both properties must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind property to itself");
        }
    }

    // endregion

    // region Abstract

    protected abstract Object getProperty1();

    protected abstract Object getProperty2();

    // endregion

    // region Override WeakChangeListener

    @Override
    public boolean wasGarbageCollected() {
        return (getProperty1() == null) || (getProperty2() == null);
    }

    // endregion

    // region Override Object

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object propertyA1 = getProperty1();
        final Object propertyA2 = getProperty2();
        if ((propertyA1 == null) || (propertyA2 == null)) {
            return false;
        }

        if (obj instanceof BidirectionalBinding) {
            final BidirectionalBinding otherBinding = (BidirectionalBinding) obj;
            final Object propertyB1 = otherBinding.getProperty1();
            final Object propertyB2 = otherBinding.getProperty2();
            // check if both property are available and we are referencing the same two instances of the properties
            return !((propertyB1 == null) || (propertyB2 == null)) && ((propertyA1 == propertyB1 && propertyA2 == propertyB2) || (propertyA1 == propertyB2 && propertyA2 == propertyB1));

        }
        return false;
    }

    // endregion

    // region Public API

    @SuppressWarnings ({"unchecked", "ConstantConditions"})
    public static <TValue, TOtherValue> BidirectionalBinding<Object> bind(@NotNull final Property<TValue> property1,
                                                                          @NotNull final Property<TOtherValue> property2,
                                                                          final @NotNull IConverter<TValue, TOtherValue> converter) {
        checkParametersOrFail(property1, property2);
        if (converter == null) {
            throw new NullPointerException("IConverter cannot be null");
        }
        final BidirectionalConverterBinding binding = new BidirectionalConverterBinding<>(property1, property2, converter);
        property1.setValue(converter.convertBack(property2.getValue()));
        property1.addListener(binding);
        property2.addListener(binding);
        return binding;
    }

    @SuppressWarnings ("unchecked")
    public static <TValue, TOtherValue> void unbind(@NotNull final Property<TValue> property1, @NotNull final Property<TOtherValue> property2) {
        checkParametersOrFail(property1, property2);
        final BidirectionalBinding binding = new UntypedGenericBidirectionalBinding(property1, property2);
        property1.removeListener(binding);
        property2.removeListener(binding);
    }

    @SuppressWarnings ("unchecked")
    public static void unbind(@NotNull final Object property1, @NotNull final Object property2) {
        checkParametersOrFail(property1, property2);
        final BidirectionalBinding binding = new UntypedGenericBidirectionalBinding(property1, property2);
        if (property1 instanceof ObservableValue) {
            ((ObservableValue) property1).removeListener(binding);
        }
        if (property2 instanceof ObservableValue) {
            ((ObservableValue) property2).removeListener(binding);
        }
    }

    // endregion

    // region Classes

    private static class BidirectionalConverterBinding<TValue, TOtherValue> extends BidirectionalBinding<Object> {

        //region Fields

        @NotNull
        private final WeakReference<Property<TValue>> property1;

        @NotNull
        private final WeakReference<Property<TOtherValue>> property2;

        private final @NotNull IConverter<TValue, TOtherValue> converter;

        private boolean updating = false;

        //endregion

        // region Constructor

        @SuppressWarnings ("ConstantConditions")
        BidirectionalConverterBinding(@NotNull final Property<TValue> property1, @NotNull final Property<TOtherValue> property2, final @NotNull IConverter<TValue, TOtherValue> converter) {
            super(property1, property2);

            this.property1 = new WeakReference<>(property1);
            this.property2 = new WeakReference<>(property2);
            this.converter = converter;
        }

        //endregion

        // region Getter

        @Override
        protected Property<TValue> getProperty1() {
            return property1.get();
        }

        @Override
        protected Property<TOtherValue> getProperty2() {
            return property2.get();
        }

        // endregion

        //region Override ChangeListener

        @SuppressWarnings ("unchecked")
        @Override
        public void changed(@NotNull final ObservableValue<?> observable, @Nullable final Object oldValue, @Nullable final Object newValue) {
            if (!updating) {
                final Property<TValue> property = this.property1.get();
                final Property<TOtherValue> otherProperty = this.property2.get();
                if ((property == null) || (otherProperty == null)) {
                    if (property != null) {
                        property.removeListener(this);
                    }
                    if (otherProperty != null) {
                        otherProperty.removeListener(this);
                    }
                } else {
                    try {
                        updating = true;
                        if (property == observable) {
                            otherProperty.setValue(converter.convertTo(property.getValue()));
                        } else {
                            property.setValue(converter.convertBack(otherProperty.getValue()));
                        }
                    } catch (RuntimeException e) {
                        try {
                            if (property == observable) {
                                property.setValue((TValue) oldValue);
                            } else {
                                otherProperty.setValue((TOtherValue) oldValue);
                            }
                        } catch (Exception e2) {
                            e2.addSuppressed(e);
                            com.sun.javafx.binding.BidirectionalBinding.unbind(property, otherProperty);
                            throw new RuntimeException("Bidirectional binding failed together with an attempt" +
                                                       " to restore the source property1 to the previous value." +
                                                       " Removing the bidirectional binding from properties " +
                                                       property +
                                                       " and " +
                                                       otherProperty, e2);
                        }
                        throw new RuntimeException("Bidirectional binding failed, setting to the previous value", e);
                    } finally {
                        updating = false;
                    }
                }
            }
        }

        //endregion
    }

    private static class UntypedGenericBidirectionalBinding extends BidirectionalBinding<Object> {

        private final Object property1;
        private final Object property2;

        public UntypedGenericBidirectionalBinding(Object property1, Object property2) {
            super(property1, property2);
            this.property1 = property1;
            this.property2 = property2;
        }

        @Override
        protected Object getProperty1() {
            return property1;
        }

        @Override
        protected Object getProperty2() {
            return property2;
        }

        @Override
        public void changed(ObservableValue<? extends Object> sourceProperty, Object oldValue, Object newValue) {
            throw new RuntimeException("Should not reach here");
        }
    }

    // endregion
}