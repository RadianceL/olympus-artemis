package com.olympus.base.utils.support.io.ftp;

import com.olympus.base.utils.collection.CollectionUtils;
import com.olympus.base.utils.support.io.ftp.data.FtpInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.ConnectException;

/**
 * ftp文件推送 <br/>
 * 2019/6/5
 *
 * @author eddie.lys
 */
@Slf4j
public class FtpUtils {

    private static FTPClient ftp;

    /**
     * 获取ftp连接
     */
    public static boolean connectFtp(FtpInfo ftpInfo) throws Exception {
        ftp = new FTPClient();
        int reply;
        if (ftpInfo.getPort() == null) {
            ftp.connect(ftpInfo.getIpAddr(), 21);
        } else {
            ftp.connect(ftpInfo.getIpAddr(), ftpInfo.getPort());
        }
        ftp.login(ftpInfo.getUserName(), ftpInfo.getPassWord());
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        ftp.changeWorkingDirectory(ftpInfo.getUploadPath());
        return true;
    }

    /**
     * 关闭ftp连接
     */
    public static void closeFtp() {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException e) {
                throw new RuntimeException("close ftp fail");
            }
        }
    }

    /**
     * ftp上传文件
     */
    public static void upload(File f) throws Exception {
        if (f.isDirectory()) {
            ftp.makeDirectory(f.getName());
            ftp.changeWorkingDirectory(f.getName());
            String[] files = f.list();
            if (CollectionUtils.isEmpty(files)) {
                return;
            }
            for (String fStr : files) {
                File file1 = new File(f.getPath() + File.separator + fStr);
                if (file1.isDirectory()) {
                    upload(file1);
                    ftp.changeToParentDirectory();
                } else {
                    File file2 = new File(f.getPath() + File.separator + fStr);
                    FileInputStream input = new FileInputStream(file2);
                    ftp.storeFile(file2.getName(), input);
                    input.close();
                }
            }
        } else {
            File file2 = new File(f.getPath());
            FileInputStream input = new FileInputStream(file2);
            ftp.storeFile(file2.getName(), input);
            input.close();
        }
    }

    /**
     * 下载链接配置
     *
     * @param ftpInfo       连接信息
     * @param localBaseDir  本地目录
     * @param remoteBaseDir 远程目录
     * @throws Exception 异常
     */
    public static void startDown(FtpInfo ftpInfo, String localBaseDir, String remoteBaseDir) throws Exception {
        if (FtpUtils.connectFtp(ftpInfo)) {
            try {
                FTPFile[] files;
                boolean changeDir = ftp.changeWorkingDirectory(remoteBaseDir);
                if (changeDir) {
                    ftp.setControlEncoding("GBK");
                    files = ftp.listFiles();
                    for (FTPFile file : files) {
                        try {
                            downloadFile(file, localBaseDir, remoteBaseDir);
                        } catch (Exception e) {
                            throw new RuntimeException("startDown download file failed", e);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("download file failed", e);
            }
        } else {
            throw new ConnectException("connect ftp service failed");
        }

    }


    /**
     * 下载FTP文件
     * 当你需要下载FTP文件的时候，调用此方法
     * 根据<b>获取的文件名，本地地址，远程地址</b>进行下载
     */
    private static void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
        if (ftpFile.isFile()) {
            if (!ftpFile.getName().contains("?")) {
                OutputStream outputStream = null;
                try {
                    File localFile = new File(relativeLocalPath + ftpFile.getName());
                    //判断文件是否存在，存在则返回
                    if (!localFile.exists()) {
                        outputStream = new FileOutputStream(relativeLocalPath + ftpFile.getName());
                        ftp.retrieveFile(ftpFile.getName(), outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("下载FTP文件异常", e);
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        } else {
            String newLocalRelatePath = relativeLocalPath + ftpFile.getName();
            String newRemote = relativeRemotePath + ftpFile.getName();
            File fl = new File(newLocalRelatePath);
            if (!fl.exists()) {
                fl.mkdirs();
            }
            try {
                newLocalRelatePath = newLocalRelatePath + File.separator;
                newRemote = newRemote + File.separator;
                String currentWorkDir = ftpFile.getName();
                boolean changeDir = ftp.changeWorkingDirectory(currentWorkDir);
                if (changeDir) {
                    FTPFile[] files;
                    files = ftp.listFiles();
                    for (FTPFile file : files) {
                        downloadFile(file, newLocalRelatePath, newRemote);
                    }
                }
                if (changeDir) {
                    ftp.changeToParentDirectory();
                }
            } catch (Exception e) {
                throw new RuntimeException("download file error", e);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        FtpInfo f = new FtpInfo();
        f.setIpAddr("1111");
        f.setUserName("root");
        f.setPassWord("111111");
        FtpUtils.connectFtp(f);
        File file = new File("");
        //把文件上传在ftp上
        FtpUtils.upload(file);
        //下载ftp文件测试
        FtpUtils.startDown(f, "e:/", "/xxtest");
        //关闭
        FtpUtils.closeFtp();
        System.out.println("ok");
    }

}
