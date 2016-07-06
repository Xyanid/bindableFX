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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Optional;

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
 * {@link BaseBinding} comes into play and handles this by attaching listeners cascadingly so that we can bind to D with no concern that B or C might be
 * null, change or become invalid.
 * <p>
 * e.g. using the above example, the code to safely bind to D would look like this.
 * <pre>
 * {@code
 * A a = new A();
 * ObjectProperty<Long> property = new SimpleObjectProperty<>();
 *
 * new NestedBinding(a.bProperty(), B::cProperty).bindBidirectional(C::dProperty, property, false);
 * }
 * </pre>
 *
 * @author xyanid on 30.03.2016.
 */
public abstract class BaseBinding<TValue> extends ObjectBinding<TValue> implements ChangeListener<TValue>, WeakListener {

    // region Fields

    /**
     * Determines the {@link ObservableValue} which is watched by this binding.
     */
    @Nullable
    private WeakReference<ObservableValue<TValue>> observedValue;

    // endregion

    // region Constructor

    protected BaseBinding() {
    }

    // endregion

    // region Getter

    /**
     * Returns the current value of the {@link #observedValue}.
     *
     * @return {@link Optional#empty()} if the {@link #observedValue} is null or an {@link Optional} of the current value of the {@link #observedValue}.
     */
    public Optional<ObservableValue<TValue>> getObservedValue() {
        if (observedValue == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(observedValue.get());
    }

    /**
     * Returns the current value of the {@link #observedValue}.
     *
     * @return {@link Optional#empty()} if the {@link #observedValue} is null or an {@link Optional} of the current value of the {@link #observedValue}.
     */
    @Override
    public TValue getValue() {

        final Optional<ObservableValue<TValue>> result = getObservedValue();
        if (result.isPresent()) {
            return result.get().getValue();
        } else {
            return null;
        }
    }

    // endregion

    // region Observed value

    /**
     * Removes this binding as the listener from the {@link #observedValue}, invokes a call to {@link #changed(ObservableValue, Object, Object)} with the oldValue and then
     * sets the {@link #observedValue} to null.
     */
    private void destroyObservedValue() {
        if (observedValue != null) {
            ObservableValue<TValue> observedValue = this.observedValue.get();
            if (observedValue != null) {
                observedValue.removeListener(this);
                changed(observedValue, observedValue.getValue(), null);
            }
            this.observedValue = null;
        }
    }

    /**
     * Sets the {@link #observedValue} and adds the this binding as the listener.
     *
     * @param observableValue the {@link ObservableValue} which will be used as the {@link #observedValue}
     */
    void setObservedValue(@NotNull final ObservableValue<TValue> observableValue) {
        // set the property that is being observe and invoke a change so that the implementation can bind the property correctly
        this.observedValue = new WeakReference<>(observableValue);
        observableValue.addListener(this);
        changed(observableValue, null, observableValue.getValue());
    }

    // endregion

    // region Change Handling

    /**
     * Returns the current value of the {@link #observedValue} if any.
     *
     * @return the current value of the {@link #observedValue} if any.
     */
    @Override
    protected TValue computeValue() {
        return getValue();
    }

    /**
     * When the observed value is changed, this binding is invalidated.
     */
    @Override
    public void changed(@Nullable final ObservableValue<? extends TValue> observable, @Nullable final TValue oldValue, @Nullable final TValue newValue) {
        invalidate();
    }

    /**
     * Returns true if the {@link #observedValue} is no longer set.
     *
     * @return true if the {@link #observedValue} is no longer set, otherwise false.
     */
    @Override
    public boolean wasGarbageCollected() {
        return observedValue != null && observedValue.get() == null;
    }

    //endregion

    // region Public

    /**
     * Stops listening to the observed value.
     */
    public void dispose() {
        destroyObservedValue();
    }

    // endregion
}