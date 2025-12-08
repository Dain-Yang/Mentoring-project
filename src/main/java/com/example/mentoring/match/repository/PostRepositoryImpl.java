package com.example.mentoring.match.repository;

import com.example.mentoring.match.dto.PostResponse;
import com.example.mentoring.match.dto.PostSearchCondition;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.mentoring.match.entity.QPost.post;
import static com.example.mentoring.match.entity.QPostTag.postTag;
import static com.example.mentoring.match.entity.QTag.tag;
import static com.example.mentoring.member.entity.QUser.user;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<PostResponse> search(PostSearchCondition condition, Pageable pageable) {

    // 컨텐츠 조회 쿼리
    List<com.example.mentoring.match.entity.Post> posts = queryFactory
        .selectFrom(post)
        .leftJoin(post.user, user).fetchJoin() // N+1 문제 방지
        .where(
            isRecruiting(),
            keywordContains(condition.getKeyword()),
            tagIn(condition.getTagIds())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(post.createdAt.desc()) // 최신순 정렬
        .distinct() // 태그 조인 시 중복 제거
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(post.count())
        .from(post)
        .where(
            isRecruiting(),
            keywordContains(condition.getKeyword()),
            tagIn(condition.getTagIds())
        );

    List<PostResponse> content = posts.stream()
        .map(PostResponse::from)
        .collect(Collectors.toList());

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  // 동적 쿼리 조건 메서드들

  private BooleanExpression isRecruiting() {
    return post.isRecruiting.isTrue();
  }

  private BooleanExpression keywordContains(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }
    // 제목이나 내용에 키워드가 포함되어 있으면 True
    return post.title.contains(keyword).or(post.content.contains(keyword));
  }

  private BooleanExpression tagIn(List<Integer> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) {
      return null;
    }
    // Post -> PostTag 연결이 필요하므로 서브쿼리나 Join을 사용할 수 있음.
    // 여기서는 간단하게 where 절 내에서 any() 사용 예시:
    // return post.postTags.any().tag.id.in(tagIds);

    // 혹은 명시적 Join을 원한다면 메인 쿼리에 join을 걸어야 함.
    // 위 search 메서드에 .leftJoin(post.postTags, postTag) 추가 필요.
    return post.postTags.any().tag.id.in(tagIds);
  }
}