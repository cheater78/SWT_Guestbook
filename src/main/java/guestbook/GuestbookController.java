/*
 * Copyright 2014-2021 the original author or authors.
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

import io.github.wimdeblauwe.hsbt.mvc.HtmxResponse;
import io.github.wimdeblauwe.hsbt.mvc.HxRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * A controller to handle web requests to manage {@link GuestbookEntry}s
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 */
@Controller
class GuestbookController {

	private final GuestbookRepository guestbook;
	private Map<Long, Boolean> showComments;
	private @NotNull Long editMode;

	GuestbookForm editEntryForm;

	public void updateShowComments(){
		for (GuestbookEntry entry : guestbook.findAll()){
			if(!showComments.containsKey(entry.getId())){
				showComments.put(entry.getId(), false);
			}
		}
	}

	public Boolean showCommntsOf(Long id){
		updateShowComments();
		if(!showComments.containsKey(id))
			showComments.put(id, false);
		return showComments.get(id);
	}

	/**
	 * Creates a new {@link GuestbookController} using the given {@link GuestbookRepository}. Spring will look for a bean
	 * of type {@link GuestbookRepository} and hand this into this class when an instance is created.
	 *
	 * @param guestbook must not be {@literal null}
	 */
	GuestbookController(GuestbookRepository guestbook) {

		Assert.notNull(guestbook, "Guestbook must not be null!");
		this.guestbook = guestbook;
		this.showComments = new HashMap<>();
		updateShowComments();

		this.editMode = (long) -1;
	}

	/**
	 * Handles requests to the application root URI. Note, that you can use {@code redirect:} as prefix to trigger a
	 * browser redirect instead of simply rendering a view.
	 *
	 * @return a redirect string
	 */
	@GetMapping(path = "/")
	String index() {
		return "redirect:/guestbook";
	}

	/**
	 * Handles requests to access the guestbook. Obtains all currently available {@link GuestbookEntry}s and puts them
	 * into the {@link Model} that's used to render the view.
	 *
	 * @param model the model that's used to render the view
	 * @param form the form to be added to the model
	 * @return a view name
	 */
	@GetMapping(path = "/guestbook")
	String guestBook(Model model, @ModelAttribute(binding = false) GuestbookForm form) {

		model.addAttribute("entries", guestbook.findAll());
		model.addAttribute("gbEntryForm", form);

		model.addAttribute("editEntryForm", editEntryForm);

		model.addAttribute("showComments", showComments);
		model.addAttribute("editMode", editMode);

		return "guestbook";
	}

	/**
	 * Handles AJAX requests to create a new {@link GuestbookEntry}. Instead of rendering a complete page, this view only
	 * renders and returns the HTML fragment representing the newly created entry.
	 * <p>
	 * Note that we do not react explicitly to a validation error: in such a case, Spring automatically returns an
	 * appropriate JSON document describing the error.
	 *
	 * @param form the form submitted by the user
	 * @param model the model that's used to render the view
	 * @return a reference to a Thymeleaf template fragment
	 * @see \#addEntry(String, String)
	 */
	@HxRequest
	@PostMapping(path = "/addEntry")
	HtmxResponse addEntry(@Valid GuestbookForm form, Model model) {

		GuestbookEntry entry = form.toNewEntry();
		guestbook.save(entry);
		if(!showComments.containsKey(entry.getId())) showComments.put(entry.getId(), false);

		model.addAttribute("index", guestbook.count());
		model.addAttribute("entry", entry);
		model.addAttribute("entries", guestbook.findAll());
		model.addAttribute("gbEntryForm", form);
		model.addAttribute("editEntryForm", editEntryForm);
		model.addAttribute("showComments", showComments);
		model.addAttribute("editMode", editMode);

		System.out.println("Added Entry: " + entry);

		return new HtmxResponse()
				.addTemplate("guestbook :: entry")
				.addTrigger("eventAdded");
	}



	@HxRequest
	@PostMapping(path = "/guestbook/edit{entry}")
	HtmxResponse editEntry(@PathVariable Optional<GuestbookEntry> entry, Model model) {
		System.out.println("Editing " + ((entry.isPresent()) ? entry.get().getId() : -1));
		if(!entry.isPresent()) return new HtmxResponse().addTemplate("guestbook :: entries");

		System.out.println("Editing Entry: " + entry.get());

		editMode = entry.get().getId();
		editEntryForm = guestbook.findById(editMode).get().toGuestbookForm();

		model.addAttribute("index", guestbook.count());
		model.addAttribute("entry", entry);
		model.addAttribute("entries", guestbook.findAll());

		model.addAttribute("editEntryForm", editEntryForm);

		model.addAttribute("showComments", showComments);
		model.addAttribute("editMode", editMode);

		return new HtmxResponse().addTemplate("guestbook :: entries");
	}

