/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author jeanderson
 */
@Entity
@Table(name = "lembretes")
public class Lembrete implements Serializable {

    @OneToOne    
    @JoinColumn(name = "contato_id")
    private Contato contato;
    @OneToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;
    @ManyToOne
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lembrete_id")
    private Integer id;
    @Column(name = "data_lembrar")
    private LocalDate dataLembrar;
    @Column(name = "observacoes", columnDefinition = "text")
    private String observacoes;

    public Lembrete() {
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public Integer getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public LocalDate getDataLembrar() {
        return dataLembrar;
    }

    public Aluno getAluno() {
        return aluno;
    }    

    public void setDataLembrar(LocalDate dataLembrar) {
        this.dataLembrar = dataLembrar;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }    
    
}
