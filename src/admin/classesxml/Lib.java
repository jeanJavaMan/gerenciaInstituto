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
@XStreamAlias(value = "Lib")
public class Lib {

    @XStreamAlias(value = "nome")
    private String nomeLib;
    @XStreamAlias(value = "URL")
    private String url;
    @XStreamAlias(value = "FileSize")
    private String fileSize;
    private File pasta;
    
    public Lib(String nomeLib, String url, String fileSize) {
        this.nomeLib = nomeLib;
        this.url = url;
        this.fileSize = fileSize;
    }

    public String getNomeLib() {
        return nomeLib;
    }

    public void setNomeLib(String nomeLib) {
        this.nomeLib = nomeLib;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
    public String getFileSize() {
		return fileSize;
	}
    
    public boolean baixar() {
    	this.pasta = new File("/lib/"+nomeLib);
    	try {
			FileUtils.copyURLToFile(new URL(url), pasta);
			return Long.toString(pasta.length()).equals(fileSize);				
		} catch (IOException e) {
			LogToFile.registerLog(getClass(), "baixar "+nomeLib, e);
			return false;
		}
    }
    
}
