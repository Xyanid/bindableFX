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
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a child that acts as an intermediate child so that multiple cascaded properties can be observed and the desired property can be bound at the
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
 * new CascadedBinding(a.bProperty(), B::cProperty).bindBidirectional(C::dProperty, property, false);
 * }
 * </pre>
 *
 * @author xyanid on 30.03.2016.
 */
public class CascadedRelayBinding<TPropertyValue, TRelayedValue> extends RelayBinding<TPropertyValue, ObservableValue<TRelayedValue>> {

    // region Fields

    /**
     * This is the child that will be initialized for the new {@link ObservableValue}, determines by invoking the {@link #relayProvider} on the current
     * value of the {@link #observedProperty}. It will be set if a call to either {@link #attach(Function)}, {@link #bind(Function, Property)},
     * {@link #bind(Function, Property, Object)}, {@link #bindReverse(Function, ObservableValue)}, {@link #bindBidirectional(Function, Property)} or
     * {@link #bindBidirectional(Function, Property, Object)} was made.
     */
    @Nullable
    private BaseBinding child;

    // endregion

    // region Constructor

    private CascadedRelayBinding(@NotNull final Function<TPropertyValue, ObservableValue<TRelayedValue>> relayProvider) {
        super(relayProvider);
    }

    public CascadedRelayBinding(@NotNull final ObservableValue<TPropertyValue> property, @NotNull final Function<TPropertyValue, ObservableValue<TRelayedValue>> relayProvider) {
        this(relayProvider);

        createObservedProperty(property);
    }

    // endregion

    // region Public

    /**
     * This will create a new {@link CascadedRelayBinding} for the relayedProperty of this child. This means that the created {@link CascadedRelayBinding} will now listen to
     * changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to  {@link #consume(Function, Consumer, Consumer)}, {@link #bind(Function, Property)},
     * {@link #bind(Function, Property, Object)}, {@link #bindReverse(Function, ObservableValue)}, {@link #bindBidirectional(Function, Property)} or
     * {@link #bindBidirectional(Function, Property, Object)} because the {@link #child} will be disposed of before a new one is created.
     *
     * @param <TRelayedObservableCascaded> the type of the value of the relayed property of the new {@link CascadedRelayBinding}.
     * @param relayProvider                the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                     {@link CascadedRelayBinding}.
     *
     * @return a new {@link CascadedRelayBinding} which is used for the {@link #child}.
     *
     * @see CascadedRelayBinding
     * @see #consume(Function, Consumer, Consumer)
     * @see #bind(Function, Property)
     * @see #bind(Function, Property, Object)
     * @see #bindReverse(Function, ObservableValue)
     * @see #bindBidirectional(Function, Property)
     * @see #bindBidirectional(Function, Property, Object)
     */
    public <TRelayedObservableCascaded> CascadedRelayBinding<TRelayedValue, TRelayedObservableCascaded> attach(
            @NotNull final Function<TRelayedValue, ObservableValue<TRelayedObservableCascaded>> relayProvider) {
        return createChild(() -> new CascadedRelayBinding<>(relayProvider));
    }

    /**
     * This will create a new {@link ConsumerRelayBinding}. This means that the created {@link ConsumerRelayBinding} will now listen to changes that happen on this bindings
     * relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)}, {@link #bind(Function, Property)}, {@link #bind(Function, Property, Object)},
     * {@link #bindReverse(Function, ObservableValue)}, {@link #bindBidirectional(Function, Property)} or {@link #bindBidirectional(Function, Property, Object)}
     * because the {@link #child} will be disposed of before a new one is created.
     *
     * @param <TRelayedObjectCascaded>      the type of the value of the relayed property of the new {@link ConsumerRelayBinding}.
     * @param relayProvider                 the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                      {@link ConsumerRelayBinding}.
     * @param unbindRelayedPropertyConsumer the {@link Consumer} to be used as the {@link ConsumerRelayBinding#unbindRelayedObjectConsumer}.
     * @param bindRelayedPropertyConsumer   the {@link Consumer} to be used as the {@link ConsumerRelayBinding#bindRelayedObjectConsumer}.
     *
     * @return a new {@link ConsumerRelayBinding} which is used for the {@link #child}.
     *
     * @see ConsumerRelayBinding
     * @see #attach(Function)
     * @see #bind(Function, Property)
     * @see #bind(Function, Property, Object)
     * @see #bindReverse(Function, ObservableValue)
     * @see #bindBidirectional(Function, Property)
     * @see #bindBidirectional(Function, Property, Object)
     */
    public <TRelayedObjectCascaded> ConsumerRelayBinding<TRelayedValue, TRelayedObjectCascaded> consume(
            @NotNull final Function<TRelayedValue, TRelayedObjectCascaded> relayProvider,
            @NotNull final Consumer<TRelayedObjectCascaded> unbindRelayedPropertyConsumer,
            @NotNull final Consumer<TRelayedObjectCascaded> bindRelayedPropertyConsumer) {
        return createChild(() -> new ConsumerRelayBinding<>(relayProvider, unbindRelayedPropertyConsumer, bindRelayedPropertyConsumer));
    }

