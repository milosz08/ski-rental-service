/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JAXBNormalizerAdapter.java
 * Last modified: 2/10/23, 7:42 PM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.core;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class JAXBNormalizerAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String text) {
        return text.trim();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String marshal(String text) {
        return text.trim();
    }
}
