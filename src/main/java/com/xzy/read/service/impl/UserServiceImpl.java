package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.entity.User;
import com.xzy.read.repository.UserRepository;
import com.xzy.read.service.FileService;
import com.xzy.read.service.UserService;

import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/23 16:07
 */
@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final FileService fileService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        User user = findByTelephone(telephone);
        if (user == null){
            throw new UsernameNotFoundException("手机号不存在");
        }
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getTelephone(),
                user.getPassword(),simpleGrantedAuthorities);
    }

    @Override
    public User findByTelephone(String telephone) {
        return userRepository.findByTelephone(telephone);
    }

    @Override
    public ResultVo save(User user) {
        if (userRepository.findByTelephone(user.getTelephone()) != null) {
            return ResultVoUtil.error(0, "该手机号已经被注册");
        }
        if (userRepository.findByNickname(user.getNickname()) != null) {
            return ResultVoUtil.error(0, "该昵称已经被使用");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setSex("保密");
        userRepository.save(user);
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo getUserInfo() {
        User u = userRepository.findByTelephone(SecurityUtil.getAuthentication().getName());
        if (u == null) {
            return ResultVoUtil.error(0,"您未登录,请重新登录");
        }
        String telephone = u.getTelephone();
        u.setTelephone(telephone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
        return ResultVoUtil.success(u);
    }

    @Override
    public ResultVo update(User user) {
        User u = userRepository.findByTelephone(SecurityUtil.getAuthentication().getName());
        if (user.getNickname() != null) {
            if (!u.getNickname().equals(user.getNickname())
                    && userRepository.findByNickname(user.getNickname()) != null) {
                return ResultVoUtil.error(0, "昵称已被使用，换一个吧");
            }
            u.setNickname(user.getNickname());
        }
        if (user.getIntroduce() != null) {
            u.setIntroduce(user.getIntroduce());
        }
        if (user.getHeadUrl() != null) {
            u.setHeadUrl(user.getHeadUrl());
        }
        if (user.getSex() != null) {
            u.setSex(user.getSex());
        }
        userRepository.save(u);
        String telephone = u.getTelephone();
        u.setTelephone(telephone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
        return ResultVoUtil.success(u);
    }

    @Override
    public ResultVo uploadHead(MultipartFile multipartFile) {
        User u = userRepository.findByTelephone(SecurityUtil.getAuthentication().getName());
        ResultVo resultVo = fileService.uploadFile(multipartFile);
        if (resultVo.getCode() == 1) {
            u.setHeadUrl(resultVo.getData().toString());
            userRepository.save(u);
            return resultVo;
        }
        return ResultVoUtil.error(0,"发生错误");
    }

    @Override
    public ResultVo resetPassword(User user) {
        User u = userRepository.findByTelephone(user.getTelephone());
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(u);
        return ResultVoUtil.success();
    }

    @Override
    public ResultVo resetTelephone(User user) {
        if (userRepository.findByTelephone(user.getTelephone()) != null) {
            return ResultVoUtil.error(0, "该手机号已经被注册");
        }
        User u = userRepository.findByTelephone(SecurityUtil.getAuthentication().getName());
        u.setTelephone(user.getTelephone());
        userRepository.save(u);
        return ResultVoUtil.success();
    }


}
