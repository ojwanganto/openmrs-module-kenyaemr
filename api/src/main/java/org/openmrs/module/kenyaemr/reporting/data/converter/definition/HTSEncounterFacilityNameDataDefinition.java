/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * Visit ID Column
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class HTSEncounterFacilityNameDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty
    private Integer encType;
    /**
     * Default Constructor
     */
    public HTSEncounterFacilityNameDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public HTSEncounterFacilityNameDataDefinition(String name) {
        super(name);
    }

    public HTSEncounterFacilityNameDataDefinition(Integer type) {
        this.encType = type;
    }

    //***** INSTANCE METHODS *****

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Double.class;
    }



    public Integer getEncType() {
        return encType;
    }

    public void setEncType(Integer encType) {
        this.encType = encType;
    }
}
