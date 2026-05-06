package advertisement.security;

import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginPasswordUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(LoginPasswordUserDetailsService.class);
    @Autowired
    IUserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(login);
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });

        return new UserPrincipal(userEntity);
    }
}
