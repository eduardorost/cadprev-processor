package com.cadprev.services;

import com.google.common.io.Files;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@Service
public class FileService {

    static Logger log = Logger.getLogger(FileService.class);

    @Value("${folder-download}")
    private String folderDownload;
    @Value("${folder-download-dair}")
    private String folderDownloadDair;

    public void renameFile(String uf, String cidade) throws IOException {
        File dairFile = new File(String.format("%s/DAIR_%s.pdf", folderDownload, new SimpleDateFormat("yyyyMMdd").format(new Date())));
        File dairNewFile = new File(String.format("%s/%s/%s/", folderDownloadDair, uf.replace(" ",""), cidade.replace(" ","")) + dairFile.getName());

        await().atMost(30, SECONDS)
                .ignoreExceptions()
                .until(dairFile::exists);

        Files.createParentDirs(dairNewFile);
        Files.move(dairFile, dairNewFile);
    }

}
