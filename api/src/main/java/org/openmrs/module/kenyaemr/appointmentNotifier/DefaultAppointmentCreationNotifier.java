/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.appointmentNotifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.model.AppointmentServiceDefinition;
import org.openmrs.module.appointments.notification.AppointmentEventNotifier;
import org.openmrs.module.appointments.notification.NotificationException;
import org.openmrs.module.appointments.notification.NotificationResult;
import org.openmrs.module.appointments.service.AppointmentsService;
import org.openmrs.module.kenyaemr.advice.SyncHFEAppointmentsWithBahmniModule;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * This class listens to events in the Bahmni appointments module
 * and synchronizes appointment date with that in the HIV followup form, the green card.
 * This is required since KenyaEMR still uses the date recorded in the follow up form to compute TX CURR
 * This class should be ignored once the EMR fully uses appointments module for managing all appointments
 */
public class DefaultAppointmentCreationNotifier implements AppointmentEventNotifier {
    private static final String APPOINTMENT_SYNCHRONIZED = "Appointment date synchronized successfully";
    private static final String MEDIUM_NONE = "NONE";

    private Log log = LogFactory.getLog(this.getClass());

    public DefaultAppointmentCreationNotifier() {}

    @Override
    public String getMedium() {
        return MEDIUM_NONE;
    }

    @Override
    public boolean isApplicable(final Appointment appointment) {
        AppointmentsService appointmentsService = Context.getService(AppointmentsService.class);
        //AppointmentServiceDefinition definition = appoi
        log.info("Appointment ID===============: " + appointment.getService().getName());
        /**
         * We don't want notification if it's a newly created Green card
         */
        if (appointment.getService().getUuid().equalsIgnoreCase(SyncHFEAppointmentsWithBahmniModule.HIV_FOLLOWUP_SERVICE)) {

            return true;
        }

        return false;
    }

    @Override
    public NotificationResult sendNotification(final Appointment appointment) throws NotificationException {
        // Check if the patient has an HIV followup visit on the appointment date. Align the date with that in the green card form
        Patient patient = appointment.getPatient();

        VisitService visitService = Context.getVisitService();
        EncounterService encounterService = Context.getEncounterService();
        FormService formService = Context.getFormService();
        Form hivRdeForm = formService.getFormByUuid(HivMetadata._Form.MOH_257_VISIT_SUMMARY);
        Form hivGreencard = formService.getFormByUuid(HivMetadata._Form.HIV_GREEN_CARD);

        Calendar cal = Calendar.getInstance();
        cal.setTime(appointment.getDateCreated());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        System.out.println("Appointment date: " + cal.getTime());

        EncounterSearchCriteriaBuilder searchCriteriaBuilder = new EncounterSearchCriteriaBuilder();
        searchCriteriaBuilder.setPatient(patient);
        searchCriteriaBuilder.setFromDate(cal.getTime()); // we are checking for encounters created on same day as appointment
        searchCriteriaBuilder.setEnteredViaForms(Arrays.asList(hivGreencard, hivRdeForm));
        List<Encounter> hivFollowupEncounters = encounterService.getEncounters(searchCriteriaBuilder.createEncounterSearchCriteria());
        Integer tcaConceptId = 5096;

        // we want to pick just one encounter
        Encounter followupEncounter = null;
        if (hivFollowupEncounters.size() >= 1) {
            followupEncounter = hivFollowupEncounters.get(0);
        }

        System.out.println("Encounter size:::::::::::::::;" + hivFollowupEncounters.size());
        if (followupEncounter != null) {
            Set<Obs> encounterObs = followupEncounter.getObs();
            Obs appointmentObs = null;
            for (Obs o : encounterObs) {
                if (o.getConcept().getConceptId().equals(tcaConceptId)) {
                    appointmentObs = o;
                    break;
                }
            }
            appointmentObs.setValueDatetime(appointment.getStartDateTime());
            Context.getObsService().saveObs(appointmentObs, "Updated appointment date");
        }
        return new NotificationResult("", getMedium(), 0, APPOINTMENT_SYNCHRONIZED);

    }

}
