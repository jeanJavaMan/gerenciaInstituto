/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author jeanderson
 */
@Entity
@Table(name = "frequencias")
public class Frequencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;
    @OneToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;
    @Column(name = "carga_horaria")
    private Duration cargaHoraria;
    @Column(name = "carga_cumprida", nullable = false)
    private Duration cargaCumprida;
    @Column(name = "porcentagem_concluida", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer porcentagemConcluida;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public Duration getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Duration cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Duration getCargaCumprida() {
        return cargaCumprida;
    }

    public void setCargaCumprida(Duration cargaCumprida) {
        this.cargaCumprida = cargaCumprida;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Integer getPorcentagemConcluida() {
        return porcentagemConcluida;
    }

    public void setPorcentagemConcluida(Integer porcentagemConcluida) {
        if (porcentagemConcluida > 100) {
            this.porcentagemConcluida = 100;
        } else {
            this.porcentagemConcluida = porcentagemConcluida;
        }
    }

    public void aumentarCargaCumprida(Duration cargaAumentada) {
        this.cargaCumprida = cargaCumprida.plus(cargaAumentada);
    }

}
