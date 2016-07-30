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

import org.jetbrains.annotations.Nullable;

/**
 * This interface is used to convert from a givne value into a desired value and vice versa.
 *
 * @author Xyanid on 29.07.2016.
 */
public interface IConverter<TValue, TConvertedValue> {

    /**
     * Converts the base value into the desired converted value.
     *
     * @param value the base value to use.
     *
     * @return the converted value.
     */
    @Nullable TConvertedValue convertTo(@Nullable final TValue value);

    /**
     * Converts back the converted value into the base value.
     *
     * @param value the converted value to use.
     *
     * @return the base value.
     */
    @Nullable TValue convertBack(@Nullable final TConvertedValue value);
}
