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

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a binding that acts as an intermediate binding so that multiple cascaded properties can be observed and the desired property can be bound at the
 * very end. So if you want to bind a property which itself is contained cascadingly in other properties.
 * <p>
 * e.G. if we have a class A which hold a property of B, which hold property of C, which holds a property of D and D is the property we want to bind to, we
 * would like build something like this
 * <pre>
 * {@code
 * A a = new A();
 * ObjectProperty<Long> property = new SimpleObjectProperty<>();
 * a.getB().getC().dProperty().bindBidirectional(property);
 * }
 * </pre>
 * However since B and C might be null we would need to listen to the values to become available at some point in time. This is where the
 * {@link CascadedBinding} comes into play and handles this by attaching listeners cascadingly so that we can bind to D with no concern that B or C might be
 * null, change or become invalid.
 * <p>
 * e.g. using the above example, the code to safely bind to D would look like this.
 * <pre>
 * {@code
 * A a = new A();
 * ObjectProperty<Long> property = new SimpleObjectProperty<>();
 *
 * a.getB().getC().dProperty().bindBidirectional(property);
 * }
 * </pre>
 *
 * @author xyanid on 30.03.2016.
 */
public class CascadedBinding<TPropertyValue, TRelayedPropertyValue, TRelayedProperty extends ObjectProperty<TRelayedPropertyValue>>
        extends RelayBinding<TPropertyValue, TRelayedPropertyValue, TRelayedProperty> {

    // region Fields

    /**
     * This is the binding that will be initialized for the new {@link ObjectProperty}, determines by invoking the {@link #relayProvider} on the current
     * value of the {@link #observedProperty}. It will be set if a call to either {@link #attach(Function)}, {@link #bind(Function, ObjectProperty)} or
     * {@link #bindBidirectional(Function, ObjectProperty)} was made.
     */
    private BaseBinding binding;

    // endregion

    // region Constructor

    private CascadedBinding(final Function<TPropertyValue, TRelayedProperty> relayProvider) {
        super(relayProvider);
    }

    public CascadedBinding(final ObjectProperty<TPropertyValue> property, final Function<TPropertyValue, TRelayedProperty> relayProvider) {
        this(relayProvider);

        setObservedProperty(property);
    }

    // endregion

    // region Public

    /**
     * This will create a new {@link CascadedBinding} for the relayedProperty of this binding. This means that the created {@link CascadedBinding}
     * will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #bind(Function, ObjectProperty)} or
     * {@link #bindBidirectional(Function, ObjectProperty)},
     * because the {@link #binding} will be disposed of before a new one is created.
     *
     * @return a new {@link CascadedBinding} which is used for the {@link #binding}.
     *
     * @see CascadedBinding
     * @see #bind(Function, ObjectProperty)
     * @see #bindBidirectional(Function, ObjectProperty)
     */
    public <TRelayedPropertyValueCascaded, TRelayedPropertyCascaded extends ObjectProperty<TRelayedPropertyValueCascaded>>
    CascadedBinding<TRelayedPropertyValue, TRelayedPropertyValueCascaded, TRelayedPropertyCascaded> attach(
            final Function<TRelayedPropertyValue, TRelayedPropertyCascaded> relayProvider) {
        return createNewBinding(() -> new CascadedBinding<>(relayProvider));
    }

    /**
     * This will create a new {@link UnidirectionalBinding} for the relayedProperty of this binding. This means that the created
     * {@link UnidirectionalBinding}
     * will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)} or {@link #bindBidirectional(Function, ObjectProperty)},
     * because the {@link #binding} will be disposed of before a new one is created.
     *
     * @return a new {@link UnidirectionalBinding} which is used for the {@link #binding}.
     *
     * @see UnidirectionalBinding
     * @see #attach(Function)
     * @see #bindBidirectional(Function, ObjectProperty)
     */
    public <TRelayedPropertyValueCascaded, TRelayedPropertyCascaded extends ObjectProperty<TRelayedPropertyValueCascaded>>
    UnidirectionalBinding<TRelayedPropertyValue, TRelayedPropertyValueCascaded, TRelayedPropertyCascaded> bind(
            final Function<TRelayedPropertyValue, TRelayedPropertyCascaded> relayProvider,
            final TRelayedPropertyCascaded targetProperty) {
        return createNewBinding(() -> new UnidirectionalBinding<>(relayProvider, targetProperty));
    }

    /**
     * This will create a new {@link BidirectionalBinding} for the relayedProperty of this binding. This means that the created
     * {@link BidirectionalBinding}
     * will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)} or {@link #bind(Function, ObjectProperty)}, because the
     * {@link #binding} will be disposed of before a new one is created.
     *
     * @return a new {@link BidirectionalBinding} which is used for the {@link #binding}.
     *
     * @see BidirectionalBinding
     * @see #attach(Function)
     * @see #bind(Function, ObjectProperty)
     */
    public <TRelayedPropertyValueCascaded, TRelayedPropertyCascaded extends ObjectProperty<TRelayedPropertyValueCascaded>>
    BidirectionalBinding<TRelayedPropertyValue, TRelayedPropertyValueCascaded, TRelayedPropertyCascaded> bindBidirectional(
            final Function<TRelayedPropertyValue, TRelayedPropertyCascaded> relayProvider,
            final TRelayedPropertyCascaded targetProperty) {
        return createNewBinding(() -> new BidirectionalBinding<>(relayProvider, targetProperty));
    }

    // endregion

    // region Override RelayBinding

    @Override
    protected void unbindProperty(final TRelayedProperty relayedProperty) {
        disposeBinding();
    }

    @Override
    protected void bindProperty(final TRelayedProperty relayedProperty) {
        if (relayedProperty != null) {
            bindBinding(relayedProperty);
        }
    }

    // endregion

    // region Private

    /**
     * Calls {@link BaseBinding#dispose()} if the current {@link #binding} is not null and sets it to null afterwards.
     */
    private void disposeBinding() {
        if (binding != null) {
            binding.dispose();
        }
    }

    /**
     * Calls {@link BaseBinding#setObservedProperty(ObjectProperty)} if the current {@link #binding} is not null.
     */
    private void bindBinding(final TRelayedProperty providedProperty) {
        if (binding != null) {
            binding.setObservedProperty(providedProperty);
        }
    }

    /**
     * Disposes the current {@link #binding} and recreates a new {@link TBaseBinding} using the given {@link Supplier}.
     *
     * @param bindingCreator the {@link Supplier} used to create the new desired binding.
     * @param <TBaseBinding> the type of the desired new binding.
     *
     * @return a new {@link TBaseBinding} which as been set as the new {@link #binding}.
     */
    private <TBaseBinding extends BaseBinding> TBaseBinding createNewBinding(final Supplier<TBaseBinding> bindingCreator) {
        disposeBinding();

        binding = bindingCreator.get();

        // the property was already set because we had a call before a call to this method was made
        getCurrentValue().ifPresent(value -> binding.setObservedProperty(getRelayedProperty()));

        return (TBaseBinding) binding;
    }

    // endregion
}