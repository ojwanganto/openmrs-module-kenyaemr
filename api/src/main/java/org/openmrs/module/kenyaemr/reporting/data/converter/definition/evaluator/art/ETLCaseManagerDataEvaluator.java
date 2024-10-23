/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.reporting.data.converter.definition.evaluator.art;

import org.openmrs.annotation.Handler;
import org.openmrs.module.kenyaemr.reporting.data.converter.definition.art.ETLCaseManagerDataDefinition;
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
 * Evaluates Patient Case Manager Date DataDefinition
 */
@Handler(supports= ETLCaseManagerDataDefinition.class, order=50)
public class ETLCaseManagerDataEvaluator implements PersonDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public EvaluatedPersonData evaluate(PersonDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedPersonData c = new EvaluatedPersonData(definition, context);


        String qry = "SELECT t.patient_id, t.latest_case_manager from (SELECT r.person_b as patient_id,r.person_b AS case_manager_id, mid(max(concat(date(r.start_date),\n" +
                "        concat_ws( ' ', pn.family_name, pn.given_name, pn.middle_name ))), 11) as latest_case_manager, max(r.start_date)\n" +
                "        as start_date FROM relationship r INNER JOIN relationship_type t ON r.relationship = t.relationship_type_id\n" +
                "       INNER JOIN person_name pn ON r.person_a = pn.person_id WHERE t.uuid = '9065e3c6-b2f5-4f99-9cbf-f67fd9f82ec5'\n" +
                "       and date(r.start_date) <= (:endDate) and r.end_date is null and r.voided = 0 GROUP BY case_manager_id) as t;";

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
