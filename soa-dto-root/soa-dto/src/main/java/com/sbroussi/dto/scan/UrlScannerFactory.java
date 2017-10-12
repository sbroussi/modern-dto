package com.sbroussi.dto.scan;

import com.sbroussi.dto.DtoException;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UrlScannerFactory {

    public List<UrlScanner> scanners = new ArrayList<UrlScanner>();


    public UrlScannerFactory() {
        scanners.add(new ScannerJar());
        scanners.add(new ScannerDirectory());
    }


    /**
     * @param url The URL to scan (a ZIP, a JAR, a FileSystem folder...)
     * @return a Scanner capable to scan this URL
     */
    public UrlScanner create(final URL url) {
        for (UrlScanner scanner : scanners) {
            if (scanner.matches(url)) {
                return scanner;
            }
        }
        throw new DtoException("no 'UrlScanner' implemented to handle URL ["
                + url.toExternalForm() + "]; please add an implementation in 'UrlScannerFactory'");

    }


}
