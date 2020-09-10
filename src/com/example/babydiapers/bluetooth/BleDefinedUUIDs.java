package com.example.babydiapers.bluetooth;

import java.util.UUID;

public class BleDefinedUUIDs {
	
	public static class Service {
//		final static public UUID HEART_RATE               = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
		final static public UUID HEART_RATE               = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
		final static public UUID BABY_DIAPER               = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	};
	
	public static class Characteristic {
//		final static public UUID HEART_RATE_MEASUREMENT   = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
		final static public UUID SIMPLEPROFILE_CHAR6   	  = UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb");
		final static public UUID SIMPLEPROFILE_CHAR7      = UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb");
		final static public UUID SIMPLEPROFILE_CHAR8      = UUID.fromString("0000fff8-0000-1000-8000-00805f9b34fb");
		final static public UUID MANUFACTURER_STRING      = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
		final static public UUID MODEL_NUMBER_STRING      = UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb");
		final static public UUID FIRMWARE_REVISION_STRING = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
		final static public UUID APPEARANCE               = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb");
		final static public UUID BODY_SENSOR_LOCATION     = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb");
		final static public UUID BATTERY_LEVEL            = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
	}
	
	public static class Descriptor {
//		final static public UUID CHAR_CLIENT_CONFIG       = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
		final static public UUID BabyPeeCH       = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
		final static public UUID BAT_LEVEL       = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
	}
	
}
