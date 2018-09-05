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
package org.eclipse.smarthome.binding.sonyaudio.internal.protocol;

import java.net.URI;

import com.google.gson.JsonObject;

/**
 * The {@link SonyAudioClientSocketEventListener} socket event listener
 * handlers.
 *
 * @author David Åberg - Initial contribution
 */
public interface SonyAudioClientSocketEventListener {
    void handleEvent(JsonObject json);

    void onConnectionClosed();

    void onConnectionOpened(URI resource);
}
