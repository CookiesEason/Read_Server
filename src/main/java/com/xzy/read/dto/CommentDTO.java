package com.xzy.read.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author XieZhongYi
 * 2020/03/29 20:02
 */
@Data
@AllArgsConstructor
public class CommentDTO {

    private SimpleCommentDTO comment;

    private List<SimpleReplyDTO> replies;

}
