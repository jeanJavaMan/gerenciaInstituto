/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.classesxml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import br.jeanderson.util.LogToFile;

/**
 *
 * @author jeand
 */
@XStreamAlias(value = "Extra")
public class Extra {

    @XStreamAlias(value = "Pasta")
    private String pasta;
    @XStreamAlias(value = "URL")
    private String url;
    @XStreamAlias(value = "FileSize")
    private String fileSize;

    public Extra(String pasta,String url, String fileSize) {
        this.pasta = pasta;
        this.url = url;
        this.fileSize = fileSize;
    }   
    
    public String getPasta() {
        return pasta;
    }

    public void setPasta(String pasta) {
        this.pasta = pasta;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean baixar() {
    	File arquivo = new File(pasta);
    	try {
			FileUtils.copyURLToFile(new URL(url), arquivo);
			return Long.toString(arquivo.length()).equals(fileSize);	
		} catch (IOException e) {
			LogToFile.registerLog(getClass(), "baixar "+pasta.toString(), e);
			return false;
		}
    }
    
}
