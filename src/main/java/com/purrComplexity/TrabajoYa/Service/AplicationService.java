package com.purrComplexity.TrabajoYa.Service;

import com.purrComplexity.TrabajoYa.Empleador.Empleador;
import com.purrComplexity.TrabajoYa.Empleador.Repository.EmpleadorRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.Repository.OfertaEmpleoRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.OfertaEmpleoResponseDTO;
import com.purrComplexity.TrabajoYa.Trabajador.Trabajador;
import com.purrComplexity.TrabajoYa.Trabajador.TrabajadorRepository;
import com.purrComplexity.TrabajoYa.Trabajador.dto.TrabajadorDTO;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.User.dto.ProfileDTO;
import com.purrComplexity.TrabajoYa.exception.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AplicationService {

    private final OfertaEmpleoRepository ofertaEmpleoRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EmpleadorRepository empleadorRepository;
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    public AceptadoDTO aceptarTrabajador(Long idUsuario,Long idEmpleo, Long idPostulante){
        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(idEmpleo).orElseThrow(EmpleadorNotFound::new);
        Trabajador trabajador= trabajadorRepository.findById(idPostulante).orElseThrow(TrabajadorNotFound::new);

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()-> new RuntimeException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        Empleador empleador=userAccount.getEmpresario();

        List<Trabajador> postulantes=ofertaEmpleo.getPostulantes();

        if (!Objects.equals(ofertaEmpleo.getEmpleador().getRuc(), empleador.getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        boolean esPostulante = postulantes.stream()
                .anyMatch(p -> p.getId().equals(idPostulante));

        if (!esPostulante) {
            throw new PostulanteNoEncontradoEnOfertaException();
        }

        trabajador.getContratado().add(ofertaEmpleo);
        trabajador.getPostulaste().remove(ofertaEmpleo);

        trabajadorRepository.save(trabajador);

        AceptadoDTO aceptadoDTO= new AceptadoDTO();

        aceptadoDTO.setNombreCompletoEmpleado(trabajador.getNombresCompletos());
        aceptadoDTO.setIdEmpleo(idEmpleo);
        aceptadoDTO.setLatitud(ofertaEmpleo.getLatitud());
        aceptadoDTO.setLongitud(ofertaEmpleo.getLongitud());
        aceptadoDTO.setCorreoEmpleador(empleador.getCorreo());

        return aceptadoDTO;
    }

    public String rechazarTrabajador(Long idUsuario,Long idEmpleo,Long idPostulante){

        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(idEmpleo).orElseThrow(OfertaEmpleoNotFound::new);
        Trabajador trabajador= trabajadorRepository.findById(idPostulante).orElseThrow(TrabajadorNotFound::new);

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()-> new RuntimeException("No existe el usuario"));


        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsEmpleadorException();
        }

        Empleador empleador=userAccount.getEmpresario();

        List<Trabajador> postulantes=ofertaEmpleo.getPostulantes();

        if (!Objects.equals(ofertaEmpleo.getEmpleador().getRuc(), empleador.getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        boolean esPostulante = postulantes.stream()
                .anyMatch(p -> p.getId().equals(idPostulante));

        if (!esPostulante) {
            throw new PostulanteNoEncontradoEnOfertaException();
        }

        trabajador.getPostulaste().remove(ofertaEmpleo);

        trabajadorRepository.save(trabajador);

        return "Postulante rechazado con éxito";
    }

    public String postularEmpleo(Long idOferta, Long idUsuario) {

        OfertaEmpleo ofertaEmpleo = ofertaEmpleoRepository.findById(idOferta).
                orElseThrow(OfertaEmpleoNotFound::new);
        UserAccount userAccount = userAccountRepository.findById(idUsuario).orElseThrow(() -> new RuntimeException("No existe el usuario"));
        Trabajador trabajador = userAccount.getTrabajador();

        // verificar si la persona ya postuló a esta oferta
        if (trabajador.getPostulaste().stream().anyMatch(oferta -> oferta.getIdOfertaEmpleo().equals(idOferta))) {
            throw new PostulacionDuplicadaException();
        }

        // Agregar la oferta de empleo a la lista de postulaciones de la persona
        // guardar la
        trabajador.getPostulaste().add(ofertaEmpleo);

        trabajadorRepository.save(trabajador);

        return "Postulación exitosa para la oferta de empleo: " + ofertaEmpleo.getIdOfertaEmpleo();
    }

    public List<OfertaEmpleoResponseDTO> obtenerEmpleos(Long userID) {
        UserAccount userAccount = userAccountRepository.findById(userID)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()) {
            return Collections.emptyList();
        }

        Empleador empleador = userAccount.getEmpresario();

        return empleador.getOfertas().stream()
                .map(oferta -> modelMapper.map(oferta, OfertaEmpleoResponseDTO.class))
                .collect(Collectors.toList());
    }


    public List<OfertaEmpleoResponseDTO> obtenerPostulaciones(Long userID){
        UserAccount userAccount= userAccountRepository.findById(userID).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador=userAccount.getTrabajador();

        List<OfertaEmpleoResponseDTO> ofertaEmpleoResponseDTOS = new ArrayList<>();

        for (OfertaEmpleo oferta : trabajador.getPostulaste()) {
            OfertaEmpleoResponseDTO dto = modelMapper.map(oferta, OfertaEmpleoResponseDTO.class);
            ofertaEmpleoResponseDTOS.add(dto);
        }

        return ofertaEmpleoResponseDTOS;

    }

    public List<TrabajadorDTO> obtenerPostulantes(Long userID, Long ofertaEmpleoID){
        UserAccount userAccount=userAccountRepository.findById(userID).orElseThrow(()->new RuntimeException("No existe el usuario"));
        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(ofertaEmpleoID).orElseThrow(OfertaEmpleoNotFound::new);

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        if (!userAccount.getEmpresario().getRuc().equals(ofertaEmpleo.getEmpleador().getRuc())) {
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        List<TrabajadorDTO> trabajadorDTOS =new ArrayList<>();

        List<Trabajador> trabajadores=ofertaEmpleo.getPostulantes();

        for (Trabajador postulante:trabajadores){
            TrabajadorDTO trabajadorDTO =modelMapper.map(postulante, TrabajadorDTO.class);
            trabajadorDTOS.add(trabajadorDTO);
        }

        return trabajadorDTOS;
    }

    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // radio de la Tierra en metros

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // distancia en metros
    }

    public List<OfertaEmpleoResponseDTO> getOfertasEmpleoCercanos(Long idUsuario, Double radioEnMetros) {

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador=userAccount.getTrabajador();

        Double lat=trabajador.getLatitud();
        Double lng=trabajador.getLongitud();

        return ofertaEmpleoRepository.findAll().stream()
                .filter(ofertaEmpleo -> {
                    double dist = calcularDistancia(lat, lng,
                            ofertaEmpleo.getLatitud(), // latitude
                            ofertaEmpleo.getLongitud()); // longitude
                    return dist <= radioEnMetros;
                }).map(ofertaEmpleo -> modelMapper.map(ofertaEmpleo, OfertaEmpleoResponseDTO.class))
                .collect(Collectors.toList());
    }

    public ProfileDTO getProfile(Long userID){
        UserAccount userAccount=userAccountRepository.findById(userID).orElseThrow(()-> new UsernameNotFoundException("No se encontró al usuario"));

        ProfileDTO profileDTO=new ProfileDTO();

        if (userAccount.getIsEmpresario() && userAccount.getIsTrabajador()){
            profileDTO.setHasEmpleadorProfile(true);
            profileDTO.setHasTrabajadorProfile(true);
            profileDTO.setEmpleadorRuc(userAccount.getEmpresario().getRuc());
            profileDTO.setEmpleadorRazonSocial(userAccount.getEmpresario().getRazonSocial());
            profileDTO.setTrabajadorId(userAccount.getTrabajador().getId());
            profileDTO.setTrabajadorNombres(userAccount.getTrabajador().getNombresCompletos());
            return profileDTO;
        }

        else{
            if (userAccount.getIsTrabajador()){
                profileDTO.setHasTrabajadorProfile(true);
                profileDTO.setHasEmpleadorProfile(false);
                profileDTO.setTrabajadorId(userAccount.getTrabajador().getId());
                profileDTO.setTrabajadorNombres(userAccount.getTrabajador().getNombresCompletos());

                return profileDTO;
            }

            if(userAccount.getIsEmpresario()){
                profileDTO.setHasEmpleadorProfile(true);
                profileDTO.setHasTrabajadorProfile(false);
                profileDTO.setEmpleadorRuc(userAccount.getEmpresario().getRuc());
                profileDTO.setEmpleadorRazonSocial(userAccount.getEmpresario().getRazonSocial());

                return profileDTO;
            }

            profileDTO.setHasEmpleadorProfile(false);
            profileDTO.setHasTrabajadorProfile(false);

        }

        return profileDTO;

    }
}
