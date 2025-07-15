package com.purrComplexity.TrabajoYa.Empleador.Service;

import com.purrComplexity.TrabajoYa.Empleador.Empleador;
import com.purrComplexity.TrabajoYa.Empleador.dto.CreateEmpleadorDTO;
import com.purrComplexity.TrabajoYa.Empleador.dto.EmpleadorDTO;
import com.purrComplexity.TrabajoYa.exception.*;
import com.purrComplexity.TrabajoYa.Empleador.Repository.EmpleadorRepository;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadorService {
    private final EmpleadorRepository empleadorRepository;
    private final ModelMapper modelMapper;
    private final UserAccountRepository userAccountRepository;

    public Empleador getEmpleadorByUserId(Long userId){
        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsEmpresario()){
            throw new UsuarioNoEsEmpleadorException();
        }

        return userAccount.getEmpresario();
    }

    public EmpleadorDTO createEmpleador(Long userId, CreateEmpleadorDTO createEmpleadorDTO) {

        if (empleadorRepository.existsByCorreo(createEmpleadorDTO.getCorreo())) {
            throw new EmpleadorWithTheSameCorreo();
        }
        if (empleadorRepository.existsById(createEmpleadorDTO.getRuc())) {
            throw new EmpleadorWithTheSameRUC();
        }

        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (userAccount.getIsEmpresario()){
            throw new UsuarioYaEsEmpleadorException();
        }

        userAccount.setIsEmpresario(true);

        Empleador empleador = modelMapper.map(createEmpleadorDTO, Empleador.class);
        Empleador savedEmpleador = empleadorRepository.save(empleador);

        userAccount.setEmpresario(empleador);

        userAccountRepository.save(userAccount);

        return modelMapper.map(savedEmpleador, EmpleadorDTO.class);
    }

    public void deleteEmpleador(Long idUser){

        Empleador empleador= getEmpleadorByUserId(idUser);

        UserAccount userAccount=empleador.getUsuario();

        userAccount.setEmpresario(null);
        userAccount.setIsEmpresario(false);

        empleadorRepository.delete(empleador);

    }

    public EmpleadorDTO updateEmpleador(EmpleadorDTO empleadorDTO, Long userId) {
        Empleador empleador=getEmpleadorByUserId(userId);

        modelMapper.map(empleadorDTO,empleadorRepository);

        Empleador savedEmpleador=empleadorRepository.save(empleador);

        return modelMapper.map(savedEmpleador,EmpleadorDTO.class);
    }

    public EmpleadorDTO getEmpleadorDtoById(Long userId){

        Empleador empleador=getEmpleadorByUserId(userId);

        return modelMapper.map(empleador,EmpleadorDTO.class);
    }

    public List<EmpleadorDTO> getAllEmpleador(){
        List<Empleador> empleadors=empleadorRepository.findAll();
        List<EmpleadorDTO> empleadorResponseDTOS=new ArrayList<>();

        for (Empleador empleador: empleadors){
            EmpleadorDTO dto=modelMapper.map(empleador,EmpleadorDTO.class);
            empleadorResponseDTOS.add(dto);
        }

        return empleadorResponseDTOS;
    }
}
