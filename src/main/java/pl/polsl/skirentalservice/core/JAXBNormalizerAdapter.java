/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class JAXBNormalizerAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String text) {
        return text.trim();
    }

    @Override
    public String marshal(String text) {
        return text.trim();
    }
}
