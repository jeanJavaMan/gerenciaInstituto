/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Jeanderson
 */
@Entity
@Table(name = "alunos")
public class Aluno implements Serializable {

    private final IntegerProperty id;
    private final StringProperty nome;
    private final StringProperty cpf;
    private final StringProperty rg;
    private final StringProperty telefone;
    private final StringProperty celular;
    private final StringProperty endereco;
    private final StringProperty bairro;
    private final StringProperty cidade;
    private final StringProperty uf;
    private LocalDate dataNascimento;
    private LocalDate dataMatricula;
    private Funcionario funcionarioMatricula;
    private final BooleanProperty responsavel;
    private final StringProperty observacao;
    private final StringProperty responsavelPor;

    public Aluno() {
        this.id = new SimpleIntegerProperty();
        this.nome = new SimpleStringProperty();
        this.cpf = new SimpleStringProperty();
        this.rg = new SimpleStringProperty();
        this.telefone = new SimpleStringProperty();
        this.celular = new SimpleStringProperty();
        this.endereco = new SimpleStringProperty();
        this.bairro = new SimpleStringProperty();
        this.cidade = new SimpleStringProperty();
        this.uf = new SimpleStringProperty();
        this.responsavel = new SimpleBooleanProperty();
        this.observacao = new SimpleStringProperty();
        this.responsavelPor = new SimpleStringProperty();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aluno_id")
    public Integer getId() {
        return id.get();
    }

    @Column(name = "nome", length = 40)
    public String getNome() {
        return nome.get();
    }

    @Column(name = "cpf", length = 20, unique = true)
    public String getCpf() {
        return cpf.get();
    }

    @Column(name = "rg", length = 15, unique = true)
    public String getRG() {
        return this.rg.get();
    }

    @Column(name = "telefone", length = 16)
    public String getTelefone() {
        return telefone.get();
    }

    @Column(name = "celular", length = 16)
    public String getCelular() {
        return celular.get();
    }

    @Column(name = "endereco", columnDefinition = "text")
    public String getEndereco() {
        return endereco.get();
    }

    @Column(name = "bairro", length = 50)
    public String getBairro() {
        return this.bairro.get();
    }

    @Column(name = "cidade", length = 50)
    public String getCidade() {
        return this.cidade.get();
    }

    @Column(name = "uf", length = 4)
    public String getUf() {
        return this.uf.get();
    }

    @Column(name = "data_nascimento")
    public LocalDate getDataNascimento() {
        return this.dataNascimento;
    }

    @Column(name = "data_matricula")
    public LocalDate getDataMatricula() {
        return this.dataMatricula;
    }

    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    public Funcionario getFuncionarioMatricula() {
        return funcionarioMatricula;
    }

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "aluno_cursos", joinColumns = @JoinColumn(name = "aluno_id"), inverseJoinColumns = @JoinColumn(name = "curso_id"))
//    public List<Curso> getCursos() {
//        return this.cursos;
//    }

    @Column(name = "responsavel")
    public Boolean getResponsavel() {
        return this.responsavel.get();
    }

    @Column(name = "observacao", columnDefinition = "text")
    public String getObservacao() {
        return this.observacao.get();
    }

    @Column(name = "responsavel_por", nullable = false)
    public String getResponsavelPor() {
        return this.responsavelPor.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public StringProperty cpfProperty() {
        return cpf;
    }

    public StringProperty telefoneProperty() {
        return telefone;
    }

    public StringProperty celularProperty() {
        return celular;
    }

    public StringProperty enderecoProperty() {
        return endereco;
    }

    public StringProperty bairroProperty() {
        return this.bairro;
    }

    public StringProperty cidadeProperty() {
        return this.cidade;
    }

    public StringProperty ufProperty() {
        return this.uf;
    }

    public BooleanProperty responsavelProperty() {
        return this.responsavel;
    }

    public StringProperty observacaoProperty() {
        return this.observacao;
    }

    public StringProperty responsavelPorProperty() {
        return this.responsavelPor;
    }

    protected void setId(int id) {
        this.id.set(id);
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public void setCpf(String cpf) {
        this.cpf.set(cpf);
    }

    public void setTelefone(String telefone) {
        this.telefone.set(telefone);
    }

    public void setCelular(String celular) {
        this.celular.set(celular);
    }

    public void setEndereco(String endereco) {
        this.endereco.set(endereco);
    }

//    public void setCursos(List<Curso> cursos) {
//        this.cursos = cursos;
//    }

    public void setRG(String rg) {
        this.rg.set(rg);
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public void setFuncionarioMatricula(Funcionario funcionario) {
        this.funcionarioMatricula = funcionario;
    }

    public void setBairro(String bairro) {
        this.bairro.set(bairro);
    }

    public void setCidade(String cidade) {
        this.cidade.set(cidade);
    }

    public void setUf(String uf) {
        this.uf.set(uf);
    }

    public void setResponsavel(Boolean responsavel) {
        this.responsavel.set(responsavel);
    }

    public void setObservacao(String observacao) {
        this.observacao.set(observacao);
    }

    public void setResponsavelPor(String por) {
        this.responsavelPor.set(por);
    }
}
