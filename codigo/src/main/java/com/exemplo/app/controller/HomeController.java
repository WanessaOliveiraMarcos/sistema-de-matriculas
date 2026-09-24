package com.exemplo.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.exemplo.app.dto.LoginResponse;
import com.exemplo.app.dto.MatriculaRequest;
import com.exemplo.app.dto.TurmaRequest;
import com.exemplo.app.model.Aluno;
import com.exemplo.app.model.Curso;
import com.exemplo.app.model.Disciplina;
import com.exemplo.app.model.Matricula;
import com.exemplo.app.model.Professor;
import com.exemplo.app.model.Semestre;
import com.exemplo.app.model.Turma;
import com.exemplo.app.model.Usuario;
import com.exemplo.app.model.enums.StatusAluno;
import com.exemplo.app.model.enums.TipoMatricula;
import com.exemplo.app.model.enums.Titulacao;
import com.exemplo.app.security.JwtAuthenticationFilter;
import com.exemplo.app.service.AlunoService;
import com.exemplo.app.service.CursoService;
import com.exemplo.app.service.DisciplinaService;
import com.exemplo.app.service.MatriculaService;
import com.exemplo.app.service.ProfessorService;
import com.exemplo.app.service.SemestreService;
import com.exemplo.app.service.TurmaService;
import com.exemplo.app.service.UsuarioService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// Centraliza a entrega das páginas (templates Thymeleaf) e o recebimento dos
// formulários das telas. Os dados são renderizados no servidor; as regras de
// negócio continuam nos services existentes.
@Controller
@RequiredArgsConstructor
public class HomeController {

    /** Auxiliar para a página do professor: turma + alunos matriculados. */
    public record TurmaComAlunos(Turma turma, List<Aluno> alunos) {
    }

    private final UsuarioService usuarioService;
    private final CursoService cursoService;
    private final DisciplinaService disciplinaService;
    private final SemestreService semestreService;
    private final TurmaService turmaService;
    private final AlunoService alunoService;
    private final ProfessorService professorService;
    private final MatriculaService matriculaService;

