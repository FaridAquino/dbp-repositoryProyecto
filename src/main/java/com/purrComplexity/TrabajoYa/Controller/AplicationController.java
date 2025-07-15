package com.purrComplexity.TrabajoYa.Controller;

import com.purrComplexity.TrabajoYa.CalificacionTrabajador.dto.CalificacionTrabajadorRequestDTO;
import com.purrComplexity.TrabajoYa.CalificacionTrabajador.dto.CalificacionTrabajadorResponseDTO;
import com.purrComplexity.TrabajoYa.Contrato.ContratoService;
import com.purrComplexity.TrabajoYa.Contrato.dto.ContratoDTO;
import com.purrComplexity.TrabajoYa.Empleador.dto.UpdateEmpleadorDTO;
import com.purrComplexity.TrabajoYa.Empleo.Service.EmpleoService;
import com.purrComplexity.TrabajoYa.Empleo.dto.EmpleoRequestDTO;
import com.purrComplexity.TrabajoYa.Empleo.dto.EmpleoResponseDTO;
import com.purrComplexity.TrabajoYa.CalificacionEmpresa.Service.CalificacionEmpresaService;
import com.purrComplexity.TrabajoYa.CalificacionEmpresa.dto.CalificacionEmpresaRequestDTO;
import com.purrComplexity.TrabajoYa.CalificacionEmpresa.dto.CalificacionEmpresaResponseDTO;
import com.purrComplexity.TrabajoYa.CalificacionTrabajador.Service.CalificacionTrabajadorService;
import com.purrComplexity.TrabajoYa.Empleador.Service.EmpleadorService;
import com.purrComplexity.TrabajoYa.Empleador.dto.EmpleadorRequestDTO;
import com.purrComplexity.TrabajoYa.Empleador.dto.EmpleadorResponseDTO;
import com.purrComplexity.TrabajoYa.Trabajador.TrabajadorServiceImpl;
import com.purrComplexity.TrabajoYa.Trabajador.dto.CreateTrabajadorDTO;
import com.purrComplexity.TrabajoYa.Trabajador.dto.TrabajadorDTO;
import com.purrComplexity.TrabajoYa.Service.AplicationService;
import com.purrComplexity.TrabajoYa.Trabajador.dto.UpdateTrabajadorDTO;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.User.UserAccountService;
import com.purrComplexity.TrabajoYa.User.dto.ProfileDTO;
import com.purrComplexity.TrabajoYa.email.EmailEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.Service.OfertaEmpleoService;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.OfertaEmpleoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AplicationController {

    private final EmpleadorService empleadorService;
    private final TrabajadorServiceImpl trabajadorServiceImpl;
    private final EmpleoService empleoService;
    private final ContratoService contratoService;
    private final CalificacionTrabajadorService calificacionTrabajadorService;
    private final CalificacionEmpresaService calificacionEmpresaService;
    private final ApplicationEventPublisher eventPublisher;
    private final AplicationService aplicationService;
    private final OfertaEmpleoService ofertaEmpleoService;
    private final UserAccountService userAccountService;

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/me/nombre")
    public ResponseEntity<String> getNombreUsuario(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        String nombre= userAccountService.getNombre(idUsuario);

        return new ResponseEntity<>(nombre,HttpStatus.OK);

    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/empleador")
    public ResponseEntity<EmpleadorResponseDTO> postEmpleador(@RequestBody EmpleadorRequestDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        EmpleadorResponseDTO resp = empleadorService.crearEmpleador(idUsuario,dto);

        if(resp == null) throw new IllegalArgumentException("El cuerpo no puede estar vacio");

        String email = resp.getCorreo();
        eventPublisher.publishEvent(new EmailEvent(email, "EMPLEADOR"));
        System.out.println("Evento email publicado para empleador: " + email);

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/trabajador")
    public ResponseEntity<TrabajadorDTO> postTrabajador(@RequestBody CreateTrabajadorDTO trabajadorDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        TrabajadorDTO createTrabajadorDTO = trabajadorServiceImpl.createPersona(idUsuario, trabajadorDTO);

        if (createTrabajadorDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Asumiendo que createPersonaDTO tiene el email
        String email = createTrabajadorDTO.getCorreo();

        // Publicar evento para enviar correo
        eventPublisher.publishEvent(new EmailEvent(email, "PERSONA"));
        System.out.println("Evento email publicado para persona: " + email);

        return new ResponseEntity<>(createTrabajadorDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @DeleteMapping("/eliminar/empleador/{ruc}")
    public ResponseEntity<String> deleteEmpleador(@PathVariable String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        empleadorService.eliminarEmpleador(idUsuario,id);

        return new ResponseEntity<>("Se borró la cuenta de empleador",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @DeleteMapping("/eliminar/trabajador/{id}")
    public ResponseEntity<String> deleteTrabajador(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        trabajadorServiceImpl.deleteTrabajador(idUsuario,id);

        return new ResponseEntity<>("Se borró la cuenta de Trabajador",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PatchMapping("/actualizar/trabajador")
    public ResponseEntity<TrabajadorDTO> patchTrabajador(@RequestBody UpdateTrabajadorDTO updateTrabajadorDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        TrabajadorDTO trabajadorDTO=trabajadorServiceImpl.updateTrabajador(idUsuario,updateTrabajadorDTO);

        return new ResponseEntity<>(trabajadorDTO,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PatchMapping("/actualizar/empleador/")
    public ResponseEntity<EmpleadorResponseDTO> patchEmpleador( @Valid @RequestBody UpdateEmpleadorDTO updateEmpleadorDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        EmpleadorResponseDTO empleadorResponseDTO=empleadorService.actualizarParcialmenteEmpleador(idUsuario,updateEmpleadorDTO);

        return new ResponseEntity<>(empleadorResponseDTO,HttpStatus.OK);

    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/crear/ofertaEmpleo")
    public ResponseEntity<EmpleoResponseDTO> postOfertaEmpleo(@RequestBody EmpleoRequestDTO empleoRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        EmpleoResponseDTO empleoAResponseDTO=empleoService.crearYAsignarEmpleo(empleoRequestDTO,idUsuario);

        return new ResponseEntity<>(empleoAResponseDTO,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/empleador/aceptar/{id_postulante}/{id_empleo}")
    public ResponseEntity<AceptadoDTO> aceptarEmpleador(@PathVariable Long id_postulante,@PathVariable Long id_empleo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        AceptadoDTO aceptadoDTO=aplicationService.aceptarTrabajador(idUsuario,id_empleo,id_postulante);

        return new ResponseEntity<>(aceptadoDTO,HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/empleador/rechazar/{id_postulante}/{id_empleo}")
    public ResponseEntity<String> rechazarEmpleador(@PathVariable Long id_empleo,@PathVariable Long id_postulante){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        String message=aplicationService.rechazarTrabajador(idUsuario,id_empleo,id_postulante);

        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PatchMapping("/actualizar/OfertaEmpleo/{id}") //
    public ResponseEntity<EmpleoResponseDTO> patchOfertaEmpleo(@RequestBody EmpleoRequestDTO empleoRequestDTO, @PathVariable Long id){
        EmpleoResponseDTO response = empleoService.actualizarParcialmenteEmpleo(id, empleoRequestDTO);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @DeleteMapping("/eliminar/OfertaEmpleo/{id}")
    public ResponseEntity<String> deleteOfertaEmpleo(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        empleoService.eliminarEmpleo(idUsuario,id);


        return new ResponseEntity<>("Se elimino correctamente",HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/contrato/crearnuevo/{id_oferta_empleo}/{id_trabajador}")
    public ResponseEntity<ContratoDTO> postContrato(
             @PathVariable Long id_trabajador,
            @PathVariable Long id_oferta_empleo) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        ContratoDTO contratoDTO = contratoService.createContrato(idUsuario,id_oferta_empleo,id_trabajador);

    URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(contratoDTO.getId())
                .toUri();

        return ResponseEntity.created(location).body(contratoDTO);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/calificar/Trabajador/{idContrato}")
    public ResponseEntity<CalificacionTrabajadorResponseDTO> postCalificacionTrabajador(@RequestBody CalificacionTrabajadorRequestDTO calificacionPersonaRequestDTO
            , @PathVariable Long idContrato){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        CalificacionTrabajadorResponseDTO calificacionPersonaResponseDTO = calificacionTrabajadorService.crearCalificacionTrabajador(idContrato, idUsuario, calificacionPersonaRequestDTO);

        return new ResponseEntity<>(calificacionPersonaResponseDTO,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/calificar/Empresa/{idContrato}")
    public ResponseEntity<CalificacionEmpresaResponseDTO> postCalificacionEmpleador(@RequestBody CalificacionEmpresaRequestDTO calificacionEmpresaRequestDTO
            , @PathVariable Long idContrato){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        CalificacionEmpresaResponseDTO calificacionEmpresaResponseDTO=calificacionEmpresaService.crearCalificacionEmpleador(idContrato,idUsuario,calificacionEmpresaRequestDTO);

        return new ResponseEntity<>(calificacionEmpresaResponseDTO,HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/mis/contratos")
    public ResponseEntity<List<ContratoDTO>> getContratosTrabajador(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        List<ContratoDTO> contratoDTOS=contratoService.getAllContratos(idUsuario);

        return new ResponseEntity<>(contratoDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("contratos/ofertaEmpleo/{id_OfertaEmpleo}")
    public ResponseEntity<List<ContratoDTO>> getContratosOfertaEmpleo(@PathVariable Long id_OfertaEmpleo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        List<ContratoDTO> contratoDTOS=ofertaEmpleoService.obtenerContratosOfertaEmpleo(idUsuario,id_OfertaEmpleo);

        return new ResponseEntity<>(contratoDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/trabajador")
    public ResponseEntity<TrabajadorDTO> getTrabajador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        TrabajadorDTO persona = trabajadorServiceImpl.getTrabajador(idUsuario);
        return ResponseEntity.ok(persona);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/empleador")
    public ResponseEntity<EmpleadorResponseDTO> getEmpleador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount userDetails = (UserAccount) authentication.getPrincipal();
        Long idUsuario = userDetails.getId();

        EmpleadorResponseDTO dto = empleadorService.obtenerEmpleador(idUsuario);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/ofertas-empleo")
    public ResponseEntity<Page<OfertaEmpleoResponseDTO>> getOfertasEmpleoPaginadas(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<OfertaEmpleoResponseDTO> page = ofertaEmpleoService.obtenerOfertasEmpleoPaginadas(pageable);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/ofertasEmpleo")
    public ResponseEntity<List<OfertaEmpleoResponseDTO>> getTodasOfertasEmpleo(){
        List<OfertaEmpleoResponseDTO> ofertaEmpleoResponseDTOS=ofertaEmpleoService.obtenerTodasOfertaEmpleo();

        return new ResponseEntity<>(ofertaEmpleoResponseDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/empleadores")
    public ResponseEntity<List<EmpleadorResponseDTO>> getTodosEmpleadorees(){
        List<EmpleadorResponseDTO> empleadorResponseDTOS=empleadorService.obtenerTodosEmpleador();

        return new ResponseEntity<>(empleadorResponseDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @PostMapping("/postular/{idOfertaEmpleo}")
    public ResponseEntity<String> postular(@PathVariable Long idOfertaEmpleo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        String message = aplicationService.postularEmpleo(idOfertaEmpleo, idUsuario);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/mis/ofertasEmpleos")
    public ResponseEntity<List<OfertaEmpleoResponseDTO>> getEmpleos(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        List<OfertaEmpleoResponseDTO> ofertaEmpleoResponseDTOS=aplicationService.obtenerEmpleos(idUsuario);

        return new ResponseEntity<>(ofertaEmpleoResponseDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/mis/postulaciones")
    public ResponseEntity<List<OfertaEmpleoResponseDTO>> getPostulaciones(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        List<OfertaEmpleoResponseDTO> ofertaEmpleoResponseDTOS=aplicationService.obtenerPostulaciones(idUsuario);

        return new ResponseEntity<>(ofertaEmpleoResponseDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/postulantes/ofertaEmpleo/{id}")
    public ResponseEntity<List<TrabajadorDTO>> getPostulantesOfertaEmpleo(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        List<TrabajadorDTO> trabajadorDTOS=ofertaEmpleoService.obtenerPosultantesOfertaEmpleo(idUsuario,id);

        return new ResponseEntity<>(trabajadorDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/aceptados/ofertaEmpleo/{id}")
    public ResponseEntity<List<TrabajadorDTO>> getContratadosOfertaEmpleo(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        List<TrabajadorDTO> trabajadorDTOS=ofertaEmpleoService.obtenerContratadosOfertaEmpleo(idUsuario,id);

        return new ResponseEntity<>(trabajadorDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/mis/postulantes/oferta/{id_ofertaEmpleo}")
    public ResponseEntity<List<TrabajadorDTO>> getPostulantesOferta(@PathVariable Long id_ofertaEmpleo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        List<TrabajadorDTO> trabajadorDTOS =aplicationService.obtenerPostulantes(idUsuario,id_ofertaEmpleo);

        return new ResponseEntity<>(trabajadorDTOS,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("/ofertas/cercanas/{radio_metros}")
    public ResponseEntity<List<OfertaEmpleoResponseDTO>> getOfertasCercanas(@PathVariable Double radio_metros){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        List<OfertaEmpleoResponseDTO> ofertaEmpleoResponseDTOS=aplicationService.getOfertasEmpleoCercanos(idUsuario,radio_metros);

        return new ResponseEntity<>(ofertaEmpleoResponseDTOS,HttpStatus.OK);

    }

    @PreAuthorize("hasRole('USUARIO')")
    @GetMapping("auth/profiles")
    public ResponseEntity<ProfileDTO> getProfiles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount detallesUser = (UserAccount) authentication.getPrincipal();
        Long idUsuario = detallesUser.getId();

        ProfileDTO profileDTO=aplicationService.getProfile(idUsuario);

        return new ResponseEntity<>(profileDTO,HttpStatus.OK);


    }

}
