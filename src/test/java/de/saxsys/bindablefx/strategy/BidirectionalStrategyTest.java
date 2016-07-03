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

package de.saxsys.bindablefx.strategy;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Xyanid on 03.07.2016.
 */
public class BidirectionalStrategyTest {

    // region Fields

    private ObjectProperty<Long> x;

    private BidirectionalStrategy<Long> cut;

    // endregion

    // region Setup

    @Before
    public void setUp() {
        x = new SimpleObjectProperty<>();
        cut = new BidirectionalStrategy<>(x);
    }

    // endregion

    //region Tests

    //region Initialization

    /**
     * When the strategy is computed the new property, will be bound against the target.
     */
    @Test
    public void whenTheStrategyIsComputedTheNewPropertyWillBeBound() {

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(2L);

        cut.computeValue(newValue);

        assertEquals(2L, x.get().longValue());

        newValue.setValue(3L);

        assertEquals(3L, x.get().longValue());
    }

    /**
     * When the strategy is computed the old property, will be unbound.
     */
    @Test
    public void whenTheStrategyIsComputedTheOldPropertyWillBeUnbound() {

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(0L);

        cut.computeValue(newValue);

        cut.computeValue(null);

        newValue.setValue(3L);

        assertEquals(0L, x.get().longValue());
    }

    //endregion

    //region Changing

    /**
     * When the target property is changed after the observed property was changed, the relayed property will be changed as well.
     */
    @Test
    public void whenTheTargetIsChangedTheBoundPropertyWillAlsoBeChanged() {

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(0L);

        cut.computeValue(newValue);

        x.setValue(2L);

        assertEquals(2L, newValue.get().longValue());
    }

    /**
     * When the target property is changed before the observed property is changed, the relayed property will be changed as well.
     */
    @Test
    public void changingTheTargetPropertyBeforeTheObservedPropertyWillAdjustTheTargetProperty() {

        x.setValue(2L);

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(0L);

        cut.computeValue(newValue);

        assertEquals(0L, newValue.get().longValue());
    }

    /**
     * When the target property is already set and the observed property get set, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyIsAlreadySetAndTheObservedPropertyChangesTheRelayedPropertyWillHaveTheSameValue() {

        //        x.setValue(2L);
        //
        //        cut = new BidirectionalStrategy<>(a.bProperty(), B::xProperty, x);
        //
        //        a.bProperty().setValue(new B());
        //
        //        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the target property get set and the observed property is already set, the relayed property have the same value as the target property.
     */
    @Test
    public void whenTheTargetPropertyChangesAndTheObservedPropertyIsAlreadySetTheRelayedPropertyWillHaveTheSameValue() {

        //        a.bProperty().setValue(new B());
        //
        //        cut = new BidirectionalStrategy<>(a.bProperty(), B::xProperty, x);
        //
        //        x.setValue(2L);
        //
        //        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property changes the target property will have the same value
     */
    @Test
    public void whenTheRelayedPropertyChangesTheTargetPropertyWillHaveTheSameValue() {

        //        a.bProperty().setValue(new B());
        //
        //        cut = new BidirectionalStrategy<>(a.bProperty(), B::xProperty, x);
        //
        //        a.bProperty().getValue().xProperty().setValue(2L);
        //
        //        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property is already set, the target property will have the same value
     */
    @Test
    public void whenTheRelayedPropertyIsAlreadySetTheTargetPropertyWillHaveTheSameValue() {

        //        a.bProperty().setValue(new B());
        //        a.bProperty().getValue().xProperty().setValue(2L);
        //
        //        cut = new BidirectionalStrategy<>(a.bProperty(), B::xProperty, x);
        //
        //        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
    }

    /**
     * When the relayed property is set to null and the binding is supposed to not set the target property to null, the target property will remain its old value.
     */
    @Test
    public void whenTheStrategyIsDisposedTheTargetPropertyShallNotBeResetTheTargetPropertyRemainItsOldValue() {
        //
        //        a.bProperty().setValue(new B());
        //
        //        cut = new BidirectionalStrategy<>(a.bProperty(), B::xProperty, x);
        //
        //        a.bProperty().getValue().xProperty().setValue(2L);
        //
        //        assertEquals(x.getValue(), a.bProperty().getValue().xProperty().getValue());
        //
        //        a.bProperty().setValue(null);
        //
        //        assertEquals(2L, x.getValue().longValue());
    }

    //endregion

    // region Disposing

    /**
     * When the target property has no hard reference, the strategy will also no longer have a hard reference.
     */
    @Test
    public void whenTheTargetPropertyHasNoHardReferenceTheStrategyWillAlsoNoLongerHaveAReference() {

        x = null;

        System.gc();

        assertNull(cut.getTarget());
    }

    /**
     * When the target property has no hard reference and was bound via the strategy, the strategy will also have no reference to it anymore.
     */
    @Test
    public void whenTheTargetPropertyHasNoHardReferenceAndWasBoundTheStrategyWillAlsoNoLongerHaveAReference() {

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(2L);

        cut.computeValue(newValue);

        x = null;

        System.gc();

        assertNull(cut.getTarget());
    }

    /**
     * When the binding is disposed, changes made to the relayed property will not affect the target property.
     */
    @Test
    public void disposingTheStrategyWillPreventThePropertyToAffectTheTargetProperty() {

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(0L);

        cut.computeValue(newValue);

        cut.dispose();

        newValue.setValue(3L);

        assertEquals(0L, x.get().longValue());
    }

    /**
     * When the binding is disposed, changes made to the target property will not affect the relayed property.
     */
    @Test
    public void disposingTheStrategyWillPreventTheTargetPropertyToAffectTheRelayedProperty() {

        final ObjectProperty<Long> newValue = new SimpleObjectProperty<>(0L);

        cut.computeValue(newValue);

        cut.dispose();

        x.setValue(3L);

        assertEquals(0L, newValue.get().longValue());
    }

    // endregion

    // endregion
}