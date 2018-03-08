/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jeanderson.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
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
import jeanderson.enums.MensalidadeTipo;
import jeanderson.enums.PagamentoTipo;

/**
 *
 * @author jeanderson
 */
@Entity
@Table(name = "mensalidades")
public class Mensalidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;
    @Column(name = "valor_mensalidade")
    private Double valorParaPagar;
    @Enumerated(EnumType.STRING)
    private PagamentoTipo formaPagamento;
    @Enumerated(EnumType.STRING)
    private MensalidadeTipo tipoDaMensalidade;
    @Column(name = "numero_parcela")
    private Integer numeroDaParcela;
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    @Column(name = "data_de_geracao")
    private LocalDate dataDeGeracao;
    @Column(name = "situacao")
    private Boolean situacao;
    @Column(name = "cursos_referentes", length = 100)
    private String cursosReferentes;
    @Column(name = "data_de_pagamento")
    private LocalDate dataDePagamento;

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

    public PagamentoTipo getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(PagamentoTipo formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public MensalidadeTipo getTipoDaMensalidade() {
        return tipoDaMensalidade;
    }

    public void setTipoDaMensalidade(MensalidadeTipo tipoDaMensalidade) {
        this.tipoDaMensalidade = tipoDaMensalidade;
    }

    public Integer getNumeroDaParcela() {
        return numeroDaParcela;
    }

    public void setNumeroDaParcela(Integer numeroDaParcela) {
        this.numeroDaParcela = numeroDaParcela;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Boolean getSituacao() {
        return situacao;
    }

    public void setSituacao(Boolean situacao) {
        this.situacao = situacao;
    }

    public Double getValorParaPagar() {
        return valorParaPagar;
    }

    public void setValorParaPagar(Double valorParaPagar) {
        this.valorParaPagar = valorParaPagar;
    }

    public LocalDate getDataDePagamento() {
        return dataDePagamento;
    }

    public void setDataDePagamento(LocalDate dataDePagamento) {
        this.dataDePagamento = dataDePagamento;
    }
        
    public LocalDate getDataDeGeracao() {
        return dataDeGeracao;
    }

    public void setDataDeGeracao(LocalDate dataDeGeracao) {
        this.dataDeGeracao = dataDeGeracao;
    }

    public String getCursosReferentes() {
        return cursosReferentes;
    }

    public void setCursosReferentes(String cursosReferentes) {
        this.cursosReferentes = cursosReferentes;
    }

    public void addCursoReferente(Curso curso) {
        this.cursosReferentes = curso.getNome();
    }

    public void addCursosReferentes(List<Curso> cursos) {
        if (cursos.size() > 1) {
            cursos.forEach(curso -> {
                this.cursosReferentes += curso.getNome() + ",";
            });
        } else {
            this.addCursoReferente(cursos.get(0));
        }
    }

    public String[] getCursosReferenteFormatado() {
        if (this.cursosReferentes != null) {
            return this.cursosReferentes.split(",");
        } else {
            return new String[0];
        }
    }
}
