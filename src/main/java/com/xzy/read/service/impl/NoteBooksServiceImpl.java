package com.xzy.read.service.impl;

import com.xzy.read.VO.ResultVo;
import com.xzy.read.dto.PageDTO;
import com.xzy.read.dto.SimpleArticleDTO;
import com.xzy.read.dto.SimpleArticleNoteBookDTO;
import com.xzy.read.dto.SimpleNoteBookDTO;
import com.xzy.read.entity.Article;
import com.xzy.read.entity.NoteBooks;
import com.xzy.read.entity.User;
import com.xzy.read.entity.enums.FollowType;
import com.xzy.read.repository.ArticleRepository;
import com.xzy.read.repository.FollowsRepository;
import com.xzy.read.repository.NoteBooksRepository;
import com.xzy.read.service.NoteBooksService;
import com.xzy.read.service.UserService;
import com.xzy.read.util.ResultVoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author XieZhongYi
 * 2020/03/25 15:08
 */
@Service
public class NoteBooksServiceImpl implements NoteBooksService {

    private NoteBooksRepository noteBooksRepository;

    private UserService userService;

    private ArticleRepository articleRepository;

    private FollowsRepository followsRepository;

    public NoteBooksServiceImpl(NoteBooksRepository noteBooksRepository, UserService userService, ArticleRepository articleRepository, FollowsRepository followsRepository) {
        this.noteBooksRepository = noteBooksRepository;
        this.userService = userService;
        this.articleRepository = articleRepository;
        this.followsRepository = followsRepository;
    }

    @Override
    public ResultVo getAll() {
        Long id = userService.getUserId();
        List<NoteBooks> noteBooks = noteBooksRepository.findAllByUserIdAndIsDeleted(id,false);
        return ResultVoUtil.success(noteBooks);
    }

    @Override
    public ResultVo create(NoteBooks noteBooks) {
        Long id = userService.getUserId();
        if (noteBooksRepository.findByNameAndUserId(noteBooks.getName(), id)!=null) {
            return ResultVoUtil.error(0,"该文集名称已经存在");
        }
        noteBooks.setUserId(id);
        noteBooksRepository.save(noteBooks);
        return ResultVoUtil.success(noteBooks);
    }

    @Override
    public ResultVo update(NoteBooks noteBooks) {
        Optional<NoteBooks> nb = noteBooksRepository.findById(noteBooks.getId());
        if (nb.isPresent()) {
            Long id = userService.getUserId();
            if (noteBooksRepository.findByNameAndUserId(noteBooks.getName(), id)!=null) {
                return ResultVoUtil.error(0,"该文集名称已经存在");
            }
            nb.get().setName(noteBooks.getName());
            noteBooksRepository.save(nb.get());
            return ResultVoUtil.success();
        }
        return ResultVoUtil.error(0,"该文集不存在");
    }


    @Override
    public ResultVo delete(Long id) {
        Optional<NoteBooks> nb = noteBooksRepository.findById(id);
        if (nb.isPresent()) {
            nb.get().setIsDeleted(true);
            noteBooksRepository.save(nb.get());
        }
        return ResultVoUtil.success();
    }

    @Override
    public NoteBooks findById(Long id) {
        Optional<NoteBooks> noteBooksOptional = noteBooksRepository.findById(id);
        return noteBooksOptional.orElse(null);
    }

    @Override
    public ResultVo getSimpleInfo(Long id) {
        Optional<NoteBooks> noteBooksOptional = noteBooksRepository.findByIdAndIsDeleted(id, false);
        if (noteBooksOptional.isPresent()) {
            NoteBooks books = noteBooksOptional.get();
            User user = userService.findById(books.getUserId());
            Long articles = articleRepository.countByNotebookId(books.getId());
            Long words = articleRepository.countWordsByNotebookId(books.getId());
            Long followers = followsRepository.countByTypeIdAndFollowTypeAndStatus(books.getId(), FollowType.NOTEBOOK, true);
            SimpleNoteBookDTO simpleNoteBookDTO = new SimpleNoteBookDTO(books.getId(), books.getName(),
                    articles,words,followers,user.getId(), user.getHeadUrl(),user.getNickname(),
                    followsRepository.existsByTypeIdAndFollowTypeAndStatusAndUserId(id,FollowType.NOTEBOOK,
                            true, userService.getUserId()));
            return ResultVoUtil.success(simpleNoteBookDTO);
        }
        return ResultVoUtil.error(0, "该文集不存在");
    }

    @Override
    public ResultVo getArticlesByNbId(Long id, int page) {
        Page<Article> articlePage = articleRepository.findAllByNotebookIdAndIsDeleted(id,
                false, PageRequest.of(page-1, 4,
                        Sort.by(Sort.Direction.DESC,"isTop","id")));
        List<SimpleArticleNoteBookDTO> simpleArticleDTOS = new ArrayList<>();
        for (Article article : articlePage.toList()) {
            SimpleArticleNoteBookDTO dto = new SimpleArticleNoteBookDTO(article.getId(),
                    article.getTitle(),removeHtml(article.getContent()),
                    article.getClicks(),
                    articleRepository.countCommentsByArticleId(article.getId()),
                    article.getLikes(),article.getCreatedDate(), article.getIsTop());
            simpleArticleDTOS.add(dto);
        }
        PageDTO<SimpleArticleNoteBookDTO> pageDTO = new PageDTO<>(simpleArticleDTOS,
                articlePage.getTotalElements(), articlePage.getTotalPages());
        return ResultVoUtil.success(pageDTO);
    }

    @Override
    public List<NoteBooks> findAllByUserId(Long userId) {
        return noteBooksRepository.findAllByUserIdAndIsDeleted(userId, false);
    }

    private String removeHtml (String content) {
        if (content == null) {
            return "";
        }
        Document doc = Jsoup.parse(content);
        return doc.text();
    }
}
