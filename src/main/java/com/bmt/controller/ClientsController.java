package com.bmt.controller;

import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.bmt.model.Client;
import com.bmt.model.ClientDto;
import com.bmt.repo.ClientRepo;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clients")
public class ClientsController {

    @Autowired
    private ClientRepo clientRepo;

    @GetMapping({"", "/"})
    public String getClients(Model model) {
        var clients = clientRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("clients", clients);
        return "clients/index";
    }

    @GetMapping("/create")
    public String createClient(Model model) {
        model.addAttribute("clientDto", new ClientDto());
        return "clients/create";
    }

    @PostMapping("/create")
    public String createClient(@Valid @ModelAttribute ClientDto clientDto, BindingResult result, Model model) {
        if (clientRepo.findByEmail(clientDto.getEmail()) != null) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email address is already used"));
        }

        if (result.hasErrors()) {
            model.addAttribute("clientDto", clientDto);
            return "clients/create";
        }

        Client client = mapDtoToClient(clientDto);
        client.setCreditAt(new Date(System.currentTimeMillis())); // Correct spelling
        clientRepo.save(client);

        return "redirect:/clients";
    }

    @GetMapping("/edit")
    public String editClient(Model model, @RequestParam int id) {
        Optional<Client> optionalClient = clientRepo.findById(id);
        if (optionalClient.isEmpty()) {
            return "redirect:/clients";
        }

        Client client = optionalClient.get();
        ClientDto clientDto = mapClientToDto(client);

        model.addAttribute("client", client);
        model.addAttribute("clientDto", clientDto);

        return "clients/edit";
    }

    @PostMapping("/edit")
    public String editClient(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ClientDto clientDto,
            BindingResult result) {

        Optional<Client> optionalClient = clientRepo.findById(id);
        if (optionalClient.isEmpty()) {
            return "redirect:/clients";
        }

        Client client = optionalClient.get();
        model.addAttribute("client", client);

        if (result.hasErrors()) {
            return "clients/edit";
        }

        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());

        try {
            clientRepo.save(client);
        } catch (Exception e) {
            result.addError(new FieldError("clientDto", "email", clientDto.getEmail(), false, null, null, "Email address is already used"));
            return "clients/edit";
        }

        return "redirect:/clients";
    }

    @GetMapping("/delete")
    public String deleteClient(@RequestParam int id) {
        clientRepo.findById(id).ifPresent(clientRepo::delete);
        return "redirect:/clients";
    }

    // Utility Method to Map ClientDto to Client
    private Client mapDtoToClient(ClientDto clientDto) {
        Client client = new Client();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());
        client.setStatus(clientDto.getStatus());
        return client;
    }

    // Utility Method to Map Client to ClientDto
    private ClientDto mapClientToDto(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName(client.getFirstName());
        clientDto.setLastName(client.getLastName());
        clientDto.setEmail(client.getEmail());
        clientDto.setPhone(client.getPhone());
        clientDto.setAddress(client.getAddress());
        clientDto.setStatus(client.getStatus());
        return clientDto;
    }
}
