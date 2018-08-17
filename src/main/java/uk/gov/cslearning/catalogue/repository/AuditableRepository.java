package uk.gov.cslearning.catalogue.repository;


import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.gov.cslearning.catalogue.domain.Auditable;
import uk.gov.cslearning.catalogue.service.AuthenticationFacade;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AuditableRepository <T extends Auditable, R extends ElasticsearchRepository<T, String>> implements ElasticsearchRepository<T, String> {

    protected final R wrappedRepository;
    private final AuthenticationFacade authenticationFacade;

    public AuditableRepository(R wrappedRepository, AuthenticationFacade authenticationFacade) {
        this.wrappedRepository = wrappedRepository;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public <S extends T> S save(S entity) {

        Optional<T> existingEntityOptional = this.wrappedRepository.findById(entity.getId());

        if (existingEntityOptional.isPresent()) {
            T existingEntity = existingEntityOptional.get();

            entity.setCreatedDate(existingEntity.getCreatedDate());
            entity.setCreatedBy(existingEntity.getCreatedBy());
            entity.setModifiedBy(authenticationFacade.getAuthentication().getName());
            entity.setModifiedDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        } else {
            entity.setCreatedBy(authenticationFacade.getAuthentication().getName());
            entity.setCreatedDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        }

        return this.wrappedRepository.save(entity);
    }


    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), false).map(this::save).collect(Collectors.toList());
    }

    @Override
    public <S extends T> S index(S entity) {
        return this.wrappedRepository.index(entity);
    }

    @Override
    public Iterable<T> search(QueryBuilder query) {
        return this.wrappedRepository.search(query);
    }

    @Override
    public Page<T> search(QueryBuilder query, Pageable pageable) {
        return this.wrappedRepository.search(query, pageable);
    }

    @Override
    public Page<T> search(SearchQuery searchQuery) {
        return this.wrappedRepository.search(searchQuery);
    }

    @Override
    public Page<T> searchSimilar(T entity, String[] fields, Pageable pageable) {
        return this.wrappedRepository.searchSimilar(entity, fields, pageable);
    }

    @Override
    public void refresh() {
        this.wrappedRepository.refresh();
    }

    @Override
    public Class<T> getEntityClass() {
        return this.wrappedRepository.getEntityClass();
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return this.wrappedRepository.findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return this.wrappedRepository.findAll(pageable);
    }

    @Override
    public Optional<T> findById(String s) {
        return this.wrappedRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return this.wrappedRepository.existsById(s);
    }

    @Override
    public Iterable<T> findAll() {
        return this.wrappedRepository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<String> strings) {
        return wrappedRepository.findAllById(strings);
    }

    @Override
    public long count() {
        return this.wrappedRepository.count();
    }

    @Override
    public void deleteById(String s) {
        this.wrappedRepository.deleteById(s);
    }

    @Override
    public void delete(T entity) {
        this.wrappedRepository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        this.wrappedRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        this.wrappedRepository.deleteAll();
    }
}
