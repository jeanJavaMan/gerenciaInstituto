/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import jeanderson.enums.Situacao;

/**
 *
 * @author Jeanderson
 */
@Entity
@Table(name = "contatos")
public class Contato implements Serializable {

    private final IntegerProperty id;
    private final StringProperty nome;
    private final StringProperty telefone;
    private final StringProperty observacao;
    private Situacao situacao;
    private Curso cursoInteresse;
    private Funcionario funcionario;

    public Contato() {
        this.id = new SimpleIntegerProperty();
        this.nome = new SimpleStringProperty();
        this.telefone = new SimpleStringProperty();
        this.observacao = new SimpleStringProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contato_id")
    public Integer getId() {
        return id.get();
    }

    @Column(name = "nome", length = 50)
    public String getNome() {
        return nome.get();
    }

    @Column(name = "telefone", length = 20)
    public String getTelefone() {
        return telefone.get();
    }

    @Column(name = "observacao", columnDefinition = "text")
    public String getObservacao() {
        return observacao.get();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao")
    public Situacao getSituacao() {
        return situacao;
    }

    @ManyToOne
    @JoinColumn(name = "curso_id")
    public Curso getCursoInteresse() {        
        return cursoInteresse;
    }
    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    public Funcionario getFuncionario(){
        return this.funcionario;
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public StringProperty telefoneProperty() {
        return telefone;
    }

    public StringProperty observacaoProperty() {
        return observacao;
    }

    protected void setId(int id) {
        this.id.set(id);
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public void setTelefone(String telefone) {
        this.telefone.set(telefone);
    }

    public void setObservacao(String observacao) {
        this.observacao.set(observacao);
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public void setCursoInteresse(Curso cursoInteresse) {
        this.cursoInteresse = cursoInteresse;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    
}
