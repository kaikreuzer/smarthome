/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.binding.bosesoundtouch.internal;

import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.PrimitiveType;
import org.eclipse.smarthome.core.types.State;

/**
 * The {@link OperationModeType} class is holding all OperationModes
 *
 * @author Christian Niessner - Initial contribution
 * @author Thomas Traunbauer - Initial contribution
 */
public enum OperationModeType implements PrimitiveType, State, Command {
    OFFLINE,
    STANDBY,
    INTERNET_RADIO,
    BLUETOOTH,
    AUX,
    AUX1,
    AUX2,
    AUX3,
    SPOTIFY,
    PANDORA,
    DEEZER,
    SIRIUSXM,
    STORED_MUSIC,
    AMAZON,
    TV,
    HDMI1,
    OTHER;

    private String name;

    private OperationModeType() {
        this.name = name();
    }

    @Override
    public String format(String pattern) {
        return String.format(pattern, this.toString());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toFullString() {
        return toString();
    }
}