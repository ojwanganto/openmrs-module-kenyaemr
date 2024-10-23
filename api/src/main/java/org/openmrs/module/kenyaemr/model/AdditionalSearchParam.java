/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.model;

public class AdditionalSearchParam {
    private String additionalSearchHandler;
    private String tests;

    public AdditionalSearchParam(String additionalSearchHandler, String tests) {
        this.additionalSearchHandler = additionalSearchHandler;
        this.tests = tests;
    }

    public AdditionalSearchParam() {
    }

    public String getAdditionalSearchHandler() {
        return additionalSearchHandler;
    }

    public void setAdditionalSearchHandler(String additionalSearchHandler) {
        this.additionalSearchHandler = additionalSearchHandler;
    }

    public String getTests(){
        return tests;
    }

    public void setTests(String tests){
        this.tests = tests;
    }
}
