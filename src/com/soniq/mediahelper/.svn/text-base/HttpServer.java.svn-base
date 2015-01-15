package com.soniq.mediahelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import android.content.Context;
import android.util.Log;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import com.geniusgithub.mediarender.RenderApplication;
import com.geniusgithub.mediarender.image.ImageActivity;

public class HttpServer implements Runnable {

	private int _port = 7000;

	private Context _context = null;
	private String _imgCachePath;

	RequestListenerThread _thread = null;

	static HttpServer _instance = null;

	static HttpServer getInstance() {
		return _instance;
	}

	public HttpServer(Context context, int port) {
		_port = port;

		_instance = this;

		_imgCachePath = String.format("%s", context.getFilesDir());
	}

	@Override
	public void run() {
		startServer();
	}

	public void stopServer() {
		_thread.close();
	}

	private void startServer() {

		try {
			_thread = new RequestListenerThread(_port);
			_thread.setDaemon(false);
			_thread.start(); // start the webservice server
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
		// try{
		// ServerSocket socket = new ServerSocket(_port);
		//
		//
		// while( true )
		// {
		// Log.v("alex", "accept...");
		// Socket socketclient = socket.accept();
		//
		// if( socketclient == null )
		// break;
		//
		// // ClientThread ct = new ClientThread();
		// // ct._socket = socketclient;
		// // ct.start();
		// }
		// }
		// catch(Exception e)
		// {
		//
		// }
	}

	static class WebServiceHandler implements HttpRequestHandler {

		public static final byte[] input2byte(InputStream inStream, int length)
				throws IOException {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			int received = 0;
			while ((rc = inStream.read(buff, 0, 100)) > 0) {

				swapStream.write(buff, 0, rc);
				received += rc;
				if (received >= length)
					break;

			}
			byte[] in2b = swapStream.toByteArray();
			return in2b;
		}

		public static byte[] intArrayToByteArray(int[] a) {
			byte[] b = new byte[a.length];

			for (int i = 0; i < a.length; i++) {
				b[i] = (byte) a[i];
			}

			return b;
		}

		public static void dump_data(byte[] body_buffer, int line) {
			Log.v("alex", "length=" + body_buffer.length);
			StringBuffer sb = new StringBuffer();
			int n = 0;
			int l = 0;
			for (int i = 0; i < body_buffer.length; i++) {
				String ss = String.format("%02x ", body_buffer[i]);
				sb.append(ss);
				n++;
				if (n == 16) {
					Log.v("alex", sb.toString());
					n = 0;
					sb.setLength(0);
					l++;
					if (line > 0 && l >= line)
						break;
				}
			}

			if (sb.length() > 0) {
				Log.v("alex", sb.toString());
			}
		}

		public static byte[] intToByteArray(int i) {
			byte[] result = new byte[4];
			result[0] = (byte) ((i >> 24) & 0xFF);
			result[1] = (byte) ((i >> 16) & 0xFF);
			result[2] = (byte) ((i >> 8) & 0xFF);
			result[3] = (byte) (i & 0xFF);
			return result;
		}

		public static byte[] loadByteFromFile(String filename) {
			try {
				FileInputStream fis = new FileInputStream(filename);

				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				outStream.close();

				byte[] data = outStream.toByteArray();

				return data;

			} catch (Exception e) {

			}

			return null;
		}

		public WebServiceHandler() {
			super();
		}

		@Override
		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {
			// TODO Auto-generated method stub
			String method = request.getRequestLine().getMethod()
					.toUpperCase(Locale.ENGLISH);
			// get uri
			String target = request.getRequestLine().getUri();

			Log.v("alex", "=====headers======");
			String s = String.format("%s %s %s", method, target, request
					.getProtocolVersion().toString());
			Log.v("alex", s);

			Header[] hs = request.getAllHeaders();
			for (int i = 0; i < hs.length; i++) {
				s = String.format("%s: %s", hs[i].getName(), hs[i].getValue());
				Log.v("alex", s);
			}
			// std::string photoAction =
			// m_httpParser->getValue("x-apple-assetaction") ?
			// m_httpParser->getValue("x-apple-assetaction") : "";
			// std::string photoCacheId =
			// m_httpParser->getValue("x-apple-assetkey") ?
			// m_httpParser->getValue("x-apple-assetkey") : "";

			String photoAction = "";
			String photoCacheId = "";
			Header htmp = request.getFirstHeader("x-apple-assetaction");
			if (htmp != null) {
				photoAction = htmp.getValue();
				Log.v("alex", "photo_action=" + photoAction);
			}

			htmp = request.getFirstHeader("x-apple-assetkey");
			if (htmp != null) {
				photoCacheId = htmp.getValue();
				Log.v("alex", "photo_cacheid=" + photoCacheId);
			}

			// BasicHttpEntityEnclosingRequest

			byte[] body_buffer = null;
			Header[] h = request.getHeaders("Content-Length");
			int content_length = 0;
			if (h != null && h.length > 0) {
				int length = Integer.parseInt(h[0].getValue());
				content_length = length;
				if (length > 0) {
					HttpEntity t = ((org.apache.http.message.BasicHttpEntityEnclosingRequest) request)
							.getEntity();
					length = (int) t.getContentLength();

					if (length > 0) {
						// Ëé∑ÂèñËØ∑Ê±ÇÁöÑËæìÂÖ•Êµ�?
						InputStream inputStream = t.getContent();

						byte[] b = input2byte(inputStream, length);

						body_buffer = b;
						String result = new String(b, "UTF-8");
						Log.v("alex", "==========body start============");
						Log.v("alex", result);
						Log.v("alex", "==========body end============");

						// // BufferedReader reader = new BufferedReader(new
						// InputStreamReader(inputStream));
						// // StringBuilder result=new StringBuilder("");
						// // String line = "";
						// // while((line=reader.readLine())!=null){
						// // result.append(line);
						// // }
						// System.out.println(result.toString());
						// Log.v("alex", result.toString());
					}
				}
			}

			if (method.equals("GET")) {
				if (target.equals("/server-info")) {
					String content = String
							.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
									+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
									+ "<plist version=\"1.0\">\n"
									+ "<dict>\n"
									+ "<key>deviceid</key>\n"
									+ "<string>%s</string>\n"
									+ "<key>features</key>\n"
									+ "<integer>%d</integer>\n"
									+ "<key>model</key>\n"
									+ "<string>%s</string>\n"
									+ "<key>protovers</key>\n"
									+ "<string>1.0</string>\n"
									+ "<key>srcvers</key>\n"
									+ "<string>%s</string>\n"
									+ "</dict>\n"
									+ "</plist>\n", AirplayConfig.deviceId,
									AirplayConfig.features_int,
									AirplayConfig.model, AirplayConfig.srcvers);

					Log.v("alex", content);
					response.setHeader("Content-Type", "text/x-apple-plist+xml");
					response.setEntity(new StringEntity(content));
					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.equals("/playback-info")) {
					/*
					 * String content = String.format(Locale.CHINA,
					 * "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					 * "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"
					 * + "<plist version=\"1.0\">"+ " <dict>"+
					 * "  <key>duration</key> <real>1801</real>"+
					 * "  <key>loadedTimeRanges</key>"+ "  <array>"+
					 * "   <dict>"+
					 * "    <key>duration</key> <real>51.541130402</real>"+
					 * "    <key>start</key> <real>18.118717650000001</real>"+
					 * "   </dict>"+ "  </array>"+
					 * "  <key>playbackBufferEmpty</key> <true/>"+
					 * "  <key>playbackBufferFull</key> <false/>"+
					 * "  <key>playbackLikelyToKeepUp</key> <true/>"+
					 * "  <key>position</key> <real>18.043869775000001</real>"+
					 * "  <key>rate</key> <real>1</real>"+
					 * "  <key>readyToPlay</key> <true/>"+
					 * "  <key>seekableTimeRanges</key>"+ "  <array>"+
					 * "   <dict>"+ "    <key>duration</key>"+
					 * "    <real>1801</real>"+ "    <key>start</key>"+
					 * "    <real>0.0</real>"+ "   </dict>"+ "  </array>"+
					 * " </dict>"+ "</plist>");
					 * response.setHeader("Content-Type",
					 * "text/x-apple-plist+xml"); response.setEntity(new
					 * StringEntity(content));
					 */
					response.setStatusCode(HttpStatus.SC_OK);

				} else if (target.equals("/slideshow-features")) {
					String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
							+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
							+ "<plist version=\"1.0\">\n"
							+ " <dict>\n"
							+ "  <key>themes</key>\n"
							+ "  <array>\n"
							+ "   <dict>\n"
							+ "    <key>key</key>\n"
							+ "    <string>Reflections</string>\n"
							+ "    <key>name</key>\n"
							+ "    <string>Reflections</string>\n"
							+ "   </dict>\n"
							+ "  </array>\n"
							+ " </dict>\n"
							+ "</plist>";

					response.setStatusCode(HttpStatus.SC_OK);
					response.setEntity(new StringEntity(content));

				} else if (target.equals("/scrub")) {
					String content = "duration:0.0000\n" + "position:0.00000";

					response.setStatusCode(HttpStatus.SC_OK);
					response.setEntity(new StringEntity(content));
				} else if (target.equals("/playback-info")) {
					/*
					 * String content = "HTTP/1.1 200 OK Date: Fri, 16 Mar 2012
					 * 15:31:42 GMT Content-Type: text/x-apple-plist+xml
					 * Content-Length: 801 X-Transmit-Date:
					 * 2012-03-16T15:31:42.607066Z
					 * 
					 * <?xml version="1.0" encoding="UTF-8"?> <!DOCTYPE plist
					 * PUBLIC "-//Apple//DTD PLIST 1.0//EN"
					 * "http://www.apple.com/DTDs/PropertyList-1.0.dtd"> <plist
					 * version="1.0"> <dict> <key>duration</key>
					 * <real>1801</real> <key>loadedTimeRanges</key> <array>
					 * <dict> <key>duration</key> <real>51.541130402</real>
					 * <key>start</key> <real>18.118717650000001</real> </dict>
					 * </array> <key>playbackBufferEmpty</key> <true/>
					 * <key>playbackBufferFull</key> <false/>
					 * <key>playbackLikelyToKeepUp</key> <true/>
					 * <key>position</key> <real>18.043869775000001</real>
					 * <key>rate</key> <real>1</real> <key>readyToPlay</key>
					 * <true/> <key>seekableTimeRanges</key> <array> <dict>
					 * <key>duration</key> <real>1801</real> <key>start</key>
					 * <real>0.0</real> </dict> </array> </dict> </plist>";
					 */
					response.setStatusCode(HttpStatus.SC_OK);

				}
			} else if (method.equals("POST")) {
				if (target.equals("/reverse")) {
					response.setHeader("Connection", "Upgrade");
					response.setHeader("Upgrade", "PTTH/1.1");
					response.setStatusCode(HttpStatus.SC_SWITCHING_PROTOCOLS);
					// response.setReasonPhrase("Switching Protocols");
				} else if (target.equals("/play")) {

					String contentLocation = "";
					String contentPosition = "";
					// ËßÜÈ¢ë
					Header h_contentype = request
							.getFirstHeader("Content-Type");
					Header h_contentlocation = request
							.getFirstHeader("Content-Location");
					if (h_contentype != null
							&& h_contentype.getValue().indexOf(
									"x-apple-binary-plist") >= 0) {
						// plist
						/*
						 * { "postAuthMs" = 0; "osBuildVersion" = "11D257";
						 * "authMs" = 0; "volume" = 1.0; "path" =
						 * "/var/mobile/Media/DCIM/102APPLE/IMG_2971.MOV";
						 * "playbackRestrictions" = 0; "Content-Location" =
						 * "http://192.168.1.112:7001/1/26508adf-b51f-5f55-9799-61ba406170dd.MOV"
						 * ; "bonjourMs" = 19; "model" = "iPhone4,1";
						 * "Start-Position" = 0.0; "connectMs" = 31; "infoMs" =
						 * 25; "rate" = 0.0; "streamType" = 0; "uuid" =
						 * "07635DDC-CEAF-44F4-8004-E75A3CE6D259";
						 * "secureConnectionMs" = 0; }
						 */
						try {
							NSObject obj = PropertyListParser
									.parse(body_buffer);
							NSDictionary d = (NSDictionary) obj;
							Log.v("alex", "parse binary plist ok!");

							Log.v("alex", d.toASCIIPropertyList());

							contentLocation = d
									.objectForKey("Content-Location")
									.toString();
							contentPosition = d.objectForKey("Start-Position")
									.toString();

							MainActivity.getInstance().showVideo(
									contentLocation);
							Log.v("alex", "url:::::::::::::::"
									+ contentLocation);

						} catch (Exception e) {
							Log.v("alex", "parse binary plist failed!");
						}
					} else if (h_contentlocation != null) {
						contentLocation = h_contentlocation.getValue();
						Log.v("alex",
								"Content-Location="
										+ h_contentlocation.getValue());
						Header header = request
								.getFirstHeader("Start-Position");
						if (header != null) {
							Log.v("alex", "Start-Position=" + header.getValue());
							contentPosition = header.getValue();
						}

						MainActivity.getInstance().showVideo(contentLocation);
						Log.v("alex", "url:::::::::::::::==" + contentLocation);
					}

					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.startsWith("/scrub")) {
					// seek to about 20 seconds
					// /scrub?position=20.097000 HTTP/1.1

					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.startsWith("/rate")) {
					// rate?value=0.0000000
					// value: 0_paused 1_normal speed

					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.equals("/stop")) {
					Log.v("alex", "***** stop *****");
					// MainActivity.getInstance().showImageWithData(null,
					// AirplayConfig.MSG_WHAT_SET_DEFAULT_IMAGE);
					// MainActivity.getInstance().hideVideo();
					// RenderApplication.closeVideo();
					// RenderApplication.closeImg();
					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.equals("/event")) {
					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.equals("/fp-setup")) {

					// 2 1 1 -> 4 : 02 00 02 bb
					int fply_1[] = { 0x46, 0x50, 0x4c, 0x59, 0x02, 0x01, 0x01,
							0x00, 0x00, 0x00, 0x00, 0x04, 0x02, 0x00, 0x02,
							0xbb };

					// 2 1 2 -> 130 : 02 02 xxx
					int fply_2[] = { 0x46, 0x50, 0x4c, 0x59, 0x02, 0x01, 0x02,
							0x00, 0x00, 0x00, 0x00, 0x82, 0x02, 0x02, 0x2f,
							0x7b, 0x69, 0xe6, 0xb2, 0x7e, 0xbb, 0xf0, 0x68,
							0x5f, 0x98, 0x54, 0x7f, 0x37, 0xce, 0xcf, 0x87,
							0x06, 0x99, 0x6e, 0x7e, 0x6b, 0x0f, 0xb2, 0xfa,
							0x71, 0x20, 0x53, 0xe3, 0x94, 0x83, 0xda, 0x22,
							0xc7, 0x83, 0xa0, 0x72, 0x40, 0x4d, 0xdd, 0x41,
							0xaa, 0x3d, 0x4c, 0x6e, 0x30, 0x22, 0x55, 0xaa,
							0xa2, 0xda, 0x1e, 0xb4, 0x77, 0x83, 0x8c, 0x79,
							0xd5, 0x65, 0x17, 0xc3, 0xfa, 0x01, 0x54, 0x33,
							0x9e, 0xe3, 0x82, 0x9f, 0x30, 0xf0, 0xa4, 0x8f,
							0x76, 0xdf, 0x77, 0x11, 0x7e, 0x56, 0x9e, 0xf3,
							0x95, 0xe8, 0xe2, 0x13, 0xb3, 0x1e, 0xb6, 0x70,
							0xec, 0x5a, 0x8a, 0xf2, 0x6a, 0xfc, 0xbc, 0x89,
							0x31, 0xe6, 0x7e, 0xe8, 0xb9, 0xc5, 0xf2, 0xc7,
							0x1d, 0x78, 0xf3, 0xef, 0x8d, 0x61, 0xf7, 0x3b,
							0xcc, 0x17, 0xc3, 0x40, 0x23, 0x52, 0x4a, 0x8b,
							0x9c, 0xb1, 0x75, 0x05, 0x66, 0xe6, 0xb3 };

					// 2 1 3 -> 152
					// 4 : 02 8f 1a 9c
					// 128 : xxx
					// 20 : 5b ed 04 ed c3 cd 5f e6 a8 28 90 3b 42 58 15 cb 74
					// 7d ee 85
					int fply_3[] = { 0x46, 0x50, 0x4c, 0x59, 0x02, 0x01, 0x03,
							0x00, 0x00, 0x00, 0x00, 0x98, 0x02, 0x8f, 0x1a,
							0x9c, 0x6e, 0x73, 0xd2, 0xfa, 0x62, 0xb2, 0xb2,
							0x07, 0x6f, 0x52, 0x5f, 0xe5, 0x72, 0xa5, 0xac,
							0x4d, 0x19, 0xb4, 0x7c, 0xd8, 0x07, 0x1e, 0xdb,
							0xbc, 0x98, 0xae, 0x7e, 0x4b, 0xb4, 0xb7, 0x2a,
							0x7b, 0x5e, 0x2b, 0x8a, 0xde, 0x94, 0x4b, 0x1d,
							0x59, 0xdf, 0x46, 0x45, 0xa3, 0xeb, 0xe2, 0x6d,
							0xa2, 0x83, 0xf5, 0x06, 0x53, 0x8f, 0x76, 0xe7,
							0xd3, 0x68, 0x3c, 0xeb, 0x1f, 0x80, 0x0e, 0x68,
							0x9e, 0x27, 0xfc, 0x47, 0xbe, 0x3d, 0x8f, 0x73,
							0xaf, 0xa1, 0x64, 0x39, 0xf7, 0xa8, 0xf7, 0xc2,
							0xc8, 0xb0, 0x20, 0x0c, 0x85, 0xd6, 0xae, 0xb7,
							0xb2, 0xd4, 0x25, 0x96, 0x77, 0x91, 0xf8, 0x83,
							0x68, 0x10, 0xa1, 0xa9, 0x15, 0x4a, 0xa3, 0x37,
							0x8c, 0xb7, 0xb9, 0x89, 0xbf, 0x86, 0x6e, 0xfb,
							0x95, 0x41, 0xff, 0x03, 0x57, 0x61, 0x05, 0x00,
							0x73, 0xcc, 0x06, 0x7e, 0x4f, 0xc7, 0x96, 0xae,
							0xba, 0x5b, 0xed, 0x04, 0xed, 0xc3, 0xcd, 0x5f,
							0xe6, 0xa8, 0x28, 0x90, 0x3b, 0x42, 0x58, 0x15,
							0xcb, 0x74, 0x7d, 0xee, 0x85 };

					// 2 1 4 -> 20 : 5b ed 04 ed c3 cd 5f e6 a8 28 90 3b 42 58
					// 15 cb 74 7d ee 85
					int fply_4[] = { 0x46, 0x50, 0x4c, 0x59, 0x02, 0x01, 0x04,
							0x00, 0x00, 0x00, 0x00, 0x14, 0x5b, 0xed, 0x04,
							0xed, 0xc3, 0xcd, 0x5f, 0xe6, 0xa8, 0x28, 0x90,
							0x3b, 0x42, 0x58, 0x15, 0xcb, 0x74, 0x7d, 0xee,
							0x85 };

					if (body_buffer != null) {
						Log.v("alex", "=====data:");
						dump_data(body_buffer, 0);

						int[] fply_header = new int[12];
						int header_length = fply_header.length;

						for (int i = 0; i < header_length; i++)
							fply_header[i] = body_buffer[i];

						int data_len = body_buffer.length - fply_header.length;
						int[] payload_data = new int[data_len];
						for (int i = 0; i < data_len; i++)
							payload_data[i] = body_buffer[header_length + i];

						if (fply_header[6] == 1) {
							fply_2[13] = body_buffer[14];

							Log.v("alex", "header 6 = 1");

							// set response = fply_2
							byte[] tmp = intArrayToByteArray(fply_2);
							Log.v("alex", "=====resp:");
							dump_data(tmp, 0);

							response.setEntity(new ByteArrayEntity(tmp));
						} else if (fply_header[6] == 3) {
							Log.v("alex", "header 6 = 3");
						}

					}

					response.setStatusCode(HttpStatus.SC_OK);
				}
			} else if (method.equals("PUT")) {
				if (target.equals("/photo")) {
					String tmpFilename = "";
					boolean showPhoto = true;
					boolean receivePhoto = true;

					if (body_buffer != null
							|| photoAction.equals("displayCached")) {
						if (body_buffer != null)
							dump_data(body_buffer, 3);

						if (photoAction.equals("cacheOnly")) // 只缓存，不现实，预加载图�?
							showPhoto = false;
						else if (photoAction.equals("displayCached")) // 显示缓存里的
							receivePhoto = false;

						if (photoCacheId.length() > 0)
							tmpFilename = String.format("photo-%s",
									photoCacheId);
						else
							tmpFilename = "photo-airplay_photo";

						if (receivePhoto && body_buffer.length > 3
								&& body_buffer[1] == 'P'
								&& body_buffer[2] == 'N'
								&& body_buffer[3] == 'G') {
							s = String.format("%s/%s.png",
									HttpServer.getInstance()._imgCachePath,
									tmpFilename);
							tmpFilename = s;
						} else {
							s = String.format("%s/%s.jpg",
									HttpServer.getInstance()._imgCachePath,
									tmpFilename);
							tmpFilename = s;
						}

						Log.v("alex", "filename=" + tmpFilename);

						if (receivePhoto) {
							Log.v("alex", "write to file");
							FileOutputStream fos = new FileOutputStream(
									tmpFilename);
							fos.write(body_buffer);
							fos.close();
						}
						Log.v("alex", "filename=-------" + showPhoto);

						if (showPhoto) {
							Log.v("alex", "showPhoto");
							MainActivity.getInstance().showImage(tmpFilename);
							byte[] data = loadByteFromFile(tmpFilename);
							// MainActivity.getInstance().showImageWithData(data,
							// AirplayConfig.MSG_WHAT_SET_IMAGE);
							// MainActivity.getInstance().showImageWithData(body_buffer,
							// AirplayConfig.MSG_WHAT_SET_IMAGE);
						}
					}
					response.setStatusCode(HttpStatus.SC_OK);
				} else if (target.startsWith("/slideshows/")) {
					response.setStatusCode(HttpStatus.SC_OK);

					String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
							+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
							+ "<plist version=\"1.0\">\n"
							+ " <dict/>\n"
							+ "</plist>";

					response.setEntity(new StringEntity(content));

				} else if (target.startsWith("/setProperty")) {
					Header header = request.getFirstHeader("Content-Type");
					if (header != null
							&& header.getValue()
									.indexOf("x-apple-binary-plist") >= 0) {
						if (body_buffer != null) {

							try {
								NSObject obj = PropertyListParser
										.parse(body_buffer);
								NSDictionary d = (NSDictionary) obj;
								Log.v("alex", "parse binary plist ok!");

								Log.v("alex", d.toASCIIPropertyList());

							} catch (Exception e) {
								Log.v("alex", "parse binary plist failed!");
							}
						}
					} else if (header != null) {
						// normal plist

					}

					String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
							+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\"\n"
							+ " \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
							+ "<plist version=\"1.0\">\n"
							+ " <dict>\n"
							+ "  <key>errorCode</key>\n"
							+ "  <integer>0</integer>\n"
							+ " </dict>\n"
							+ "</plist>";
					try {
						NSObject obj = PropertyListParser.parse(content
								.getBytes());
						NSDictionary d = (NSDictionary) obj;
						// OutputStream
						// PropertyListParser.saveAsBinary(obj, out);
					} catch (Exception e) {

					}

				}
			}

			// if (method.equals("GET") ) {
			// response.setStatusCode(HttpStatus.SC_OK);
			// StringEntity entity = new
			// StringEntity("<xml><method>get</method><url>" + target +
			// "</url></xml>");
			// response.setEntity(entity);
			// }
			// else if (method.equals("POST") )
			// {
			// response.setStatusCode(HttpStatus.SC_OK);
			// StringEntity entity = new
			// StringEntity("<xml><method>post</method><url>" + target +
			// "</url></xml>");
			// response.setEntity(entity);
			// }
			// else
			// {
			// throw new MethodNotSupportedException(method
			// + " method not supported");
			// }
		}
	}

