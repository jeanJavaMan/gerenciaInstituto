/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeanderson.model.Usuario;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author jeand
 */
public class Email {

    private final HtmlEmail email;
    private final File arqLog;

    public Email() {
        this.arqLog = new File("log.txt");
        this.email = new HtmlEmail();
        this.email.setSSLOnConnect(true);
        email.setHostName("smtp.gmail.com");
        email.setSslSmtpPort("465");
        email.setAuthentication("gerenciainstitutomarques@gmail.com", "marques1023x");

    }

    public ResultadoBD reportarErros(String msg, String nome) {
        String comentario = "Nome: " + nome + "\nComentário: " + msg;
        ResultadoBD result = new ResultadoBD();
        try {
            if (arqLog.exists()) {
                EmailAttachment anexo = new EmailAttachment();
                anexo.setPath(arqLog.getName());
                anexo.setDisposition(EmailAttachment.ATTACHMENT);
                anexo.setName("log.txt");
                email.attach(anexo);
            }
            email.setFrom("gerenciainstitutomarques@gmail.com");
            email.setSubject("Reportando erros no programa!");
            email.setMsg(comentario);
            email.addTo("jeandersonsilvalopes@gmail.com");
            email.send();
            result.setMensagem("Enviado com sucesso!");
            result.setResultado(true);
            if (arqLog.exists()) {
                arqLog.delete();
            }
        } catch (EmailException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            Log.salvaLogger(ex);
            result.setResultado(false);
            result.setMensagem(ex.getMessage());
        }
        return result;
    }
    
    public ResultadoBD enviarRecuperacaoDeSenha(Usuario usuario){
        String mensagem = "Recuperação de Senha e Usuário requisitado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/M/yyyy"));
        ResultadoBD result = new ResultadoBD();
        mensagem += "\nUsuário: " + usuario.getUsuario() + "\nSenha: " + usuario.getSenha();
        try {
            email.setFrom("gerenciainstitutomarques@gmail.com");
            email.setSubject("Recuperação de Senha");
            email.addTo(usuario.getFuncionario().getEmail());
            email.setMsg(mensagem);
            email.send();
            result.setMensagem("Enviado com sucesso!");
            result.setResultado(true);
        } catch (EmailException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
            Log.salvaLogger(ex);
            result.setResultado(false);
            result.setMensagem(ex.getMessage());
        }
        return result;
    }

}
