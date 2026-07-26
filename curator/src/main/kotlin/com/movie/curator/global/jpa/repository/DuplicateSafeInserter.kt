package com.movie.curator.global.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * 유니크 제약이 있는 엔티티를 멱등하게 삽입하기 위한 헬퍼.
 *
 * 삽입을 REQUIRES_NEW 트랜잭션으로 격리한다. 동시 경쟁으로 유니크 제약 위반이 나면
 * 이 내부 트랜잭션만 롤백되고 [org.springframework.dao.DataIntegrityViolationException]이
 * 호출측으로 전달되어 무시(no-op)할 수 있다. 호출측의 외부 트랜잭션은 rollback-only로
 * 오염되지 않는다.
 *
 * 자기-호출은 프록시를 우회하므로, REQUIRES_NEW가 적용되려면 반드시 별도 빈으로 분리해
 * 호출해야 한다.
 */
@Component
class DuplicateSafeInserter {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun <T : Any> insert(repository: JpaRepository<T, *>, entity: T) {
        repository.saveAndFlush(entity)
    }
}
