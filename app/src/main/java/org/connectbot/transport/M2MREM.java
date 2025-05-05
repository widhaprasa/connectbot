package org.connectbot.transport;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.connectbot.bean.HostBean;

import com.trilead.ssh2.ServerHostKeyVerifier;

import android.net.Uri;
import android.util.Base64;

public class M2MREM {

	private static final String PROTOCOL = "m2mrem";

	public static String getProtocolName() {
		return PROTOCOL;
	}

	public static void assignHost(HostBean host, Uri uri) {

		// Parse host and port
		String hostname = uri.getHost();
		int port = uri.getPort();
		if (port == -1) {
			port = 22;
		}
		host.setNickname(hostname + ':' + port);

		// Parse query
		String query = uri.getQuery();
		Map<String, String> queryMap = new HashMap<>();
		if (query != null && !query.isEmpty()) {
			String[] token = query.split("&");
			for (String t : token) {
				int i = t.indexOf("=");
				try {
					queryMap.put(URLDecoder.decode(t.substring(0, i), "UTF-8"),
							URLDecoder.decode(t.substring(i + 1), "UTF-8"));
				} catch (UnsupportedEncodingException ignored) {
				}
			}
		}

		// Parse username and password
		String username = "root";
		String password = null;
		if (queryMap.containsKey("u")) {
			String b = queryMap.get("u");
			try {
				username = new String(Base64.decode(b, Base64.DEFAULT));
			} catch (IllegalArgumentException ignored) {
			}

			if (queryMap.containsKey("p")) {
				b = queryMap.get("p");
				try {
					password = new String(Base64.decode(b, Base64.DEFAULT));
				} catch (IllegalArgumentException ignored) {
				}
			}
		}

		host.setHostname(hostname);
		host.setPort(port);
		host.setUsername(username);
		host.setPassword(password);
	}

	public static class HostKeyVerifier implements ServerHostKeyVerifier {

		@Override
		public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
			return true;
		}
	}
}
