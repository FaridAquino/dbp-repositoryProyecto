package com.purrComplexity.TrabajoYa.Service;

import com.purrComplexity.TrabajoYa.Contrato.Contrato;
import com.purrComplexity.TrabajoYa.Contrato.dto.ContratoDTO;
import com.purrComplexity.TrabajoYa.DTO.AceptadoDTO;
import com.purrComplexity.TrabajoYa.Empleador.Empleador;
import com.purrComplexity.TrabajoYa.Empleador.Repository.EmpleadorRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.Repository.OfertaEmpleoRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.OfertaEmpleoDTO;
import com.purrComplexity.TrabajoYa.Trabajador.Trabajador;
import com.purrComplexity.TrabajoYa.Trabajador.TrabajadorRepository;
import com.purrComplexity.TrabajoYa.Trabajador.dto.TrabajadorDTO;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.DTO.ProfileDTO;
import com.purrComplexity.TrabajoYa.exception.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public String postularOfertaEmpleo(Long ofertaEmpleoId, Long userId) {

        OfertaEmpleo ofertaEmpleo = ofertaEmpleoRepository.findById(ofertaEmpleoId).
                orElseThrow(OfertaEmpleoNotFound::new);

        UserAccount userAccount = userAccountRepository.findById(userId).orElseThrow(() -> new RuntimeException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador = userAccount.getTrabajador();

        if (trabajador.getPostulaste().stream().anyMatch(oferta -> oferta.getIdOfertaEmpleo().equals(ofertaEmpleoId))) {
            throw new PostulacionDuplicadaException();
        }

        int maxPostulaciones = ofertaEmpleo.getNumeroPostulaciones() != null
                ? ofertaEmpleo.getNumeroPostulaciones().intValue()
                : Integer.MAX_VALUE;

        int contratadosActuales = ofertaEmpleo.getContratados().size();

        if (contratadosActuales >= maxPostulaciones) {
            throw new NumeroPostulacionesMaximaAlcanzado();
        }

        trabajador.getPostulaste().add(ofertaEmpleo);

        trabajadorRepository.save(trabajador);

        return "Postulación exitosa para la oferta de empleo: " + ofertaEmpleo.getIdOfertaEmpleo();
    }

    public AceptadoDTO aceptarTrabajador(Long userId, Long ofertaEmpleoId, Long trabjadorId){
        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(EmpleadorNotFound::new);
        Trabajador trabajador= trabajadorRepository.findById(trabjadorId).orElseThrow(TrabajadorNotFound::new);

        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()-> new RuntimeException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        Empleador empleador=userAccount.getEmpresario();

        List<Trabajador> postulantes=ofertaEmpleo.getPostulantes();

        if (!Objects.equals(ofertaEmpleo.getEmpleador().getRuc(), empleador.getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        boolean esPostulante = postulantes.stream()
                .anyMatch(p -> p.getId().equals(trabjadorId));

        if (!esPostulante) {
            throw new PostulanteNoEncontradoEnOfertaException();
        }

        int maxPostulaciones = ofertaEmpleo.getNumeroPostulaciones();

        int contratadosActuales = ofertaEmpleo.getContratados().size();

        if (contratadosActuales >= maxPostulaciones) {
            throw new NumeroPostulacionesMaximaAlcanzado();
        }

        trabajador.getContratado().add(ofertaEmpleo);
        trabajador.getPostulaste().remove(ofertaEmpleo);

        trabajadorRepository.save(trabajador);

        AceptadoDTO aceptadoDTO= new AceptadoDTO();

        aceptadoDTO.setTrabajadorNombreCompleto(trabajador.getNombresCompletos());
        aceptadoDTO.setOfertaEmpleoId(ofertaEmpleoId);
        aceptadoDTO.setOfertaEmpleoLatitud(ofertaEmpleo.getLatitud());
        aceptadoDTO.setOfertaEmpleoLongitud(ofertaEmpleo.getLongitud());
        aceptadoDTO.setEmpleadorCorreo(empleador.getCorreo());

        return aceptadoDTO;
    }

    public String rechazarTrabajador(Long userId,Long ofertaEmpleoId,Long trabajadrId){

        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        Trabajador trabajador= trabajadorRepository.findById(trabajadrId).orElseThrow(TrabajadorNotFound::new);

        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()-> new RuntimeException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsEmpleadorException();
        }

        Empleador empleador=userAccount.getEmpresario();

        List<Trabajador> postulantes=ofertaEmpleo.getPostulantes();

        if (!Objects.equals(ofertaEmpleo.getEmpleador().getRuc(), empleador.getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        boolean esPostulante = postulantes.stream()
                .anyMatch(p -> p.getId().equals(trabajadrId));

        if (!esPostulante) {
            throw new PostulanteNoEncontradoEnOfertaException();
        }

        trabajador.getPostulaste().remove(ofertaEmpleo);

        trabajadorRepository.save(trabajador);

        return "Postulante rechazado con éxito";
    }

    public List<OfertaEmpleoDTO> obtenerMisPostulaciones(Long userID){
        UserAccount userAccount= userAccountRepository.findById(userID).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador=userAccount.getTrabajador();

        List<OfertaEmpleoDTO> ofertaEmpleoResponseDTOS = new ArrayList<>();

        for (OfertaEmpleo oferta : trabajador.getPostulaste()) {
            OfertaEmpleoDTO dto = modelMapper.map(oferta, OfertaEmpleoDTO.class);
            ofertaEmpleoResponseDTOS.add(dto);
        }

        return ofertaEmpleoResponseDTOS;

    }

    public List<TrabajadorDTO> obtenerPostulantes(Long userId, Long ofertaEmpleoId){
        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new RuntimeException("No existe el usuario"));
        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        if (!userAccount.getEmpresario().getRuc().equals(ofertaEmpleo.getEmpleador().getRuc())) {
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        List<TrabajadorDTO> trabajadorDTOS =new ArrayList<>();

        for (Trabajador postulante:ofertaEmpleo.getPostulantes()){
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

    public List<OfertaEmpleoDTO> getOfertasEmpleoCercanos(Long idUsuario, Double radioEnMetros) {

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
                }).map(ofertaEmpleo -> modelMapper.map(ofertaEmpleo, OfertaEmpleoDTO.class))
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

    public List<ContratoDTO> getAllContratosOfertaEmpleo(Long userId, Long ofertaEmpleoId){
        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        if (!userAccount.getEmpresario().getRuc().equals(ofertaEmpleo.getEmpleador().getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        List<Contrato> contratoes=ofertaEmpleo.getContratos();

        List<ContratoDTO> contratoDTOS=new ArrayList<>();

        for (Contrato c: contratoes){
            ContratoDTO contratoDTO=modelMapper.map(c,ContratoDTO.class);
            contratoDTOS.add(contratoDTO);
        }

        return contratoDTOS;
    }

    public List<OfertaEmpleoDTO> getMisOfertasEmplo(Long userId){
        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        Empleador empleador=userAccount.getEmpresario();

        List<OfertaEmpleo> ofertaEmpleos=empleador.getOfertas();

        List<OfertaEmpleoDTO> ofertaEmpleoDTOS=new ArrayList<>();

        for (OfertaEmpleo o: ofertaEmpleos){
            OfertaEmpleoDTO ofertaEmpleoDTO=modelMapper.map(o,OfertaEmpleoDTO.class);
            ofertaEmpleoDTOS.add(ofertaEmpleoDTO);
        }

        return ofertaEmpleoDTOS;
    }

    public List<TrabajadorDTO> getContratadosOfertaEmplo(Long userId, Long ofertaEmpleoId){
        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        OfertaEmpleo ofertaEmpleo=ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        if (!ofertaEmpleo.getEmpleador().getRuc().equals(userAccount.getEmpresario().getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        List<Trabajador> trabajadors=ofertaEmpleo.getContratados();

        List<TrabajadorDTO> trabajadorDTOS=new ArrayList<>();

        for (Trabajador t:trabajadors){
            TrabajadorDTO trabajadorDTO=modelMapper.map(t,TrabajadorDTO.class);
            trabajadorDTOS.add(trabajadorDTO);
        }

        return trabajadorDTOS;
    }
}