    /**
     * This will create a new {@link UnidirectionalRelayBinding} that does not reset its target property when the relayed property is unbound for the relayedProperty. This means
     * that the created {@link UnidirectionalRelayBinding} will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)}, {@link #consume(Function, Consumer, Consumer)},
     * {@link #bind(Function, Property, Object)}, {@link #bindReverse(Function, ObservableValue)}, {@link #bindBidirectional(Function, Property)} or
     * {@link #bindBidirectional(Function, Property, Object)} because the {@link #child} will be disposed of before a new one is created.
     *
     * @param <TRelayedObservableValueCascaded> the type of the value of the relayed property of the new {@link UnidirectionalRelayBinding}.
     * @param relayProvider                     the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                          {@link UnidirectionalRelayBinding}.
     * @param targetProperty                    the target property to be used for the new {@link UnidirectionalRelayBinding}.
     *
     * @return a new {@link UnidirectionalRelayBinding} which is used for the {@link #child}.
     *
     * @see UnidirectionalRelayBinding
     * @see #attach(Function)
     * @see #consume(Function, Consumer, Consumer)
     * @see #bind(Function, Property, Object)
     * @see #bindReverse(Function, ObservableValue)
     * @see #bindBidirectional(Function, Property)
     * @see #bindBidirectional(Function, Property, Object)
     */
    public <TRelayedObservableValueCascaded> UnidirectionalRelayBinding<TRelayedValue, TRelayedObservableValueCascaded> bind(
            @NotNull final Function<TRelayedValue, ObservableValue<TRelayedObservableValueCascaded>> relayProvider,
            @NotNull final Property<TRelayedObservableValueCascaded> targetProperty) {
        return createChild(() -> new UnidirectionalRelayBinding<>(relayProvider, targetProperty));
    }

    /**
     * This will create a new {@link ResettableUnidirectionalRelayBinding}. This means that the created {@link ResettableUnidirectionalRelayBinding} will now listen to changes
     * that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)}, {@link #consume(Function, Consumer, Consumer)}, {@link #bind(Function, Property)},
     * {@link #bindReverse(Function, ObservableValue)}, {@link #bindBidirectional(Function, Property)} or {@link #bindBidirectional(Function, Property, Object)} because the
     * {@link #child} will be disposed of before a new one is created.
     *
     * @param <TRelayedObservableValueCascaded> the type of the value of the relayed property of the new {@link UnidirectionalRelayBinding}.
     * @param relayProvider                     the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                          {@link UnidirectionalRelayBinding}.
     * @param targetProperty                    the target property to be used for the new {@link UnidirectionalRelayBinding}.
     * @param resetValue                        value to use when the target property gets unbound.
     *
     * @return a new {@link ResettableUnidirectionalRelayBinding} which is used for the {@link #child}.
     *
     * @see UnidirectionalRelayBinding
     * @see #attach(Function)
     * @see #consume(Function, Consumer, Consumer)
     * @see #bind(Function, Property)
     * @see #bindReverse(Function, ObservableValue)
     * @see #bindBidirectional(Function, Property)
     * @see #bindBidirectional(Function, Property, Object)
     */
    public <TRelayedObservableValueCascaded> ResettableUnidirectionalRelayBinding<TRelayedValue, TRelayedObservableValueCascaded> bind(
            @NotNull final Function<TRelayedValue, ObservableValue<TRelayedObservableValueCascaded>> relayProvider,
            @NotNull final Property<TRelayedObservableValueCascaded> targetProperty,
            @Nullable final TRelayedObservableValueCascaded resetValue) {
        return createChild(() -> new ResettableUnidirectionalRelayBinding<>(relayProvider, targetProperty, resetValue));
    }

