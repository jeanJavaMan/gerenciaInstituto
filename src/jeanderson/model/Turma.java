/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import jeanderson.enums.Semana;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Formula;

/**
 *
 * @author jeanderson
 */
@Entity
@Table(name = "turmas")
public class Turma implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "turma_id")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    @ManyToMany
    @BatchSize(size = 10)
    @JoinTable(name = "alunos_turmas", joinColumns = @JoinColumn(name = "turma_id"), inverseJoinColumns = @JoinColumn(name = "aluno_id"))    
    private List<Aluno> alunos;
    @Column(name = "horario", length = 50)
    private String horario;
    @ElementCollection(targetClass = Semana.class)
    @BatchSize(size = 5)
    @CollectionTable(name = "turma_dias", joinColumns = @JoinColumn(name = "turma_id"))
    @Column(name = "dias_da_semana")
    @Enumerated(EnumType.STRING)
    private List<Semana> diasDaSemana;
    @Column(name = "limite_alunos")
    private Integer limiteAlunos;
    @Formula(value = "(select count(*) from alunos_turmas where alunos_turmas.turma_id = turma_id)")
    private Integer quantidadeDeAlunos;

    public Turma() {
        this.alunos = new ArrayList<>();
        this.diasDaSemana = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

    public String getHorario() {
        return horario;
    }

    public Integer getQuantidadeDeAlunos() {
        return quantidadeDeAlunos;
    }    

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public List<Semana> getDiasDaSemana() {
        return diasDaSemana;
    }

    public Integer getLimiteAlunos() {
        return limiteAlunos;
    }

    public void setLimiteAlunos(Integer limiteAlunos) {
        this.limiteAlunos = limiteAlunos;
    }

    public void setDiasDaSemana(List<Semana> diasDaSemana) {
        this.diasDaSemana = diasDaSemana;
    }

    public void addAluno(Aluno aluno) {
        this.alunos.add(aluno);
    }

    public void removeAluno(Aluno aluno) {
        this.alunos.remove(aluno);
    }

    public void addSemana(Semana semana) {
        this.diasDaSemana.add(semana);
    }

    public void removeSemana(Semana semana) {
        this.diasDaSemana.remove(semana);
    }

    public BooleanProperty isDisponivel() {
        return new SimpleBooleanProperty(!(this.quantidadeDeAlunos >= limiteAlunos));
    }
}
