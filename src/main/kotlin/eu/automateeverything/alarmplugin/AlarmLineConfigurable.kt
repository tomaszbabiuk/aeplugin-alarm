/*
 * Copyright (c) 2019-2022 Tomasz Babiuk
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.automateeverything.alarmplugin

import eu.automateeverything.data.automation.State
import eu.automateeverything.data.instances.InstanceDto
import eu.automateeverything.domain.automation.AutomationUnit
import eu.automateeverything.domain.configurable.*
import eu.automateeverything.domain.events.EventBus
import eu.automateeverything.domain.hardware.BinaryInput
import eu.automateeverything.domain.hardware.PortFinder
import org.pf4j.Extension

@Extension
class AlarmLineConfigurable(private val portFinder: PortFinder, private val eventBus: EventBus) :
    StateDeviceConfigurable() {

    override val parent: Class<out Configurable>
        get() = AlarmDevicesConfigurable::class.java

    private val portField =
        BinaryInputPortField(FIELD_PORT, R.field_port_hint, RequiredStringValidator())
    private val contactTypeField = ContactTypeField(FIELD_CONTACT_TYPE, R.field_contact_type_hint)
    private val delayTimeField =
        DurationField(FIELD_DELAY_TIME, R.field_delay_time_hint, Duration(0))

    override fun buildAutomationUnit(instance: InstanceDto): AutomationUnit<State> {
        val portId = extractFieldValue(instance, portField)
        val port = portFinder.searchForInputPort(BinaryInput::class.java, portId)
        val name = extractFieldValue(instance, nameField)
        val contactType = extractFieldValue(instance, contactTypeField)
        val delayTime = extractFieldValue(instance, delayTimeField)

        return AlarmLineAutomationUnit(
            eventBus,
            instance,
            name,
            states,
            port,
            contactType,
            delayTime
        )
    }

    override val states: Map<String, State>
        get() {
            val states: MutableMap<String, State> = HashMap()
            states[STATE_INIT] =
                State.buildReadOnlyState(
                    STATE_INIT,
                    R.state_unknown,
                )
            states[STATE_DISARMED] =
                State.buildReadOnlyState(
                    STATE_DISARMED,
                    R.state_disarmed,
                )
            states[STATE_WATCHING] =
                State.buildReadOnlyState(
                    STATE_WATCHING,
                    R.state_watching,
                )
            states[STATE_PREALARM] =
                State.buildReadOnlyState(
                    STATE_PREALARM,
                    R.state_prealarm,
                )
            states[STATE_ALARM] =
                State.buildReadOnlyState(
                    STATE_ALARM,
                    R.state_alarm,
                )
            return states
        }

    override val fieldDefinitions: Map<String, FieldDefinition<*>>
        get() {
            val result: MutableMap<String, FieldDefinition<*>> =
                LinkedHashMap(super.fieldDefinitions)
            result[FIELD_PORT] = portField
            result[FIELD_CONTACT_TYPE] = contactTypeField
            result[FIELD_DELAY_TIME] = delayTimeField
            return result
        }

    override val addNewRes = R.configurable_alarmline_add
    override val editRes = R.configurable_alarmline_edit
    override val titleRes = R.configurable_alarmlines_title
    override val descriptionRes = R.configurable_alarmlines_description

    override val iconRaw: String
        get() =
            """
            <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" id="Layer_1" x="0px" y="0px" width="100px" height="100px" viewBox="0 0 100 100" enable-background="new 0 0 100 100" xml:space="preserve">
            <path d="M96.92,32.188c0-8.444-21.007-15.289-46.92-15.289c-25.911,0-46.917,6.845-46.917,15.289L0,48.305  c0,1.223,0.39,2.423,1.115,3.578C4.081,43.873,24.479,37.86,50,37.86c25.523,0,45.921,6.013,48.89,14.022  c0.721-1.155,1.11-2.355,1.11-3.578L96.92,32.188z"/>
            <path d="M49.998,39.36c-25.335,0-46.812,6.375-47.726,14.037c2.297,2.502,8.652,10.306,8.652,10.306  c0.025,0.009,0.047,0.014,0.072,0.022c0.589,0.788,1.653,1.538,3.18,2.238c-0.301,0.424-0.5,0.855-0.5,1.299  c0,0.855,4.587,6.68,5.669,7.463c0,0.019,0.004,0.034,0.006,0.052c-0.194,0.078-0.397,0.154-0.58,0.235  c-0.033,0.147-0.054,0.291-0.054,0.438c0,4.227,14.004,7.651,31.281,7.651c17.282,0,31.286-3.425,31.286-7.651  c0-0.147-0.021-0.291-0.055-0.438c-0.184-0.081-0.387-0.157-0.578-0.235c0-0.021,0.006-0.033,0.006-0.054  c1.088-0.788,5.668-6.605,5.668-7.461c0-0.443-0.199-0.875-0.498-1.301c1.521-0.698,2.584-1.448,3.174-2.234  c0.029-0.009,0.051-0.016,0.078-0.024c0,0,6.354-7.804,8.65-10.306C96.814,45.735,75.338,39.36,49.998,39.36z M82,63.469  c-3-0.713-3.663-1.365-6.134-1.934l0.747-2.183C80.043,60.527,82,61.94,82,63.461C82,63.463,82,63.465,82,63.469z M18,63.469  c0-0.004,0-0.006,0-0.008c0-0.439,0.107-0.867,0.425-1.283c1.138-0.806,2.493-1.75,4.484-2.68c0.132-0.049,0.224-0.099,0.361-0.146  l0.873,2.183C21.671,62.105,20,62.756,18,63.469z M26.377,58.325C31.795,57.04,41,56.209,47,56.091v3  c-6,0.094-14.324,0.712-19.975,1.699L26.377,58.325z M47,64.48v6.071c-4,0.079-10.937,0.506-15.738,1.202l-2.219-5.414  C33.776,65.275,41,64.594,47,64.48z M52,64.48c7,0.113,13.472,0.795,18.206,1.859l-1.972,5.414C63.434,71.058,59,70.631,52,70.552  V64.48z M52,59.091v-3c9,0.118,15.702,0.949,21.12,2.234l-0.897,2.465C66.57,59.803,59,59.185,52,59.091z M21.376,70.896  c0-0.83,0.724-1.623,1.999-2.35c0.265-0.154,0.566-0.305,0.882-0.45c0.565-0.263,1.184-0.517,1.895-0.757l1.835,5.048  c-1.576,0.3-3.035,0.636-4.384,0.996C22.175,72.618,21.376,71.779,21.376,70.896z M72.012,72.386l1.836-5.046  c0.705,0.237,1.32,0.49,1.883,0.75c0.316,0.147,0.617,0.295,0.885,0.449c1.281,0.73,2.008,1.525,2.008,2.357  c0,0.883-0.799,1.722-2.227,2.487C75.047,73.023,73.588,72.688,72.012,72.386z"/>
            </svg>
        """
                .trimIndent()

    companion object {
        const val FIELD_PORT = "portId"
        const val FIELD_CONTACT_TYPE = "inactiveState"
        const val FIELD_DELAY_TIME = "delayTime"
        const val STATE_DISARMED = "disarmed"
        const val STATE_WATCHING = "watching"
        const val STATE_PREALARM = "prealarm"
        const val STATE_ALARM = "alarm"
    }
}
