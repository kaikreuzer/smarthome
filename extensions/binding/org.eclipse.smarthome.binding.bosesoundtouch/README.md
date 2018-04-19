# Bose SoundTouch Binding

This binding supports the Bose SoundTouch multiroom system.

## Supported Things
 
t.b.d.
 
## Discovery
 
Speakers are automatically discovered using mDNS in the local network.
 
## Binding Configuration
 
The binding has no configuration options, all configuration is done at Thing level.
 
## Thing Configuration
 
t.b.d.
 
## Channels

t.b.d. 
 
## Full Example

Items:

```
Switch  Bose1_Power                      "Power: [%s]"          <switch>      { channel="bosesoundtouch:device:BOSEMACADDR:power" }
Dimmer  Bose1_Volume                     "Volume: [%d %%]"      <volume>      { channel="bosesoundtouch:device:BOSEMACADDR:volume" }
Number  Bose1_Bass                       "Bass: [%d %%]"        <volume>      { channel="bosesoundtouch:device:BOSEMACADDR:bass" }
Switch  Bose1_Mute                       "Mute: [%s]"           <volume_mute> { channel="bosesoundtouch:device:BOSEMACADDR:mute" }
String  Bose1_OperationMode              "OperationMode: [%s]"  <text>        { channel="bosesoundtouch:device:BOSEMACADDR:operationMode" }
String  Bose1_PlayerControl              "Player Control: [%s]" <text>        { channel="bosesoundtouch:device:BOSEMACADDR:playerControl" }
Number  Bose1_Preset                     "Preset: [%d]"         <text>        { channel="bosesoundtouch:device:BOSEMACADDR:preset" }
Number  Bose1_SaveAsPreset               "Save as Preset: [%d]" <text>        { channel="bosesoundtouch:device:BOSEMACADDR:saveAsPreset" }
String  Bose1_KeyCode                    "Key Code: [%s]"       <text>        { channel="bosesoundtouch:device:BOSEMACADDR:keyCode" }
Switch  Bose1_RateEnabled                "Rate: [%s]"           <switch>      { channel="bosesoundtouch:device:BOSEMACADDR:rateEnabled" }
Switch  Bose1_SkipEnabled                "Skip: [%s]"           <switch>      { channel="bosesoundtouch:device:BOSEMACADDR:skipEnabled" }
Switch  Bose1_SkipPreviousEnabled        "SkipPrevious: [%s]"   <switch>      { channel="bosesoundtouch:device:BOSEMACADDR:skipPreviousEnabled" }
String  Bose1_nowPlayingAlbum            "Album: [%s]"          <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingAlbum" }
String  Bose1_nowPlayingArtist           "Artist: [%s]"         <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingArtist" }
Image  Bose1_nowPlayingArtwork           "Artwork"              <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingArtwork" }
String  Bose1_nowPlayingDescription      "Description: [%s]"    <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingDescription" }
String  Bose1_nowPlayingGenre            "Genre: [%s]"          <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingGenre" }
String  Bose1_nowPlayingItemName         "Playing: [%s]"        <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingItemName" }
String  Bose1_nowPlayingStationLocation  "Radio Location: [%s]" <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingStationLocation" }
String  Bose1_nowPlayingStationName      "Radio Name: [%s]"     <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingStationName" }
String  Bose1_nowPlayingTrack            "Track: [%s]"          <text>        { channel="bosesoundtouch:device:BOSEMACADDR:nowPlayingTrack" }
```

Sitemap:

```
sitemap demo label="Bose Test Items"
{
	Frame label="Bose 1" {
        Switch item=Bose1_Power
		Slider item=Bose1_Volume
		Number item=Bose1_Bass
		Switch item=Bose1_Mute
		Text item=Bose1_OperationMode
		Text item=Bose1_PlayerControl
		Text item=Bose1_ZoneAdd
		Text item=Bose1_ZoneRemove
		Number item=Bose1_Preset
		Text item=Bose1_PresetControl
		Number item=Bose1_SaveAsPreset
		Text item=Bose1_KeyCode
		Text item=Bose1_ZoneInfo
		Text item=Bose1_nowPlayingAlbum
		Text item=Bose1_nowPlayingArtist
		Text item=Bose1_nowPlayingArtwork
		Text item=Bose1_nowPlayingDescription
		Text item=Bose1_nowPlayingGenre
		Text item=Bose1_nowPlayingItemName
		Text item=Bose1_nowPlayingStationLocation
		Text item=Bose1_nowPlayingTrack
	}
}
```
