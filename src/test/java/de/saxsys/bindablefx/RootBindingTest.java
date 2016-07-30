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
import javafx.scene.control.Label;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Xyanid on 27.07.2016.
 */
@RunWith (MockitoJUnitRunner.class)
public class RootBindingTest {

    //region Fields

    private A a;

    private RootBinding<B> cut;

    //endregion

    //region Setup

    @Before
    public void setUp() {

        a = new A();

        cut = Bindings.observe(a.bProperty());
    }

    //endregion

    // region Tests

    public void test() {
        Label label = new Label().textProperty().bindBidirectional();
    }

    // endregion
}