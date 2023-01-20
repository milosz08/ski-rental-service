/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: JAXBNormalizerAdapter.java
 *  Last modified: 19/01/2023, 20:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
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
