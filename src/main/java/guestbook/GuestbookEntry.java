/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guestbook;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

/**
 * A guestbook entry. An entity as in the Domain Driven Design context. Mapped onto the database using JPA annotations.
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 * @see https://en.wikipedia.org/wiki/Domain-driven_design#Building_blocks
 */
@Entity
class GuestbookEntry {

	private @Id @GeneratedValue Long id;
	private @NotBlank String name, email, text;
	private LocalDateTime date;
	private boolean editable;
	private boolean show;
	private List<GuestbookEntry> comments;

	/**
	 * Creates a new {@link GuestbookEntry} for the given name and text.
	 *
	 * @param name must not be {@literal null} or empty
	 * @param text must not be {@literal null} or empty
	 */
	public GuestbookEntry(String name, String email, String text, boolean editable, boolean show) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.hasText(text, "Text must not be null or empty!");

		this.name = name;
		this.email = email;
		this.text = text;
		this.editable = editable;
		this.date = LocalDateTime.now();
		this.show = show;

		this.comments = new ArrayList<>();
	}

	@SuppressWarnings("unused")
	private GuestbookEntry() {
		this.name = null;
		this.email = null;
		this.text = null;
		this.date = null;

		this.show = false;
		this.editable = true;

		this.comments = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public String getEmail(){ return email; }

	public Long getId() {
		return id;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getText() {
		return text;
	}

	public void setVisible(boolean show) {
		this.show = show;
	}

	public void addComment(GuestbookEntry comment){
		if(comment == null) throw new NullPointerException();
		this.comments.add(comment);
	}

	public boolean removeComment(GuestbookEntry comment){
		if(comment == null) throw new NullPointerException();
		return this.comments.remove(comment);
	}
}
