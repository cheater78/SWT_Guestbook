/*
 * Copyright 2015-2019 the original author or authors.
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

import jakarta.validation.constraints.NotBlank;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Type to bind request payloads and make them available in the controller. In contrast to {@link GuestbookEntry} it is
 * particularly designed to also be able to capture invalid input, so that the raw form data can be bound and validated
 * against business constraints using code and / or annotations.
 * <p>
 * Note how the fields are annotated with the {@link NotBlank} annotation, which tells Spring how to validate the values.
 *
 * @author Oliver Drotbohm
 * @see GuestbookController#addEntry(GuestbookForm, org.springframework.validation.Errors, org.springframework.ui.Model)
 */
class GuestbookForm {

	private Optional<Long> parent;
	private @NotBlank String name;
	private @NotBlank String email;
	private @NotBlank String text;
	private @NotBlank String color;
	private LocalDateTime date;
	private Boolean editable;
	private Boolean show;


	/**
	 * Creates a new {@link GuestbookForm} with the given name and text. Spring Framework will use this constructor to
	 * bind the values provided in the web form described in {@code src/main/resources/templates/guestbook.html}, in
	 * particular the {@code name} and {@code text} fields as they correspond to the parameter names of the constructor.
	 * The constructor needs to be public so that Spring will actually consider it for form data binding until
	 * {@link \https://github.com/spring-projects/spring-framework/issues/22600} is resolved.
	 *
	 * @param name the value to bind to {@code name}
	 * @param text the value to bind to {@code text}
	 */
	public GuestbookForm(String name, String email, String text, Boolean editable, Boolean show, Optional<Long> parentID) {
		this.name = name;
		this.email = email;
		this.text = text;
		this.date = LocalDateTime.now();

		if(editable == null) editable = true;
		if(show == null) show = true;

		this.editable = editable;
		this.show = show;

		this.parent = parentID;
	}

	public Optional<Long> getParentID() { return parent; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getEmail(){ return email; }
	public void setEmail(String email) { this.email = email; }



	public LocalDateTime getDate() { return date; }
	public void setDate(LocalDateTime date) { this.date = date; }

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	public String getColor() { return color; }
	public void setColor(String color) { this.color = color; }

	public Boolean getEditable() { return editable; }
	public void setEditable(Boolean editable) { if(editable == null) editable = true; this.editable = editable; }

	public Boolean getShow() { return show; }
	public void setShow(Boolean show) { if(show == null) show = true; this.show = show; }



	GuestbookEntry toNewEntry() { return new GuestbookEntry(name, email, text, color, editable, show, parent.orElseGet(() -> (long) -1)); }
}
