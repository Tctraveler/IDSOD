package com.Tc_traveler.IDSOD.service;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;

public interface SFTPService {

    void firstUploadFile(String localFilePath, ChannelSftp channelSftp) throws JSchException, SftpException;

    void executeCommand(String command) throws JSchException, SftpException, IOException;

    void deleteFile(String filename,ChannelSftp channelSftp) throws JSchException, SftpException;

    void deleteFolder(String folder) throws JSchException, SftpException, IOException, InterruptedException;

    void deleteAllFile(File folder);

    void downloadFile(String originalFilePath, ChannelSftp channelSftp) throws SftpException;

    void secondUploadFile(String localFilePath, ChannelSftp channelSftp) throws SftpException;
}
