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
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Value("${sftp.firstRemote-dir}")
    private String firstRemoteDir;

    @Value("${savePath}")
    private String localSavePath;

    @Override
    public void firstUploadFile(String localFilePath, ChannelSftp channelSftp) throws SftpException {
        channelSftp.put(localFilePath, firstRemoteDir);
    }

    @Override
    public void executeCommand(String command,ChannelShell channelShell) throws IOException {
        PrintWriter writer;
        BufferedReader reader;
        writer = new PrintWriter(channelShell.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(channelShell.getInputStream()));
        writer.println(command);
        writer.flush();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        writer.close();
        reader.close();
    }

    @Override
    public void deleteFile(String filename,ChannelSftp channelSftp) throws SftpException {
        channelSftp.rm(filename);
    }

    @Override
    public void deleteFolder(String folder,ChannelSftp channelSftp) throws SftpException {
        channelSftp.rmdir(folder);
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