	@HxRequest
	@PostMapping(path = "/guestbook/editSubmit{entry}")
	HtmxResponse editSubmitEntry(@PathVariable Optional<GuestbookEntry> entry, @Valid GuestbookForm form, Model model) {
		System.out.println("editSubmit");
		if(!entry.isPresent()) return new HtmxResponse().addTemplate("guestbook :: entries");

		GuestbookEntry orig = entry.get();
		form.writeToEntry(orig);
		guestbook.save(orig);
		editMode = (long) -1;

		System.out.println("Submiting " + form.toNewEntry() + "\n for: \n" + orig);

		model.addAttribute("index", guestbook.count());
		model.addAttribute("entry", entry.get());
		model.addAttribute("entries", guestbook.findAll());
		model.addAttribute("gbEntryForm", form);
		model.addAttribute("editEntryForm", editEntryForm);
		model.addAttribute("showComments", showComments);
		model.addAttribute("editMode", editMode);

		return new HtmxResponse().addTemplate("guestbook :: entries");
	}

	@HxRequest
	@PostMapping(path = "/guestbook/showComments{entry}")
	HtmxResponse showEntryComments(@PathVariable Optional<GuestbookEntry> entry, @ModelAttribute(binding = false) GuestbookForm form, Model model) {
		if(!entry.isPresent()) return new HtmxResponse().addTemplate("guestbook :: entries");
		showComments.put(entry.get().getId(), !showCommntsOf(entry.get().getId()));

		model.addAttribute("index", guestbook.count());
		model.addAttribute("entry", entry.get());
		model.addAttribute("entries", guestbook.findAll());
		model.addAttribute("gbEntryForm", form);
		model.addAttribute("editEntryForm", editEntryForm);
		model.addAttribute("showComments", showComments);
		model.addAttribute("editMode", editMode);

		return new HtmxResponse().addTemplate("guestbook :: entries");
	}

	@HxRequest
	@PostMapping(path = "/guestbook/addComment{entry}")
	HtmxResponse addEntryComments(@PathVariable Optional<GuestbookEntry> entry, @Valid GuestbookForm form, Model model) {

		if(!entry.isPresent()) return new HtmxResponse().addTemplate("guestbook :: entries");


		GuestbookEntry newEntry = form.toNewEntry();
		newEntry.setParent(entry.get().getId());
		guestbook.save(newEntry);
		if(!showComments.containsKey(newEntry.getId())) showComments.put(newEntry.getId(), false);


		model.addAttribute("index", guestbook.count());
		model.addAttribute("entry", entry.get());
		model.addAttribute("entries", guestbook.findAll());
		model.addAttribute("gbEntryForm", form);
		model.addAttribute("editEntryForm", editEntryForm);
		model.addAttribute("showComments", showComments);
		model.addAttribute("editMode", editMode);

		return new HtmxResponse().addTemplate("guestbook :: entries");
	}


	/**
	 * Handles AJAX requests to delete {@link GuestbookEntry}s. Otherwise, this method is similar to
	 * {@link #removeEntry(Optional)}.
	 *
	 * @param entry an {@link Optional} with the {@link GuestbookEntry} to delete
	 * @return a response entity indicating success or failure of the removal
	 * @throws ResponseStatusException
	 */
	@HxRequest
	//@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(path = "/guestbook/remove{entry}")
	HtmxResponse removeEntryHtmx(@PathVariable Optional<GuestbookEntry> entry, Model model) {

		return entry.map(it -> {

			guestbook.delete(it);
			if(showComments.containsKey(it.getId())) showComments.remove(it.getId());

			model.addAttribute("index", guestbook.count());
			model.addAttribute("entry", entry);
			model.addAttribute("entries", guestbook.findAll());
			model.addAttribute("editEntryForm", editEntryForm);
			model.addAttribute("showComments", showComments);
			model.addAttribute("editMode", editMode);

			return new HtmxResponse()
					.addTemplate("guestbook :: entries");

		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
}
