package br.senac.tads.dsw.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.senac.tads.dsw.usuarios.entidades.Papel;

@Repository
public interface PapelRepository extends JpaRepository<Papel, Integer>{

}
