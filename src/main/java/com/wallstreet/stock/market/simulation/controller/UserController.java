package com.wallstreet.stock.market.simulation.controller;

import com.wallstreet.stock.market.simulation.model.User;
import com.wallstreet.stock.market.simulation.repository.UserRepository;
import com.wallstreet.stock.market.simulation.dto.UserResponse;
import com.wallstreet.stock.market.simulation.dto.UserCreateRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid; 
import java.util.Optional;  
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
}
