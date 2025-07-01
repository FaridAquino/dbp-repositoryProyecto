package com.purrComplexity.TrabajoYa.EmpleoA.Service;

import com.purrComplexity.TrabajoYa.Empleador.Empleador;
import com.purrComplexity.TrabajoYa.Empleador.Repository.EmpleadorRepository;
import com.purrComplexity.TrabajoYa.EmpleoA.EmpleoA;
import com.purrComplexity.TrabajoYa.EmpleoA.Repository.EmpleoARepository;
import com.purrComplexity.TrabajoYa.EmpleoA.dto.EmpleoARequestDTO;
import com.purrComplexity.TrabajoYa.EmpleoA.dto.EmpleoAResponseDTO;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.exception.EmpleoANotFoundException;
import com.purrComplexity.TrabajoYa.exception.NoEmpleadorException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleoAService {
    private final EmpleoARepository empleoARepository;
    private final EmpleadorRepository empleadorRepository;
    private final ModelMapper modelMapper;
    private final UserAccountRepository userAccountRepository;

    public EmpleoAResponseDTO publicarEmpleoA(EmpleoARequestDTO empleoARequestDTO){
        EmpleoA empleoA=modelMapper.map(empleoARequestDTO,EmpleoA.class);

        empleoA =empleoARepository.save(empleoA);

        EmpleoAResponseDTO empleoAResponseDTO=modelMapper.map(empleoA,EmpleoAResponseDTO.class);

        return empleoAResponseDTO;
    }

    public void eliminarEmpleoA(Long id){
        EmpleoA empleoA=
                empleoARepository.findById(id).orElseThrow(()->new RuntimeException("No se encontró el Empleo de tipo A"));

        empleoARepository.delete(empleoA);
    }

    public EmpleoAResponseDTO actulizarEmpleoA(Long id, EmpleoARequestDTO empleoARequestDTO){
        EmpleoA empleoA=
                empleoARepository.findById(id).orElseThrow(()->new RuntimeException("No se encontró el Empleo de tipo A"));

        modelMapper.map(empleoARequestDTO, empleoA);

        empleoA = empleoARepository.save(empleoA);
        // por que tenemos empleoA = empleoARepository.save(empleoA);

        EmpleoAResponseDTO empleoAResponseDTO=modelMapper.map(empleoA,EmpleoAResponseDTO.class);

        return empleoAResponseDTO;

    }

    public EmpleoAResponseDTO actualizarEmpleoA_nuevo(Long id, EmpleoARequestDTO empleoARequestDTO){
        EmpleoA empleoA = empleoARepository.findById(id).orElseThrow(()->new RuntimeException("No se encontró el Empleo a actualizar."));
        modelMapper.map(empleoARequestDTO,empleoA);

        empleoA = empleoARepository.save(empleoA);
        EmpleoAResponseDTO empleoAResponseDTO = modelMapper.map(empleoA, EmpleoAResponseDTO.class);
        return empleoAResponseDTO;
    }

    public EmpleoAResponseDTO actualizarParcialmenteEmpleoA(Long id, EmpleoARequestDTO empleoARequestDTO){
        ModelMapper mapper=new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        EmpleoA empleoA=
                empleoARepository.findById(id).orElseThrow(()->new EmpleoANotFoundException(id));

        mapper.map(empleoARequestDTO,empleoA);

        empleoA=empleoARepository.save(empleoA);

        EmpleoAResponseDTO empleoAResponseDTO=modelMapper.map(empleoA,EmpleoAResponseDTO.class);

        return empleoAResponseDTO;
    }

    public EmpleoAResponseDTO obtenerEmpleoAPorId(Long id){
        EmpleoA empleoA=
                empleoARepository.findById(id).orElseThrow(()->new RuntimeException("No se encontró el Empleo de tipo A"));
        EmpleoAResponseDTO empleoAResponseDTO=modelMapper.map(empleoA,EmpleoAResponseDTO.class);

        return empleoAResponseDTO;
    }

    public List<EmpleoAResponseDTO> obtenerTodosEmpleoA(){
        List<EmpleoA> empleos=empleoARepository.findAll();

        List<EmpleoAResponseDTO> empleoAResponseDTOS=new ArrayList<>();

        for (EmpleoA empleoA:empleos){
            EmpleoAResponseDTO dto=modelMapper.map(empleoA,EmpleoAResponseDTO.class);
            empleoAResponseDTOS.add(dto);
        }

        return empleoAResponseDTOS;
    }

    public EmpleoAResponseDTO crearYAsignarEmpleoA(EmpleoARequestDTO empleoARequestDTO, Long idUsuario){

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()->new UsernameNotFoundException("El usuario no existe"));

        if (!userAccount.getIsEmpresario()){
            throw new NoEmpleadorException("El usuario no tiene asociado una cuenta empleador");
        }

        String id=userAccount.getEmpresario().getRuc();

        EmpleoA empleoA =modelMapper.map(empleoARequestDTO,EmpleoA.class);

        Empleador empleador=
                empleadorRepository.findById(id).orElseThrow(()->new RuntimeException("No existe un empresario con ese ruc"));

        empleoA.setEmpleador(empleador);

        empleoA=empleoARepository.save(empleoA);

        EmpleoAResponseDTO empleoAResponseDTO=modelMapper.map(empleoA,EmpleoAResponseDTO.class);

        empleoAResponseDTO.setRazonSocial(empleador.getRazonSocial());

        return empleoAResponseDTO;
    }

}
