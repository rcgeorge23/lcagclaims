package uk.co.novinet.e2e;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TestSftpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSftpService.class);

    private String sftpUsername = "user";
    private String sftpPassword = "password";
    private String sftpHost = "localhost";
    private Integer sftpPort = 2222;
    private String sftpRootDirectory = "/upload";

    public void removeAllDocsForEmailAddress(String emailAddress) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;

        try {
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            String memberSftpRootDirectory = memberRootDirectory(emailAddress);

            if (directoryExists(sftpChannel, memberSftpRootDirectory)) {
                recursiveDirectoryDelete(sftpChannel, memberSftpRootDirectory + "/");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private boolean directoryExists(ChannelSftp sftpChannel, String memberSftpRootDirectory) {
        try {
            sftpChannel.stat(memberSftpRootDirectory);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void recursiveDirectoryDelete(ChannelSftp channelSftp, String remoteDir) {
        try {
            if (isDirectory(channelSftp, remoteDir)) {
                Vector<ChannelSftp.LsEntry> dirList = channelSftp.ls(remoteDir);

                for (ChannelSftp.LsEntry entry : dirList) {
                    if (!(entry.getFilename().equals(".") || entry.getFilename().equals(".."))) {
                        if (entry.getAttrs().isDir()) {
                            recursiveDirectoryDelete(channelSftp, remoteDir + entry.getFilename() + File.separator);
                        } else {
                            channelSftp.rm(remoteDir + entry.getFilename());
                        }
                    }
                }

                channelSftp.cd("..");
                channelSftp.rmdir(remoteDir);
            }
        } catch (SftpException e) {
            LOGGER.warn("Could not remove member sftp directory: " + remoteDir, e);
        }
    }

    private boolean isDirectory(ChannelSftp channelSftp, String remoteDirectory) throws SftpException {
        return channelSftp.stat(remoteDirectory).isDir();
    }

    public List<SftpDocument> getAllDocumentsForEmailAddress(String emailAddress) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        List<SftpDocument> sftpDocuments = new ArrayList<>();

        try {
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            String memberSftpRootDirectory = memberRootDirectory(emailAddress);

            Vector<ChannelSftp.LsEntry> memberTimestampDirectories = null;

            try {
                memberTimestampDirectories = sftpChannel.ls(memberSftpRootDirectory);
            } catch (SftpException e) {
                return sftpDocuments;
            }

            for (ChannelSftp.LsEntry timestampSubdirectoryLsEntry : memberTimestampDirectories) {
                if (timestampSubdirectoryLsEntry.getAttrs().isDir()) {
                    String timestampSubdirectory = memberSftpRootDirectory + "/" + timestampSubdirectoryLsEntry.getFilename();

                    Vector<ChannelSftp.LsEntry> memberDocuments = sftpChannel.ls(timestampSubdirectory);
                    for (ChannelSftp.LsEntry documentLsEntry : memberDocuments) {
                        if (!documentLsEntry.getAttrs().isDir()) {
                            sftpDocuments.add(new SftpDocument(documentLsEntry.getFilename(), timestampSubdirectory + "/" + documentLsEntry.getFilename(), Instant.ofEpochMilli(Long.parseLong(timestampSubdirectoryLsEntry.getFilename())), documentLsEntry.getAttrs().getSize()));
                        }
                    }
                }
            }

            return sftpDocuments;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public void uploadFileForEmailAddress(String emailAddress, String filename) {
        Long currentTimeMillis = System.currentTimeMillis();
        String sanitisedEmailAddress = sanitisedEmailAddress(emailAddress);
        String destinationPath = "/" + sanitisedEmailAddress + "/" + currentTimeMillis;

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;

        try {
            session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
            session.setPassword(sftpPassword);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            createDestinationDirectoryIfNecessary(sftpRootDirectory + "/" + destinationPath, sftpChannel);

            File tempFile = File.createTempFile("lcag", "test");
            FileUtils.write(tempFile, "This is some text", "UTF-8");

            sftpChannel.put(new FileInputStream(tempFile), sftpRootDirectory + destinationPath + "/" + filename);
        } catch (Exception e) {
            LOGGER.error("Unable to sftp file {} to {}", filename, destinationPath, e);
            throw new RuntimeException(e);
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private void createDestinationDirectoryIfNecessary(String destinationPath, ChannelSftp sftpChannel) throws SftpException {
        LOGGER.info("Going to create directory {}", destinationPath);
        String[] folders = destinationPath.split("/");
        for (String folder : folders) {
            if (folder.length() > 0) {
                try {
                    sftpChannel.cd(folder);
                } catch (SftpException e) {
                    sftpChannel.mkdir(folder);
                    sftpChannel.cd(folder);
                }
            }
        }
    }

    private String memberRootDirectory(String emailAddress) {
        return sftpRootDirectory + "/" + sanitisedEmailAddress(emailAddress);
    }

    private String sanitisedEmailAddress(String emailAddress) {
        return emailAddress.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
}
