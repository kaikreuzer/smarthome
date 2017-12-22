/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.bluetooth.bluegiga;

import org.eclipse.smarthome.binding.bluetooth.BluetoothBindingConstants;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link BlueGigaBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Chris Jackson - Initial contribution
 */
public class BlueGigaBindingConstants {

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BLUEGIGA = new ThingTypeUID(BluetoothBindingConstants.BINDING_ID,
            "bluegiga");

    public static final String CONFIGURATION_PORT = "port";
    public static final String PROPERTY_LINKLAYER = "linklayer";
    public static final String PROPERTY_PROTOCOL = "protocol";
}