    /**
     * This will create a new {@link ReverseUnidirectionalRelayBinding}. This means that the created {@link ReverseUnidirectionalRelayBinding} will now listen to changes that
     * happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)}, {@link #consume(Function, Consumer, Consumer)},
     * {@link #bind(Function, Property)}, {@link #bind(Function, Property, Object)}, {@link #bindBidirectional(Function, Property)} or
     * {@link #bindBidirectional(Function, Property, Object)} because the {@link #child} will be disposed of before a new one is created.
     *
     * @param <TRelayedPropertyValueCascaded> the type of the value of the relayed property of the new {@link ReverseUnidirectionalRelayBinding}.
     * @param relayProvider                   the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                        {@link ReverseUnidirectionalRelayBinding}.
     * @param targetProperty                  the target property to be used for the new {@link ReverseUnidirectionalRelayBinding}.
     *
     * @return a new {@link ReverseUnidirectionalRelayBinding} which is used for the {@link #child}.
     *
     * @see UnidirectionalRelayBinding
     * @see #attach(Function)
     * @see #consume(Function, Consumer, Consumer)
     * @see #bind(Function, Property)
     * @see #bind(Function, Property, Object)
     * @see #bindBidirectional(Function, Property)
     * @see #bindBidirectional(Function, Property, Object)
     */
    public <TRelayedPropertyValueCascaded> ReverseUnidirectionalRelayBinding<TRelayedValue, TRelayedPropertyValueCascaded> bindReverse(
            @NotNull final Function<TRelayedValue, Property<TRelayedPropertyValueCascaded>> relayProvider,
            @NotNull final ObservableValue<TRelayedPropertyValueCascaded> targetProperty) {
        return createChild(() -> new ReverseUnidirectionalRelayBinding<>(relayProvider, targetProperty));
    }

    /**
     * This will create a new {@link BidirectionalRelayBinding} that does not reset its target property when the relayed property is unbound for the relayedProperty. This means
     * that the created {@link BidirectionalRelayBinding} will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)}, {@link #consume(Function, Consumer, Consumer)},
     * {@link #bind(Function, Property)}, {@link #bind(Function, Property, Object)}, {@link #bindReverse(Function, ObservableValue)} or
     * {@link #bindBidirectional(Function, Property, Object)} because the {@link #child} will be disposed of before a new one is created.
     *
     * @param <TRelayedPropertyValueCascaded> the type of the value of the relayed property of the new {@link BidirectionalRelayBinding}.
     * @param relayProvider                   the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                        {@link BidirectionalRelayBinding}.
     * @param targetProperty                  the target property to be used for the new {@link BidirectionalRelayBinding}.
     *
     * @return a new {@link BidirectionalRelayBinding} which is used for the {@link #child}.
     *
     * @see BidirectionalRelayBinding
     * @see #attach(Function)
     * @see #consume(Function, Consumer, Consumer)
     * @see #bind(Function, Property)
     * @see #bind(Function, Property, Object)
     * @see #bindReverse(Function, ObservableValue)
     * @see #bindBidirectional(Function, Property, Object)
     */
    public <TRelayedPropertyValueCascaded> BidirectionalRelayBinding<TRelayedValue, TRelayedPropertyValueCascaded> bindBidirectional(
            @NotNull final Function<TRelayedValue, Property<TRelayedPropertyValueCascaded>> relayProvider, @NotNull final Property<TRelayedPropertyValueCascaded> targetProperty) {
        return createChild(() -> new BidirectionalRelayBinding<>(relayProvider, targetProperty));
    }

