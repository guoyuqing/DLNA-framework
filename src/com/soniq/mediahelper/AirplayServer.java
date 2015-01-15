package com.soniq.mediahelper;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import com.geniusgithub.mediarender.util.DlnaUtils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AirplayServer {

	public static AirplayServer _instance = null;

	public Context _context = null;

	static final String AIR_TUNES_SERVICE_TYPE = "_raop._tcp.local.";
	static final String AIR_PLAY_SERVICE_TYPE = "_airplay._tcp.local.";

	private HttpServer _httpServer = null;

	protected List<JmDNS> jmDNSInstances;

	public static AirplayServer getInstance(Context context) {
		if (_instance == null)
			_instance = new AirplayServer(context);

		return _instance;
	}

	private AirplayServer(Context context) {
		jmDNSInstances = new java.util.LinkedList<JmDNS>();
		_context = context;
	}

	public String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public void stopService() {
		Log.v("alex", "stopService");
		if (_httpServer != null) {
			_httpServer.stopServer();
			_httpServer = null;
		}

		synchronized (jmDNSInstances) {
			for (final JmDNS jmDNS : jmDNSInstances) {
				try {
					jmDNS.unregisterAllServices();
				} catch (Exception e) {
					e.printStackTrace();
					Log.v("alex", "Failed to unregister some services");
				}
			}
		}
		Log.v("alex", "ariplay--------------stopService");
	}

	public void startService(Context context, String defalut_host_name) {

		// Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		// @Override
		// public void run() {
		// stopService();
		// }
		// }));

		NetworkUtils networkUtils = NetworkUtils.getInstance();

		// String hostName = AirplayConfig.serviceName;//
		// networkUtils.getHostUtils();
		String hostName = "";
		if ("".equals(DlnaUtils.getDevName(context))) {
			hostName = defalut_host_name;
		} else {
			hostName = DlnaUtils.getDevName(context) + "@"
					+ DlnaUtils.getIpLastNum(DlnaUtils.getLocalIP(context));
		}
//		String hardwareAddressString = getLocalMacAddress(context);
		String hardwareAddressString = DlnaUtils.getLocalIP(context);

		AirplayConfig.deviceId = hardwareAddressString;
		String hardwareAddressShortString = hardwareAddressString.replaceAll(
				":", "");

		Log.v("alex", "start http server at " + AirplayConfig.AirPlayPort);
		_httpServer = new HttpServer(_context, AirplayConfig.AirPlayPort);
		new Thread(_httpServer).start();

		try {
			synchronized (jmDNSInstances) {
				for (final NetworkInterface iface : Collections
						.list(NetworkInterface.getNetworkInterfaces())) {
					if (iface.isLoopback() || iface.isPointToPoint()
							|| !iface.isUp())
						continue;

					for (final InetAddress addr : Collections.list(iface
							.getInetAddresses())) {
						if (!(addr instanceof Inet4Address)
								&& !(addr instanceof Inet6Address))
							continue;

						try {
							final JmDNS jmDNS = JmDNS.create(addr, hostName
									+ "-jmdns");
							jmDNSInstances.add(jmDNS);

							String name = hardwareAddressShortString + "@"
									+ hostName;// + " (" + iface.getName() +
												// ")";

							// Publish RAOP service
							Map<String, String> airtunes_txt = new HashMap<String, String>();

							airtunes_txt.put("txtvers", "1");
							airtunes_txt.put("tp", "UDP");
							airtunes_txt.put("ch", "2");
							airtunes_txt.put("ss", "16");
							airtunes_txt.put("sr", "44100");
							airtunes_txt.put("pw", "false");
							airtunes_txt.put("sm", "false");
							airtunes_txt.put("sv", "false");
							airtunes_txt.put("ek", "1");
							airtunes_txt.put("et", "0,1");
							airtunes_txt.put("cn", "0,1,3");
							airtunes_txt.put("vn", "3");
							airtunes_txt.put("da", "true");
							airtunes_txt.put("md", "0,1,2");
							airtunes_txt.put("am", AirplayConfig.model);
							airtunes_txt.put("vs", "130.14");

							Log.v("alex", "register AirTunes service...");

							final ServiceInfo airTunesServiceInfo = ServiceInfo
									.create(AIR_TUNES_SERVICE_TYPE, name,
											AirplayConfig.AirTunesPort, 0, // weight
																			// ,
											0, // priority,
											airtunes_txt);

							jmDNS.registerService(airTunesServiceInfo);

							Log.v("alex", "Done!");
							Log.v("alex", "register AirPlay service...");

							// Publish AirPlay service
							Map<String, String> airplay_txt = new HashMap<String, String>();
							airplay_txt.put("deviceid", AirplayConfig.deviceId);
							airplay_txt.put("model", AirplayConfig.model);
							airplay_txt.put("features", AirplayConfig.features);
							airplay_txt.put("srcvers", AirplayConfig.srcvers);
							airplay_txt.put("flags", "0x4");
							airplay_txt.put("vv", "1");

							final ServiceInfo airPlayServiceInfo = ServiceInfo
									.create(AIR_PLAY_SERVICE_TYPE, hostName,
											AirplayConfig.AirPlayPort, 0, // weight
																			// ,
											0, // priority,
											airplay_txt// airplay_txt//AIRPLAY_SERVICE_PROPERTIES
									);

							jmDNS.registerService(airPlayServiceInfo);
							Log.v("alex", "Done!");
						} catch (Exception e) {
							Log.v("alex",
									"ariplay--------------===" + e.toString());

						}

					}
				}
			}
		} catch (Exception e) {
			Log.v("alex", "ariplay--------------" + e.toString());
		}
		Log.v("alex", "ariplay--------------start successfully!");

	}
}
