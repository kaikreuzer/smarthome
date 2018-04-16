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

/**
 * The {@link APIRequest} class handles the API requests
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public enum APIRequest {
    KEY("key"),
    SELECT("select"),
    SOURCES("sources"),
    BASSCAPABILITIES("bassCapabilities"),
    BASS("bass"),
    GET_ZONE("getZone"),
    SET_ZONE("setZone"),
    ADD_ZONE_SLAVE("addZoneSlave"),
    REMOVE_ZONE_SLAVE("removeZoneSlave"),
    NOW_PLAYING("now_playing"),
    TRACK_INFO("trackInfo"),
    VOLUME("volume"),
    PRESETS("presets"),
    INFO("info"),
    NAME("name");

    private String name;

    private APIRequest(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}