/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javax.persistence.Table;
import jeanderson.enums.FuncionarioTipo;

/**
 *
 * @author Jeanderson
 */
@Entity
@Table(name = "funcionarios")
public class Funcionario implements Serializable {

    private final IntegerProperty id;
    private final StringProperty nome;
    private final StringProperty telefone;
    private final StringProperty email;
    private FuncionarioTipo funcao;
    private BooleanProperty privilegioAdmin;
    private BooleanProperty ativo;

    public Funcionario() {
        this.id = new SimpleIntegerProperty();
        this.nome = new SimpleStringProperty();
        this.telefone = new SimpleStringProperty();
        this.email = new SimpleStringProperty();
        this.privilegioAdmin = new SimpleBooleanProperty();
        this.ativo = new SimpleBooleanProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funcionario_id")
    public Integer getId() {
        return id.get();
    }

    @Column(name = "nome", length = 40, unique = true)
    public String getNome() {
        return nome.get();
    }

    @Column(name = "telefone", length = 20)
    public String getTelefone() {
        return telefone.get();
    }

    @Column(name = "email", length = 50)
    public String getEmail() {
        return email.get();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "funcao", length = 40)
    public FuncionarioTipo getFuncao() {
        return funcao;
    }

    @Column(name = "privilegio_admin", columnDefinition = "bit default 0", nullable = false)
    public Boolean getPrivilegioAdmin() {
        return privilegioAdmin.get();
    }

    @Column(name = "situacao", columnDefinition = "bit default 1")
    public Boolean getAtivo() {
        return ativo.get();
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

    public StringProperty emailProperty() {
        return email;
    }

    public BooleanProperty ativoProperty() {
        return this.ativo;
    }

    public BooleanProperty privilegioAdmin() {
        return this.privilegioAdmin;
    }

    public void setFuncao(FuncionarioTipo funcao) {
        this.funcao = funcao;
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

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setPrivilegioAdmin(boolean privilegioAdmin) {
        this.privilegioAdmin.set(privilegioAdmin);
    }

    public void setAtivo(Boolean ativo) {
        this.ativo.set(ativo);
    }

}
