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
 * {@link CascadedRelayBinding} comes into play and handles this by attaching listeners cascadingly so that we can bind to D with no concern that B or C might be
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
public class CascadedRelayBinding<TPropertyValue, TRelayedPropertyValue> extends RelayBinding<TPropertyValue, TRelayedPropertyValue> {

    // region Fields

    /**
     * This is the binding that will be initialized for the new {@link ObjectProperty}, determines by invoking the {@link #relayProvider} on the current
     * value of the {@link #observedProperty}. It will be set if a call to either {@link #attach(Function)}, {@link #bind(Function, ObjectProperty)} or
     * {@link #bindBidirectional(Function, ObjectProperty)} was made.
     */
    private BaseBinding binding;

    // endregion

    // region Constructor

    private CascadedRelayBinding(final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider) {
        super(relayProvider);
    }

    public CascadedRelayBinding(final ObjectProperty<TPropertyValue> property, final Function<TPropertyValue, ObjectProperty<TRelayedPropertyValue>> relayProvider) {
        this(relayProvider);

        createObservedProperty(property);
    }

    // endregion

    // region Public

    /**
     * This will create a new {@link CascadedRelayBinding} for the relayedProperty of this binding. This means that the created {@link CascadedRelayBinding}
     * will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #bind(Function, ObjectProperty)} or
     * {@link #bindBidirectional(Function, ObjectProperty)},
     * because the {@link #binding} will be disposed of before a new one is created.
     *
     * @return a new {@link CascadedRelayBinding} which is used for the {@link #binding}.
     *
     * @see CascadedRelayBinding
     * @see #bind(Function, ObjectProperty)
     * @see #bindBidirectional(Function, ObjectProperty)
     */
    public <TRelayedPropertyValueCascaded> CascadedRelayBinding<TRelayedPropertyValue, TRelayedPropertyValueCascaded> attach(final Function<TRelayedPropertyValue,
            ObjectProperty<TRelayedPropertyValueCascaded>> relayProvider) {
        return createNewBinding(() -> new CascadedRelayBinding<>(relayProvider));
    }

    /**
     * This will create a new {@link UnidirectionalRelayBinding} for the relayedProperty of this binding. This means that the created
     * {@link UnidirectionalRelayBinding}
     * will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)} or {@link #bindBidirectional(Function, ObjectProperty)},
     * because the {@link #binding} will be disposed of before a new one is created.
     *
     * @return a new {@link UnidirectionalRelayBinding} which is used for the {@link #binding}.
     *
     * @see UnidirectionalRelayBinding
     * @see #attach(Function)
     * @see #bindBidirectional(Function, ObjectProperty)
     */
    public <TRelayedPropertyValueCascaded> UnidirectionalRelayBinding<TRelayedPropertyValue, TRelayedPropertyValueCascaded> bind(final Function<TRelayedPropertyValue,
            ObjectProperty<TRelayedPropertyValueCascaded>> relayProvider, final ObjectProperty<TRelayedPropertyValueCascaded>
                                                                                                                                         targetProperty) {
        return createNewBinding(() -> new UnidirectionalRelayBinding<>(relayProvider, targetProperty));
    }

    /**
     * This will create a new {@link BidirectionalRelayBinding} for the relayedProperty of this binding. This means that the created
     * {@link BidirectionalRelayBinding}
     * will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)} or {@link #bind(Function, ObjectProperty)}, because the
     * {@link #binding} will be disposed of before a new one is created.
     *
     * @return a new {@link BidirectionalRelayBinding} which is used for the {@link #binding}.
     *
     * @see BidirectionalRelayBinding
     * @see #attach(Function)
     * @see #bind(Function, ObjectProperty)
     */
    public <TRelayedPropertyValueCascaded> BidirectionalRelayBinding<TRelayedPropertyValue, TRelayedPropertyValueCascaded> bindBidirectional(final
                                                                                                                                             Function<TRelayedPropertyValue,
                                                                                                                                                     ObjectProperty<TRelayedPropertyValueCascaded>> relayProvider,
                                                                                                                                             final
                                                                                                                                             ObjectProperty<TRelayedPropertyValueCascaded> targetProperty) {
        return createNewBinding(() -> new BidirectionalRelayBinding<>(relayProvider, targetProperty));
    }

    // endregion

    // region Override RelayBinding

    /**
     * {@inheritDoc}This implementation will also invoke {@link BaseBinding#dispose()} on the {@link #binding} and set it to to null.
     */
    @Override
    public void dispose() {
        super.dispose();
        disposeChildBinding();
        binding = null;
    }

    @Override
    protected void unbindProperty(final ObjectProperty<TRelayedPropertyValue> relayedProperty) {
        destroyChildBinding();
    }

    @Override
    protected void bindProperty(final ObjectProperty<TRelayedPropertyValue> relayedProperty) {
        if (relayedProperty != null) {
            resetChildBinding(relayedProperty);
        }
    }

    // endregion

    // region Child Binding

    /**
     * Calls {@link BaseBinding#createObservedProperty(ObjectProperty)} if the current {@link #binding} is set.
     */
    @SuppressWarnings("unchecked")
    private void resetChildBinding(final ObjectProperty<TRelayedPropertyValue> providedProperty) {
        if (binding != null) {
            binding.createObservedProperty(providedProperty);
        }
    }

    /**
     * Calls {@link BaseBinding#destroyObservedProperty()} if the current {@link #binding} is set.
     */
    private void destroyChildBinding() {
        if (binding != null) {
            binding.destroyObservedProperty();
        }
    }

    /**
     * Calls {@link BaseBinding#dispose()} if the current {@link #binding} is set.
     */
    private void disposeChildBinding() {
        if (binding != null) {
            binding.dispose();
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
    @SuppressWarnings("unchecked")
    private <TBaseBinding extends BaseBinding> TBaseBinding createNewBinding(final Supplier<TBaseBinding> bindingCreator) {
        disposeChildBinding();

        binding = bindingCreator.get();

        // the property was already set because we had a change or already set up view model before a call to this method was made
        getCurrentObservedValue().ifPresent(value -> binding.createObservedProperty(getRelayProvider().apply(value)));

        return (TBaseBinding) binding;
    }

    // endregion
}