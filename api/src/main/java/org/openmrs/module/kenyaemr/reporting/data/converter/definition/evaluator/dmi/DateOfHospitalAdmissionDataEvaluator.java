/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.dmi;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dmi.AdmittedInICUOrHDUDataDefinition;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.dmi.DateOfHospitalAdmissionDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.SqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

/**
 * Evaluates a ComplaintAttendantProviderDataDefinition
 */
@Handler(supports= DateOfHospitalAdmissionDataDefinition.class, order=50)
public class DateOfHospitalAdmissionDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);

        String qry = "select a.patient_id, e.date_of_patient_admission\n" +
                "from (select patient_id, c.visit_date\n" +
                "      from kenyaemr_etl.etl_allergy_chronic_illness c\n" +
                "      where c.complaint = 143264\n" +
                "        and c.complaint_duration < 10\n" +
                "        and date(c.visit_date) between date(:startDate) and date(:endDate)\n" +
                "      group by patient_id) a\n" +
                "         join kenyaemr_etl.etl_clinical_encounter v\n" +
                "              on a.patient_id = v.patient_id and date(a.visit_date) = date(v.visit_date) and v.patient_outcome = 1654\n" +
                "         join kenyaemr_etl.etl_patient_triage t\n" +
                "              on a.patient_id = t.patient_id and date(t.visit_date) = date(v.visit_date) and t.temperature >= 38\n" +
                "         join kenyaemr_etl.etl_clinical_encounter e on a.patient_id = e.patient_id;";

        SqlQueryBuilder queryBuilder = new SqlQueryBuilder();
        queryBuilder.append(qry);
        Date startDate = (Date)context.getParameterValue("startDate");
        Date endDate = (Date)context.getParameterValue("endDate");
        queryBuilder.addParameter("endDate", endDate);
        queryBuilder.addParameter("startDate", startDate);
        Map<Integer, Object> data = evaluationService.evaluateToMap(queryBuilder, Integer.class, Object.class, context);
        c.setData(data);
        return c;
    }
}