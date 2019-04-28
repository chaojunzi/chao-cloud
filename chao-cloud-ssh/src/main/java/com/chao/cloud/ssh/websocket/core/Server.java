package com.chao.cloud.ssh.websocket.core;

public class Server {
	
	//主机名称
	private String hostname;
	//用户名
	private String username;
	//密码
	private String password;

	public Server(String hostname, String username, String password) {
		super();
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
