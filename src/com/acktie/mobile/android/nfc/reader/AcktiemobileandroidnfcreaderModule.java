/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.acktie.mobile.android.nfc.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Parcelable;

import com.acktie.mobile.android.nfc.reader.proxy.NFCMessage;
import com.acktie.mobile.android.nfc.reader.proxy.ParsedNdefRecord;
import com.google.common.base.Preconditions;

/**
 * The main NFC Module.
 * 
 * @author TNuzzi
 * 
 */
@Kroll.module(name = "Acktiemobileandroidnfcreader", id = "com.acktie.mobile.android.nfc.reader")
public class AcktiemobileandroidnfcreaderModule extends KrollModule {

	// Standard Debugging variables
	private static final String LCAT = "AcktiemobileandroidnfcreaderModule";

	@SuppressWarnings("unused")
	private static final boolean DBG = TiConfig.LOGD;

	// Android NFC variables
	private static NfcManager nfcManager = null;
	private static NfcAdapter nfcAdaptor = null;
	private static boolean isInit = false;
	private List<ParsedNdefRecord> ndefRecords = null;
	private PendingIntent pendingIntent = null;
	private IntentFilter ndefDetected = null;
	private IntentFilter[] ndefFilters = null;
	private IntentFilter tagDetected = null;
	private IntentFilter[] tagFilters = null;
	private boolean writeModeEnabled = false;

	private static final String MIME_TYPE = "mimeType";

	private static String[][] TECH_LIST_FILTERS = new String[][] {
			new String[] { MifareUltralight.class.getName(),
					Ndef.class.getName(), NfcA.class.getName() },
			new String[] { MifareClassic.class.getName(), Ndef.class.getName(),
					NfcA.class.getName() } };

	// Constants exposed in Javascript via module name (e.g. nfc.IsoDep)
	@Kroll.constant
	public static final String ACTION_NDEF_DISCOVERED = android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED;
	@Kroll.constant
	public static final String ACTION_TECH_DISCOVERED = android.nfc.NfcAdapter.ACTION_TECH_DISCOVERED;
	@Kroll.constant
	public static final String ACTION_TAG_DISCOVERED = android.nfc.NfcAdapter.ACTION_TAG_DISCOVERED;
	@Kroll.constant
	public static final String EXTRA_ID = android.nfc.NfcAdapter.EXTRA_ID;
	@Kroll.constant
	public static final String EXTRA_NDEF_MESSAGES = android.nfc.NfcAdapter.EXTRA_NDEF_MESSAGES;
	@Kroll.constant
	public static final String EXTRA_TAG = android.nfc.NfcAdapter.EXTRA_TAG;

	@Kroll.constant
	public static final short TNF_ABSOLUTE_URI = NdefRecord.TNF_ABSOLUTE_URI;
	@Kroll.constant
	public static final short TNF_EMPTY = NdefRecord.TNF_EMPTY;
	@Kroll.constant
	public static final short TNF_EXTERNAL_TYPE = NdefRecord.TNF_EXTERNAL_TYPE;
	@Kroll.constant
	public static final short TNF_MIME_MEDIA = NdefRecord.TNF_MIME_MEDIA;
	@Kroll.constant
	public static final short TNF_UNCHANGED = NdefRecord.TNF_UNCHANGED;
	@Kroll.constant
	public static final short TNF_UNKNOWN = NdefRecord.TNF_UNKNOWN;
	@Kroll.constant
	public static final short TNF_WELL_KNOWN = NdefRecord.TNF_WELL_KNOWN;

	// Had to hardcode to String because of an NDK issue or not allowing
	// class.getName() assignments
	@Kroll.constant
	public static final String IsoDep = "android.nfc.tech.IsoDep";
	@Kroll.constant
	public static final String MifareClassic = "android.nfc.tech.MifareClassic";
	@Kroll.constant
	public static final String MifareUltralight = "android.nfc.tech.MifareUltralight";
	@Kroll.constant
	public static final String Ndef = "android.nfc.tech.Ndef";
	@Kroll.constant
	public static final String NdefFormatable = "android.nfc.tech.NdefFormatable";
	@Kroll.constant
	public static final String NfcA = "android.nfc.tech.NfcA";
	@Kroll.constant
	public static final String NfcB = "android.nfc.tech.NfcB";
	@Kroll.constant
	public static final String NfcF = "android.nfc.tech.NfcF";
	@Kroll.constant
	public static final String NfcV = "android.nfc.tech.NfcV";

