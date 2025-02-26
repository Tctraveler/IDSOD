package com.Tc_traveler.IDSOD.service.impl;

import com.Tc_traveler.IDSOD.service.SFTPService;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;

@Component
public class SFTPServiceImpl implements SFTPService {
    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.remote-dir}")
    private String remoteDir;

    @Override
    public void uploadFile(String localFilePath) throws JSchException, SftpException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;
        try {
            session = jsch.getSession(username,host,port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.put(localFilePath, remoteDir);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    @Override
    public ArrayList<String> executeCommand(String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelShell channel = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        ArrayList<String> feedbacks = new ArrayList<>();
        try {
            session = jsch.getSession(username,host,port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelShell) session.openChannel("shell");
            channel.connect();
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            writer = new PrintWriter(channel.getOutputStream());
            writer.println(command);
            writer.flush();
            String line = null;
            while ((line = reader.readLine()) != null) {
                feedbacks.add(line);
                System.out.println(line);
            }
        } finally {
            if (writer != null && channel.isConnected()) {
                writer.close();
            }
            if (reader != null && channel.isConnected()) {
                reader.close();
            }
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return feedbacks;
    }


}
