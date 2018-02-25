package de.htw_berlin.tpro.plugin.bookstore.mb;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import de.htw_berlin.tpro.plugin.bookstore.model.Book;
import de.htw_berlin.tpro.plugin.bookstore.persistence.BookFacade;
import de.htw_berlin.tpro.plugin.bookstore.persistence.DefaultBookFacade;
import de.htw_berlin.tpro.user_management.model.User;
import de.htw_berlin.tpro.user_management.LoggedIn;
import lombok.Getter;
import lombok.Setter;

@Named
@SessionScoped
public class BookListMB implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject @DefaultBookFacade
	BookFacade bookFacade;

	@Inject @LoggedIn
    private User currentUser;
	
	private @Getter @Setter List<Book> books;
	
	@PostConstruct
    public void init() {
		books = bookFacade.getAllBooks();
    }
}
