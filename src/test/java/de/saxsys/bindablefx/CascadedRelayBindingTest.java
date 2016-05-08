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

import de.saxsys.bindablefx.mocks.A;
import de.saxsys.bindablefx.mocks.B;
import de.saxsys.bindablefx.mocks.C;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author xyanid on 31.03.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CascadedRelayBindingTest {

    //region Tests

    /**
     * Tests if main items can be added and removed.
     */
    @Test
    public void oneLevelUnidirectionalBindingWorks() {

        ObjectProperty<Long> otherD = new SimpleObjectProperty<>();

        A a = new A();

        a.bProperty().bind();

        // this should work and binds xProperty bidirectional as soon as it is se to otherD
        Bindings.bindRelayedCascaded(a.bProperty(), B::cProperty).bind(C::xProperty, otherD);

        ListView<A> listView = new ListView<>();

        ReadOnlyListWrapper<A> item = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

        ObjectProperty<A> selectedItem = new SimpleObjectProperty<>();

        selectedItem.bind(listView.getSelectionModel().selectedItemProperty());
    }


    // endregion
}