	static class RequestListenerThread extends Thread {

		private final ServerSocket serversocket;
		private final HttpParams params;
		private final HttpService httpService;

		public void close() {
			this.interrupt();

			try {
				this.serversocket.close();
			} catch (Exception e) {

			}

		}

		public RequestListenerThread(int port) throws IOException {
			//
			this.serversocket = new ServerSocket(port);

			// Set up the HTTP protocol processor
			// HttpProcessor httpproc = new ImmutableHttpProcessor(
			// new HttpResponseInterceptor[] {
			// new ResponseDate(), new ResponseServer(),
			// new ResponseContent(), new ResponseConnControl() });
			//
			// HttpProcessor httpproc = new BasicHttpProcessor;// = new
			// HttpProcessor();

			BasicHttpProcessor httpproc = new BasicHttpProcessor();
			httpproc.addInterceptor(new ResponseDate());
			httpproc.addInterceptor(new ResponseServer());
			httpproc.addInterceptor(new ResponseContent());
			httpproc.addInterceptor(new ResponseConnControl());

			this.params = new BasicHttpParams();
			this.params
					.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
					.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
							8 * 1024)
					.setBooleanParameter(
							CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
					.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
					.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
							"HttpComponents/1.1");

			// Set up request handlers
			HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
			reqistry.register("*", new WebServiceHandler()); // WebServiceHandlerÁî®Êù•Â§ÑÁêÜwebserviceËØ∑Ê±Ç�?ÄÇ

