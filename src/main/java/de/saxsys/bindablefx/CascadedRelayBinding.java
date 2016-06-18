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
import org.jetbrains.annotations.Nullable;

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
public final class CascadedRelayBinding<TValue, TComputedValue> extends RelayBinding<TValue, TComputedValue> implements ICascadeBuilder {

    // region Fields

    /**
     * The child binding held by this cascaded binding.
     */
    @Nullable
    private RelayBinding<ObservableValue<TComputedValue>, ?> child;

    // endregion

    // region Constructor

    CascadedRelayBinding() {
    }

    // endregion

    // region Child Binding

    /**
     * Calls {@link RelayBinding#dispose()} if the current {@link #child} is set.
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
    private <TBaseBinding extends RelayBinding<ObservableValue<TComputedValue>, ?>> TBaseBinding createChild(@NotNull final Supplier<TBaseBinding> bindingCreator) {
        disposeChild();

        child = bindingCreator.get();

        assert child != null;

        child.setObservedValue(this);

        return (TBaseBinding) child;
    }

    // endregion

    // region Override RelayBinding

    /**
     * {@inheritDoc}
     * This implementation will also invoke {@link RelayBinding#dispose()} on the {@link #child} and set it to to null.
     */
    @Override
    public void dispose() {
        super.dispose();
        disposeChild();
        child = null;
    }

    // endregion

    // region Public

    public ICascadeBuilder wait(Function<TValue, ObservableValue<TComputedValue>> relayProvider) {
        setRelayProvider(relayProvider);
        return this;
    }

    public <TCascadedComputedValue> ICascadeBuilder waitFor(@NotNull final Function<ObservableValue<TComputedValue>, ObservableValue<TCascadedComputedValue>> relayProvider) {

        CascadedRelayBinding<ObservableValue<TComputedValue>, TCascadedComputedValue>
                result =
                createChild(CascadedRelayBinding<ObservableValue<TComputedValue>, TCascadedComputedValue>::new);
        result.setRelayProvider(relayProvider);
        return result;
    }

    // endregion
}