package com.purrComplexity.TrabajoYa.OfertaEmpleo.Service;

import com.purrComplexity.TrabajoYa.HorarioDia.HorarioDia;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.Repository.OfertaEmpleoRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.CreateOfertaEmpleoDTO;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.OfertaEmpleoDTO;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.UpdateOfertaEmpleoDTO;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.exception.OfertaEmpleoNoPerteneceAlEmpleadorException;
import com.purrComplexity.TrabajoYa.exception.OfertaEmpleoNotFound;
import com.purrComplexity.TrabajoYa.exception.UsuarioNoEsEmpleadorException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfertaEmpleoService {
    private final OfertaEmpleoRepository ofertaEmpleoRepository;
    private final ModelMapper modelMapper;
    private final UserAccountRepository userAccountRepository;

    public OfertaEmpleoDTO createOfertaEmpleo(CreateOfertaEmpleoDTO createOfertaEmpleoDTO, Long userId) {

        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        OfertaEmpleo ofertaEmpleo = modelMapper.map(createOfertaEmpleoDTO, OfertaEmpleo.class);

        List<HorarioDia> horarios = createOfertaEmpleoDTO.getHorarioDias().stream().map(h -> {
            HorarioDia horario = new HorarioDia();
            horario.setWeekDays(h.getWeekDays());
            horario.setHoras(h.getHoras());
            horario.setOfertaEmpleo(ofertaEmpleo); // clave
            return horario;
        }).toList();

        ofertaEmpleo.setHorarioDias(horarios);

        ofertaEmpleo.setEmpleador(userAccount.getEmpresario());

        OfertaEmpleo savedOfertaEmpleo = ofertaEmpleoRepository.save(ofertaEmpleo);

        OfertaEmpleoDTO ofertaEmpleoDTO=modelMapper.map(savedOfertaEmpleo, OfertaEmpleoDTO.class);

        ofertaEmpleoDTO.setRazonSocial(userAccount.getEmpresario().getRazonSocial());

        return ofertaEmpleoDTO;
    }

    public void deleteOfertaEmpleo(Long userId,Long ofertaEmpleoId){

        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        OfertaEmpleo ofertaEmpleo=
                ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        if (!ofertaEmpleo.getEmpleador().getRuc().equals(userAccount.getEmpresario().getRuc())){
            throw new OfertaEmpleoNoPerteneceAlEmpleadorException();
        }

        ofertaEmpleo.getPostulantes().forEach(t -> t.getPostulaste().remove(ofertaEmpleo));
        ofertaEmpleo.getContratados().forEach(t -> t.getContratado().remove(ofertaEmpleo));

        ofertaEmpleo.setEmpleador(null);

        ofertaEmpleoRepository.delete(ofertaEmpleo);
    }

    public OfertaEmpleoDTO updteOfertaEmpleo(UpdateOfertaEmpleoDTO ofertaEmpleoRequestDTO, Long ofertaEmpleoId){

        OfertaEmpleo ofertaEmpleo=
                ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        modelMapper.map(ofertaEmpleoRequestDTO, ofertaEmpleo);

        List<HorarioDia> horarios = ofertaEmpleoRequestDTO.getHorarioDias().stream().map(h -> {
            HorarioDia horario = new HorarioDia();
            horario.setWeekDays(h.getWeekDays());
            horario.setHoras(h.getHoras());
            horario.setOfertaEmpleo(ofertaEmpleo); // clave
            return horario;
        }).toList();

        OfertaEmpleo savedoOfertaEmpleo = ofertaEmpleoRepository.save(ofertaEmpleo);

        OfertaEmpleoDTO ofertaEmpleoDTO=modelMapper.map(savedoOfertaEmpleo, OfertaEmpleoDTO.class);
        ofertaEmpleoDTO.setRazonSocial(savedoOfertaEmpleo.getEmpleador().getRazonSocial());

        return ofertaEmpleoDTO;
    }

    public OfertaEmpleoDTO getOfertaEmpleoById(Long ofertaEmpleoId){
        OfertaEmpleo ofertaEmpleo=
                ofertaEmpleoRepository.findById(ofertaEmpleoId).orElseThrow(OfertaEmpleoNotFound::new);

        OfertaEmpleoDTO ofertaEmpleoDTO= modelMapper.map(ofertaEmpleo, OfertaEmpleoDTO.class);
        ofertaEmpleoDTO.setRazonSocial(ofertaEmpleo.getEmpleador().getRazonSocial());

        return ofertaEmpleoDTO;
    }

    public List<OfertaEmpleoDTO> getAllOfertaEmpleo(){

        List<OfertaEmpleo> ofertasEmpleos= ofertaEmpleoRepository.findAll();

        List<OfertaEmpleoDTO> ofertaEmpleoResponseDTOS=new ArrayList<>();

        for (OfertaEmpleo oferta : ofertasEmpleos) {
            OfertaEmpleoDTO dto = modelMapper.map(oferta, OfertaEmpleoDTO.class);
            dto.setRazonSocial(oferta.getEmpleador().getRazonSocial());
            ofertaEmpleoResponseDTOS.add(dto);
        }
        return ofertaEmpleoResponseDTOS;
    }
}