			this.httpService = new HttpService(httpproc,
					new DefaultConnectionReuseStrategy(),
					new DefaultHttpResponseFactory());
			httpService.setParams(this.params);
			httpService.setHandlerResolver(reqistry); // ‰∏∫httpÊúçÂä°ËÆæÁΩÆÊ≥®ÂÜåÂ�?ΩÁöÑËØ∑Ê±ÇÂ§ÑÁêÜÂô®�?ÄÇ
		}

		@Override
		public void run() {
			System.out.println("Listening on port "
					+ this.serversocket.getLocalPort());
			System.out.println("Thread.interrupted = " + Thread.interrupted());
			while (!Thread.interrupted()) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					System.out.println("Incoming connection from "
							+ socket.getInetAddress());
					conn.bind(socket, this.params);

					// Start worker thread
					Thread t = new WorkerThread(this.httpService, conn);
					t.setDaemon(true);
					t.start();
				} catch (InterruptedIOException ex) {
					break;
				} catch (IOException e) {
					System.err
							.println("I/O error initialising connection thread: "
									+ e.getMessage());
					break;
				}
			}

			Log.v("alex", "accept thread terminated");

		}
	}

	static class WorkerThread extends Thread {

		private final HttpService httpservice;
		private final HttpServerConnection conn;

		public WorkerThread(final HttpService httpservice,
				final HttpServerConnection conn) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			System.out.println("New connection thread");
			HttpContext context = new BasicHttpContext(null);
			try {
				while (!Thread.interrupted() && this.conn.isOpen()) {
					this.httpservice.handleRequest(this.conn, context);
				}
			} catch (ConnectionClosedException ex) {
				System.err.println("Client closed connection");
			} catch (IOException ex) {
				System.err.println("I/O error: " + ex.getMessage());
			} catch (HttpException ex) {
				System.err.println("Unrecoverable HTTP protocol violation: "
						+ ex.getMessage());
			} finally {
				try {
					this.conn.shutdown();
				} catch (IOException ignore) {
				}
			}
		}
	}

	public class ClientThread extends Thread {
		public Socket _socket = null;

		@Override
		public void run() {

			try {

				String ip = _socket.getInetAddress().getHostAddress();
				Log.v("alex", "a client connected, " + ip);

				BufferedReader socketinput = new BufferedReader(
						new InputStreamReader(_socket.getInputStream()));
				// Ëé∑Âèñ�?∏Ä�?∏™ËæìÂá∫ÂØ�?�±°Ôºå�?ª•�?æøÊääÊúçÂä°Âô®ÁöÑ‰ø°ÊÅØËøîÂõûÁªôÂÆ¢Êà∑Á´Ø
				PrintWriter socketoutput = new PrintWriter(
						_socket.getOutputStream(), true);
				while (true) {
					String temp = socketinput.readLine();// ËØªÂèñËæìÂÖ•ÂØ�?�±°ÁöÑ�?ø°ÊÅØ
					if (temp == null) {
						break;
					}
					Log.v("alex", ip + "==>" + temp);
				}

				Log.v("alex", "client disconnected");
				socketinput.close();
				socketoutput.close();
				_socket.close();
			} catch (Exception e) {

			}
		}
	}

}