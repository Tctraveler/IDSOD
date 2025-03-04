package com.Tc_traveler.IDSOD.service.impl;

import com.Tc_traveler.IDSOD.service.SFTPService;
import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class SFTPServiceImpl implements SFTPService {
    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.username}")
    private String name;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.firstRemote-dir}")
    private String firstRemoteDir;

    @Value("${sftp.thirdRemote-dir}")
    private String thirdRemoteDir;

    @Value("${savePath}")
    private String localSavePath;

    @Override
    public void firstUploadFile(String localFilePath, ChannelSftp channelSftp) throws SftpException {
        channelSftp.put(localFilePath, firstRemoteDir);
    }

    @Override
    public void secondUploadFile(String localFilePath, ChannelSftp channelSftp) throws SftpException {
        channelSftp.put(localFilePath, thirdRemoteDir);
    }

    @Override
    public void executeCommand(String command) throws IOException, JSchException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelShell channelShell = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        try {
            session = jsch.getSession(name, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelShell = (ChannelShell) session.openChannel("shell");
            channelShell.connect();
            writer = new PrintWriter(channelShell.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
            writer.println(command);
            writer.flush();
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if(line.equals("done")){
                    break;
                }
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (channelShell != null && channelShell.isConnected()) {
                channelShell.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    @Override
    public void deleteFile(String filename,ChannelSftp channelSftp) throws SftpException {
        channelSftp.rm(filename);
    }

    @Override
    public void deleteFolder(String folder) throws JSchException, InterruptedException {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channelExec = null;
        try {
            session = jsch.getSession(name, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand("rm -rf "+folder);
            channelExec.connect();
            while (!channelExec.isClosed()) {
                Thread.sleep(100);
            }
            // 检查退出状态码（0表示成功）
            int exitStatus = channelExec.getExitStatus();
            if (exitStatus != 0) {
                throw new RuntimeException("删除失败，错误码: " + exitStatus);
            }
        } finally {
            if (channelExec != null && channelExec.isConnected()) {
                channelExec.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    @Override
    public void deleteAllFile(File folder){
        if(folder.exists()){
            File[] files = folder.listFiles();
            if(files != null){
                for(File file : files){
                    if(file.isDirectory()){
                        deleteAllFile(file);
                    }else {
                        file.delete();
                    }
                }
            }
            folder.delete();
        }
    }

    @Override
    public void downloadFile(String originalFilePath, ChannelSftp channelSftp) throws SftpException {
        channelSftp.get(originalFilePath,localSavePath);
    }
}
