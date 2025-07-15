package com.purrComplexity.TrabajoYa.Trabajador;

import com.purrComplexity.TrabajoYa.Trabajador.dto.CreateTrabajadorDTO;
import com.purrComplexity.TrabajoYa.Trabajador.dto.TrabajadorDTO;
import com.purrComplexity.TrabajoYa.Trabajador.dto.UpdateTrabajadorDTO;
import com.purrComplexity.TrabajoYa.User.Repository.UserAccountRepository;
import com.purrComplexity.TrabajoYa.User.UserAccount;
import com.purrComplexity.TrabajoYa.exception.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrabajadorService {

    private final TrabajadorRepository trabajadorRepository;
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    public TrabajadorDTO createTrabajador(Long idUsuario, CreateTrabajadorDTO dto) {

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (userAccount.getIsTrabajador()){
            throw new UsuarioYaEsTrabajadorException();
        }

        Trabajador trabajador = modelMapper.map(dto, Trabajador.class);

        if (trabajadorRepository.existsByCorreo(dto.getCorreo())){
            throw new TrabajadorWithSameCorreo();
        }

        userAccount.setIsTrabajador(true);

        Trabajador savedtrabajador=trabajadorRepository.save(trabajador);

        userAccount.setTrabajador(trabajador);

        userAccountRepository.save(userAccount);

        return modelMapper.map(savedtrabajador, TrabajadorDTO.class);
    }

    public TrabajadorDTO getTrabajadorById(Long idUsuario) {
        UserAccount userAccount = userAccountRepository.findById(idUsuario).orElseThrow(() -> new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador = userAccount.getTrabajador();

        return modelMapper.map(trabajador, TrabajadorDTO.class);
    }

    public List<TrabajadorDTO> getAllTrabajador() {

        List<TrabajadorDTO> trabajadorDTOS=new ArrayList<>();

        List<Trabajador> trabajadors=trabajadorRepository.findAll();

        for (Trabajador t:trabajadors){
            TrabajadorDTO trabajadorDTO=modelMapper.map(t,TrabajadorDTO.class);
            trabajadorDTOS.add(trabajadorDTO);
        }

        return trabajadorDTOS;
    }

    public TrabajadorDTO updateTrabajador(Long idUsuario, UpdateTrabajadorDTO updateTrabajadorDTO) {

        UserAccount userAccount=userAccountRepository.findById(idUsuario).orElseThrow(()->new UsernameNotFoundException("No existe el usuario"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador = userAccount.getTrabajador();

        modelMapper.map(updateTrabajadorDTO,trabajador);

        Trabajador savedTrabajador=trabajadorRepository.save(trabajador);

        return modelMapper.map(savedTrabajador,TrabajadorDTO.class);
    }

    public void deleteTrabajador(Long userId) {

        UserAccount userAccount=userAccountRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("El usuario no existe"));

        if (!userAccount.getIsTrabajador()){
            throw new UsuarioNoEsTrabajadorException();
        }

        Trabajador trabajador = userAccount.getTrabajador();

        userAccount.setTrabajador(null);

        userAccount.setIsTrabajador(false);

        trabajadorRepository.delete(trabajador);
    }


}

