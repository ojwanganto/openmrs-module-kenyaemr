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
import org.openmrs.Patient;
import org.openmrs.module.appointments.model.Appointment;
import org.openmrs.module.appointments.notification.AppointmentEventNotifier;
import org.openmrs.module.appointments.notification.NotificationException;
import org.openmrs.module.appointments.notification.NotificationResult;

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
        // just check if appointment is of type HIV follow up. We only want to limit this to appointment updates since creation is done in green card
        if (appointment.getServiceType().equals("HIV follow up")) { // TODO: complete the check
            return true;
        }
        return false;
    }

    @Override
    public NotificationResult sendNotification(final Appointment appointment) throws NotificationException {
        // Check if the patient has an HIV followup visit on the appointment date. Align the date with that in the green card form
        Patient patient = appointment.getPatient();
        log.info(appointment);
        return new NotificationResult("", "EMAIL", 0, APPOINTMENT_SYNCHRONIZED);

    }

}
