package org.elsys.health_tracker.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.elsys.health_tracker.controller.resources.UserResource;
import org.elsys.health_tracker.entity.User;
import org.elsys.health_tracker.exception.DuplicateEntityFieldException;
import org.elsys.health_tracker.repository.UserRepository;
import org.elsys.health_tracker.service.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.elsys.health_tracker.mapper.UserMapper.USER_MAPPER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final MealStatService mealStatService;
    private final SleepStatService sleepStatService;
    private final WorkoutStatService workoutStatService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @Override
    public List<UserResource> getAll() {
        return USER_MAPPER.toUserResources(userRepository.findAll());
    }

    @Override
    public UserResource getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unable to find user with id " + id + "."));

        return USER_MAPPER.toUserResource(user);
    }

    @Override
    public UserResource update(UserResource userResource, Long id) {
       try {
           User user = userRepository.getReferenceById(id);

           // TODO: I know what I have to do.
           user.setUsername(userResource.getUsername());

           if (!passwordEncoder.matches(userResource.getPassword(), user.getPassword())) {
               user.setPassword(passwordEncoder.encode(userResource.getPassword()));
           } else {
               throw new DuplicateEntityFieldException("Password is the same as the old one.");
           }

           return USER_MAPPER.toUserResource(userRepository.save(user));
       } catch (EntityNotFoundException e) {
           throw new EntityNotFoundException("Unable to find user with id " + id + ".");
       } catch (DataIntegrityViolationException e) {
           throw new DuplicateEntityFieldException("Username already exists.");
       }
    }

    @Override
    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Unable to find user with id " + id + ".");
        }
    }
}
