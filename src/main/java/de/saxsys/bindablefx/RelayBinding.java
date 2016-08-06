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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.function.Function;

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
 * {@link RelayBinding} comes into play and handles this by attaching listeners cascadingly so that we can bind to D with no concern that B or C might be
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
public class RelayBinding<TParentValue, TValue, TObservedValue extends ObservableValue<TValue>> extends RootBinding<TValue> {

    // region Fields

    /**
     * The current parent that is used in this binding.
     */
    @NotNull
    private final WeakReference<ObservableValue<TParentValue>> parent;

    /**
     * The {@link ChangeListener} that is attached to the parent.
     */
    @NotNull
    private final ChangeListener<TParentValue> onParentChanged;

    // endregion

    // region Constructor

    RelayBinding(@NotNull final ObservableValue<TParentValue> parent, @NotNull final Function<TParentValue, TObservedValue> relayResolver) {
        this.onParentChanged = (observable, oldValue, newValue) -> {
            destroyObservedValue();
            if (newValue != null) {
                setObservedValue(relayResolver.apply(newValue));
            }
            invalidate();
        };
        this.parent = new WeakReference<>(parent);
        parent.addListener(this.onParentChanged);
        onParentChanged.changed(parent, null, parent.getValue());
    }

    // endregion

    // region Override RootBinding

    /**
     * {@inheritDoc} Also stops listening to the {@link #parent}.
     */
    @Override
    public void dispose() {
        super.dispose();
        final ObservableValue<TParentValue> parent = this.parent.get();
        if (parent != null) {
            parent.removeListener(onParentChanged);
        }
    }

    // endregion
}