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
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

/**
 * A guestbook entry. An entity as in the Domain Driven Design context. Mapped onto the database using JPA annotations.
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 * @see \https://en.wikipedia.org/wiki/Domain-driven_design#Building_blocks
 */
@Entity
class GuestbookEntry {

	private @Id @GeneratedValue Long id;
	private @NotNull Long parent;
	private @NotBlank String name;
	private @NotBlank String email;
	private @NotBlank String text;
	private @NotBlank String color;

	private LocalDateTime date;
	private boolean editable;
	private boolean show;


	//private int rating;

	/**
	 * Creates a new {@link GuestbookEntry} for the given name and text.
	 *
	 * @param name must not be {@literal null} or empty
	 * @param text must not be {@literal null} or empty
	 */
	public GuestbookEntry(String name, String email, String text, String color, boolean editable, boolean show, Long parentID) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.hasText(text, "Text must not be null or empty!");

		this.name = name;
		this.email = email;
		this.text = text;
		this.editable = editable;
		this.date = LocalDateTime.now();
		this.show = show;
		if(color == null || color.isEmpty()) color = "#e0b689";
		this.color = color;

		this.parent = parentID;
	}

	public GuestbookEntry(String name, String email, String text, boolean editable, boolean show) {

		Assert.hasText(name, "Name must not be null or empty!");
		Assert.hasText(text, "Text must not be null or empty!");

		this.name = name;
		this.email = email;
		this.text = text;
		this.color = "#e0b689";

		this.date = LocalDateTime.now();

		this.editable = editable;
		this.show = show;

		this.parent = (long) -1;
	}

	public GuestbookEntry() {
		this.name = null;
		this.email = null;
		this.text = null;
		this.color = "#000000";

		this.date = null;

		this.show = false;
		this.editable = true;

		this.parent = (long) -1;
	}

	public GuestbookEntry copy(){
		return new GuestbookEntry(name, email, text, color, editable, show, parent);
	}

	/*
	public void replace(GuestbookEntry entry){
		this.name = entry.getName();
		this.email = entry.getEmail();
		this.text = entry.getText();
		this.date = LocalDateTime.now();

		this.editable = entry.isEditable();
		this.show = entry.isVisible();

		this.parent = entry.getParent();
	}

	 */

	public Long getId() { return id; }

	public Boolean hasParent() { if(parent == null) parent = (long) -1; return parent != (long)-1; }

	public Long getParent() { return parent; }
	public void setParent(Long parent) { this.parent = parent; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getEmail(){ return email; }
	public void setEmail(String email) { this.email = email; }

	public String getColor() { return color; }
	public void setColor(String color) { this.color = color; }

	public LocalDateTime getDate() { return date; }
	public void setDate(LocalDateTime date) { this.date = date; }

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	public boolean getEditable() { return editable; }
	public void setEditable(boolean editable) { this.editable = editable; }

	public boolean getShow() { return show; }
	public void setShow(boolean show) { this.show = show; }

	public GuestbookForm toGuestbookForm(){
		GuestbookForm form = new GuestbookForm(name, email, text, editable, show, Optional.ofNullable(parent));
		form.setColor(color);
		return form;
	}

	public String toString(){
		String out = "  GuestBookEntry:" + "\n    ";
		out += this.id + "\n    ";
		out += this.name + "\n    ";
		out += this.email + "\n    ";
		out += this.text + "\n    ";
		out += this.color + "\n    ";
		out += this.date + "\n    ";
		out += this.show + "\n    ";
		out += this.editable + "\n    ";
		out += this.parent + "\n";
		out += "\n\n";
		return out;
	}
}
