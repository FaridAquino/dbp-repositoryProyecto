package com.purrComplexity.TrabajoYa.Contrato;

import com.purrComplexity.TrabajoYa.Contrato.dto.ContratoDTO;
import com.purrComplexity.TrabajoYa.Contrato.dto.UpdateContratoDTO;
import com.purrComplexity.TrabajoYa.Contrato.mapper.ContratoMapper;
import com.purrComplexity.TrabajoYa.Empleador.Empleador;
import com.purrComplexity.TrabajoYa.Empleador.Repository.EmpleadorRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.Repository.OfertaEmpleoRepository;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.exception.*;
import com.purrComplexity.TrabajoYa.Trabajador.Trabajador;
import com.purrComplexity.TrabajoYa.Trabajador.TrabajadorRepository;
import com.purrComplexity.TrabajoYa.email.EmailService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContratoServiceImpl implements ContratoService {
    private final ContratoRepository contratoRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final ModelMapper modelMapper;
    private final OfertaEmpleoRepository ofertaEmpleoRepository;
    private final ContratoMapper contratoMapper;
    private final EmailService emailService;
    private final UserAccountRepository userAccountRepository;
    private final EmpleadorRepository empleadorRepository;

    @Override
    public ContratoDTO createContrato(Long idUsuario,Long idOfertaEmpleo, Long idTrabajador) {

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()->new UsernameNotFoundException("El usuario no existe"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        Trabajador trabajador=trabajadorRepository.findById(idTrabajador).orElseThrow(TrabajadorNotFound::new);

        Empleador empleador=userAccount.getEmpresario();

        OfertaEmpleo oferta = ofertaEmpleoRepository.findById(idOfertaEmpleo)
                .orElseThrow(OfertaEmpleoNotFound::new);


        if (!userAccount.getEmpresario().getRuc().equals(oferta.getEmpleador().getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        if (!oferta.getContratados().contains(trabajador)) {
            throw new TrabajadorNoContratadoException();
        }



        Contrato contrato=new Contrato();

        contrato.setFechaCreacion(new Date());

        contrato.setTrabajadorContratado(trabajador);
        contrato.setOfertaEmpleo(oferta);

        Contrato savedContrato = contratoRepository.save(contrato);

        String mensajeEmpleador = """
Estimado empleador,

Nos complace informarle que se ha creado un nuevo contrato en la plataforma TrabajoYa.

Por favor, revise los detalles y confirme con la persona contratada para asegurar el correcto inicio de la relación laboral.

Quedamos a su disposición para cualquier consulta o apoyo adicional.

Atentamente,
Equipo TrabajoYa
""";

        emailService.sendSimpleMessage(empleador.getCorreo(), "Notificación de nuevo contrato en TrabajoYa", mensajeEmpleador);

        String mensajePersona = """
Estimado/a,

Le informamos que ha sido seleccionado/a para un nuevo contrato a través de TrabajoYa.

Le recomendamos revisar la oferta y comunicarse con el empleador para coordinar los detalles.

Gracias por confiar en nosotros.

Atentamente,
Equipo TrabajoYa
""";

        emailService.sendSimpleMessage(trabajador.getCorreo(), "Confirmación de nuevo contrato en TrabajoYa", mensajePersona);

        ContratoDTO contratoDTO= new ContratoDTO();

        contratoDTO.setFechaCreacion(contrato.getFechaCreacion());
        contratoDTO.setOfertaEmpleoId(contrato.getOfertaEmpleo().getIdOfertaEmpleo());
        contratoDTO.setPersonaContratadaId(contrato.getTrabajadorContratado().getId());
        contratoDTO.setPromedioCalificaciones(contrato.getCalificacionPromedio());

        trabajador.getContratado().remove(oferta);

        trabajadorRepository.save(trabajador);

        return contratoDTO;
    }

    @Override
    public ContratoDTO getContratoById(Long id) {
        Contrato contrato = contratoRepository.findById(id)
            .orElseThrow(ContratoNotFound::new);
        return contratoMapper.toDTO(contrato);
    }
    
    @Override
    public List<ContratoDTO> getAllContratos(Long userId) {
        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador=userAccount.getTrabajador();

        List<Contrato> contratos=trabajador.getContratos();

        List<ContratoDTO> contratoDTOS=new ArrayList<>();

        for (Contrato a:contratos){
            ContratoDTO contratoDTO=new ContratoDTO();
            contratoDTO.setId(a.getId());
            contratoDTO.setFechaCreacion(a.getFechaCreacion());
            contratoDTO.setOfertaEmpleoId(a.getOfertaEmpleo().getIdOfertaEmpleo());
            contratoDTO.setPersonaContratadaId(a.getTrabajadorContratado().getId());
            contratoDTO.setPromedioCalificaciones(a.getCalificacionPromedio());

            contratoDTOS.add(contratoDTO);
        }

        return contratoDTOS;
    }
    
    @Override
    public ContratoDTO updateContrato(Long id, UpdateContratoDTO updateContratoDTO) {
        Contrato contrato = contratoRepository.findById(id)
            .orElseThrow(ContratoNotFound::new);
            
        Trabajador trabajador = null;
        if (updateContratoDTO.getPersonaContratadaId() != null) {
            trabajador = trabajadorRepository.findById(updateContratoDTO.getPersonaContratadaId())
                .orElseThrow(TrabajadorNotFound::new);
        }
        
        contratoMapper.updateEntityFromDTO(updateContratoDTO, contrato, trabajador);
        Contrato updatedContrato = contratoRepository.save(contrato);
        return contratoMapper.toDTO(updatedContrato);
    }
    
    @Override
    public void deleteContrato(Long id) {
        if (!contratoRepository.existsById(id)) {
            throw new ContratoNotFound();
        }
        contratoRepository.deleteById(id);
    }
    
    @Override
    public List<ContratoDTO> getContratosByTrabajadorId(Long trabajadorId) {
        if (!trabajadorRepository.existsById(trabajadorId)) {
            throw new TrabajadorNotFound();
        }
        return contratoRepository.findByTrabajadorContratadoId(trabajadorId).stream()
            .map(contratoMapper::toDTO)
            .collect(Collectors.toList());
    }
}

