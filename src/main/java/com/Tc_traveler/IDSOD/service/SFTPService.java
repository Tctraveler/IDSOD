package com.Tc_traveler.IDSOD.service;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface SFTPService {
    void uploadFile(String localFilePath) throws JSchException, SftpException;
}
