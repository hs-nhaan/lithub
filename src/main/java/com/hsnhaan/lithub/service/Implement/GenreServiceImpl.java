package com.hsnhaan.lithub.service.Implement;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hsnhaan.lithub.dao.GenreDAO;
import com.hsnhaan.lithub.model.Genre;
import com.hsnhaan.lithub.service.IGenreService;

@Service
public class GenreServiceImpl implements IGenreService {

	private final GenreDAO genreDAO;
	
	@Autowired
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
	public List<Genre> search(String keyword) {
		return genreDAO.findByNameContaining(keyword);
	}

	@Override
	public Page<Genre> getAll(int page, int limit) {
		Pageable pageable = PageRequest.of(page - 1, limit);
		return genreDAO.findAll(pageable);
	}

	@Override
	public Page<Genre> search(String keyword, int page, int limit) {
		List<Genre> genres =search(keyword);
		
		Pageable pageable = PageRequest.of(page - 1, limit);
		int start = (int) pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), genres.size());
		
		List<Genre> subGenres = genres.subList(start, end);
		
		return new PageImpl<Genre>(subGenres, pageable, genres.size());
	}
	
	@Override
	public long countByIds(List<Integer> ids) {
		return genreDAO.countByIdIn(ids);
	}

	@Override
	public void save(Genre genre) {
		validate(genre, true);
		genreDAO.save(genre);
	}

	@Override
	public void update(Genre genre) {
		if (!genreDAO.existsById(genre.getId()))
			throw new RuntimeException("Không tìm thấy thể loại");
		validate(genre, false);
		genreDAO.save(genre);
	}

	@Override
	public void delete(Genre genre) {
		if (!genreDAO.existsById(genre.getId()))
			throw new RuntimeException("Không tìm thấy thể loại");
		genreDAO.delete(genre);
	}
	
	private void validate(Genre genre, boolean isNew) {
		if (!StringUtils.hasText(genre.getName()))
			throw new RuntimeException("Tên thể loại không được để trống");
		if (isNew || !genreDAO.findById(genre.getId()).map(g -> g.getName().equals(genre.getName())).orElse(false))
			if (genreDAO.existByName(genre.getName()))
				throw new RuntimeException("Tên thể loại đã tồn tại");
	}

}
