package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.dto.RecommendSimpleArticle;
import com.xzy.read.dto.RecommendUserDTO;
import com.xzy.read.dto.SimpleArticleDTO;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.User;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.FollowersRepository;
import com.xzy.read.repository.UserRepository;
import com.xzy.read.service.FileService;
import com.xzy.read.service.UserService;

import com.xzy.read.util.ResultVoUtil;
import com.xzy.read.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private ArticleRepository articleRepository;

    private FollowersRepository followersRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService, ArticleRepository articleRepository, FollowersRepository followersRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
        this.articleRepository = articleRepository;
        this.followersRepository = followersRepository;
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
    public Long getUserId() {
        return userRepository.findIdByTelephone(SecurityUtil.getAuthentication().getName());
    }

    @Override
    public User findById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
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

    @Override
    public ResultVo recommendUsers(int size, int page) {
        Page<Object[]> hotUsersPage = userRepository.findHotUsers(PageRequest.of(page,size,
                Sort.by(Sort.Direction.DESC, "words", "likes")));
        List<Object[]> usersObject = hotUsersPage.toList();
        List<RecommendUserDTO> recommendUserDTOS = new ArrayList<>();
        Long userId = getUserId();
        for (Object[] objects : usersObject) {
            boolean isFollowed = false;
            BigInteger bi = new BigInteger(objects[0].toString());
            if (userId != null) {
                isFollowed = followersRepository.countByFromUserIdAndToUserIdAndStatus(userId, bi.longValue(),true) > 0;
            }
            Page<Article> articlePage = articleRepository.findAllByUserIdAndIsPublishedAndIsDeleted(bi.longValue(),
                    true, false,PageRequest.of(0,3,Sort.by(Sort.Direction.DESC,"createdDate")));
            List<RecommendSimpleArticle> articleDTOS = new ArrayList<>();
            for (Article article : articlePage.toList()) {
                RecommendSimpleArticle simpleArticleDTO = new RecommendSimpleArticle(article.getId(), article.getTitle());
                articleDTOS.add(simpleArticleDTO);
            }
            RecommendUserDTO recommendUserDTO = new RecommendUserDTO((BigInteger) objects[0],(String) objects[1],(String) objects[2],
                    (String) objects[3],(String) objects[4],(BigDecimal) objects[5],(BigDecimal ) objects[6],
                    isFollowed,articleDTOS);
            recommendUserDTOS.add(recommendUserDTO);
        }
        PageDTO<RecommendUserDTO> pageDTO = new PageDTO<>(recommendUserDTOS, hotUsersPage.getTotalElements(), hotUsersPage.getTotalPages());
        return ResultVoUtil.success(pageDTO);
    }


}
