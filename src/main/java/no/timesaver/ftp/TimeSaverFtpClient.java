package no.timesaver.ftp;

import no.timesaver.exception.TaveoFtpClientException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class TimeSaverFtpClient {
    private static Logger log = LoggerFactory.getLogger(TimeSaverFtpClient.class);

    private final String server;
    private final int port;
    private final String user;
    private final String pass;
    private final String iconFolder;

    @Autowired
    public TimeSaverFtpClient(
            @Value("${timeSaver.ftp.host}") String server,
            @Value("${timeSaver.ftp.port}") int port,
            @Value("${timeSaver.ftp.un}") String user,
            @Value("${timeSaver.ftp.pw}") String pass,
            @Value("${timeSaver.iconFolder}") String iconFolder
    ) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.iconFolder = iconFolder;
    }

    private Optional<FTPClient> getClient() {
        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return Optional.of(ftpClient);
        } catch (IOException e) {
            log.error("Unable to create FTP Client", e);
            return Optional.empty();
        }
    }

    boolean uploadImage(String fileName,InputStream in) {
        return uploadFile(fileName,iconFolder,in);
    }

    boolean uploadFile(String fileName,String folderFromRoot,InputStream in) {
        FTPClient ftpClient = getClient().orElseThrow(() -> new TaveoFtpClientException("Unable to initialize Taveo FTP client"));
        String dest = folderFromRoot;
        if(StringUtils.isEmpty(dest)) {
            dest = "";
        }
        try {
            boolean done = ftpClient.storeFile(dest + "/" + fileName, in);
            in.close();
            if (done) {
                log.info("Uploaded file {} to folder \"{}\" successfully",fileName,dest);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            log.error("Unable to upload file to Taveo FTP server",e);
            return false;
        } finally {
            closeFtpConnection(ftpClient);
        }
    }

    private void closeFtpConnection(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            log.warn("Failed to log-out/disconnect from FTP server:",ex);
        }
    }
}
