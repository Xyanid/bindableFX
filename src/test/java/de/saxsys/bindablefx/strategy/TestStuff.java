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

import de.saxsys.bindablefx.mocks.A;
import de.saxsys.bindablefx.mocks.B;
import de.saxsys.bindablefx.mocks.C;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.fxmisc.easybind.EasyBind;
import org.junit.Test;

/**
 * @author Xyanid on 09.07.2016.
 */
public class TestStuff {

    @Test
    public void testEasyBind() {

        final A a = new A();

        final ObjectProperty<Long> x = new SimpleObjectProperty<>();

        x.addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });

        EasyBind.select(a.bProperty()).selectObject(B::cProperty).selectProperty(C::xProperty);


        x.bindBidirectional(EasyBind.select(a.bProperty()).selectObject(B::xProperty).orElse(new SimpleObjectProperty<>(0L)));

        a.bProperty().setValue(new B());
        a.bProperty().getValue().xProperty().setValue(1L);

        a.bProperty().setValue(new B());
    }

}
