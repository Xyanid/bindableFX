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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author xyanid on 31.03.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class BidirectionalBindingTest {

    //region Tests

    /**
     * Tests if the binding of a property works as expected in this case we create a new A on which we will listen to the B property, once this is set we
     * will get the C property of the B property and bind towards the X property.
     * <p>
     * e.G. we would like build something like this
     * <pre>
     * {@code
     * A a = new A();
     * ObjectProperty<Long> property = new SimpleObjectProperty<>();
     * a.getB().getC().dProperty().bindBidirectional(property);
     * }
     * </pre>
     * However since B and C might be null we would need to listen to the values to become available at some point in time
     */
    @Test
    public void bindingAPropertyWorksAsExpected() {


    }


    // endregion
}
