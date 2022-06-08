/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Dummy SSL Factory
 * 
 * @author gpelipas
 *
 */
public class LdapDummySocketFactory extends SSLSocketFactory {

	private SSLSocketFactory sslSocketFactory;

	public LdapDummySocketFactory() {
		try {
			init();
		} catch (Throwable e) {
			throw new RuntimeException("Error during initialization", e);
		}
	}

	private void init() throws Exception {
		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
			}
		}

		};

		SSLContext sslCtx = SSLContext.getInstance("TLS");
		sslCtx.init(null, trustAllCerts, new java.security.SecureRandom());

		sslSocketFactory = sslCtx.getSocketFactory();
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return sslSocketFactory.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return sslSocketFactory.getDefaultCipherSuites();
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		return sslSocketFactory.createSocket(s, host, port, autoClose);
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return sslSocketFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return sslSocketFactory.createSocket(host, port, localHost, localPort);
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return sslSocketFactory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
			throws IOException {
		return sslSocketFactory.createSocket(address, port, localAddress, localPort);
	}

}
