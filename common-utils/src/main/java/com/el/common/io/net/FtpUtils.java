package com.el.common.io.net;

import com.el.common.io.entity.FtpBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

/**
 * ftp文件推送
 * 2019/6/5
 *
 * @author eddielee
 */
@Slf4j
public class FtpUtils {

    private static FTPClient ftp;

    /**
     * 获取ftp连接
     *
     * @param ftpBO
     * @return
     * @throws Exception
     */
    public static boolean connectFtp(FtpBO ftpBO) throws Exception {
        ftp = new FTPClient();
        int reply;
        if (ftpBO.getPort() == null) {
            ftp.connect(ftpBO.getIpAddr(), 21);
        } else {
            ftp.connect(ftpBO.getIpAddr(), ftpBO.getPort());
        }
        ftp.login(ftpBO.getUserName(), ftpBO.getPassWord());
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        ftp.changeWorkingDirectory(ftpBO.getUploadPath());
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
                e.printStackTrace();
            }
        }
    }

    /**
     * ftp上传文件
     *
     * @param f
     * @throws Exception
     */
    public static void upload(File f) throws Exception {
        if (f.isDirectory()) {
            ftp.makeDirectory(f.getName());
            ftp.changeWorkingDirectory(f.getName());
            String[] files = f.list();
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
     * @param f
     * @param localBaseDir  本地目录
     * @param remoteBaseDir 远程目录
     * @throws Exception
     */
    public static void startDown(FtpBO f, String localBaseDir, String remoteBaseDir) throws Exception {
        if (FtpUtils.connectFtp(f)) {
            try {
                FTPFile[] files;
                boolean changeDir = ftp.changeWorkingDirectory(remoteBaseDir);
                if (changeDir) {
                    ftp.setControlEncoding("GBK");
                    files = ftp.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        try {
                            downloadFile(files[i], localBaseDir, remoteBaseDir);
                        } catch (Exception e) {
                            log.error("startDown [{}]下载失败", files[i].getName(), e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("下载过程中出现异常", e);
            }
        } else {
            log.error("链接失败！");
        }

    }


    /**
     * 下载FTP文件
     * 当你需要下载FTP文件的时候，调用此方法
     * 根据<b>获取的文件名，本地地址，远程地址</b>进行下载
     *
     * @param ftpFile
     * @param relativeLocalPath
     * @param relativeRemotePath
     */
    private static void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
        if (ftpFile.isFile()) {
            if (ftpFile.getName().indexOf("?") == -1) {
                OutputStream outputStream = null;
                try {
                    File locaFile = new File(relativeLocalPath + ftpFile.getName());
                    //判断文件是否存在，存在则返回
                    if (locaFile.exists()) {
                        return;
                    } else {
                        outputStream = new FileOutputStream(relativeLocalPath + ftpFile.getName());
                        ftp.retrieveFile(ftpFile.getName(), outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e) {
                    log.error("下载FTP文件异常", e);
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        log.error("输出文件流异常", e);
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
                    for (int i = 0; i < files.length; i++) {
                        downloadFile(files[i], newLocalRelatePath, newRemote);
                    }
                }
                if (changeDir) {
                    ftp.changeToParentDirectory();
                }
            } catch (Exception e) {
                log.error("下载异常", e);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        FtpBO f = new FtpBO();
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
