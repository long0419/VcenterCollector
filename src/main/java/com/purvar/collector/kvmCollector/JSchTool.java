package com.purvar.collector.kvmCollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSchTool {

	private JSch jsch;
	private Session session;
	
	private String user;
	private String password;
	private String host;
	private int port;
	
	public JSchTool(String user, String password, String host, int port) throws JSchException{
		this.user = user;
		this.password = password;
		this.host = host;
		this.port = port;
		connect();
	}

	/**
	 * 连接到指定的IP
	 * 
	 */
	private void connect() throws JSchException {
		jsch = new JSch();// 创建JSch对象
		session = jsch.getSession(user, host, port);// 根据用户名、主机ip、端口号获取一个Session对象
		session.setPassword(password);// 设置密码

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);// 为Session对象设置properties
		session.connect();// 通过Session建立连接
	}

	public boolean isConnected(){
		if(session == null){
			return false;
		}
		return session.isConnected();
	}
	
	
	/**
	 * 关闭连接
	 */
	public void close() {
		session.disconnect();
	}

	/**
	 * 执行相关的命令
	 * 
	 * @throws JSchException
	 */
	public String execCmd(String command) throws JSchException {
		String resultStr = "";
		BufferedReader reader = null;
		Channel channel = null;
		try {
			if (command != null) {
				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				// ((ChannelExec) channel).setErrStream(System.err);
				channel.connect();

				InputStream in = channel.getInputStream();
				reader = new BufferedReader(new InputStreamReader(in));
				String buf = null;
				while ((buf = reader.readLine()) != null) {
					resultStr = resultStr + "\n" + buf;
//					System.out.println("resultStr---" + resultStr);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			channel.disconnect();
		}
		return resultStr;
	}
	
}
