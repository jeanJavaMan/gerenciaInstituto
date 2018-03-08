/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import admin.classesxml.Aplicacao;
import admin.classesxml.Extra;
import admin.classesxml.Lib;
import com.thoughtworks.xstream.XStream;

/**
 *
 * @author jeand
 */
public class TesteApplicationXML {
    public static void main(String[] args) {
        XStream stream = new XStream();
        stream.processAnnotations(Aplicacao.class);
        Aplicacao app = new Aplicacao();
        app.setVersao("1.9.4");
        app.setUrl("https://drive.google.com/uc?export=download&id=0B6YGL0lcp06mVlVadjJOS2tBbUk");
        app.setFileSize("4453378");
        //app.getLibs().add(new Lib("commons-beanutils-1.9.3.jar", "https://drive.google.com/uc?export=download&id=0B6YGL0lcp06mNWVmZkVGc3JabUk","246174"));        
        app.getExtra().add(new Extra("UpdateNew.jar", "https://drive.google.com/uc?export=download&id=0B6YGL0lcp06mbEp5NUNNNkMxaWs", "84611"));
        app.getClassModels().add("Caixa");
        System.out.println(stream.toXML(app));
    }
}