    // ------------------------------------------------------------------
    // Login / Logout
    // ------------------------------------------------------------------

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String email, @RequestParam String senha,
            HttpServletResponse response, Model model) {
        try {
            LoginResponse dados = usuarioService.autenticar(email, senha);

            Cookie cookie = new Cookie(JwtAuthenticationFilter.COOKIE_NAME, dados.token());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(120 * 60); // mesmo valor de app.jwt.expiracao-minutos
            response.addCookie(cookie);

            return switch (dados.perfil()) {
                case "SECRETARIO" -> "redirect:/secretaria";
                case "PROFESSOR" -> "redirect:/professor";
                case "ALUNO" -> "redirect:/aluno";
                default -> "redirect:/login";
            };
        } catch (ResponseStatusException ex) {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtAuthenticationFilter.COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }

    // ------------------------------------------------------------------
    // Secretaria
    // ------------------------------------------------------------------

    @GetMapping("/secretaria")
    public String secretaria(Model model) {
        preencherSecretaria(model);
        return "secretaria";
    }

    @PostMapping("/secretaria/cursos")
    public String criarCurso(@RequestParam String nome, @RequestParam Integer creditos,
            RedirectAttributes ra, Model model) {
        try {
            Curso curso = new Curso();
            curso.setNome(nome);
            curso.setCreditos(creditos);
            cursoService.criar(curso);
            ra.addFlashAttribute("sucesso", "Curso cadastrado com sucesso.");
            return "redirect:/secretaria";
        } catch (Exception ex) {
            return erroSecretaria(model, ex);
        }
    }

    @PostMapping("/secretaria/disciplinas")
    public String criarDisciplina(@RequestParam String nome,
            @RequestParam TipoMatricula tipoDisciplina,
            @RequestParam(required = false) Integer cursoId,
            RedirectAttributes ra, Model model) {
        try {
            Disciplina disciplina = new Disciplina();
            disciplina.setNome(nome);
            disciplina.setTipoMatricula(tipoDisciplina);
            Disciplina criada = disciplinaService.criar(disciplina);
            if (cursoId != null) {
                cursoService.adicionarDisciplina(cursoId, criada.getCodigo());
            }
            ra.addFlashAttribute("sucesso", "Disciplina cadastrada com sucesso.");
            return "redirect:/secretaria";
        } catch (Exception ex) {
            return erroSecretaria(model, ex);
        }
    }

    @PostMapping("/secretaria/semestres")
    public String criarSemestre(@RequestParam Integer ano, @RequestParam Integer semestre,
            @RequestParam LocalDate inicioMatriculas, @RequestParam LocalDate finalMatriculas,
            RedirectAttributes ra, Model model) {
        try {
            Semestre s = new Semestre();
            s.setAno(ano);
            s.setSemestre(semestre);
            s.setInicioMatriculas(inicioMatriculas);
            s.setFinalMatriculas(finalMatriculas);
            semestreService.criar(s);
            ra.addFlashAttribute("sucesso", "Semestre cadastrado com sucesso.");
            return "redirect:/secretaria";
        } catch (Exception ex) {
            return erroSecretaria(model, ex);
        }
    }

    @PostMapping("/secretaria/turmas")
    public String criarTurma(@RequestParam Integer professorCodigo,
            @RequestParam Integer disciplinaCodigo, @RequestParam Integer semestreCodigo,
            RedirectAttributes ra, Model model) {
        try {
            turmaService.criar(new TurmaRequest(professorCodigo, disciplinaCodigo, semestreCodigo));
            ra.addFlashAttribute("sucesso", "Turma cadastrada com sucesso.");
            return "redirect:/secretaria";
        } catch (Exception ex) {
            return erroSecretaria(model, ex);
        }
    }

    @PostMapping("/secretaria/alunos")
    public String criarAluno(@RequestParam String nome, @RequestParam String email,
            @RequestParam String senha, RedirectAttributes ra, Model model) {
        try {
            Aluno aluno = new Aluno();
            aluno.setNome(nome);
            aluno.setEmail(email);
            aluno.setSenha(senha);
            alunoService.criar(aluno);
            ra.addFlashAttribute("sucesso", "Aluno cadastrado com sucesso.");
            return "redirect:/secretaria";
        } catch (Exception ex) {
            return erroSecretaria(model, ex);
        }
    }

    @PostMapping("/secretaria/professores")
    public String criarProfessor(@RequestParam String nome, @RequestParam String email,
            @RequestParam String senha, @RequestParam Titulacao titulacao,
            RedirectAttributes ra, Model model) {
        try {
            Professor professor = new Professor();
            professor.setNome(nome);
            professor.setEmail(email);
            professor.setSenha(senha);
            professor.setTitulacao(titulacao);
            professorService.criar(professor);
            ra.addFlashAttribute("sucesso", "Professor cadastrado com sucesso.");
            return "redirect:/secretaria";
        } catch (Exception ex) {
            return erroSecretaria(model, ex);
        }
    }

    // ------------------------------------------------------------------
    // Professor
    // ------------------------------------------------------------------

    @GetMapping("/professor")
    public String professor(Model model) {
        Usuario usuario = usuarioLogado();
        List<Turma> turmas = professorService.listarTurmas(usuario.getCodigo());
        List<TurmaComAlunos> dados = turmas.stream()
                .map(t -> new TurmaComAlunos(t, turmaService.listarAlunos(t.getCodigo())))
                .toList();
        model.addAttribute("nomeUsuario", usuario.getNome());
        model.addAttribute("turmas", dados);
        return "professor";
    }

    // ------------------------------------------------------------------
    // Aluno
    // ------------------------------------------------------------------

    @GetMapping("/aluno")
    public String aluno(Model model) {
        preencherAluno(model);
        return "aluno";
    }

    @PostMapping("/aluno/matricular")
    public String matricular(@RequestParam Integer turmaCodigo,
            RedirectAttributes ra, Model model) {
        Integer alunoCodigo = usuarioLogado().getCodigo();
        try {
            matriculaService.matricular(new MatriculaRequest(alunoCodigo, turmaCodigo));
            ra.addFlashAttribute("sucesso", "Matrícula realizada com sucesso!");
            return "redirect:/aluno";
        } catch (Exception ex) {
            model.addAttribute("erro", mensagem(ex));
            preencherAluno(model);
            return "aluno";
        }
    }

    @PostMapping("/aluno/cancelar")
    public String cancelar(@RequestParam Integer matriculaCodigo, RedirectAttributes ra, Model model) {
        Integer alunoCodigo = usuarioLogado().getCodigo();
        try {
            Matricula matricula = matriculaService.buscar(matriculaCodigo);
            if (!matricula.getAluno().getCodigo().equals(alunoCodigo)) {
                throw new IllegalStateException("Matrícula não pertence ao aluno logado");
            }
            matriculaService.cancelar(matriculaCodigo);
            ra.addFlashAttribute("sucesso", "Matrícula cancelada com sucesso.");
            return "redirect:/aluno";
        } catch (Exception ex) {
            model.addAttribute("erro", mensagem(ex));
            preencherAluno(model);
            return "aluno";
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void preencherSecretaria(Model model) {
        model.addAttribute("cursos", cursoService.listar());
        model.addAttribute("disciplinas", disciplinaService.listar());
        model.addAttribute("semestres", semestreService.listar());
        model.addAttribute("turmas", turmaService.listar());
        model.addAttribute("alunos", alunoService.listar());
        model.addAttribute("professores", professorService.listar());
        model.addAttribute("nomeUsuario", usuarioLogado().getNome());
    }

    private void preencherAluno(Model model) {
        Usuario usuario = usuarioLogado();
        // Alunos não ativos (trancados/formados) podem apenas visualizar o histórico,
        // sem realizar matrículas ou cancelamentos
        boolean ativo = usuario instanceof Aluno aluno && aluno.getStatus() == StatusAluno.ATIVO;
        model.addAttribute("nomeUsuario", usuario.getNome());
        model.addAttribute("podeMatricular", ativo);
        model.addAttribute("matriculas", matriculaService.listarPorAluno(usuario.getCodigo()));
        // Apenas turmas cujo período de matrículas está aberto (e para alunos ativos)
        List<Turma> turmas = List.of();
        if (ativo) {
            LocalDate hoje = LocalDate.now();
            turmas = turmaService.listar().stream()
                    .filter(t -> t.getSemestre() != null && t.getSemestre().periodoMatriculaAberto(hoje))
                    .toList();
        }
        model.addAttribute("turmas", turmas);
    }

    private String erroSecretaria(Model model, Exception ex) {
        model.addAttribute("erro", mensagem(ex));
        preencherSecretaria(model);
        return "secretaria";
    }

    private Usuario usuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        throw new IllegalStateException("Usuário não autenticado");
    }

    private String mensagem(Exception ex) {
        if (ex instanceof ResponseStatusException rse) {
            return rse.getReason() != null ? rse.getReason() : rse.getMessage();
        }
        return ex.getMessage() != null ? ex.getMessage() : "Erro na operação";
    }
}