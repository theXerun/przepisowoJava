//package pl.mgromniak.przepisowo.Security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import pl.mgromniak.przepisowo.Entity.Attempt;
//import pl.mgromniak.przepisowo.Entity.User;
//import pl.mgromniak.przepisowo.Repository.AttemptRepository;
//import pl.mgromniak.przepisowo.Repository.UserRepository;
//import pl.mgromniak.przepisowo.Service.SecurityUserDetailsService;
//
//import java.util.Optional;
//
//@Component
//public class AuthProvider implements AuthenticationProvider {
//    private static final int ATTEMPTS_LIMIT = 3;
//
//    @Autowired
//    private SecurityUserDetailsService userDetailsService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private AttemptRepository attemptsRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String username = authentication.getName();
//        String password = authentication.getCredentials().toString();
//        Optional<Attempt> userAttempts = attemptsRepository.findAttemptsByUsername(username);
//        if (userAttempts.isPresent()) {
//            Attempt attempts = userAttempts.get();
//            attempts.setAttempts(0);
//            attemptsRepository.save(attempts);
//        }
//        return new UsernamePasswordAuthenticationToken(username, password);
//    }
//    private void processFailedAttempts(String username, User user) {
//        Optional<Attempt> userAttempts = attemptsRepository.findAttemptsByUsername(username);
//        if (userAttempts.isEmpty()) {
//            Attempt attempts = new Attempt();
//            attempts.setUsername(username);
//            attempts.setAttempts(1);
//            attemptsRepository.save(attempts);
//        } else {
//            Attempt attempts = userAttempts.get();
//            attempts.setAttempts(attempts.getAttempts() + 1);
//            attemptsRepository.save(attempts);
//
//            if (attempts.getAttempts() + 1 >
//                    ATTEMPTS_LIMIT) {
//                user.setAccountNonLocked(false);
//                userRepository.save(user);
//                throw new LockedException("Too many invalid attempts. Account is locked!");
//            }
//        }
//    }
//    @Override public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}