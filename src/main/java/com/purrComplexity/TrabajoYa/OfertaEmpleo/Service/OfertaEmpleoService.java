package com.purrComplexity.TrabajoYa.OfertaEmpleo.Service;

import com.purrComplexity.TrabajoYa.Contrato.Contrato;
import com.purrComplexity.TrabajoYa.Contrato.dto.ContratoDTO;
import com.purrComplexity.TrabajoYa.HorarioDia.HorarioDia;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.OfertaEmpleo;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.Repository.OfertaEmpleoRepository;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.CreateOfertaEmpleoDTO;
import com.purrComplexity.TrabajoYa.OfertaEmpleo.dto.OfertaEmpleoDTO;
import com.purrComplexity.TrabajoYa.Trabajador.Trabajador;
import com.purrComplexity.TrabajoYa.Trabajador.dto.TrabajadorDTO;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.exception.OfertaEmpleoNoPerteneceAlEmpleadorException;
import com.purrComplexity.TrabajoYa.exception.OfertaEmpleoNotFound;
import com.purrComplexity.TrabajoYa.exception.UsuarioNoEsEmpleadorException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public OfertaEmpleoDTO createOfertaEmpleo(CreateOfertaEmpleoDTO createOfertaEmpleoDTO) {

        OfertaEmpleo ofertaEmpleo = modelMapper.map(createOfertaEmpleoDTO, OfertaEmpleo.class);

        List<HorarioDia> horarios = createOfertaEmpleoDTO.getHorarioDias().stream().map(h -> {
            HorarioDia horario = new HorarioDia();
            horario.setWeekDays(h.getWeekDays());
            horario.setHoras(h.getHoras());
            horario.setOfertaEmpleo(ofertaEmpleo); // clave
            return horario;
        }).toList();

        ofertaEmpleo.setHorarioDias(horarios);

        OfertaEmpleo savedOfertaEmpleo = ofertaEmpleoRepository.save(ofertaEmpleo);

        return modelMapper.map(savedOfertaEmpleo, OfertaEmpleoDTO.class);
    }

    public void deleteOfertaEmpleo(Long id){
        OfertaEmpleo ofertaEmpleo=
                ofertaEmpleoRepository.findById(id).orElseThrow(()->new RuntimeException("No se encontró la oferta de empleo"));


        ofertaEmpleo.getPostulantes().forEach(t -> t.getPostulaste().remove(ofertaEmpleo));
        ofertaEmpleo.getContratados().forEach(t -> t.getContratado().remove(ofertaEmpleo));

        ofertaEmpleo.setEmpleador(null);

        ofertaEmpleoRepository.delete(ofertaEmpleo);
    }

    public OfertaEmpleoDTO updteOfertaEmpleo(OfertaEmpleo ofertaEmpleoRequestDTO, Long id){
        OfertaEmpleo ofertaEmpleo=
                ofertaEmpleoRepository.findById(id).orElseThrow(()->new RuntimeException("No e encontró la oferta de empleo"));

        modelMapper.map(ofertaEmpleoRequestDTO, ofertaEmpleo);

        OfertaEmpleo savedoOfertaEmpleo = ofertaEmpleoRepository.save(ofertaEmpleo);


        return modelMapper.map(ofertaEmpleo, OfertaEmpleoDTO.class);
    }

    public OfertaEmpleoDTO getOfertaEmpleoById(Long id){
        OfertaEmpleo ofertaEmpleo=
                ofertaEmpleoRepository.findById(id).orElseThrow(()->new RuntimeException("No e encontró la oferta de empleo"));

        return  modelMapper.map(ofertaEmpleo, OfertaEmpleoDTO.class);
    }

    public List<OfertaEmpleoDTO> getAllOfertaEmpleo(){

        List<OfertaEmpleo> ofertasEmpleos= ofertaEmpleoRepository.findAll();

        List<OfertaEmpleoDTO> ofertaEmpleoResponseDTOS=new ArrayList<>();

        for (OfertaEmpleo oferta : ofertasEmpleos) {
            OfertaEmpleoDTO dto = modelMapper.map(oferta, OfertaEmpleoDTO.class);
            ofertaEmpleoResponseDTOS.add(dto);
        }
        return ofertaEmpleoResponseDTOS;
    }
}
