package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
public class SSUDS implements UserDetailsService {

    private AppUserRepository userRepo;

    public SSUDS(AppUserRepository userRepository) {
        this.userRepo = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       try
       {
           AppUser thisUser = userRepo.findAppUserByUsername(username);
           if(thisUser==null) {
               return null;
           }
           return new User(thisUser.getUsername(),thisUser.getPassword(),grantedAuthorities(thisUser));
       }  catch (Exception e){

           throw new UsernameNotFoundException("User Not Found");
       }


    }

    private Set <GrantedAuthority> grantedAuthorities(AppUser user)
    {
        Set <GrantedAuthority> userAuthorities = new HashSet<>();
        for(AppRole eachRole: user.getRoles())
        {
            GrantedAuthority grantedAuthority=new SimpleGrantedAuthority(eachRole.getRoleName());
            userAuthorities.add(new SimpleGrantedAuthority(eachRole.getRoleName()));
        }
        return userAuthorities;
    }
}
