package com.hsnhaan.lithub.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "stories")
public class Story {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String title;
	private String description;
	private String cover_image;
	private LocalDate created_at;
	private boolean status;
	private String slug;
	@ManyToMany
	@JoinTable(name = "story_genre", joinColumns = @JoinColumn(name = "story_id"),
					inverseJoinColumns = @JoinColumn(name = "genre_id"))
	private Set<Genre> genres = new HashSet<Genre>();
	@OneToMany(mappedBy = "story")
	private Set<Rating> ratings = new HashSet<Rating>();
	@OneToMany(mappedBy = "story")
	private Set<Chapter> chapters = new HashSet<Chapter>();
	
	public Story() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCover_image() {
		return cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public LocalDate getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDate created_at) {
		this.created_at = created_at;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}
	
	public float getTotalRating() {
		if (ratings != null && !ratings.isEmpty())
			return (float) ratings.stream().mapToDouble(Rating::getRating).average().orElse(0.0f);
		return 0.0f;
	}
	
	public long getRatingCount() {
		return ratings.size();
	}

	public Set<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(Set<Chapter> chapters) {
		this.chapters = chapters;
	}
	
	public boolean isNew() {
		if (created_at == null)
			return false;
		return created_at.isAfter(LocalDate.now().minusMonths(6));
	}
	
	public long getChapterCount() {
		return chapters.size();
	}
	
}
