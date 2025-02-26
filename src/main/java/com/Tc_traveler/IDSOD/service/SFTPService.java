package com.Tc_traveler.IDSOD.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.util.ArrayList;

public interface SFTPService {

    void uploadFile(String localFilePath) throws JSchException, SftpException;

    ArrayList<String> executeCommand(String command) throws JSchException, SftpException, IOException;

}
