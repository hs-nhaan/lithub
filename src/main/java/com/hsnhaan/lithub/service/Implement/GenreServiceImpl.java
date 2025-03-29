package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.hsnhaan.lithub.dao.GenreDAO;
import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.service.IGenreService;
import com.hsnhaan.lithub.util.StringHelper;

@Service
public class GenreServiceImpl implements IGenreService {

	private final GenreDAO genreDAO;
	
	public GenreServiceImpl(GenreDAO genreDAO ) {
		this.genreDAO = genreDAO;
	}
	
	@Override
	public List<Genre> getAll() {
		return genreDAO.findAll();
	}

	@Override
	public Genre getById(int id) {
		return genreDAO.findById(id).orElse(null);
	}
	
	@Override
	public List<Genre> getByIds(List<Integer> ids) {
		return genreDAO.findAllById(ids);
	}
	
	@Override
	public Optional<Genre> getBySlug(String slug) {
		return Optional.ofNullable(genreDAO.findBySlug(slug));
	}

	@Override
	public Page<Genre> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return genreDAO.findAll(pageable);
	}

	@Override
	public Page<Genre> search(String keyword, int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return genreDAO.findByNameContaining(keyword, pageable);
	}
	
	@Override
	public long countByIds(List<Integer> ids) {
		return genreDAO.countByIdIn(ids);
	}

	@Override
	public void save(Genre genre) {
		genre.setSlug(StringHelper.toSlug(genre.getName()));
		genre.setName(StringHelper.toTitleCase(genre.getName()));
		validate(genre, null, true);
		genreDAO.save(genre);
	}

	@Override
	public Genre update(String slug, String name) {
		Genre genre = Optional.ofNullable(genreDAO.findBySlug(slug)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String odlName = genre.getName();
		genre.setSlug(StringHelper.toSlug(name));
		genre.setName(StringHelper.toTitleCase(name));
		validate(genre, odlName, false);
		return genreDAO.save(genre);
	}
	
	@Override
	public void delete(String slug) {
		Genre genre = getBySlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		genreDAO.delete(genre);
	}
	
	private void validate(Genre genre, String oldName, boolean isNew) {
		if (!StringUtils.hasText(genre.getName()) || genre.getName().length() > 255)
			throw new IllegalArgumentException("Tên thể loại không hợp lệ");
		if (isNew || !oldName.equals(genre.getName()))
			if (genreDAO.existByName(genre.getName()))
				throw new DuplicateKeyException("Tên thể loại đã tồn tại");
	}

}
