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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import jeanderson.enums.CaixaTipo;

/**
 *
 * @author jeand
 */
@Entity
@Table(name = "caixa")
public class Caixa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "data_geracao")
    private LocalDate dataGeracao;
    @Column(name = "caixa_tipo")
    @Enumerated(EnumType.STRING)
    private CaixaTipo caixaTipo;
    @Column(name = "valor")
    private Double valor;
    @Column(name = "funcionario_nome")
    private String funcionarioNome;

    public Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDate dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public CaixaTipo getCaixaTipo() {
        return caixaTipo;
    }

    public void setCaixaTipo(CaixaTipo caixaTipo) {
        this.caixaTipo = caixaTipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getFuncionarioNome() {
        return funcionarioNome;
    }

    public void setFuncionarioNome(String funcionarioNome) {
        this.funcionarioNome = funcionarioNome;
    }
    
}