    /**
     * This will create a new {@link ResettableBidirectionalRelayBinding} for the relayedProperty of this child. This means that the created
     * {@link ResettableBidirectionalRelayBinding} will now listen to changes that happen on this bindings relayedProperty and in turn invoke its own binding mechanism.
     * <p>
     * Note that a call to this method will nullify any calls made to {@link #attach(Function)}, {@link #consume(Function, Consumer, Consumer)},
     * {@link #bind(Function, Property)}, {@link #bind(Function, Property, Object)}, {@link #bindReverse(Function, ObservableValue)} because the {@link #child} or
     * {@link #bindBidirectional(Function, Property)} will be disposed of before a new one is created.
     *
     * @param <TRelayedPropertyValueCascaded> the type of the value of the relayed property of the new {@link BidirectionalRelayBinding}.
     * @param relayProvider                   the {@link Function} used to retrieve the relayed property of the current value of the observed property of the new
     *                                        {@link BidirectionalRelayBinding}.
     * @param targetProperty                  the target property to be used for the new {@link BidirectionalRelayBinding}.
     * @param resetValue                      value to use when the target property gets unbound.
     *
     * @return a new {@link ResettableBidirectionalRelayBinding} which is used for the {@link #child}.
     *
     * @see BidirectionalRelayBinding
     * @see #attach(Function)
     * @see #consume(Function, Consumer, Consumer)
     * @see #bind(Function, Property)
     * @see #bind(Function, Property, Object)
     * @see #bindReverse(Function, ObservableValue)
     */
    public <TRelayedPropertyValueCascaded> ResettableBidirectionalRelayBinding<TRelayedValue, TRelayedPropertyValueCascaded> bindBidirectional(
            @NotNull final Function<TRelayedValue, Property<TRelayedPropertyValueCascaded>> relayProvider,
            @NotNull final Property<TRelayedPropertyValueCascaded> targetProperty,
            @Nullable final TRelayedPropertyValueCascaded resetValue) {
        return createChild(() -> new ResettableBidirectionalRelayBinding<>(relayProvider, targetProperty, resetValue));
    }


    // endregion

    // region Override RelayBinding

    /**
     * {@inheritDoc}This implementation will also invoke {@link BaseBinding#dispose()} on the {@link #child} and set it to to null.
     */
    @Override
    public void dispose() {
        super.dispose();
        disposeChild();
        child = null;
    }

    @Override
    protected void unbindProperty(final @Nullable ObservableValue<TRelayedValue> relayedObject) {
        destroyChild();
    }

    @Override
    protected void bindProperty(@Nullable final ObservableValue<TRelayedValue> relayedObject) {
        if (relayedObject != null) {
            resetChild(relayedObject);
        }
    }

    // endregion

    // region Child Binding

    /**
     * Calls {@link BaseBinding#createObservedProperty(ObservableValue)} if the current {@link #child} is set.
     */
    @SuppressWarnings ("unchecked")
    private void resetChild(final @NotNull ObservableValue<TRelayedValue> providedProperty) {
        if (child != null) {
            child.createObservedProperty(providedProperty);
        }
    }

    /**
     * Calls {@link BaseBinding#destroyObservedProperty()} if the current {@link #child} is set.
     */
    private void destroyChild() {
        if (child != null) {
            child.destroyObservedProperty();
        }
    }

    /**
     * Calls {@link BaseBinding#dispose()} if the current {@link #child} is set.
     */
    private void disposeChild() {
        if (child != null) {
            child.dispose();
        }
    }

    /**
     * Disposes the current {@link #child} and recreates a new {@link TBaseBinding} using the given {@link Supplier}.
     *
     * @param bindingCreator the {@link Supplier} used to create the new desired child.
     * @param <TBaseBinding> the type of the desired new child.
     *
     * @return a new {@link TBaseBinding} which as been set as the new {@link #child}.
     */
    @SuppressWarnings ("unchecked")
    @NotNull
    private <TBaseBinding extends BaseBinding> TBaseBinding createChild(@NotNull final Supplier<TBaseBinding> bindingCreator) {
        disposeChild();

        child = bindingCreator.get();

        assert child != null;

        // the property was already set because we had a change or already set up view model before a call to this method was made
        getCurrentObservedValue().ifPresent(value -> child.createObservedProperty(getRelayProvider().apply(value)));

        return (TBaseBinding) child;
    }

    // endregion
}