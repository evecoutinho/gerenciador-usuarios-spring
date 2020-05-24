package br.senac.tads.dsw.usuarios.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.senac.tads.dsw.usuarios.entidades.Usuario;
import br.senac.tads.dsw.usuarios.repository.PapelRepository;
import br.senac.tads.dsw.usuarios.repository.UsuarioRepository;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private PapelRepository papelRepo;

	@GetMapping
	public ModelAndView listar() {
		List<Usuario> usuarios = usuarioRepo.findAll();
		ModelAndView modelAndView = new ModelAndView("lista-template");
		modelAndView.addObject("itens", usuarios);
		return modelAndView;
	}

	@GetMapping("/novo")
	public ModelAndView adicionarNovo() {
		ModelAndView modelAndView = new ModelAndView("form-template");
		Usuario usuario = new Usuario();
		modelAndView.addObject("item", usuario);
		modelAndView.addObject("papeis", papelRepo.findAll());

		return modelAndView;
	}

	@GetMapping("/editar/{id}")
	public ModelAndView editar(@PathVariable("id") Integer id) {
		Optional<Usuario> optUsuario = usuarioRepo.findById(id);
		if (optUsuario.isPresent()) {
			ModelAndView modelAndView = new ModelAndView("form-template");
			Usuario usuario = optUsuario.get();
			modelAndView.addObject("item", usuario);
			modelAndView.addObject("papeis", papelRepo.findAll());
			return modelAndView;

		} else {
			ModelAndView modelAndView = new ModelAndView("lista-template");
			return modelAndView;
		}
	}

	@PostMapping("/salvar")
	@Transactional
	public ModelAndView salvar(@ModelAttribute("item") @Valid Usuario usuarioEnviado, BindingResult bindingResult,
			RedirectAttributes rediAttr) {
		if (bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("form-template");
			modelAndView.addObject("papeis", papelRepo.findAll());
			return modelAndView;
		}

		if (usuarioEnviado.getId() != null) {
			Optional<Usuario> optUsuario = usuarioRepo.findById(usuarioEnviado.getId());
			if (optUsuario.isPresent()) {
				usuarioEnviado.setDataCadastro(LocalDateTime.now());
				usuarioEnviado = usuarioRepo.save(usuarioEnviado);
				rediAttr.addFlashAttribute("mensagem", "Usuário atualizado com sucesso");
			}

		} else if (usuarioRepo.existsByUsername(usuarioEnviado.getUsername()) == false) {
			usuarioEnviado.setDataCadastro(LocalDateTime.now());
			usuarioEnviado = usuarioRepo.save(usuarioEnviado);
			rediAttr.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso");

		} else {
			rediAttr.addFlashAttribute("mensagemError", "username indisponível");
			ModelAndView modelAndView = new ModelAndView("redirect:/usuario/novo");
			return modelAndView;
		}
		ModelAndView modelAndView = new ModelAndView("redirect:/usuario");
		return modelAndView;
	}

	@PostMapping("/remover/{id}")
	@Transactional
	public String remover(@PathVariable("id") Integer id, RedirectAttributes rediAttr) {
		Optional<Usuario> optUsuario = usuarioRepo.findById(id);
		if (!optUsuario.isPresent()) {
			rediAttr.addAttribute("mensagemError", "Usuário não encontrado");
			return "redirect:/usuario";
		}
		Usuario usuario = optUsuario.get();
		usuarioRepo.delete(usuario);
		
		rediAttr.addFlashAttribute("mensagem", "Usuário deletado com sucesso");
		return "redirect:/usuario";
	}

}
