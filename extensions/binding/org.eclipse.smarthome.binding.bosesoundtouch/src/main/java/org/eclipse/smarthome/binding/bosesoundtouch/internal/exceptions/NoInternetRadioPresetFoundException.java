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
package org.eclipse.smarthome.binding.bosesoundtouch.internal.exceptions;

/**
 * The {@link NoInternetRadioPresetFoundException} class is an exception
 *
 * @author Thomas Traunbauer - Initial contribution
 */
public class NoInternetRadioPresetFoundException extends NoPresetFoundException {
    private static final long serialVersionUID = 1L;

    public NoInternetRadioPresetFoundException() {
        super();
    }

    public NoInternetRadioPresetFoundException(String message) {
        super(message);
    }

    public NoInternetRadioPresetFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInternetRadioPresetFoundException(Throwable cause) {
        super(cause);
    }
}