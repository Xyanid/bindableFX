package de.saxsys.bindablefx;

import de.saxsys.bindablefx.mocks.A;
import de.saxsys.bindablefx.mocks.B;
import de.saxsys.bindablefx.mocks.C;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author x1rhents on 31.03.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class IntermediateBindingTest {

    //region Tests

    /**
     * Tests if main items can be added and removed.
     */
    @Test
    public void simpleIntermediateBinding() {

        ObjectProperty<Long> otherD = new SimpleObjectProperty<>();

        A a = new A();

        // does not work since b and c could be null
        a.getB().getC().xProperty().bind(otherD);

        // this should work and binds xProperty as soon as it is se to otherD
        CascadedBinding binding = new CascadedBinding<>(a.bProperty(), B::cProperty).attach(C::dProperty);

        // this should work and binds xProperty bidirectional as soon as it is se to otherD
        CascadedBinding bindingBidirectional = new CascadedBinding<>(a.bProperty(), b -> b.cProperty()).bindBidirectional(cProperty -> cProperty.dProperty(), otherD);
    }

    /**
     * Tests if main items can be added and removed.
     */
    @Test
    public void multiplePropertiesInARow() {

        ObjectProperty<Long> otherD = new SimpleObjectProperty<>();

        A a = new A();

        // does not work since b, c, d and e could be null
        a.getB().getC().getD().getE().fProperty().bind(otherD);


        // this should work for more complex structures where we have mulitple intermediate bindings in a line
        CascadedBinding MulitpleIntermediateBinding = new CascadedBinding<>(a.bProperty(), B::cProperty).bind(cProperty -> cProperty.dProperty())
                                                                                                        .bind(dProperty -> {
                                                                                                                           return dProperty.eProperty();
                                                                                                                       })
                                                                                                        .bind(eProperty -> {
                                                                                                                           return eProperty.fProperty();
                                                                                                                       }, otherD);


        CascadedBinding MulitpleIntermediateBindingBidirectional = new CascadedBinding<>(a.bProperty(), b -> {
            return b.cProperty();
        }).bind(cProperty -> {
            return cProperty.dProperty();
        }).bind(dProperty -> {
            return dProperty.eProperty();
        }).bindBidirectional(eProperty -> {
            return eProperty.fProperty();
        }, otherD);
    }

    /**
     * Tests if main items can be added and removed.
     */
    @Test
    public void finalBindingProperties() {

        ObjectProperty<Long> otherD = new SimpleObjectProperty<>();

        A a = new A();

        // a more complex scenario where we have multiple intermediate bindings in a line
        a.getB().yProperty().bind(otherD);

        // this should work for more complex structures where we have mulitple intermediate bindings in a line
        UnidirectionalBinding finalBinding = new UnidirectionalBinding<>(a.bProperty(), bProperty -> {
            return bProperty.yProperty();
        }, otherD);
    }

    // endregion
}
