package com.scw.springtodomanagement.service;

import com.scw.springtodomanagement.controller.request.PostCURequestDTO;
import com.scw.springtodomanagement.controller.request.PostDRequestDTO;
import com.scw.springtodomanagement.controller.response.PostResponseDTO;
import com.scw.springtodomanagement.entity.Post;
import com.scw.springtodomanagement.entity.enums.DomainType;
import com.scw.springtodomanagement.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    /**
     * create
     * throw 도메인 검증 { google, naver, github}
     */
    @Transactional
    public PostResponseDTO createPost(PostCURequestDTO requestDTO) {
        DomainType.fromDomainValidation(requestDTO.getManagerEmail());
        Post savePost = postRepository.save(requestDTO.toPostDomain());

        return getPostResponseDTO(savePost);
    }

    /**
     * 단건 조회
     * throw 게시물 여부 검증
     */
    public PostResponseDTO findPostById(Long id) {
        Post findPostData = postRepository.findByIdOrElseThrow(id);
        return getPostResponseDTO(findPostData);
    }

    /**
     * 다건 조회
     * 작성일 기준 내림차순
     */
    public List<PostResponseDTO> findAll() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(PostResponseDTO::new)
                .toList();
    }

    /**
     * update
     * throw 게시물 여부 검증
     * throw 도메인 검증 { google, naver, github}
     */
    @Transactional
    public PostResponseDTO updatePost(Long id, PostCURequestDTO requestDTO) {
        Post findPostData = postRepository.findByIdOrElseThrow(id);
        DomainType.fromDomainValidation(requestDTO.getManagerEmail());

        passwordValidation(requestDTO.getPassword(), findPostData.getPassword());

        findPostData.updateTitle(requestDTO);
        return getPostResponseDTO(findPostData);
    }

    /**
     * delete
     * throw 게시글 여부 검증
     * throw 비밀번호 동일 여부 검증
     */
    @Transactional
    public void deletePost(Long id, PostDRequestDTO requestDTO) {
        Post findPostData = postRepository.findByIdOrElseThrow(id);

        passwordValidation(requestDTO.getPassword(), findPostData.getPassword());

        postRepository.delete(findPostData);
    }

    /**
     * 비밀번호 검증
     */
    private void passwordValidation(String inputPassword, String findPassword) {
        if (!inputPassword.equals(findPassword)) {
            throw new IllegalArgumentException("일치하지 않는 비밀번호입니다.");
        }
    }

    /**
     * responseMethod
     * return PostResponseDTO
     */
    private PostResponseDTO getPostResponseDTO(Post savePostData) {
        return PostResponseDTO.builder()
                .id(savePostData.getId())
                .title(savePostData.getTitle())
                .content(savePostData.getContent())
                .manager(savePostData.getManagerEmail())
                .createdAt(savePostData.getCreatedAt())
                .lastModifiedAt(savePostData.getLastModifiedAt())
                .build();
    }
}