package com.Tc_traveler.IDSOD.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;

public interface SFTPService {

    void firstUploadFile(String localFilePath, ChannelSftp channelSftp) throws JSchException, SftpException;

    void executeCommand(String command, ChannelShell channelShell) throws JSchException, SftpException, IOException;

    void deleteFile(String filename,ChannelSftp channelSftp) throws JSchException, SftpException;

    void deleteFolder(String folder,ChannelSftp channelSftp) throws JSchException, SftpException;

    void deleteAllFile(File folder);

    void downloadFile(String originalFilePath, ChannelSftp channelSftp) throws SftpException;
}
