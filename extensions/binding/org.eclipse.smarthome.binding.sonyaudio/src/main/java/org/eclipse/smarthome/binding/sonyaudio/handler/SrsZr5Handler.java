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
package org.eclipse.smarthome.binding.sonyaudio.handler;

import java.io.IOException;

import java.util.concurrent.CompletableFuture;
import java.util.Map;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.library.types.StringType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SrsZr5Handler} is responsible for handling commands for SRS-ZR5, which are
 * sent to one of the channels.
 *
 * @author David Åberg - Initial contribution
 */
public class SrsZr5Handler extends SonyAudioHandler {

    private final Logger logger = LoggerFactory.getLogger(SonyAudioHandler.class);

    public SrsZr5Handler(Thing thing) {
        super(thing);
    }

    @Override
    public String setInputCommand(Command command){
      switch(command.toString().toLowerCase()){
        case "btaudio": return "extInput:btaudio";
        case "usb": return "storage:usb1";
        case "analog": return "extInput:line?port=1";
        case "hdmi": return "extInput:hdmi";
        case "network": return "dlna:music";
      }
      return command.toString();
    }

    @Override
    public StringType inputSource(String input){
        String in = input.toLowerCase();
        if(in.contains("extinput:btaudio".toLowerCase())){
            return new StringType("btaudio");
        }
        if(in.contains("storage:usb1".toLowerCase())){
            return new StringType("usb");
        }
        if(in.contains("extinput:line?port=1".toLowerCase())){
            return new StringType("analog");
        }
        if(in.contains("extinput:hdmi".toLowerCase())){
            return new StringType("hdmi1");
        }
        if(in.contains("dlna:music".toLowerCase())){
            return new StringType("network");
        }
        return new StringType(input);
    }

    @Override
    public void handleSoundSettings(Command command, ChannelUID channelUID) throws IOException {
        if (command instanceof RefreshType) {
            logger.debug("SrsZr5Handler handleSoundSettings RefreshType");
            Map<String,String> result = sound_settings_cache.getValue();

            logger.debug("SrsZr5Handler Updateing sound field to {} {}", result.get("clearAudio"), result.get("soundField"));
            if(result.get("clearAudio").equalsIgnoreCase("on")){
                updateState(channelUID, new StringType("clearAudio"));
            }else{
                updateState(channelUID, new StringType(result.get("soundField")));
            }
        }
        if (command instanceof StringType) {
            if(((StringType) command).toString().equalsIgnoreCase("clearAudio")){
                connection.setSoundSettings("clearAudio", "on");
            }else{
                connection.setSoundSettings("soundField", ((StringType) command).toString());
            }
        }
    }
}
