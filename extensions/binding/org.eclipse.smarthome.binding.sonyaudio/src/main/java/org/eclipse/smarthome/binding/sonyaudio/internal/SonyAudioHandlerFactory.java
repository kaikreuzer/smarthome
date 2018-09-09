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
package org.eclipse.smarthome.binding.sonyaudio.internal;

import org.eclipse.smarthome.binding.sonyaudio.SonyAudioBindingConstants;

import org.eclipse.smarthome.binding.sonyaudio.handler.StrDn1080Handler;
import org.eclipse.smarthome.binding.sonyaudio.handler.HtCt800Handler;
import org.eclipse.smarthome.binding.sonyaudio.handler.HtSt5000Handler;
import org.eclipse.smarthome.binding.sonyaudio.handler.HtZf9Handler;
import org.eclipse.smarthome.binding.sonyaudio.handler.HtZ9fHandler;
import org.eclipse.smarthome.binding.sonyaudio.handler.HtMt500Handler;
import org.eclipse.smarthome.binding.sonyaudio.handler.SrsZr5Handler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * The {@link SonyAudioHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author David - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.sonyaudio", configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SonyAudioHandlerFactory extends BaseThingHandlerFactory {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SonyAudioBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        switch(thingTypeUID.getId()){
          case SonyAudioBindingConstants.SONY_TYPE_STRDN1080: return new StrDn1080Handler(thing);
          case SonyAudioBindingConstants.SONY_TYPE_HTCT800: return new HtCt800Handler(thing);
          case SonyAudioBindingConstants.SONY_TYPE_HTST5000: return new HtSt5000Handler(thing);
          case SonyAudioBindingConstants.SONY_TYPE_HTZ9F: return new HtZ9fHandler(thing);
          case SonyAudioBindingConstants.SONY_TYPE_HTZF9: return new HtZf9Handler(thing);
          case SonyAudioBindingConstants.SONY_TYPE_HTMT500: return new HtMt500Handler(thing);
          case SonyAudioBindingConstants.SONY_TYPE_SRSZR5: return new SrsZr5Handler(thing);
          default: return null;
        }
    }
}
