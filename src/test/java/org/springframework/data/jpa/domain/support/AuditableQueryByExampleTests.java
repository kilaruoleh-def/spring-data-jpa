/*
 * Copyright 2008-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.domain.support;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.sample.AuditableUser;
import org.springframework.data.jpa.domain.sample.AuditorAwareStub;
import org.springframework.data.jpa.repository.sample.AnnotatedAuditableUserRepository;
import org.springframework.data.jpa.repository.sample.AuditableUserRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for {@link org.springframework.data.repository.query.QueryByExampleExecutor} involving
 * {@link java.util.Optional}, using {@link org.springframework.data.domain.Auditable} as the means to verify it works
 * properly.
 *
 * @author Greg Turnquist
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:auditing/auditing-query-by-example.xml")
public class AuditableQueryByExampleTests {

	@Autowired AuditableUserRepository repository;
	@Autowired AnnotatedAuditableUserRepository annotatedUserRepository;

	@Autowired AuditorAwareStub auditorAware;

	private AuditableUser user;

	@BeforeEach
	void setUp() {

		user = new AuditableUser();
		auditorAware.setAuditor(user);

		repository.saveAndFlush(user);
	}

	@Test
	void queryByExampleTreatsEmptyOptionalsLikeNulls() {

		user.setFirstname("Greg");
		user = repository.saveAndFlush(user);

		AuditableUser probe = new AuditableUser();
		probe.setFirstname("Greg");
		Example<AuditableUser> example = Example.of(probe);

		List<AuditableUser> results = repository.findAll(example);

		assertThat(results).hasSize(1);
		assertThat(results).extracting(AuditableUser::getFirstname).containsExactly("Greg");
	}
}
