/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.util;

/**
 * Classe para informa se ocorreu um processo com sucesso ou não
 *
 * @author Jeanderson
 */
public class ResultadoBD {

    private String mensagem;
    private boolean execucaoResult;

    public ResultadoBD() {
        this.mensagem = "";
        this.execucaoResult = false;
    }

    public String mensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.identificaErro(mensagem);
    }

    public boolean resultado() {
        return execucaoResult;
    }

    public void setResultado(boolean execucaoResult) {
        this.execucaoResult = execucaoResult;
    }

    private void identificaErro(String mensagem) {
        if (mensagem.contains("ConstraintViolationException")) {
            this.mensagem = "\nProvável erro: Já existe um dado igual salvo, ou ele está sendo utilizado também em outro lugar.\nErro: " + mensagem;
        } else if (mensagem.contains("DataException")) {
            this.mensagem = "\nProvável erro: Ultrapassou o limite de dados(texto digitado) permitido! Ex: Observações, Nome e etc...\nErro: " + mensagem;
        } else if (mensagem.contains("PersistentObjectException")) {
            this.mensagem = "\nProvável erro: Erro de programação, tentanto salvar um objeto que já existe!\nErro: " + mensagem;
        } else if (mensagem.contains("PropertyValueException")) {
            this.mensagem = "\nProvável erro: Algum dado pode está vázio, cujo é obrigátorio que não esteja!\nErro: " + mensagem;
        } else {
            this.mensagem = mensagem;
        }
    }

}
