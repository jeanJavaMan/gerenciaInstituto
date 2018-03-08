/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin.classesxml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jeand
 */
@XStreamAlias(value = "Aplicacao")
public class Aplicacao {

	@XStreamAlias(value = "Versao")
	private String versao;
	@XStreamAlias(value = "URL")
	private String url;
	@XStreamAlias(value = "Libs")
	@XStreamImplicit
	private List<Lib> libs;
	@XStreamAlias(value = "ClassModel")
	private List<String> classModels;
	@XStreamAlias(value = "Extras")
	@XStreamImplicit
	private List<Extra> extra;
	@XStreamAlias(value = "FileSize")
	private String fileSize;

	public Aplicacao() {
		this.libs = new ArrayList<>();
		this.classModels = new ArrayList<>();
		this.extra = new ArrayList<>();
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Lib> getLibs() {
		return libs;
	}

	public void setLibs(List<Lib> libs) {
		this.libs = libs;
	}

	public List<String> getClassModels() {
		return classModels;
	}

	public void setClassModels(List<String> classModels) {
		this.classModels = classModels;
	}

	public List<Extra> getExtra() {
		return extra;
	}

	public void setExtra(List<Extra> extra) {
		this.extra = extra;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}