	private static final String[] PREFIXES = { "http://www.", "https://www.",
			"http://", "https://", "tel:", "mailto:",
			"ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://", "sftp://",
			"smb://", "nfs://", "ftp://", "dav://", "news:", "telnet://",
			"imap:", "rtsp://", "urn:", "pop:", "sip:", "sips:", "tftp:",
			"btspp://", "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://",
			"file://", "urn:epc:id:", "urn:epc:tag:", "urn:epc:pat:",
			"urn:epc:raw:", "urn:epc:", "urn:nfc:" };

	/**
	 * Called by Appcelerator module framework. Calls {@code initModule}
	 * 
	 * @param app
	 *            - Passed in from Appcelerator when initializing the module
	 */
	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		Log.d(LCAT, "[Inside] onAppCreate");
		initModule();
	}

	/**
	 * Used to initialize the module with user settings. This initialization is
	 * controlled by the user in the specific Appcelerator application. <br />
	 * 
	 * <pre>
	 * nfc.init();
	 * </pre>
	 * 
	 * @param args
	 *            - This parameter is optional however see docs for usage
	 */
	@Kroll.method
	public void init(@Kroll.argument(optional = true) KrollDict args) {
		Log.d(LCAT, "[Inside] init(args:" + args + ")");
		String mimeType = "*/*";
		if (args != null && args.containsKey(MIME_TYPE)) {
			mimeType = args.getString(MIME_TYPE);
		}

		Activity activity = TiApplication.getAppRootOrCurrentActivity();

		pendingIntent = PendingIntent.getActivity(activity, 0, new Intent(
				activity, activity.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		appceleratorWorkaroundForActivityThreadIssue();

		// NDef Intent
		ndefDetected = new IntentFilter(ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType(mimeType);
		} catch (MalformedMimeTypeException e) {
			Log.d(LCAT, e.getMessage());
		}
		ndefFilters = new IntentFilter[] { ndefDetected };

		// Tag Intent
		tagDetected = new IntentFilter(ACTION_TAG_DISCOVERED);
		tagFilters = new IntentFilter[] { tagDetected };

		isInit = true;
	}

	/**
	 * Tests to determine if Android app was launched because of an NFC intents
	 * received.
	 * 
	 * @param action
	 *            - NFC intents are {@code ACTION_NDEF_DISCOVERED},
	 *            {@code ACTION_TECH_DISCOVERED}, or
	 *            {@code ACTION_TAG_DISCOVERED} Note: This parameter is
	 *            optional. If left off then it will use all the intents to
	 *            determine if incoming intent was NFC
	 * @return If the current intent is from NFC, this method will return true.
	 */
	@Kroll.method
	public boolean wasAppLaunchViaNFCIntent(
			@Kroll.argument(optional = true) String action) {
		Log.d(LCAT, "wasAppLaunchViaNFCIntent(action:" + action + ")");
		boolean wasNFCIntent = false;
		Intent intent = TiApplication.getAppRootOrCurrentActivity().getIntent();
		String intentAction = intent.getAction();

		if (action != null) {
			if (action.equalsIgnoreCase(intentAction)) {
				wasNFCIntent = true;
			} else {
				wasNFCIntent = false;
			}
		} else {
			if (intentAction.equalsIgnoreCase(ACTION_NDEF_DISCOVERED)
					|| intentAction.equalsIgnoreCase(ACTION_TECH_DISCOVERED)
					|| intentAction.equalsIgnoreCase(ACTION_TAG_DISCOVERED)) {
				wasNFCIntent = true;
			} else {
				wasNFCIntent = false;
			}
		}

		return wasNFCIntent;
	}

	/**
	 * Determines if NFC is enabled on the Android device.
	 * 
	 * @return true if Adaptor is available.
	 */
	@Kroll.getProperty
	public boolean getIsNFCEnabled() {
		Log.d(LCAT, "isNFCEnabled()");
		if (nfcManager != null && nfcManager.getDefaultAdapter() != null
				&& nfcManager.getDefaultAdapter().isEnabled()) {
			Log.d(LCAT, "NfcAdaptor: " + nfcManager.getDefaultAdapter());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines if the module is in NFC Tag write mode
	 * 
	 * @return true if NFC is in Tag write mode.
	 */
	@Kroll.getProperty
	public boolean getIsWriteModeEnabled() {
		Log.d(LCAT, "getIsWriteModeEnabled()");
		return writeModeEnabled;
	}

	/**
	 * First checks to see if intent is NFC intent. If so, look for NDEF
	 * messages in the intent. If no, intent was passed then the method will
	 * look in the module and get the current/active intent.
	 * 
	 * @return If NFC messages are in the intent will return true. Will return
	 *         false if, 1) intent is not NFC, 2) if intent does not contain any
	 *         NDEF messages.
	 */
	@Kroll.method
	public boolean containsKnownNdefMessages(
			@Kroll.argument(optional = true) IntentProxy proxyIntent) {
		Log.d(LCAT, "[Inside] containsKnownNdefMessages(newIntent:"
				+ proxyIntent + ")");

		Intent intent = getIntent(proxyIntent);

		Log.d(LCAT,
				"containsKnownNdefMessages: Intent was " + intent.getAction());
		if (intent.getAction().equalsIgnoreCase(ACTION_TECH_DISCOVERED)
				|| intent.getAction().equalsIgnoreCase(ACTION_NDEF_DISCOVERED)
				|| intent.getAction().equalsIgnoreCase(ACTION_TAG_DISCOVERED)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			Log.d(LCAT, "containsKnownNdefMessages: rawMsgs " + rawMsgs);
			if (rawMsgs != null) {
				return true;
			} else {
				Log.d(LCAT, "containsKnownNdefMessages: Unknown Tag detected");
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Parse the NDEF messages in the current or passed in intent.
	 * 
	 * @param proxyIntent
	 *            Intent to retreive messages from.
	 */
	@Kroll.method
	public void parse(@Kroll.argument(optional = true) IntentProxy proxyIntent) {
		Log.d(LCAT, "[Inside] parse(newIntent:" + proxyIntent + ")");

		Intent intent = getIntent(proxyIntent);

		NdefMessage[] ndefMessages = getNdefMessages(intent);
		if (ndefMessages != null && ndefMessages.length != 0) {
			Log.d(LCAT, "[Inside] parsing records");
			ndefRecords = getRecords(ndefMessages[0].getRecords());
		}
	}

	/**
	 * Returns a collection of parsed NDEF Records.
	 * 
	 * @return Array of NDEF records
	 */
	@Kroll.method
	public ParsedNdefRecord[] getParsedNdefRecords() {
		ParsedNdefRecord[] parsedRecords = new ParsedNdefRecord[0];
		if (ndefRecords != null) {
			ParsedNdefRecord[] tempArray = new ParsedNdefRecord[ndefRecords
					.size()];
			parsedRecords = ndefRecords.toArray(tempArray);
		}

		return parsedRecords;
	}

	/**
	 * Returns the first NDRF record. According to the Android docs only 1
	 * record should exist.
	 * 
	 * @return The first Parsed NDEF record
	 */
	@Kroll.method
	public ParsedNdefRecord getParsedNdefRecord() {
		ParsedNdefRecord parsedRecords = null;
		if (ndefRecords != null) {
			parsedRecords = ndefRecords.get(0);
		}

		return parsedRecords;
	}

	/**
	 * Calls the disable disableForegroundDispatch on the NfcAdaptor with the
	 * current activity
	 */
	@Kroll.method
	public void disableForegroundDispatch() {
		Log.d(LCAT, "[Inside] disableForegroundDispatch()");
		nfcAdaptor.disableForegroundDispatch(TiApplication
				.getAppRootOrCurrentActivity());
	}

	/**
	 * Calls the disable disableForegroundNdefPush on the NfcAdaptor with the
	 * current activity
	 */
	@Kroll.method
	public void disableForegroundNdefPush() {
		Log.d(LCAT, "[Inside] disableForegroundDispatch()");
		nfcAdaptor.disableForegroundNdefPush(TiApplication
				.getAppRootOrCurrentActivity());
	}

	/**
	 * Calls the method enableForegroundDispatch on the NfcAdaptor with the
	 * current activity and a predefined pending intent. The user can pass in an
	 * intent filter which to trigger the foreground dispatch. Also, a tech list
	 * can be used to look for certain types of tags. For general use it is fine
	 * not to specify either of them.
	 * 
	 * @param intentFilter
	 *            Can be either {@code ACTION_TAG_DISCOVERED} or
	 *            {@code ACTION_NDEF_DISCOVERED} all other string values will be
	 *            ignored.
	 * @param filterWithTechList
	 *            Whether or not to use a tech list filter.
	 */
	@Kroll.method
	public void enableForegroundDispatch(
			@Kroll.argument(optional = true) String intentFilter,
			@Kroll.argument(optional = true) boolean filterWithTechList) {
		Log.d(LCAT, "[Inside] enableForegroundDispatch(intentFilter:"
				+ intentFilter + ", filterWithTechList:" + filterWithTechList
				+ ")");

		Activity activity = TiApplication.getAppRootOrCurrentActivity();

		String[][] techListFilters = null;
		if (filterWithTechList) {
			techListFilters = TECH_LIST_FILTERS;
		}

		IntentFilter[] filters = null;
		if (intentFilter != null
				&& intentFilter.equalsIgnoreCase(ACTION_TAG_DISCOVERED)) {
			filters = tagFilters;
		} else if (intentFilter != null
				&& intentFilter.equalsIgnoreCase(ACTION_NDEF_DISCOVERED)) {
			filters = ndefFilters;
		}

		nfcAdaptor.enableForegroundDispatch(activity, pendingIntent, filters,
				techListFilters);
	}

	/**
	 * Calls the {@code NfcAdapter.enableForegroundNdefPush} with the current
	 * Activity and the passed {@code NdefMessage}
	 * 
	 * @param msg
	 *            The NdefMessage to push.
	 */
	@Kroll.method
	public void enableForegroundNdefPush(NFCMessage msg) {
		Log.d(LCAT, "[Inside] enableForegroundNdefPush(msg:" + msg + ")");

		Activity activity = TiApplication.getAppRootOrCurrentActivity();
		nfcAdaptor.enableForegroundNdefPush(activity, msg.getMessage());
	}

	/**
	 * Creates a Well-Known Plain Text NDefMessage for mime type text/plain
	 * 
	 * @param text
	 *            Text of the NdefMessage
	 * @param language
	 *            The 2 character language code
	 * @param useUTF16Encoding
	 *            Should the text be encoded for 16-bit? By default 8-bit
	 *            encoding is used
	 * @return An NdefMessage containing the encoded text.
	 */
	@Kroll.method
	public NFCMessage createPlainTextNFCData(String text,
			@Kroll.argument(optional = true) String language,
			@Kroll.argument(optional = true) boolean useUTF16Encoding) {
		Log.d(LCAT, "[Inside] createTextNFCData(text:" + text
				+ ", useUTF16Encoding:" + useUTF16Encoding + ")");

		Preconditions.checkNotNull(text);

		Locale locale = Locale.getDefault();

		if (language != null) {
			locale = new Locale(language);
		}

		return new NFCMessage(new NdefMessage(
				new NdefRecord[] { createTextRecord(text, locale,
						useUTF16Encoding) }));
	}

	/**
	 * Create a Well-know URI NDefMessage
	 * 
	 * @param uri
	 *            The URL to encode
	 * @return A NdefMessage containing the encoded url.
	 */
	@Kroll.method
	public NFCMessage createURINFCData(String uri) {
		Log.d(LCAT, "[Inside] createURINFCData(uri:" + uri + ")");

		Preconditions.checkNotNull(uri);

		return new NFCMessage(new NdefMessage(
				new NdefRecord[] { createURIRecord(uri) }));
	}

	/**
	 * Create a Well-know Absolute URI NDefMessage
	 * 
	 * @param uri
	 *            The URL to encode
	 * @return A NdefMessage containing the encoded url.
	 */
	@Kroll.method
	public NFCMessage createAbsoluteURINFCData(String uri) {
		Log.d(LCAT, "[Inside] createAbsoluteURINFCData(uri:" + uri + ")");

		Preconditions.checkNotNull(uri);

		return new NFCMessage(new NdefMessage(
				new NdefRecord[] { createAbsoluteURIRecord(uri) }));
	}

	/**
	 * Create a Well-know Absolute URI NDefMessage
	 * 
	 * @param mimeType
	 *            The mime type
	 * @param message
	 *            The message to go along with the mime type
	 * @return A NdefMessage containing the MIME type and message.
	 */
	@Kroll.method
	public NFCMessage createMimeMediaNFCData(String mimeType, String message) {
		Log.d(LCAT, "[Inside] createAbsoluteURINFCData(mimeType:" + mimeType
				+ ", message:" + message + ")");

		Preconditions.checkNotNull(mimeType);
		Preconditions.checkNotNull(message);

		return new NFCMessage(new NdefMessage(
				new NdefRecord[] { createMimeMediaRecord(mimeType, message) }));
	}

	/**
	 * Put the module in write tag mode. This is a convenience method because it
	 * could be done in JS. It disables the current foreground activity and sets
	 * a new foreground activity for writing the tag.
	 * 
	 * @param dontDisablePendingForegroundDispatchOrPush
	 *            Boolean to indicate whether or not to auto disable the pending
	 *            foreground activity. If the application already disabled the
	 *            foreground activities then pass true.
	 */
	@Kroll.method
	public void enableTagWriteMode(
			@Kroll.argument(optional = true) boolean dontDisablePendingForegroundDispatchOrPush) {
		Log.d(LCAT,
				"[Inside] enableForegroundNdefPush(dontDisablePendingForegroundDispatchOrPush:"
						+ dontDisablePendingForegroundDispatchOrPush + ")");

		Activity activity = TiApplication.getAppRootOrCurrentActivity();

		if (!dontDisablePendingForegroundDispatchOrPush) {
			disableForegroundDispatch();
			disableForegroundNdefPush();
		}

		writeModeEnabled = true;
		nfcAdaptor.enableForegroundDispatch(activity, pendingIntent,
				tagFilters, null);
	}

	/**
	 * Disables Tag Write Mode and cancels the tag write dispatch
	 */
	@Kroll.method
	public void disableTagWriteMode() {
		Log.d(LCAT, "[Inside] disableTagWriteMode()");
		writeModeEnabled = false;
		disableForegroundDispatch();
	}

	/**
	 * Writes the passed NdefMessage to the tag. The tag is derived from the
	 * intent passed. Once the tag has been written the passed callback will be
	 * called.
	 * 
	 * @param nFCmessage
	 *            The NdefMessage to write
	 * @param proxyIntent
	 *            The intent to retrieve the tag from
	 * @param callback
	 *            The JS callback to call once the write is finished.
	 */
	@Kroll.method
	public void writeToTag(NFCMessage nFCmessage, IntentProxy proxyIntent,
			KrollFunction callback) {
		Log.d(LCAT, "[Inside] createTextNFCData(nFCmessage:" + nFCmessage
				+ ", proxyIntent:" + proxyIntent + ")");

		Preconditions.checkNotNull(nFCmessage);
		Preconditions.checkNotNull(proxyIntent);
		Preconditions.checkNotNull(callback);

		NdefMessage message = nFCmessage.getMessage();

		Tag tag = proxyIntent.getIntent().getParcelableExtra(
				NfcAdapter.EXTRA_TAG);
		HashMap results = new HashMap();
		boolean result = false;
		String resultMessage = null;

		int size = message.toByteArray().length;

		try {
			android.nfc.tech.Ndef ndef = android.nfc.tech.Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					resultMessage = "Unable to write to Tag, Tag is read-only.";
				}
				if (ndef.getMaxSize() < size) {
					resultMessage = "Message is big to hold on the Tag.  Tag size is "
							+ ndef.getMaxSize()
							+ " bytes, message is size is"
							+ size + " bytes.";
				}

				ndef.writeNdefMessage(message);
				Log.d(LCAT, "Wrote message to pre-formatted tag.");
				result = true;
			} else {
				android.nfc.tech.NdefFormatable format = android.nfc.tech.NdefFormatable
						.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						Log.d(LCAT, "Formatted tag and wrote message");
						result = true;
					} catch (IOException e) {
						e.printStackTrace();
						resultMessage = "Failed to format tag.";
					}
				} else {
					resultMessage = "Tag doesn't support NDEF Format";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMessage = "Unable to write tag";
		}

		results.put("result", result);

		if (!result) {
			results.put("message", resultMessage);
			Log.d(LCAT, resultMessage);
		}

		callback.callAsync(getKrollObject(), results);
	}

	/**
	 * Returns a List of ParsedNdefRecords from an Array of records
	 * 
	 * @param records
	 *            The array of records
	 * @return List of records
	 */
	protected List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
		Log.d(LCAT, "[Inside] getRecords()");

		Preconditions.checkNotNull(records);

		List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
		for (NdefRecord record : records) {
			elements.add(new ParsedNdefRecord(record));
		}
		return elements;
	}

	/**
	 * Method that initializes the module. Checks with the Android OS to
	 * determine if the NFC_SERVICE is available. If not NFC is not available on
	 * the phone.
	 */
	protected static void initModule() {
		Log.d(LCAT, "[Inside] initModule()");
		// put module init code that needs to run when the application is
		// created

		Object nfcObject = TiApplication.getInstance().getApplicationContext()
				.getSystemService(Context.NFC_SERVICE);

		if (nfcObject != null && nfcObject instanceof NfcManager) {
			nfcManager = (NfcManager) nfcObject;
			nfcAdaptor = nfcManager.getDefaultAdapter();
		}

		Log.d(LCAT, "NfcManager: " + nfcManager);
		Log.d(LCAT, "nfcAdaptor: " + nfcAdaptor);
	}

	/**
	 * Workaround needed for the latest version (2.x) of Appcelerator Android
	 * module development. If the workaround is not used a nullpointer is thrown
	 * within the Android NFC code.
	 */
	protected static void appceleratorWorkaroundForActivityThreadIssue() {
		Log.d(LCAT, "[Inside] appceleratorWorkaroundForActivityThreadIssue()");
		if (ActivityThread.currentActivityThread() == null) {
			ActivityThread.systemMain();
		}
	}

	/**
	 * Returns a array of NdefMesage(s) from the current activity intent. Checks
	 * to see if intent is NFC intent if not returns null. If NFC intent but
	 * payload is not known then will return an unknown NdefMessage
	 * {@code NdefRecord.TNF_UNKNOWN}.
	 * 
	 * @return
	 */
	protected NdefMessage[] getNdefMessages(Intent intent) {
		Log.d(LCAT, "[Inside] getNdefMessages(newIntent:" + intent + ")");

		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (action.equalsIgnoreCase(ACTION_TAG_DISCOVERED)
				|| action.equalsIgnoreCase(ACTION_NDEF_DISCOVERED)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.d(LCAT, "Not an NFC intent.");
			TiApplication.getAppRootOrCurrentActivity().finish();
		}
		return msgs;
	}

	/**
	 * Helper method to create a Text NdefRecord
	 * 
	 * @param text
	 *            The text to encode
	 * @param locale
	 *            The locale to use for encoding
	 * @param encodeInUtf16
	 *            A boolean of whether or not to encode in 16-bit. Default is
	 *            8-bit
	 * @return The NdefRecord with the encoded text
	 */
	protected NdefRecord createTextRecord(String text, Locale locale,
			boolean encodeInUtf16) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf16 ? Charset.forName("UTF-16")
				: Charset.forName("UTF-8");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf16 ? (1 << 7) : 0;
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}

	/**
	 * Create a NdefRecord with an URI
	 * 
	 * @param uri
	 *            The URI to encode
	 * @return The NdefRecod with the URI
	 */
	protected NdefRecord createURIRecord(String uri) {
		byte[] uriField = "acktie.com".getBytes(Charset.forName("US-ASCII"));
		byte[] payload = new byte[uriField.length + 1]; // add 1 for the URI
														// Prefix
		payload[0] = 0x01; // prefixes http://www. to the URI
		System.arraycopy(uriField, 0, payload, 1, uriField.length); // appends
																	// URI to
																	// payload
		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI,
				new byte[0], payload);
	}

	/**
	 * Create a NdefRecord with an Absolute URI
	 * 
	 * @param uri
	 *            The URI to encode
	 * @return The NdefRecod with the URI
	 */
	protected NdefRecord createAbsoluteURIRecord(String uri) {
		NdefRecord uriRecord = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI,
				uri.getBytes(Charset.forName("US-ASCII")), new byte[0],
				new byte[0]);

		return uriRecord;
	}

	/**
	 * Create a NdefRecord with an MIME Media and message
	 * 
	 * @param mimeType
	 *            The mime type
	 * @param message
	 *            The message to go along with the mime type
	 * @return The NdefRecod with the MIME Media
	 */
	protected NdefRecord createMimeMediaRecord(String mimeType, String message) {
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeType.getBytes(Charset.forName("US-ASCII")), new byte[0],
				message.getBytes(Charset.forName("US-ASCII")));

		return mimeRecord;
	}

	/**
	 * If null is passed will return the current/active intent. Else the intent
	 * in the proxy intent is returned
	 * 
	 * @param proxyIntent
	 *            A proxy intent, can be null
	 * @return
	 */
	protected Intent getIntent(IntentProxy proxyIntent) {
		Intent intent;
		if (proxyIntent != null) {
			intent = proxyIntent.getIntent();
		} else {
			intent = TiApplication.getAppRootOrCurrentActivity().getIntent();
		}
		return intent;
	}
}